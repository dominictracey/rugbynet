/**
 * 
 */
package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import net.rugby.foundation.core.server.factory.BaseConfigurationFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.Competition;
import net.rugby.foundation.model.shared.CoreConfiguration;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.CoreConfiguration.Environment;
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
	public OfyConfigurationFactory(ICompetitionFactory cf, IUniversalRoundFactory urf, IRatingSeriesFactory rsf) {
		super(cf, urf, rsf);

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IConfigurationFactory#get()
	 */
	@Override
	public ICoreConfiguration getFromPersistentDatastore(Long id) {
		Objectify ofy = DataStoreFactory.getOfy();
		// there should just be one...
		ICoreConfiguration cc = ofy.query(CoreConfiguration.class).get();
		
		if (cc == null) {
			cc = create();	
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Creating new core configuration, Don't forget to set global and default comps!");
			ofy.put(cc);
		}

		// set up transient values
		cc.setCurrentUROrdinal(urf.get(new DateTime()).ordinal);
		
		
		// build all, underway and forClient lists from scratch as they are not stored in the db
		Map<Long,String> allCompNames = cf.getAllCompIds();
		
		List<Long> underwayComps = cf.getUnderwayCompIds();
		List<Long> clientComps = cf.getClientComps();
		
		for (Long cid: allCompNames.keySet()) {
			cc.getAllComps().add(0, cid);
			cc.addCompetition(cid, allCompNames.get(cid));
		}
		
		for (Long cid: underwayComps) {
			cc.getCompsUnderway().add(cid);
		}
		
		for (Long cid: clientComps) {
			cc.getCompsForClient().add(cid);
			cc.getSeriesMap().put(cid, rsf.getModesForComp(cid));
		}		
		
	
		// confirm we have a global comp
//					if (c.getGlobalCompId() == null) {
//						ICompetition gc = cf.create();
//						gc.setAbbr("GLOBAL");
//						gc.setCompType(CompetitionType.GLOBAL);
//						gc.setLongName("Global Ratings");
//						gc.setShortName("Global");
//						gc.setUnderway(true);
//						cf.put(gc);
//						c.setGlobalCompId(gc.getId());
//						c.addCompUnderway(gc.getId());
//						c.addCompetition(gc.getId(), gc.getShortName());
//						put(c);
//					}
		
		
		if (cc.getEnvironment() == null) {
			cc.setEnvironment(Environment.LOCAL);
			put(cc);
		}
	
		return cc;
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
		ICoreConfiguration c = new CoreConfiguration();
		c.setEnvironment(Environment.LOCAL);
		return c;
	}

}
