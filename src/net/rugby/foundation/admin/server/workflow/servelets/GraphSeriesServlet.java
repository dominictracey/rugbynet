package net.rugby.foundation.admin.server.workflow.servelets;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rugby.foundation.admin.server.workflow.ratingseries.UpdateGraphRoundNodes;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.topten.server.factory.IRoundNodeFactory;

import org.joda.time.DateTime;

import com.google.appengine.tools.cloudstorage.RetriesExhaustedException;
import com.google.appengine.tools.pipeline.JobSetting;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Parameter:compId to create data for
 * Parameter:action as either [create|delete]
 */
@Singleton
public class GraphSeriesServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2415381790205617886L;
	private IRoundNodeFactory rnf;
	private ICompetitionFactory cf;


	@Inject
	public GraphSeriesServlet(ICompetitionFactory cf, IRoundNodeFactory rnf) {
		this.rnf = rnf;
		this.cf = cf;
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		try {
			DateTime timestamp = new DateTime();
			resp.setContentType("text/html");
			String sCompId = req.getParameter("compId");
			Long compId = Long.parseLong(sCompId);
			if (compId != null) {
				ICompetition c = cf.get(compId);
				resp.getWriter().println("<h3>Managing graph components for " + c.getLongName() + "</h3>");
				resp.getWriter().println("<code>" + timestamp.toString() + "</code><hr/>");

				PipelineService service = PipelineServiceFactory.newPipelineService();

				// start a pipeline
				String pipelineId = "";
				try {
					JobSetting backOffFactor = new JobSetting.BackoffFactor(1);
					JobSetting backOffSeconds = new JobSetting.BackoffSeconds(30); // give the backend a bit to come online
					JobSetting maxAttempts = new JobSetting.MaxAttempts(3); // If it doesn't go, don't keep messing about
					pipelineId = service.startNewPipeline(new UpdateGraphRoundNodes(), compId, req.getParameter("action"), "Update graph for " + c.getLongName(), backOffFactor, backOffSeconds, maxAttempts);
				} catch (RetriesExhaustedException ree) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Round processing failure - retry maximum reached", ree);
				}


				resp.setContentType("text/html");
				resp.getWriter().println("<p>Started processing (" + req.getParameter("action") + ") graph data for <a href=\"/_ah/pipeline/status?root=" + pipelineId +"\" target=\"_blank\">" + c.getLongName() + "</a></p>");
				
				if (req.getParameter("action").equals("create")) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Started creation of graphing data<br>");
				} else if (req.getParameter("action").equals("delete")) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Started deletion of graphing data.<br>");
				} else {
					resp.getWriter().println( "Unrecognized or missing action parameter<hr>");
					showUsage(resp);
				}
				

			} else {
				resp.getWriter().println("<p>must provide compId GET parameter</p><hr>");
				showUsage(resp);
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Bad news", ex);
			resp.getWriter().println( "FAILURE: " + ex.toString());
		}
	}

	private void showUsage(HttpServletResponse resp) throws IOException {
		resp.getWriter().println("Invalid usage.</br> try /admin/workflow/graph?compId=xxxxxx&action=[create|delete]");

	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doPost(req,resp);
	}
}
