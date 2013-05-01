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
import net.rugby.foundation.admin.client.ui.EditTeam;
import net.rugby.foundation.admin.client.ui.SmartBar;
import net.rugby.foundation.admin.client.ui.playerlistview.PlayerListView;
import net.rugby.foundation.admin.client.ui.playermatchstatspopup.PlayerMatchStatsPopupView.PlayerMatchStatsPopupViewPresenter;
import net.rugby.foundation.admin.client.ui.playerpopup.PlayerPopupView;
import net.rugby.foundation.admin.shared.EditPlayerAdminTask;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.IRound;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DialogBox;

/**
 * Activities are started and stopped by an ActivityManager associated with a container Widget.
 */
public class CompActivity extends AbstractActivity implements  
CompetitionView.Presenter, EditTeam.Presenter, EditComp.Presenter, 
EditMatch.Presenter, PlayerListView.Listener<IPlayerMatchInfo>, PlayerPopupView.Presenter<IPlayer>,
PlayerMatchStatsPopupViewPresenter<IPlayerMatchStats>, SmartBar.Presenter {
	/**
	 * Used to obtain views, eventBus, placeController.
	 * Alternatively, could be injected via GIN.
	 */
	private ClientFactory clientFactory;
	CompetitionView view = null;
	private String url;
	private SelectionModel<IPlayerMatchInfo> selectionModel;
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
	private PlayerListView<IPlayerMatchInfo> plv;
	private AdminCompPlace place;

	public CompActivity(AdminCompPlace place, ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		view = clientFactory.getCompView();
		selectionModel = new SelectionModel<IPlayerMatchInfo>();
		this.place = place;
	}



	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		view.setPresenter(this);
		clientFactory.getPlayerPopupView().setPresenter(this);
		clientFactory.getPlayerMatchStatsPopupView().setPresenter(this);

		containerWidget.setWidget(view.asWidget());

		if (!view.isAllSetup()) {
			clientFactory.getRpcService().getComps(place.getFilter(), new AsyncCallback<List<ICompetition>>() {

				@Override
				public void onFailure(Throwable caught) {


				}

				@Override
				public void onSuccess(List<ICompetition> result) {
					comps = result;
					view.addComps(result);
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
		view.showWait(true);
		view.setInitialized(false);
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
	//				view.showRound(result);
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
				view.showStatus(caught.getMessage());

			}

			@Override
			public void onSuccess(Map<String, ITeamGroup> result) {
				for (ITeamGroup t: result.values()) {
					teams.add(t);
				}
				view.showTeams(result);

			}
		});		
	}

	@Override
	public void fetchMatchesClicked(Map<String,ITeamGroup> teams) {
		clientFactory.getRpcService().fetchMatches(url, teams, new AsyncCallback<Map<String,IMatchGroup>>() {

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
	public void fetchRoundsClicked(Map<String,IMatchGroup> matches) {
		clientFactory.getRpcService().fetchRounds(url, matches, new AsyncCallback<List<IRound> >() {

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

	@Override
	public void saveMatchesClicked(Map<String, IMatchGroup> matchMap) {
		clientFactory.getRpcService().saveMatches(matchMap, new AsyncCallback<Map<String, IMatchGroup>>() {

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
	public void saveRoundsClicked(List<IRound> roundList) {
		clientFactory.getRpcService().saveRounds(roundList, new AsyncCallback<List<IRound> >() {

			@Override
			public void onFailure(Throwable caught) {
				view.showStatus(caught.getMessage());
			}

			@Override
			public void onSuccess(List<IRound> result) {
				view.showRounds(result);

			}
		});		

	}

	@Override
	public void saveTeamsClicked(Map<String, ITeamGroup> teamMap) {

		//view.showTeams(teamMap);

		clientFactory.getRpcService().saveTeams(teamMap, new AsyncCallback<Map<String, ITeamGroup>>() {

			@Override
			public void onFailure(Throwable caught) {
				view.showStatus(caught.getMessage());
			}

			@Override
			public void onSuccess(Map<String, ITeamGroup> result) {
				view.showTeams(result);

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

				//view.addTeams(compId,result);

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

				//view.addRounds(compId,result);

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

				view.addRound(compId, roundId, result);

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

				//view.addResults(compId, roundId, matchId, result);

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
	public void editMatchInit(EditMatch editMatch, PlayerListView<IPlayerMatchInfo> editMatchInfo, final long matchId, long roundId, long compId) {
		final EditMatch.Presenter presenter = this;  // there must be a way to do this...
		em = editMatch;

		final PlayerListView.Listener<IPlayerMatchInfo> presenter2 = this;  // there must be a way to do this...
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

				clientFactory.getRpcService().getPlayerMatchInfo(matchId, new AsyncCallback<List<IPlayerMatchInfo>>() {

					@Override
					public void onFailure(Throwable caught) {


					}

					@Override
					public void onSuccess(List<IPlayerMatchInfo> result) {
						plv.setListener(presenter2);
						plv.setPlayers(result);
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
				em.ShowMatch(result);

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
		clientFactory.getRpcService().fetchMatchStats(matchGroup.getId(), new AsyncCallback<List<IPlayerMatchInfo>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed fetching Match Stats: " + caught.getMessage());
			}

			@Override
			public void onSuccess(List<IPlayerMatchInfo> result) {
				view.getPlayerListView().setPlayers(result);
			}
		});	
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.EditMatch.Presenter#fetchPlayerStats(net.rugby.foundation.model.shared.IMatchGroup)
	 */
	@Override
	public void fetchPlayerStats(IMatchGroup matchGroup) {
		// TODO Auto-generated method stub

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

				Window.alert(result.toString());

			}
		});	
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
		clientFactory.getPlayerMatchStatsPopupView().setTarget(info.getPlayerMatchStats());
		((DialogBox) clientFactory.getPlayerMatchStatsPopupView()).center();
	}

	@Override
	public void showEditRating(IPlayerMatchInfo player) {
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

		clientFactory.getRpcService().savePlayerMatchStats(pms, null, new AsyncCallback<IPlayerMatchInfo>() {

			@Override
			public void onFailure(Throwable caught) {

				Window.alert("Player Stats not saved: " + caught.getMessage());
			}

			@Override
			public void onSuccess(IPlayerMatchInfo result) {

				view.getPlayerListView().updatePlayerMatchStats(result);
				//				Window.alert("Player Stats saved");
				//				((DialogBox) clientFactory.getPlayerMatchStatsPopupView()).hide();
				//				clientFactory.getRpcService().getMatch(pms.getMatchId(), new AsyncCallback<IMatchGroup>() {
				//
				//					@Override
				//					public void onFailure(Throwable caught) {
				//
				//
				//					}
				//
				//					@Override
				//					public void onSuccess(IMatchGroup result) {
				//						fetchMatchStats(result);
				//					}
				//				});	

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
	public void showPlayerPopup(IPlayerMatchStats target) {
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





}
