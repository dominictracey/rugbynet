package net.rugby.foundation.admin.client.activity;

import java.util.Date;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DialogBox;
import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.place.SeriesPlace;
import net.rugby.foundation.admin.client.ui.AdminView;
import net.rugby.foundation.admin.client.ui.SmartBar;
import net.rugby.foundation.admin.client.ui.playermatchstatspopup.PlayerMatchStatsPopupView.PlayerMatchStatsPopupViewPresenter;
import net.rugby.foundation.admin.client.ui.playerpopup.PlayerPopupView;
import net.rugby.foundation.admin.client.ui.seriesconfiguration.SeriesConfigPopupView.Presenter;
import net.rugby.foundation.admin.client.ui.seriesconfiguration.SeriesConfigurationView;
import net.rugby.foundation.admin.client.ui.seriesconfiguration.SeriesConfigurationView.SeriesConfigurationViewPresenter;
import net.rugby.foundation.admin.client.ui.task.TaskView;
import net.rugby.foundation.admin.client.ui.task.TaskView.TaskViewPresenter;
import net.rugby.foundation.admin.client.ui.teammatchstatspopup.TeamMatchStatsPopupView.TeamMatchStatsPopupViewPresenter;
import net.rugby.foundation.admin.shared.EditPlayerAdminTask;
import net.rugby.foundation.admin.shared.EditPlayerMatchStatsAdminTask;
import net.rugby.foundation.admin.shared.EditTeamMatchStatsAdminTask;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.ITeamMatchStats;

public class SeriesActivity extends AbstractActivity implements  
AdminView.Presenter,  
SmartBar.Presenter,
SeriesConfigurationViewPresenter<ISeriesConfiguration>
, Presenter<ISeriesConfiguration>
{ 
	/**
	 * Used to obtain views, eventBus, placeController.
	 * Alternatively, could be injected via GIN.
	 */
	private ClientFactory clientFactory;
	private SeriesPlace place;
	SelectionModel<IAdminTask> selectionModel;
	int index; 
	private SeriesConfigurationView<ISeriesConfiguration> view;
	private MenuItemDelegate menuItemDelegate;
	protected Timer t = null;

	public SeriesActivity(SeriesPlace place, ClientFactory clientFactory) {
		selectionModel = new SelectionModel<IAdminTask>();

		this.clientFactory = clientFactory;
		view = clientFactory.getSeriesView();
		
		this.place = place;
		
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {

		view.setPresenter(this);
		panel.setWidget(view.asWidget());

		if (place != null) {
			clientFactory.getRpcService().getAllSeriesConfigurations(new AsyncCallback<List<ISeriesConfiguration>>() {
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Failed to fetch series configs");
				}

				@Override
				public void onSuccess(List<ISeriesConfiguration> result) {
					view.showList(result);
				}
			});
		}
	}



	@Override
	public String mayStop()
	{
		return null;

	}

	@Override
	public ClientFactory getClientFactory() {
		return clientFactory;
	}

	@Override
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}

	@Override
	public void compPicked(Long id) {
		// TODO Auto-generated method stub

	}


	@Override
	public void flushAllPipelineJobs() {
		clientFactory.flushAllPipelineJobs();

	}



	@Override
	public void createContent() {
		clientFactory.createContent();
	}


	@Override
	public void cleanUp() {
		getMenuItemDelegate().cleanUp();

	}

	private MenuItemDelegate getMenuItemDelegate() {
		if (menuItemDelegate == null) {
			menuItemDelegate = new MenuItemDelegate(clientFactory);
		}

		return menuItemDelegate;
	}

	@Override
	public void processSeriesConfig(final Long seriesId) {
		// the processing has been kicked off in the View, we just need to start polling to see if it is done yet
		// we keep checking until the seriesConfig moves from state PENDING to something else or the activity ends (mayStop) or the lastRun timestamp changes
		final Date start = new Date();

		if (t == null) {
			t = new Timer() {
				@Override
				public void run() {
					clientFactory.getRpcService().getSeriesConfiguration(seriesId, new AsyncCallback<ISeriesConfiguration>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Failed fetching Series Conf: " + caught.getMessage());
						}

						@Override
						public void onSuccess(final ISeriesConfiguration result) {
							boolean ran = false;
							if (result!= null && result.getLastRun() != null)
								ran = result.getLastRun().after(start);
							if (result != null && result.getStatus() != null && result.getLastRun() != null && (!result.getStatus().equals(ISeriesConfiguration.Status.PENDING) || ran)) {
								view.updateSeriesConfigurationRow(result);  
								t = null;
							} else {
								// go again
								t.schedule(5000);
							}
						}
					});
				}
			};
		}

		// only run it if we 
		t.schedule(5000);
	}

	@Override
	public void editSeriesConfig(ISeriesConfiguration seriesConf) {
		clientFactory.getRpcService().saveSeriesConfiguration(seriesConf, new AsyncCallback<ISeriesConfiguration>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed saving Series Conf: " + caught.getMessage());
			}

			@Override
			public void onSuccess(ISeriesConfiguration result) {
				clientFactory.getSeriesView().updateSeriesConfigurationRow(result);			
			}
			
		});
	}


	@Override
	public Boolean deleteSeriesConfig(Long seriesId) {
		SeriesPlace place = new SeriesPlace("srsly");
		clientFactory.getPlaceController().goTo(place);
		return null;
	}

	@Override
	public void showConfigPopup(ISeriesConfiguration config) {
		clientFactory.getSeriesConfigrPopupView().setPresenter(this);
		if (config == null) {
			// they want a new one
			clientFactory.getRpcService().getSeriesConfiguration(null, new AsyncCallback<ISeriesConfiguration>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Failed saving Series Conf: " + caught.getMessage());
				}

				@Override
				public void onSuccess(ISeriesConfiguration result) {
					clientFactory.getSeriesConfigrPopupView().setConfig(result);	
					((DialogBox) clientFactory.getSeriesConfigrPopupView()).center();
				}
				
			});
			
		} else {
			// edit existing
			clientFactory.getSeriesConfigrPopupView().setConfig(config);	
			((DialogBox) clientFactory.getSeriesConfigrPopupView()).center();
		}
	}

	@Override
	public void onSaveConfigClicked(ISeriesConfiguration config) {
		((DialogBox) clientFactory.getSeriesConfigrPopupView()).hide();
		clientFactory.getRpcService().saveSeriesConfiguration(config, new AsyncCallback<ISeriesConfiguration>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed saving Series Conf: " + caught.getMessage());
			}

			@Override
			public void onSuccess(ISeriesConfiguration result) {
				clientFactory.getSeriesView().updateSeriesConfigurationRow(result);			
			}
			
		});
		
	}

	@Override
	public void onCancelConfigClicked() {
		((DialogBox) clientFactory.getSeriesConfigrPopupView()).hide();
		
	}

}
