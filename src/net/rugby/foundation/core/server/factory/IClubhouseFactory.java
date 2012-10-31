package net.rugby.foundation.core.server.factory;

import java.util.List;

import net.rugby.foundation.model.shared.IClubhouse;

public interface IClubhouseFactory {

	void setId(Long id);
	
	IClubhouse get();
	IClubhouse put(IClubhouse r);

	/**
	 * @return all active clubhouses
	 */
	List<IClubhouse> getAll();
}
