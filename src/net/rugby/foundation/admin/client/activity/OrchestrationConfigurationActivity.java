package net.rugby.foundation.admin.client.activity;

import java.util.Map;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.place.AdminOrchPlace;
import net.rugby.foundation.admin.client.ui.AdminView;
import net.rugby.foundation.admin.client.ui.OrchestrationConfigurationView;
import net.rugby.foundation.admin.shared.IOrchestrationConfiguration;
import net.rugby.foundation.admin.shared.IWorkflowConfiguration;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * Activities are started and stopped by an ActivityManager associated with a container Widget.
 */
public class OrchestrationConfigurationActivity extends AbstractActivity implements  OrchestrationConfigurationView.Presenter  {
	/**
	 * Used to obtain views, eventBus, placeController.
	 * Alternatively, could be injected via GIN.
	 */
	private ClientFactory clientFactory;
	private  Map<String, IOrchestrationConfiguration> orchConfig = null;
	private OrchestrationConfigurationView view;

	public OrchestrationConfigurationActivity(AdminOrchPlace place, ClientFactory clientFactory) {
		//this.name = place.getName();
		this.clientFactory = clientFactory;
		view = clientFactory.getOrchestrationConfigurationView();

	}



	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		view.setPresenter(this);

		containerWidget.setWidget(view.asWidget());

		clientFactory.getRpcService().getWorkflowConfiguration(new AsyncCallback<IWorkflowConfiguration>() {

			@Override
			public void onFailure(Throwable caught) {


			}

			@Override
			public void onSuccess(final IWorkflowConfiguration workflowConfig) {

				//view.getWorkflowConfig().setCompetitions(comps, workflowConfig);


				// nest this to solve the asynchronous problem of creating two wfcs.
				clientFactory.getRpcService().getOrchestrationConfiguration(new AsyncCallback<Map<String, IOrchestrationConfiguration>>() {
					@Override
					public void onFailure(Throwable caught) {

						view.showStatus(caught.getMessage());
					}

					@Override
					public void onSuccess(Map<String, IOrchestrationConfiguration> result) {					
						view.setOrchConfigs(result);	
					}
				});
			}
		});	

	}

	//	@Override
	//	public String mayStop() {
	//		return "Please hold on. This activity is stopping.";
	//	}

	/**
	 * @see AdminView.Presenter#goTo(Place)
	 */
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.OrchestrationConfigurationView.Presenter#saveClicked(java.util.Map)
	 */
	@Override
	public Map<String, IOrchestrationConfiguration> saveClicked(Map<String, IOrchestrationConfiguration> configs) {

		clientFactory.getRpcService().saveOrchestrationConfiguration(configs, new AsyncCallback<Map<String, IOrchestrationConfiguration>>() {

			@Override
			public void onFailure(Throwable caught) {
				view.showStatus(caught.getMessage());
			}

			@Override
			public void onSuccess(Map<String, IOrchestrationConfiguration> result) {
				view.showStatus("Success");
				orchConfig = result;
				Window.alert("Saved");
			}
		});
		return orchConfig;	
	}



}
