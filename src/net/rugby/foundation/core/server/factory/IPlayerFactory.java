package net.rugby.foundation.core.server.factory;

import net.rugby.foundation.model.shared.IPlayer;

public interface IPlayerFactory extends ICachingFactory<IPlayer> {
	
	IPlayer getByScrumId(Long id);


}
