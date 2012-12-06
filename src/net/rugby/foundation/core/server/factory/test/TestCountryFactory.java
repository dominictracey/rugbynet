package net.rugby.foundation.core.server.factory.test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.rugby.foundation.core.server.factory.ICountryFactory;
import net.rugby.foundation.model.shared.Country;
import net.rugby.foundation.model.shared.ICountry;

public class TestCountryFactory implements Serializable, ICountryFactory {

	private static Map<Long, Country> idMap = new HashMap<Long,Country>();
	private static Map<String, Country> nameMap = new HashMap<String, Country>();
	
	private static Long counter = 10000L;
	/**
	 * 
	 */
	private static final long serialVersionUID = -8226478123240704157L;

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.test.ICountryFactory#getById(java.lang.Long)
	 */
	@Override
	public ICountry getById(Long id) {
		if (idMap.get(id) != null) {
			return idMap.get(id);
		}
		
		if (id.equals(5001L)) {
			return NewZealand();
		} else if (id.equals(5002L)) {
			return Australia();
		} else {
			return null;
		}

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.test.ICountryFactory#getByName(java.lang.String)
	 */
	@Override
	public ICountry getByName(String name) {
		if (nameMap.get(name) != null) {
			return nameMap.get(name);
		}
		
		if (name.equals("New Zealand")) {
			return NewZealand();
		} else if (name.equals("Australia")) {
			return Australia();
		} else {
			return null;
		}

	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.test.ICountryFactory#put(net.rugby.foundation.model.shared.Country)
	 */
	@Override
	public ICountry put(Country country) {
		
		if (country.getId() == null) {
			country.setId(counter++); 
		}
		
		idMap.put(country.getId(), country);
		nameMap.put(country.getName(), country);
		
		return country;
	}
	
	private ICountry NewZealand()
	{
		Country c = new Country(1L, "New Zealand", "NEW ZEALAND", "NZL", "All Blacks");
		idMap.put(c.getId(), c);
		nameMap.put(c.getName(), c);
		return c;
	}
	
	private ICountry Australia() {
		Country c = new Country(2L, "Australia", "AUSTRALIA", "AUS", "Wallabies");
		idMap.put(c.getId(), c);
		nameMap.put(c.getName(), c);
		return c;
	}

	@Override
	public Iterator<Country> getAll() {
		if (idMap != null) {
			return idMap.values().iterator();
		} else {
			return null;
		}
	}


}
