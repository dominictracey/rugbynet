package net.rugby.foundation.core.server.factory.ofy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.pipeline.NoSuchObjectException;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IMatchResultFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchRatingFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.Group;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ISimpleScoreMatchResult;
import net.rugby.foundation.model.shared.MatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.Round;

public class OfyMatchGroupFactory implements IMatchGroupFactory, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5536925770981961238L;
	private Long id;

	private ITeamGroupFactory tf;
	private IMatchResultFactory mrf;
	private IRoundFactory rf;
	protected ITeamMatchStatsFactory tmsf;
	protected IPlayerMatchStatsFactory pmsf;
	protected IPlayerMatchRatingFactory pmrf;
	@Inject
	OfyMatchGroupFactory(IMatchResultFactory mrf, ITeamGroupFactory tf, IRoundFactory rf, ITeamMatchStatsFactory tmsf, IPlayerMatchStatsFactory pmsf,
			IPlayerMatchRatingFactory pmrf) {

		this.tf = tf;
		this.mrf = mrf;
		this.rf = rf;
		this.tmsf = tmsf;
		this.pmsf = pmsf;
		this.pmrf = pmrf;
	}

	@Override
	public IMatchGroup getGame() {
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			IMatchGroup m = null;

			if (id == null) {
				return (MatchGroup) put(null);
			}

			value = (byte[])syncCache.get(id);
			if (value == null) {
				setId(id);
				m = getFromDB();

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos);   
				out.writeObject(m);
				byte[] yourBytes = bos.toByteArray(); 

				out.close();
				bos.close();

				syncCache.put(id, yourBytes);
			} else {

				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				m = (IMatchGroup)in.readObject();

				bis.close();
				in.close();

			}
			return m;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}

	}


	protected IMatchGroup getFromDB() {
		MatchGroup g = null;
		if (id != null) {
			Objectify ofy = DataStoreFactory.getOfy();
			g = ofy.get(new Key<MatchGroup>(MatchGroup.class,id));
			tf.setId(g.getHomeTeamId());
			g.setHomeTeam(tf.getTeam());
			tf.setId(g.getVisitingTeamId());
			g.setVisitingTeam(tf.getTeam());

			if (g.getSimpleScoreMatchResultId() != null) {
				mrf.setId(g.getSimpleScoreMatchResultId());
				g.setSimpleScoreMatchResult((ISimpleScoreMatchResult) mrf.get());  // @REX need to sort this out before other types of results are added
			}
		} else { // create new
			// @REX cast
			g = (MatchGroup) put(null);
		}
		return g;
	}

	@Override
	public void setId(Long id) {
		this.id = id;

	}

	@Override
	public IMatchGroup put(IMatchGroup g) {
		try {
			if (g == null) {
				return new MatchGroup();
			}

			if (g.getHomeTeam() == null) {
				tf.setId(null);
				g.setHomeTeam(tf.getTeam());
			} else {
				tf.put(g.getHomeTeam());
			}

			if (g.getVisitingTeam() == null) {
				tf.setId(null);
				g.setVisitingTeam(tf.getTeam());
			} else {
				tf.put(g.getVisitingTeam());
			}

			((MatchGroup)g).setHomeTeamId(g.getHomeTeam().getId());
			((MatchGroup)g).setVisitingTeamId(g.getVisitingTeam().getId());
			Objectify ofy = DataStoreFactory.getOfy();
			ofy.put(g);

			// now update the memcache version
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			syncCache.delete(g.getId());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);   
			out.writeObject(g);
			byte[] yourBytes = bos.toByteArray(); 

			out.close();
			bos.close();

			syncCache.put(id, yourBytes);
			// force top-level reload
			if (g.getRoundId() != null) {
				rf.build(g.getRoundId());
			}

			return g;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IMatchGroupFactory#find(net.rugby.foundation.model.shared.IMatchGroup)
	 * 
	 * It's ok not to cache this as it is just used (as of 7/13/13) in the competition import/set-up
	 */
	@Override
	public IMatchGroup find(IMatchGroup match) {
		Objectify ofy = DataStoreFactory.getOfy();
		// @REX should we just check the match - don't we need to also cross-check against comp as well?
		Query<Group> qg = ofy.query(Group.class).filter("displayName",match.getDisplayName()).filter("date", match.getDate());

		if (qg.count() != 1) {
			return null;
		}

		if (qg.get() instanceof IMatchGroup) {
			IMatchGroup g = (IMatchGroup)qg.get();
			tf.setId(g.getHomeTeamId());
			g.setHomeTeam(tf.getTeam());
			tf.setId(g.getVisitingTeamId());
			g.setVisitingTeam(tf.getTeam());
			if (g.equals(match)) {
				return g; // will have an id since it came out of the db
			}
		}
		return null;
	}

	@Override
	public List<IMatchGroup> getMatchesForRound(Long roundId) {
		//		Objectify ofy = DataStoreFactory.getOfy();
		//		Round r = ofy.get(new Key<Round>(Round.class,roundId));
		rf.setId(roundId);
		IRound r = rf.getRound();  //roundFactory handles memcaching
		if (r != null) {
			return r.getMatches();
		} else {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Could not find requested Round " + roundId);
			return null;
		}
	}

	@Override
	public List<? extends IMatchGroup> getMatchesWithPipelines() {
		Objectify ofy = DataStoreFactory.getOfy();
		// @REX should we just check the match - don't we need to also cross-check against comp as well?
		Query<MatchGroup> qg = ofy.query(MatchGroup.class).filter("fetchMatchStatsPipelineId !=", null);
		return qg.list();
	}

	@Override
	public boolean delete(Long matchId) {
		try {
			setId(matchId);
			IMatchGroup m = getGame();


			if (m != null) {
				// delete any results

				if (m.getSimpleScoreMatchResultId() != null) {

					if (!mrf.delete(m.getSimpleScoreMatchResultId())) {
						return false;
					}
				}

				// delete any teamMatchStats
				if (!tmsf.deleteForMatch(m)) {
					return false;
				}

				// delete any playerMatchStats
				if (!pmsf.deleteForMatch(m)) {
					return false;
				}

				// delete any playerMatchRatings
				if (!pmrf.deleteForMatch(m)) {
					return false;
				}

				// purge any pipeline jobs
				if (m.getFetchMatchStatsPipelineId() != null) {
					PipelineService service = PipelineServiceFactory.newPipelineService();

					try {
						service.deletePipelineRecords(m.getFetchMatchStatsPipelineId(),true,false);
					} catch (NoSuchObjectException nsox) {
						// it's ok, just was a dangling reference in the match record
					}
				}

				Objectify ofy = DataStoreFactory.getOfy();
				ofy.delete(m);
				return true;
			} else {
				return true; // ?
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Error deleting match " + matchId + ex.getLocalizedMessage());
			return false;
		}
		
	}



}
