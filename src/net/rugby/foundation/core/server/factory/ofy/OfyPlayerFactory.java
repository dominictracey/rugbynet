/**
 * 
 */
package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
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
	private Objectify ofy;

	public OfyPlayerFactory() {
		this.ofy = DataStoreFactory.getOfy();
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IPlayerFactory#get()
	 */
	@Override
	public IPlayer getById(Long id) {
		if (id != null)
			return ofy.get(new Key<ScrumPlayer>(ScrumPlayer.class,id));
		else
			return null;
	}

	@Override
	public IPlayer getByScrumId(Long id) {
		
		if (id != null) {
			Query<ScrumPlayer> qsp = ofy.query(ScrumPlayer.class).filter("scrumId", id);
			return qsp.get();
		} else {
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IPlayerFactory#put(net.rugby.foundation.model.shared.IPlayer)
	 */
	@Override
	public IPlayer put(IPlayer player) {
		ofy.put(player);
		return player;
	}



}
