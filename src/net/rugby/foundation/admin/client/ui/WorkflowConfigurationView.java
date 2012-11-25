package net.rugby.foundation.admin.client.ui;

import java.util.List;

import net.rugby.foundation.admin.server.workflow.IWorkflow;
import net.rugby.foundation.model.shared.ICompetition;

public interface WorkflowConfigurationView {

	public interface Presenter {
		void saveWorkflowConfiguration(List<IWorkflow> workflows);
	}
	
	public abstract void setPresenter(CompetitionView.Presenter cvlistener,
			WorkflowConfigurationView.Presenter wfclistener);

	public abstract void setCompetitions(List<ICompetition> result,
			List<IWorkflow> workflows);

	public abstract void showStatus(String message);

}