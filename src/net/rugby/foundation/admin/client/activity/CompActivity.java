package net.rugby.foundation.admin.client.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.place.AdminCompPlace;
import net.rugby.foundation.admin.client.ui.AdminView;
import net.rugby.foundation.admin.client.ui.CompetitionView;
import net.rugby.foundation.admin.client.ui.EditComp;
import net.rugby.foundation.admin.client.ui.EditMatch;
import net.rugby.foundation.admin.client.ui.EditRound;
import net.rugby.foundation.admin.client.ui.EditRound.RoundPresenter;
import net.rugby.foundation.admin.client.ui.EditTeam;
import net.rugby.foundation.admin.client.ui.SmartBar;
import net.rugby.foundation.admin.client.ui.matchratingengineschemapopup.MatchRatingEngineSchemaPopupView.MatchRatingEngineSchemaPopupViewPresenter;
import net.rugby.foundation.admin.client.ui.playerlistview.PlayerListView;
import net.rugby.foundation.admin.client.ui.playermatchstatspopup.PlayerMatchStatsPopupView.PlayerMatchStatsPopupViewPresenter;
import net.rugby.foundation.admin.client.ui.playerpopup.PlayerPopupView;
import net.rugby.foundation.admin.client.ui.teammatchstatspopup.TeamMatchStatsPopupView.TeamMatchStatsPopupViewPresenter;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICompetition.CompetitionType;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.Status;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema20130713;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DialogBox;

/**
 * Activities are started and stopped by an ActivityManager associated with a container Widget.
 */
