/**
 * 
 */
package net.rugby.foundation.admin.server.orchestration;

import java.util.List;

import net.rugby.foundation.admin.shared.AdminOrchestrationActions;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Builder;

/**
 * @author home
 *
 */
public class OrchestrationHelper {

	
	/**
	 * @param updatenextandprev
	 * @param comp
	 * @param comp2
	 */
	public void SpawnCompOrchestration(AdminOrchestrationActions.CompActions action,
			AdminOrchestrationTargets.Targets target, ICompetition comp, List<String> log) {
		assert(target == AdminOrchestrationTargets.Targets.COMP);
	    Queue queue = QueueFactory.getDefaultQueue();
	    TaskOptions to = Builder.withUrl("/admin/orchestration/ICompetitionGroup").
	    		param(AdminOrchestrationActions.CompActions.getKey(), action.toString()).
	    		param(AdminOrchestrationTargets.Targets.getKey(), target.toString()).
	    		param("id",comp.getId().toString()).
	    		param("extraKey", "0L");

	    log.add("New Competition Activity: " + action.getValue() + " for comp " + comp.getShortName() + " (" + comp.getId() + ") in compId ");
	    		
	    queue.add(to);		
	}

	/**
	 * @param action
	 * @param target
	 * @param id
	 */
	public void SpawnMatchOrchestration(AdminOrchestrationActions.MatchActions action, AdminOrchestrationTargets.Targets target, IMatchGroup match, ICompetition comp, List<String> log) {
		
		assert(target == AdminOrchestrationTargets.Targets.MATCH);
	    Queue queue = QueueFactory.getDefaultQueue();
	    TaskOptions to = Builder.withUrl("/admin/orchestration/IMatchGroup").
	    		param(AdminOrchestrationActions.MatchActions.getKey(), action.toString()).
	    		param(AdminOrchestrationTargets.Targets.getKey(), target.toString()).
	    		param("id",match.getId().toString()).
	    		param("extraKey", comp.getId().toString());

	    log.add("New Match Activity: " + action.getValue() + " for match " + match.getDisplayName() + " (" + match.getId() + ") in compId " + comp.getId().toString() + " url as " + to.getUrl());
	    		
	    try {
	    	queue.add(to);
	    } catch (Throwable e) {
	    	log.add("Error in enqueing orchestration request " + e.getMessage());
	    }
		
	}
}
