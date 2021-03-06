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
import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.admin.server.factory.espnscrum.ScrumCompetitionFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.ScrumPlayerMatchStatsFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.ScrumQueryRatingEngineFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.ScrumResultFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.StandingsFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.TestUrlCacher;
import net.rugby.foundation.admin.server.factory.test.TestAdminTaskFactory;
import net.rugby.foundation.admin.server.factory.test.TestBlurbFactory;
import net.rugby.foundation.admin.server.factory.test.TestDigestEmailFactory;
import net.rugby.foundation.admin.server.factory.test.TestMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.server.factory.test.TestSeriesConfigurationFactory;
import net.rugby.foundation.admin.server.orchestration.IOrchestrationConfigurationFactory;
import net.rugby.foundation.admin.server.orchestration.IOrchestrationFactory;
import net.rugby.foundation.admin.server.orchestration.OrchestrationFactory;
import net.rugby.foundation.admin.server.orchestration.TestOrchestrationConfigurationFactory;
import net.rugby.foundation.admin.server.rules.CoreRuleFactory;
import net.rugby.foundation.admin.server.rules.ICoreRuleFactory;
import net.rugby.foundation.admin.server.workflow.IWorkflowConfigurationFactory;
import net.rugby.foundation.admin.server.workflow.IWorkflowFactory;
import net.rugby.foundation.admin.server.workflow.TestWorkflowConfigurationFactory;
import net.rugby.foundation.admin.server.workflow.TestWorkflowFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.core.server.factory.UniversalRoundFactory;
import net.rugby.foundation.core.server.factory.test.TestPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.test.TestRatingQueryFactory;

import com.google.inject.AbstractModule;

public class AdminTestModule extends AbstractModule {
	@Override
	 protected void configure() {
		bind(IWorkflowConfigurationFactory.class).to(TestWorkflowConfigurationFactory.class);
		bind(IOrchestrationConfigurationFactory.class).to(TestOrchestrationConfigurationFactory.class);
		//bind(IWorkflow.class).to(CompetitionWorkflow.class);
		bind(IOrchestrationFactory.class).to(OrchestrationFactory.class);
		bind(ICoreRuleFactory.class).to(CoreRuleFactory.class);
		bind(IResultFetcherFactory.class).to(ScrumResultFetcherFactory.class);
		bind(IForeignCompetitionFetcherFactory.class).to(ScrumCompetitionFetcherFactory.class);
		bind(IWorkflowFactory.class).to(TestWorkflowFactory.class);
		bind(IAdminTaskFactory.class).to(TestAdminTaskFactory.class);
		bind(IPlayerMatchStatsFetcherFactory.class).to(ScrumPlayerMatchStatsFetcherFactory.class);
		bind(IMatchRatingEngineSchemaFactory.class).to(TestMatchRatingEngineSchemaFactory.class);
		bind(IQueryRatingEngineFactory.class).to(ScrumQueryRatingEngineFactory.class);
		bind(IUrlCacher.class).to(TestUrlCacher.class);
		bind(IStandingsFetcherFactory.class).to(StandingsFetcherFactory.class);
		bind(IRatingQueryFactory.class).to(TestRatingQueryFactory.class);
		bind(IPlayerRatingFactory.class).to(TestPlayerRatingFactory.class);
		bind(ISeriesConfigurationFactory.class).to(TestSeriesConfigurationFactory.class);
		bind(IUniversalRoundFactory.class).to(UniversalRoundFactory.class);
		bind(IBlurbFactory.class).to(TestBlurbFactory.class);
		bind(IDigestEmailFactory.class).to(TestDigestEmailFactory.class);
	}
	
}