public class CompActivity extends AbstractActivity implements  
CompetitionView.Presenter, EditTeam.Presenter, EditComp.Presenter, 
EditMatch.Presenter, PlayerListView.Listener<IPlayerRating>, PlayerPopupView.Presenter<IPlayer>,
PlayerMatchStatsPopupViewPresenter<IPlayerMatchStats>, TeamMatchStatsPopupViewPresenter<ITeamMatchStats>, 
SmartBar.Presenter, SmartBar.SchemaPresenter, MatchRatingEngineSchemaPopupViewPresenter<ScrumMatchRatingEngineSchema20130713>, RoundPresenter {
	/**
	 * Used to obtain views, eventBus, placeController.
	 * Alternatively, could be injected via GIN.
	 */
	private ClientFactory clientFactory;
	CompetitionView view = null;
	private String url;
	private SelectionModel<IPlayerRating> selectionModel;
	private EditTeam et = null;  //@REX stupid
	private EditComp ec = null;  //@REX stupid
	private EditRound er = null; //@REX and yet I continue doing it...

	private List<ICompetition> comps = null;
	private EditMatch em;

	private Long currentCompId = null;
	private Long currentRoundId = null;
	private Long currentMatchId = null;

	// temp storage for building up new comp
	private List<ITeamGroup> teams = new ArrayList<ITeamGroup>();
	protected List<IRound> rounds = new ArrayList<IRound>();
	private PlayerListView<IPlayerRating> plv;
	private AdminCompPlace place;
	private boolean waiting = false; // used to see if matchStats fetching is complete
	private boolean ready = false;

	private MenuItemDelegate menuItemDelegate = null;
	
	public CompActivity(AdminCompPlace place, ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		view = clientFactory.getCompView();
		selectionModel = new SelectionModel<IPlayerRating>();
		this.place = place;
	}



	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		view.setPresenter(this);

		containerWidget.setWidget(view.asWidget());

		GWT.runAsync(new RunAsyncCallback() {

			@Override
			public void onSuccess() {


				if (!view.isAllSetup()) {
					clientFactory.getRpcService().getComps(place.getFilter(), new AsyncCallback<List<ICompetition>>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Problem getting comp list: " + caught.getLocalizedMessage());
						}

						@Override
						public void onSuccess(List<ICompetition> result) {
							comps = result;
							view.addComps(result);
							clientFactory.getRpcService().getContentList(true, new AsyncCallback<List<IContent>>() {

								@Override
								public void onFailure(Throwable caught) {
									Window.alert("Problem getting content list: " + caught.getLocalizedMessage());
								}

								@Override
								public void onSuccess(List<IContent> result) {
									view.getSmartBar().setContents(result, clientFactory);
									clientFactory.getRpcService().getScrumSchemaList(new AsyncCallback<List<ScrumMatchRatingEngineSchema>>() {

										@Override
										public void onFailure(Throwable caught) {
											Window.alert("Problem getting schema list: " + caught.getLocalizedMessage());
										}

										@Override
										public void onSuccess(List<ScrumMatchRatingEngineSchema> result) {
											view.setSchemaList(result);
										}
									});
								}
							});
						}
					});		
				}
			}

			@Override
			public void onFailure(Throwable reason) {
				// TODO Auto-generated method stub

			}

		});
	}


	/**
	 * @see AdminView.Presenter#goTo(Place)
	 */
	public void goTo(Place place) {
		view.showWait(true);
		view.setInitialized(false);
		clientFactory.getPlaceController().goTo(place);
	}

	@Override
	public void fetchCompetitionClicked(List<IRound> rounds, CompetitionType compType) {


		clientFactory.getRpcService().fetchCompetition(url, rounds, teams, compType, new AsyncCallback<ICompetition>() {

			@Override
			public void onFailure(Throwable caught) {


			}

			@Override
			public void onSuccess(ICompetition result) {
				view.showCompetition(result);

			}
		});

	}

	@Override
	public void saveCompetitionClicked(ICompetition comp, Map<String,ITeamGroup> teamMap) {
		comp.setTeams(teams);
		comp.setRounds(rounds);
		clientFactory.getRpcService().saveCompetition(comp, teamMap, new AsyncCallback<ICompetition>() {

			@Override
			public void onFailure(Throwable caught) {
				view.showStatus(caught.getMessage());
			}

			@Override
			public void onSuccess(ICompetition result) {
				view.showCompetition(result);

			}
		});			
	}

	@Override
	public void fetchTeamsClicked(String url, CompetitionType compType) {
		this.url = url;
		clientFactory.getRpcService().fetchTeams(url, compType, new AsyncCallback<Map<String, ITeamGroup>>() {

			@Override
			public void onFailure(Throwable caught) {
				view.showStatus(caught.getMessage());

			}

			@Override
			public void onSuccess(Map<String, ITeamGroup> result) {
				if (result != null) {
					for (ITeamGroup t: result.values()) {
						teams.add(t);
					}
					view.showTeams(result);
				}

			}
		});		
	}

	@Override
	public void fetchMatchesClicked(Map<String,ITeamGroup> teams, CompetitionType compType) {
		clientFactory.getRpcService().fetchMatches(url, teams, compType, new AsyncCallback<Map<String,IMatchGroup>>() {

			@Override
			public void onFailure(Throwable caught) {
				view.showStatus(caught.getMessage());
			}

			@Override
			public void onSuccess(Map<String, IMatchGroup> result) {
				view.showMatches(result);

			}
		});		
	}

	@Override
	public void fetchRoundsClicked(Map<String,IMatchGroup> matches, CompetitionType compType) {
		clientFactory.getRpcService().fetchRounds(url, matches, compType, new AsyncCallback<List<IRound> >() {

			@Override
			public void onFailure(Throwable caught) {
				view.showStatus(caught.getMessage());
			}

			@Override
			public void onSuccess(List<IRound> result) {
				rounds = result;
				view.showRounds(result);

			}
		});	
	}

	//	@Override
	//	public void saveWorkflowConfiguration(IWorkflowConfiguration wfc) {
	//		clientFactory.getRpcService().saveWorkflowConfig(wfc, new AsyncCallback<IWorkflowConfiguration>() {
	//
	//			@Override
	//			public void onFailure(Throwable caught) {
	//				view.getWorkflowConfig().showStatus(caught.getMessage());
	//			}
	//
	//			@Override
	//			public void onSuccess(IWorkflowConfiguration result) {
	//				view.getWorkflowConfig().showStatus("Success");
	//
	//			}
	//		});		
	//	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.CompetitionView.Presenter#roundClicked(java.lang.Long, java.lang.Long)
	 */
	@Override
	public void roundClicked(final EditRound editRound, final Long compId, final Long roundId) {
		this.er = editRound;
		er.SetPresenter(this);
		ICompetition comp = null;
		IRound round = null;
		// find the round 
		for (ICompetition c : comps) {
			if (c.getId().equals(compId)) {
				comp = c;
				break;
			}
		}

		if (comp == null) {
			Window.alert("Could not find comp matching this round.");
			return;
		}

		for (IRound r : comp.getRounds()) {
			if (r.getId().equals(roundId)) {
				round = r;
				break;
			}
		}

		if (round == null) {
			Window.alert("Could not find round in the comp.");
			return;
		}
		final IRound fRound = round;

		clientFactory.getRpcService().getMatches(roundId, new AsyncCallback<List<IMatchGroup>>() {

			@Override
			public void onFailure(Throwable caught) {


			}

			@Override
			public void onSuccess(List<IMatchGroup> result) {

				view.addRound(compId, roundId, result);
				clientFactory.getRpcService().getStandings(roundId, new AsyncCallback<List<IStanding>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(List<IStanding> result) {
						er.ShowRound(fRound,result);
					}

				});

			}
		});			
	}



	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.EditTeam.Presenter#saveTeamInfo(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void saveTeamInfo(ITeamGroup teamGroup) {
		clientFactory.getRpcService().saveTeam(teamGroup, new AsyncCallback<ITeamGroup>() {

			@Override
			public void onFailure(Throwable caught) {


			}

			@Override
			public void onSuccess(ITeamGroup result) {

				view.showStatus("team info saved");

			}
		});			
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.CompetitionView.Presenter#editTeam(net.rugby.foundation.admin.client.ui.EditTeam, long)
	 */

	@Override
	public void editTeamInit(final EditTeam editTeam, long teamId, long compId) {
		final EditTeam.Presenter presenter = this;  // there must be a way to do this...
		et = editTeam;
		clientFactory.getRpcService().getTeam(teamId, new AsyncCallback<ITeamGroup>() {

			@Override
			public void onFailure(Throwable caught) {


			}

			@Override
			public void onSuccess(ITeamGroup result) {
				et.SetPresenter(presenter);
				et.ShowTeam(result);

			}
		});			

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.CompetitionView.Presenter#createAdminClicked()
	 */
	@Override
	public void createAdminClicked() {
		clientFactory.getRpcService().createAdmin(new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {


			}

			@Override
			public void onSuccess(Boolean result) {
				Window.alert("Success");

			}
		});					
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.CompetitionView.Presenter#sanityCheckClicked()
	 */
	@Override
	public void sanityCheckClicked() {
		clientFactory.getRpcService().sanityCheck(new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {


			}

			@Override
			public void onSuccess(List<String> result) {
				Window.alert(result.toString());

			}
		});				

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.CompetitionView.Presenter#compClicked(long)
	 */
	@Override
	public void compClicked(final EditComp editComp, long compId) {
		//final EditComp.Presenter presenter = this;  // there must be a way to do this...
		ec = editComp;
		ec.SetPresenter(this);	

		for (ICompetition comp : comps) {
			if (comp.getId().equals(compId)) {
				ec.ShowComp(comp);
			}
		}

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.EditComp.Presenter#saveCompInfo(net.rugby.foundation.model.shared.ICompetition)
	 */
	@Override
	public void saveCompInfo(ICompetition comp) {
		clientFactory.getRpcService().saveCompInfo(comp, new AsyncCallback<ICompetition>() {

			@Override
			public void onFailure(Throwable caught) {

				Window.alert("Comp not saved " + caught.getMessage());
			}

			@Override
			public void onSuccess(ICompetition result) {
				//ec.SetPresenter(presenter);
				if (result != null)
					Window.alert("Comp saved");
				else
					Window.alert("Comp not saved");


			}
		});			

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.CompetitionView.Presenter#editMatchInit(net.rugby.foundation.admin.client.ui.EditMatch, long)
	 */
	@Override
	public void editMatchInit(EditMatch editMatch, PlayerListView<IPlayerRating> editMatchInfo, final long matchId, long roundId, long compId) {
		final EditMatch.Presenter presenter = this;  // there must be a way to do this...
		em = editMatch;

		final PlayerListView.Listener<IPlayerRating> presenter2 = this;  // there must be a way to do this...
		plv = editMatchInfo;

		setCurrentRoundId(roundId);
		setCurrentCompId(compId);
		clientFactory.getRpcService().getMatch(matchId, new AsyncCallback<IMatchGroup>() {

			@Override
			public void onFailure(Throwable caught) {


			}

			@Override
			public void onSuccess(IMatchGroup result) {
				em.SetPresenter(presenter);
				em.ShowMatch(result);
				final IMatchGroup match = result;
				clientFactory.getRpcService().getPlayerMatchInfo(matchId, new AsyncCallback<List<IPlayerRating>>() {

					@Override
					public void onFailure(Throwable caught) {


					}

					@Override
					public void onSuccess(List<IPlayerRating> result) {
						plv.setListener(presenter2);
						plv.setPlayers(result, match);
					}
				});
			}
		});			
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.EditMatch.Presenter#saveMatchInfo(net.rugby.foundation.model.shared.IMatchGroup)
	 */
	@Override
	public void saveMatchInfo(IMatchGroup matchGroup) {
		clientFactory.getRpcService().saveMatch(matchGroup, new AsyncCallback<IMatchGroup>() {

			@Override
			public void onFailure(Throwable caught) {

				Window.alert("Match not saved " + caught.getMessage());
			}

			@Override
			public void onSuccess(IMatchGroup result) {
				//ec.SetPresenter(presenter);
				if (result != null)
					Window.alert("Match saved");
				else
					Window.alert("Comp not saved");


			}
		});			

	}


	public Long getCurrentCompId() {
		return currentCompId;
	}

	public void setCurrentCompId(Long currentCompId) {
		this.currentCompId = currentCompId;
	}

	public Long getCurrentRoundId() {
		return currentRoundId;
	}

	public void setCurrentRoundId(Long currentRoundId) {
		this.currentRoundId = currentRoundId;
	}

	public Long getCurrentMatchId() {
		return currentMatchId;
	}

	public void setCurrentMatchId(Long currentMatchId) {
		this.currentMatchId = currentMatchId;
	}

	@Override
	public void lockMatch(boolean lock, IMatchGroup matchGroup) {
		List<String> log = new ArrayList<String>();
		clientFactory.getRpcService().lockMatch(lock, matchGroup, currentCompId, log, new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {

				Window.alert("Match not saved " + caught.getMessage());
			}

			@Override
			public void onSuccess(List<String> result) {
				//ec.SetPresenter(presenter);
				//				if (result != null)
				//					Window.alert("Match saved");
				//				else
				//					Window.alert("Comp not saved");
				view.showStatus(result.toString());


			}
		});		

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.EditMatch.Presenter#fetchScore(net.rugby.foundation.model.shared.IMatchGroup)
	 */
	@Override
	public void fetchScore(IMatchGroup matchGroup) {
		List<String> log = new ArrayList<String>();
		clientFactory.getRpcService().fetchMatchScore(matchGroup, currentCompId, log, new AsyncCallback<IMatchGroup>() {

			@Override
			public void onFailure(Throwable caught) {

				view.showStatus("Match score not fetched: " + caught.getMessage());
			}

			@Override
			public void onSuccess(IMatchGroup result) {
				//editMatchInit(em, plv, result.getId(), result.getRoundId(), currentCompId);
				if (result != null) {
					em.ShowMatch(result);
				} else {
					Window.alert("Something happened getting match score");
				}

			}
		});		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.EditMatch.Presenter#fetchMatchStats(net.rugby.foundation.model.shared.IMatchGroup)
	 */
	@Override
	public void fetchMatchStats(final IMatchGroup matchGroup) {
		//final EditMatch.Presenter presenter = this;  // there must be a way to do this...

		clientFactory.getRpcService().fetchMatchStats(matchGroup.getId(), new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed fetching Match Stats: " + caught.getMessage());
			}

			@Override
			public void onSuccess(final String result) {
				em.setPipelineId(result);
				waiting = true;
				ready = true;

				Timer t = new Timer() {
					@Override
					public void run() {
						checkPipelineStatus(result);
					}
				};
				
				t.schedule(5000);

			}

			private void checkPipelineStatus(final String id) {
				clientFactory.getRpcService().checkPipelineStatus(id, matchGroup.getId(), new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						waiting = false;
						Window.alert("Problem checking pipeline status for " + id);					
					}

					@Override
					public void onSuccess(String result) {
						clientFactory.getRpcService().getPlayerMatchInfo(matchGroup.getId(), new AsyncCallback<List<IPlayerRating>>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Failed getting updated Match (for pipeline Id): " + caught.getMessage());

							}

							@Override
							public void onSuccess(List<IPlayerRating> playerInfo) {
								//em.SetPresenter(presenter);
								//em.ShowMatch(matchGroup);	
								//em.setPipelineId(id);
								view.getPlayerListView().setPlayers(playerInfo, matchGroup);
							}

						});
						
						// do we have to check again?
						if (result.equals("COMPLETED")) {
							em.setPipelineId(null);
							
						} else {
							// call ourselves recursively until we finish
							Timer t = new Timer() {
								@Override
								public void run() {
									checkPipelineStatus(id);
								}
							};
							
							t.schedule(5000);
						}

					}

				});

			}
		});
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
		if (ensureSingleMatch(player)) {
			clientFactory.getPlayerPopupView().setPresenter(this);
			clientFactory.getRpcService().getPlayer(player.getMatchStats().get(0).getPlayerId(), new AsyncCallback<IPlayer>() {
	
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
		} else {
			Window.alert("Invalid attempt to edit player");
		}


	}

	@Override
	public void showEditStats(IPlayerRating info) {
		if (ensureSingleMatch(info)) {
			clientFactory.getPlayerMatchStatsPopupView().setPresenter(this);
			clientFactory.getPlayerMatchStatsPopupView().setTarget(info.getMatchStats().get(0));
			((DialogBox) clientFactory.getPlayerMatchStatsPopupView()).center();
		} else {
			Window.alert("Invalid attempt to edit player match stats.");
		}
	}

	@Override
	public void showEditRating(IPlayerRating player) {
		// TODO Auto-generated method stub

	}

	@Override
	public ClientFactory getClientFactory() {
		return clientFactory;
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
		((DialogBox) clientFactory.getPlayerPopupView()).hide();
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

				view.getPlayerListView().updatePlayerMatchStats(result);
				((DialogBox) clientFactory.getPlayerMatchStatsPopupView()).hide();

			}
		});			
	}



	@Override
	public void onCancelEditPlayerMatchStatsClicked() {
		((DialogBox) clientFactory.getPlayerMatchStatsPopupView()).hide();
	}



	@Override
	public void compPicked(Long id) {
		// TODO Auto-generated method stub

	}



	@Override
	public void repairComp(ICompetition comp) {
		clientFactory.getRpcService().repairComp(comp, new AsyncCallback<ICompetition>() {

			@Override
			public void onFailure(Throwable caught) {

				Window.alert("Comp not repaired " + caught.getMessage());
			}

			@Override
			public void onSuccess(ICompetition result) {
				//ec.SetPresenter(presenter);
				if (result != null)
					Window.alert("Comp repaired");
				else
					Window.alert("Comp not repaired");


			}
		});			

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
	public void showHomeTeamMatchStats(IMatchGroup matchGroup) {
		clientFactory.getTeamMatchStatsPopupView().setPresenter(this);
		clientFactory.getRpcService().getTeamMatchStats(matchGroup.getId(), matchGroup.getHomeTeamId(), new AsyncCallback<ITeamMatchStats>() {
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



	@Override
	public void showVisitingTeamMatchStats(IMatchGroup matchGroup) {
		clientFactory.getTeamMatchStatsPopupView().setPresenter(this);
		clientFactory.getRpcService().getTeamMatchStats(matchGroup.getId(), matchGroup.getVisitingTeamId(), new AsyncCallback<ITeamMatchStats>() {
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

				//view.getPlayerListView().updatePlayerMatchStats(result);
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
	public void onCancelEditMatchRatingEngineSchemaClicked() {
		((DialogBox)clientFactory.getMatchRatingEngineSchemaPopupView()).hide();

	}



	@Override
	public void onDeleteRatingsForMatchRatingEngineSchemaClicked(ScrumMatchRatingEngineSchema20130713 schema) {
		clientFactory.getMatchRatingEngineSchemaPopupView().setPresenter((MatchRatingEngineSchemaPopupViewPresenter<ScrumMatchRatingEngineSchema20130713>) this);
		clientFactory.getRpcService().deleteRatingsForMatchRatingEngineSchema(schema, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to delete ratings match rating engine schema. Details:" + caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					Window.alert("Ratings successfully deleted");
				} else {
					Window.alert("Ratings not deleted.");
				}
			}
		});
	}



	@Override
	public void onDeleteMatchRatingEngineSchemaClicked(ScrumMatchRatingEngineSchema20130713 schema) {
		clientFactory.getMatchRatingEngineSchemaPopupView().setPresenter((MatchRatingEngineSchemaPopupViewPresenter<ScrumMatchRatingEngineSchema20130713>) this);
		clientFactory.getRpcService().deleteMatchRatingEngineSchema(schema, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to delete match rating engine schema, you probably have ratings you need to delete first, but check the logs to see what's up...");
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					((DialogBox)clientFactory.getMatchRatingEngineSchemaPopupView()).hide();
					Window.alert("Schema successfully deleted");
					clientFactory.getRpcService().getScrumSchemaList(new AsyncCallback<List<ScrumMatchRatingEngineSchema>>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Failed to refresh schema list");
						}

						@Override
						public void onSuccess(List<ScrumMatchRatingEngineSchema> result) {
							view.setSchemaList(result);
						}

					});
				} else {
					Window.alert("Schema not deleted. You probably have ratings you need to delete first.");
				}
			}
		});

	}



	@Override
	public void editSchema(IRatingEngineSchema schema) {
		clientFactory.getMatchRatingEngineSchemaPopupView().setPresenter((MatchRatingEngineSchemaPopupViewPresenter<ScrumMatchRatingEngineSchema20130713>) this);
		if (schema instanceof ScrumMatchRatingEngineSchema20130713) {
			clientFactory.getMatchRatingEngineSchemaPopupView().setTarget((ScrumMatchRatingEngineSchema20130713)schema);
			((DialogBox)clientFactory.getMatchRatingEngineSchemaPopupView()).center();
		}

	}



	@Override
	public void createSchema() {
		clientFactory.getMatchRatingEngineSchemaPopupView().setPresenter((MatchRatingEngineSchemaPopupViewPresenter<ScrumMatchRatingEngineSchema20130713>) this);
		clientFactory.getRpcService().getMatchRatingEngineSchema(null, new AsyncCallback<ScrumMatchRatingEngineSchema>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to save match rating engine schema, see logs for details");
			}

			@Override
			public void onSuccess(ScrumMatchRatingEngineSchema result) {
				if (result instanceof ScrumMatchRatingEngineSchema20130713) {
					clientFactory.getMatchRatingEngineSchemaPopupView().setTarget((ScrumMatchRatingEngineSchema20130713)result);
					((DialogBox)clientFactory.getMatchRatingEngineSchemaPopupView()).center();
				}
			}
		});
	}



	@Override
	public void onSaveMatchRatingEngineSchemaClicked(
			ScrumMatchRatingEngineSchema20130713 schema) {
		clientFactory.getMatchRatingEngineSchemaPopupView().setPresenter((MatchRatingEngineSchemaPopupViewPresenter<ScrumMatchRatingEngineSchema20130713>)this);
		clientFactory.getRpcService().saveMatchRatingEngineSchema((ScrumMatchRatingEngineSchema)schema, new AsyncCallback<ScrumMatchRatingEngineSchema>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to save match rating engine schema: " + caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(ScrumMatchRatingEngineSchema result) {
				if (result instanceof ScrumMatchRatingEngineSchema20130713) {
					clientFactory.getMatchRatingEngineSchemaPopupView().setTarget((ScrumMatchRatingEngineSchema20130713)result);
					((DialogBox)clientFactory.getMatchRatingEngineSchemaPopupView()).hide();
					clientFactory.getRpcService().getScrumSchemaList(new AsyncCallback<List<ScrumMatchRatingEngineSchema>>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Failed to refresh schema list: " + caught.getLocalizedMessage());
						}

						@Override
						public void onSuccess(List<ScrumMatchRatingEngineSchema> result) {
							view.setSchemaList(result);
						}

					});
				} else {
					Window.alert("Schema not saved. Didn't get a good schema back from server.");
				}
			}
		});

	}



	@Override
	public void onSaveAsCopyMatchRatingEngineSchemaClicked(
			ScrumMatchRatingEngineSchema20130713 schema) {
		clientFactory.getMatchRatingEngineSchemaPopupView().setPresenter((MatchRatingEngineSchemaPopupViewPresenter<ScrumMatchRatingEngineSchema20130713>)this);
		clientFactory.getRpcService().saveMatchRatingEngineSchemaAsCopy((ScrumMatchRatingEngineSchema)schema, new AsyncCallback<ScrumMatchRatingEngineSchema>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to save copy of match rating engine schema. Details: " + caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(ScrumMatchRatingEngineSchema result) {
				if (result instanceof ScrumMatchRatingEngineSchema20130713) {
					clientFactory.getMatchRatingEngineSchemaPopupView().setTarget((ScrumMatchRatingEngineSchema20130713)result);
					((DialogBox)clientFactory.getMatchRatingEngineSchemaPopupView()).hide();
					clientFactory.getRpcService().getScrumSchemaList(new AsyncCallback<List<ScrumMatchRatingEngineSchema>>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Failed to refresh schema list: " + caught.getLocalizedMessage());
						}

						@Override
						public void onSuccess(List<ScrumMatchRatingEngineSchema> result) {
							view.setSchemaList(result);
						}

					});
				} else {
					Window.alert("Schema not saved. Didn't get a good schema back from server.");
				}
			}
		});

	}



	@Override
	public void onSetMatchRatingEngineSchemaAsDefaultClicked(
			ScrumMatchRatingEngineSchema20130713 schema) {
		clientFactory.getMatchRatingEngineSchemaPopupView().setPresenter((MatchRatingEngineSchemaPopupViewPresenter<ScrumMatchRatingEngineSchema20130713>)this);
		clientFactory.getRpcService().setMatchRatingEngineSchemaAsDefault(schema, new AsyncCallback<ScrumMatchRatingEngineSchema>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to save copy of match rating engine schema. Details: " + caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(ScrumMatchRatingEngineSchema result) {
				if (result instanceof ScrumMatchRatingEngineSchema20130713) {
					clientFactory.getMatchRatingEngineSchemaPopupView().setTarget((ScrumMatchRatingEngineSchema20130713)result);
					((DialogBox)clientFactory.getMatchRatingEngineSchemaPopupView()).center();
					clientFactory.getRpcService().getScrumSchemaList(new AsyncCallback<List<ScrumMatchRatingEngineSchema>>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Failed to refresh schema list: " + caught.getLocalizedMessage());
						}

						@Override
						public void onSuccess(List<ScrumMatchRatingEngineSchema> result) {
							view.setSchemaList(result);
						}

					});
				} else {
					Window.alert("Schema not saved. Didn't get a good schema back from server.");
				}
			}
		});	}



	@Override
	public void flushAllPipelineJobs() {
		clientFactory.flushAllPipelineJobs();

	}



	@Override
	public void deleteComp(ICompetition comp) {
		clientFactory.getRpcService().deleteComp(comp.getId(), new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Comp deletion failure: " + caught.getLocalizedMessage());

			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					Window.alert("Comp deleted. Don't forget to flush memcache!");
				} else {
					Window.alert("Comp not deleted. See logs for details");
				}

			}

		}); 

	}



	@Override
	public void setCompAsDefault(ICompetition comp) {
		clientFactory.getRpcService().setCompAsDefault(comp.getId(), new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Set comp as default failure: " + caught.getLocalizedMessage());

			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					Window.alert("Comp set as default. Don't forget to flush memcache!");
				} else {
					Window.alert("Comp not set as default. See logs for details");
				}

			}

		}); 
	}



	@Override
	public void createContent() {
		clientFactory.createContent();
	}



	@Override
	public void showEditTeamStats(IPlayerRating pmr) {
		if (ensureSingleMatch(pmr)) {
			clientFactory.getTeamMatchStatsPopupView().setPresenter(this);
			clientFactory.getRpcService().getTeamMatchStats(pmr.getMatchStats().get(0).getMatchId(), pmr.getMatchStats().get(0).getTeamId(), new AsyncCallback<ITeamMatchStats>() {
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



	private boolean ensureSingleMatch(IPlayerRating pmr) {
		if (pmr == null || pmr.getMatchStats() == null || pmr.getMatchStats().size() != 1) 
			return false;
		else
			return true;
	}



	// @REX doesn't save Round info, only standings
	@Override
	public void saveRound(final IRound r, List<IStanding> ss) {
		clientFactory.getRpcService().saveStandings(r.getId(), ss, new AsyncCallback<List<IStanding>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Troubles saving standings: " + caught.getLocalizedMessage());

			}

			@Override
			public void onSuccess(List<IStanding> result) {
				er.ShowRound(r,result);		
			}

		});

	}



	@Override
	public void fetchRoundStandings(final IRound round) {
		clientFactory.getRpcService().FetchRoundStandings(round.getId(), new AsyncCallback<List<IStanding>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Troubles fetching standings: " + caught.getLocalizedMessage());

			}

			@Override
			public void onSuccess(List<IStanding> result) {
				er.ShowRound(round,result);		
			}

		});

	}



	@Override
	public void saveScore(Long matchId, int hS, int vS, Status status) {
		clientFactory.getRpcService().SaveScore(matchId, hS, vS, status, new AsyncCallback<IMatchGroup>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Troubles fetching standings: " + caught.getLocalizedMessage());

			}

			@Override
			public void onSuccess(IMatchGroup result) {
				Window.alert("Score saved");	
				em.ShowMatch(result);
			}

		});		
	}



	@Override
	public void addMatch(IRound round) {
		clientFactory.getRpcService().AddMatchToRound(round, new AsyncCallback<IMatchGroup>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Troubles fetching standings: " + caught.getLocalizedMessage());

			}

			@Override
			public void onSuccess(IMatchGroup result) {
				if (result != null)
				{
					Window.alert("Match	Added");	
					em.ShowMatch(result);
				} else {
					Window.alert("Match not added. See server log for details");
				}
			}

		});		}



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
	public void virtualCompClicked() {
		clientFactory.getRpcService().addVirtualComp(new AsyncCallback<ICompetition>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Troubles creating virtual comp: " + caught.getLocalizedMessage());

			}

			@Override
			public void onSuccess(ICompetition result) {
				if (result != null)
				{
					Window.alert("Comp	Added");	
					comps.add(result);
					view.addComps(comps);
				} else {
					Window.alert("Comp not added. See server log for details");
				}
			}

		});
		
	}
	
}
