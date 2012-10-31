package net.rugby.foundation.game1.client.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.core.client.Identity;
import net.rugby.foundation.game1.client.ClientFactory;
import net.rugby.foundation.game1.client.place.Game1Place;
import net.rugby.foundation.game1.client.ui.Game1ConfigurationView;
import net.rugby.foundation.game1.client.ui.Game1View;
import net.rugby.foundation.game1.client.ui.Game1View.Presenter;
import net.rugby.foundation.game1.client.ui.ClubhouseView;
import net.rugby.foundation.game1.client.ui.HomeView;
import net.rugby.foundation.game1.client.ui.MatchStatsView;
import net.rugby.foundation.game1.client.ui.NewEntryView;
import net.rugby.foundation.game1.client.ui.NewEntryViewImpl;
import net.rugby.foundation.game1.client.ui.PlayPanel;
import net.rugby.foundation.game1.client.ui.PlayView;
import net.rugby.foundation.game1.client.ui.SmartBar;
import net.rugby.foundation.game1.shared.IConfiguration;
import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.game1.shared.ILeaderboard;
import net.rugby.foundation.game1.shared.IMatchEntry;
import net.rugby.foundation.game1.shared.IMatchStats;
import net.rugby.foundation.game1.shared.IRoundEntry;
import net.rugby.foundation.game1.shared.MatchEntry;
import net.rugby.foundation.game1.shared.RoundEntry;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.IClubhouseMembership;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamGroup;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * Activities are started and stopped by an ActivityManager associated with a container Widget.
 */
public class Game1Activity extends AbstractActivity implements Presenter, SmartBar.Presenter, HomeView.Presenter, NewEntryView.Presenter, PlayView.Presenter, PlayPanel.Presenter, net.rugby.foundation.core.client.ui.CreateClubhouse.Presenter, Game1ConfigurationView.Presenter, ClubhouseView.Presenter, MatchStatsView.Presenter{
	private ICoreConfiguration coreConfig;
	/**
	 * Used to obtain views, eventBus, placeController.
	 * Alternatively, could be injected via GIN.
	 */
	private ClientFactory clientFactory;
	private Game1Place place;
	private NewEntryViewImpl nev = null;
	private int numEntries = 1;

	private net.rugby.foundation.game1.shared.IConfiguration game1Config;

	public Game1Activity(Game1Place place, ClientFactory clientFactory) {
		this.place = place;
		this.clientFactory = clientFactory;
	}

	// When start() is done Core.isInitialized() needs to be true
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		final Game1View view = clientFactory.getGame1View();

