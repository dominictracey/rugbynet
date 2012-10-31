/**
 * 
 */
package net.rugby.foundation.game1.server.BPM;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Builder;
import net.rugby.foundation.admin.server.orchestration.AdminOrchestrationTargets;
import net.rugby.foundation.admin.server.rules.IRule;
import net.rugby.foundation.admin.server.workflow.IWorkflow;
import net.rugby.foundation.admin.server.workflow.IWorkflowConfigurationFactory;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions;
import net.rugby.foundation.admin.shared.IWorkflowConfiguration;
import net.rugby.foundation.core.server.factory.IClubhouseFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.game1.server.BPM.Game1OrchestrationActions.ClubhouseActions;
import net.rugby.foundation.game1.server.BPM.Game1OrchestrationActions.ClubhouseLeagueMapActions;
import net.rugby.foundation.game1.server.BPM.Game1OrchestrationActions.LeagueActions;
import net.rugby.foundation.game1.server.BPM.Game1OrchestrationActions.MatchEntryActions;
import net.rugby.foundation.game1.server.BPM.Game1OrchestrationActions.RoundEntryActions;
import net.rugby.foundation.game1.server.BPM.Game1OrchestrationTargets.Targets;
import net.rugby.foundation.game1.server.BPM.ICoreRuleFactory.ClubhouseLeagueMapRule;
import net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory;
import net.rugby.foundation.game1.server.factory.IEntryFactory;
import net.rugby.foundation.game1.server.factory.ILeagueFactory;
import net.rugby.foundation.game1.server.factory.IMatchEntryFactory;
import net.rugby.foundation.game1.server.factory.IRoundEntryFactory;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.ILeague;
import net.rugby.foundation.game1.shared.IMatchEntry;
import net.rugby.foundation.game1.shared.IRoundEntry;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.ICompetition;

/**
 * @author home
 *
 */
public class WorkflowCompetition implements IWorkflow {

	private List<String> log = null;
	private ICompetitionFactory cf;
	private IWorkflowConfigurationFactory wfcf;
	private ICoreRuleFactory crf;
	private IClubhouseLeagueMapFactory chlmf;
	private ILeagueFactory lf;
	private IClubhouseFactory chf;
	private IMatchEntryFactory mef;
	private IRoundEntryFactory ref;
	private IEntryFactory ef;
	
	public WorkflowCompetition(IClubhouseLeagueMapFactory chlmf, ICompetitionFactory cf, ICoreRuleFactory crf, ILeagueFactory lf, IClubhouseFactory chf, IMatchEntryFactory mef, IRoundEntryFactory ref, IEntryFactory ef) {		
		this.cf = cf;
		this.wfcf = wfcf;
		this.crf = crf;
		this.chlmf = chlmf;
		this.lf = lf;
		this.chf = chf;
		this.mef = mef;
		this.ref = ref;
		this.ef = ef;
	}
	
//	/* (non-Javadoc)
//	 * @see net.rugby.foundation.admin.server.workflow.IWorkflow#process()
//	 */
//	@Override
//	public void process() {
//		IWorkflowConfiguration wfc = wfcf.get();
//		process(wfc);
//	}

