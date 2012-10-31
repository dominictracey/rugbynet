package net.rugby.foundation.game1.server;

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
import net.rugby.foundation.core.server.factory.IClubhouseFactory;
import net.rugby.foundation.game1.server.BPM.Game1OrchestrationActions;
import net.rugby.foundation.game1.server.BPM.Game1OrchestrationTargets;
import net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory;
import net.rugby.foundation.game1.server.factory.ILeagueFactory;
import net.rugby.foundation.game1.server.factory.IMatchEntryFactory;
import net.rugby.foundation.game1.server.factory.IRoundEntryFactory;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.ILeague;
import net.rugby.foundation.game1.shared.IMatchEntry;
import net.rugby.foundation.game1.shared.IRoundEntry;
import net.rugby.foundation.model.shared.IClubhouse;

@Singleton
public class OrchestrationServlet extends HttpServlet {

	
	private static final long serialVersionUID = 1L;
	private IOrchestrationFactory of;
	private IClubhouseLeagueMapFactory chlmf;
	private IClubhouseFactory chf;
	private ILeagueFactory lf;
	private IRoundEntryFactory ref;
	private IMatchEntryFactory mef;

	@Inject
	public OrchestrationServlet(IClubhouseLeagueMapFactory chlmf, IClubhouseFactory chf, IOrchestrationFactory of, ILeagueFactory lf, IMatchEntryFactory mef,
			IRoundEntryFactory ref) {
		this.of = of;
		this.chlmf = chlmf;
		this.chf = chf;
		this.lf = lf;
		this.ref = ref;
		this.mef = mef;
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

		resp.setContentType("text/plain");
        //resp.getWriter().println("-oOOo-------oOOo-");

		String action = req.getParameter(AdminOrchestrationActions.MatchActions.getKey());
		String target = req.getParameter(AdminOrchestrationTargets.Targets.getKey());
		if (target.equals(Game1OrchestrationTargets.Targets.CLUBHOUSE.toString())) {
	        chf.setId(Long.parseLong(req.getParameter("id")));
	        IOrchestration<IClubhouse> orch = of.get(chf.get(), Game1OrchestrationActions.CompActions.valueOf(Game1OrchestrationActions.ClubhouseActions.class, action));
	        if (orch != null) {
	        	orch.setExtraKey(Long.parseLong(req.getParameter("extraKey")));
	        	orch.execute();
	        }
		}  else if (target.equals(Game1OrchestrationTargets.Targets.CLM.toString())) {
	        chlmf.setId(Long.parseLong(req.getParameter("id")));
	        IOrchestration<IClubhouseLeagueMap> orch = of.get(chlmf.get(), Game1OrchestrationActions.CompActions.valueOf(Game1OrchestrationActions.ClubhouseLeagueMapActions.class, action));
	        if (orch != null) {
	        	orch.setExtraKey(Long.parseLong(req.getParameter("extraKey")));
	        	orch.execute();
	        }
		}   else if (target.equals(Game1OrchestrationTargets.Targets.LEAGUE.toString())) {
	        lf.setId(Long.parseLong(req.getParameter("id")));
	        IOrchestration<ILeague> orch = of.get(lf.get(), Game1OrchestrationActions.CompActions.valueOf(Game1OrchestrationActions.LeagueActions.class, action));
	        if (orch != null) {
	        	orch.setExtraKey(Long.parseLong(req.getParameter("extraKey")));
	        	orch.execute();
	        }
		}   else if (target.equals(Game1OrchestrationTargets.Targets.MATCHENTRY.toString())) {
	        mef.setId(Long.parseLong(req.getParameter("id")));
	        IOrchestration<IMatchEntry> orch = of.get(mef.getMatchEntry(), Game1OrchestrationActions.CompActions.valueOf(Game1OrchestrationActions.MatchEntryActions.class, action));
	        if (orch != null) {
	        	orch.setExtraKey(Long.parseLong(req.getParameter("extraKey")));
	        	orch.execute();
	        }
		}   else if (target.equals(Game1OrchestrationTargets.Targets.ROUNDENTRY.toString())) {
	        ref.setId(Long.parseLong(req.getParameter("id")));
	        IOrchestration<IRoundEntry> orch = of.get(ref.getRoundEntry(), Game1OrchestrationActions.CompActions.valueOf(Game1OrchestrationActions.RoundEntryActions.class, action));
	        if (orch != null) {
	        	orch.setExtraKey(Long.parseLong(req.getParameter("extraKey")));
	        	orch.execute();
	        }
		}
//		  else if (target.equals(AdminOrchestrationTargets.Targets.APPUSER.getValue())) {
//	        cf.setId(Long.parseLong(req.getParameter("id")));
//	        IOrchestration<ICompetition> orch = of.get(cf.getCompetition(), Game1OrchestrationActions.CompActions.valueOf(Game1OrchestrationActions.CompActions.class, action));
//	        orch.execute();
//		} 
    }
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
		doPost(req,resp);
	}
}