		// get the core configuration, competition and entries
		view.clear(false);
		Core.getInstance().getConfiguration(new AsyncCallback<ICoreConfiguration>() {

			@Override
			public void onFailure(Throwable caught) {
				Logger.getLogger("Game1Activity").log(Level.SEVERE, "start.getConfiguration: " + caught.getMessage(), caught);
			}

			@Override
			public void onSuccess(final ICoreConfiguration config) {
				coreConfig = config;
				view.setComps(coreConfig.getCompetitionMap());

				// figure out a competition to work with
				Long compId = null;

				// save off our current place in case the user does account actions
				Game1Place.Tokenizer tokenizer = new Game1Place.Tokenizer();
				String url = "Game1Place:"; 
//				if (!GWT.isProdMode())
//					url = "/index.html?gwt.codesvr=127.0.0.1:9997#Game1Place" + ":";
//				else
//					url = "/index.html?#Game1Place" + ":";
				Core.getCore().getClientFactory().getIdentityManager().setDestination(url+tokenizer.getToken(place));
				//clientFactory.setPostCoreDestination(place);
				// order of precedence of competition selectors:
				//	1) specified in URL
				//	2) user preference (LoginInfo.getLastComp)
				//	3) site default
				if (place.getCompetitionId() != null && place.getCompetitionId() != 0L) {
					compId = place.getCompetitionId();
				} else {
					// is anyone logged in?
					if (Core.getCore().getClientFactory().getLoginInfo().isLoggedIn()) {

						// go to the user's last competition if there is one or the site default if not
						compId = clientFactory.getCoreClientFactory().getLoginInfo().getLastCompetitionId();
						if (compId == null)
							compId = coreConfig.getDefaultCompId();
					} else {
						clientFactory.getGame1View().getHomeView().showCreateClubhouse(false);
						clientFactory.getGame1View().getClubhouseView().showCreateClubhouse(false);
						compId = coreConfig.getDefaultCompId();
					}
				}


				Core.getCore().getComp(compId, new AsyncCallback<ICompetition>() {

					@Override
					public void onFailure(Throwable caught) {
						Logger.getLogger("Game1Activity").log(Level.SEVERE,"start.getComp: " +  caught.getMessage(), caught);						
					}

					@Override
					public void onSuccess(final ICompetition comp) {

						// this triggers the result panel to show the proper scores
						Core.getCore().setCurrentCompId(comp.getId());

						assert(Core.getCore().isInitialized());

						// if no-one is logged on we are done
						if (Core.getCore().getClientFactory().getLoginInfo().isLoggedIn()) {
							clientFactory.getRpcservice().getEntriesForCurrentUser(comp.getId(), new AsyncCallback<ArrayList<IEntry>>() {

								@Override
								public void onFailure(Throwable caught) {
									Logger.getLogger("Game1Activity").log(Level.SEVERE, "start.getEntriesForCurrentUser: " +  caught.getMessage(), caught);
								}

								@Override
								public void onSuccess(ArrayList<IEntry> entries) {
									clientFactory.getGame1View().getHomeView().showCreateClubhouse(true);
									clientFactory.getGame1View().getClubhouseView().showCreateClubhouse(true);
									showEntryChoices(entries, comp);
									if (entries != null)
										numEntries = entries.size() + 1;
									showClubhouseChoices();
									Long currentClubhouseId = place.getClubhouseId();
									if (currentClubhouseId != null && currentClubhouseId != 0L) {
										Core.getCore().getClubhouse(currentClubhouseId, new AsyncCallback<IClubhouse>() {

											@Override
											public void onFailure(Throwable caught) {
												Logger.getLogger("Game1Activity").log(Level.SEVERE, "start.getClubhouse: " +  caught.getMessage(), caught);
											}

											@Override
											public void onSuccess(IClubhouse currentClubhouse) {

												setClubhouseContent(comp, currentClubhouse);														
											}

										});

									} else {
										setClubhouseContent(comp, null);
									}
								}
							});
						}  else {
							//setClubhouseContent(comp, null);
							view.selectTab(0);
							view.clear(true);
						}											
					}					
				});

			}
		});

		view.setPresenter(this);

