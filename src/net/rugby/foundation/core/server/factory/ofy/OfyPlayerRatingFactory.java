package net.rugby.foundation.core.server.factory.ofy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.inject.Inject;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.PlayerRating;
import net.rugby.foundation.model.shared.RatingQuery;
import net.rugby.foundation.model.shared.PlayerRating.RatingComponent;

public class OfyPlayerRatingFactory extends BaseCachingFactory<IPlayerRating> implements IPlayerRatingFactory {

	private IPlayerFactory pf;
	private IPlayerMatchStatsFactory pmsf;

	@Inject
	public OfyPlayerRatingFactory(IPlayerFactory pf, IPlayerMatchStatsFactory pmsf) {
		this.pf = pf;
		this.pmsf = pmsf;
	}

	@Override
	public IPlayerRating create() {
		try {
			IPlayerRating pr = new PlayerRating();
			return pr;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected IPlayerRating getFromPersistentDatastore(Long id) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			if (id != null) {
				IPlayerRating rq = ofy.get(new Key<PlayerRating>(PlayerRating.class,id));
				if (rq != null && rq.getPlayerId() != null) {
					rq.setPlayer(pf.get(rq.getPlayerId()));
				}
				for (Long pmsid : rq.getMatchStatIds()) {
					rq.addMatchStats(pmsf.get(pmsid));
				}
				return rq;
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Don't try to get with null. Call create() instead!");
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected IPlayerRating putToPersistentDatastore(IPlayerRating rq) {
		try {
			if (rq != null) {
				Objectify ofy = DataStoreFactory.getOfy();
				
				// don't let them double up
				Query<PlayerRating> prq = ofy.query(PlayerRating.class).filter("playerId", rq.getPlayerId()).filter("queryId", rq.getQueryId());
				if (prq.count() > 0) {
					// log and delete the existing one before saving
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Duplicate player rating found for " + rq.getPlayer().getDisplayName() + ". Probably a engine instance shift. Deleting existing one...");
					ofy.delete(prq.list());
				}
				
				ofy.put(rq);

				return rq;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected boolean deleteFromPersistentDatastore(IPlayerRating rq) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			ofy.delete(rq);

			return true;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return false;
		}
	}

	@Override
	public List<IPlayerRating> query(IRatingQuery query) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			// confirm list is in ascending order
			Query<PlayerRating> prq = ofy.query(PlayerRating.class).filter("queryId", query.getId()).order("-rating").limit(30);
			List<IPlayerRating> list = new ArrayList<IPlayerRating>();
			list.addAll(prq.list());
			for (IPlayerRating r : list) {
				if (r.getPlayerId() != null) {
					r.setPlayer(pf.get(r.getPlayerId()));
				}
				for (Long pmsid : r.getMatchStatIds()) {
					r.addMatchStats(pmsf.get(pmsid));
				}
			}
			return list;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public boolean deleteForQuery(IRatingQuery rq) {
		try {
			if (rq != null) {
				Objectify ofy = DataStoreFactory.getOfy();

				Query<PlayerRating> qpmr = ofy.query(PlayerRating.class).filter("queryId", rq.getId());
				ofy.delete(qpmr);


			} else {
				return false; // null query
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem in deleteForQuery: " + ex.getLocalizedMessage());
			return false;
		}
		return true;	

	}

	@Override
	public boolean deleteForMatch(IMatchGroup m) {
		try {
			if (m != null) {
				Objectify ofy = DataStoreFactory.getOfy();

				Query<PlayerRating> qpmr = ofy.query(PlayerRating.class).filter("groupId", m.getId());
				ofy.delete(qpmr);


			} else {
				return false; // null match
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem in deleteForMatch: " + ex.getLocalizedMessage());
			return false;
		}
		return true;
	}

	@Override
	public List<IPlayerRating> getForMatch(Long matchId, IRatingEngineSchema schema) {
		// get the PMSs from their factory
		List<IPlayerMatchStats> pmsl = pmsf.getByMatchId(matchId);
		// create wrapper PlayerRating objects for them
		List<IPlayerRating> list = new ArrayList<IPlayerRating>();
		for (IPlayerMatchStats pms : pmsl) {
			IPlayerRating pr = create();
			pr.addMatchStats(pms);
			pr.setPlayerId(pms.getPlayerId());
			list.add(pr);
		}
		return list;
	}

	@Override
	public Boolean deleteForSchema(IRatingEngineSchema schema) {
		try {
			if (schema != null) {
				Objectify ofy = DataStoreFactory.getOfy();

				Query<PlayerRating> qpmr = ofy.query(PlayerRating.class).filter("schemaId", schema.getId());
				ofy.delete(qpmr);

			} else {
				return false; // null query
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem in deleteForSchema: " + ex.getLocalizedMessage());
			return false;
		}
		return true;	}


	@Override
	public void deleteAll() {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			QueryResultIterable<Key<PlayerRating>> keys = ofy.query(PlayerRating.class).fetchKeys();
			ofy.delete(keys);

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
		}	

	}

	@Override
	public List<IPlayerRating> getFromBefore(Date datetime) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			// confirm list is in ascending order
			Query<PlayerRating> prq = ofy.query(PlayerRating.class).filter("generated <", datetime);
			List<IPlayerRating> list = new ArrayList<IPlayerRating>();
			list.addAll(prq.list());
//			for (IPlayerRating r : list) {
//				if (r.getPlayerId() != null) {
//					r.setPlayer(pf.get(r.getPlayerId()));
//				}
//				for (Long pmsid : r.getMatchStatIds()) {
//					r.addMatchStats(pmsf.get(pmsid));
//				}
//			}
			return list;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public List<IPlayerRating> putBatch(List<IPlayerRating> prl) {
		try {
			if (prl != null) {
				Objectify ofy = DataStoreFactory.getOfy();
				
//				List<PlayerRating> cleansed = new ArrayList<PlayerRating>();
//				
//				// they have to already be in the database (i.e. they have to have an id);
//				for (IPlayerRating pr : prl) {
//					if (pr.getId() != null && pr instanceof PlayerRating) {
//						cleansed.add((PlayerRating) pr);
//					} else {
//						// log and delete the existing one before saving
//						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Trying to batch save a new PlayerRating, please save it individually first.");
//					}
//				}
				ofy.put(prl);

				// TODO  should return the cleansed one, but it's type is wrong
				return prl;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * Just gets and puts everything in the database to index and de-index various fields.
	 */
	@Override
	public void touchAll() {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
 
			Query<PlayerRating> prq = ofy.query(PlayerRating.class);
			
			for (PlayerRating r : prq.list()) {
				ofy.put(r);
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	@Override
	public void cleanUp() {
//		touchAll(); // we can take this line out once it's run once.
//		
//		Objectify ofy = DataStoreFactory.getOfy();
//		DateTime twoWeeksAgo = new DateTime();
//		twoWeeksAgo = twoWeeksAgo.minusWeeks(2);
//		Query<PlayerRating> prq = ofy.query(PlayerRating.class).filter("generated <", twoWeeksAgo.toDate());
//		
//		for (IPlayerRating pr : prq) {
//			pr.setDetails(null);
//			for (RatingComponent rc : pr.getRatingComponents()) {
//				rc.setRatingDetails(null);
//				rc.setStatsDetails(null);
//			}
//		}
//		
//		ofy.put(prq);
		
	}




}
