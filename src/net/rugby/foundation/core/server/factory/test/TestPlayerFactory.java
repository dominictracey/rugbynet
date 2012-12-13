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
		coreyJane();
		conradSmith();
		maaNanu();
		richardKahui();
		aaronCruden();
		piriWeepu();
		tonyWoodcock();
		kevenMealamu();
		owenFranks();
		bradThorn();
		samWhitelock();
		jeromeKaino();
		richieMcCaw();
		kieranRead();
		andrewHore();
		benFranks();
		aliWilliams();
		victorVito();
		andyEllis();
		stephenDonald();
		sonnybillWilliams();
		
		//Awstraalya
		adamAshleyCooper();
		jamesOConnor();
		anthonyFaingaa();
		patMcCabe();
		digbyIoane();
		quadeCooper();
		willGenia();
		sekopeKepu();
		stephenMoore();
		benAlexander();
		danVickerman();
		jamesHorwill();
		rockyElsom();
		davidPocock();
		radikeSamo();
		tatafupolotanau();
		jamesSlipper();
		robSimmons();
		benMcCalman();
		lukeBurgess();
		berrickBarnes();
		robHorne();
	}

	private IPlayer hugoSouthwell() {
		
		IPlayer p = getById(null);
		p.setId(9207001L);
		p.setScrumId(14505L);
		p.setDisplayName("Hugo Southwell");
		p.setCountry(cf.getById(5012L));  // Scotland?
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
		p.setCountry(cf.getById(5001L));  //NZ
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
private IPlayer coreyJane() {
		
		IPlayer p = getById(null);
		p.setId(9001002L);
		p.setScrumId(15235L);
		p.setDisplayName("Corey Jane");
		p.setCountry(cf.getById(5001L));  //NZ
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.MONTH, 2); 
		cal.set(Calendar.DAY_OF_MONTH, 8);
		cal.set(Calendar.YEAR, 1983);
		p.setBirthDate(cal.getTime());
		p.setHeight(182.8F);
		p.setWeight(87.5F);
		p.setNumCaps(43);
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
private IPlayer conradSmith() {
	
	IPlayer p = getById(null);
	p.setId(9001003L);
	p.setScrumId(14650L);
	p.setDisplayName("Conrad Smith");
	p.setCountry(cf.getById(5001L));  //NZ
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 10); 
	cal.set(Calendar.DAY_OF_MONTH, 12);
	cal.set(Calendar.YEAR, 1982);
	p.setBirthDate(cal.getTime());
	p.setHeight(185.4F);
	p.setWeight(94F);
	p.setNumCaps(66);
	p.setPosition(position.CENTER);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("New Zealand");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer maaNanu() {
	
	IPlayer p = getById(null);
	p.setId(9001004L);
	p.setScrumId(14237L);
	p.setDisplayName("Ma'a Nanu");
	p.setCountry(cf.getById(5001L));  //NZ
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 5); 
	cal.set(Calendar.DAY_OF_MONTH, 21);
	cal.set(Calendar.YEAR, 1982);
	p.setBirthDate(cal.getTime());
	p.setHeight(182.8F);
	p.setWeight(103.8F);
	p.setNumCaps(76);
	p.setPosition(position.CENTER);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("New Zealand");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer richardKahui() {
	
	IPlayer p = getById(null);
	p.setId(9001005L);
	p.setScrumId(15236L);
	p.setDisplayName("Richard Kahui");
	p.setCountry(cf.getById(5001L));  //NZ
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 5); 
	cal.set(Calendar.DAY_OF_MONTH, 21);
	cal.set(Calendar.YEAR, 1982);
	p.setBirthDate(cal.getTime());
	p.setHeight(190.5F);
	p.setWeight(98.8F);
	p.setNumCaps(17);
	p.setPosition(position.CENTER);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("New Zealand");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer aaronCruden() {
	
	IPlayer p = getById(null);
	p.setId(9001006L);
	p.setScrumId(117315L);
	p.setDisplayName("Aaron Cruden");
	p.setCountry(cf.getById(5001L));  //NZ
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 1); 
	cal.set(Calendar.DAY_OF_MONTH, 8);
	cal.set(Calendar.YEAR, 1989);
	p.setBirthDate(cal.getTime());
	p.setHeight(177F);
	p.setWeight(83.9F);
	p.setNumCaps(20);
	p.setPosition(position.FLYHALF);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("New Zealand");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer piriWeepu() {
	
	IPlayer p = getById(null);
	p.setId(9001007L);
	p.setScrumId(14680L);
	p.setDisplayName("Piri Weepu");
	p.setCountry(cf.getById(5001L));  //NZ
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 7); 
	cal.set(Calendar.DAY_OF_MONTH, 7);
	cal.set(Calendar.YEAR, 1983);
	p.setBirthDate(cal.getTime());
	p.setHeight(177F);
	p.setWeight(93.9F);
	p.setNumCaps(69);
	p.setPosition(position.SCRUMHALF);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("New Zealand");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer tonyWoodcock() {
	
	IPlayer p = getById(null);
	p.setId(9001008L);
	p.setScrumId(14108L);
	p.setDisplayName("Tony Woodcock");
	p.setCountry(cf.getById(5001L));  //NZ
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 1); 
	cal.set(Calendar.DAY_OF_MONTH, 27);
	cal.set(Calendar.YEAR, 1981);
	p.setBirthDate(cal.getTime());
	p.setHeight(185.4F);
	p.setWeight(117.9F);
	p.setNumCaps(96);
	p.setPosition(position.PROP);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("New Zealand");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer kevenMealamu() {
	
	IPlayer p = getById(null);
	p.setId(9001009L);
	p.setScrumId(14082L);
	p.setDisplayName("Keven Mealamu");
	p.setCountry(cf.getById(5001L));  //NZ
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 3); 
	cal.set(Calendar.DAY_OF_MONTH, 20);
	cal.set(Calendar.YEAR, 1979);
	p.setBirthDate(cal.getTime());
	p.setHeight(180.4F);
	p.setWeight(105.6F);
	p.setNumCaps(102);
	p.setPosition(position.HOOKER);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("New Zealand");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer owenFranks() {
	
	IPlayer p = getById(null);
	p.setId(9001010L);
	p.setScrumId(98923L);
	p.setDisplayName("Owen Franks");
	p.setCountry(cf.getById(5001L));  //NZ
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 12); 
	cal.set(Calendar.DAY_OF_MONTH, 23);
	cal.set(Calendar.YEAR, 1987);
	p.setBirthDate(cal.getTime());
	p.setHeight(185.4F);
	p.setWeight(111.5F);
	p.setNumCaps(44);
	p.setPosition(position.PROP);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("New Zealand");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer bradThorn() {
	
	IPlayer p = getById(null);
	p.setId(9001011L);
	p.setScrumId(14241L);
	p.setDisplayName("Brad Thorn");
	p.setCountry(cf.getById(5001L));  //NZ
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 2); 
	cal.set(Calendar.DAY_OF_MONTH, 3);
	cal.set(Calendar.YEAR, 1975);
	p.setBirthDate(cal.getTime());
	p.setHeight(195.5F);
	p.setWeight(114.7F);
	p.setNumCaps(59);
	p.setPosition(position.LOCK);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("New Zealand");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer samWhitelock() {
	
	IPlayer p = getById(null);
	p.setId(9001012L);
	p.setScrumId(117327L);
	p.setDisplayName("Sam Whitelock");
	p.setCountry(cf.getById(5001L));  //NZ
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 10); 
	cal.set(Calendar.DAY_OF_MONTH, 12);
	cal.set(Calendar.YEAR, 1988);
	p.setBirthDate(cal.getTime());
	p.setHeight(203.2F);
	p.setWeight(113.7F);
	p.setNumCaps(39);
	p.setPosition(position.LOCK);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("New Zealand");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer jeromeKaino() {
	
	IPlayer p = getById(null);
	p.setId(9001013L);
	p.setScrumId(15268L);
	p.setDisplayName("Jerome Kaino");
	p.setCountry(cf.getById(5001L));  //NZ
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 4); 
	cal.set(Calendar.DAY_OF_MONTH, 6);
	cal.set(Calendar.YEAR, 1983);
	p.setBirthDate(cal.getTime());
	p.setHeight(195.3F);
	p.setWeight(104.7F);
	p.setNumCaps(48);
	p.setPosition(position.FLYHALF);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("New Zealand");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer richieMcCaw() {
	
	IPlayer p = getById(null);
	p.setId(9001014L);
	p.setScrumId(13784L);
	p.setDisplayName("Richie McCaw");
	p.setCountry(cf.getById(5001L));  //NZ
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 12); 
	cal.set(Calendar.DAY_OF_MONTH, 31);
	cal.set(Calendar.YEAR, 1980);
	p.setBirthDate(cal.getTime());
	p.setHeight(187.9F);
	p.setWeight(105.7F);
	p.setNumCaps(116);
	p.setPosition(position.FLANKER);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("New Zealand");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer kieranRead() {
	
	IPlayer p = getById(null);
	p.setId(9001015L);
	p.setScrumId(15623L);
	p.setDisplayName("Kieran Read");
	p.setCountry(cf.getById(5001L));  //NZ
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 10); 
	cal.set(Calendar.DAY_OF_MONTH, 26);
	cal.set(Calendar.YEAR, 1985);
	p.setBirthDate(cal.getTime());
	p.setHeight(193.4F);
	p.setWeight(104.7F);
	p.setNumCaps(48);
	p.setPosition(position.NUMBER8);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("New Zealand");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer andrewHore() {
	
	IPlayer p = getById(null);
	p.setId(9001016L);
	p.setScrumId(14020L);
	p.setDisplayName("Andrew Hore");
	p.setCountry(cf.getById(5001L));  //NZ
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 9); 
	cal.set(Calendar.DAY_OF_MONTH, 13);
	cal.set(Calendar.YEAR, 1978);
	p.setBirthDate(cal.getTime());
	p.setHeight(182.8F);
	p.setWeight(109.7F);
	p.setNumCaps(74);
	p.setPosition(position.HOOKER);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("New Zealand");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer benFranks() {
	
	IPlayer p = getById(null);
	p.setId(9001017L);
	p.setScrumId(117319L);
	p.setDisplayName("Ben Franks");
	p.setCountry(cf.getById(5001L));  //NZ
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 3); 
	cal.set(Calendar.DAY_OF_MONTH, 27);
	cal.set(Calendar.YEAR, 1984);
	p.setBirthDate(cal.getTime());
	p.setHeight(182.8F);
	p.setWeight(113.7F);
	p.setNumCaps(23);
	p.setPosition(position.PROP);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("New Zealand");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer aliWilliams() {
	
	IPlayer p = getById(null);
	p.setId(9001018L);
	p.setScrumId(14094L);
	p.setDisplayName("Ali Williams");
	p.setCountry(cf.getById(5001L));  //NZ
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 4); 
	cal.set(Calendar.DAY_OF_MONTH, 30);
	cal.set(Calendar.YEAR, 1981);
	p.setBirthDate(cal.getTime());
	p.setHeight(203.2F);
	p.setWeight(111.5F);
	p.setNumCaps(77);
	p.setPosition(position.LOCK);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("New Zealand");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer victorVito() {
	
	IPlayer p = getById(null);
	p.setId(9001019L);
	p.setScrumId(117326L);
	p.setDisplayName("Victor Vito");
	p.setCountry(cf.getById(5001L));  //NZ
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 3); 
	cal.set(Calendar.DAY_OF_MONTH, 27);
	cal.set(Calendar.YEAR, 1987);
	p.setBirthDate(cal.getTime());
	p.setHeight(193.2F);
	p.setWeight(108.5F);
	p.setNumCaps(20);
	p.setPosition(position.FLANKER);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("New Zealand");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer andyEllis() {
	
	IPlayer p = getById(null);
	p.setId(9001020L);
	p.setScrumId(15403L);
	p.setDisplayName("Andy Ellis");
	p.setCountry(cf.getById(5001L));  //NZ
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 2); 
	cal.set(Calendar.DAY_OF_MONTH, 21);
	cal.set(Calendar.YEAR, 1984);
	p.setBirthDate(cal.getTime());
	p.setHeight(182.8F);
	p.setWeight(88.9F);
	p.setNumCaps(26);
	p.setPosition(position.SCRUMHALF);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("New Zealand");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer stephenDonald() {
	
	IPlayer p = getById(null);
	p.setId(9001021L);
	p.setScrumId(15303L);
	p.setDisplayName("Stephen Donald");
	p.setCountry(cf.getById(5001L));  //NZ
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 12); 
	cal.set(Calendar.DAY_OF_MONTH, 3);
	cal.set(Calendar.YEAR, 1983);
	p.setBirthDate(cal.getTime());
	p.setHeight(190.5F);
	p.setWeight(98.3F);
	p.setNumCaps(23);
	p.setPosition(position.FLYHALF);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("New Zealand");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer sonnybillWilliams() {
	
	IPlayer p = getById(null);
	p.setId(9001022L);
	p.setScrumId(27386L);
	p.setDisplayName("Sonny Bill Williams");
	p.setCountry(cf.getById(5001L));  //NZ
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 8); 
	cal.set(Calendar.DAY_OF_MONTH, 3);
	cal.set(Calendar.YEAR, 1985);
	p.setBirthDate(cal.getTime());
	p.setHeight(193.5F);
	p.setWeight(107.9F);
	p.setNumCaps(19);
	p.setPosition(position.CENTER);

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
		p.setCountry(cf.getById(5003L));  //AUS
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
private IPlayer jamesOConnor() {
		
		IPlayer p = getById(null);
		p.setId(9002002L);  
		p.setScrumId(85497L);
		p.setDisplayName("James O'Connor");
		p.setCountry(cf.getById(5003L));  //AUS
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.MONTH, 7); 
		cal.set(Calendar.DAY_OF_MONTH, 5);
		cal.set(Calendar.YEAR, 1990);
		p.setBirthDate(cal.getTime());
		p.setHeight(180F);
		p.setWeight(89.8F);
		p.setNumCaps(37);
		p.setPosition(position.WING);

		// save it
		put(p);
		
		// team
		ITeamGroup team = tf.getTeamByName("Australia");
		if (team != null) {
			((IGroup)team).add(p);
		}
		return p;
	}
