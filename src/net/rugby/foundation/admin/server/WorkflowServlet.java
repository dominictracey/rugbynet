package net.rugby.foundation.admin.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rugby.foundation.admin.server.workflow.AdminWorkflow;
import net.rugby.foundation.admin.server.workflow.IWorkflow;
import net.rugby.foundation.admin.server.workflow.IWorkflowFactory;
import net.rugby.foundation.admin.shared.CompetitionWorkflow;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 
 * @author Dominic Tracey
 *
 * <p>The WorkflowServlet implements the abstract top-most business logic of the system. It deals with
 * the fundamental entities such as competitions, matches and games and is architecturally constrained by
 * several essential mechanisms:<ol>
 * 		<li>This servlet is invoked through the cron mechanism on an hourly basis.</li>
 * 		<li>This servlet makes no updates to the datastore. </li>
 * 		<li>This servlet effects change by invoking orchestrations through the queue mechanism.</li>
 * </ol>
 * </p>
 */

@Singleton
public class WorkflowServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private IWorkflowFactory wff;

	@Inject
	public WorkflowServlet(IWorkflowFactory wff) {
		this.wff = wff;
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
	  
		ArrayList<String> log = new ArrayList<String>();


		
        resp.setContentType("text/plain");
        

		String targetType = req.getParameter(AdminWorkflow.Targets.getKey());
		String targetKey = req.getParameter(AdminWorkflow.Key);
		String secondaryKey = req.getParameter(AdminWorkflow.SecondaryKey);
//		if (targetType.equals("MatchResultFetchWF")) {
//			
//			PipelineService service = PipelineServiceFactory.newPipelineService();
//			String pipelineId = service.startNewPipeline(new MatchResultFetchWF(), 11, 5, 7);
//
//			// Later, check on the status and get the final output
//			JobInfo jobInfo;
//			try {
//				jobInfo = service.getJobInfo(pipelineId);
//				JobInfo.State state = jobInfo.getJobState();
//				if (JobInfo.State.COMPLETED_SUCCESSFULLY == state){
//				  System.out.println("The output is " + jobInfo.getOutput());
//				} else {
//					System.out.println("The job is in state " + jobInfo.getJobState().toString());
//				}
//			} catch (NoSuchObjectException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		if (targetType.equals(AdminWorkflow.Targets.MATCH.toString())) {
//	        wff.setId(Long.parseLong(req.getParameter("id")));  
//	        IOrchestration<IMatchGroup> orch = of.get(mgf.getGame(), AdminOrchestrationActions.MatchActions.valueOf(AdminOrchestrationActions.MatchActions.class, action));
//	        if (orch != null) {
//	        	orch.setExtraKey(Long.parseLong(req.getParameter("extraKey")));
//	        	orch.execute();
//	        }
//		} else 
		if (targetType.equals(AdminWorkflow.Targets.COMP.toString())) {
	        String event = req.getParameter(CompetitionWorkflow.Events.key);
	        wff.setId(Long.parseLong(targetKey));
	        IWorkflow wf = wff.get();
	        if (wf != null) {
	        	wf.process(event, targetKey, secondaryKey);
	        }
		} 
		
        resp.getWriter().println("Workflow processed...");
        for (String s : log) {
        	resp.getWriter().println(s);
        }
    }

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
		doGet(req, resp);
	}

}
