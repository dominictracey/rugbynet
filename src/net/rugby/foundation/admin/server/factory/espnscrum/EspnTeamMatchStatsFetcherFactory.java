package net.rugby.foundation.admin.server.factory.espnscrum;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.model.EspnTeamMatchStatsFetcher;
import net.rugby.foundation.admin.server.model.ITeamMatchStatsFetcher;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.model.shared.ICompetition.CompetitionType;

public class EspnTeamMatchStatsFetcherFactory implements ITeamMatchStatsFetcherFactory  {

	private IMatchGroupFactory mf;
	private IConfigurationFactory ccf;
	private ITeamMatchStatsFactory tmsf;
	private ITeamGroupFactory tf;

	@Inject
	public void setFactories(ITeamGroupFactory tf, ITeamMatchStatsFactory tmsf, IMatchGroupFactory mf, IConfigurationFactory ccf) {
		this.tf = tf;
		this.tmsf  = tmsf;
		this.mf = mf;
		this.ccf = ccf;
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.espnscrum.ITeamMatchStatsFetcherFactory#get(net.rugby.foundation.model.shared.ICompetition.CompetitionType)
	 */
	@Override
	public ITeamMatchStatsFetcher get(CompetitionType fetcherType) {	
		return  new EspnTeamMatchStatsFetcher(tf, tmsf, mf, ccf);
		
	}


}
