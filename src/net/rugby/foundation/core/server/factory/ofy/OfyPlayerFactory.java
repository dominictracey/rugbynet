/**
 * 
 */
package net.rugby.foundation.core.server.factory.ofy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.ICountryFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.ScrumPlayer;

/**
 * @author home
 *
 */
public class OfyPlayerFactory implements IPlayerFactory, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5267158546061782777L;
	//private Objectify ofy;
	private ICountryFactory cf;

	@Inject
	public OfyPlayerFactory(ICountryFactory cf) {
		this.cf = cf;
		//this.ofy = DataStoreFactory.getOfy();
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IPlayerFactory#get()
	 */
	@Override
	public IPlayer getById(Long id) {
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			IPlayer p = null;
			
			if (id == null) {
				return (IPlayer) put(null);
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
					if (p.getScrumId() != null) {
						syncCache.put(getScrumCacheId(p.getScrumId()), yourBytes);
					}
				} else {
					return (IPlayer) put(null);
				}
			} else {
	
				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				p = (IPlayer)in.readObject();
	
				bis.close();
				in.close();

			}
			return p;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "GetById" + ex.getMessage(), ex);
			return null;
		}
	}



	@Override
	public IPlayer getByScrumId(Long id) {
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			IPlayer p = null;
			
			if (id == null) {
				return (IPlayer) put(null);
			}
	
			
			value = (byte[])syncCache.get(getScrumCacheId(id));
			if (value == null) {
				p = getFromDBByScrumId(id);

				if (p != null) {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutput out = new ObjectOutputStream(bos);   
					out.writeObject(p);
					byte[] yourBytes = bos.toByteArray(); 
		
					out.close();
					bos.close();
		
					syncCache.put(getScrumCacheId(id), yourBytes);
					syncCache.put(p.getId(), yourBytes);
				}	else {
					return (IPlayer) put(null);
				}
			} else {
	
				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				p = (IPlayer)in.readObject();
	
				bis.close();
				in.close();

			}
			return p;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "GetByScrumId" + ex.getMessage(), ex);
			return null;
		}
	}
	
	private Object getScrumCacheId(Long id) {
		return "SP" + id.toString();
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IPlayerFactory#put(net.rugby.foundation.model.shared.IPlayer)
	 */
	@Override
	public IPlayer put(IPlayer player) {
		if (player == null) {
			return new ScrumPlayer();
		}
		Objectify ofy = DataStoreFactory.getOfy();
		ofy.put(player);
		return player;
	}

	protected IPlayer getFromDB(Long id) {
		Objectify ofy = DataStoreFactory.getOfy();
		if (id != null) {
			IPlayer p = ofy.get(new Key<ScrumPlayer>(ScrumPlayer.class,id));
			if (p.getCountryId() != null) {
				p.setCountry(cf.getById(p.getCountryId()));
			}
			return p;
		} else
			return new ScrumPlayer();
	}
	
	protected IPlayer getFromDBByScrumId(Long id) {
		Objectify ofy = DataStoreFactory.getOfy();
		if (id != null) {
			Query<ScrumPlayer> qsp = ofy.query(ScrumPlayer.class).filter("scrumId", id);
			return qsp.get();
		} else {
			return new ScrumPlayer();
		}
	}




}
