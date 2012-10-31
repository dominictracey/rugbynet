/**
 * 
 */
package net.rugby.foundation.core.server.factory.test;

import java.util.ArrayList;
import java.util.List;

import net.rugby.foundation.core.server.factory.IClubhouseFactory;
import net.rugby.foundation.model.shared.Clubhouse;
import net.rugby.foundation.model.shared.IClubhouse;

/**
 * @author home
 *
 */
public class TestClubhouseFactory implements IClubhouseFactory {

	private Long id;
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseFactory#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseFactory#get()
	 */
	@Override
	public IClubhouse get() {
		IClubhouse ch = new Clubhouse();
		
		if (id == null) {
			return ch;
		} else if (id == 70L) {
			ch.setDescription("Rugby.net Championships");
			ch.setName("Rugby.net");
			ch.setOwnerID(7000L);  //TODO who should own CCs?
		} else if (id == 71L) {
			ch.setDescription("Portland Rugby Football Club Online");
			ch.setName("PRFC");
			ch.setOwnerID(7001L);
		} else if (id == 72L) {  // a stock group 
			ch.setDescription("Boston RFC");
			ch.setName("BRFC");
			ch.setOwnerID(7004L);  // is this the admin?
		} else if (id == 75L) {  // CC for Heineken Cup
			ch.setDescription("Heineken Cup");
			ch.setName("Heineken Cup");
			ch.setOwnerID(7000L);   //TODO who should own CCs?
		} else if (id == 73L) {  // Workflow test - no CLM
			ch.setDescription("No CLM");
			ch.setName("Bad 1 - no CLM");
			ch.setOwnerID(7001L);  
		} else if (id == 74L) {  // Workflow test - no League
			ch.setDescription("No League");
			ch.setName("Bad 2 - no league");
			ch.setOwnerID(7001L);  
		} else {
			return ch; // send back an empty one
		}
		
		ch.setId(id);
		return ch;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseFactory#put(net.rugby.foundation.model.shared.IClubhouse)
	 */
	@Override
	public IClubhouse put(IClubhouse ch) {
		ch.setId(99L);
		return ch;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseFactory#getAll()
	 */
	@Override
	public List<IClubhouse> getAll() {
		List<IClubhouse> all = new ArrayList<IClubhouse>();
		
		setId(70L);
		all.add(get());
		setId(71L);
		all.add(get());
		setId(72L);
		all.add(get());
		setId(73L);
		all.add(get());
		setId(74L);
		all.add(get());
		setId(75L);
		all.add(get());		
		return all;
	}

}
