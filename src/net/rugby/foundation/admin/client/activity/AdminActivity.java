package net.rugby.foundation.admin.client.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.place.AdminPlace;
import net.rugby.foundation.admin.client.place.EmailHandlerPlace;
import net.rugby.foundation.admin.client.ui.AdminView;
import net.rugby.foundation.admin.client.ui.CompetitionView;
import net.rugby.foundation.admin.client.ui.EditComp;
import net.rugby.foundation.admin.client.ui.EditComp.Presenter;
import net.rugby.foundation.admin.client.ui.EditMatch;
import net.rugby.foundation.admin.client.ui.EditPlayer;
import net.rugby.foundation.admin.client.ui.OrchestrationConfigurationView;
import net.rugby.foundation.admin.client.ui.EditTeam;
import net.rugby.foundation.admin.shared.IOrchestrationConfiguration;
import net.rugby.foundation.admin.shared.IWorkflowConfiguration;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ScrumPlayer;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * Activities are started and stopped by an ActivityManager associated with a container Widget.
 */
public class AdminActivity extends AbstractActivity implements EditPlayer.Presenter, 
						AdminView.Presenter, CompetitionView.Presenter, /*WorkflowConfigurationView.Presenter,*/ 
						OrchestrationConfigurationView.Presenter, EditTeam.Presenter, Presenter, 
						net.rugby.foundation.admin.client.ui.EditMatch.Presenter { //, Game1ConfigurationView.Presenter {
	/**
	 * Used to obtain views, eventBus, placeController.
	 * Alternatively, could be injected via GIN.
	 */
	private ClientFactory clientFactory;
	AdminView view = null;
	private String url;
	//private int showTab = 0;
	//private IWorkflowConfiguration workflowConfig = null;
	private  Map<String, IOrchestrationConfiguration> orchConfig = null;
	private EditTeam et = null;  //@REX stupid
	private EditComp ec = null;  //@REX stupid

	private List<ICompetition> comps = null;
	private EditMatch em;

	private Long currentCompId = null;
	private Long currentRoundId = null;
	private Long currentMatchId = null;

	// temp storage for building up new comp
	private List<ITeamGroup> teams = new ArrayList<ITeamGroup>();
	protected List<IRound> rounds = new ArrayList<IRound>();
	private boolean handleEmail = false;
	private EmailHandlerPlace emailHandlerPlace;

	public AdminActivity(AdminPlace place, ClientFactory clientFactory) {
		//this.name = place.getName();
		this.clientFactory = clientFactory;
		view = clientFactory.getAdminView();

		// Select the tab corresponding to the token value
		if (place.getToken() != null) {
			// By default the first tab is selected
			if (place.getToken().equals("") || place.getToken().equals("1")) {
				view.selectTab(0);
			} else if (place.getToken().equals("2")) {
				view.selectTab(1);
				//showTab = 1;
			} else if (place.getToken().equals("3")) {
				view.selectTab(2);
				//showTab = 2;
			}
		}

	}

	public AdminActivity(EmailHandlerPlace place, ClientFactory clientFactory) {
		//this.name = place.getName();
		this.clientFactory = clientFactory;
		view = clientFactory.getAdminView();

		// Select the tab corresponding to the token value
		if (place.getToken() != null) {
			view.selectTab(3);
			handleEmail = true;	

			emailHandlerPlace = place;
		}
	}

	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		view.setPresenter(this);

		containerWidget.setWidget(view.asWidget());

		if (handleEmail) {
			clientFactory.getRpcService().getPlayer(null, new AsyncCallback<IPlayer>() {



				@Override
				public void onFailure(Throwable caught) {


				}

				@Override
				public void onSuccess(IPlayer result) {
					view.getEditPlayer().ShowPlayer(result);
					view.getEditPlayer().ShowPlace(emailHandlerPlace);
				}
			});
			
		} else {

			clientFactory.getRpcService().getAllComps(new AsyncCallback<List<ICompetition>>() {



				@Override
				public void onFailure(Throwable caught) {


				}

				@Override
				public void onSuccess(List<ICompetition> result) {
					comps = result;
					view.getCompView().addComps(result);

					//		} else if (showTab == 3) {  // workflow conf
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

									view.getOrchestrationConfig().showStatus(caught.getMessage());
								}

								@Override
								public void onSuccess(Map<String, IOrchestrationConfiguration> result) {					
									view.getOrchestrationConfig().setOrchConfigs(result);					
								}
							});
						}
					});	
				}
			});		
		}

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

	@Override
	public void fetchCompetitionClicked(List<IRound> rounds) {


		clientFactory.getRpcService().fetchCompetition(url, rounds, teams, new AsyncCallback<ICompetition>() {

			@Override
			public void onFailure(Throwable caught) {


			}

			@Override
			public void onSuccess(ICompetition result) {
				view.getCompView().showCompetition(result);

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
				view.getCompView().showStatus(caught.getMessage());
			}

			@Override
			public void onSuccess(ICompetition result) {
				view.getCompView().showCompetition(result);

			}
		});			
	}

	//
	//	@Override
	//	public void roundClicked(String roundName) {
	//		clientFactory.getRpcService().fetchRound(url, new AsyncCallback<Map<String,IMatchGroup>>() {
	//
	//			@Override
	//			public void onFailure(Throwable caught) {
	//				
	//				
	//			}
	//
	//			@Override
	//			public void onSuccess(Map<String, IMatchGroup> result) {
	//				view.getCompView().showRound(result);
	//				
	//			}
	//		});	
	//		
	//	}

	@Override
	public void fetchTeamsClicked(String url, String resultType) {
		this.url = url;
		clientFactory.getRpcService().fetchTeams(url, resultType, new AsyncCallback<Map<String, ITeamGroup>>() {

			@Override
			public void onFailure(Throwable caught) {
				view.getCompView().showStatus(caught.getMessage());

			}

			@Override
			public void onSuccess(Map<String, ITeamGroup> result) {
				for (ITeamGroup t: result.values()) {
					teams.add(t);
				}
				view.getCompView().showTeams(result);

			}
		});		
	}

	@Override
	public void fetchMatchesClicked(Map<String,ITeamGroup> teams) {
		clientFactory.getRpcService().fetchMatches(url, teams, new AsyncCallback<Map<String,IMatchGroup>>() {

			@Override
			public void onFailure(Throwable caught) {
				view.getCompView().showStatus(caught.getMessage());
			}

			@Override
			public void onSuccess(Map<String, IMatchGroup> result) {
				view.getCompView().showMatches(result);

			}
		});		
	}

	@Override
	public void fetchRoundsClicked(Map<String,IMatchGroup> matches) {
		clientFactory.getRpcService().fetchRounds(url, matches, new AsyncCallback<List<IRound> >() {

			@Override
			public void onFailure(Throwable caught) {
				view.getCompView().showStatus(caught.getMessage());
			}

			@Override
			public void onSuccess(List<IRound> result) {
				rounds = result;
				view.getCompView().showRounds(result);

			}
		});	
	}

	@Override
	public void saveMatchesClicked(Map<String, IMatchGroup> matchMap) {
		clientFactory.getRpcService().saveMatches(matchMap, new AsyncCallback<Map<String, IMatchGroup>>() {

			@Override
			public void onFailure(Throwable caught) {
				view.getCompView().showStatus(caught.getMessage());
			}

			@Override
			public void onSuccess(Map<String, IMatchGroup> result) {
				view.getCompView().showMatches(result);

			}
		});		

	}

	@Override
	public void saveRoundsClicked(List<IRound> roundList) {
		clientFactory.getRpcService().saveRounds(roundList, new AsyncCallback<List<IRound> >() {

			@Override
			public void onFailure(Throwable caught) {
				view.getCompView().showStatus(caught.getMessage());
			}

			@Override
			public void onSuccess(List<IRound> result) {
				view.getCompView().showRounds(result);

			}
		});		

	}

	@Override
	public void saveTeamsClicked(Map<String, ITeamGroup> teamMap) {

		//view.getCompView().showTeams(teamMap);

		clientFactory.getRpcService().saveTeams(teamMap, new AsyncCallback<Map<String, ITeamGroup>>() {

			@Override
			public void onFailure(Throwable caught) {
				view.getCompView().showStatus(caught.getMessage());
			}

			@Override
			public void onSuccess(Map<String, ITeamGroup> result) {
				view.getCompView().showTeams(result);

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
	 * @see net.rugby.foundation.admin.client.ui.OrchestrationConfigurationView.Presenter#saveClicked(java.util.Map)
	 */
	@Override
	public Map<String, IOrchestrationConfiguration> saveClicked(Map<String, IOrchestrationConfiguration> configs) {

		clientFactory.getRpcService().saveOrchestrationConfiguration(configs, new AsyncCallback<Map<String, IOrchestrationConfiguration>>() {

			@Override
			public void onFailure(Throwable caught) {
				view.getOrchestrationConfig().showStatus(caught.getMessage());
			}

			@Override
			public void onSuccess(Map<String, IOrchestrationConfiguration> result) {
				view.getOrchestrationConfig().showStatus("Success");
				orchConfig = result;
				Window.alert("Saved");
			}
		});
		return orchConfig;	
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.CompetitionView.Presenter#teamsClicked(long)
	 */
	@Override
	public void teamsClicked(final Long compId) {
		clientFactory.getRpcService().getTeams(compId, new AsyncCallback<List<ITeamGroup>>() {

			@Override
			public void onFailure(Throwable caught) {


			}

			@Override
			public void onSuccess(List<ITeamGroup> result) {

				//view.getCompView().addTeams(compId,result);

			}
		});		

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.CompetitionView.Presenter#roundsClicked(long)
	 */
	@Override
	public void roundsClicked(final Long compId) {
		clientFactory.getRpcService().getRounds(compId, new AsyncCallback<List<IRound>>() {

			@Override
			public void onFailure(Throwable caught) {


			}

			@Override
			public void onSuccess(List<IRound> result) {

				//view.getCompView().addRounds(compId,result);

			}
		});			
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.CompetitionView.Presenter#roundClicked(java.lang.Long, java.lang.Long)
	 */
	@Override
	public void roundClicked(final Long compId, final Long roundId) {
		clientFactory.getRpcService().getMatches(roundId, new AsyncCallback<List<IMatchGroup>>() {

			@Override
			public void onFailure(Throwable caught) {


			}

			@Override
			public void onSuccess(List<IMatchGroup> result) {

//				view.getCompView().addRound(compId, roundId, result);

			}
		});			
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.CompetitionView.Presenter#resultsClicked(long)
	 */
	@Override
	public void resultsClicked(final Long compId, final Long roundId, final Long matchId) {
		clientFactory.getRpcService().getResults(matchId, new AsyncCallback<List<IMatchResult>>() {

			@Override
			public void onFailure(Throwable caught) {


			}

			@Override
			public void onSuccess(List<IMatchResult> result) {

				//view.getCompView().addResults(compId, roundId, matchId, result);

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

				view.getCompView().showStatus("team info saved");

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
		////			clientFactory.getRpcService().getTeam(teamId, new AsyncCallback<ITeamGroup>() {
		//			Core.getCore().getComp(compId, new AsyncCallback<ICompetition>() {
		//
		//				@Override
		//				public void onFailure(Throwable caught) {
		//					
		//					Window.alert("Couldn't get comp info " + caught.getMessage());
		//				}
		//
		//				@Override
		//				public void onSuccess(ICompetition result) {
		//					//ec.SetPresenter(presenter);
		//					ec.ShowComp(result);
		//					
		//				}
		//			});		

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
	public void editMatchInit(EditMatch editMatch, long matchId, long roundId, long compId) {
		final EditMatch.Presenter presenter = this;  // there must be a way to do this...
		em = editMatch;
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
				view.getCompView().showStatus(result.toString());


			}
		});		

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.EditMatch.Presenter#fetchScore(net.rugby.foundation.model.shared.IMatchGroup)
	 */
	@Override
	public void fetchScore(IMatchGroup matchGroup) {
		List<String> log = new ArrayList<String>();
		clientFactory.getRpcService().fetchMatchScore(matchGroup, currentCompId, log, new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {

				Window.alert("Match score not fetched: " + caught.getMessage());
			}

			@Override
			public void onSuccess(List<String> result) {
				//ec.SetPresenter(presenter);
				//				if (result != null)
				//					Window.alert("Match saved");
				//				else
				//					Window.alert("Comp not saved");
				view.getCompView().showStatus(result.toString());


			}
		});		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.EditMatch.Presenter#fetchPlayers(net.rugby.foundation.model.shared.IMatchGroup)
	 */
	@Override
	public void fetchPlayers(IMatchGroup matchGroup) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.EditMatch.Presenter#fetchMatchStats(net.rugby.foundation.model.shared.IMatchGroup)
	 */
	@Override
	public void fetchMatchStats(IMatchGroup matchGroup) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.EditMatch.Presenter#fetchPlayerStats(net.rugby.foundation.model.shared.IMatchGroup)
	 */
	@Override
	public void fetchPlayerStats(IMatchGroup matchGroup) {
		// TODO Auto-generated method stub

	}

	@Override
	public void savePlayerInfo(IPlayer player) {
		if (emailHandlerPlace != null) {
			clientFactory.getRpcService().savePlayer(player, emailHandlerPlace.getPromisedHandle(), new AsyncCallback<IPlayer>() {

				@Override
				public void onFailure(Throwable caught) {

					Window.alert("Player not saved: " + caught.getMessage());
				}

				@Override
				public void onSuccess(IPlayer result) {
					//ec.SetPresenter(presenter);
					//				if (result != null)
					//					Window.alert("Match saved");
					//				else
					//					Window.alert("Comp not saved");
					view.getEditPlayer().ShowPlayer(result);


				}
			});		

		}
		
	}

	@Override
	public void testMatchStatsClicked(Long matchId) {
		clientFactory.getRpcService().testMatchStats(matchId, new AsyncCallback<List<IPlayerMatchStats>>() {

			@Override
			public void onFailure(Throwable caught) {

				Window.alert("Player not saved: " + caught.getMessage());
			}

			@Override
			public void onSuccess(List<IPlayerMatchStats> result) {
				//ec.SetPresenter(presenter);
				//				if (result != null)
				//					Window.alert("Match saved");
				//				else
				//					Window.alert("Comp not saved");
				//view.getEditPlayer().ShowPlayer(result);


			}
		});	
		}

	@Override
	public IPlayer getNewPlayer() {
		// TODO Auto-generated method stub
		return new ScrumPlayer();
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.WorkflowConfigurationView.Presenter#saveWorkflowConfiguration(java.util.List)
	 */
	//	@Override
	//	public void saveWorkflowConfiguration(List<IWorkflow> workflows) {
	//		// TODO Auto-generated method stub
	//
	//	}


}
