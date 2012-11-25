package net.rugby.foundation.core.server;

import net.rugby.foundation.admin.server.rules.CoreRuleFactory;
import net.rugby.foundation.admin.server.rules.ICoreRuleFactory;
import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.core.server.factory.IClubhouseFactory;
import net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IMatchResultFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.test.TestAppUserFactory;
import net.rugby.foundation.core.server.factory.test.TestClubhouseFactory;
import net.rugby.foundation.core.server.factory.test.TestClubhouseMembershipFactory;
import net.rugby.foundation.core.server.factory.test.TestCompetitionFactory;
import net.rugby.foundation.core.server.factory.test.TestConfigurationFactory;
import net.rugby.foundation.core.server.factory.test.TestMatchGroupFactory;
import net.rugby.foundation.core.server.factory.test.TestMatchResultFactory;
import net.rugby.foundation.core.server.factory.test.TestRoundFactory;
import net.rugby.foundation.core.server.factory.test.TestTeamFactory;

import com.google.inject.AbstractModule;

public class CoreTestModule extends AbstractModule {
	@Override
	 protected void configure() {
//		bind(CoreServiceImpl.class);
		bind(ICompetitionFactory.class).to(TestCompetitionFactory.class);
		bind(IRoundFactory.class).to(TestRoundFactory.class);
		bind(IMatchGroupFactory.class).to(TestMatchGroupFactory.class);
		bind(ITeamGroupFactory.class).to(TestTeamFactory.class);
		bind(IMatchResultFactory.class).to(TestMatchResultFactory.class);
		bind(IAppUserFactory.class).to(TestAppUserFactory.class);
		bind(ICoreRuleFactory.class).to(CoreRuleFactory.class);
		bind(IClubhouseFactory.class).to(TestClubhouseFactory.class);
		bind(IClubhouseMembershipFactory.class).to(TestClubhouseMembershipFactory.class);
		bind(IConfigurationFactory.class).to(TestConfigurationFactory.class);
		bind(IAccountManager.class).to(AccountManager.class);
		bind(IExternalAuthticatorProviderFactory.class).to(ExternalAuthenticatorFactory.class);
	}
}

