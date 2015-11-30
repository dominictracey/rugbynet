/**
 * 
 */
package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import net.rugby.foundation.core.server.factory.BaseConfigurationFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.Competition;
import net.rugby.foundation.model.shared.CoreConfiguration;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICompetition.CompetitionType;
import net.rugby.foundation.model.shared.ICoreConfiguration;

/**
 * @author home
 *
 */
public class OfyConfigurationFactory extends BaseConfigurationFactory implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	
	@Inject
	public OfyConfigurationFactory(ICompetitionFactory cf, IUniversalRoundFactory urf) {
		super(cf, urf);

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IConfigurationFactory#get()
	 */
	@Override
	public ICoreConfiguration getFromPersistentDatastore(Long id) {
		Objectify ofy = DataStoreFactory.getOfy();
		// there should just be one...
		ICoreConfiguration c = ofy.query(CoreConfiguration.class).get();
		
		if (c == null) {
			c = new CoreConfiguration();			
		}
		
		List<Long> copyOf = new ArrayList<Long>();
		copyOf.addAll(c.getCompsUnderway());
		boolean dirty = false;
		for (Long compId : copyOf) {
			ICompetition comp =  (ICompetition)ofy.get(new Key<Competition>(Competition.class,compId));  //cf.get(compId);  // << this should speed it up as we don't build all comps
			if (comp != null) {
				c.addCompetition(compId, comp.getLongName());
				//c.getSeriesMap().put(compId, comp.getSeriesMap());
			} else {
				// remove orphan
				c.getCompsUnderway().remove(compId);
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Removing compId from active comp list in core config: " + compId);
				dirty = true;
			}
		}
		
		if (dirty) {
			put(c);
		}
		
		// and the comps for the client self-cleaning oven
		copyOf.clear();
		copyOf.addAll(c.getCompsForClient());
		dirty = false;
		for (Long compId : copyOf) {
			ICompetition comp = cf.get(compId);
			if (comp != null) {
				c.addCompForClient(compId);
				c.getSeriesMap().put(compId, comp.getSeriesMap());
			} else {
				// remove orphan
				c.getCompsForClient().remove(compId);
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Removing compId from client comp list in core config: " + compId);
				dirty = true;
			}
		}
		
		if (dirty) {
			put(c);
		}
		

		// confirm we have a global comp
		if (c.getGlobalCompId() == null) {
			ICompetition gc = cf.create();
			gc.setAbbr("GLOBAL");
			gc.setCompType(CompetitionType.GLOBAL);
			gc.setLongName("Global Ratings");
			gc.setShortName("Global");
			gc.setUnderway(true);
			cf.put(gc);
			c.setGlobalCompId(gc.getId());
			c.addCompUnderway(gc.getId());
			c.addCompetition(gc.getId(), gc.getShortName());
			put(c);
		}
		
		return c;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IConfigurationFactory#put(net.rugby.foundation.model.shared.ICoreConfiguration)
	 */
	@Override
	public ICoreConfiguration putToPersistentDatastore(ICoreConfiguration conf) {
		if (conf.getDefaultCompId() == null && !conf.getCompsUnderway().isEmpty()) {
			// set it to the first one
			conf.setDefaultCompId(conf.getCompsUnderway().get(0));
		}
		Objectify ofy = DataStoreFactory.getOfy();
		ofy.put(conf);
		return conf;
	}

	@Override
	protected boolean deleteFromPersistentDatastore(ICoreConfiguration t) {
		Objectify ofy = DataStoreFactory.getOfy();
		ofy.delete(t);
		return true;
	}

	@Override
	public ICoreConfiguration create() {
		return new CoreConfiguration();
	}

}
