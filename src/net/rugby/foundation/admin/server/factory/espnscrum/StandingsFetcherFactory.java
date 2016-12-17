package net.rugby.foundation.admin.server.factory.espnscrum;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.IStandingsFetcherFactory;
import net.rugby.foundation.admin.server.model.EspnMultiTableStandingsFetcher;
import net.rugby.foundation.admin.server.model.EspnSingleTableStandingsFetcher;
import net.rugby.foundation.admin.server.model.IStandingsFetcher;
import net.rugby.foundation.admin.server.model.ScrumHeinekenStandingsFetcher;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IRound;

public class StandingsFetcherFactory implements IStandingsFetcherFactory {

	private ICompetitionFactory cf;
	private IStandingFactory sf;
	private IConfigurationFactory ccf;
	private ITeamGroupFactory tf;

	@Inject
	public StandingsFetcherFactory(ICompetitionFactory cf, IStandingFactory sf, IConfigurationFactory ccf, ITeamGroupFactory tf) {
		this.cf = cf;
		this.sf = sf;
		this.ccf = ccf;
		this.tf = tf;
	}
	
	@Override
	public IStandingsFetcher getFetcher(IRound r) {
		ICompetition c = null;
		if (r.getCompId() != null) {
			c = cf.get(r.getCompId());
		}
		
//		if (c != null && c.getCompType() == ICompetition.CompetitionType.HEINEKEN_CUP) {
//			return new ScrumHeinekenStandingsFetcher(sf);
//		} else 
			if (c != null && (c.getCompType() == ICompetition.CompetitionType.SUPER_RUGBY ||
				c.getCompType() == ICompetition.CompetitionType.CHALLENGE_CUP ||
				c.getCompType() == ICompetition.CompetitionType.CHAMPIONS_CUP)) {
			return new EspnMultiTableStandingsFetcher(ccf, tf);
		} else if (c != null && (c.getCompType() == ICompetition.CompetitionType.AVIVA_PREMIERSHIP || 
				 				c.getCompType() == ICompetition.CompetitionType.PRO12 || 
				 				c.getCompType() == ICompetition.CompetitionType.TOP14)) {
				return new EspnSingleTableStandingsFetcher(ccf, tf);
		}
			
		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Request for standings fetcher for comp with unrecognized competition type: " + c != null ? c.getCompType().toString() : " (comp was null)");
		return null;
	}

}
