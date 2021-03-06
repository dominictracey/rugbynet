package net.rugby.foundation.admin.server;

import net.rugby.foundation.admin.server.factory.IAdminTaskFactory;
import net.rugby.foundation.admin.server.factory.IBlurbFactory;
import net.rugby.foundation.admin.server.factory.IDigestEmailFactory;
import net.rugby.foundation.admin.server.factory.IForeignCompetitionFetcherFactory;
import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.server.factory.IPlayerMatchStatsFetcherFactory;
import net.rugby.foundation.admin.server.factory.IQueryRatingEngineFactory;
import net.rugby.foundation.admin.server.factory.IResultFetcherFactory;
import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.server.factory.IStandingsFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.EspnCompetitionFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.EspnLineupFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.EspnTeamMatchStatsFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.ILineupFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.ITeamMatchStatsFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.admin.server.factory.espnscrum.ScrumCompetitionFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.ScrumPlayerMatchStatsFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.ScrumQueryRatingEngineFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.ScrumResultFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.StandingsFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.UrlCacher;
import net.rugby.foundation.admin.server.factory.ofy.OfyAdminTaskFactory;
import net.rugby.foundation.admin.server.factory.ofy.OfyBlurbFactory;
import net.rugby.foundation.admin.server.factory.ofy.OfyDigestEmailFactory;
import net.rugby.foundation.admin.server.factory.ofy.OfyMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.server.factory.ofy.OfySeriesConfigurationFactory;
import net.rugby.foundation.admin.server.orchestration.IOrchestrationConfigurationFactory;
import net.rugby.foundation.admin.server.orchestration.IOrchestrationFactory;
import net.rugby.foundation.admin.server.orchestration.OfyOrchestrationConfigurationFactory;
import net.rugby.foundation.admin.server.orchestration.OrchestrationFactory;
import net.rugby.foundation.admin.server.rules.CoreRuleFactory;
import net.rugby.foundation.admin.server.rules.ICoreRuleFactory;
import net.rugby.foundation.admin.server.workflow.IWorkflowConfigurationFactory;
import net.rugby.foundation.admin.server.workflow.IWorkflowFactory;
import net.rugby.foundation.admin.server.workflow.OfyWorkflowConfigurationFactory;
import net.rugby.foundation.admin.server.workflow.OfyWorkflowFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.core.server.factory.UniversalRoundFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyRatingQueryFactory;

import com.google.inject.AbstractModule;

public class AdminMainModule extends AbstractModule {
	@Override
	 protected void configure() {
		bind(IWorkflowConfigurationFactory.class).to(OfyWorkflowConfigurationFactory.class);
		bind(IOrchestrationConfigurationFactory.class).to(OfyOrchestrationConfigurationFactory.class);
		bind(IOrchestrationFactory.class).to(OrchestrationFactory.class);
		bind(ICoreRuleFactory.class).to(CoreRuleFactory.class);
		bind(IResultFetcherFactory.class).to(ScrumResultFetcherFactory.class);
		bind(IForeignCompetitionFetcherFactory.class).to(EspnCompetitionFetcherFactory.class);
		bind(IWorkflowFactory.class).to(OfyWorkflowFactory.class);
		bind(IAdminTaskFactory.class).to(OfyAdminTaskFactory.class);
		bind(IPlayerMatchStatsFetcherFactory.class).to(ScrumPlayerMatchStatsFetcherFactory.class);
		bind(IMatchRatingEngineSchemaFactory.class).to(OfyMatchRatingEngineSchemaFactory.class);
		bind(IQueryRatingEngineFactory.class).to(ScrumQueryRatingEngineFactory.class);
		bind(IUrlCacher.class).to(UrlCacher.class);
		bind(IStandingsFetcherFactory.class).to(StandingsFetcherFactory.class);
		bind(IRatingQueryFactory.class).to(OfyRatingQueryFactory.class);
		bind(IPlayerRatingFactory.class).to(OfyPlayerRatingFactory.class);
		bind(ISeriesConfigurationFactory.class).to(OfySeriesConfigurationFactory.class);
		bind(IUniversalRoundFactory.class).to(UniversalRoundFactory.class);
		bind(IBlurbFactory.class).to(OfyBlurbFactory.class);
		bind(IDigestEmailFactory.class).to(OfyDigestEmailFactory.class);
		bind(ILineupFetcherFactory.class).to(EspnLineupFetcherFactory.class);
		bind(ITeamMatchStatsFetcherFactory.class).to(EspnTeamMatchStatsFetcherFactory.class);
	}
	
}

