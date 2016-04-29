package net.rugby.foundation.admin.server.workflow.servelets;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rugby.foundation.admin.server.AdminEmailer;
import net.rugby.foundation.admin.server.workflow.IWorkflowConfigurationFactory;
import net.rugby.foundation.admin.server.workflow.midweek.results.VS0MidweekResult;
import net.rugby.foundation.admin.shared.IWorkflowConfiguration;

import com.google.appengine.tools.pipeline.JobInfo;
import com.google.appengine.tools.pipeline.NoSuchObjectException;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

// /cron/weekendInit
@Singleton
public class CronMidweekFinalizeServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2415381790205617886L;

	private IWorkflowConfigurationFactory wfcf;

	@Inject
	public CronMidweekFinalizeServlet(IWorkflowConfigurationFactory wfcf) {
		this.wfcf = wfcf;
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String pipelineId = "";
		resp.setContentType("text/html");
		try {		
			// Look at all the underway comps, find this weekend's round for them (assume it's Thursday during the day) and start a new ProcessRound pipeline
			IWorkflowConfiguration wfc = wfcf.get();

			PipelineService service = PipelineServiceFactory.newPipelineService();

			// check the pipeline for each comp's round
			pipelineId = wfc.getMidweekPipelineId();
			if (pipelineId != null && !pipelineId.isEmpty()) {
				JobInfo jobInfo = service.getJobInfo(pipelineId);
				JobInfo.State state = jobInfo.getJobState();
				// check if it is complete
				if (JobInfo.State.COMPLETED_SUCCESSFULLY == state){
					VS0MidweekResult result = (VS0MidweekResult)jobInfo.getOutput();


					//email the results to the admins
					AdminEmailer emailer = new AdminEmailer();

					emailer.setSubject("Midweek processing report");
					emailer.setMessage(result.log.toString());

					emailer.send();

					System.out.println(result.log.toString());

					for (String s: result.log) {
						resp.getWriter().println(s + "<br/>");
					}

					// destroy the pipeline
					service.deletePipelineRecords(pipelineId,true,false);

				}
			}

			wfc.setMidweekPipelineId(null);
			wfcf.put(wfc);

			// Now we can run our regional lists...
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Successful shutdown of pipelineId: " + pipelineId);

		} catch (NoSuchObjectException nsox) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Attempt to delete a non-existent midweekProcessing pipelineId: " + pipelineId);
		}


	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doPost(req,resp);
	}
}
