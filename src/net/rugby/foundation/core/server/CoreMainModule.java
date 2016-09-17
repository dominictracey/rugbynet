package net.rugby.foundation.core.server;

import net.rugby.foundation.admin.server.model.IRatingSeriesManager;
import net.rugby.foundation.admin.server.model.RatingSeriesManager;
import net.rugby.foundation.admin.server.rules.CoreRuleFactory;
import net.rugby.foundation.admin.server.rules.ICoreRuleFactory;
import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.core.server.factory.IClubhouseFactory;
import net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IContentFactory;
import net.rugby.foundation.core.server.factory.ICountryFactory;
import net.rugby.foundation.core.server.factory.ILineupSlotFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IMatchResultFactory;
import net.rugby.foundation.core.server.factory.IPlaceFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IRatingGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingMatrixFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.IRawScoreFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ISponsorFactory;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyClubhouseFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyAppUserFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyClubhouseMembershipFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyCompetitionFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyConfigurationFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyContentFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyCountryFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyLineupSlotFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyMatchGroupFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyMatchResultFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyPlaceFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyPlayerFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyRatingGroupFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyRatingMatrixFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyRawScoreFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyRoundFactory;
import net.rugby.foundation.core.server.factory.ofy.OfySponsorFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyStandingFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyTeamFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyTeamMatchStatsFactory;
import com.google.appengine.tools.pipeline.impl.servlets.PipelineServlet;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class CoreMainModule extends AbstractModule {
	@Override
	 protected void configure() {
//		bind(CoreServiceImpl.class);
		bind(ICompetitionFactory.class).to(OfyCompetitionFactory.class);
		bind(IRoundFactory.class).to(OfyRoundFactory.class); //.in(Singleton.class);
		bind(IMatchGroupFactory.class).to(OfyMatchGroupFactory.class); //.in(Singleton.class);
		bind(ITeamGroupFactory.class).to(OfyTeamFactory.class);
		bind(IMatchResultFactory.class).to(OfyMatchResultFactory.class);
		bind(IAppUserFactory.class).to(OfyAppUserFactory.class);
		bind(ICoreRuleFactory.class).to(CoreRuleFactory.class);
		bind(IClubhouseFactory.class).to(OfyClubhouseFactory.class);
		bind(IClubhouseMembershipFactory.class).to(OfyClubhouseMembershipFactory.class);
		bind(IConfigurationFactory.class).to(OfyConfigurationFactory.class);
		bind(IAccountManager.class).to(AccountManager.class);
		bind(IExternalAuthticatorProviderFactory.class).to(ExternalAuthenticatorFactory.class);
		bind(IPlayerFactory.class).to(OfyPlayerFactory.class);
		bind(ITeamMatchStatsFactory.class).to(OfyTeamMatchStatsFactory.class);
		bind(IPlayerMatchStatsFactory.class).to(OfyPlayerMatchStatsFactory.class);
		bind(ICountryFactory.class).to(OfyCountryFactory.class);
		bind(PipelineServlet.class).in(Singleton.class);
		//bind(new TypeLiteral<ICachingFactory<IContent>>(){}).to(new TypeLiteral<OfyContentFactory>(){});
		bind(IStandingFactory.class).to(OfyStandingFactory.class);
		bind(IRawScoreFactory.class).to(OfyRawScoreFactory.class);
		bind(IRatingSeriesFactory.class).to(OfyRatingSeriesFactory.class);
		bind(IRatingGroupFactory.class).to(OfyRatingGroupFactory.class);
		bind(IRatingMatrixFactory.class).to(OfyRatingMatrixFactory.class);
		bind(IRatingSeriesManager.class).to(RatingSeriesManager.class);
		bind(IPlaceFactory.class).to(OfyPlaceFactory.class);
		bind(ISponsorFactory.class).to(OfySponsorFactory.class);
		bind(IContentFactory.class).to(OfyContentFactory.class);
		bind(ILineupSlotFactory.class).to(OfyLineupSlotFactory.class);
	}
}

