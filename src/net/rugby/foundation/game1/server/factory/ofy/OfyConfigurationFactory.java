/**
 * 
 */
package net.rugby.foundation.game1.server.factory.ofy;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.googlecode.objectify.Objectify;

import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory;
import net.rugby.foundation.game1.server.factory.IConfigurationFactory;
import net.rugby.foundation.game1.shared.Configuration;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.IConfiguration;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.ICompetition;

/**
 * @author home
 *
 */
public class OfyConfigurationFactory implements IConfigurationFactory {

	private final Objectify ofy;
	private IClubhouseLeagueMapFactory chlmf;
	private ICompetitionFactory cf;

	@Inject
	public OfyConfigurationFactory(IClubhouseLeagueMapFactory chlmf, ICompetitionFactory cf) {
		this.ofy = DataStoreFactory.getOfy();
		this.chlmf = chlmf;
		this.cf = cf;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IConfigurationFactory#get()
	 */
	@Override
	public IConfiguration get() {
		
		//there should only be one!
		Configuration c = ofy.query(Configuration.class).get();
		
		assert (c!= null);
		
		//populate the map
		if (c != null) {
			for (Long clmId : c.getCompetitionClubhouseLeageMapIds()) {
				chlmf.setId(clmId);
				IClubhouseLeagueMap chlm = chlmf.get();
				c.getCompetitionClubhouseLeageMapList().add(chlm);
				c.getClubhouseIds().add(chlm.getClubhouseId());
				c.getLeagueIdMap().put(chlm.getCompId(), chlm.getLeagueId());
			}
		} else { //create basic one with entries for underway comps
			c = new Configuration();
			//c.setClubhouseIds(new ArrayList<Long>());
			c.setCompetitionClubhouseLeageMapIds(new ArrayList<Long>());
			//c.setLeagueIdMap(new HashMap<Long,Long>());
			
			List<ICompetition> list = cf.getUnderwayComps();
			for (ICompetition comp: list) {
				chlmf.setClubhouseAndCompId(comp.getId(), comp.getCompClubhouseId());
				
				IClubhouseLeagueMap clm = chlmf.get();
				if (clm != null) {
					c.getCompetitionClubhouseLeageMapIds().add(clm.getId());
					//c.getClubhouseIds().add(comp.getCompClubhouseId());
					//c.getLeagueIdMap().put(comp.getId(), clm.getLeagueId());
					//c.getCompetitionClubhouseLeageMapList().add(clm);
				}
			}
			
			ofy.put(c);
			return get();  // fancy
		}
		
		return c;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IConfigurationFactory#put(net.rugby.foundation.game1.shared.IConfiguration)
	 */
	@Override
	public IConfiguration put(IConfiguration c) {
		for (IClubhouseLeagueMap clm : c.getCompetitionClubhouseLeageMapList()) {
			ofy.put(clm);
			if (!c.getClubhouseIds().contains(clm.getClubhouseId())) {
				c.getClubhouseIds().add(clm.getClubhouseId());
			}
			if (!c.getLeagueIdMap().containsKey(clm.getCompId())) {
				c.getLeagueIdMap().put(clm.getCompId(),clm.getLeagueId());
			}
			if (!c.getCompetitionClubhouseLeageMapIds().contains(clm.getId())) {
				c.getCompetitionClubhouseLeageMapIds().add(clm.getId());
			}
		}

		ofy.put(c);

		return c;
	}

}
