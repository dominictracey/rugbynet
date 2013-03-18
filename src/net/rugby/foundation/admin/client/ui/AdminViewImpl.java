package net.rugby.foundation.admin.client.ui;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.place.AdminCompPlace;
import net.rugby.foundation.admin.client.place.AdminTaskPlace;
import net.rugby.foundation.admin.client.ui.task.TaskView;
import net.rugby.foundation.admin.client.ui.task.TaskView.TaskViewPresenter;
import net.rugby.foundation.admin.client.ui.task.TaskViewColumnDefinitions;
import net.rugby.foundation.admin.client.ui.task.TaskViewImpl;
import net.rugby.foundation.admin.shared.IAdminTask;

import org.cobogw.gwt.user.client.CSS;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * Sample implementation of {@link AdminView}.
 */
public class AdminViewImpl extends Composite implements AdminView , SelectionHandler<Integer> {

	private Presenter listener;
	private CompetitionViewImpl cv;
	//private WorkflowConfigurationViewImpl wfc;
	private OrchestrationConfigurationViewImpl ocv;
	//private Game1ConfigurationViewImpl g1cv;
	private TaskViewImpl<IAdminTask> taskv;
	TabPanel tabs;
	private ClientFactory clientFactory;

	public AdminViewImpl() {

		tabs = new TabPanel();
		initComposite();
		initWidget(tabs);


		tabs.setWidth("100%");
		CSS.setProperty(tabs.getWidget(0), CSS.A.PADDING, "6px");
		CSS.setProperty(tabs.getWidget(0), CSS.A.MARGIN, "6px");
	}

	private void initComposite()
	{
		cv = new CompetitionViewImpl();
		ocv = new OrchestrationConfigurationViewImpl();
		taskv = new TaskViewImpl<IAdminTask>();
		TaskViewColumnDefinitions<IAdminTask> taskCols = new TaskViewColumnDefinitions<IAdminTask>();
		taskv.setColumnDefinitions(taskCols);

		tabs.add(cv, "Competitions");
		tabs.add(ocv, "Orchestration Config");
		tabs.add(taskv, "Admin Tasks");
		tabs.addSelectionHandler(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
		if (listener instanceof CompetitionView.Presenter) {
			cv.setPresenter((CompetitionView.Presenter)listener);
			cv.setClientFactory(clientFactory);
			ocv.setPresenter((OrchestrationConfigurationView.Presenter)listener);
		}
		
		if (listener instanceof TaskViewPresenter<?>) {
			taskv.setPresenter((TaskViewPresenter<IAdminTask>)listener);
		}
	}


	@Override
	public CompetitionView getCompView() {
		return cv;
	}

//	@Override
//	public WorkflowConfigurationView getWorkflowConfig() {
//		return wfc;
//	}
	
	@Override
	public void selectTab(int index, boolean fireEvents) {
	    if (index >=0 && index < tabs.getWidgetCount()) {
	        if (index != tabs.getTabBar().getSelectedTab()) {
	        	tabs.selectTab(index);	        	
	        }
	    }	
	}

	@Override
	public void onSelection(SelectionEvent<Integer> event) {

//	    	if (event.getSelectedItem() == 2) {
//	    		cv.setVisible(false);
//	    		ocv.setVisible(false);
//	    		taskv.setVisible(true);
//	    		if (taskv.getPresenter() == null) {
//	    			listener.goTo(new AdminTaskPlace(""));
//	    		}
//	    	} else {
//	    		cv.setVisible(true);
//	    		taskv.setVisible(false);
//	    		if (cv.)
//	    		//listener.goTo(new AdminPlace(""));
//	    	}

	    // Fire an history event corresponding to the tab selected
	    switch (event.getSelectedItem()) {
	    case 0:
	        History.newItem("admin:comp");
	        break;
	    case 1:
	        History.newItem("admin:orch");
	        break;
	    case 2:
	        History.newItem("admin:task");
	        break;
	    }
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.AdminView#getOrchestrationConfig()
	 */
	@Override
	public OrchestrationConfigurationView getOrchestrationConfig() {
		return ocv;
	}

	@Override
	public TaskView<IAdminTask> getTaskView() {
		return taskv;
	}

	@Override
	public void setClientFactory(ClientFactory clientFactoryImpl) {
		this.clientFactory = clientFactoryImpl;
		
	}

	public Presenter getListener() {
		return listener;
	}

}
