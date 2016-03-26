package net.rugby.foundation.admin.server.workflow.servelets;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rugby.foundation.admin.server.AdminEmailer;
import net.rugby.foundation.admin.server.workflow.fetchstats.FetchMatchStats;
import net.rugby.foundation.admin.server.workflow.weekend.results.ProcessRoundResult;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.IRound;

import com.google.appengine.tools.cloudstorage.RetriesExhaustedException;
import com.google.appengine.tools.pipeline.JobInfo;
import com.google.appengine.tools.pipeline.JobSetting;
import com.google.appengine.tools.pipeline.NoSuchObjectException;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

// /cron/weekendInit
@Singleton
public class CronWeekendFinalizeServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2415381790205617886L;
	private IConfigurationFactory ccf;
	private IRoundFactory rf;
	private ICompetitionFactory cf;

	@Inject
	public CronWeekendFinalizeServlet(IConfigurationFactory ccf, IRoundFactory rf, ICompetitionFactory cf) {
		this.ccf = ccf;
		this.rf = rf;
		this.cf = cf;
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String pipelineId = "";
		resp.setContentType("text/html");
		try {		
			// Look at all the underway comps, find this weekend's round for them (assume it's Thursday during the day) and start a new ProcessRound pipeline
			ICoreConfiguration cc = ccf.get();
			for (Long cid: cc.getCompsUnderway()) {
				IRound r = rf.getForUR(cid, cc.getCurrentUROrdinal());

				if (r != null) {
					PipelineService service = PipelineServiceFactory.newPipelineService();

					// check the pipeline for each comp's round
					pipelineId = r.getWeekendProcessingPipelineId();
					if (pipelineId != null && !pipelineId.isEmpty()) {
						JobInfo jobInfo = service.getJobInfo(pipelineId);
						JobInfo.State state = jobInfo.getJobState();
						// check if it is complete
						if (JobInfo.State.COMPLETED_SUCCESSFULLY == state){
							ProcessRoundResult result = (ProcessRoundResult)jobInfo.getOutput();


							//email the results to the admins
							AdminEmailer emailer = new AdminEmailer();

							ICompetition c = cf.get(r.getCompId());

							emailer.setSubject("Weekend processing for " + r.getName() + " of " + c.getLongName() + " report");
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

					r.setWeekendProcessingPipelineId(null);
					rf.put(r);

					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Successful shutdown of pipelineId: " + pipelineId);
				}
			}
		} catch (NoSuchObjectException nsox) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Attempt to delete a non-existent weekendProcessing pipelineId: " + pipelineId);
		}


	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doPost(req,resp);
	}
}
