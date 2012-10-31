package net.rugby.foundation.client.activity;

import java.util.ArrayList;

import net.rugby.foundation.client.ClientFactory;
import net.rugby.foundation.client.PlayersServiceAsync;
import net.rugby.foundation.client.place.Home;
import net.rugby.foundation.client.place.Home.actions;
import net.rugby.foundation.client.place.Manage;
import net.rugby.foundation.client.place.Manage.step;
import net.rugby.foundation.client.ui.CreateAccount;
import net.rugby.foundation.client.ui.CreateLeague;
import net.rugby.foundation.client.ui.HomeView;
import net.rugby.foundation.client.ui.NativeLogin;
import net.rugby.foundation.client.ui.ShowContent;
import net.rugby.foundation.model.shared.Group.GroupType;
import net.rugby.foundation.model.shared.CoreConfiguration;
import net.rugby.foundation.model.shared.Clubhouse;
import net.rugby.foundation.model.shared.ClubhouseMembership;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.IClubhouseMembership;
import net.rugby.foundation.model.shared.LoginInfo;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class HomeActivity extends AbstractActivity implements
	HomeView.Presenter<LoginInfo> {
	
	private ClientFactory clientFactory;
	private final PlayersServiceAsync rpcService;
	private Home place;
	private HomeView<LoginInfo> homeView;
	private static String noLoginContent = null;
	private static String noDraftContent = null;
	private static String noRoundContent = null;
	private static String completeContent = null;
	private static String leaderboard = null;
	private static boolean isInLeague = false;
	
	public HomeActivity(Home place, ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		this.rpcService = clientFactory.getRpcService();
		this.place = place;
		//reg = clientFactory.getEventBus().addHandler(AccountActionCompleteEvent.TYPE,callback);

	}

	
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		homeView = clientFactory.getHomeView();
		homeView.setPresenter(this);
		containerWidget.setWidget(homeView.asWidget());
		
		// sanity check - the valid GroupTypes for Home place are MY (logged in) and NONE (not logged in) and TEAM (to not show empty stack panel)
		if (place.getGroupTypeID() != GroupType.NONE && place.getGroupTypeID() != GroupType.MY && place.getGroupTypeID() != GroupType.TEAM)  {
			goTo(new Home(clientFactory.getHomeID(),GroupType.MY,0L,0L,actions.NONE));
			return;
		}
		
		//sanity check - the valid actions are LOGIN, LOGOUT, CREATE, NONE, UPDATE, JOIN, NEWLEAGUE
		actions action = place.getAction();
		if (action != actions.LOGIN && action != actions.LOGOUT && action != actions.CREATE && action != actions.UPDATE && action != actions.NONE && action != actions.NEWLEAGUE && action != actions.JOIN) {
			goTo(new Home(clientFactory.getHomeID(),GroupType.MY,0L,0L,actions.NONE));
			return;			
		}
		
		//sanity check - must be logged on for LOGOUT, NEWLEAGUE
		// @REX getLoginInfo can not be null and getLoginInfo().getLoggedOn() can be false.
		if (clientFactory.getLoginInfo()==null && (action == actions.LOGOUT || action == actions.NEWLEAGUE)) {
			goTo(new Home(clientFactory.getHomeID(),GroupType.MY,0L,0L,actions.NONE));
			return;			
		}

		if (place.getAction() == actions.LOGIN)
		{
			//they need to do this in case of reload
			setView(false,false,false,false,false,false,false);
			homeView.clear();
			showLogin();
		} else if (place.getAction() == actions.CREATE) {
			//need to do this in case of reload
			setView(false,false,false,false,false,false,false);
			homeView.clear();
			showCreate();
		} else if (place.getAction() == actions.OPTOUT) {
			setView(false,false,false,false,false,false,false);
			homeView.clear();
			showPopupContent("You've been unsubscribed.");
			goTo(new Home(clientFactory.getHomeID(),GroupType.MY,0L,0L,actions.NONE));
		} else if (place.getAction() == actions.UPDATE) { // set when someone thinks the groupBrowser needs a kick		
			clientFactory.getGroupBrowser().refreshMyStack();
			clientFactory.getRpcService().login(Document.get().getURL() + "#Home:1+MY+0+0+NONE", new AsyncCallback<LoginInfo>() {


				public void onFailure(Throwable error) {
					clientFactory.getPlaceController().goTo(new Home(clientFactory.getHomeID(),GroupType.MY,0L,0L,actions.NONE));
			      }

			      public void onSuccess(LoginInfo result) {
			    	clientFactory.setLoginInfo(result);
			    	clientFactory.getIdentityManager().showLoggedIn();
			    	completeContent = null; //redraw leaderboard
			    	goTo(new Home(clientFactory.getHomeID(),GroupType.MY,place.getGroupID(),0L,actions.NONE));	
			      }
			    });
			
		} else if (place.getAction() == actions.NEWLEAGUE) { // wants to create and join a new league
			setView(false,false,false,false,false,false,false);
			homeView.clear();
			showNewLeague();
		}  else if (place.getAction() == actions.JOIN) { // wants to join an existing league		
			// first make sure they are signed up and signed in. - note that groupID is the group they want to join
			if (clientFactory.getLoginInfo() == null) {
				goTo(new Home(clientFactory.getHomeID(),GroupType.MY,place.getGroupID(),0L,actions.CREATE));				
			} else {
				if (clientFactory.getLoginInfo().isLoggedIn()) {
					addToLeague();
					completeContent = null; // rebuild their home page to show leaderboard
				} else {
					goTo(new Home(clientFactory.getHomeID(),GroupType.MY,place.getGroupID(),0L,actions.CREATE));	
				}
			}
		} else if (place.getAction() == actions.LOGOUT) {
		    rpcService.logOff(null, new AsyncCallback<LoginInfo>() {
				public void onSuccess(LoginInfo result) {
			    	clientFactory.setLoginInfo(result);
			    	clientFactory.getIdentityManager().showLoggedIn();
					goTo(new Home(clientFactory.getHomeID(),GroupType.MY,place.getGroupID(),0L,actions.NONE));									
				}
			      
				public void onFailure(Throwable caught) {
			        Window.alert("Error Logging Out");

			    }
			});	
		} else if (place.getAction() == actions.NONE) {
			
			if (clientFactory.getLoginInfo().isLoggedIn())	{
				if (!CoreConfiguration.isLockedDown()) {
					if (!clientFactory.getLoginInfo().getRoundsComplete().get(5))  {
						//they need to do initial draft
						setContent(CoreConfiguration.getNoDraftContentId());
					} else if  (!clientFactory.getLoginInfo().getRoundsComplete().get(clientFactory.getCurrentround()))  {
						// need to pick for this round
						setContent(CoreConfiguration.getNoRoundContentId());		
					} else {
						// they are all set
						setContent(CoreConfiguration.getCompleteContentId());
											
					}
				} else {
					// no drafting or picking when we are locked down
					setContent(CoreConfiguration.getCompleteContentId());					
				}
			}	else {
					//they need to login or sign up
					setContent(CoreConfiguration.getNoLoginContentId());
			}
			
		} else { // unrecognized action
			assert false; // shouldn't make it here because of sanity check
		}
	    
	}


