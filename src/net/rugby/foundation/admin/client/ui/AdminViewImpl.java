package net.rugby.foundation.admin.client.ui;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.place.AdminPlace;
import net.rugby.foundation.admin.client.place.AdminTaskPlace;
import net.rugby.foundation.admin.client.ui.task.TaskView;
import net.rugby.foundation.admin.client.ui.task.TaskView.TaskViewPresenter;
import net.rugby.foundation.admin.client.ui.task.TaskViewColumnDefinitions;
import net.rugby.foundation.admin.client.ui.task.TaskViewImpl;
import net.rugby.foundation.admin.shared.IAdminTask;

import org.cobogw.gwt.user.client.CSS;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * Sample implementation of {@link AdminView}.
 */
public class AdminViewImpl extends Composite implements AdminView, SelectionHandler<Integer> {
	
//	interface Binder extends UiBinder<Widget, AdminViewImpl> {
//	}
//	
//	private static final Binder binder = GWT.create(Binder.class);

	private Presenter listener;
	private CompetitionViewImpl cv;
	//private WorkflowConfigurationViewImpl wfc;
	private OrchestrationConfigurationViewImpl ocv;
	//private Game1ConfigurationViewImpl g1cv;
	private TaskView<IAdminTask> taskv;
	
//	@UiField
	TabPanel tabs;
	private ClientFactory clientFactory;

	public AdminViewImpl() {
//		initWidget(binder.createAndBindUi(this));
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
		//wfc = new WorkflowConfigurationViewImpl();
		//g1cv = new Game1ConfigurationViewImpl();

		tabs.add(cv, "Competitions");
		tabs.add(ocv, "Orchestration Config");
		tabs.add(taskv, "Admin Tasks");
//		tabs.add(wfc,"Workflow Config");
		//tabs.add(g1cv,"Game1 Config");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setPresenter(Presenter listener) {
		this.setListener(listener);
		if (listener instanceof CompetitionView.Presenter) {
			cv.setPresenter((CompetitionView.Presenter)listener);
			cv.setClientFactory(clientFactory);
//			wfc.setPresenter((CompetitionView.Presenter)listener, (WorkflowConfigurationView.Presenter)listener);
			ocv.setPresenter((OrchestrationConfigurationView.Presenter)listener);
			//g1cv.setPresenter((Game1ConfigurationView.Presenter)listener);	
			
		}
		
		if (listener instanceof TaskViewPresenter<?>) {
			taskv.setPresenter((TaskViewPresenter<IAdminTask>)listener);
		}

		tabs.addSelectionHandler(this);

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
	public void selectTab(int index) {
	    if (index >=0 && index < tabs.getWidgetCount()) {
	        if (index != tabs.getTabBar().getSelectedTab()) {

	        		tabs.selectTab(index);
	        	
	        }
	    }	
	}

	@Override
	public void onSelection(SelectionEvent<Integer> event) {
    	if (event.getSelectedItem() == 2) {
    		listener.goTo(new AdminTaskPlace(""));
    	} else {
    		listener.goTo(new AdminPlace(""));
    	}
	    // Fire an history event corresponding to the tab selected
//	    switch (event.getSelectedItem()) {
//	    case 0:
//	        History.newItem("admin:1");
//	        break;
//	    case 1:
//	        History.newItem("admin:2");
//	        break;
//	    case 2:
//	        History.newItem("admin:3");
//	        break;
//	    case 3:
//	        History.newItem("admin:4");
//	        break;
//	    }
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

	public void setListener(Presenter listener) {
		this.listener = listener;
	}



}
