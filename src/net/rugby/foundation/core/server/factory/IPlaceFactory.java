package net.rugby.foundation.core.server.factory;

import net.rugby.foundation.model.shared.IServerPlace;

public interface IPlaceFactory extends ICachingFactory<IServerPlace>{
	public abstract IServerPlace getForGuid(String guid);
	public abstract IServerPlace getForName(String name);
}
