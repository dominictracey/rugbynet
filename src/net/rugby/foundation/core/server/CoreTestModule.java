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
import net.rugby.foundation.core.server.factory.ofy.OfyPlaceFactory;
import net.rugby.foundation.core.server.factory.test.TestAppUserFactory;
import net.rugby.foundation.core.server.factory.test.TestClubhouseFactory;
import net.rugby.foundation.core.server.factory.test.TestClubhouseMembershipFactory;
import net.rugby.foundation.core.server.factory.test.TestCompetitionFactory;
import net.rugby.foundation.core.server.factory.test.TestConfigurationFactory;
import net.rugby.foundation.core.server.factory.test.TestContentFactory;
import net.rugby.foundation.core.server.factory.test.TestCountryFactory;
import net.rugby.foundation.core.server.factory.test.TestMatchGroupFactory;
import net.rugby.foundation.core.server.factory.test.TestMatchResultFactory;
import net.rugby.foundation.core.server.factory.test.TestPlaceFactory;
import net.rugby.foundation.core.server.factory.test.TestPlayerFactory;
import net.rugby.foundation.core.server.factory.test.TestPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.test.TestRatingGroupFactory;
import net.rugby.foundation.core.server.factory.test.TestRatingMatrixFactory;
import net.rugby.foundation.core.server.factory.test.TestRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.test.TestRawScoreFactory;
import net.rugby.foundation.core.server.factory.test.TestRoundFactory;
import net.rugby.foundation.core.server.factory.test.TestSponsorFactory;
import net.rugby.foundation.core.server.factory.test.TestStandingFactory;
import net.rugby.foundation.core.server.factory.test.TestTeamFactory;
import net.rugby.foundation.core.server.factory.test.TestTeamMatchStatsFactory;
import com.google.inject.AbstractModule;

public class CoreTestModule extends AbstractModule {
	@Override
	 protected void configure() {
//		bind(CoreServiceImpl.class);
		bind(ICompetitionFactory.class).to(TestCompetitionFactory.class);
		bind(IRoundFactory.class).to(TestRoundFactory.class); //.in(Singleton.class);
		bind(IMatchGroupFactory.class).to(TestMatchGroupFactory.class); //.in(Singleton.class);
		bind(ITeamGroupFactory.class).to(TestTeamFactory.class);
		bind(IMatchResultFactory.class).to(TestMatchResultFactory.class);
		bind(IAppUserFactory.class).to(TestAppUserFactory.class);
		bind(ICoreRuleFactory.class).to(CoreRuleFactory.class);
		bind(IClubhouseFactory.class).to(TestClubhouseFactory.class);
		bind(IClubhouseMembershipFactory.class).to(TestClubhouseMembershipFactory.class);
		bind(IConfigurationFactory.class).to(TestConfigurationFactory.class);
		bind(IAccountManager.class).to(AccountManager.class);
		bind(IExternalAuthticatorProviderFactory.class).to(ExternalAuthenticatorFactory.class);
		bind(IPlayerFactory.class).to(TestPlayerFactory.class);
		bind(ITeamMatchStatsFactory.class).to(TestTeamMatchStatsFactory.class);
		bind(IPlayerMatchStatsFactory.class).to(TestPlayerMatchStatsFactory.class);
		bind(ICountryFactory.class).to(TestCountryFactory.class);
		//bind(new TypeLiteral<ICachingFactory<IContent>>(){}).to(new TypeLiteral<TestContentFactory>(){});
		bind(IStandingFactory.class).to(TestStandingFactory.class);
		bind(IRawScoreFactory.class).to(TestRawScoreFactory.class);
		bind(IRatingSeriesFactory.class).to(TestRatingSeriesFactory.class);
		bind(IRatingGroupFactory.class).to(TestRatingGroupFactory.class);
		bind(IRatingMatrixFactory.class).to(TestRatingMatrixFactory.class);
		bind(IRatingSeriesManager.class).to(RatingSeriesManager.class);
		bind(IPlaceFactory.class).to(TestPlaceFactory.class);
		bind(ISponsorFactory.class).to(TestSponsorFactory.class);
		bind(IContentFactory.class).to(TestContentFactory.class);
	}
}