	/**
	 * 
	 * @param wfc There is only one instance of the {@link <WorkflowConfiguration> [WorkflowConfiguration]}. It contains a list of ids of underway competitions.
	 * <br/>
	 */
	private void process(IWorkflowConfiguration wfc) {

		boolean sanityCheck = true;
		
		// keep track of which leagues we've deleted so we don't try to redelete them.
		Set<Long> leagueIdsDeleted = new HashSet<Long>();
		
		for (Long cid : wfc.getUnderwayCompetitions()) {
			
			cf.setId(cid);
			ICompetition c = cf.getCompetition();
			
			// Check that the clubhouses are all ok (that they each have a valid CLM)
			List<IClubhouse> clubhouses = chf.getAll();
			for (IClubhouse ch : clubhouses) {
				IRule<IClubhouse> clubhouseChecker = new RuleCheckClubhouse(ch, c, chlmf, lf);
				if (!clubhouseChecker.test()) {
					SpawnClubhouseOrchestration(Game1OrchestrationActions.ClubhouseActions.FIX, Game1OrchestrationTargets.Targets.CLUBHOUSE, ch, c);
					sanityCheck = false;
				}
			}
			
			// Check that the CLMs have good leagues and comp references
			chlmf.setCompetitionId(cid);
			List<IClubhouseLeagueMap> clms = chlmf.getList();
			for (IClubhouseLeagueMap clm : clms) {
				IRule<IClubhouseLeagueMap> clmChecker = new RuleCheckClm(clm, c, lf, chf);
				if (!clmChecker.test()) {
					SpawnClmOrchestration(Game1OrchestrationActions.ClubhouseLeagueMapActions.FIX, Game1OrchestrationTargets.Targets.CLM, clm, c);
					sanityCheck = false;
				}
			}
			
			// check the leagues 
			Set<ILeague> leagues = lf.getAll();
			for (ILeague league: leagues) {
				IRule<ILeague> leagueChecker = new RuleCheckLeague(league, c, chlmf);
				if (!leagueChecker.test()) {
					if (leagueIdsDeleted.add(league.getId())) {
							SpawnLeagueOrchestration(Game1OrchestrationActions.LeagueActions.FIX, Game1OrchestrationTargets.Targets.LEAGUE, league, c);
							sanityCheck = false;
					}
				}
			}
			
			// 6. Every matchEntry should be in one and only one RoundEntry
			Set<IMatchEntry> mes = mef.getAll();
			Set<IRoundEntry> res = ref.getAll();
			for (IRoundEntry re : res) {
				for (IMatchEntry me: re.getMatchPickMap().values()) {
					IMatchEntry foundObj = null;
					for (IMatchEntry rawMe: mes) {
						if (me.getId().equals(rawMe.getId())) {
							foundObj = rawMe;
							break;
						}
					}

					if (foundObj != null) {
						mes.remove(foundObj);
					} else {
						Logger.getLogger(WorkflowCompetition.class.getName()).log(Level.WARNING,"test found MatchEntry reference to  " + me.getId() + " in RoundEntry " + re.getId() + " that either didn't exist or was in some other RoundEntry.");
						SpawnRoundEntryOrchestration(Game1OrchestrationActions.RoundEntryActions.DELETEMATCHENTRY, Game1OrchestrationTargets.Targets.ROUNDENTRY, re, me);					
						sanityCheck = false;
					}
				}
			}

			// 6b. the ones left in mes are orphans
			for (IMatchEntry me : mes) {
				Logger.getLogger(WorkflowCompetition.class.getName()).log(Level.WARNING,"test found MatchEntry " + me.getId() + " that is not a member of any RoundEntry.");
				SpawnMatchEntryOrchestration(Game1OrchestrationActions.MatchEntryActions.DELETE, Game1OrchestrationTargets.Targets.MATCHENTRY, me, c);						
				sanityCheck = false;
			}
			
			

			if (sanityCheck) {
				//are all match results in nextRound fetched now?
				//if (c.getNextRound() != null) {
				chlmf.setCompetitionId(c.getId());
				List<IClubhouseLeagueMap> list = chlmf.getList();
				
				for (IClubhouseLeagueMap clm : list) {
					checkLeaderboards(clm, c);
				}
			}

		}
		
		System.out.println("Workflow processing complete");
	}





//	private Boolean sanityCheck(ICompetition comp) {
//		try {
//
//			// sanity check
//			IRule<ICompetition> checkCLM = new RuleCLMCorrect(comp, chf, chlmf, lf, mef, ref);
//			return checkCLM.test();
//
//
//		} catch (Throwable ex) {
//			Logger.getLogger("Game1 Workflow").log(Level.SEVERE, "checkLastRoundComplete: " + ex.getMessage());
//			return false;
//		}	
//	}
	
