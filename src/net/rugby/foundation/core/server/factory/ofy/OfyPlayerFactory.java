/**
 * 
 */
package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.server.factory.BasePlayerFactory;
import net.rugby.foundation.core.server.factory.ICountryFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.ScrumPlayer;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

/**
 * @author home
 *
 */
public class OfyPlayerFactory extends BasePlayerFactory implements IPlayerFactory, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5267158546061782777L;
	private ICountryFactory cf;

	@Inject
	public OfyPlayerFactory(ICountryFactory cf) {
		this.cf = cf;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IPlayerFactory#put(net.rugby.foundation.model.shared.IPlayer)
	 */
	@Override
	public IPlayer putToPersistentDatastore(IPlayer player) {
		try {
			if (player == null) {
				return new ScrumPlayer();
			}
			Objectify ofy = DataStoreFactory.getOfy();
			ofy.put(player);
			
			// drop any cached scrumId version of the player
			dropFromCache(getScrumCacheId(player.getScrumId()).toString());
			
			return player;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	protected IPlayer getFromPersistentDatastore(Long id) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			if (id != null) {
				IPlayer p = ofy.get(new Key<ScrumPlayer>(ScrumPlayer.class,id));
				if (p.getCountryId() != null) {
					p.setCountry(cf.getById(p.getCountryId()));
				}
				return p;
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Don't try to get with null. Call create() instead!");
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	protected IPlayer getFromPersistentDatastoreByScrumId(Long id) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			if (id != null) {
				Query<ScrumPlayer> qsp = ofy.query(ScrumPlayer.class).filter("scrumId", id);
				return qsp.get();
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Don't try to getByScrumId with null. Call create() instead!");
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public IPlayer create() {
		try {
			IPlayer p = new ScrumPlayer();
			return p;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}


	@Override
	protected boolean deleteFromPersistentDatastore(IPlayer t) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			ofy.delete(t);
			return true;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return false;
		}
	}

}
