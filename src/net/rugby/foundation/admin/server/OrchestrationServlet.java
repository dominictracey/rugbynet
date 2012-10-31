package net.rugby.foundation.admin.server;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.rugby.foundation.admin.server.orchestration.AdminOrchestrationTargets;
import net.rugby.foundation.admin.server.orchestration.IOrchestration;
import net.rugby.foundation.admin.server.orchestration.IOrchestrationFactory;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;

@Singleton
public class OrchestrationServlet extends HttpServlet {

	
	private static final long serialVersionUID = 1L;
	private IOrchestrationFactory of;
	private ICompetitionFactory cf;
	private IMatchGroupFactory mgf;

	@Inject
	public OrchestrationServlet(IOrchestrationFactory of, ICompetitionFactory cf, IMatchGroupFactory mgf, IRoundFactory rf, ITeamGroupFactory tf) {
		this.of = of;
		this.cf = cf;
		this.mgf = mgf;
		//mgf.setFactories(rf, tf);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

		resp.setContentType("text/plain");
        //resp.getWriter().println("-oOOo-------oOOo-");

		// TODO this is a little icky
		String action = req.getParameter(AdminOrchestrationActions.MatchActions.getKey());
		String target = req.getParameter(AdminOrchestrationTargets.Targets.getKey());
		if (target.equals(AdminOrchestrationTargets.Targets.MATCH.toString())) {
	        mgf.setId(Long.parseLong(req.getParameter("id")));  
	        IOrchestration<IMatchGroup> orch = of.get(mgf.getGame(), AdminOrchestrationActions.MatchActions.valueOf(AdminOrchestrationActions.MatchActions.class, action));
	        if (orch != null) {
	        	orch.setExtraKey(Long.parseLong(req.getParameter("extraKey")));
	        	orch.execute();
	        }
		} else if (target.equals(AdminOrchestrationTargets.Targets.COMP.toString())) {
	        cf.setId(Long.parseLong(req.getParameter("id")));
	        IOrchestration<ICompetition> orch = of.get(cf.getCompetition(), AdminOrchestrationActions.CompActions.valueOf(AdminOrchestrationActions.CompActions.class, action));
	        if (orch != null) {
	        	orch.execute();
	        }
		} 
    }
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
		doPost(req,resp);
	}
}