	private void checkLeaderboards(IClubhouseLeagueMap clm, ICompetition comp) {
		IRule<IClubhouseLeagueMap> rule = crf.get(clm, ClubhouseLeagueMapRule.LEADERBOARD_NEEDED);
		
		if (rule.test()) {
			Logger.getLogger(WorkflowCompetition.class.getName()).log(Level.WARNING,"Leaderboard needed for CLM " + clm.getId() + " for comp " + comp.getShortName());
		
			SpawnCLMOrchestration(Game1OrchestrationActions.ClubhouseLeagueMapActions.CREATE_LEADERBOARD, Game1OrchestrationTargets.Targets.CLM, clm, comp);
		}
	}
	
	/**
	 * @param updatenextandprev
	 * @param comp
	 * @param comp2
	 */
	private void SpawnCLMOrchestration(Game1OrchestrationActions.ClubhouseLeagueMapActions action,
			Game1OrchestrationTargets.Targets target, IClubhouseLeagueMap clm, ICompetition comp) {
		assert(target == Game1OrchestrationTargets.Targets.CLM);
	    Queue queue = QueueFactory.getDefaultQueue();
	    TaskOptions to = Builder.withUrl("/game1/orchestration/IClubhouseLeagueMap").
	    		param(AdminOrchestrationActions.CompActions.getKey(), action.toString()).
	    		param(AdminOrchestrationTargets.Targets.getKey(), target.toString()).
	    		param("id",clm.getId().toString()).
	    	    param("extraKey", comp.getId().toString()).
	    	    header("Host", BackendServiceFactory.getBackendService().getBackendAddress("bpm"));

	    log.add("New CLM Activity: " + action.getValue() + " for clm " + clm.getId() + " (compId: " + comp.getId() + ")");
	    		
	    queue.add(to);		
	}

	/**
	 * @param updatenextandprev
	 * @param comp
	 * @param comp2
	 */
	private void SpawnCompOrchestration(Game1OrchestrationActions.CompActions action,
			Game1OrchestrationTargets.Targets target, ICompetition comp) {
		assert(target == Game1OrchestrationTargets.Targets.COMP);
		Queue queue = QueueFactory.getDefaultQueue();
		TaskOptions to = Builder.withUrl("/game1/orchestration/ICompetition").
				param(Game1OrchestrationActions.CompActions.getKey(), action.toString()).
				param(Game1OrchestrationTargets.Targets.getKey(), target.toString()).
				param("id",comp.getId().toString()).
	    	    header("Host", BackendServiceFactory.getBackendService().getBackendAddress("bpm"));

		log.add("New Competition Activity: " + action.getValue() + " for comp " + comp.getShortName() + " (" + comp.getId() + ") in compId ");

		queue.add(to);		
	}
	
	/**
	 * @param fix
	 * @param clubhouse
	 * @param ch
	 * @param c
	 */
	private void SpawnClubhouseOrchestration(ClubhouseActions action,
			Targets target, IClubhouse ch, ICompetition comp) {
		assert(target == Game1OrchestrationTargets.Targets.CLUBHOUSE);
		Queue queue = QueueFactory.getDefaultQueue();
		TaskOptions to = Builder.withUrl("/game1/orchestration/IClubhouse").
				param(Game1OrchestrationActions.CompActions.getKey(), action.toString()).
				param(Game1OrchestrationTargets.Targets.getKey(), target.toString()).
				param("id",ch.getId().toString()).
				param("extraKey", comp.getId().toString()).
	    	    header("Host", BackendServiceFactory.getBackendService().getBackendAddress("bpm"));

		log.add("New Clubhouse Orchestration spawned: " + action.getValue() + " for clubhouse " + ch.getName() + " (" + ch.getId() + ") in compId " + comp.getId());

		queue.add(to);	
		
	}
	
