package net.rugby.foundation.admin.client.ui;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.ui.portal.PortalView;
import net.rugby.foundation.admin.client.ui.task.TaskView;
import net.rugby.foundation.admin.shared.IAdminTask;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View base interface.
 * Extends IsWidget so a view impl can easily provide its container widget.
 */
public interface AdminView extends IsWidget {
  
	void selectTab(int index, boolean fireEvents);
	
	void setPresenter(Presenter listener);

	public interface Presenter {
		/**
		 * Navigate to a new Place in the browser.
		 */
		void goTo(Place place);
	}

	CompetitionView getCompView();
	//WorkflowConfigurationView getWorkflowConfig();
	OrchestrationConfigurationView getOrchestrationConfig();

	void setClientFactory(ClientFactory clientFactoryImpl);

	TaskView<IAdminTask> getTaskView();
	
	PortalView getPortalView();
}
