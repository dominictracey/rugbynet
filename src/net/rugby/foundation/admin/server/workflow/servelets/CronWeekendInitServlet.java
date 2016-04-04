package net.rugby.foundation.admin.server.workflow.servelets;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rugby.foundation.admin.server.workflow.weekend.RJ0ProcessRound;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.IRound;

import org.joda.time.DateTime;

import com.google.appengine.tools.cloudstorage.RetriesExhaustedException;
import com.google.appengine.tools.pipeline.JobSetting;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

// /cron/weekendInit
@Singleton
public class CronWeekendInitServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2415381790205617886L;
	private IConfigurationFactory ccf;
	private IRoundFactory rf;

	@Inject
	public CronWeekendInitServlet(IConfigurationFactory ccf, IRoundFactory rf) {
		this.ccf = ccf;
		this.rf = rf;
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		// Look at all the underway comps, find this weekend's round for them (assume it's Thursday during the day) and start a new ProcessRound pipeline
		ICoreConfiguration cc = ccf.get();
		resp.getWriter().println("<h3>Started processing for Universal Round " + cc.getCurrentUROrdinal() + "</h3>");
		DateTime timestamp = new DateTime();
		resp.getWriter().println("<code>" + timestamp.toString() + "</code><hr/>");
		for (Long cid: cc.getCompsUnderway()) {
			IRound r = rf.getForUR(cid, cc.getCurrentUROrdinal());
			
			if (r != null) {
				PipelineService service = PipelineServiceFactory.newPipelineService();
				// start a pipeline for each comp's round
				String pipelineId = "";
				try {
					JobSetting backOffFactor = new JobSetting.BackoffFactor(1);
					JobSetting backOffSeconds = new JobSetting.BackoffSeconds(30); // give the backend a bit to come online
					JobSetting maxAttempts = new JobSetting.MaxAttempts(3); // If it doesn't go, don't keep messing about
					pipelineId = service.startNewPipeline(new RJ0ProcessRound(), r.getId(), backOffFactor, backOffSeconds, maxAttempts);
				} catch (RetriesExhaustedException ree) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Round processing failure - retry maximum reached", ree);
				}
				r.setWeekendProcessingPipelineId(pipelineId);
				rf.put(r);
	
				resp.setContentType("text/html");
				resp.getWriter().println("<p>Started processing " + r.getName() + " for <a href=\"/_ah/pipeline/status?root=" + pipelineId +"\" target=\"_blank\">" + cc.getCompetitionMap().get(cid) + "</a></p>");
				
				//resp.sendRedirect();
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Initiated weekend processing for round " + r.getName() + " of compId " + cid + " on pipeline " + pipelineId);
			} else {
				resp.getWriter().println("<p>No current round for comp " + cc.getCompetitionMap().get(cid) + "</p>");
			}
		}
		
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doPost(req,resp);
	}
}
