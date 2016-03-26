package net.rugby.foundation.admin.server.workflow.servelets;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.IRound;

import com.google.appengine.tools.pipeline.NoSuchObjectException;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

// /cron/weekendInit
@Singleton
public class UtilWeekendCancelServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2415381790205617886L;
	private IConfigurationFactory ccf;
	private IRoundFactory rf;

	@Inject
	public UtilWeekendCancelServlet(IConfigurationFactory ccf, IRoundFactory rf) {
		this.ccf = ccf;
		this.rf = rf;
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String pipelineId = "";
		try {		
			resp.setContentType("text/html");
			// Look at all the underway comps, find this weekend's round for them (assume it's Thursday during the day) and start a new ProcessRound pipeline
			ICoreConfiguration cc = ccf.get();
			for (Long cid: cc.getCompsUnderway()) {
				IRound r = rf.getForUR(cid, cc.getCurrentUROrdinal());

				if (r != null) {
					PipelineService service = PipelineServiceFactory.newPipelineService();

					// check the pipeline for each comp's round
					pipelineId = r.getWeekendProcessingPipelineId();
					if (pipelineId != null && !pipelineId.isEmpty()) {
						// destroy the pipeline
						service.deletePipelineRecords(pipelineId,true,false);					
					}

					r.setWeekendProcessingPipelineId(null);
					rf.put(r);

					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Successful deletion of pipelineId: " + pipelineId);
				}
			}
			resp.getWriter().println("Successful deletion of pipelineId: " + pipelineId);
		} catch (NoSuchObjectException nsox) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Attempt to delete a non-existent weekendProcessing pipelineId: " + pipelineId);
		}


	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doPost(req,resp);
	}
}
