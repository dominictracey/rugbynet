package net.rugby.foundation.engine.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.server.orchestration.AdminOrchestrationTargets;
import net.rugby.foundation.admin.server.orchestration.IOrchestration;
import net.rugby.foundation.admin.server.orchestration.IOrchestrationFactory;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;

@Singleton
public class OrchestrationServlet extends HttpServlet {

	
	private static final long serialVersionUID = 1L;
	private IOrchestrationFactory of;
	private ICompetitionFactory cf;
	private IMatchGroupFactory mgf;
	private IRatingQueryFactory rqf;
	private ISeriesConfigurationFactory scf;

	@Inject
	public OrchestrationServlet(IOrchestrationFactory of, ICompetitionFactory cf, IMatchGroupFactory mgf, IRoundFactory rf, ITeamGroupFactory tf, 
			IRatingQueryFactory rqf, ISeriesConfigurationFactory scf) {
		this.of = of;
		this.cf = cf;
		this.mgf = mgf;
		this.rqf = rqf;
		this.scf = scf;
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

		resp.setContentType("text/plain");
        //resp.getWriter().println("-oOOo-------oOOo-");

		try  {
		
		// TODO this is a little icky
		String action = req.getParameter(AdminOrchestrationActions.MatchActions.getKey());
		String target = req.getParameter(AdminOrchestrationTargets.Targets.getKey());
		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,"Orchestration invoked with action " + action + " and target " + target + ".");
		if (action == null) {
			resp.getWriter().println("No action parameter.");
			return;
		} else if (target == null) {
			resp.getWriter().println("No target parameter.");
			return;
		}
		if (target.equals(AdminOrchestrationTargets.Targets.MATCH.toString())) {
			if (req.getParameter("id") == null) {
				resp.getWriter().println("No id parameter (for match).");
				return;
			}
	        IOrchestration<IMatchGroup> orch = of.get(mgf.get(Long.parseLong(req.getParameter("id"))), AdminOrchestrationActions.MatchActions.valueOf(AdminOrchestrationActions.MatchActions.class, action));
	        if (orch != null) {
	        	if (req.getParameter("extraKey") != null) {
		        	orch.setExtraKey(Long.parseLong(req.getParameter("extraKey")));
		        	orch.execute();
	        	}
	        }
		} else if (target.equals(AdminOrchestrationTargets.Targets.COMP.toString())) {
			if (req.getParameter("id") == null) {
				resp.getWriter().println("No id parameter (for comp).");
				return;
			}
	        IOrchestration<ICompetition> orch = of.get(cf.get(Long.parseLong(req.getParameter("id"))), AdminOrchestrationActions.CompActions.valueOf(AdminOrchestrationActions.CompActions.class, action));
	        if (orch != null) {
	        	orch.execute();
	        }
		}  else if (target.equals(AdminOrchestrationTargets.Targets.RATING.toString())) {
			if (req.getParameter("id") == null) {
				resp.getWriter().println("No id parameter (for RatingQuery).");
				return;
			}
	        IRatingQuery rq = null;
	        if (Long.parseLong(req.getParameter("id")) != 0) {
	        		rq = rqf.get(Long.parseLong(req.getParameter("id")));
	        }
	        IOrchestration<IRatingQuery> orch = of.get(rq, AdminOrchestrationActions.RatingActions.valueOf(AdminOrchestrationActions.RatingActions.class, action));
	        if (orch != null) {
	        	orch.execute();
	        }
		}  else if (target.equals(AdminOrchestrationTargets.Targets.SERIES.toString())) {
			if (req.getParameter("id") == null) {
				resp.getWriter().println("No id parameter (for RatingSeries).");
				return;
			}
	        ISeriesConfiguration sc = null;
	        if (Long.parseLong(req.getParameter("id")) != 0) {
	        	sc = scf.get(Long.parseLong(req.getParameter("id")));
	        }
	        IOrchestration<ISeriesConfiguration> orch = of.get(sc, AdminOrchestrationActions.RatingActions.valueOf(AdminOrchestrationActions.SeriesActions.class, action));
	        if (orch != null) {
	        	orch.execute();
	        }
		} 
		} catch (Exception ex)  {
			resp.getWriter().println("Exception in Orchestration Servlet: \n" + ex.getLocalizedMessage() + "\n" + ex.getStackTrace().toString());
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Exception in Orchestration Servlet", ex);
		}
    }
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
		doPost(req,resp);
	}
}
