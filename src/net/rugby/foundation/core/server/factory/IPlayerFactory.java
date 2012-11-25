package net.rugby.foundation.core.server.factory;

import net.rugby.foundation.model.shared.IPlayer;

public interface IPlayerFactory {
	
	IPlayer getById(Long id);
	IPlayer getByScrumId(Long id);
	
	IPlayer put(IPlayer r);

	/**
	 * @return all active players for team
	 */
	//List<IPlayer> getAll();
}
