/**
 * 
 */
package net.rugby.foundation.game1.server.factory.test;

import com.google.inject.Inject;

import net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory;
import net.rugby.foundation.game1.server.factory.IConfigurationFactory;
import net.rugby.foundation.game1.shared.Configuration;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.IConfiguration;

/**
 * @author home
 *
 */
public class TestConfigurationFactory implements IConfigurationFactory {

	private IClubhouseLeagueMapFactory clmf;

	@Inject
	public TestConfigurationFactory(IClubhouseLeagueMapFactory clmf) {
		this.clmf = clmf;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IConfigurationFactory#get()
	 */
	@Override
	public IConfiguration get() {
		IConfiguration c = new Configuration();
		
		c.getCompetitionClubhouseLeageMapIds().add(50L);
		c.getCompetitionClubhouseLeageMapIds().add(53L);
		
		//populate the map and list
		if (c != null) {
			for (Long clmId : c.getCompetitionClubhouseLeageMapIds()) {
				clmf.setId(clmId);
				IClubhouseLeagueMap chlm = clmf.get();
				c.getCompetitionClubhouseLeageMapList().add(chlm);
				c.getClubhouseIds().add(chlm.getClubhouseId());
				c.getLeagueIdMap().put(chlm.getCompId(), chlm.getLeagueId());
			}
		}


		return c;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IConfigurationFactory#put(net.rugby.foundation.game1.shared.IConfiguration)
	 */
	@Override
	public IConfiguration put(IConfiguration dl) {
		return dl;
	}

}
