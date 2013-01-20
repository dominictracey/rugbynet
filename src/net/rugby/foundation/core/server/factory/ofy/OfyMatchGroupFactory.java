package net.rugby.foundation.core.server.factory.ofy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IMatchResultFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.Group;
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
	private final Objectify ofy;
	private ITeamGroupFactory tf;
	private IMatchResultFactory mrf;
	private IRoundFactory rf;
	
	@Inject
	OfyMatchGroupFactory(IMatchResultFactory mrf, ITeamGroupFactory tf, IRoundFactory rf) {
		this.ofy = DataStoreFactory.getOfy();
		this.tf = tf;
		this.mrf = mrf;
		this.rf = rf;
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
			Logger.getLogger("Core Service: OfyMatchGroupFactory.Get").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
		
	}


	protected IMatchGroup getFromDB() {
		MatchGroup g = null;
		if (id != null) {
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
			Logger.getLogger("Core Service: OfyMatchGroupFactory.Put").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IMatchGroupFactory#find(net.rugby.foundation.model.shared.IMatchGroup)
	 */
	@Override
	public IMatchGroup find(IMatchGroup match) {
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
		Round r = ofy.get(new Key<Round>(Round.class,roundId));
		if (r != null) {
			return r.getMatches();
		} else {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Could not find requested Round " + roundId);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IMatchGroupFactory#setFactories(net.rugby.foundation.core.server.factory.IRoundFactory, net.rugby.foundation.core.server.factory.ITeamGroupFactory)
	 */
//	@Override
//	public void setFactories(IRoundFactory rf, ITeamGroupFactory tf) {
//		this.tf = tf;
//		this.rf = rf;		
//	}

}
