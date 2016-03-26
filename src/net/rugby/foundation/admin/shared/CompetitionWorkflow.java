/**
 * 
 */
package net.rugby.foundation.admin.shared;

import java.util.List;

import com.googlecode.objectify.annotation.Subclass;

import net.rugby.foundation.admin.server.orchestration.AdminOrchestrationTargets;
import net.rugby.foundation.admin.server.orchestration.OrchestrationHelper;
import net.rugby.foundation.admin.server.rules.ICoreRuleFactory;
import net.rugby.foundation.admin.server.rules.ICoreRuleFactory.MatchRule;
import net.rugby.foundation.admin.server.rules.ICoreRuleFactory.RoundRule;
import net.rugby.foundation.admin.server.rules.IRule;
import net.rugby.foundation.admin.server.workflow.Workflow;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;

/**
 * @author home
 *
 */
@Subclass
public class CompetitionWorkflow extends Workflow {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static enum Events { Initiated, CronCheck, ResultFound, ResultNotFound, MatchLocked, RoundAdvanced, Archived, Terminated ; 
	
								public static String key = "Event"; }

	private List<String> log = null;
	private ICompetitionFactory cf;
	private ICoreRuleFactory crf;
	private OrchestrationHelper queuer;
	private IMatchGroupFactory mf;
	
//	@Inject
	public CompetitionWorkflow(ICompetitionFactory cf, ICoreRuleFactory crf, IMatchGroupFactory mf) {		
		this.cf = cf;
		this.crf = crf;
		this.mf = mf;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.workflow.IWorkflow#process()
	 */
	@Override
	public void process(String event, String targetKey, String secondaryKey) {
		Events ev = Events.valueOf(Events.class, event);
		//IWorkflowConfiguration wfc = wfcf.get();
		queuer = new OrchestrationHelper();
		
		if (ev == Events.CronCheck) {
			checkMatches(compId);
		} else if (ev == Events.MatchLocked) {
			// see if the match needs to be fetched now

			checkResults(mf.get(Long.parseLong(targetKey)),cf.get(getCompId()));
		} else if (ev == Events.ResultFound) {
			// see if the round can be advanced now

			checkResults(mf.get(Long.parseLong(targetKey)),cf.get(getCompId()));
			ICompetition c = cf.get(getCompId());
			if (c.getNextRound() != null) {
				checkRoundComplete(c.getNextRound(), c);	
			}
		}
	}

	/**
	 * 
	 * @param wfc There is only one instance of the {@link <WorkflowConfiguration> [WorkflowConfiguration]}. It contains a list of ids of underway competitions.
	 * <br/>
	 * We are looking for two or three rounds: <ul>
	 * <li>The round that last completed</li>
	 * <li>Optionally, the round that is underway</li>
	 * <li>The round that is fixing to start</li>
	 */
	private void checkMatches(Long compId) {
		
			ICompetition c = cf.get(compId);
			
			
			if (c.getNextRound() != null) {
				
				for (IMatchGroup m : c.getNextRound().getMatches()) {
					// need results fetched?
					checkResults(m, c);
				
					// need to be locked?
					checkLocks(m, c);	
				
					// getting stale? (need results)
					checkStaleAttention(m,c);
					
					// getting really stale? (mark as unreported)
					checkStaleGiveUp(m,c);
					
					// time to get all stats?
					checkStats(m,c);
				}
			}		
			
			//are all match results in nextRound fetched now?
			if (c.getNextRound() != null) {
				checkRoundComplete(c.getNextRound(), c);	
			}	
		
		System.out.println("Workflow processing complete");
	}

	/**
	 * @param m
	 * @param c
	 */
	private void checkStaleGiveUp(IMatchGroup m, ICompetition comp) {
		IRule<IMatchGroup> rule = crf.get(m, MatchRule.STALE_MATCH_NEED_ATTENTION);
		 
		if (rule.test()) {
			// fetch match
			queuer.SpawnMatchOrchestration(AdminOrchestrationActions.MatchActions.MATCH_STALE_NEEDS_ATTENTION, AdminOrchestrationTargets.Targets.MATCH, m, comp, log);
		}			
	}

	/**
	 * @param m
	 * @param c
	 */
	private void checkStaleAttention(IMatchGroup m, ICompetition comp) {
		IRule<IMatchGroup> rule = crf.get(m, MatchRule.STALE_MATCH_TO_MARK_UNREPORTED);
		 
		if (rule.test()) {
			// fetch match
			queuer.SpawnMatchOrchestration(AdminOrchestrationActions.MatchActions.MATCH_STALE_MARK_UNREPORTED, AdminOrchestrationTargets.Targets.MATCH, m, comp, log);
		}	
	}

	/**
	 * @param r - Round to check for results on
	 */
	private void checkResults(IMatchGroup m, ICompetition comp) {
		IRule<IMatchGroup> rule = crf.get(m, MatchRule.MATCH_TO_FETCH);
			 
		if (rule.test()) {
			// fetch match
			queuer.SpawnMatchOrchestration(AdminOrchestrationActions.MatchActions.FETCH, AdminOrchestrationTargets.Targets.MATCH, m, comp, log);
		}						
	}

	private void checkStats(IMatchGroup m, ICompetition comp) {
		IRule<IMatchGroup> rule = crf.get(m, MatchRule.MATCH_STATS_AVAILABLE);
			 
		if (rule.test()) {
			// fetch match
			queuer.SpawnMatchOrchestration(AdminOrchestrationActions.MatchActions.FETCHSTATS, AdminOrchestrationTargets.Targets.MATCH, m, comp, log);
		}						
	}
	
	private void checkLocks(IMatchGroup m, ICompetition comp) {
		IRule<IMatchGroup> rule = crf.get(m, MatchRule.MATCH_TO_LOCK);
 
		if (rule.test()) {
			// lock match
			queuer.SpawnMatchOrchestration(AdminOrchestrationActions.MatchActions.LOCK, AdminOrchestrationTargets.Targets.MATCH, m, comp, log);
		}
				
	}
	
	private void checkRoundComplete(IRound r, ICompetition comp) {
		IRule<IRound> rule = crf.get(r, RoundRule.ROUND_COMPLETE);
		
		if (rule.test())
			queuer.SpawnCompOrchestration(AdminOrchestrationActions.CompActions.UPDATENEXTANDPREV, AdminOrchestrationTargets.Targets.COMP, comp, log);
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

	
}
