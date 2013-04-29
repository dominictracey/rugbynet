package net.rugby.foundation.admin.server.factory.ofy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.admin.server.factory.IAdminTaskFactory;
import net.rugby.foundation.admin.server.workflow.matchrating.GenerateMatchRatings.Home_or_Visitor;
import net.rugby.foundation.admin.shared.AdminTask;
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
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.PlayerMatchRating;

public class OfyAdminTaskFactory implements IAdminTaskFactory {
	
	private final long DEFAULTADMINID = 0L;

	private IPlayerFactory pf;
	private IMatchGroupFactory mgf;
	private IPlayerMatchStatsFactory pmsf;
	private Objectify ofy;
	
	@Inject
	public OfyAdminTaskFactory(IPlayerFactory pf, IMatchGroupFactory mgf, IPlayerMatchStatsFactory pmsf) {
		this.pf = pf;
		this.mgf = mgf;
		this.pmsf = pmsf;
		this.ofy = DataStoreFactory.getOfy();
	}
	
	@Override
	public IAdminTask get(Long id) {
		return ofy.get(new Key<AdminTask>(AdminTask.class, id));
	}

	@Override
	public List<? extends IAdminTask> getAllOpen() {
		Query<AdminTask> qat = ofy.query(AdminTask.class).filter("status", Status.OPEN);
		return qat.list();

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
		
		Long id = null;
		if (player != null) {
			id = player.getId();
		}
		
		return new EditPlayerAdminTask(null, Action.EDITPLAYER, DEFAULTADMINID, new Date(), null, Status.OPEN, Priority.MAJOR, summary, details, new ArrayList<String>(), promiseHandle, pipelineRoot, pipelineJob, id, player);
	}

	@Override
	public IAdminTask put(IAdminTask task) {
		ofy.put(task);
		return task;
	}

	@Override
	public IAdminTask complete(IAdminTask task) {
		task.setStatus(Status.COMPLETE);
		task.setCompleted(new Date());
		ofy.put(task);
		return task;
	}

	@Override
	public List<? extends IAdminTask> delete(List<IAdminTask> selectedItems) {
		ofy.delete(selectedItems);
		
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

}
