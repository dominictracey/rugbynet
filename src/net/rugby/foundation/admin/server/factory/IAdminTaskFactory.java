package net.rugby.foundation.admin.server.factory;

import java.util.List;

import net.rugby.foundation.admin.server.workflow.matchrating.GenerateMatchRatings.Home_or_Visitor;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.ITeamMatchStats;

public interface IAdminTaskFactory {
	IAdminTask get(Long id);
	List<? extends IAdminTask> getAllOpen();
	IAdminTask getNewEditPlayerMatchStatsTask(String summary, String details,
			IPlayer player, IMatchGroup match, Home_or_Visitor hov,
			Integer slot, IPlayerMatchStats pms, boolean sendEmail,
			String pipelineRoot, String pipelineJob, String promiseHandle);
	IAdminTask getNewEditTeamMatchStatsTask(String summary, String details,
			ITeamGroup team, IMatchGroup match, Home_or_Visitor hov,
			ITeamMatchStats tms, boolean sendEmail,
			String pipelineRoot, String pipelineJob, String promiseHandle);	
	IAdminTask getNewEditPlayerTask(String summary, String details,
			IPlayer player, boolean sendEmail,
			String pipelineRoot, String pipelineJob, String promiseHandle);
	IAdminTask put(IAdminTask task);
	IAdminTask complete(IAdminTask task);
	List<? extends IAdminTask> delete(List<IAdminTask> selectedItems);

}