private void showPopupContent(String string) {
		ShowContent content = new ShowContent(string);
		content.center();
		
	}


private String setContent(Long id) {
		if (id.equals(CoreConfiguration.getNoLoginContentId())) {
			if (noLoginContent == null) {
			    rpcService.getContent(id, new AsyncCallback<String>() {
				      public void onSuccess(String result) {
				    	  noLoginContent = result;
				    	  homeView.setNoLoginContent(result);
				    	  homeView.showNoLogin(true);
				    	  setView(true, true, false, false, false, false,false);
				      }		      
				      public void onFailure(Throwable caught) {
				    	  noLoginContent = "System down for maintenance, check back soon! (001)";
				      }
				    });	
			} else {
		    	  homeView.setNoLoginContent(noLoginContent);
		    	  homeView.showNoLogin(true);
		    	  setView(true, true, false, false, false, false,false);
			}
			return noLoginContent;
		} else if (id.equals(CoreConfiguration.getNoDraftContentId())) {
			if (noDraftContent == null) {
			    rpcService.getContent(id, new AsyncCallback<String>() {
				      public void onSuccess(String result) {
				    	  noDraftContent = result;
				    	  homeView.setNoDraftContent(result);
				    	  homeView.showNoDraft(true);
				    	  setView(false, false, true, true, false, false,false);
				      }		      
				      public void onFailure(Throwable caught) {
				    	  noDraftContent = "System down for maintenance, check back soon! (002)";
				      }
				    });	
			} else {
		    	  homeView.setNoDraftContent(noDraftContent);
		    	  homeView.showNoDraft(true);
		    	  setView(false, false, true, true, false, false,false);
		    }
			return noDraftContent;
		} else if (id.equals(CoreConfiguration.getNoRoundContentId())) {
			if (noRoundContent == null) {
			    rpcService.getContent(id, new AsyncCallback<String>() {
				    
					public void onSuccess(String result) {
						noRoundContent = result;
				    	homeView.setNoRoundContent(noRoundContent);
				    	homeView.showNoRoundSelection(true);
				    	boolean lastPicked = clientFactory.getLoginInfo().getRoundsComplete().get(CoreConfiguration.getCurrentround()-1);
				    	setView(false, false, false, false, true, false, lastPicked);
				      }		      

					public void onFailure(Throwable caught) {
				    	  noRoundContent = "System down for maintenance, check back soon! (003)";
				      }
				    });	
			} else {
		    	  homeView.setNoRoundContent(noRoundContent);
		    	  homeView.showNoRoundSelection(true);
		    	  boolean lastPicked = clientFactory.getLoginInfo().getRoundsComplete().get(CoreConfiguration.getCurrentround()-1);
		    	  setView(false, false, false, false, true, false, lastPicked);
			}
			return noRoundContent;
	} else if (id.equals(CoreConfiguration.getCompleteContentId())) {
		if (completeContent == null) {
		    rpcService.getContent(id, new AsyncCallback<String>() {
			      public void onSuccess(String result) {
			    		completeContent = result;	
			    		rpcService.getMyClubhouse(null, new AsyncCallback<IClubhouse>() {  // league info
						      public void onSuccess(IClubhouse l) {
						    	  if (l != null) {
						    		isInLeague = true;
							    	final String leagueName = l.getName();
							    	final String joinLink = l.getJoinLink();
							    	
								    rpcService.getLeaderBoard(0L, new AsyncCallback<ArrayList<ClubhouseMembership>>() {  // leaderboard for league
									      public void onSuccess(ArrayList<ClubhouseMembership> lb) {
									    	  if (!lb.isEmpty()) {
									    		  leaderboard = "<table id='leaderBoard'><th>" + leagueName + " Leaderboard</th>";
									    		  String oddEven = "odd";
									    		  for (IClubhouseMembership lm : lb) {
//									    			  String nameLink = "";
//									    			  if (lm.getCurrentGroupID() != 0L) {
//									    				  nameLink = "<a href='http://fantasy.rugby.net/#Browse:"+place.getRootID()+"+MY+" + lm.getCurrentGroupID() +"+0'>"+ lm.getUserName() + "</a>";
//									    			  } else {
//									    				  nameLink = lm.getUserName();
//									    			  }
//									    			  leaderboard += "<tr class ='"+ oddEven+"'><td>" + nameLink + "</td><td>" + lm.getCurrentRoundScore() + "</td></tr>";
//									    			  if (oddEven.equals("odd")) {
//									    				  oddEven = "even";
//									    			  } else {
//									    				  oddEven = "odd";
//									    			  }
									    		  }
	
										    	  completeContent = "<table id='completeContent'><tr><td>" + completeContent + "</td><td>" + leaderboard + "</td></tr><tr><td id='joinLinkInstructions'>Email or share on Facebook the link below to invite others:</td></tr><tr><td><form><input id='joinLink' type='text' size='30' readonly value='" + joinLink + "'></form></td></tr></table>";
	
										    	  setView(false, false, false, false, false, false, false);	
									    	  } else {
									    		  // show the create league link
										    	  setView(false, false, false, false, false, true, false);	
										      }
									    	  homeView.setCompleteContent(completeContent);
									    	  homeView.showComplete(true);
	
									      }
										    	  
									      public void onFailure(Throwable caught) {
									    	  completeContent = "System down for maintenance, check back soon! (007)";
									      }
							    	});
						      } else {
						    	  // they aren't in a league
					    		  // show the create league link
						    	  setView(false, false, false, false, false, true, false);	
						    	  homeView.setCompleteContent(completeContent);
						    	  homeView.showComplete(true);
						    	  
						      }

						    }
						    public void onFailure(Throwable caught) {
						    	  completeContent = "System down for maintenance, check back soon! (008)";
						    }
					});	
			      }		      
			      public void onFailure(Throwable caught) {
			    	  completeContent = "System down for maintenance, check back soon! (004)";
			      }
		    });	
		} else {
	    	  homeView.setCompleteContent(completeContent);
	    	  homeView.showComplete(true);
	    	  setView(false, false, false, false, false, !isInLeague, false);			
		}
		return noLoginContent;
	}
		return "System down for maintenance, check back soon! (005)";
}

