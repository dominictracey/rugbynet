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
import net.rugby.foundation.admin.client.place.AdminCompPlace.Filter;
import net.rugby.foundation.admin.client.place.PortalPlace;
import net.rugby.foundation.admin.client.ui.AdminView;
import net.rugby.foundation.admin.client.ui.SmartBar;
import net.rugby.foundation.admin.client.ui.playerlistview.PlayerListView;
import net.rugby.foundation.admin.client.ui.playermatchstatspopup.PlayerMatchStatsPopupView.PlayerMatchStatsPopupViewPresenter;
import net.rugby.foundation.admin.client.ui.playerpopup.PlayerPopupView;
import net.rugby.foundation.admin.client.ui.portal.EditTTLInfo;
import net.rugby.foundation.admin.client.ui.portal.EditTTLInfo.EditTTLInfoPresenter;
import net.rugby.foundation.admin.client.ui.portal.PortalView;
import net.rugby.foundation.admin.client.ui.portal.PortalView.PortalViewPresenter;
import net.rugby.foundation.admin.client.ui.teammatchstatspopup.TeamMatchStatsPopupView;
import net.rugby.foundation.admin.client.ui.teammatchstatspopup.TeamMatchStatsPopupView.TeamMatchStatsPopupViewPresenter;
import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingQuery.Status;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.model.shared.RatingQuery;

