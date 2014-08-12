package net.rugby.foundation.core.server.factory.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Inject;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.PlayerRating;

public class TestPlayerRatingFactory extends BaseCachingFactory<IPlayerRating> implements IPlayerRatingFactory {

	private Map<Long,IPlayerRating> idMap = new HashMap<Long,IPlayerRating>();
	//private Map<Long,List<IPlayerRating>> queryMap = new HashMap<Long,List<IPlayerRating>>();

	private Random random = new Random();
	private IPlayerMatchStatsFactory pmsf;
	private IPlayerFactory pf;

	@Inject
	public TestPlayerRatingFactory(IPlayerMatchStatsFactory pmsf, IPlayerFactory pf) {
		this.pmsf = pmsf;
		this.pf = pf;
	}

	@Override
	public IPlayerRating create() {
		try {
			IPlayerRating pr = new PlayerRating();
			pr.setRating(getRandomRating());
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
				if (pr.getId() == null) {
					pr.setId(random.nextLong());
				}
				idMap.put(pr.getId(),pr);
				if (pr.getQueryId() != null) {
					// get queryMap out of MemCache
					Map<Long,List<Long>> queryMap = getQueryMapFromMemCache();
					// create a new entry if this is the first we've heard of this query
					if (!queryMap.containsKey(pr.getQueryId())) {
						List<Long> list = new ArrayList<Long>();
						list.add(pr.getId());
						queryMap.put(pr.getQueryId(),list);
					} else {
						// if we've heard of this query but haven't put this playerRating in the list yet...
						if (!queryMap.get(pr.getQueryId()).contains(pr.getId())) {
							queryMap.get(pr.getQueryId()).add(pr.getId());
						}
					}
					putQueryMapToMemCache(queryMap);
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

	// key queryId, value List<ratingIds>
	private void putQueryMapToMemCache(Map<Long, List<Long>> queryMap) {
		try {
			Serializable sMap = (Serializable) queryMap;
			
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			syncCache.delete(queryMapKey);
			if (sMap != null) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos);   
				out.writeObject(sMap);
				byte[] yourBytes = bos.toByteArray(); 

				out.close();
				bos.close();

				syncCache.put(queryMapKey, yourBytes);
				//Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,"** putting list at " + key + " *** \n" + syncCache.getStatistics());
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
		}		
	}

	private final String queryMapKey = "QUERYMAP";
	
	// key queryId, value List<ratingIds>
	@SuppressWarnings("unchecked")
	private Map<Long, List<Long>> getQueryMapFromMemCache() {
		byte[] value = null;
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		Map<Long, List<Long>> map = null;

		try {
			value = (byte[])syncCache.get(queryMapKey);
			if (value == null) {
				return new HashMap<Long,List<Long>>();
			} else {

				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				Object obj = in.readObject();

				//				if (typeLiteral.equals(obj.getClass())) {  // can't do 'obj instanceof List<T>' *sadfase*
				map = (Map<Long, List<Long>>)obj;


				bis.close();
				in.close();

			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
		return map;
	}

	@Override
	protected boolean deleteFromPersistentDatastore(IPlayerRating rq) {
		try {
			idMap.remove(rq);
			if (rq.getQueryId() != null) {
				Map<Long,List<Long>> queryMap = getQueryMapFromMemCache();
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
			Map<Long,List<Long>> queryMap = getQueryMapFromMemCache();
			List<IPlayerRating> retval = new ArrayList<IPlayerRating>();
			
			for (Long rid : queryMap.get(query.getId())) {
				IPlayerRating pr = get(rid);
				pr.setPlayer(pf.get(pr.getPlayerId()));
				pr.getPlayer().setPosition(pr.getMatchStats().get(0).getPosition());
				retval.add(pr);
			}	

			return retval;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

//	private IPlayerRating createForPMS(List<IPlayerMatchStats> pmsList) {
//		IPlayerRating r = create();
//		r.setGenerated(DateTime.now().toDate());
//		r.setMatchStats(pmsList);
//		List<Long> pmss = new ArrayList<Long>();
//		for (IPlayerMatchStats pms : pmsList) {
//			pmss.add(pms.getId());
//		}
//		r.setMatchStatIds(pmss);
//		r.setPlayerId(pmsList.get(0).getPlayerId());
//		r.setPlayer(pf.get(pmsList.get(0).getPlayerId()));
//		r.setRawScore(random.nextFloat() * 40);
//		r.setSchemaId(sf.getDefault().getId());
//		r.setSchema(sf.getDefault());
//
//		return r;
//	}

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

	private int getRandomRating() {
		return random.nextInt(1000);
	}

	@Override
	public boolean deleteForMatch(IMatchGroup m) {
		try {
			if (m != null) {


			} else {
				return false; // null match
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem in delete: " + ex.getLocalizedMessage());
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
			pr.setPlayer(pf.get(pms.getPlayerId()));
			pr.setRating(getRandomRating());
			list.add(pr);
		}
		return list;
	}

	@Override
	public Boolean deleteForSchema(IRatingEngineSchema rq) {
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

	@Override
	public void deleteAll() {
		return;

	}

}
