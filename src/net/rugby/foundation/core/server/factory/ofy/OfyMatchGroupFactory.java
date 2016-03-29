package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.server.factory.BaseMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.Group;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.Status;
import net.rugby.foundation.model.shared.IMatchGroup.WorkflowStatus;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.ISimpleScoreMatchResult;
import net.rugby.foundation.model.shared.MatchGroup;
import net.rugby.foundation.model.shared.Round;

import org.joda.time.DateTime;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

public class OfyMatchGroupFactory extends BaseMatchGroupFactory implements Serializable,IMatchGroupFactory {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5536925770981961238L;


	@Override
	protected IMatchGroup getFromPersistentDatastore(Long id) {
		if (id == null) {
			throw new RuntimeException("Error in MatchGroupFactory.getFromPersistentDatastore - don't pass in null please.");
		}
		MatchGroup g = null;
		if (id != null) {
			Objectify ofy = DataStoreFactory.getOfy();
			g = ofy.get(new Key<MatchGroup>(MatchGroup.class,id));
			g.setHomeTeam(tf.get(g.getHomeTeamId()));
			g.setVisitingTeam(tf.get(g.getVisitingTeamId()));

			if (g.getSimpleScoreMatchResultId() != null) {
				IMatchResult mr = mrf.get(g.getSimpleScoreMatchResultId());
//@JsonIgnore				mr.setMatch(g);
				g.setSimpleScoreMatchResult((ISimpleScoreMatchResult)mr);  // @REX need to sort this out before other types of results are added

			}

			// self cleaning oven for workflowStatus			
			if (g.getWorkflowStatus() == null) {
				g.setWorkflowStatus(WorkflowStatus.PENDING);
				// if the match happened more than two weeks ago we either got stats or didn't
				DateTime mTime = new DateTime(g.getDate());
				if (mTime.isBefore(DateTime.now().minusWeeks(2))) {
					if (pmsf.getByMatchId(id).isEmpty()) {
						g.setWorkflowStatus(WorkflowStatus.NO_STATS);

					} else {
						g.setWorkflowStatus(WorkflowStatus.FETCHED);
					}
				}
				ofy.put(g); 
			}
		}
		return g;
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
			g.setHomeTeam(tf.get(g.getHomeTeamId()));
			g.setVisitingTeam(tf.get(g.getVisitingTeamId()));
			if (g.equals(match)) {
				return g; // will have an id since it came out of the db
			}
		}
		return null;
	}



	@Override
	public List<? extends IMatchGroup> getMatchesWithPipelines() {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			// @REX should we just check the match - don't we need to also cross-check against comp as well?
			Query<MatchGroup> qg = ofy.query(MatchGroup.class).filter("fetchMatchStatsPipelineId !=", null);
			List<IMatchGroup> list = new ArrayList<IMatchGroup>();
			Iterator<MatchGroup> it = qg.list().iterator();
			while (it.hasNext()) {
				IMatchGroup g = (IMatchGroup)it.next();
				g.setHomeTeam(tf.get(g.getHomeTeamId()));
				g.setVisitingTeam(tf.get(g.getVisitingTeamId()));
				list.add(g);
			}
			return list;
		} catch (Throwable ex) {

			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"getMatchesWithPipelines", ex);
			return null;
		}

	}

	@Override
	public boolean deleteFromPersistentDatastore(IMatchGroup m) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			ofy.delete(m);
			return true;

		} catch (Throwable ex) {
			String id = "null";
			if (m != null) {
				id = m.getId().toString();
			}
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Error deleting match " + id + ex.getLocalizedMessage());
			return false;
		}

	}



	@Override
	public IMatchGroup create() {
		try {
			IMatchGroup m =  new MatchGroup();
			m.setStatus(Status.SCHEDULED);
			m.setWorkflowStatus(WorkflowStatus.PENDING);
			//m.setCreatedDate(new Date());
			return m;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Error creating match " + ex.getLocalizedMessage(), ex);
			return null;
		}
	}



	@Override
	protected IMatchGroup putToPersistentDatastore(IMatchGroup m) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			ofy.put(m);
			return m;

		} catch (Throwable ex) {
			String id = "null";
			if (m != null) {
				id = m.getId().toString();
			}
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Error saving match " + id + ex.getLocalizedMessage(), ex);
			return null;
		}
	}



	@Override
	protected List<IMatchGroup> getMatchesForVirualCompFromPersistentDatastore(int ordinal, Long virtualCompId) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			List<IMatchGroup> retval = new ArrayList<IMatchGroup>();
			
			// look at the component competitions for their matches
			ICompetition hostComp = cf.get(virtualCompId);
			for (Long cid : hostComp.getComponentCompIds()) {
				//ICompetition compComp = cf.get(cid);
				Query<Round> roundQ = ofy.query(Round.class).filter("compId", cid).filter("urOrdinal", ordinal);
				for (Round r : roundQ.list()) {
					// should be 0 or 1
					for (Long mid : r.getMatchIDs()) {
						retval.add(get(mid));
					}
				}
						
			}
			
			return retval;
			
		} catch (Throwable ex) {

			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"getMatchesForVirualCompFromPersistentDatastore", ex);
			return null;
		}
	}



	@Override
	public List<IMatchGroup> getFutureMatchesForTeam(Long id) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			List<IMatchGroup> list = new ArrayList<IMatchGroup>();
			
			Query<MatchGroup> qg = ofy.query(MatchGroup.class).filter("homeTeamID", id).filter("status", "SCHEDULED");

			Iterator<MatchGroup> it = qg.list().iterator();
			while (it.hasNext()) {
				IMatchGroup g = (IMatchGroup)it.next();
				g.setHomeTeam(tf.get(g.getHomeTeamId()));
				g.setVisitingTeam(tf.get(g.getVisitingTeamId()));
				list.add(g);
			}
			
			qg = ofy.query(MatchGroup.class).filter("visitingTeamID", id).filter("status", "SCHEDULED");

			it = qg.list().iterator();
			while (it.hasNext()) {
				IMatchGroup g = (IMatchGroup)it.next();
				g.setHomeTeam(tf.get(g.getHomeTeamId()));
				g.setVisitingTeam(tf.get(g.getVisitingTeamId()));
				list.add(g);
			}
			
			return list;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"getFutureMatchesForTeam", ex);
			return null;
		}
	}



}
