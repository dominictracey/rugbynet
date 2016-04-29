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
import com.google.appengine.tools.pipeline.NoSuchObjectException;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Parameter:roundId to activate processing pipeline on
 * Parameter:action as either [process|cancel]
 */
@Singleton
public class UtilRoundWorkflowServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2415381790205617886L;
	private IConfigurationFactory ccf;
	private IRoundFactory rf;

	@Inject
	public UtilRoundWorkflowServlet(IConfigurationFactory ccf, IRoundFactory rf) {
		this.ccf = ccf;
		this.rf = rf;
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		// Look at all the underway comps, find this weekend's round for them (assume it's Thursday during the day) and start a new ProcessRound pipeline
		ICoreConfiguration cc = ccf.get();
		DateTime timestamp = new DateTime();
		resp.setContentType("text/html");
		String sRoundId = req.getParameter("roundId");
		Long roundId = Long.parseLong(sRoundId);
		if (roundId != null) {
			IRound r = rf.get(roundId);

			if (r != null) {
				resp.getWriter().println("<h3>Processing for " + r.getName() + " of " + cc.getCompetitionMap().get(r.getCompId()) + "</h3>");
				resp.getWriter().println("<code>" + timestamp.toString() + "</code><hr/>");

				PipelineService service = PipelineServiceFactory.newPipelineService();
				String pipelineId = "";
				// start a pipeline for each comp's round
				try {				
					if (req.getParameter("action").equals("process")) {


						JobSetting backOffFactor = new JobSetting.BackoffFactor(1);
						JobSetting backOffSeconds = new JobSetting.BackoffSeconds(30*60); // retry every 30 minutes
						JobSetting maxAttempts = new JobSetting.MaxAttempts(200); // about 4 days
						pipelineId = service.startNewPipeline(new RJ0ProcessRound(), r.getId(), backOffFactor, backOffSeconds, maxAttempts);

						r.setWeekendProcessingPipelineId(pipelineId);
						rf.put(r);

						resp.setContentType("text/html");
						resp.getWriter().println("<p>Started processing " + r.getName() + " for <a href=\"/_ah/pipeline/status?root=" + pipelineId +"\" target=\"_blank\">" + cc.getCompetitionMap().get(r.getCompId()) + "</a></p>");

						//resp.sendRedirect();
						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Initiated weekend processing for round " + r.getName() +  " on pipeline " + pipelineId);
					} else if (req.getParameter("action").equals("cancel")) {

						// check the pipeline for each comp's round
						pipelineId = r.getWeekendProcessingPipelineId();
						if (pipelineId != null && !pipelineId.isEmpty()) {
							// destroy the pipeline
							service.deletePipelineRecords(pipelineId,true,false);					
						}

						r.setWeekendProcessingPipelineId(null);
						rf.put(r);

						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Successful deletion of pipelineId: " + pipelineId);
						resp.getWriter().println("Successful deletion of pipelineId: " + pipelineId);
					} else {
						resp.getWriter().println( "Unrecognized or missing action parameter<hr>");
						showUsage(resp);
					}
				} catch (RetriesExhaustedException ree) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Round processing failure - retry maximum reached", ree);
					resp.getWriter().println( "Round processing failure - retry maximum reached<hr>");
				} catch (NoSuchObjectException nsox) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Round processing failure - no pipeline found to delete", nsox);
					resp.getWriter().println( "Round processing failure - no pipeline found to delete<hr>");
				}
			} else {
				resp.getWriter().println("<p>No round for id " + roundId + "</p><hr>");
			}
		} else {
			showUsage(resp);
		}

	}

	private void showUsage(HttpServletResponse resp) throws IOException {
		resp.getWriter().println("Invalid usage.</br> try /admin/workflow/round?roundId=xxxxxx&action=[process|cancel]");

	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doPost(req,resp);
	}
}