private IPlayer anthonyFaingaa() {
	
	IPlayer p = getById(null);
	p.setId(9002003L);  
	p.setScrumId(91484L);
	p.setDisplayName("Anthony Faingaa");
	p.setCountry(cf.getById(5003L));  //AUS
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 2); 
	cal.set(Calendar.DAY_OF_MONTH, 2);
	cal.set(Calendar.YEAR, 1987);
	p.setBirthDate(cal.getTime());
	p.setHeight(182.8F);
	p.setWeight(91.6F);
	p.setNumCaps(23);
	p.setPosition(position.CENTER);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("Australia");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
  }
private IPlayer patMcCabe() {
	
	IPlayer p = getById(null);
	p.setId(9002004L);  
	p.setScrumId(110215L);
	p.setDisplayName("Pat McCabe");
	p.setCountry(cf.getById(5003L));  //AUS
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 3); 
	cal.set(Calendar.DAY_OF_MONTH, 21);
	cal.set(Calendar.YEAR, 1988);
	p.setBirthDate(cal.getTime());
	p.setHeight(185.4F);
	p.setWeight(93.9F);
	p.setNumCaps(19);
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
private IPlayer digbyIoane() {
	
	IPlayer p = getById(null);
	p.setId(9002005L);  
	p.setScrumId(15641L);
	p.setDisplayName("Digby Ioane");
	p.setCountry(cf.getById(5003L));  //AUS
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 7); 
	cal.set(Calendar.DAY_OF_MONTH, 14);
	cal.set(Calendar.YEAR, 1985);
	p.setBirthDate(cal.getTime());
	p.setHeight(182.8F);
	p.setWeight(95.7F);
	p.setNumCaps(34);
	p.setPosition(position.WING);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("Australia");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer quadeCooper() {
	
	IPlayer p = getById(null);
	p.setId(9002006L);  
	p.setScrumId(85482L);
	p.setDisplayName("Quade Cooper");
	p.setCountry(cf.getById(5003L));  //AUS
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 4); 
	cal.set(Calendar.DAY_OF_MONTH, 5);
	cal.set(Calendar.YEAR, 1988);
	p.setBirthDate(cal.getTime());
	p.setHeight(185.4F);
	p.setWeight(91.6F);
	p.setNumCaps(38);
	p.setPosition(position.FLYHALF);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("Australia");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer willGenia() {
	
	IPlayer p = getById(null);
	p.setId(9002007L);  
	p.setScrumId(100275L);
	p.setDisplayName("Will Genia");
	p.setCountry(cf.getById(5003L));  //AUS
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 1); 
	cal.set(Calendar.DAY_OF_MONTH, 17);
	cal.set(Calendar.YEAR, 1988);
	p.setBirthDate(cal.getTime());
	p.setHeight(175.2F);
	p.setWeight(83.4F);
	p.setNumCaps(41);
	p.setPosition(position.SCRUMHALF);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("Australia");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer sekopeKepu() {
	
	IPlayer p = getById(null);
	p.setId(9002008L);  
	p.setScrumId(16053L);
	p.setDisplayName("Sekope Kepu");
	p.setCountry(cf.getById(5003L));  //AUS
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 2); 
	cal.set(Calendar.DAY_OF_MONTH, 5);
	cal.set(Calendar.YEAR, 1986);
	p.setBirthDate(cal.getTime());
	p.setHeight(187.9F);
	p.setWeight(119F);
	p.setNumCaps(23);
	p.setPosition(position.PROP);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("Australia");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer stephenMoore() {
	
	IPlayer p = getById(null);
	p.setId(9002009L);  
	p.setScrumId(14887L);
	p.setDisplayName("Stephen Moore");
	p.setCountry(cf.getById(5003L));  //AUS
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 1); 
	cal.set(Calendar.DAY_OF_MONTH, 20);
	cal.set(Calendar.YEAR, 1983);
	p.setBirthDate(cal.getTime());
	p.setHeight(185.4F);
	p.setWeight(111.6F);
	p.setNumCaps(76);
	p.setPosition(position.PROP);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("Australia");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer benAlexander() {
	
	IPlayer p = getById(null);
	p.setId(9002010L);  
	p.setScrumId(16050L);
	p.setDisplayName("Ben Alexander");
	p.setCountry(cf.getById(5003L));  //AUS
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 11); 
	cal.set(Calendar.DAY_OF_MONTH, 13);
	cal.set(Calendar.YEAR, 1984);
	p.setBirthDate(cal.getTime());
	p.setHeight(187.9F);
	p.setWeight(119.7F);
	p.setNumCaps(48);
	p.setPosition(position.PROP);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("Australia");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer danVickerman() {
	
	IPlayer p = getById(null);
	p.setId(9002011L);  
	p.setScrumId(14010L);
	p.setDisplayName("Dan Vickerman");
	p.setCountry(cf.getById(5003L));  //AUS
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 6); 
	cal.set(Calendar.DAY_OF_MONTH, 4);
	cal.set(Calendar.YEAR, 1979);
	p.setBirthDate(cal.getTime());
	p.setHeight(203.2F);
	p.setWeight(114.8F);
	p.setNumCaps(63);
	p.setPosition(position.LOCK);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("Australia");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer jamesHorwill() {
	
	IPlayer p = getById(null);
	p.setId(9002012L);  
	p.setScrumId(15676L);
	p.setDisplayName("James Horwill");
	p.setCountry(cf.getById(5003L));  //AUS
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 5); 
	cal.set(Calendar.DAY_OF_MONTH, 29);
	cal.set(Calendar.YEAR, 1985);
	p.setBirthDate(cal.getTime());
	p.setHeight(200.66F);
	p.setWeight(116.5F);
	p.setNumCaps(35);
	p.setPosition(position.LOCK);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("Australia");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer rockyElsom() {
	
	IPlayer p = getById(null);
	p.setId(9002013L);  
	p.setScrumId(14879L);
	p.setDisplayName("Rocky Elsom");
	p.setCountry(cf.getById(5003L));  //AUS
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 5); 
	cal.set(Calendar.DAY_OF_MONTH, 29);
	cal.set(Calendar.YEAR, 1985);
	p.setBirthDate(cal.getTime());
	p.setHeight(198.1F);
	p.setWeight(105.6F);
	p.setNumCaps(75);
	p.setPosition(position.FLANKER);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("Australia");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer davidPocock() {
	
	IPlayer p = getById(null);
	p.setId(9002014L);  
	p.setScrumId(15603L);
	p.setDisplayName("David Pocock");
	p.setCountry(cf.getById(5003L));  //AUS
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 5); 
	cal.set(Calendar.DAY_OF_MONTH, 29);
	cal.set(Calendar.YEAR, 1985);
	p.setBirthDate(cal.getTime());
	p.setHeight(182.8F);
	p.setWeight(103.8F);
	p.setNumCaps(46);
	p.setPosition(position.FLANKER);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("Australia");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer radikeSamo() {
	
	IPlayer p = getById(null);
	p.setId(9002015L);  
	p.setScrumId(14554L);
	p.setDisplayName("Radike Samo");
	p.setCountry(cf.getById(5003L));  //AUS
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 7); 
	cal.set(Calendar.DAY_OF_MONTH, 9);
	cal.set(Calendar.YEAR, 1976);
	p.setBirthDate(cal.getTime());
	p.setHeight(198.1F);
	p.setWeight(115.6F);
	p.setNumCaps(23);
	p.setPosition(position.NUMBER8);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("Australia");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer tatafupolotanau() {
	
	IPlayer p = getById(null);
	p.setId(9002016L);  
	p.setScrumId(15068L);
	p.setDisplayName("Tatafu Polota-Nau");
	p.setCountry(cf.getById(5003L));  //AUS
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 7); 
	cal.set(Calendar.DAY_OF_MONTH, 26);
	cal.set(Calendar.YEAR, 1985);
	p.setBirthDate(cal.getTime());
	p.setHeight(180.3F);
	p.setWeight(112F);
	p.setNumCaps(44);
	p.setPosition(position.PROP);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("Australia");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer jamesSlipper() {
	
	IPlayer p = getById(null);
	p.setId(9002017L);  
	p.setScrumId(117323L);
	p.setDisplayName("James Slipper");
	p.setCountry(cf.getById(5003L));  //AUS
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 6); 
	cal.set(Calendar.DAY_OF_MONTH, 6);
	cal.set(Calendar.YEAR, 1989);
	p.setBirthDate(cal.getTime());
	p.setHeight(185.4F);
	p.setWeight(116.5F);
	p.setNumCaps(34);
	p.setPosition(position.PROP);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("Australia");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer robSimmons() {
	
	IPlayer p = getById(null);
	p.setId(9002018L);  
	p.setScrumId(91481L);
	p.setDisplayName("Rob Simmons");
	p.setCountry(cf.getById(5003L));  //AUS
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 4); 
	cal.set(Calendar.DAY_OF_MONTH, 19);
	cal.set(Calendar.YEAR, 1989);
	p.setBirthDate(cal.getTime());
	p.setHeight(200.6F);
	p.setWeight(114.5F);
	p.setNumCaps(34);
	p.setPosition(position.LOCK);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("Australia");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer benMcCalman() {
	
	IPlayer p = getById(null);
	p.setId(9002019L);  
	p.setScrumId(110286L);
	p.setDisplayName("Ben McCalman");
	p.setCountry(cf.getById(5003L));  //AUS
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 4); 
	cal.set(Calendar.DAY_OF_MONTH, 19);
	cal.set(Calendar.YEAR, 1989);
	p.setBirthDate(cal.getTime());
	p.setHeight(193.4F);
	p.setWeight(105.6F);
	p.setNumCaps(21);
	p.setPosition(position.NUMBER8);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("Australia");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer lukeBurgess() {
	
	IPlayer p = getById(null);
	p.setId(9002020L);  
	p.setScrumId(16015L);
	p.setDisplayName("Luke Burgess");
	p.setCountry(cf.getById(5003L));  //AUS
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 8); 
	cal.set(Calendar.DAY_OF_MONTH, 20);
	cal.set(Calendar.YEAR, 1983);
	p.setBirthDate(cal.getTime());
	p.setHeight(180.3F);
	p.setWeight(88.9F);
	p.setNumCaps(37);
	p.setPosition(position.SCRUMHALF);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("Australia");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer berrickBarnes() {
	
	IPlayer p = getById(null);
	p.setId(9002021L);  
	p.setScrumId(15590L);
	p.setDisplayName("Berrick Barnes");
	p.setCountry(cf.getById(5003L));  //AUS
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 5); 
	cal.set(Calendar.DAY_OF_MONTH, 28);
	cal.set(Calendar.YEAR, 1986);
	p.setBirthDate(cal.getTime());
	p.setHeight(182.8F);
	p.setWeight(86.6F);
	p.setNumCaps(50);
	p.setPosition(position.FLYHALF);

	// save it
	put(p);
	
	// team
	ITeamGroup team = tf.getTeamByName("Australia");
	if (team != null) {
		((IGroup)team).add(p);
	}
	return p;
}
private IPlayer robHorne() {
	
	IPlayer p = getById(null);
	p.setId(9002022L);  
	p.setScrumId(117541L);
	p.setDisplayName("Rob Horne");
	p.setCountry(cf.getById(5003L));  //AUS
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.MONTH, 8); 
	cal.set(Calendar.DAY_OF_MONTH, 15);
	cal.set(Calendar.YEAR, 1989);
	p.setBirthDate(cal.getTime());
	p.setHeight(185.4F);
	p.setWeight(89.8F);
	p.setNumCaps(14);
	p.setPosition(position.CENTER);

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

