package net.rugby.foundation.admin.client.activity;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.place.AdminCompPlace;
import net.rugby.foundation.admin.client.place.AdminTaskPlace;
import net.rugby.foundation.admin.client.ui.AddMatchPopup.AddMatchPopupPresenter;
import net.rugby.foundation.admin.client.ui.AddRoundPopup.AddRoundPopupPresenter;
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
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.Status;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.IStandingFull;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.Round;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema20130713;

import org.gwtbootstrap3.extras.bootbox.client.Bootbox;
import org.gwtbootstrap3.extras.bootbox.client.callback.ConfirmCallback;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
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
SmartBar.Presenter, SmartBar.SchemaPresenter, MatchRatingEngineSchemaPopupViewPresenter<ScrumMatchRatingEngineSchema20130713>, 
RoundPresenter, AddRoundPopupPresenter, AddMatchPopupPresenter {
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

	
	private EditMatch em;

	private Long currentCompId = null;
	private Long currentRoundId = null;
	private Long currentMatchId = null;

	// temp storage for building up new comp
	private List<ITeamGroup> teams = new ArrayList<ITeamGroup>();
	protected List<IRound> rounds = new ArrayList<IRound>();
	private PlayerListView<IPlayerRating> plv;
	private AdminCompPlace place;
	//	private boolean waiting = false; // used to see if matchStats fetching is complete
	//	private boolean ready = false;

	private MenuItemDelegate menuItemDelegate = null;
	private int weeks;
	private int fetchCompWeeks;

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
					clientFactory.getRpcService().getConfiguration(new AsyncCallback<ICoreConfiguration>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Problem getting comp list: " + caught.getLocalizedMessage());
						}

						@Override
						public void onSuccess(ICoreConfiguration result) {
							//comps = result;
							view.addCompNames(result,place.getFilter());
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
	public void fetchTeamsClicked(String url, String weeks, CompetitionType compType) {
		this.url = url;
		this.fetchCompWeeks = Integer.parseInt(weeks);

		clientFactory.getRpcService().fetchTeams(url, weeks, compType, new AsyncCallback<Map<String, ITeamGroup>>() {

			@Override
			public void onFailure(Throwable caught) {
				view.showStatus(caught.getMessage());

			}

			@Override
			public void onSuccess(final Map<String, ITeamGroup> teamMap) {
				if (teamMap != null) {
					for (ITeamGroup t: teamMap.values()) {
						teams.add(t);
					}
					view.showTeams(teamMap);
					Notify.notify("Teams with green names will be created. This is often a bad thing. If they should already be in the DB, stop now. Go find them in another comp and change their names so they match the ESPN ones. And flush memcache. And then re-run until they are not green.",NotifyType.WARNING);

					
						
				} else {
					view.showStatus("Something is wrong. The node.js server down prolly.");
				}

			}
		});		
	}

	/**
	 * Recursively calls itself, fetching the number of weeks worth of matches
	 */
	@Override
	public void fetchMatchesClicked(final Map<String,ITeamGroup> teamMap, final Map<String, IMatchGroup> matchMap, final int numWeeks, final int currWeek, final CompetitionType compType) {

		if (currWeek == 0) {
			boolean hasNew = false;
			for (ITeamGroup t : teamMap.values()) {
				if (t.getId() == null) {
					hasNew = true;
					break;
				}
			}
			if (hasNew) {
				
				Bootbox.confirm("Do you want to save the newly created teams (in green)?", new ConfirmCallback() {
					@Override
					public void callback(boolean result) {
						if (result) {
							
							clientFactory.getRpcService().saveTeams(teamMap, new AsyncCallback<Map<String,ITeamGroup>>() {

								@Override
								public void onFailure(Throwable caught) {
									Notify.notify("Failed to save teams",NotifyType.DANGER);
								}

								@Override
								public void onSuccess(Map<String, ITeamGroup> result) {
									Notify.notify("Saved new teams",NotifyType.SUCCESS);
								
									teams.clear();
									for (ITeamGroup t: result.values()) {
										teams.add(t);
									}
									
									view.showTeams(result);
									
									if (currWeek < numWeeks) {
										final int _currWeek = currWeek;
										Map<String, IMatchGroup> _matchMap = matchMap;
										if (matchMap == null) {
											_matchMap =  new HashMap<String, IMatchGroup>();
										}
								 		clientFactory.getRpcService().fetchMatches(url, currWeek, teamMap, _matchMap, compType, new AsyncCallback<Map<String,IMatchGroup>>() {
								
											@Override
											public void onFailure(Throwable caught) {
												view.showStatus(caught.getMessage());
												Notify.notify("Problem fetching matches " + caught.getLocalizedMessage(), NotifyType.DANGER);
											}
								
											@Override
											public void onSuccess(Map<String, IMatchGroup> result) {
												Notify.notify("Found " + result.size() + " matches through week " + _currWeek, NotifyType.SUCCESS);
												view.addMatches(result);
												fetchMatchesClicked(teamMap, result, numWeeks, _currWeek +1, compType);
											}
										});	
									} else {
										view.showMatches(matchMap);
										Notify.notify("Done! Found " + matchMap.size() + " matches for competition!", NotifyType.SUCCESS);
											
									}
								}
							});
						}
					}
				});
			} else {
				if (currWeek < numWeeks) {
					final int _currWeek = currWeek;
					Map<String, IMatchGroup> _matchMap = matchMap;
					if (matchMap == null) {
						_matchMap =  new HashMap<String, IMatchGroup>();
					}
			 		clientFactory.getRpcService().fetchMatches(url, currWeek, teamMap, _matchMap, compType, new AsyncCallback<Map<String,IMatchGroup>>() {
			
						@Override
						public void onFailure(Throwable caught) {
							view.showStatus(caught.getMessage());
							Notify.notify("Problem fetching matches " + caught.getLocalizedMessage(), NotifyType.DANGER);
						}
			
						@Override
						public void onSuccess(Map<String, IMatchGroup> result) {
							Notify.notify("Found " + result.size() + " matches through week " + _currWeek, NotifyType.SUCCESS);
							view.addMatches(result);
							fetchMatchesClicked(teamMap, result, numWeeks, _currWeek +1, compType);
						}
					});	
				} else {
					view.showMatches(matchMap);
					Notify.notify("Done! Found " + matchMap.size() + " matches for competition!", NotifyType.SUCCESS);
						
				}
			}
		} else {
			if (currWeek < numWeeks) {
				final int _currWeek = currWeek;
				Map<String, IMatchGroup> _matchMap = matchMap;
				if (matchMap == null) {
					_matchMap =  new HashMap<String, IMatchGroup>();
				}
		 		clientFactory.getRpcService().fetchMatches(url, currWeek, teamMap, _matchMap, compType, new AsyncCallback<Map<String,IMatchGroup>>() {
		
					@Override
					public void onFailure(Throwable caught) {
						view.showStatus(caught.getMessage());
						Notify.notify("Problem fetching matches " + caught.getLocalizedMessage(), NotifyType.DANGER);
					}
		
					@Override
					public void onSuccess(Map<String, IMatchGroup> result) {
						Notify.notify("Found " + result.size() + " matches through week " + _currWeek, NotifyType.SUCCESS);
						view.addMatches(result);
						fetchMatchesClicked(teamMap, result, numWeeks, _currWeek +1, compType);
					}
				});	
			} else {
				view.showMatches(matchMap);
				Notify.notify("Done! Found " + matchMap.size() + " matches for competition!", NotifyType.SUCCESS);
					
			}
		}
		
		
		
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
		
		// find the round 
		clientFactory.getCompAsync(compId, new AsyncCallback<ICompetition>() {

			@Override
			public void onFailure(Throwable caught) {
				Notify.notify("Could not find comp matching this round.", NotifyType.DANGER);
				
			}

			@Override
			public void onSuccess(ICompetition comp) {
				IRound round = null;
				if (comp == null) {
					Notify.notify("Could not find comp matching this round.", NotifyType.DANGER);
				} else {
					for (IRound r : comp.getRounds()) {
						if (r.getId().equals(roundId)) {
							round = r;
							break;
						}
					}

					if (round == null) {
						Notify.notify("Could not find round in the comp.", NotifyType.DANGER);
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
							clientFactory.getRpcService().getStandings(roundId, new AsyncCallback<List<IStandingFull>>() {

								@Override
								public void onFailure(Throwable caught) {
									Notify.notify("Could not find standings for round " + roundId, NotifyType.DANGER);

								}

								@Override
								public void onSuccess(List<IStandingFull> result) {
									er.ShowRound(fRound,result);
								}

							});

						}
					});		
				}
				
			}
			
		});

	
	}



	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.EditTeam.Presenter#saveTeamInfo(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void saveTeamInfo(ITeamGroup teamGroup, boolean saveMatches) {
		clientFactory.getRpcService().saveTeam(teamGroup, saveMatches, new AsyncCallback<ITeamGroup>() {

			@Override
			public void onFailure(Throwable caught) {


			}

			@Override
			public void onSuccess(ITeamGroup result) {

				Notify.notify(result.getDisplayName() + " saved. When you are done saving teams, remember to flush the memcache before doing other things.");

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
	public void compClicked(final EditComp editComp, final long compId) {
		//final EditComp.Presenter presenter = this;  // there must be a way to do this...
		ec = editComp;
		ec.SetPresenter(this);	


		clientFactory.getCompAsync( compId, new AsyncCallback<ICompetition>() {

			@Override
			public void onFailure(Throwable caught) {
				Notify.notify("Could not find competition with id " + compId);
				
			}

			@Override
			public void onSuccess(ICompetition comp) {
				ec.ShowComp(comp);
			}
			
		});
		

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
					//Window.alert("Comp saved");
					Notify.notify("Comp " + result.getLongName() + " saved.", NotifyType.SUCCESS);
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

				Notify.notify("Match not saved " + caught.getMessage(), NotifyType.DANGER);
			}

			@Override
			public void onSuccess(IMatchGroup result) {
				//ec.SetPresenter(presenter);
				if (result != null)
					Notify.notify("Match saved", NotifyType.SUCCESS);
				else
					Notify.notify("Match not saved", NotifyType.WARNING);


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
				Notify.notify("Match score not fetched: " + caught.getMessage(), NotifyType.DANGER);
			}

			@Override
			public void onSuccess(IMatchGroup result) {
				//editMatchInit(em, plv, result.getId(), result.getRoundId(), currentCompId);
				if (result != null) {
					em.ShowMatch(result);
					Notify.notify("Score fetched",NotifyType.SUCCESS);
				} else {
					Notify.notify("Something happened getting match score", NotifyType.WARNING);
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
					if (result.getBlockingTaskIds() == null || result.getBlockingTaskIds().isEmpty()) {
						clientFactory.getPlayerPopupView().setPlayer(result);
						((DialogBox) clientFactory.getPlayerPopupView()).center();
					} else {
						AdminTaskPlace place = new AdminTaskPlace();
						place.setFilter("All");
						place.setTaskId(result.getBlockingTaskIds().get(0).toString());
						goTo(place);
					}
				}
			});	
		} else {
			Window.alert("Invalid attempt to edit player");
		}


	}

	@Override
	public void showEditStats(IPlayerRating info) {
		if (ensureSingleMatch(info)) {
			if (info.getMatchStats().get(0).getBlockingTaskIds() != null && !info.getMatchStats().get(0).getBlockingTaskIds().isEmpty()) {
				AdminTaskPlace place = new AdminTaskPlace();
				place.setFilter("All");
				place.setTaskId(info.getMatchStats().get(0).getBlockingTaskIds().get(0).toString());
				goTo(place);
			} else {
				clientFactory.getPlayerMatchStatsPopupView().setPresenter(this);
				clientFactory.getPlayerMatchStatsPopupView().setTarget(info.getMatchStats().get(0));
				((DialogBox) clientFactory.getPlayerMatchStatsPopupView()).center();
			}
		} else {
			Bootbox.alert("Invalid attempt to edit player match stats.");
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
				Notify.notify("Match stats for " + result.getPlayer().getDisplayName() + " saved.");
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
	public void onFlushRawScoresButtonClicked(ScrumMatchRatingEngineSchema20130713 schema) {
		clientFactory.getMatchRatingEngineSchemaPopupView().setPresenter((MatchRatingEngineSchemaPopupViewPresenter<ScrumMatchRatingEngineSchema20130713>) this);
		clientFactory.getRpcService().deleteRawScoresForMatchRatingEngineSchema(schema, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to delete eaw scores for match rating engine schema. Details:" + caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					Window.alert("Raw scores successfully flushed");
				} else {
					Window.alert("Raw scores not flushed.");
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
		clientFactory.getRpcService().createMatchRatingEngineSchema(new AsyncCallback<ScrumMatchRatingEngineSchema>() {
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
	public void deleteComp(final ICompetition comp) {
		Bootbox.confirm("Are you sure you want to delete this competition?", new ConfirmCallback() {
			@Override
			public void callback(boolean result) {
				if (result) {
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
			}
		});
	}



	@Override
	public void setCompAsGlobal(ICompetition comp) {
		clientFactory.getRpcService().setCompAsGlobal(comp.getId(), new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Notify.notify("Set comp as global failure: " + caught.getLocalizedMessage(),NotifyType.DANGER);

			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					Notify.notify("Comp set as global. Don't forget to flush memcache!", NotifyType.SUCCESS);
				} else {
					Notify.notify("Comp not set as global. See logs for details",NotifyType.WARNING);
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
					Notify.notify("Failed to fetch team match stats to edit",NotifyType.DANGER);
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

	@Override
	public void saveRound(final IRound r, final List<IStandingFull> ss) {
		clientFactory.getRpcService().saveRound(r, new AsyncCallback<IRound>() { 

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(IRound result) {
				clientFactory.getRpcService().saveStandings(r.getId(), ss, new AsyncCallback<List<IStandingFull>>() {

					@Override
					public void onFailure(Throwable caught) {
						Notify.notify("Troubles saving standings: " + caught.getLocalizedMessage(), NotifyType.DANGER);
					}

					@Override
					public void onSuccess(List<IStandingFull> result) {
						er.ShowRound(r,result);	
						Notify.notify("Round saved",NotifyType.SUCCESS);
					}

				});

			}
		});


	}



	@Override
	public void fetchRoundStandings(final IRound round) {
		clientFactory.getRpcService().FetchRoundStandings(round.getId(), new AsyncCallback<List<IStandingFull>>() {

			@Override
			public void onFailure(Throwable caught) {
				Notify.notify("Troubles fetching standings: " + caught.getLocalizedMessage(), NotifyType.DANGER);
			}

			@Override
			public void onSuccess(List<IStandingFull> result) {
				er.ShowRound(round,result);		
				Notify.notify("Standings fetched", NotifyType.SUCCESS);
			}

		});

	}



	@Override
	public void saveScore(Long matchId, int hS, int vS, Status status) {
		clientFactory.getRpcService().SaveScore(matchId, hS, vS, status, new AsyncCallback<IMatchGroup>() {

			@Override
			public void onFailure(Throwable caught) {
				Notify.notify("Troubles fetching standings: " + caught.getLocalizedMessage(), NotifyType.DANGER);

			}

			@Override
			public void onSuccess(IMatchGroup result) {
				Notify.notify("Score saved", NotifyType.SUCCESS);	
				em.ShowMatch(result);
			}

		});		
	}



	@Override
	public void addMatch(IRound round) {
		clientFactory.getAddMatchPopup().setPresenter(this);
		clientFactory.getAddMatchPopup().setRound((Round)round);
		clientFactory.getAddMatchPopup().center();
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
	public void virtualCompClicked() {
		clientFactory.getRpcService().addVirtualComp(new AsyncCallback<ICompetition>() {

			@Override
			public void onFailure(Throwable caught) {
				Notify.notify("Troubles creating virtual comp: " + caught.getLocalizedMessage(), NotifyType.DANGER);

			}

			@Override
			public void onSuccess(ICompetition result) {
				if (result != null)
				{
					Notify.notify("New virtual comp added. Refresh to view", NotifyType.SUCCESS);	

					//view.addComp(result);
				} else {
					Window.alert("Comp not added. See server log for details");
				}
			}

		});

	}



	@Override
	public void addRound(ICompetition comp) {
		clientFactory.getAddRoundPopup().setComp(comp);
		clientFactory.getAddRoundPopup().setPresenter((AddRoundPopupPresenter) this);
		clientFactory.getAddRoundPopup().center();

	}



	@Override
	public void addRound(ICompetition comp, int uri, String name) {
		clientFactory.getRpcService().addRound(comp.getId(), uri, name, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Problems adding round");

			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					Window.alert("Success adding round");
					clientFactory.getAddRoundPopup().hide();
				} else {
					Window.alert("No good. Check the logs or try again. Maybe that round already exists? Try reloading maybe?");
				}
			}

		});

	}



	@Override
	public void scrapeRound(ICompetition comp, int uri, String name) {
		clientFactory.getRpcService().scrapeRound(comp.getId(), uri, name, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Problems scraping round");

			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					Window.alert("Success scraping round");
					clientFactory.getAddRoundPopup().hide();
				} else {
					Window.alert("No good. Check the logs or try again. Maybe that round doesn't exist? Try reloading maybe?");
				}
			}

		});
		
	}

	@Override
	public void cancelAddRound() {
		clientFactory.getAddRoundPopup().hide();

	}



	@Override
	public void addMatch(Round round, Long homeTeamId, Long visitingTeamId, Long espnMatchId, Long espnLeagueId) {
		clientFactory.getRpcService().AddMatchToRound(round, homeTeamId, visitingTeamId, espnMatchId, espnLeagueId, new AsyncCallback<IMatchGroup>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Troubles adding match: " + caught.getLocalizedMessage());

			}

			@Override
			public void onSuccess(IMatchGroup result) {
				if (result != null)
				{
					Window.alert("Match	Added");	
					//					em.ShowMatch(result);
					clientFactory.getAddMatchPopup().hide();
				} else {
					Window.alert("Match not added. See server log for details");
				}
			}

		});
	}



	@Override
	public void cancelAddMatch() {
		clientFactory.getAddMatchPopup().hide();

	}



	@Override
	public void fetchLineups(IMatchGroup matchGroup) {
		clientFactory.getRpcService().FetchLineups(matchGroup.getId(), new AsyncCallback<IMatchGroup>() {

			@Override
			public void onFailure(Throwable caught) {
				Notify.notify("Troubles fetching lineups for match: " + caught.getLocalizedMessage(), NotifyType.DANGER);
			}

			@Override
			public void onSuccess(IMatchGroup result) {
				if (result != null)
				{
					em.ShowMatch(result);
					Notify.notify("Lineups fetched", NotifyType.SUCCESS);	
				} else {
					Notify.notify("Lineups not fetched", NotifyType.WARNING);
				}
			}

		});
	}



	@Override
	public void teamsClicked(final long compId) {
		clientFactory.getCompAsync(compId, new AsyncCallback<ICompetition>() {

			@Override
			public void onFailure(Throwable caught) {
				Notify.notify("Troubles fetching teams for comp: " + caught.getLocalizedMessage(), NotifyType.DANGER);
			}

			@Override
			public void onSuccess(ICompetition result) {
				clientFactory.getCompView().addTeams(compId, result.getTeams());				
			}
			
		});
		
	}



	@Override
	public void roundsClicked(long compId) {
		clientFactory.getCompAsync(compId, new AsyncCallback<ICompetition>() {

			@Override
			public void onFailure(Throwable caught) {
				Notify.notify("Troubles fetching rounds for comp: " + caught.getLocalizedMessage(), NotifyType.DANGER);
			}

			@Override
			public void onSuccess(ICompetition result) {
				clientFactory.getCompView().addRounds(result, result.getRounds());				
			}
			
		});
		
	}



	@Override
	public void setCompAsDefault(ICompetition comp) {
		clientFactory.getRpcService().setCompAsDefault(comp.getId(), new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Notify.notify("Set comp as default failure: " + caught.getLocalizedMessage(),NotifyType.DANGER);
	
			}
	
			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					Notify.notify("Comp set as default. Don't forget to flush memcache!", NotifyType.SUCCESS);
				} else {
					Notify.notify("Comp not set as default. See logs for details",NotifyType.DANGER);
				}
	
			}

	}); 
		
	}



	@Override
	public void showWorkflowLog(IMatchGroup match) {

			clientFactory.getRpcService().getMatchWorkflowLog(match.getId(), new AsyncCallback<List<String>>() {

				@Override
				public void onFailure(Throwable caught) {
					Notify.notify("Fetch log failure: " + caught.getLocalizedMessage(),NotifyType.DANGER);
		
				}
		
				@Override
				public void onSuccess(List<String> result) {
					if (result == null) {
						Notify.notify("No workflow log found.", NotifyType.WARNING);
					} else {
						Window.alert(result.toString());
					}
		
				}
			});

	}



	@Override
	public void cancelWorkflow(final IRound round) {
		clientFactory.getRpcService().cancelWorkflow(round.getId(), new AsyncCallback<IRound>() {

			@Override
			public void onFailure(Throwable caught) {
				Notify.notify("Workflow cancel failure: " + caught.getLocalizedMessage(),NotifyType.DANGER);
			}

			@Override
			public void onSuccess(final IRound result) {
				if (result != null && result.getWeekendProcessingPipelineId() == null) {
					
					clientFactory.getRpcService().getStandings(round.getId(), new AsyncCallback<List<IStandingFull>>() {
						@Override
						public void onFailure(Throwable caught) {
							Notify.notify("Workflow cancel failure: " + caught.getLocalizedMessage(),NotifyType.DANGER);
						}
						
						@Override
						public void onSuccess(List<IStandingFull> standings) {
							Notify.notify("Workflow canceled.", NotifyType.SUCCESS);
							er.ShowRound(result, standings);
						}
					});							
				} else {
					Notify.notify("Workflow not canceled.", NotifyType.WARNING);
				}
			}	
		});
	}



	@Override
	public void initiateWorkflow(final IRound round) {
		clientFactory.getRpcService().initiateWorkflow(round.getId(), new AsyncCallback<IRound>() {

			@Override
			public void onFailure(Throwable caught) {
				Notify.notify("Workflow cancel failure: " + caught.getLocalizedMessage(),NotifyType.DANGER);
			}

			@Override
			public void onSuccess(final IRound result) {
				if (result != null && result.getWeekendProcessingPipelineId() != null) {
					Notify.notify("Workflow initiated.", NotifyType.SUCCESS);
					clientFactory.getRpcService().getStandings(round.getId(), new AsyncCallback<List<IStandingFull>>() {
						@Override
						public void onFailure(Throwable caught) {
							Notify.notify("Workflow cancel failure: " + caught.getLocalizedMessage(),NotifyType.DANGER);
						}
						
						@Override
						public void onSuccess(List<IStandingFull> standings) {
							er.ShowRound(result, standings);
						}
					});	
				} else {
					Notify.notify("Workflow not initiated.", NotifyType.WARNING);
				}
			}	
		});
	}









}
