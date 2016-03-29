package net.rugby.foundation.core.server;

import net.rugby.foundation.admin.server.RugbyAdminServiceImpl;
import net.rugby.foundation.admin.server.workflow.servelets.CronMidweekFinalizeServlet;
import net.rugby.foundation.admin.server.workflow.servelets.CronMidweekInitServlet;
import net.rugby.foundation.admin.server.workflow.servelets.CronWeekendFinalizeServlet;
import net.rugby.foundation.admin.server.workflow.servelets.CronWeekendInitServlet;
import net.rugby.foundation.admin.server.workflow.servelets.UtilRoundWorkflowServlet;
import net.rugby.foundation.admin.server.workflow.servelets.UtilWeekendCancelServlet;
import net.rugby.foundation.core.server.mail.UnsubscribeServlet;
import net.rugby.foundation.engine.server.CleanupServlet;
import net.rugby.foundation.topten.server.HomePage;
import net.rugby.foundation.topten.server.MetaTagGenerator;
import net.rugby.foundation.topten.server.SeriesPage;
import net.rugby.foundation.topten.server.SiteMap;
import net.rugby.foundation.topten.server.TeamPage;
import net.rugby.foundation.topten.server.TopTenServiceImpl;

import com.google.inject.servlet.ServletModule;

public class BPMServletModule extends ServletModule {
	
	@Override protected void configureServlets() {
//		serve("/game1/orchestration/*").with(net.rugby.foundation.game1.server.OrchestrationServlet.class);
//		serve("/game1/workflow/").with(net.rugby.foundation.game1.server.WorkflowServlet.class);
//		serve("/game1/service").with(Game1ServiceImpl.class);
		
		serve("/core/CoreService").with(CoreServiceImpl.class);
		serve("/topten/TopTenService").with(TopTenServiceImpl.class);
		serve("/login/*").with(LoginServlet.class);
		serve("/_ah/login/*").with(LoginServlet.class);
		serve("/util/facebook/channel.html").with(FacebookChannelServlet.class);
		//serve("/_ah/start").with(BackEndStartupServlet.class);
		serve("/fb/core/CoreService").with(CoreServiceImpl.class);
		serve("/fb/topten/TopTenService").with(TopTenServiceImpl.class);
		serve("/fb/topten.html").with(MetaTagGenerator.class);
		serve("/_ah/login_required").with(LoginRequiredServlet.class);
		serve("/s/*").with(SeriesPage.class);
		serve("/teams/*").with(TeamPage.class);
		serve("/sitemap/*").with(SiteMap.class);
		serve("/session/").with(SessionServlet.class);
		serve("/").with(HomePage.class);
		serve("/email/unsubscribe").with(UnsubscribeServlet.class);
		
		
		// admin only
		serve("/cron/weekend/process").with(CronWeekendInitServlet.class);
		serve("/cron/weekend/finalize").with(CronWeekendFinalizeServlet.class);
		serve("/cron/midweek/process").with(CronMidweekInitServlet.class);
		serve("/cron/midweek/finalize").with(CronMidweekFinalizeServlet.class);
		serve("/admin/weekend/cancel").with(UtilWeekendCancelServlet.class);
		serve("/admin/workflow/round").with(UtilRoundWorkflowServlet.class);
		serve("/admin/orchestration/*").with(net.rugby.foundation.engine.server.OrchestrationServlet.class);
		//serve("/admin/workflow/*").with(net.rugby.foundation.engine.server.WorkflowServlet.class);
		serve("/admin/cleanUp").with(CleanupServlet.class);
		serve("/admin/rugbyAdminService").with(RugbyAdminServiceImpl.class);
	}
}