		containerWidget.setWidget(view.asWidget());
	}

	/**
	 * populate the home tab's entry table and the smartBar's entry menu
	 * @param entries
	 * @param comp 
	 */
	private void showEntryChoices(List<IEntry> entries, ICompetition comp) {
		final Game1View view = clientFactory.getGame1View();
		view.getHomeView().setEntries(entries);
		view.getSmartBar().setEntries(entries);

		if (!comp.getUnderway()) {
			view.getHomeView().showCompOver(comp);
		}
		view.selectTab((int)(long)place.getTab());
	}

	/**
//	 * populate the home tab's entry table and the smartBar's entry menu
//	 * @param entries
//	 */
	//	private void showCompetitionsChoices(IConfiguration config) {
	//		final Game1View view = clientFactory.getGame1View();
	//
	//		view.getSmartBar().setComps(config.getCompetitionMap());
	//	}

	/**
	 * populate the smartBar Clubhouse menu
	 */
	private void showClubhouseChoices() {
		assert (Core.getCore().getClientFactory().getLoginInfo().isLoggedIn());

		// get the game local config
		clientFactory.getRpcservice().getConfiguration(new AsyncCallback<net.rugby.foundation.game1.shared.IConfiguration>() {

			@Override
			public void onFailure(Throwable caught) {
				Logger.getLogger("Game1Activity").log(Level.SEVERE, "showClubhouseChoices.getConfiguration" + caught.getMessage());
			}

			@Override
			public void onSuccess(final net.rugby.foundation.game1.shared.IConfiguration config) {
				game1Config = config;
				// Factors here are:
				//		1) The list of clubhouses the user belongs to - displayed in the Smartbar.
				//		2) The last clubhouse visited - displayed as the selected Clubhouse in the SmartBar and also on the Clubhouse tab
				//clientFactory.getRpcservice().getClubhouse(comp.getId(), new AsyncCallback<ArrayList<IEntry>>() {
				Core.getCore().getClubhouses(new AsyncCallback<List<IClubhouse>>()  {
					@Override
					public void onFailure(Throwable caught) {
						Logger.getLogger("Game1Activity").log(Level.SEVERE, "showClubhouseChoices.getClubhouses" + caught.getMessage());
					}

					@Override
					public void onSuccess(List<IClubhouse> entries) {
						if (entries != null)
							clientFactory.getGame1View().getSmartBar().setClubhouses(entries, game1Config);
						//}
					}
				});		
			}
		});	

	}

	/**
	 * @see SmartBar.Presenter#goTo(Place)
	 */
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}



	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.HomeView.Presenter#newEntryClicked()
	 */
	@Override
	public void newEntryClicked() {
		if (nev == null)
			nev = new NewEntryViewImpl();
		nev.setPresenter(this);
		assert (Core.getCore().isInitialized());
		if (Core.getCore().isInitialized()) {
			ICompetition comp = Core.getCore().getCurrentComp();
			if (comp != null)
				nev.setCompName(comp.getShortName());
			nev.setUserName(clientFactory.getCoreClientFactory().getLoginInfo().getNickname());
			nev.setCount(numEntries);
			nev.center();
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.NewEntryView.Presenter#onCreate(java.lang.String)
	 */
	@Override
	public void onCreate(final String entryName) {

		if (Core.getCore().isInitialized()) {
			//first check they haven't duplicated the name
			clientFactory.getRpcservice().getEntryByName(entryName, Core.getCore().getCurrentCompId(),  new AsyncCallback<IEntry>() {

				@Override
				public void onFailure(Throwable caught) {
					Logger.getLogger("Game1Activity").log(Level.SEVERE, "onCreate.getEntryByName" + caught.getMessage());

				}

				@Override
				public void onSuccess(IEntry result) {
					if (result == null) { // don't already have one of this name
						clientFactory.getRpcservice().createEntry(entryName, Core.getCore().getCurrentCompId(), new AsyncCallback<IEntry>() {

							@Override
							public void onFailure(Throwable caught) {
								Logger.getLogger("Game1Activity").log(Level.SEVERE, "onCreate.createEntry" + caught.getMessage());
							}

							@Override
							public void onSuccess(final IEntry entry) {
								nev.hide();
								clientFactory.getGame1View().getPlayView().setEntry(entry, Core.getCore().getCurrentComp());
								clientFactory.getGame1View().selectTab(1);						
							}
						});
					} else {
						// they already have one of this name
						Window.alert("You already have an entry with this name for this competition. Please pick a new name.");
					}
				}


			});
		}


	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.NewEntryView.Presenter#onCancel()
	 */
	@Override
	public void onCancel()
	{
		nev.hide();
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.HomeView.Presenter#entryClicked(net.rugby.foundation.game1.shared.Entry)
	 */
	@Override
	public void entryClicked(IEntry e) {
		assert(Core.getCore().isInitialized());

		if (Core.getCore().isInitialized()) {
			setEntryContent(e);		
			clientFactory.getGame1View().selectTab(1);	

			Long leagueId = game1Config.getLeagueIdMap().get(Core.getCore().getCurrentCompId());
			// TODO should lazy load this
			if (leagueId != null) {
				setLeaderboardContent(leagueId);
			}

			//TODO lazy load this
			if (Core.getCore().getCurrentClubhouse() != null && Core.getCore().getCurrentComp() != null) {
				//find the clubhouse's league
				setClubhouseContent(Core.getCore().getCurrentComp(), Core.getCore().getCurrentClubhouse());
			}
		}
	}

	private void setEntryContent(IEntry entry) {
		assert(Core.getCore().isInitialized());

		if (Core.getCore().isInitialized()) {
			clientFactory.getGame1View().getPlayView().setEntry(entry, Core.getCore().getCurrentComp());
		}

	}

	private void setLeaderboardContent(Long leagueId)	{

		clientFactory.getRpcservice().getLeaderboard(Core.getCore().getCurrentCompId(), Core.getCore().getCurrentComp().getCompClubhouseId(), new AsyncCallback<ILeaderboard>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Fail!");				
				clientFactory.getGame1View().getLeaderboardView().setData(null);
			}

			@Override
			public void onSuccess(final ILeaderboard lb) {

				// it's ok if we get back null, the view will just show a "No leaderboard available yet" message.
				clientFactory.getGame1View().getLeaderboardView().setData(lb);

			}
		});

	}


	private void setClubhouseContent(ICompetition comp, final IClubhouse clubhouse) {

		if (clubhouse == null) {
			clientFactory.getGame1View().getClubhouseView().clear();
		} else {
			assert(Core.getCore().isInitialized());

			if (Core.getCore().isInitialized()) {

				if (Core.getCore().getClientFactory().getLoginInfo().getLastClubhouseId() != null) {
					clientFactory.getRpcservice().getLeaderboard(Core.getCore().getCurrentCompId(), clubhouse.getId(), new AsyncCallback<ILeaderboard>() {

						@Override
						public void onFailure(Throwable caught) {	
							clientFactory.getGame1View().getClubhouseView().clear();
						}

						@Override
						public void onSuccess(final ILeaderboard lb) {
							// ok if it's null - just means that they have a clubhouse but there's no leaderboard yet (comp hasn't started)
							Core.getCore().getClubhouseMembers(clubhouse.getId(), new AsyncCallback<List<IClubhouseMembership>> () {
								@Override
								public void onFailure(Throwable caught) {
									clientFactory.getGame1View().getClubhouseView().clear();
								}

								@Override
								public void onSuccess(List<IClubhouseMembership> members) {	
									clientFactory.getGame1View().getClubhouseView().setData(lb, clubhouse, members);
								}
							});
						}
					});
				} else {
					clientFactory.getGame1View().getClubhouseView().clear();
				}
			}
		}

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.PlayView.Presenter#saveEntry(net.rugby.foundation.game1.shared.IEntry)
	 */
	@Override
	public void saveEntry(IEntry entry) {
		clientFactory.getRpcservice().saveEntry(entry, new AsyncCallback<IEntry>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Fail!");

			}

			@Override
			public void onSuccess(final IEntry entry) {
				clientFactory.getGame1View().getPlayView().setEntry(entry, Core.getCore().getCurrentComp());
				Window.alert("Saved");
				clientFactory.getRpcservice().getEntriesForCurrentUser(Core.getCore().getCurrentCompId(), new AsyncCallback<ArrayList<IEntry>>() {

					@Override
					public void onFailure(Throwable caught) {
						Logger.getLogger("Game1Activity").log(Level.SEVERE, "start.getEntriesForCurrentUser: " +  caught.getMessage());
					}

					@Override
					public void onSuccess(ArrayList<IEntry> entries) {
						showEntryChoices(entries, Core.getCore().getCurrentComp());
						numEntries = entries.size() + 1;
					}
				});
			}
		});
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.PlayPanel.Presenter#pickMade(java.lang.Long, boolean)
	 */
	@Override
	public void pickMade(IEntry entry, IRound round, IMatchGroup match, ITeamGroup team) {
		if (entry.getRoundEntries().get(round.getId()) == null) {
			IRoundEntry re = new RoundEntry(); // TODO GIN?
			re.setRoundId(round.getId());

			// last match of the round is the tiebreaker
			re.setTieBreakerMatchId(round.getMatches().get(round.getMatches().size()-1).getId()); 
			entry.getRoundEntries().put(round.getId(), re);
		}

		//remove any existing picks for this match
		entry.getRoundEntries().get(round.getId()).getMatchPickMap().remove(match.getId());

		//TODO GIN!
		IMatchEntry me = new MatchEntry(null, match.getId(), team.getId());

		entry.getRoundEntries().get(round.getId()).getMatchPickMap().put(match.getId(), me);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.PlayView.Presenter#tiebreakerHomeScoreSet(net.rugby.foundation.game1.shared.IEntry, net.rugby.foundation.model.shared.IRound, net.rugby.foundation.model.shared.IMatchGroup, net.rugby.foundation.model.shared.ITeamGroup, int)
	 */
	@Override
	public void tiebreakerHomeScoreSet(IEntry entry, IRound round, IMatchGroup m, ITeamGroup homeTeam, int score) {

		if (entry.getRoundEntries().get(round.getId()) == null) {
			IRoundEntry re = new RoundEntry(); // TODO GIN?
			re.setRoundId(round.getId());
			entry.getRoundEntries().put(round.getId(), re);
		}

		entry.getRoundEntries().get(round.getId()).setTieBreakerMatchId(m.getId());		
		entry.getRoundEntries().get(round.getId()).setTieBreakerMatch(m);
		entry.getRoundEntries().get(round.getId()).setTieBreakerHomeScore(score);		

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.PlayView.Presenter#tiebreakerVisitScoreSet(net.rugby.foundation.game1.shared.IEntry, net.rugby.foundation.model.shared.IRound, net.rugby.foundation.model.shared.IMatchGroup, net.rugby.foundation.model.shared.ITeamGroup, int)
	 */
	@Override
	public void tiebreakerVisitScoreSet(IEntry entry, IRound round,
			IMatchGroup m, ITeamGroup visitingTeam, int score) {
		if (entry.getRoundEntries().get(round.getId()) == null) {
			IRoundEntry re = new RoundEntry(); // TODO GIN?
			re.setRoundId(round.getId());
			entry.getRoundEntries().put(round.getId(), re);
		}

		entry.getRoundEntries().get(round.getId()).setTieBreakerMatchId(m.getId());		
		entry.getRoundEntries().get(round.getId()).setTieBreakerMatch(m);
		entry.getRoundEntries().get(round.getId()).setTieBreakerVisitScore(score);		

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.HomeView.Presenter#getClientFactory()
	 */
	@Override
	public ClientFactory getClientFactory() {
		return clientFactory;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.SmartBar.Presenter#compPicked(java.lang.Long)
	 */
	@Override
	public void compPicked(final Long id) {

		goTo(new Game1Place(place.getEntryId(),place.getClubhouseId(),id,0L));

		//		clientFactory.getCoreClientFactory().getLoginInfo().setLastCompetitionId(id);
		//		Core.getCore().getComp(id, new AsyncCallback<ICompetition>() {
		//
		//			@Override
		//			public void onFailure(Throwable caught) {
		//				Logger.getLogger("Game1Activity").log(Level.SEVERE,"start.getComp: " +  caught.getMessage());						
		//			}
		//
		//			@Override
		//			public void onSuccess(final ICompetition comp) {
		////				Core.getCore().setCurrentCompId(id);  // should always call this from within the getComp.onSuccess so you have the Competition info on hand when the Result panel is looking for it.
		////				assert(Core.getCore().isInitialized());
		////				place.setCompetitionId(id);
		//				goTo(new Game1Place(place.getEntryId(),place.getClubhouseId(),id,0L));
		//			}
		//		});

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.SmartBar.Presenter#entryPicked(java.lang.Long)
	 */
	@Override
	public void entryPicked(IEntry e) {
		//		//Core.getCore().setCurrentEntryId(id);
		//		Core.getCore().getClientFactory().getLoginInfo().setLastEntryId(id);
		//		place.setEntryId(id);
		//		goTo(new Game1Place(id,place.getClubhouseId(),place.getCompetitionId(),1L));
		entryClicked(e);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.SmartBar.Presenter#clubhousePicked(java.lang.Long)
	 */
	@Override
	public void clubhousePicked(Long id) {
		Core.getCore().setCurrentClubhouseId(id);
		Core.getCore().getClientFactory().getLoginInfo().setLastClubhouseId(id);
		place.setClubhouseId(id);

		Core.getCore().getClubhouse(id, new AsyncCallback<IClubhouse>() {

			@Override
			public void onFailure(Throwable caught) {
				Logger.getLogger("Game1Activity").log(Level.SEVERE, "start.getClubhouse: " +  caught.getMessage());
			}

			@Override
			public void onSuccess(IClubhouse currentClubhouse) {
				setClubhouseContent(Core.getCore().getCurrentComp(), currentClubhouse);

			}

		});	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.Game1View.Presenter#getPlace()
	 */
	@Override
	public Game1Place getPlace() {
		return place;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.Game1View.Presenter#setCurrentTab(int)
	 */
	@Override
	public void setCurrentTab(int tab) {
		if (clientFactory.getPlaceController().getWhere() instanceof Game1Place)
			place.setTab((long) tab);

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.Game1View.Presenter#getCurrentTab()
	 */
	@Override
	public int getCurrentTab() {

		return (int)(long)place.getTab();
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.ui.CreateClubhouse.Presenter#clubhouseclubhouseCreated(net.rugby.foundation.model.shared.IClubhouse)
	 */
	@Override
	public void clubhouseCreated(final IClubhouse clubhouse) {

		// so the new clubhouse has been created. Now we need to:
		//	1) create the CLMs and League for any active comps
		clientFactory.getRpcservice().createNewClubhouseLeagues(clubhouse, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Logger.getLogger("Game1Activity").log(Level.SEVERE, "clubhouseCreated: " +  caught.getMessage());

			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {

					// update the smartbar
					Core.getCore().getClientFactory().getLoginInfo().setLastClubhouseId(clubhouse.getId());

					// show the new clubhouse in the clubhouse tab
					goTo(new Game1Place(place.getEntryId(),clubhouse.getId(),Core.getCore().getCurrentCompId(),3L));

				}

			}

		});


	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.ui.CreateClubhouse.Presenter#clubhouseCreationCanceled()
	 */
	@Override
	public void clubhouseCreationCanceled() {
		// no-op?

	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.Game1ConfigurationView.Presenter#saveClicked(net.rugby.foundation.game1.shared.IConfiguration, java.util.List, java.util.List)
	 */
	@Override
	public net.rugby.foundation.game1.shared.IConfiguration saveClicked(
			net.rugby.foundation.game1.shared.IConfiguration config,
			List<Long> compsToAdd, List<Long> compsToDrop) {
		clientFactory.getRpcservice().updateConfiguration(config, compsToAdd, compsToDrop, new AsyncCallback<net.rugby.foundation.game1.shared.IConfiguration>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Save failed");

			}

			@Override
			public void onSuccess(net.rugby.foundation.game1.shared.IConfiguration result) {
				Window.alert("Config saved");

			}

		});

		return config;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.Game1View.Presenter#isAdmin()
	 */
	@Override
	public boolean isAdmin() {

		return true;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.Game1ConfigurationView.Presenter#populateView()
	 */
	@Override
	public void populateView() {
		//if (Core.getCore().getClientFactory().getLoginInfo().isAdmin()) {
		if (game1Config != null && coreConfig != null) {
			clientFactory.getGame1View().getConfigurationView().setConfig(game1Config, coreConfig);
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.MatchStatsView.Presenter#getMatchStats(java.lang.Long, com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	@Override
	public void getMatchStats(Long matchId, final AsyncCallback<List<IMatchStats>> cb) {
		clientFactory.getRpcservice().getMatchStats(matchId, new AsyncCallback<List<IMatchStats>>() {

			@Override
			public void onFailure(Throwable caught) {
				cb.onFailure(caught);
			}

			@Override
			public void onSuccess(List<IMatchStats> result) {
				cb.onSuccess(result);
			}

		});

		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.Game1ConfigurationView.Presenter#redoMatchStatsClicked(net.rugby.foundation.game1.shared.IConfiguration, java.util.List)
	 */
	@Override
	public void redoMatchStatsClicked(IConfiguration config, List<Long> compsToRedo) {
		clientFactory.getRpcservice().updateMatchStats(config, compsToRedo, new AsyncCallback<net.rugby.foundation.game1.shared.IConfiguration>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Update failed");

			}

			@Override
			public void onSuccess(net.rugby.foundation.game1.shared.IConfiguration result) {
				Window.alert("Update succeeded");

			}

		});
	}


}