public class PortalActivity extends AbstractActivity implements  
AdminView.Presenter, PlayerPopupView.Presenter<IPlayer>, SmartBar.Presenter,
PlayerMatchStatsPopupViewPresenter<IPlayerMatchStats>, PortalViewPresenter<IPlayerMatchInfo>,
PlayerListView.Listener<IPlayerMatchInfo>, EditTTLInfoPresenter, TeamMatchStatsPopupViewPresenter<ITeamMatchStats> { 
	/**
	 * Used to obtain views, eventBus, placeController.
	 * Alternatively, could be injected via GIN.
	 */
	private ClientFactory clientFactory;
	private PortalPlace place;
	SelectionModel<IPlayerMatchInfo> selectionModel;
	int index; // the task line item number
	private PortalView<IPlayerMatchInfo> view;
	private EditTTLInfo ttltext;

	protected boolean isTimeSeries= false;

	public PortalActivity(PortalPlace place, ClientFactory clientFactory) {
		selectionModel = new SelectionModel<IPlayerMatchInfo>();

		this.clientFactory = clientFactory;
		view = clientFactory.getPortalView();
		// Select the tab corresponding to the token value
		if (place.getToken() != null) {
			//			view.selectTab(2, false);

			this.place = place;
		}
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {

		view.setPresenter(this);
		panel.setWidget(view.asWidget());


		if (place != null) {
			clientFactory.getRpcService().getConfiguration(new AsyncCallback<ICoreConfiguration>() {
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Failed to fetch comps");
				}

				@Override
				public void onSuccess(ICoreConfiguration result) {
					view.setComps(result);

					clientFactory.getRpcService().fetchPositionList(new AsyncCallback<List<position>>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Failed to fetch position list");

						}

						@Override
						public void onSuccess(List<position> result) {
							view.setPositions(result);

							clientFactory.getRpcService().fetchCountryList(new AsyncCallback<List<ICountry>>() {

								@Override
								public void onFailure(Throwable caught) {
									Window.alert("Failed to fetch country list");

								}

								@Override
								public void onSuccess(List<ICountry> result) {
									view.setCountries(result);

									// now see if we have a query to display
									if (place != null && place.getqueryId() != null && !place.getqueryId().equals("null")) {
										clientFactory.getRpcService().getRatingQuery(Long.parseLong(place.getqueryId()), new  AsyncCallback<IRatingQuery>() {

											@Override
											public void onFailure(Throwable caught) {
												Window.alert("Problem finding the query with id " + place.getqueryId());

											}

											@Override
											public void onSuccess(IRatingQuery result) {
												view.setRatingQuery(result);
												if (result.getStatus() == Status.COMPLETE) {
													clientFactory.getRpcService().getRatingQueryResults(Long.parseLong(place.getqueryId()), new AsyncCallback<List<IPlayerMatchInfo>>() {

														@Override
														public void onFailure(Throwable caught) {
															Window.alert("Problem finding the query results with id " + place.getqueryId());
														}

														@Override
														public void onSuccess(List<IPlayerMatchInfo> result) {
															view.showAggregatedMatchInfo(result);
														}

													});
												} else if (result.getStatus() == Status.ERROR) {
													Window.alert("The query has terminated without delivering results. Check the server log for details.");
												} else if (result.getStatus() == Status.RUNNING || result.getStatus() == Status.NEW) {
													// keep checking
													goTo(new PortalPlace("queryId=" + place.getqueryId()));
												}
											}	
										});
									}
								}

							});

						}

					});

				}
			});

		}


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
		clientFactory.getPlayerMatchStatsPopupView().setPresenter(this);

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
	public void onSavePlayerMatchStatsClicked(final IPlayerMatchStats pms) {
		clientFactory.getPlayerMatchStatsPopupView().setPresenter(this);

		clientFactory.getRpcService().savePlayerMatchStats(pms, null, new AsyncCallback<IPlayerMatchInfo>() {

			@Override
			public void onFailure(Throwable caught) {

				Window.alert("Player Stats not saved: " + caught.getMessage());
			}

			@Override
			public void onSuccess(IPlayerMatchInfo result) {

				clientFactory.getPlayerListView().updatePlayerMatchStats(result);
				((DialogBox) clientFactory.getPlayerMatchStatsPopupView()).hide();

			}
		});			
	}

	@Override
	public void onCancelEditPlayerMatchStatsClicked() {
		((DialogBox)clientFactory.getPlayerMatchStatsPopupView()).hide();

	}

	@Override
	public void onSaveEditPlayerClicked(IPlayer player) {

		clientFactory.getRpcService().savePlayer(player, null, new AsyncCallback<IPlayer>() {

			@Override
			public void onFailure(Throwable caught) {

				Window.alert("Player not saved: " + caught.getMessage());
			}

			@Override
			public void onSuccess(IPlayer result) {

				Window.alert("Player saved");
				((DialogBox) clientFactory.getPlayerPopupView()).hide();


			}
		});		


	}

	@Override
	public void onCancelEditPlayerClicked() {
		((DialogBox)clientFactory.getPlayerPopupView()).hide();

	}


	@Override
	public boolean onItemSelected(IPlayerMatchInfo c) {
		if (selectionModel.isSelected(c)) {
			selectionModel.removeSelection(c);
		}

		else {
			selectionModel.addSelection(c);
		}

		return true;
	}

	@Override
	public boolean isSelected(IPlayerMatchInfo c) {
		return selectionModel.isSelected(c);
	}

	@Override
	public void showEditPlayer(IPlayerMatchInfo player) {
		clientFactory.getPlayerPopupView().setPresenter(this);

		clientFactory.getRpcService().getPlayer(player.getPlayerMatchStats().getPlayerId(), new AsyncCallback<IPlayer>() {

			@Override
			public void onFailure(Throwable caught) {

				Window.alert("Player info not fetched for editing: " + caught.getMessage());
			}

			@Override
			public void onSuccess(IPlayer result) {
				clientFactory.getPlayerPopupView().setPlayer(result);
				((DialogBox) clientFactory.getPlayerPopupView()).center();
			}
		});	


	}

	@Override
	public void showEditStats(IPlayerMatchInfo info) {
		clientFactory.getPlayerMatchStatsPopupView().setPresenter(this);

		clientFactory.getPlayerMatchStatsPopupView().setTarget(info.getPlayerMatchStats());
		((DialogBox) clientFactory.getPlayerMatchStatsPopupView()).center();
	}

	@Override
	public void showEditRating(IPlayerMatchInfo player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void flushAllPipelineJobs() {
		clientFactory.flushAllPipelineJobs();

	}

	@Override
	public void createTopTenList(TopTenSeedData data) {
		if (ttltext == null) {
			ttltext = new EditTTLInfo();
		}

		ttltext.setText("Top Ten List Properties");
		data.setTitle("Top Ten Performances for ");
		ttltext.setPresenter(this);
		ttltext.showTTI(data);
	}

	@Override
	public void saveTTIText(final TopTenSeedData tti) {
		ttltext.hide();
		clientFactory.getRpcService().createTopTenList(tti, new AsyncCallback<TopTenSeedData>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Problem creating top ten list: " + caught.getMessage());
			}

			@Override
			public void onSuccess(TopTenSeedData result) {
				if (result != null) {
					Window.alert("Top ten list created: " + result.getTitle());
				} else {
					Window.alert("Problem creating top ten list for " + tti.getTitle());
				}
			}
		});	

	}

	@Override
	public void cancelTTITextEdit(TopTenSeedData tti) {
		ttltext.hide();

	}

	@Override
	public void createContent() {
		clientFactory.createContent();
	}

	@Override
	public void portalViewCompSelected(long compId) {
		clientFactory.getRpcService().getComp(compId, new AsyncCallback<ICompetition>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Problem fetching comp: " + caught.getMessage());
			}

			@Override
			public void onSuccess(ICompetition result) {
				if (result != null) {
					view.setComp(result, false);
				}
			}
		});	

	}

	@Override
	public void showEditTeamStats(IPlayerMatchInfo pmi) {

		clientFactory.getTeamMatchStatsPopupView().setPresenter(this);
		clientFactory.getRpcService().getTeamMatchStats(pmi.getPlayerMatchStats().getMatchId(), pmi.getPlayerMatchStats().getTeamId(), new AsyncCallback<ITeamMatchStats>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to fetch team match stats to edit");
			}

			@Override
			public void onSuccess(ITeamMatchStats result) {

				clientFactory.getTeamMatchStatsPopupView().setTarget(result);
				((DialogBox)clientFactory.getTeamMatchStatsPopupView()).center();
			}
		});
	}

	/**
	 * 
	 * @param Team
	 */
	@Override
	public void onSaveTeamMatchStatsClicked(ITeamMatchStats tms) {
		clientFactory.getRpcService().saveTeamMatchStats(tms, null, new AsyncCallback<ITeamMatchStats>() {

			@Override
			public void onFailure(Throwable caught) {

				Window.alert("Team Match Stats not saved: " + caught.getMessage());
			}

			@Override
			public void onSuccess(ITeamMatchStats result) {
				((DialogBox) clientFactory.getTeamMatchStatsPopupView()).hide();
			}
		});		}



	@Override
	public void onCancelEditTeamMatchStatsClicked() {
		((DialogBox)clientFactory.getTeamMatchStatsPopupView()).hide();

	}



	@Override
	public void onRefetchEditTeamMatchStatsClicked(ITeamMatchStats target) {
		clientFactory.getRpcService().refetchTeamMatchStats(target, new AsyncCallback<ITeamMatchStats>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to fetch Team Match Stats, see logs for details");
			}

			@Override
			public void onSuccess(ITeamMatchStats result) {
				clientFactory.getTeamMatchStatsPopupView().setTarget(result);
				((DialogBox)clientFactory.getTeamMatchStatsPopupView()).center();
			}
		});

	}

	@Override
	public void submitPortalQuery(List<Long> compIds, List<Long> roundIds, List<position> posis, List<Long> countryIds, List<Long> teamIds) {

		//if (!isTimeSeries) {
			clientFactory.getRpcService().createRatingQuery(compIds, roundIds, posis, countryIds, teamIds, new AsyncCallback<IRatingQuery>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Query failed: " + caught.getLocalizedMessage());
				}

				@Override
				public void onSuccess(IRatingQuery result) {
					//clientFactory.getPortalView().setRatingQuery(result);	
					goTo(new PortalPlace("queryId=" + result.getId().toString()));
				}
			});
		//}

	}

	@Override
	public void deleteQuery(IRatingQuery query) {
		clientFactory.getRpcService().deleteRatingQuery(query, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Delete query failed: " + caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Boolean result) {
				//clientFactory.getPortalView().setRatingQuery(result);	
				if (result) {
					Window.alert("Query deleted successfully");
				} else {
					Window.alert("Query not deleted successfully");
				}
				view.clear();
				goTo(new PortalPlace(""));
			}
		});

	}

	@Override
	public void portalViewCompPopulate(Long id) {
		clientFactory.getRpcService().getComp(id, new AsyncCallback<ICompetition>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Problem fetching comp: " + caught.getMessage());
			}

			@Override
			public void onSuccess(ICompetition result) {
				if (result != null) {
					view.setComp(result, true);
				}
			}
		});			
	}

	@Override
	public void setTimeSeries(boolean isTrue) {
		isTimeSeries = isTrue;		
	}

}
