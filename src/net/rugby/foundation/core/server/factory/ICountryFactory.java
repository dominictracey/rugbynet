package net.rugby.foundation.core.server.factory;

import java.util.Iterator;

import net.rugby.foundation.model.shared.Country;
import net.rugby.foundation.model.shared.ICountry;

public interface ICountryFactory {

	public abstract ICountry getById(Long id);

	public abstract ICountry getByName(String name);

	public abstract ICountry put(Country country);

	public abstract Iterator<Country> getAll();

}