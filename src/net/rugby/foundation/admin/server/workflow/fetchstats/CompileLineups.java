package net.rugby.foundation.admin.server.workflow.fetchstats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rugby.foundation.admin.server.workflow.weekend.results.MS3LineupsAnnounced;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.ILineupSlotFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.model.shared.ILineupSlot;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.WorkflowStatus;
import net.rugby.foundation.model.shared.IPlayer;

import com.google.appengine.tools.pipeline.Job4;
import com.google.appengine.tools.pipeline.Job6;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

public class CompileLineups extends Job6<MS3LineupsAnnounced, Long, List<Long>, List<Long>, List<Long>, List<Long>, String> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 6387917945891986170L;
	private transient IMatchGroupFactory mf;
	private IPlayerFactory pf;
	private ILineupSlotFactory lsf;

	public CompileLineups() {

	}

	@Override
	public Value<MS3LineupsAnnounced> run(Long matchId, List<Long> hLUSs, List<Long> vLUSs, List<Long> hpIds, List<Long> vpIds, String job) {
		Injector injector = BPMServletContextListener.getInjectorForNonServlets();

		this.mf = injector.getInstance(IMatchGroupFactory.class);
		this.pf = injector.getInstance(IPlayerFactory.class);
		this.lsf = injector.getInstance(ILineupSlotFactory.class);
		
		List<String> log = new ArrayList<String>();

		IMatchGroup match = null;
		if (hLUSs == null ||  hLUSs.size() == 0 || vLUSs == null || vLUSs.size() == 0) {
			GenerateFetchLineupsResults retval = new GenerateFetchLineupsResults(hLUSs, vLUSs, job);
			
			MS3LineupsAnnounced wrapper = new MS3LineupsAnnounced();
			wrapper.fetchSubTreeResults = retval;
			wrapper.success = false;
			wrapper.log.add("Lineups not fetched");

			
			return immediate(wrapper);
		} else {
			match = mf.get(matchId);
		}
		
		// update the lineupSlots to include the playerIds;
		Map<Long, IPlayer> playerMap = new HashMap<Long, IPlayer>();  //key: foreignPlayerId
		for (Long id: hpIds) {
			IPlayer p = pf.get(id);
			playerMap.put(p.getScrumId(), p);
		}

		for (Long id: vpIds) {
			IPlayer p = pf.get(id);
			playerMap.put(p.getScrumId(), p);
		}
		
		for (Long id: hLUSs) {
			ILineupSlot lus = lsf.get(id);
			if (lus != null) {
				Long scrumId = lus.getForeignPlayerId();
				if (scrumId != null) {
					IPlayer p = playerMap.get(scrumId);
					if (p != null) {
						lus.setPlayerId(p.getId());
						lus.setMatchId(matchId);
						lsf.put(lus);
					}
				}
			}
		}
		
		for (Long id: vLUSs) {
			ILineupSlot lus = lsf.get(id);
			if (lus != null) {
				Long scrumId = lus.getForeignPlayerId();
				if (scrumId != null) {
					IPlayer p = playerMap.get(scrumId);
					if (p != null) {
						lus.setPlayerId(p.getId());
						lus.setMatchId(matchId);
						lsf.put(lus);
					}
				}
			}
		}
		
		// all AdminTasks have been completed when we get here so update the WorkflowState
		match.setWorkflowStatus(WorkflowStatus.LINEUPS);
		mf.put(match);
		log.add("Stats fetched for " + match.getDisplayName());

		
		GenerateFetchLineupsResults retval = new GenerateFetchLineupsResults(hLUSs, vLUSs, job);
		retval.log.addAll(log);
		MS3LineupsAnnounced wrapper = new MS3LineupsAnnounced();
		wrapper.fetchSubTreeResults = retval;
		wrapper.matchId = match.getId();
		wrapper.log.addAll(retval.log);

		
		wrapper.success = true;
		
		return (Value<MS3LineupsAnnounced>)immediate(wrapper);
	}

}
