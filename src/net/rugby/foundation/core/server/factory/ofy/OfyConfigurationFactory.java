/**
 * 
 */
package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;

import com.google.inject.Inject;
import com.googlecode.objectify.Objectify;

import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.model.shared.CoreConfiguration;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;

/**
 * @author home
 *
 */
public class OfyConfigurationFactory implements IConfigurationFactory, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Objectify ofy;
	private ICompetitionFactory cf;

	
	@Inject
	OfyConfigurationFactory(ICompetitionFactory cf) {
		this.ofy = DataStoreFactory.getOfy();
		
		this.cf = cf;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IConfigurationFactory#get()
	 */
	@Override
	public ICoreConfiguration get() {
		
		// there should just be one...
		ICoreConfiguration c = ofy.query(CoreConfiguration.class).get();
		
		if (c == null) {
			c = new CoreConfiguration();			
		}
		
		for (Long compId : c.getCompsUnderway()) {
			cf.setId(compId);
			ICompetition comp = cf.getCompetition();
			c.addCompetition(compId, comp.getLongName());
		}
		

		
		return c;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IConfigurationFactory#put(net.rugby.foundation.model.shared.ICoreConfiguration)
	 */
	@Override
	public ICoreConfiguration put(ICoreConfiguration conf) {
		if (conf.getDefaultCompId() == null) {
			// set it to the first one
			conf.setDefaultCompId(conf.getCompsUnderway().get(0));
		}
		ofy.put(conf);
		return conf;
	}

}
