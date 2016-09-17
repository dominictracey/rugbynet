package net.rugby.foundation.admin.server.factory.espnscrum;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.model.EspnLineupFetcher;
import net.rugby.foundation.admin.server.model.ILineupFetcher;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.ILineupSlotFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.model.shared.ICompetition.CompetitionType;

public class EspnLineupFetcherFactory implements ILineupFetcherFactory {

	private IPlayerFactory pf;
	private IMatchGroupFactory mf;
	private IConfigurationFactory ccf;
	private ILineupSlotFactory lsf;

	@Inject
	public void setFactories(ILineupSlotFactory lsf, IPlayerFactory pf, IMatchGroupFactory mf, IConfigurationFactory ccf) {
		this.pf  = pf;
		this.mf = mf;
		this.ccf = ccf;
		this.lsf = lsf;
	}


	public ILineupFetcher getLineupFetcher(CompetitionType fetcherType) {
		
		ILineupFetcher luf =  new EspnLineupFetcher(lsf,ccf, pf, mf);

		return luf;
		
	}


}
