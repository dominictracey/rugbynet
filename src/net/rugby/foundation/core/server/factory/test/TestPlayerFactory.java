/**
 * 
 */
package net.rugby.foundation.core.server.factory.test;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;

import net.rugby.foundation.core.server.factory.ICountryFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.IGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.ITeamGroup;
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
	private ICountryFactory cf;
	private Map<Long,IPlayer> idMap = new HashMap<Long,IPlayer>();
	private Map<Long,IPlayer> scrumIdMap = new HashMap<Long,IPlayer>();
	
	private Long count = 1000000L;
	private ITeamGroupFactory tf;

	@Inject
	public TestPlayerFactory(ICountryFactory cf) {
		this.cf = cf;	
	}

	public void setTeamFactory(ITeamGroupFactory tf) {
		this.tf = tf;
		populate();
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IPlayerFactory#get()
	 */
	@Override
	public IPlayer getById(Long id) {
		if (id == null) {
			IPlayer player = new ScrumPlayer();		
			return player;
		} else {
			return idMap.get(id);
		}
	}

	@Override
	public IPlayer getByScrumId(Long id) {
		if (id == null) {
			IPlayer player = new ScrumPlayer();		
			return player;
		} else {
			return scrumIdMap.get(id);
		}
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IPlayerFactory#put(net.rugby.foundation.model.shared.IPlayer)
	 */
	@Override
	public IPlayer put(IPlayer player) {
		if (player == null) {
			return null;
		}
		
		if (player.getId() == null) {
			player.setId(count++);
		}
		
		idMap.put(player.getId(), player);
		
		if (player.getScrumId() != null) {
			scrumIdMap.put(player.getScrumId(), player);
		}
		
		return player;
	}


	private void populate() {
		hugoSouthwell();
		
		//New Zillund
		israelDagg();
		
		//Awstraalya
		adamAshleyCooper();
	}

	private IPlayer hugoSouthwell() {
		
		IPlayer p = getById(null);
		p.setId(9207001L);
		p.setScrumId(14505L);
		p.setDisplayName("Hugo Southwell");
		p.setCountry(cf.getById(12L));  // Scotland?
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.MONTH, 5); 
		cal.set(Calendar.DAY_OF_MONTH, 14);
		cal.set(Calendar.YEAR, 1980);
		p.setBirthDate(cal.getTime());
		p.setHeight(188F);
		p.setWeight(95.7F);
		p.setNumCaps(59);
		p.setPosition(position.FULLBACK);

		// save it
		put(p);
		
		// team
		ITeamGroup team = tf.getTeamByName("Toulouse");
		if (team != null) {
			((IGroup)team).add(p);
		} else {
			// didn't call setTeamFactory
		}
		
		return p;
	}


	// NEW ZEALAND
	private IPlayer israelDagg() {
		
		IPlayer p = getById(null);
		p.setId(9001001L);
		p.setScrumId(117316L);
		p.setDisplayName("Israel Dagg");
		p.setCountry(cf.getById(1L));  //NZ
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.MONTH, 6); 
		cal.set(Calendar.DAY_OF_MONTH, 6);
		cal.set(Calendar.YEAR, 1988);
		p.setBirthDate(cal.getTime());
		p.setHeight(185F);
		p.setWeight(94.8F);
		p.setNumCaps(25);
		p.setPosition(position.FULLBACK);

		// save it
		put(p);
		
		// team
		ITeamGroup team = tf.getTeamByName("New Zealand");
		if (team != null) {
			((IGroup)team).add(p);
		}
		return p;
	}
	
	
	// AUSTRALIA
	private IPlayer adamAshleyCooper() {
		
		IPlayer p = getById(null);
		p.setId(9002001L);  
		p.setScrumId(117316L);
		p.setDisplayName("Adam Ashley-Cooper");
		p.setCountry(cf.getById(2L));  //AUS
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.MONTH, 3); 
		cal.set(Calendar.DAY_OF_MONTH, 27);
		cal.set(Calendar.YEAR, 1984);
		p.setBirthDate(cal.getTime());
		p.setHeight(183F);
		p.setWeight(97.9F);
		p.setNumCaps(77);
		p.setPosition(position.FULLBACK);

		// save it
		put(p);
		
		// team
		ITeamGroup team = tf.getTeamByName("Australia");
		if (team != null) {
			((IGroup)team).add(p);
		}
		return p;
	}
	
}
