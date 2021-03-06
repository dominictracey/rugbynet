package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IRound.WorkflowStatus;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.Round;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

public class OfyRoundFactory extends BaseCachingFactory<IRound> implements IRoundFactory, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2311118171036828094L;
	private IMatchGroupFactory gf;
	private ICompetitionFactory cf;
	private IStandingFactory sf;
	private IUniversalRoundFactory urf;

	@Inject
	OfyRoundFactory(ICompetitionFactory cf, IMatchGroupFactory gf, IStandingFactory sf, IUniversalRoundFactory urf) {
		this.gf = gf;
		this.cf = cf;
		this.sf = sf;
		this.urf = urf;
		
		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.INFO);
	}

	@Override
	protected IRound getFromPersistentDatastore(Long id) {
		try {
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
//			if (r.getWorkflowStatus() == null) {
//				r.setWorkflowStatus(WorkflowStatus.FETCHED);
//				ofy.put(r); 	
//			}

			// self cleaning oven for urOrdinal
			if (r.getUrOrdinal() < 1) {
				r.setUrOrdinal(urf.get(r).ordinal);
				ofy.put(r);
			}

			return r;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}

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
						if (g != null) {
							((Round)r).getMatchIDs().add(g.getId());
						} else {
							Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "There was a problem saving a match in Round " + r.getAbbr() + ", it was dropped from the round.");
						}
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
					if (s.getId() != null) {
						sf.delete(s);
					}
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
			IRound r = new Round();
			r.setWorkflowStatus(WorkflowStatus.PENDING);
			return r;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public IRound getForUR(Long compId, int uROrdinal) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();

			// Find the round
			Query<Round> qr = ofy.query(Round.class).filter("compId", compId).filter("urOrdinal", uROrdinal);

			if (qr.count() == 0) {
				return null;
			} else if (qr.count() > 1) {
				throw new Exception("More than one round matches the parameters compId=" + compId + " and urOrdinal " + uROrdinal + ". This is a Bad Thing.");
			} else {
				return get(qr.get().getId());  // build the round
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}

	}

}
