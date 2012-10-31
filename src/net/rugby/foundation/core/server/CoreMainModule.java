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
import net.rugby.foundation.core.server.factory.ofy.OfyClubhouseFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyAppUserFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyClubhouseMembershipFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyCompetitionFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyConfigurationFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyMatchGroupFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyMatchResultFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyRoundFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyTeamFactory;
import com.google.inject.AbstractModule;

public class CoreMainModule extends AbstractModule {
	@Override
	 protected void configure() {
//		bind(CoreServiceImpl.class);
		bind(ICompetitionFactory.class).to(OfyCompetitionFactory.class);
		bind(IRoundFactory.class).to(OfyRoundFactory.class);
		bind(IMatchGroupFactory.class).to(OfyMatchGroupFactory.class);
		bind(ITeamGroupFactory.class).to(OfyTeamFactory.class);
		bind(IMatchResultFactory.class).to(OfyMatchResultFactory.class);
		bind(IAppUserFactory.class).to(OfyAppUserFactory.class);
		bind(ICoreRuleFactory.class).to(CoreRuleFactory.class);
		bind(IClubhouseFactory.class).to(OfyClubhouseFactory.class);
		bind(IClubhouseMembershipFactory.class).to(OfyClubhouseMembershipFactory.class);
		bind(IConfigurationFactory.class).to(OfyConfigurationFactory.class);
		bind(IAccountManager.class).to(AccountManager.class);
		bind(IExternalAuthticatorProviderFactory.class).to(ExternalAuthenticatorFactory.class);
	}
}

