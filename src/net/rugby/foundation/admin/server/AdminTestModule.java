package net.rugby.foundation.admin.server;

import net.rugby.foundation.admin.server.factory.IAdminTaskFactory;
import net.rugby.foundation.admin.server.factory.IForeignCompetitionFetcherFactory;
import net.rugby.foundation.admin.server.factory.IMatchRatingEngineFactory;
import net.rugby.foundation.admin.server.factory.IPlayerMatchInfoFactory;
import net.rugby.foundation.admin.server.factory.IResultFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.ScrumCompetitionFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.ScrumMatchRatingEngineFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.ScrumResultFetcherFactory;
import net.rugby.foundation.admin.server.factory.test.TestAdminTaskFactory;
import net.rugby.foundation.admin.server.factory.test.TestPlayerMatchInfoFactory;
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
		bind(IMatchRatingEngineFactory.class).to(ScrumMatchRatingEngineFactory.class);
		bind(IAdminTaskFactory.class).to(TestAdminTaskFactory.class);
		bind(IPlayerMatchInfoFactory.class).to(TestPlayerMatchInfoFactory.class);
	}
	
}