	/**
	 * @param fix
	 * @param clubhouse
	 * @param clm
	 * @param c
	 */
	private void SpawnClmOrchestration(ClubhouseLeagueMapActions action, Targets target, IClubhouseLeagueMap clm, ICompetition comp) {
		assert(target == Game1OrchestrationTargets.Targets.CLM);
		Queue queue = QueueFactory.getDefaultQueue();
		TaskOptions to = Builder.withUrl("/game1/orchestration/IClubhouseLeagueMap").
				param(Game1OrchestrationActions.CompActions.getKey(), action.toString()).
				param(Game1OrchestrationTargets.Targets.getKey(), target.toString()).
				param("id",clm.getId().toString()).
				param("extraKey", comp.getId().toString()).
	    	    header("Host", BackendServiceFactory.getBackendService().getBackendAddress("bpm"));

		log.add("New CLM Orchestration spawned: " + action.getValue() + " for CLM " + clm.getId() +  " in compId " + comp.getId());

		queue.add(to);			
	}
	
	/**
	 * @param fix
	 * @param clubhouse
	 * @param clm
	 * @param c
	 */
	private void SpawnLeagueOrchestration(LeagueActions action, Targets target, ILeague l, ICompetition comp) {
		assert(target == Game1OrchestrationTargets.Targets.LEAGUE);
		Queue queue = QueueFactory.getDefaultQueue();
		TaskOptions to = Builder.withUrl("/game1/orchestration/ILeague").
				param(Game1OrchestrationActions.CompActions.getKey(), action.toString()).
				param(Game1OrchestrationTargets.Targets.getKey(), target.toString()).
				param("id",l.getId().toString()).
				param("extraKey", comp.getId().toString()).
	    	    header("Host", BackendServiceFactory.getBackendService().getBackendAddress("bpm"));

		log.add("New League Orchestration spawned: " + action.getValue() + " for League " + l.getId() +  " in compId " + comp.getId());

		queue.add(to);			
	}
	
	private void SpawnMatchEntryOrchestration(MatchEntryActions action, Targets target, IMatchEntry me, ICompetition comp) {
		assert(target == Game1OrchestrationTargets.Targets.MATCHENTRY);
		Queue queue = QueueFactory.getDefaultQueue();
		TaskOptions to = Builder.withUrl("/game1/orchestration/IMatchEntry").
				param(Game1OrchestrationActions.CompActions.getKey(), action.toString()).
				param(Game1OrchestrationTargets.Targets.getKey(), target.toString()).
				param("id",me.getId().toString()).
				param("extraKey", comp.getId().toString()).
	    	    header("Host", BackendServiceFactory.getBackendService().getBackendAddress("bpm"));

		log.add("New MatchEntry Orchestration spawned: " + action.getValue() + " for MatchEntry " + me.getId() +  " in compId " + comp.getId());

		queue.add(to);			
	}
	
	private void SpawnRoundEntryOrchestration(RoundEntryActions action, Targets target, IRoundEntry re, IMatchEntry me) {
		assert(target == Game1OrchestrationTargets.Targets.ROUNDENTRY);
		Queue queue = QueueFactory.getDefaultQueue();
		TaskOptions to = Builder.withUrl("/game1/orchestration/IRoundEntry").
				param(Game1OrchestrationActions.CompActions.getKey(), action.toString()).
				param(Game1OrchestrationTargets.Targets.getKey(), target.toString()).
				param("id", re.getId().toString()).
				param("extraKey", me.getId().toString()).
	    	    header("Host", BackendServiceFactory.getBackendService().getBackendAddress("bpm"));

		log.add("New RoundEntry Orchestration spawned: " + action.getValue() + " for RoundEntry " + re.getId() +  " dealing with MatchEntry " + me.getId());

		queue.add(to);			
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.workflow.IWorkflow#getLog()
	 */
	@Override
	public List<String> getLog() {
		return log;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.workflow.IWorkflow#setLog(java.util.List)
	 */
	@Override
	public void setLog(List<String> log) {
		this.log = log;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.workflow.IWorkflow#process(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void process(String event, String targetKey, String secondaryKey) {
		// TODO Auto-generated method stub
		
	}

	
}
