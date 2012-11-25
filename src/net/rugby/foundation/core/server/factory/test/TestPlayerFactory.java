/**
 * 
 */
package net.rugby.foundation.core.server.factory.test;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.ScrumPlayer;
import net.rugby.foundation.model.shared.Position.position;

/**
 * @author home
 *
 */
public class TestPlayerFactory implements IPlayerFactory, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5267158546061782777L;



	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IPlayerFactory#get()
	 */
	@Override
	public IPlayer getById(Long id) {
		IPlayer player = new ScrumPlayer();
		
		if (id == null) {
			return player;
		} else if (id == 1000L) {
			player.setDisplayName("Richie McCaw");
		} else if (id == 1001L) {
			return(getHugoSouthwell(player));
		} else if (id == 1002L) { 
			player.setDisplayName("George Gregan");
		} else if (id == 1005L) {
			player.setDisplayName("Kieran Read");
		} else if (id == 1003L) {
			player.setDisplayName("Ben Franks");
		} else if (id == 1004L) {
			player.setDisplayName("Keven Mealamu");
		} else {
			return player; // send back an empty one
		}
		
		player.setId(id);
		return player;
	}

	@Override
	public IPlayer getByScrumId(Long id) {
		
		if (id.equals(14505L)) {
			return getHugoSouthwell(new ScrumPlayer());
		}
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IPlayerFactory#put(net.rugby.foundation.model.shared.IPlayer)
	 */
	@Override
	public IPlayer put(IPlayer player) {
		if (player != null && player.getId() == null)
			player.setId(99L);
		return player;
	}



	private IPlayer getHugoSouthwell(IPlayer p) {
		
		p.setId(1001L);
		p.setScrumId(14505L);
		p.setDisplayName("Hugo Southwell");
		p.setCountry("Scotland");
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.MONTH, 5); 
		cal.set(Calendar.DAY_OF_MONTH, 14);
		cal.set(Calendar.YEAR, 1980);
		p.setBirthDate(cal.getTime());
		p.setHeight(188F);
		p.setWeight(95.7F);
		p.setNumCaps(59);
		p.setPosition(position.FULLBACK);

		return p;
	}


}
