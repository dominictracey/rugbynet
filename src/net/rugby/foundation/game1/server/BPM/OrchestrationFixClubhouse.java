/**
 * 
 */
package net.rugby.foundation.game1.server.BPM;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.TaskOptions;

import net.rugby.foundation.admin.server.orchestration.OrchestrationCore;
import net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory;
import net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory;
import net.rugby.foundation.game1.server.factory.IEntryFactory;
import net.rugby.foundation.game1.server.factory.ILeagueFactory;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.game1.shared.ILeague;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.IClubhouseMembership;

/**
 * @author home
 *
 */
@SuppressWarnings("deprecation")
public class OrchestrationFixClubhouse extends OrchestrationCore<IClubhouse> {

	private IClubhouseLeagueMapFactory chlmf;
	private Long compId;
	private ILeagueFactory lf;
	private IClubhouseMembershipFactory chmf;
	private IEntryFactory ef;

	public OrchestrationFixClubhouse(IClubhouseLeagueMapFactory chlmf, ILeagueFactory lf, IClubhouseMembershipFactory chmf,
			IEntryFactory ef) {
		this.chlmf = chlmf;
		this.chmf = chmf;
		this.lf = lf;
		this.ef = ef;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#execute()
	 */
	@Override
	public void execute() {
		chlmf.setClubhouseAndCompId(compId, target.getId());
		List<IClubhouseLeagueMap> clms = chlmf.getList();
		IClubhouseLeagueMap clm = null;
		if (clms == null || clms.size() == 0) {
			// if we don't find any, create one
			chlmf.setId(null);
			clm = chlmf.get();
			clm.setClubhouseId(target.getId());
			clm.setCompId(compId);
			clm.setLeagueId(null);
			Logger.getLogger(OrchestrationFixClubhouse.class.getName()).log(Level.WARNING,"Created new CLM for clubhouse " + target.getName() + " (" + target.getId() + ") in comp " + compId);
			chlmf.put(clm);
		} else if (clms.size() > 1) {
			// if we find more than one, delete all but the first
			boolean skipped = false;
			for (IClubhouseLeagueMap toGo : clms) {
				if (!skipped) {
					// leave the first one
					skipped = true;
					clm = toGo;
				} else {
					chlmf.setId(toGo.getId());
					chlmf.delete();
					Logger.getLogger(OrchestrationFixClubhouse.class.getName()).log(Level.WARNING,"Deleted extra CLM for clubhouse " + target.getName() + " (" + target.getId() + ") in comp " + compId);
				}
			}
		} else {
			clm = clms.get(0);
		}
		
		assert clm != null;
		
		lf.setId(clm.getLeagueId());
		ILeague league = lf.get();
		
		if (league.getId() == null) {  // if its a blank/new one build it
			chmf.setClubhouseId(target.getId());
	
			List<IClubhouseMembership> memberships = chmf.getList();
			league.getEntryIds().clear();
			league.getEntryMap().clear();
			if (memberships != null) {
				// for each user, get any entries that they have for the comp we are talking about 
				//	and add them to the league if they aren't already there.				
				for (IClubhouseMembership membership : memberships) {
					ef.setUserIdAndCompId(membership.getAppUserID(), compId);
					List<IEntry> entries = ef.getEntries();
					// for each entry, add them to the league
					for (IEntry e: entries) {
						if (!league.getEntryIds().contains(e)) {
							league.addEntry(e);
						}
					}
				}
				lf.put(league);
				
				// and update the CLM with the new league
				clm.setLeagueId(league.getId());
				chlmf.put(clm);
				
				Logger.getLogger(OrchestrationFixClubhouse.class.getName()).log(Level.WARNING,"Created new League " + league.getId() + " for clubhouse " + target.getName() + " (" + target.getId() + ") in comp " + compId);

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
