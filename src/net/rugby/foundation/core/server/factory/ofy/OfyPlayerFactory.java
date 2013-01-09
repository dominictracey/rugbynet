/**
 * 
 */
package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;

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

	@Override
	public IPlayer getByScrumId(Long id) {
		Objectify ofy = DataStoreFactory.getOfy();
		if (id != null) {
			Query<ScrumPlayer> qsp = ofy.query(ScrumPlayer.class).filter("scrumId", id);
			return qsp.get();
		} else {
			return new ScrumPlayer();
		}
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IPlayerFactory#put(net.rugby.foundation.model.shared.IPlayer)
	 */
	@Override
	public IPlayer put(IPlayer player) {
		Objectify ofy = DataStoreFactory.getOfy();
		ofy.put(player);
		return player;
	}



}
