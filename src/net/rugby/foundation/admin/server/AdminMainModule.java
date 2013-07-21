package net.rugby.foundation.admin.server;

import net.rugby.foundation.admin.server.factory.IAdminTaskFactory;
import net.rugby.foundation.admin.server.factory.IForeignCompetitionFetcherFactory;
import net.rugby.foundation.admin.server.factory.IMatchRatingEngineFactory;
import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.server.factory.IPlayerMatchInfoFactory;
import net.rugby.foundation.admin.server.factory.IPlayerMatchStatsFetcherFactory;
import net.rugby.foundation.admin.server.factory.IResultFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.ScrumCompetitionFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.ScrumMatchRatingEngineFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.ScrumPlayerMatchStatsFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.ScrumResultFetcherFactory;
import net.rugby.foundation.admin.server.factory.ofy.OfyAdminTaskFactory;
import net.rugby.foundation.admin.server.factory.ofy.OfyMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.server.factory.ofy.OfyPlayerMatchInfoFactory;
import net.rugby.foundation.admin.server.factory.test.TestAdminTaskFactory;
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
import com.google.inject.AbstractModule;

public class AdminMainModule extends AbstractModule {
	@Override
	 protected void configure() {
		bind(IWorkflowConfigurationFactory.class).to(OfyWorkflowConfigurationFactory.class);
		bind(IOrchestrationConfigurationFactory.class).to(OfyOrchestrationConfigurationFactory.class);
		bind(IOrchestrationFactory.class).to(OrchestrationFactory.class);
		bind(ICoreRuleFactory.class).to(CoreRuleFactory.class);
		bind(IResultFetcherFactory.class).to(ScrumResultFetcherFactory.class);
		bind(IForeignCompetitionFetcherFactory.class).to(ScrumCompetitionFetcherFactory.class);
		bind(IWorkflowFactory.class).to(OfyWorkflowFactory.class);
		bind(IMatchRatingEngineFactory.class).to(ScrumMatchRatingEngineFactory.class);
		bind(IAdminTaskFactory.class).to(OfyAdminTaskFactory.class);
		bind(IPlayerMatchInfoFactory.class).to(OfyPlayerMatchInfoFactory.class);
		bind(IPlayerMatchStatsFetcherFactory.class).to(ScrumPlayerMatchStatsFetcherFactory.class);
		bind(IMatchRatingEngineSchemaFactory.class).to(OfyMatchRatingEngineSchemaFactory.class);
	}
	
}

