package net.rugby.foundation.admin.server.factory.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.rugby.foundation.admin.server.factory.IAdminTaskFactory;
import net.rugby.foundation.admin.server.workflow.fetchstats.FetchMatchStats.Home_or_Visitor;
import net.rugby.foundation.admin.shared.EditPlayerAdminTask;
import net.rugby.foundation.admin.shared.EditPlayerMatchStatsAdminTask;
import net.rugby.foundation.admin.shared.EditTeamMatchStatsAdminTask;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.admin.shared.IAdminTask.Action;
import net.rugby.foundation.admin.shared.IAdminTask.Priority;
import net.rugby.foundation.admin.shared.IAdminTask.Status;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.ITeamMatchStats;

import com.google.inject.Inject;

public class TestAdminTaskFactory implements IAdminTaskFactory {
	
	private IPlayerFactory pf;
	private IMatchGroupFactory mgf;
	private IPlayerMatchStatsFactory pmsf;
	
	private final long DEFAULTADMINID = 0L;
	@Inject
	public TestAdminTaskFactory(IPlayerFactory pf, IMatchGroupFactory mgf, IPlayerMatchStatsFactory pmsf) {
		this.pf = pf;
		this.mgf = mgf;
		this.pmsf = pmsf;
	}
	
	@Override
	public IAdminTask get(Long id) {
		IAdminTask retval = null;
		if (id.equals(500000L)) {
			retval = new EditPlayerAdminTask(
					id, Action.EDITPLAYER, 1L,
					new Date(), null, Status.OPEN, Priority.BLOCKER,
					"Could not find country for R McCaw", "Just couldn't get it", null, null,
					null, null, 9001014L, pf.get(9001014L) );
		} else if (id.equals(500001L)) {
			retval = new EditPlayerMatchStatsAdminTask(
					id, Action.EDITPLAYERMATCHSTATS, 1L,
					new Date(), null, Status.OPEN, Priority.BLOCKER,
					"Could not get time played for R McCaw in NZL vs AUS", "Just couldn't get it", null, null,
					null, null, pf.get(9001014L), mgf.get(300L), null);
		}	
		return retval;
	}
	
	@Override
	public List<IAdminTask> getAllOpen() {
		List<IAdminTask> list = new ArrayList<IAdminTask>();
		
		list.add(get(500000L));
		//list.add(getById(500001L));
		
		return list;
	}

	@Override
	public IAdminTask getNewEditPlayerMatchStatsTask(String summary, String details,
			IPlayer player, IMatchGroup match, Home_or_Visitor hov,
			Integer slot, IPlayerMatchStats pms, boolean sendEmail,
			String pipelineRoot, String pipelineJob, String promiseHandle) {
		
		return new EditPlayerMatchStatsAdminTask(null, Action.EDITPLAYERMATCHSTATS, DEFAULTADMINID, new Date(), null, Status.OPEN, Priority.MAJOR, summary, details, new ArrayList<String>(), promiseHandle, pipelineRoot, pipelineJob, player, match, pms);
	}

	@Override
	public IAdminTask getNewEditPlayerTask(String summary, String details,
			IPlayer player, boolean sendEmail, String pipelineRoot,
			String pipelineJob, String promiseHandle) {
		
		return new EditPlayerAdminTask(null, Action.EDITPLAYER, DEFAULTADMINID, new Date(), null, Status.OPEN, Priority.MAJOR, summary, details, new ArrayList<String>(), promiseHandle, pipelineRoot, pipelineJob, player.getId(), player);
	}
	
	@Override
	public IAdminTask put(IAdminTask task) {
		return task;
	}

	@Override
	public IAdminTask complete(IAdminTask task) {
		task.setStatus(Status.COMPLETE);
		task.setCompleted(new Date());
		return task;
	}

	@Override
	public List<? extends IAdminTask> delete(List<IAdminTask> selectedItems) {
		
		return getAllOpen();
	}

	@Override
	public IAdminTask getNewEditTeamMatchStatsTask(String summary,
			String details, ITeamGroup team, IMatchGroup match,
			Home_or_Visitor hov, ITeamMatchStats tms, boolean sendEmail,
			String pipelineRoot, String pipelineJob, String promiseHandle) {
		
		return new EditTeamMatchStatsAdminTask(null, Action.EDITTEAMMATCHSTATS, DEFAULTADMINID,
				 new Date(), null, Status.OPEN, Priority.MAJOR, summary, details, new ArrayList<String>(), promiseHandle, pipelineRoot, pipelineJob,
				 team, match, tms);
	}

	@Override
	public List<? extends IAdminTask> getForPipeline(String fetchMatchStatsPipelineId) {
		return new ArrayList<IAdminTask>();
	}

	@Override
	public IAdminTask getNewEditPlayerTwitterTask(String summary,
			String details, IPlayer player, Long topTenItemId,
			Long topTenListId, boolean sendEmail, String pipelineRoot,
			String pipelineJob, String promiseHandle) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
