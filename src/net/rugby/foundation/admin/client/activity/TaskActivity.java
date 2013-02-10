package net.rugby.foundation.admin.client.activity;

import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DialogBox;
import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.place.AdminTaskPlace;
import net.rugby.foundation.admin.client.ui.AdminView;
import net.rugby.foundation.admin.client.ui.playermatchstatspopup.PlayerMatchStatsPopupView.PlayerMatchStatsPopupViewPresenter;
import net.rugby.foundation.admin.client.ui.playerpopup.PlayerPopupView;
import net.rugby.foundation.admin.client.ui.task.TaskView.TaskViewPresenter;
import net.rugby.foundation.admin.shared.EditPlayerAdminTask;
import net.rugby.foundation.admin.shared.EditPlayerMatchStatsAdminTask;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;
import net.rugby.foundation.model.shared.IPlayerMatchStats;

public class TaskActivity extends AbstractActivity implements  
AdminView.Presenter, PlayerPopupView.Presenter<IPlayer>,
PlayerMatchStatsPopupViewPresenter<IPlayerMatchStats>, TaskViewPresenter<IAdminTask> { 
	/**
	 * Used to obtain views, eventBus, placeController.
	 * Alternatively, could be injected via GIN.
	 */
	private ClientFactory clientFactory;
	AdminView view = null;
	private AdminTaskPlace adminTaskPlace;
	SelectionModel<IAdminTask> selectionModel;
	int index; // the task line item number
	private IAdminTask target;
	public TaskActivity(AdminTaskPlace place, ClientFactory clientFactory) {
		selectionModel = new SelectionModel<IAdminTask>();

		this.clientFactory = clientFactory;
		view = clientFactory.getAdminView();
		clientFactory.getPlayerPopupView().setPresenter(this);
		// Select the tab corresponding to the token value
		if (place.getToken() != null) {
			view.selectTab(2);

			adminTaskPlace = place;
		}
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {

		view.setPresenter(this);
		clientFactory.getPlayerPopupView().setPresenter(this);
		clientFactory.getPlayerMatchStatsPopupView().setPresenter(this);

		panel.setWidget(view.asWidget());

		if (adminTaskPlace != null) {
			if (adminTaskPlace.getTaskId() != null) {
				clientFactory.getRpcService().getAllOpenAdminTasks( new AsyncCallback<List<IAdminTask>>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to fetch admin tasks");
					}

					@Override
					public void onSuccess(List<IAdminTask> result) {
						view.getTaskView().showList(result);

						IAdminTask target = null;
						index = 0;
						// find the task specified in the url
						for (IAdminTask task : result) {
							if (task.getId().toString().equals(adminTaskPlace.getTaskId())) {
								target = task;
								break;
							}
							++index;
						}

						// if we found it, go to the right editor for it
						if (target != null) {
							showTask(index,target);

						} 
					}
				});
			}
		}

	}



	@Override
	public Boolean onItemClicked(IAdminTask c, int i) {
		showTask(i,c);
		return null;
	}
	@Override
	public void deleteSelected() {
		
		clientFactory.getRpcService().deleteTasks(selectionModel.getSelectedItems(), new AsyncCallback<List<IAdminTask>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to delete tasks");
			}

			@Override
			public void onSuccess(List<IAdminTask> result) {
				view.getTaskView().showList(result);
			}
		});
	}
	@Override
	public void showTask(int i, IAdminTask target) {
		this.target = target;
		this.index = i;
		if (target.getAction().equals(IAdminTask.Action.EDITPLAYER)) {	
			assert (target instanceof EditPlayerAdminTask);
			clientFactory.getRpcService().getPlayer(((EditPlayerAdminTask)target).getPlayerId(), new AsyncCallback<IPlayer>() {
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Failed to fetch player to edit");
				}

				@Override
				public void onSuccess(IPlayer result) {
					clientFactory.getPlayerPopupView().setPlayer(result);
					((DialogBox)clientFactory.getPlayerPopupView()).center();
				}
			});
		}
		else if (target.getAction().equals(IAdminTask.Action.EDITPLAYERMATCHSTATS)) {
			assert (target instanceof EditPlayerMatchStatsAdminTask);
			clientFactory.getRpcService().getPlayerMatchStats(((EditPlayerMatchStatsAdminTask)target).getPlayerMatchStatsId(), new AsyncCallback<IPlayerMatchStats>() {
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Failed to fetch player match stats to edit");
				}

				@Override
				public void onSuccess(IPlayerMatchStats result) {
					clientFactory.getPlayerMatchStatsPopupView().setTarget(result);
					((DialogBox)clientFactory.getPlayerPopupView()).center();
				}
			});
		} else if (target.getAction().equals(IAdminTask.Action.EDITTEAMMATCHSTATS)) {
//							view.getTaskView().editPlayerMatchStats(result, index);
		}
	}

	@Override
	public ClientFactory getClientFactory() {
		return clientFactory;
	}
	
	@Override
	public void onSaveEditPlayerClicked(IPlayer player) {
		clientFactory.getRpcService().savePlayer(player, target, new AsyncCallback<IPlayer>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to save player");
			}

			@Override
			public void onSuccess(IPlayer result) {
				((DialogBox)clientFactory.getPlayerPopupView()).hide();
				updateTask();
			}


		});

	}
	@Override
	public void onCancelEditPlayerClicked() {
		((DialogBox)clientFactory.getPlayerPopupView()).hide();

	}
	@Override
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}

	@Override
	public Boolean onItemSelected(IAdminTask c) {
		if (selectionModel.isSelected(c)) {
			selectionModel.removeSelection(c);
		}

		else {
			selectionModel.addSelection(c);
		}

		return true;
	}

	@Override
	public Boolean isSelected(IAdminTask c) {
		return selectionModel.isSelected(c);
	}
	
	private void updateTask() {
		// get the updated task and display it
		clientFactory.getRpcService().getTask(target.getId(), new AsyncCallback<IAdminTask>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to save player");
			}
			
			@Override
			public void onSuccess(IAdminTask result) {
				view.getTaskView().updateTaskRow(index, result);
			}
		});
	}

	@Override
	public void onSavePlayerMatchStatsClicked(IPlayerMatchStats pms) {
		clientFactory.getRpcService().savePlayerMatchStats(pms, target, new AsyncCallback<IPlayerMatchInfo>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to save player match stats");
			}

			@Override
			public void onSuccess(IPlayerMatchInfo result) {
				((DialogBox)clientFactory.getPlayerMatchStatsPopupView()).hide();
				updateTask();
			}


		});		
	}

	@Override
	public void onCancelEditPlayerMatchStatsClicked() {
		((DialogBox)clientFactory.getPlayerMatchStatsPopupView()).hide();
	}

}
