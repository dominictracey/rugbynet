package net.rugby.foundation.engine.server;

import net.rugby.foundation.admin.server.workflow.servelets.CronWeekendFinalizeServlet;
import net.rugby.foundation.admin.server.workflow.servelets.CronWeekendInitServlet;
import net.rugby.foundation.admin.server.workflow.servelets.UtilRoundWorkflowServlet;
import net.rugby.foundation.admin.server.workflow.servelets.UtilWeekendCancelServlet;

import com.google.inject.servlet.ServletModule;

public class BPMServletModule extends ServletModule {
	
	@Override protected void configureServlets() {
		serve("/engine/orchestration/*").with(net.rugby.foundation.engine.server.OrchestrationServlet.class);
		serve("/engine/workflow/*").with(net.rugby.foundation.engine.server.WorkflowServlet.class);
		serve("/engine/cleanUp").with(CleanupServlet.class);
		
		serve("/engine/cron/weekend/process").with(CronWeekendInitServlet.class);
		serve("/engine/cron/weekend/finalize").with(CronWeekendFinalizeServlet.class);
		serve("/engine/admin/weekend/cancel").with(UtilWeekendCancelServlet.class);
		serve("/engine/admin/workflow/round").with(UtilRoundWorkflowServlet.class);

	}
}

