package net.rugby.foundation.admin.server.factory.espnscrum;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.IStandingsFetcherFactory;
import net.rugby.foundation.admin.server.model.IStandingsFetcher;
import net.rugby.foundation.admin.server.model.ScrumHeinekenStandingsFetcher;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IRound;

public class StandingsFetcherFactory implements IStandingsFetcherFactory {

	private ICompetitionFactory cf;
	private IStandingFactory sf;

	@Inject
	public StandingsFetcherFactory(ICompetitionFactory cf, IStandingFactory sf) {
		this.cf = cf;
		this.sf = sf;
	}
	
	@Override
	public IStandingsFetcher getFetcher(IRound r) {
		ICompetition c = null;
		if (r.getCompId() != null) {
			cf.setId(r.getCompId());
			c = cf.getCompetition();
		}
		
		if (c != null && c.getCompType() == ICompetition.CompetitionType.HEINEKEN_CUP) {
			return new ScrumHeinekenStandingsFetcher(sf);
		}
		return null;
	}

}
