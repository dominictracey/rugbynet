package net.rugby.foundation.core.server.factory.ofy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.admin.shared.IMatchRatingEngineSchema;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchRatingFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IGroup;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchRating;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.PlayerMatchRating;
import net.rugby.foundation.model.shared.PlayerRating;

public class OfyPlayerMatchRatingFactory implements IPlayerMatchRatingFactory, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private transient IPlayerFactory pf;
	private transient IMatchGroupFactory mf;
	private transient IPlayerMatchStatsFactory pmsf;

	private static String memCachePrefix="PMR001-";


	public OfyPlayerMatchRatingFactory() {

	}

	@Inject
	public void setFactories(IPlayerFactory pf, IMatchGroupFactory mf, IPlayerMatchStatsFactory pmsf) {
		this.pf = pf;
		this.mf = mf;
		this.pmsf = pmsf;
	}

	@Override
	public IPlayerMatchRating get(Long id) {
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			IPlayerMatchRating p = null;

			if (id == null) {
				return (IPlayerMatchRating) put(null);
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
					return (IPlayerMatchRating) put(null);
				}
			} else {

				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				p = (IPlayerMatchRating)in.readObject();

				bis.close();
				in.close();

			}
			return p;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "get" + ex.getMessage(), ex);
			return null;
		}

	}

	private IPlayerMatchRating getFromDB(Long id) {
		Objectify ofy = DataStoreFactory.getOfy();

		// hydrate the transient objects from their stored Ids
		PlayerMatchRating pmr = ofy.get(new Key<PlayerMatchRating>(PlayerMatchRating.class, id));
		if (pf != null) {
			pmr.setPlayer(pf.getById(pmr.getPlayerId()));
		}

		if (pmsf != null) {
			pmr.setPlayerMatchStats(pmsf.getById(pmr.getPlayerMatchStatsId()));
			//have to have the PMR to get the match
			if (mf != null) {
				mf.setId(pmr.getPlayerMatchStats().getMatchId());
				pmr.setGroup((IGroup)mf.getGame());
			}
		}
		return pmr;
	}

	@Override
	public List<? extends IPlayerMatchRating> getForMatch(Long matchId) {
		Objectify ofy = DataStoreFactory.getOfy();
		Query<PlayerMatchRating> qpmr = ofy.query(PlayerMatchRating.class).filter("matchId", matchId);
		return qpmr.list();
	}

	@Override
	public IPlayerMatchRating getNew(IPlayer player, IMatchGroup match,
			Integer rating, IMatchRatingEngineSchema schema,
			IPlayerMatchStats playerMatchStats) {
		Objectify ofy = DataStoreFactory.getOfy();
		PlayerMatchRating pmr = new PlayerMatchRating(rating, player, (IGroup)match, schema, playerMatchStats);
		ofy.put(pmr);
		return pmr;
	}

	@Override
	public IPlayerMatchRating put(IPlayerMatchRating pmr) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			// only one per match
			if (pmr != null && pmr.getPlayerMatchStats() != null) {
				IPlayerMatchRating existing = get(pmr.getPlayerMatchStats(), pmr.getSchema());
				if (existing != null) {
					ofy.delete(existing);
				}
			}

			if (pmr != null) {
				pmr.setGenerated(new Date());
				ofy.put(pmr);

				// put it in memcache - note we don't have to remove any existing because SetPolicy.SET_ALWAYS will replace them.
				MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos);   
				out.writeObject(pmr);
				byte[] yourBytes = bos.toByteArray(); 

				out.close();
				bos.close();

				syncCache.put(((IPlayerRating)pmr).getId(), yourBytes);

				//also put it in by PMS id+schema id
				syncCache.put(memCachePrefix + pmr.getPlayerMatchStats().getId().toString() + pmr.getSchemaId(), yourBytes);
			}

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "put" + ex.getMessage(), ex);
			return null;
		}
		return pmr;
	}

	@Override
	public IPlayerMatchRating get(IPlayerMatchStats pms, IMatchRatingEngineSchema schema) {
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			IPlayerMatchRating p = null;

			if (pms == null) {
				return (IPlayerMatchRating) put(null);
			}

			String key = memCachePrefix + pms.getId().toString() + schema.getId().toString();

			value = (byte[])syncCache.get(key);
			if (value == null) {
				p = getByPMSFromDB(pms, schema);

				if (p != null) {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutput out = new ObjectOutputStream(bos);   
					out.writeObject(p);
					byte[] yourBytes = bos.toByteArray(); 

					out.close();
					bos.close();

					syncCache.put(key, yourBytes);
					// also put in by OG id
					syncCache.put(p.getId(), yourBytes);
				} else {
					return (IPlayerMatchRating) put(null);
				}
			} else {

				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				p = (IPlayerMatchRating)in.readObject();

				bis.close();
				in.close();

			}
			return p;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "get by PMS + schema" + ex.getMessage(), ex);
			return null;
		}

	}

	private IPlayerMatchRating getByPMSFromDB(IPlayerMatchStats pms, IMatchRatingEngineSchema schema) {
		Objectify ofy = DataStoreFactory.getOfy();
		// I don't remember why we need both filters?
		Query<PlayerRating> qpmr = ofy.query(PlayerRating.class).filter("playerMatchStatsId", pms.getId()).filter("schemaId", schema.getId());
		IPlayerRating qr = qpmr.get();

		if (qr != null) {
			if (pf != null) {
				qr.setPlayer(pf.getById(qr.getPlayerId()));
			}

			if (pmsf != null) {
				//have to have the PMR to get the match
				if (mf != null && qr instanceof IPlayerMatchRating) {
					((IPlayerMatchRating)qr).setPlayerMatchStats(pmsf.getById(((IPlayerMatchRating)qr).getPlayerMatchStatsId()));
					mf.setId(((IPlayerMatchRating)qr).getPlayerMatchStats().getMatchId());
					qr.setGroup((IGroup)mf.getGame());
				}
			}

			if (qr instanceof IPlayerMatchRating) {
				return (IPlayerMatchRating)qr;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public Boolean deleteForSchema(IMatchRatingEngineSchema schema) {
		Objectify ofy = DataStoreFactory.getOfy();

		try {
			Query<PlayerRating> qpmr = ofy.query(PlayerRating.class).filter("schemaId", schema.getId());
			ofy.delete(qpmr);
			return true;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "delete for schema" + ex.getMessage(), ex);
			return false;
		}

	}
}
