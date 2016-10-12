package net.rugby.foundation.admin.client.activity;

import java.util.List;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.place.AdminTaskPlace;
import net.rugby.foundation.admin.client.ui.AdminView;
import net.rugby.foundation.admin.client.ui.SmartBar;
import net.rugby.foundation.admin.client.ui.playermatchstatspopup.PlayerMatchStatsPopupView.PlayerMatchStatsPopupViewPresenter;
import net.rugby.foundation.admin.client.ui.playerpopup.PlayerPopupView;
import net.rugby.foundation.admin.client.ui.task.TaskView;
import net.rugby.foundation.admin.client.ui.task.TaskView.TaskViewPresenter;
import net.rugby.foundation.admin.client.ui.teammatchstatspopup.TeamMatchStatsPopupView.TeamMatchStatsPopupViewPresenter;
import net.rugby.foundation.admin.shared.EditPlayerAdminTask;
import net.rugby.foundation.admin.shared.EditPlayerMatchStatsAdminTask;
import net.rugby.foundation.admin.shared.EditTeamMatchStatsAdminTask;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.ITeamMatchStats;

import org.gwtbootstrap3.extras.notify.client.constants.NotifyType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DialogBox;

public class TaskActivity extends AbstractActivity implements  
AdminView.Presenter, PlayerPopupView.Presenter<IPlayer>, SmartBar.Presenter,
PlayerMatchStatsPopupViewPresenter<IPlayerMatchStats>, TeamMatchStatsPopupViewPresenter<ITeamMatchStats>, TaskViewPresenter<IAdminTask> { 
	/**
	 * Used to obtain views, eventBus, placeController.
	 * Alternatively, could be injected via GIN.
	 */
	private ClientFactory clientFactory;
	private AdminTaskPlace adminTaskPlace;
	SelectionModel<IAdminTask> selectionModel;
	int index; // the task line item number
	private IAdminTask target;
	private TaskView<IAdminTask> view;
	private MenuItemDelegate menuItemDelegate;
	
	public TaskActivity(AdminTaskPlace place, ClientFactory clientFactory) {
		selectionModel = new SelectionModel<IAdminTask>();

		this.clientFactory = clientFactory;
		view = clientFactory.getTaskView();
		// Select the tab corresponding to the token value
		if (place.getToken() != null) {
//			view.selectTab(2, false);

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
						view.showList(result);

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
			} else /*if (adminTaskPlace.getFilter() != null)*/ {
				clientFactory.getRpcService().getAllOpenAdminTasks( new AsyncCallback<List<IAdminTask>>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to fetch admin tasks");
					}

					@Override
					public void onSuccess(List<IAdminTask> result) {
						view.showList(result);

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
				view.showList(result);
			}
		});
	}
	@Override
	public void showTask(int i, IAdminTask target) {
		this.target = target;
		this.index = i;
		Notify.notify(target.getDetails());
		if (target.getAction().equals(IAdminTask.Action.EDITPLAYER) || target.getAction().equals(IAdminTask.Action.EDITPLAYERTWITTER)) {	
			assert (target instanceof EditPlayerAdminTask);
			clientFactory.getPlayerPopupView().setPresenter(this);
			clientFactory.getRpcService().getPlayer(((EditPlayerAdminTask)target).getPlayerId(), new AsyncCallback<IPlayer>() {
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Failed to fetch player to edit");
				}

				@Override
				public void onSuccess(IPlayer result) {
					if (result != null) {
						clientFactory.getPlayerPopupView().setPlayer(result);
					} else {
						clientFactory.getPlayerPopupView().clear();
					}
					((DialogBox)clientFactory.getPlayerPopupView()).center();
				}
			});
		}
		else if (target.getAction().equals(IAdminTask.Action.EDITPLAYERMATCHSTATS)) {
			assert (target instanceof EditPlayerMatchStatsAdminTask);
			clientFactory.getPlayerMatchStatsPopupView().setPresenter(this);
			clientFactory.getRpcService().getPlayerMatchStats(((EditPlayerMatchStatsAdminTask)target).getPlayerMatchStatsId(), new AsyncCallback<IPlayerMatchStats>() {
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Failed to fetch player match stats to edit");
				}

				@Override
				public void onSuccess(IPlayerMatchStats result) {
					if (result != null) {
						clientFactory.getPlayerMatchStatsPopupView().setTarget(result);
					} else {
						clientFactory.getPlayerMatchStatsPopupView().clear();
					}
					((DialogBox)clientFactory.getPlayerMatchStatsPopupView()).center();
				}
			});
		} else if (target.getAction().equals(IAdminTask.Action.EDITTEAMMATCHSTATS)) {
			assert (target instanceof EditTeamMatchStatsAdminTask);
			clientFactory.getTeamMatchStatsPopupView().setPresenter(this);
			clientFactory.getRpcService().getTeamMatchStats(((EditTeamMatchStatsAdminTask)target).getMatchId(), ((EditTeamMatchStatsAdminTask)target).getTeamId(), new AsyncCallback<ITeamMatchStats>() {
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Failed to fetch team match stats to edit");
				}

				@Override
				public void onSuccess(ITeamMatchStats result) {
					if (result != null) {
						clientFactory.getTeamMatchStatsPopupView().setTarget(result);
					} else {
						clientFactory.getTeamMatchStatsPopupView().clear();
					}
					((DialogBox)clientFactory.getTeamMatchStatsPopupView()).center();
				}
			});		}
	}

	@Override
	public ClientFactory getClientFactory() {
		return clientFactory;
	}
	
	@Override
	public void onSaveEditPlayerClicked(IPlayer player) {
		clientFactory.getPlayerPopupView().setPresenter(this);
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
				view.updateTaskRow(index, result);
			}
		});
	}

	@Override
	public void onSavePlayerMatchStatsClicked(IPlayerMatchStats pms) {
		clientFactory.getRpcService().savePlayerMatchStats(pms, target, new AsyncCallback<IPlayerRating>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to save player match stats");
			}

			@Override
			public void onSuccess(IPlayerRating result) {
				((DialogBox)clientFactory.getPlayerMatchStatsPopupView()).hide();
				updateTask();
			}


		});		
	}

	@Override
	public void onCancelEditPlayerMatchStatsClicked() {
		((DialogBox)clientFactory.getPlayerMatchStatsPopupView()).hide();
	}

	@Override
	public void compPicked(Long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showPlayerPopup(IPlayerMatchStats target, PlayerPopupView.Presenter<IPlayer> presenter) {
		// note the popup dialog will call back to the presenter (which may not be this activity)
		if (presenter == null) {
			presenter = this;
		}
		clientFactory.getPlayerPopupView().setPresenter(presenter);
		clientFactory.getRpcService().getPlayer(target.getPlayerId(), new AsyncCallback<IPlayer>() {
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

	@Override
	public void onRefetchEditPlayerMatchStatsClicked(IPlayerMatchStats target) {
		clientFactory.getRpcService().refetchPlayerMatchStats(target, new AsyncCallback<IPlayerMatchStats>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to fetch playerMatchStats, see logs for details");
			}

			@Override
			public void onSuccess(IPlayerMatchStats result) {
				clientFactory.getPlayerMatchStatsPopupView().setTarget(result);
				((DialogBox)clientFactory.getPlayerMatchStatsPopupView()).center();
			}
		});
		
	}
	
	@Override
	public void flushAllPipelineJobs() {
		clientFactory.flushAllPipelineJobs();
		
	}

	@Override
	public void onSaveTeamMatchStatsClicked(ITeamMatchStats tms) {
		clientFactory.getRpcService().saveTeamMatchStats(tms, target, new AsyncCallback<ITeamMatchStats>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to save team match stats: " + caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(ITeamMatchStats result) {
				((DialogBox)clientFactory.getTeamMatchStatsPopupView()).hide();
				updateTask();
			}


		});	
		
	}

	@Override
	public void onCancelEditTeamMatchStatsClicked() {
		((DialogBox)clientFactory.getTeamMatchStatsPopupView()).hide();
	}

	@Override
	public void onRefetchEditTeamMatchStatsClicked(ITeamMatchStats target) {
		clientFactory.getRpcService().refetchTeamMatchStats(target, new AsyncCallback<ITeamMatchStats>() {
			@Override
			public void onFailure(Throwable caught) {
				Notify.notify("Failed to fetch Team Match Stats, see logs for details", NotifyType.DANGER);
			}

			@Override
			public void onSuccess(ITeamMatchStats result) {
				clientFactory.getTeamMatchStatsPopupView().setTarget(result);
				((DialogBox)clientFactory.getTeamMatchStatsPopupView()).center();
				Notify.notify("Team match stats successfully refetched for " + result.getTeamAbbr(), NotifyType.SUCCESS);
			}
		});
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

}
