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

import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.RatingQuery;
import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.model.shared.ScrumPlayerMatchStats;

/**
 * 
 * @author DPT
 * @copyright RUGBY.NET LLC
 * 
 * Memcache strategy: We store PMS objects as themselves with their ids as keys. 
 * We also store a list of PMS ids (List<Long>) under the key "PMSIdsForMatch<matchid>".
 * This can then be used to pull the PMS objects from the memcache (or DB).
 * One implication is that when a PMS gets (re-)written, the match that it is culled from should
 * have its PMS id list dropped from the cache.
 *
 */
public class OfyPlayerMatchStatsFactory implements IPlayerMatchStatsFactory, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -175156134974074733L;
	private IRoundFactory rf;


	@Inject
	public OfyPlayerMatchStatsFactory(IRoundFactory rf) {
		this.rf = rf;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory#get()
	 */
	@Override
	public IPlayerMatchStats getById(Long id) {
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			IPlayerMatchStats p = null;

			if (id == null) {
				return (IPlayerMatchStats) put(null);
			}

			value = (byte[])syncCache.get(id);
			if (value == null) {
				p = getFromDB(id);

				if (p != null) {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutput out = new ObjectOutputStream(bos);   
					out.writeObject(p);
					byte[] yourBytes = bos.toByteArray(); 

					out.close();
					bos.close();

					syncCache.put(id, yourBytes);
				} else {
					return (IPlayerMatchStats) put(null);
				}
			} else {

				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				p = (IPlayerMatchStats)in.readObject();

				bis.close();
				in.close();

			}
			return p;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getById" + ex.getMessage(), ex);
			return null;
		}

	}

	private IPlayerMatchStats getFromDB(Long id) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			if (id != null)
				return ofy.get(new Key<ScrumPlayerMatchStats>(ScrumPlayerMatchStats.class,id));
			else
				return new ScrumPlayerMatchStats();
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getFromDB" + ex.getMessage(), ex);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory#put(net.rugby.foundation.model.shared.IPlayerMatchStats)
	 */
	@Override
	public IPlayerMatchStats put(IPlayerMatchStats pms) {
		try {
			if (pms == null) {
				return new ScrumPlayerMatchStats();
			}

			Objectify ofy = DataStoreFactory.getOfy();

			// only one per player per match
			ScrumPlayerMatchStats existing = ofy.query(ScrumPlayerMatchStats.class).filter("playerId", pms.getPlayerId()).filter("matchId", pms.getMatchId()).get();

			if (existing != null) {
				ofy.delete(existing);
			}

			ofy.put(pms);

			// now update the memcache version
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			syncCache.delete(pms.getId());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);   
			out.writeObject(pms);
			byte[] yourBytes = bos.toByteArray(); 

			out.close();
			bos.close();

			syncCache.put(pms.getId(), yourBytes);

			//also if there is a list of PMS ids for this PMS's match in memcache, it may now be no good so delete it
			String mKey = getMatchIdCacheKey(pms.getMatchId());
			if (syncCache.contains(mKey)) {
				syncCache.delete(mKey);
			}

			return pms;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "put" + ex.getMessage(), ex);
			return null;
		}
	}


	private String getMatchIdCacheKey(Long matchId) {
		if (matchId == null) {
			throw new RuntimeException("No matchId provided for getMatchIdCacheKey");
		}
		return "PMSIdsForMatch"+matchId.toString();
	}

	public Boolean delete(IPlayerMatchStats val) {
		try {
			if (val != null) {
				Objectify ofy = DataStoreFactory.getOfy();
				ofy.delete(val);
				// also from cache
				MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
				if (syncCache.contains(val.getId())) {
					syncCache.delete(val.getId());
				}
				return true;
			} else {
				return false;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "delete" + ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public List<IPlayerMatchStats> getByMatchId(Long matchId) {
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			List<IPlayerMatchStats> p = null;

			if (matchId == null) {
				return new ArrayList<IPlayerMatchStats>();
			}

			value = (byte[])syncCache.get(getMatchIdCacheKey(matchId));
			if (value == null) {
				p = getByMatchIdFromDB(matchId);

				if (p != null) {
					// put a List<Long> in the cache in case this gets called again
					List<Long> pMSIds = new ArrayList<Long>();
					for (IPlayerMatchStats pms : p) {
						pMSIds.add(pms.getId());
						//also make sure each PMS is in the cache
						if (!syncCache.contains(pms.getId())) {  //@REX should we replace existing entries?
							ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
							ObjectOutput out2 = new ObjectOutputStream(bos2);   
							out2.writeObject(pms);
							byte[] pmsBytes = bos2.toByteArray(); 

							out2.close();
							bos2.close();
							syncCache.put(pms.getId(), pmsBytes);
						}
					}

					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutput out = new ObjectOutputStream(bos);   
					out.writeObject(pMSIds);
					byte[] yourBytes = bos.toByteArray(); 

					out.close();
					bos.close();

					syncCache.put(getMatchIdCacheKey(matchId), yourBytes);
				} else {
					return new ArrayList<IPlayerMatchStats>();
				}
			} else {

				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				@SuppressWarnings("unchecked")
				List<Long>ids = (List<Long>)in.readObject();

				bis.close();
				in.close();

				p = new ArrayList<IPlayerMatchStats>();

				for (Long id : ids) {
					p.add(getById(id));
				}

			}
			return p;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getByMatchId" + ex.getMessage(), ex);
			return null;
		}


	}

	private List<IPlayerMatchStats> getByMatchIdFromDB(Long matchId) {
		Objectify ofy = DataStoreFactory.getOfy();

		Query<ScrumPlayerMatchStats> qpms = ofy.query(ScrumPlayerMatchStats.class).filter("matchId",matchId).order("teamId").order("slot");

		List<IPlayerMatchStats> list = new ArrayList<IPlayerMatchStats>();

		for (ScrumPlayerMatchStats spms : qpms) {
			list.add(spms);
		}

		return list;
	}
	//return qpms.list();  // wish we could do this	}

	@Override
	public List<IPlayerMatchStats> query(List<Long> matchIds,
			position posi, Long countryId, Long teamId) {
		Objectify ofy = DataStoreFactory.getOfy();


		Query<ScrumPlayerMatchStats> qpms = ofy.query(ScrumPlayerMatchStats.class).filter("matchId in",matchIds);
		if (posi != null && !posi.equals(position.NONE)) {
			qpms = qpms.filter("pos",posi);
		}

		//TODO should make country.NONE a constant
		if (countryId != null && countryId != 5000L) {
			qpms = qpms.filter("countryId", countryId);
		}

		if (teamId != null  && teamId != -1) {
			qpms = qpms.filter("teamId", teamId);
		}

		//		qpms = qpms.order("teamId").order("slot");

		List<IPlayerMatchStats> list = new ArrayList<IPlayerMatchStats>();

		for (ScrumPlayerMatchStats spms : qpms) {
			list.add(spms);
		}

		return list;
		//return (List<IPlayerMatchStats>)qpms.list();
	}

	@Override
	public boolean deleteForMatch(IMatchGroup m) {
		try {
			if (m != null) {
				Objectify ofy = DataStoreFactory.getOfy();

				List<IPlayerMatchStats> c = getByMatchId(m.getId());

				if (c != null) {
					ofy.delete(c);
				}

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
	public List<IPlayerMatchStats> query(IRatingQuery rq) {
		Objectify ofy = DataStoreFactory.getOfy();

		// @REX case of just specifying the comp and not the rounds (want all rounds)
		List<Long> matchIds = new ArrayList<Long>();
		for (Long rid : rq.getRoundIds()) {
			rf.setId(rid);
			IRound r = rf.getRound();
			for (Long mid : r.getMatchIDs()) {
				matchIds.add(mid);
			}
		}
		
		Query<ScrumPlayerMatchStats> qpms = ofy.query(ScrumPlayerMatchStats.class).filter("matchId in",matchIds);

		if (rq.getPositions() != null && !rq.getPositions().isEmpty()) {
			qpms = qpms.filter("pos in",rq.getPositions());
		}


		if (rq.getCountryIds() != null && !rq.getCountryIds().isEmpty() && !rq.getCountryIds().contains(5000L)) {
			qpms = qpms.filter("countryId in", rq.getCountryIds());
		}


		if (rq.getTeamIds() != null && !rq.getTeamIds().isEmpty() && !rq.getTeamIds().contains(-1L)) {
			qpms = qpms.filter("teamId in", rq.getTeamIds());
		}


		List<IPlayerMatchStats> list = new ArrayList<IPlayerMatchStats>();

		for (ScrumPlayerMatchStats spms : qpms) {
			list.add(spms);
		}

		return list;
	}
}
