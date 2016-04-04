package net.rugby.foundation.admin.server.workflow.servelets;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rugby.foundation.admin.server.workflow.IWorkflowConfigurationFactory;
import net.rugby.foundation.admin.server.workflow.midweek.VJ0ProcessMidweek;
import net.rugby.foundation.admin.shared.IWorkflowConfiguration;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;

import org.joda.time.DateTime;

import com.google.appengine.tools.cloudstorage.RetriesExhaustedException;
import com.google.appengine.tools.pipeline.JobSetting;
import com.google.appengine.tools.pipeline.NoSuchObjectException;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

// /cron/midweek
@Singleton
public class CronMidweekInitServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2415381790205617886L;

	private IUniversalRoundFactory urf;

	private IWorkflowConfigurationFactory wfcf;

	@Inject
	public CronMidweekInitServlet(IUniversalRoundFactory urf, IWorkflowConfigurationFactory wfcf) {
		this.urf = urf;
		this.wfcf = wfcf;
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		resp.getWriter().println("<h3>Started midweek processing for Universal Round " + urf.getCurrent().shortDesc + " (" + urf.getCurrent().ordinal + ")</h3>");
		DateTime timestamp = new DateTime();
		resp.getWriter().println("<code>" + timestamp.toString() + "</code><hr/>");

		PipelineService service = PipelineServiceFactory.newPipelineService();

		String pipelineId = wfcf.get().getMidweekPipelineId();
		

		
		try {
			
			// flush if necessary
			if (pipelineId != null && !pipelineId.isEmpty()) {
				// destroy the pipeline
				service.deletePipelineRecords(pipelineId,true,false);
			}
			
			JobSetting backOffFactor = new JobSetting.BackoffFactor(1);
			JobSetting backOffSeconds = new JobSetting.BackoffSeconds(30); // give the backend a bit to come online
			JobSetting maxAttempts = new JobSetting.MaxAttempts(3); // If it doesn't go, don't keep messing about
			pipelineId = service.startNewPipeline(new VJ0ProcessMidweek(), urf.getCurrent(), backOffFactor, backOffSeconds, maxAttempts);
			
			// save it off so we can check the status later.
			IWorkflowConfiguration wfc = wfcf.get();
			wfc.setMidweekPipelineId(pipelineId);
			wfcf.put(wfc);
			
		} catch (RetriesExhaustedException ree) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Round processing failure - retry maximum reached", ree);
		} catch (IllegalStateException e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Round processing failure - bad state", e);
		} catch (NoSuchObjectException e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Round processing failure - no such object", e);
		}


		resp.setContentType("text/html");
		resp.getWriter().println("<p>Started midweek processing for <a href=\"/_ah/pipeline/status?root=" + pipelineId +"\" target=\"_blank\">" + urf.getCurrent().shortDesc + "</a></p>");		
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doPost(req,resp);
	}
}
