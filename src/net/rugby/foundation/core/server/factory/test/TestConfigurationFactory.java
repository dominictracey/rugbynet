/**
 * 
 */
package net.rugby.foundation.core.server.factory.test;

import net.rugby.foundation.core.server.factory.BaseConfigurationFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.model.shared.CoreConfiguration;
import net.rugby.foundation.model.shared.CoreConfiguration.Environment;
import net.rugby.foundation.model.shared.ICoreConfiguration;

/**
 * @author home
 *
 */
public class TestConfigurationFactory extends BaseConfigurationFactory implements IConfigurationFactory {

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IConfigurationFactory#get()
	 */
	@Override
	public ICoreConfiguration getFromPersistentDatastore() {
		ICoreConfiguration c = new CoreConfiguration();
		c.addCompetition(1L, "Rugby.net Championships");
		c.addCompetition(2L, "Heineken Cup");
		c.setDefaultCompId(1L);
		c.setEnvironment(Environment.LOCAL);
		return c;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IConfigurationFactory#put(net.rugby.foundation.model.shared.ICoreConfiguration)
	 */
	@Override
	public ICoreConfiguration putToPersistentDatastore(ICoreConfiguration conf) {
		// NO-OP
		return conf;
	}

}
