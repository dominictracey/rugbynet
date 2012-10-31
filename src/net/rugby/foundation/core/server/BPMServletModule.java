package net.rugby.foundation.core.server;

import net.rugby.foundation.admin.server.RugbyAdminServiceImpl;
import net.rugby.foundation.game1.server.Game1ServiceImpl;

import com.google.inject.servlet.ServletModule;

public class BPMServletModule extends ServletModule {
	
	@Override protected void configureServlets() {
		serve("/game1/orchestration/*").with(net.rugby.foundation.game1.server.OrchestrationServlet.class);
		serve("/game1/workflow/").with(net.rugby.foundation.game1.server.WorkflowServlet.class);
		serve("/game1/service").with(Game1ServiceImpl.class);
		serve("/admin/orchestration/*").with(net.rugby.foundation.admin.server.OrchestrationServlet.class);
		serve("/admin/workflow/*").with(net.rugby.foundation.admin.server.WorkflowServlet.class);
		serve("/admin/rugbyAdminService").with(RugbyAdminServiceImpl.class);
		serve("/core/CoreService").with(CoreServiceImpl.class);
		serve("/login/*").with(LoginServlet.class);
		serve("/util/facebook/channel.html").with(FacebookChannelServlet.class);
		serve("/_ah/start").with(BackEndStartupServlet.class);
	}

}