private void setView(boolean signIn, boolean signUp, boolean draft, boolean random, boolean round, boolean league, boolean reuse) {

	  homeView.showSignInButton(signIn);
	  homeView.showSignUpButton(signUp);
	  homeView.showDraftButton(draft);
	  homeView.showRandomButton(random);
	  homeView.showRoundButton(round);
	  homeView.showCreateLeague(league);
	  homeView.showReuse(reuse);
	
}

private void showCreate() {

		CreateAccount<LoginInfo> createPanel = clientFactory.getCreateAccount();
		createPanel.setClientFactory(clientFactory);
		createPanel.init(this);
	}


private void showLogin() {
	NativeLogin<LoginInfo> nativeLogin = clientFactory.getNativeLogin();
	nativeLogin.setClientFactory(clientFactory);
	nativeLogin.setPresenter(this);
	nativeLogin.init();
			
	}

private void showNewLeague() {

	CreateLeague<Clubhouse> createLeague = new CreateLeague<Clubhouse>();
	createLeague.setClientFactory(clientFactory);
	createLeague.init(this);
}

	@Override
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}

	@Override
	public void onDraftButtonClicked() {
		goTo(new Manage(clientFactory.getHomeID(), GroupType.MY, CoreConfiguration.getCurrentstage(), 5, step.START, true));
		
	}

	@Override
	public void onRandomButtonClicked() {
		goTo(new Manage(clientFactory.getHomeID(), GroupType.MY, CoreConfiguration.getCurrentstage(), 5, step.RANDOM, true));
		
	}

	@Override
	public void onUniversalSportsClicked() {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void onRoundButtonClicked() {
		goTo(new Manage(clientFactory.getHomeID(), GroupType.MY, CoreConfiguration.getCurrentstage(), CoreConfiguration.getCurrentround(), step.ROUND, true));
		
	}


	@Override
	public void onSignUpButtonClicked() {
		goTo(new Home(clientFactory.getHomeID(), GroupType.MY, 0L, 0L, actions.CREATE));
		
	}


	@Override
	public void onSignInButtonClicked() {
		goTo(new Home(clientFactory.getHomeID(), GroupType.MY, 0L, 0L, actions.LOGIN));
		
	}


	@Override
	public void onLoginComplete() {
		if (place.getGroupID() != 0L)
			addToLeague();
		clientFactory.getIdentityManager().showLoggedIn();
		goTo(new Home(clientFactory.getHomeID(), GroupType.MY, 0L, 0L, actions.UPDATE));
		
	}

	private void addToLeague() {
	    rpcService.joinLeague(place.getGroupID(), new AsyncCallback<String>() {
		      public void onSuccess(String result) {
		  		goTo(new Home(clientFactory.getHomeID(), GroupType.MY, 0L, 0L, actions.NONE));

		      }		      
		      public void onFailure(Throwable caught) {
		    	  completeContent = "System down for maintenance, check back soon! (004)";
		      }
		    });	
		
	}


	@Override
	public void onCreateLeagueClicked() {
		goTo(new Home(clientFactory.getHomeID(), GroupType.MY, 0L, 0L, actions.NEWLEAGUE));		
	}


	@Override
	public void onCreateLeagueFinished() {
		completeContent = null;  // redraw home page
		goTo(new Home(clientFactory.getHomeID(), GroupType.MY, 0L, 0L, actions.NONE));		
		
	}


	@Override
	public void onReuseButtonClicked() {
		// copy the last round's pick to the current round.
	    rpcService.copyLastRoundsPick( new AsyncCallback<Boolean>() {
		      public void onSuccess(Boolean result) {
		    	  if (result) {
		    		  goTo(new Home(clientFactory.getHomeID(), GroupType.MY, 0L, 0L, actions.UPDATE));
		    	  } else {
		    		  noRoundContent = "Problems reusing group. Please pick for this round again.";
		    	  }

		      }		      
		      public void onFailure(Throwable caught) {
		    	  completeContent = "Problems reusing group. Please pick for this round again. (010)";
		      }
		    });	
	}

}