package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.Round;
import net.rugby.foundation.model.shared.IRound.WorkflowStatus;

public class OfyRoundFactory extends BaseCachingFactory<IRound> implements IRoundFactory, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2311118171036828094L;
	private IMatchGroupFactory gf;
	private ICompetitionFactory cf;
	private IStandingFactory sf;

	@Inject
	OfyRoundFactory(ICompetitionFactory cf, IMatchGroupFactory gf, IStandingFactory sf) {
		this.gf = gf;
		this.cf = cf;
		this.sf = sf;
	}

	@Override
	protected IRound getFromPersistentDatastore(Long id) {
		if (id == null) {
			return new Round();
		}
		Objectify ofy = DataStoreFactory.getOfy();

		Round r = ofy.get(new Key<Round>(Round.class,id));

		if (r != null) {
			r.setMatches(new ArrayList<IMatchGroup>());
			for (Long gid : r.getMatchIDs()) {
				IMatchGroup g = gf.get(gid);
				r.getMatches().add(g);
			}
		} else {
			r = new Round();
		}

		// self cleaning oven for workflowStatus
		if (r.getWorkflowStatus() == null) {
			// check it's matches to see if they are all fetched
			r.setWorkflowStatus(WorkflowStatus.FETCHED);
			for (IMatchGroup m : r.getMatches()) {
				if (m.getWorkflowStatus() == IMatchGroup.WorkflowStatus.TASKS_PENDING) {
					r.setWorkflowStatus(WorkflowStatus.TASKS_PENDING);
					break;
				} else if (m.getWorkflowStatus() == IMatchGroup.WorkflowStatus.PENDING) {
					r.setWorkflowStatus(WorkflowStatus.PENDING);
					// don't break in case there are tasks pending
				}
				// ignore if match is NO_STATS - the round can still be in FETCHED state
			}
			ofy.put(r); 	
		}
		
		return r;
	}

	@Override
	public IRound putToPersistentDatastore(IRound r) {

		try {
			Objectify ofy = DataStoreFactory.getOfy();

			if (r != null) {			
				((Round)r).setMatchIDs(new ArrayList<Long>());
				if (r.getId() == null)
					ofy.put(r); // get an id to pass down to the matches

				if (r.getMatches() != null) {
					for (IMatchGroup g : r.getMatches()) {
						g.setRoundId(r.getId());
						g = gf.put(g);
						((Round)r).getMatchIDs().add(g.getId());
					}
				} else {
					r.setMatches(new ArrayList<IMatchGroup>());
					((Round)r).setMatchIDs(new ArrayList<Long>());
				}
			} else {
				r = new Round();
				r.setName("--");
			}		

			ofy.put(r);

			// force top-level reload
			cf.invalidate(r.getCompId());

			return r;
		} catch (Throwable ex) {
			Logger.getLogger("Core Service OfyRoundFactory.put").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IRoundFactory#find(net.rugby.foundation.model.shared.IRound)
	 */
	@Override
	public IRound find(IRound round) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();

			// Find the rounds with the same name
			Query<Round> qr = ofy.query(Round.class).filter("name", round.getName());

			// now see if any of these have the same matches
			for (Round r : qr) {			
				if (r.equals(round)) {
					return get(r.getId());  // r has id set, parameter round may not
				}
			}

			return null;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IRoundFactory#build(java.lang.Long)
	 */
	@Override
	public void invalidate(Long roundId) {
		try {
			IRound r = get(roundId);
			if (r != null) {
				deleteFromMemcache(r);
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
		}	
	}

	@Override
	public boolean deleteFromPersistentDatastore(IRound r) {
		try {
			boolean ok = true;
			if (r != null) {
				for (Long mid : r.getMatchIDs()) {
					if (ok) {						
						IMatchGroup m = gf.get(mid);
						ok = gf.delete(m);
					}
				}

				// also delete standings
				List<IStanding> standings = sf.getForRound(r);
				for (IStanding s : standings) {
					sf.delete(s);
				}

				if (ok) {
					Objectify ofy = DataStoreFactory.getOfy();
					ofy.delete(r);
					return true;
				}
			}


		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return false;
		}

		return false;
	}

	@Override
	public IRound create() {
		try {
			return new Round();
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

}
