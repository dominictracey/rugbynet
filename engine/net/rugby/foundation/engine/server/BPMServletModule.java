package net.rugby.foundation.engine.server;

import com.google.inject.servlet.ServletModule;

public class BPMServletModule extends ServletModule {
	
	@Override protected void configureServlets() {
//		serve("/game1/orchestration/*").with(net.rugby.foundation.game1.server.OrchestrationServlet.class);
//		serve("/game1/workflow/").with(net.rugby.foundation.game1.server.WorkflowServlet.class);
//		serve("/game1/service").with(Game1ServiceImpl.class);
		serve("/engine/orchestration/*").with(net.rugby.foundation.engine.server.OrchestrationServlet.class);
		serve("/engine/workflow/*").with(net.rugby.foundation.engine.server.WorkflowServlet.class);
		serve("/engine/cleanUp").with(CleanupServlet.class);
	}
}

