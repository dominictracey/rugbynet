/**
 * 
 */
package net.rugby.foundation.game1.server.BPM;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.TaskOptions;

import net.rugby.foundation.admin.server.orchestration.OrchestrationCore;
import net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory;
import net.rugby.foundation.game1.server.factory.ILeagueFactory;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.ILeague;

/**
 * @author home
 *
 */
@SuppressWarnings("deprecation")
public class OrchestrationFixLeague extends OrchestrationCore<ILeague> {

	private IClubhouseLeagueMapFactory chlmf;
	private Long compId;
	private ILeagueFactory lf;

	public OrchestrationFixLeague(IClubhouseLeagueMapFactory chlmf, ILeagueFactory lf) {
		this.chlmf = chlmf;
		this.lf = lf;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#execute()
	 */
	@Override
	public void execute() {

		chlmf.setLeagueId(target.getId());
		List<IClubhouseLeagueMap> chlml = chlmf.getList();

		//5. 
		if (chlml == null || chlml.isEmpty()) {
			lf.setId(target.getId());
			lf.delete();
			Logger.getLogger(OrchestrationFixLeague.class.getName()).log(Level.WARNING,"deleted league " + target.getId());

		} else if (chlml.size() > 1) {
			boolean skipped = false;
			// delete all but the first
			for (IClubhouseLeagueMap clm : chlml) {
				if (!skipped) {
					skipped = true;
				} else {
					lf.setId(clm.getLeagueId());
					lf.delete();
					chlmf.setId(clm.getId());
					chlmf.delete();
					Logger.getLogger(OrchestrationFixLeague.class.getName()).log(Level.WARNING,"deleted league " + clm.getLeagueId() + " and CLM " + clm.getId());

				}
			}

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
