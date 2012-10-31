/**
 * 
 */
package net.rugby.foundation.game1.server.BPM;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.TaskOptions;

import net.rugby.foundation.admin.server.orchestration.OrchestrationCore;
import net.rugby.foundation.game1.server.factory.IRoundEntryFactory;
import net.rugby.foundation.game1.shared.IRoundEntry;

/**
 * @author home
 *
 */
@SuppressWarnings("deprecation")
public class OrchestrationDeleteMatchEntryFromRoundEntry extends OrchestrationCore<IRoundEntry> {

	private IRoundEntryFactory ref;
	private Long matchEntryId;

	public OrchestrationDeleteMatchEntryFromRoundEntry(IRoundEntryFactory ref) {
		this.ref = ref;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#execute()
	 */
	@Override
	public void execute() {

		// take the matchEntry out
		if (target.getMatchPickMap().remove(matchEntryId) != null) {
			if (target.getMatchPickIdList().remove(target.getId())) {
				ref.put(target);
				Logger.getLogger(OrchestrationDeleteMatchEntry.class.getName()).log(Level.WARNING,"removed matchEntry " + matchEntryId + " from RoundEntry "+ target.getId());

			}
		}


	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#setExtraKey(java.lang.Long)
	 */
	@Override
	public void setExtraKey(Long id) {
		this.matchEntryId = id;

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#getExtraKey()
	 */
	@Override
	public Long getExtraKey() {
		return matchEntryId;
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
