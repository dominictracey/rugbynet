/**
 * 
 */
package net.rugby.foundation.game1.server.BPM;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.TaskOptions;

import net.rugby.foundation.admin.server.orchestration.OrchestrationCore;
import net.rugby.foundation.game1.server.factory.IMatchEntryFactory;
import net.rugby.foundation.game1.shared.IMatchEntry;

/**
 * @author home
 *
 */
@SuppressWarnings("deprecation")
public class OrchestrationDeleteMatchEntry extends OrchestrationCore<IMatchEntry> {

	private IMatchEntryFactory mef;
	private Long compId;

	public OrchestrationDeleteMatchEntry(IMatchEntryFactory mef) {
		this.mef = mef;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#execute()
	 */
	@Override
	public void execute() {

		// take the matchEntry out
		if (target != null) {
			mef.setId(target.getId());
			mef.delete();
	
			Logger.getLogger(OrchestrationDeleteMatchEntry.class.getName()).log(Level.WARNING,"deleted matchEntry " + target.getId());
		} else {
			Logger.getLogger(OrchestrationDeleteMatchEntry.class.getName()).log(Level.WARNING,"Called with null MatchEntry!");
		}

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
