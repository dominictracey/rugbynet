/**
 * 
 */
package net.rugby.foundation.game1.server.BPM;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.TaskOptions;

import net.rugby.foundation.admin.server.orchestration.OrchestrationCore;
import net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;

/**
 * @author home
 *
 */
@SuppressWarnings("deprecation")
public class OrchestrationFixClm extends OrchestrationCore<IClubhouseLeagueMap> {

	private IClubhouseLeagueMapFactory chlmf;
	private Long compId;

	public OrchestrationFixClm(IClubhouseLeagueMapFactory chlmf) {
		this.chlmf = chlmf;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#execute()
	 */
	@Override
	public void execute() {

		// This would be a CLM that doesn't have a valid League or Clubhouse and needs to just go away. I suppose it is a way to fix it.
		chlmf.setId(target.getId());
		chlmf.delete();
		Logger.getLogger(OrchestrationFixClm.class.getName()).log(Level.WARNING,"Deleted CLM " + target.getId() + " in comp " + compId);

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#setExtraKey(java.lang.Long)
	 */
	@Override
	public void setExtraKey(Long id) {
		this.compId = id;
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#getExtraKey()
	 */
	@Override
	public Long getExtraKey() {
		return compId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#addParams(com.google.appengine.api.taskqueue.TaskOptions)
	 */
	@Override
	public TaskOptions addParams(TaskOptions builder) {
		// TODO Auto-generated method stub
		return null;
	}

}
