package net.rugby.foundation.core.server.factory;

import net.rugby.foundation.model.shared.ICoreConfiguration;


public abstract class BaseConfigurationFactory extends BaseCachingFactory<ICoreConfiguration> implements IConfigurationFactory {

	private Long id = null;

	// since there is only one, this convenience method gets it
	@Override
	public ICoreConfiguration get() {
		if (id != null) {
			return get(id);
		} else {
			ICoreConfiguration cc = getFromPersistentDatastore(0L);
			id = cc.getId();
			return cc;
		}
	}
}
