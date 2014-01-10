package net.rugby.foundation.core.server.factory.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.PlayerRating;

public class TestPlayerRatingFactory extends BaseCachingFactory<IPlayerRating> implements IPlayerRatingFactory {
	
	private Map<Long,IPlayerRating> idMap = new HashMap<Long,IPlayerRating>();
	private Map<Long,List<IPlayerRating>> queryMap = new HashMap<Long,List<IPlayerRating>>();
	
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
			if (id != null) {
				return idMap.get(id);
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
	protected IPlayerRating putToPersistentDatastore(IPlayerRating pr) {
		try {
			if (pr != null) {
				idMap.put(pr.getId(),pr);
				if (pr.getQueryId() != null) {
					// create a new entry if this is the first we've heard of this query
					if (!queryMap.containsKey(pr.getQueryId())) {
						List<IPlayerRating> list = new ArrayList<IPlayerRating>();
						list.add(pr);
						queryMap.put(pr.getQueryId(),list);
					} else {
						// if we've heard of this query but haven't put this playerRating in the list yet...
						if (!queryMap.get(pr.getQueryId()).contains(pr)) {
							queryMap.get(pr.getQueryId()).add(pr);
						}
					}
				}
				return pr;
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
			idMap.remove(rq);
			if (rq.getQueryId() != null) {
				if (queryMap.containsKey(rq.getQueryId()) && queryMap.get(rq.getQueryId()).contains(rq)) {
					queryMap.get(rq.getQueryId()).remove(rq);
				} else {
					throw new RuntimeException("Trying to delete a PlayerRating that has a queryId but isn't in the test factory query hashmap.");
				}
			}
			return true;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return false;
		}
	}

	@Override
	public List<IPlayerRating> query(IRatingQuery query) {
		try {
			List<IPlayerRating> list = new ArrayList<IPlayerRating>();

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

				
			} else {
				return false; // null match
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem in delete: " + ex.getLocalizedMessage());
			return false;
		}
		return true;	
		
	}

	

}
