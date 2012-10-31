package net.rugby.foundation.game1.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rugby.foundation.admin.server.workflow.AdminWorkflow;
import net.rugby.foundation.admin.server.workflow.IWorkflow;
import net.rugby.foundation.admin.server.workflow.IWorkflowConfigurationFactory;
import net.rugby.foundation.admin.shared.CompetitionWorkflow;
import net.rugby.foundation.core.server.factory.IClubhouseFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.game1.server.BPM.WorkflowCompetition;
import net.rugby.foundation.game1.server.BPM.ICoreRuleFactory;
import net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory;
import net.rugby.foundation.game1.server.factory.IEntryFactory;
import net.rugby.foundation.game1.server.factory.ILeagueFactory;
import net.rugby.foundation.game1.server.factory.IMatchEntryFactory;
import net.rugby.foundation.game1.server.factory.IRoundEntryFactory;

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
	private IWorkflow wf;
	private ICompetitionFactory cf;
	private ICoreRuleFactory crf;
	private IClubhouseLeagueMapFactory chlmf;
	private ILeagueFactory lf;
	private IClubhouseFactory chf;
	private IMatchEntryFactory mef;
	private IRoundEntryFactory ref;
	private IEntryFactory ef;

	@Inject
	public WorkflowServlet(ICompetitionFactory cf, ICoreRuleFactory crf, 
			IClubhouseLeagueMapFactory chlmf, ILeagueFactory lf, IClubhouseFactory chf,
			IMatchEntryFactory mef, IRoundEntryFactory ref, IEntryFactory ef) {
		this.cf = cf;
		this.crf = crf;
		this.chlmf = chlmf;
		this.lf = lf;
		this.chf = chf;
		this.mef = mef;
		this.ref = ref;
		this.ef = ef;
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

		String target = req.getParameter(AdminWorkflow.Targets.getKey());
		String targetKey = req.getParameter(AdminWorkflow.Key);
		String secondaryKey = req.getParameter(AdminWorkflow.SecondaryKey);
		
		wf = new WorkflowCompetition(chlmf, cf, (ICoreRuleFactory) crf, lf, chf, mef, ref, ef);  //@REX something screwy w/ 3rd param
		
		ArrayList<String> log = new ArrayList<String>();
		wf.setLog(log);
		wf.process(target, targetKey, secondaryKey);
		
//		if (target.equals(AdminWorkflow.Targets.COMP.toString())) {
//	        String event = req.getParameter(CompetitionWorkflow.Events.key);
//	        wff.setId(Long.parseLong(req.getParameter("id")));
//	        IWorkflow wf = wff.get();
//	        if (wf != null) {
//	        	wf.process(event, targetKey, secondaryKey);
//	        }
//		} 
//		
        resp.setContentType("text/plain");
        resp.getWriter().println("Workflow processed");
        for (String s : log) {
        	resp.getWriter().println(s);
        }
    }

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
		doGet(req, resp);
	}

}
