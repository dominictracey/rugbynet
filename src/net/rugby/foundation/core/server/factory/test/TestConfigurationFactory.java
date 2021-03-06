/**
 * 
 */
package net.rugby.foundation.core.server.factory.test;

import com.google.inject.Inject;

import net.rugby.foundation.core.server.factory.BaseConfigurationFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.CoreConfiguration;
import net.rugby.foundation.model.shared.CoreConfiguration.Environment;
import net.rugby.foundation.model.shared.ICoreConfiguration;

/**
 * @author home
 *
 */
public class TestConfigurationFactory extends BaseConfigurationFactory {

	@Inject
	public TestConfigurationFactory(ICompetitionFactory cf, IUniversalRoundFactory urf, IRatingSeriesFactory rsf) {
		super(cf, urf, rsf);

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IConfigurationFactory#get()
	 */
	@Override
	public ICoreConfiguration getFromPersistentDatastore(Long id) {
		ICoreConfiguration c = new CoreConfiguration();
		c.addCompetition(1L, "Rugby.net Championships");
		c.addCompetition(2L, "Heineken Cup");
		c.addCompUnderway(1L);
		c.addCompUnderway(2L);
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

	@Override
	protected boolean deleteFromPersistentDatastore(ICoreConfiguration t) {
		return true;
	}

	@Override
	public ICoreConfiguration create() {
		// TODO Auto-generated method stub
		return new CoreConfiguration();
	}

}
