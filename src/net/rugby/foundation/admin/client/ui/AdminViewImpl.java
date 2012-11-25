package net.rugby.foundation.admin.client.ui;

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
	private EditPlayer epv;
	
//	@UiField
	TabPanel tabs;

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
		epv = new EditPlayer();
		//wfc = new WorkflowConfigurationViewImpl();
		//g1cv = new Game1ConfigurationViewImpl();

		tabs.add(cv, "Competitions");
		tabs.add(ocv, "Orchestration Config");
		tabs.add(epv, "Edit Player");
//		tabs.add(wfc,"Workflow Config");
		//tabs.add(g1cv,"Game1 Config");
	}
	
	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
		if (listener instanceof CompetitionView.Presenter) {
			cv.setPresenter((CompetitionView.Presenter)listener);
//			wfc.setPresenter((CompetitionView.Presenter)listener, (WorkflowConfigurationView.Presenter)listener);
			ocv.setPresenter((OrchestrationConfigurationView.Presenter)listener);
			//g1cv.setPresenter((Game1ConfigurationView.Presenter)listener);	
			
		}
		
		if (listener instanceof EditPlayer.Presenter) {
			epv.SetPresenter((EditPlayer.Presenter)listener);
		}
		
		tabs.selectTab(0);

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
	public EditPlayer getEditPlayer() {
		return epv;
	}



}
