/**
 * 
 */
package net.rugby.foundation.core.server.factory.test;

import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.model.shared.CoreConfiguration;
import net.rugby.foundation.model.shared.ICoreConfiguration;

/**
 * @author home
 *
 */
public class TestConfigurationFactory implements IConfigurationFactory {

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IConfigurationFactory#get()
	 */
	@Override
	public ICoreConfiguration get() {
		ICoreConfiguration c = new CoreConfiguration();
		c.addCompetition(1L, "Rugby.net Championships");
		c.addCompetition(2L, "Heineken Cup");
		c.setDefaultCompId(2L);
		return c;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IConfigurationFactory#put(net.rugby.foundation.model.shared.ICoreConfiguration)
	 */
	@Override
	public ICoreConfiguration put(ICoreConfiguration conf) {
		// NO-OP
		return conf;
	}

}
