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
import net.rugby.foundation.admin.client.ui.teammatchstatspopup.TeamMatchStatsPopupView.TeamMatchStatsPopupViewPresenter;
import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingQuery.Status;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema;

public class PortalActivity extends AbstractActivity implements  
AdminView.Presenter, PlayerPopupView.Presenter<IPlayer>, SmartBar.Presenter,
PlayerMatchStatsPopupViewPresenter<IPlayerMatchStats>, PortalViewPresenter<IPlayerRating>,
PlayerListView.Listener<IPlayerRating>, PlayerListView.RatingListener<IPlayerRating>, EditTTLInfoPresenter, TeamMatchStatsPopupViewPresenter<ITeamMatchStats> { 
	/**
	 * Used to obtain views, eventBus, placeController.
	 * Alternatively, could be injected via GIN.
	 */
	private ClientFactory clientFactory;
	private PortalPlace place;
	SelectionModel<IPlayerRating> selectionModel;
	int index; // the task line item number
	private PortalView<IPlayerRating> view;
	private EditTTLInfo ttltext;

	protected boolean isTimeSeries= false;
	private MenuItemDelegate menuItemDelegate;


	public PortalActivity(PortalPlace place, ClientFactory clientFactory) {
		selectionModel = new SelectionModel<IPlayerRating>();

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

									clientFactory.getRpcService().getScrumSchemaList(new AsyncCallback<List<ScrumMatchRatingEngineSchema>>() {

										@Override
										public void onFailure(Throwable caught) {
											Window.alert("Failed to fetch schema list");

										}

										@Override
										public void onSuccess(List<ScrumMatchRatingEngineSchema> result) {

											view.setSchemas(result);
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
															final boolean isTimeSeries = result.isTimeSeries();
															//if (!result.isTimeSeries()) {
															clientFactory.getRpcService().getRatingQueryResults(Long.parseLong(place.getqueryId()), new AsyncCallback<List<IPlayerRating>>() {

																private List<IPlayerRating> currentList;

																@Override
																public void onFailure(Throwable caught) {
																	Window.alert("Problem finding the query results with id " + place.getqueryId());
																}

																@Override
																public void onSuccess(List<IPlayerRating> result) {
																	if (isTimeSeries)
																		view.showTimeWeightedMatchInfo(result);
																	else
																		view.showAggregatedMatchInfo(result);
																}

															});
															//} 
															//													else {
															//														clientFactory.getRpcService().getTimeSeriesRatingQueryResults(Long.parseLong(place.getqueryId()), new AsyncCallback<List<IPlayerRating>>() {
															//
															//															@Override
															//															public void onFailure(Throwable caught) {
															//																Window.alert("Problem finding the query results with id " + place.getqueryId());
															//															}
															//
															//															@Override
															//															public void onSuccess(List<IPlayerRating> result) {
															//																view.showTimeWeightedMatchInfo(result);
															//															}
															//
															//														});
															//													}
														} else if (result.getStatus() == Status.ERROR) {
															Window.alert("The query has terminated without delivering results. Check the server log for details.");
														} else if (result.getStatus() == Status.RUNNING || result.getStatus() == Status.NEW) {
															// keep checking
															goTo(new PortalPlace("queryId=" + place.getqueryId()));
														}
													}	
												});
											} else {
												view.showAggregatedMatchInfo(null);
											}
										}
									});
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

		clientFactory.getRpcService().savePlayerMatchStats(pms, null, new AsyncCallback<IPlayerRating>() {

			@Override
			public void onFailure(Throwable caught) {

				Window.alert("Player Stats not saved: " + caught.getMessage());
			}

			@Override
			public void onSuccess(IPlayerRating result) {

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
	public boolean onItemSelected(IPlayerRating c) {
		if (selectionModel.isSelected(c)) {
			selectionModel.removeSelection(c);
		}

		else {
			selectionModel.addSelection(c);
		}

		return true;
	}

	@Override
	public boolean isSelected(IPlayerRating c) {
		return selectionModel.isSelected(c);
	}

	@Override
	public void showEditPlayer(IPlayerRating player) {
		showEditPlayer(player.getPlayerId());
	}


	@Override
	public void showEditPlayerFromTS(IPlayerRating player) {
		showEditPlayer(player.getPlayerId());
	}

	private void showEditPlayer(Long playerId) {
		clientFactory.getPlayerPopupView().setPresenter(this);

		clientFactory.getRpcService().getPlayer(playerId, new AsyncCallback<IPlayer>() {

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
	public void showEditStats(IPlayerRating info) {

		clientFactory.getPlayerMatchStatsPopupView().setPresenter(this);

		clientFactory.getPlayerMatchStatsPopupView().setTarget(info.getMatchStats().get(index));
		((DialogBox) clientFactory.getPlayerMatchStatsPopupView()).center();

	}

	@Override
	public void showEditStats(IPlayerRating info, int index) {
		if (info != null && info.getMatchStats() != null && info.getMatchStats().size() > index) {
			clientFactory.getPlayerMatchStatsPopupView().setPresenter(this);

			clientFactory.getRpcService().getPlayerMatchStats(info.getMatchStatIds().get(index), new AsyncCallback<IPlayerMatchStats>() {
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Problem creating top ten list: " + caught.getMessage());
				}

				@Override
				public void onSuccess(IPlayerMatchStats result) {
					clientFactory.getPlayerMatchStatsPopupView().setTarget(result);
					((DialogBox) clientFactory.getPlayerMatchStatsPopupView()).center();
				}
			});
		} else {
			Window.alert("Invalid attempt to edit stats");
		}
	}

	@Override
	public void showEditRating(IPlayerRating player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void flushAllPipelineJobs() {
		clientFactory.flushAllPipelineJobs();

	}

	@Override
	public void createTopTenList(final TopTenSeedData data) {
		if (ttltext == null) {
			ttltext = new EditTTLInfo();
		}

		final EditTTLInfoPresenter _this = this;

		Core.getCore().getConfiguration(new AsyncCallback<ICoreConfiguration>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(ICoreConfiguration result) {

				ttltext.setText("Top Ten List Properties");
				data.setTitle("Top Ten Performances for ");

				ttltext.removePlayers();

				for (int i=0; i<10; ++i)
				{
					if (view.getCurrentList() != null && view.getCurrentList().size() > i) {
						IPlayer p = view.getCurrentList().get(i).getPlayer();
						ttltext.addTwitterPlayer(p);				
					}
				}
				ttltext.setComps(result);
				ttltext.setPresenter(_this);
				ttltext.showTTI(data);
			}
		});
	}

	@Override
	public void saveTTIText(final TopTenSeedData tti) {
		ttltext.hide();
		clientFactory.getRpcService().createTopTenList(tti, ttltext.getTwitterDictionary(), new AsyncCallback<TopTenSeedData>() {

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
		if (compId > 0) {
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
	}

	@Override
	public void showEditTeamStats(IPlayerRating pr) {

		// assumption is that this is only for the case where there is rating over 1 match
		assert(pr != null);
		assert(pr.getMatchStatIds() != null);
		assert(pr.getMatchStatIds().size() == 1);
		if (pr != null && pr.getMatchStatIds() != null && pr.getMatchStatIds().size() == 1) {
			clientFactory.getTeamMatchStatsPopupView().setPresenter(this);
			clientFactory.getRpcService().getTeamMatchStats(pr.getMatchStats().get(0).getMatchId(), pr.getMatchStats().get(0).getTeamId(), new AsyncCallback<ITeamMatchStats>() {
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
		} else {
			Window.alert("Invalid attempt to edit team match stats");
		}
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
	public void submitPortalQuery(List<Long> compIds, List<Long> roundIds, List<position> posis, List<Long> countryIds, List<Long> teamIds, Long schemaId,
			Boolean scaleTime, Boolean scaleComp, Boolean scaleStanding, Boolean instrument) {

		//if (!isTimeSeries) {
		clientFactory.getRpcService().createRatingQuery(compIds, roundIds, posis, countryIds, teamIds, schemaId,
				scaleTime, scaleComp, scaleStanding, instrument, new AsyncCallback<IRatingQuery>() {

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
	public void rerunQuery(IRatingQuery rq) {
		if (rq != null) {
			if (rq.getRatingMatrixId() == null) {
				Window.alert("Only Series Queries (not Ad Hoc) may be re-run");
				return;
			} else {
				clientFactory.getRpcService().rerunRatingQuery(rq.getId(), new AsyncCallback<IRatingQuery>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Query re-run failed: " + caught.getLocalizedMessage());
					}

					@Override
					public void onSuccess(IRatingQuery result) {
						goTo(new PortalPlace("queryId=" + result.getId().toString()));
					}
				});
			}
		}

	}

}
