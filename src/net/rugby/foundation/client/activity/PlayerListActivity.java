package net.rugby.foundation.client.activity;

import java.util.ArrayList;
import java.util.List;

import net.rugby.foundation.client.ClientFactory;
import net.rugby.foundation.client.ClientFactory.GetCompetitionCallback;
import net.rugby.foundation.client.PlayersServiceAsync;
import net.rugby.foundation.client.place.Details;
import net.rugby.foundation.client.place.Browse;
import net.rugby.foundation.client.place.Home;
import net.rugby.foundation.client.place.Manage;
import net.rugby.foundation.client.place.Manage.step;
import net.rugby.foundation.client.ui.GroupSplash;
import net.rugby.foundation.client.ui.PlayerListView;
import net.rugby.foundation.client.ui.PlayerPopupView;
import net.rugby.foundation.client.ui.ShowContent;
import net.rugby.foundation.model.shared.CoreConfiguration;
import net.rugby.foundation.model.shared.Feature;
import net.rugby.foundation.model.shared.Group;
import net.rugby.foundation.model.shared.Group.GroupType;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.PlayerPopupData;
import net.rugby.foundation.model.shared.PlayerRowData;
import net.rugby.foundation.model.shared.Position.position;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;

public class PlayerListActivity extends AbstractActivity implements
		PlayerListView.Presenter<PlayerRowData>, PlayerPopupView.Presenter<PlayerPopupData>{
	// Used to obtain views, eventBus, placeController
	// Alternatively, could be injected via GIN
	private ClientFactory clientFactory;
	// Name that will be appended to "PlayerList,"
	private Long playerID;
	private Long groupID;
	private GroupType groupTypeID;
	
	private List<PlayerRowData> players  = new ArrayList<PlayerRowData>();
	private final PlayersServiceAsync rpcService;
	private SelectionModel<PlayerRowData> selectionModel = new SelectionModel<PlayerRowData>();
	
	private Place place;
	private position currPosition;
	private PlayerListView<PlayerRowData> playerListView;
	
	protected PlayerListActivity This = this;
	private AcceptsOneWidget contWidget;
	
	public PlayerListActivity(Browse place, ClientFactory clientFactory) {
		this.playerID = place.getPlayerID();
		this.groupID = place.getGroupID();
		this.groupTypeID = place.getGroupTypeID();
		this.clientFactory = clientFactory;
		this.rpcService = clientFactory.getRpcService();
		this.place = place;
	}

	public PlayerListActivity(Manage place, ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		this.rpcService = clientFactory.getRpcService();
		this.place = place;
	}

	public PlayerListActivity(Home place, ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		this.rpcService = clientFactory.getRpcService();
		this.place = place;	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		
		contWidget = containerWidget;
		
		clientFactory.getCompetitionAsync(CoreConfiguration.getDefaultCompetitionShortName(), new GetCompetitionCallback() {

			@Override
			public void onCompetitionFetched(ICompetition comp) {
				if (place instanceof Browse) {
					if (!clientFactory.getLoginInfo().isAdmin()) {
						if (((Browse) place).getRootID() == comp.getId()) {
							playerListView = clientFactory.getPlayerListViewNoSelect();  // no checkbox column if you are browsing
						} else { // looking at pool stuff
							playerListView = clientFactory.getPlayerListViewPool();
						}
					} else {
						playerListView = clientFactory.getPlayerListView(); //admins can edit
						
					}
					
				} else
					playerListView = clientFactory.getPlayerListView();

		  	  	PlayerPopupView<PlayerPopupData> popupView = clientFactory.getPlayerPopupView();
		  	  	popupView.setPresenter(This);
				playerListView.showDraftAnalysisLink(false);  // only show for positions during draft
		 	  	

				playerListView.setPresenter(This);
				if (place instanceof Browse && groupID == 0L && groupTypeID != GroupType.MY) {
					// show our splash pages for team, position and match
					GroupSplash splash = new GroupSplash();
					splash.show(groupTypeID);
					contWidget.setWidget(splash.asWidget());
					return;
				} else	
					contWidget.setWidget(playerListView.asWidget());
				
				if (clientFactory.getLoginInfo().isAdmin())
					playerListView.setButtonCaptions( "",  "Edit",  "",  "");

				if (place instanceof Browse) {
					//do the async call last!
					if (groupID > 0L) {
						fetchPlayerRowData();
						fetchGroupInfo("");
					} 
				} else if (place instanceof Manage) {
					playerListView.setGroupInfo("");
					if (((Manage)place).getStep() != step.START && ((Manage)place).getStep() != step.DONE 
							&& ((Manage)place).getStep() != step.RANDOM && ((Manage)place).getStep() != step.ROUND) {
						currPosition = position.NONE;
						currPosition = position.getAt(((Manage)place).getStep().ordinal());
						if (currPosition != position.NONE) {
							groupID = getGroupIdFromStep();
							groupTypeID = GroupType.POSITION;
							fetchPlayerRowData();
							playerListView.showDraftAnalysisLink(true);  // only show for positions
							((Widget)playerListView).setVisible(true);
						}
					} else if (((Manage)place).getStep() == step.ROUND) {
						// for a round choice just let them pick from their roster.
						//if (((Manage)Place).getRootID() == )
						//TODO needs to be cleaned up - grep 5
						groupID = clientFactory.getLoginInfo().getDraftID(); // @REX - hopefully the roster is the first in the list.
						fetchPlayerRowData();
						((Widget)playerListView).setVisible(true);
					} else  {  // hide for start and done.
						((Widget)playerListView).setVisible(false);
						return;
					}
				} else if (place instanceof Home) {
					// only home pages 1 & 4 (not 2 & 3)
					if (clientFactory.getLoginInfo().isLoggedIn() && clientFactory.getLoginInfo().getRoundsComplete().get(clientFactory.getCurrentround()))	{
						setFeaturedGroup();		
					} else {
						((Widget)playerListView).setVisible(false);
					}
				}					
			}
			
		});

	}

	private void setFeaturedGroup() {
		groupID = null;
	    rpcService.getCurrentlyFeaturedGroup( new AsyncCallback<Feature>() {
		      public void onSuccess(Feature result) {
		    	  groupID = null;
		    	  if (result != null) {
			    	  groupID = result.getGroupID();
						if (groupID != null) {
							fetchPlayerRowData();
							fetchGroupInfo(result.getTitle());
						}
		    	  }
		      }
		      
		      public void onFailure(Throwable caught) {
		    	  groupID = null;
		      }
		    });	
	}

	private Long getGroupIdFromStep() {
		//TODO - just horrendous
		Long gid = 0L;
		if (((Manage)place).getStep() == step.CENTER)  {
			gid = 29L;
		} else if (currPosition == position.WING) {
			gid = 28L;
		} else {
			gid = 20L + currPosition.ordinal();
		}		
		
		return gid;
	}

	private void fetchGroupInfo(final String preamble) {
	    rpcService.getGroupInfo(groupID, new AsyncCallback<String>() {
		      public void onSuccess(String result) {
		    	  playerListView.setGroupInfo(preamble + result);
		    	  //+"<a href=\"http://www.pntrac.com/t/R0BIS0hMRUBKR0ZIQ0BJSElGSA\"><img src=\"http://www.pntrac.com/b/R0BIS0hMRUBKR0ZIQ0BJSElGSA\" border=\"0\" width=\"300\" height=\"250\" title=\"Rugby World Cup\" alt=\"Rugby World Cup\"></a>");
		    			  //"<a href=\"http://www.pntrac.com/t/R0BIS0hMREBKR0ZIQ0BJSElGSA\"><img src=\"http://www.pntrac.com/b/R0BIS0hMREBKR0ZIQ0BJSElGSA\" border=\"0\" width=\"728\" height=\"90\" title=\"Rugby World Cup\" alt=\"Rugby World Cup\"></a>");
		    	  playerListView.asWidget().setVisible(true);
		    	  if (clientFactory.getLoginInfo().isAdmin()) {
		    		  playerListView.showEditGroupInfoLink(true);
		    	  } else {
		    		  playerListView.showEditGroupInfoLink(false);
		    	  }
		      }
		      
		      public void onFailure(Throwable caught) {
		        Window.alert("Error fetching Group Name");
		      }
		    });	
	}

	private void fetchPlayerRowData() {
		if (groupTypeID == GroupType.POSITION) {
			
			//@REX when you see stuff like this you know something is a bit off
			Long rootID = 0L;
			boolean showInactive = true;
			if (place instanceof Browse) {
				rootID = ((Browse)place).getRootID();
				if (((Browse) place).getGroupTypeID() == GroupType.POSITION || ((Browse) place).getGroupTypeID() == GroupType.MATCH) 
					showInactive = false; // don't show inactive players (Frans Steyn)
			} else if (place instanceof Manage) {
				rootID = ((Manage)place).getRootID();
				showInactive = false; // don't let people pick inactive players
			} else if (place instanceof Home) {
				rootID = ((Home)place).getRootID();
			} else {
				rootID = clientFactory.getHomeID();
			}
			
		    rpcService.getPlayerRowDataByPositionAndComp(groupID, rootID, showInactive, rootID != clientFactory.getHomeID(), new AsyncCallback<ArrayList<PlayerRowData>>() {
			      public void onSuccess(ArrayList<PlayerRowData> result) {
			    	  players = result;
			    	  playerListView.setPlayers(players);
			      }
			      
			      public void onFailure(Throwable caught) {
			        Window.alert("Error fetching Player details");
			      }
			    });			
		} else {
			// @TODO refactor this somewhat contrived code that allows us to show the match ratings of pool games
			boolean poolMatch = false;
			if (place instanceof Browse) {
				if (((Browse)place).getRootID() != clientFactory.getHomeID()) {
						poolMatch = true;
				}
			}
		    rpcService.getPlayerRowDataByGroup(groupID, poolMatch, new AsyncCallback<ArrayList<PlayerRowData>>() {
		      public void onSuccess(ArrayList<PlayerRowData> result) {
		    	  players = result;
		    	  playerListView.setPlayers(players);
		      }
		      
		      public void onFailure(Throwable caught) {
		        Window.alert("Error fetching Player details");
		      }
		    });
		}
	  }

	private void fetchPlayerPopupData() {
	    rpcService.getPlayerPopupData(playerID, new AsyncCallback<PlayerPopupData>() {
	      public void onSuccess(PlayerPopupData result) {

	    	  clientFactory.getPlayerPopupView().setPlayer(result);
	      }
	      
	      public void onFailure(Throwable caught) {
	        Window.alert("Error fetching Player details");
	      }
	    });
	  }

	/**
	 * Ask user before stopping this activity
	 */
//	@Override
//	public String mayStop() {
//		return "Please hold on. This activity is stopping.";
//	}

	/**
	 * Navigate to a new Place in the browser
	 */
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}

	@Override
	public void onButton1Clicked() {
		ArrayList<Long> player = new ArrayList<Long>();
		Details place = new Details(clientFactory.getHomeID(), groupTypeID, groupID, player);
		goTo(place);
	}

	@Override
	public void onButton2Clicked() {
		//deleteSelectedPlayers();
		Details place = new Details();
		
		place.setRootID(clientFactory.getHomeID());
		place.setGroupTypeID(groupTypeID);
		place.setGroupID(groupID);
		
		for (PlayerRowData p : selectionModel.selectedItems)
		{
			place.getEditPlayerIDs().add(p.getId());
		}
		
		goTo(place);

	}
	
	@Override
	public void onButton3Clicked() {

		
	}

	@Override
	public void onButton4Clicked() {
		// nothing to do in browse
		
	}

    @Override
    public void onItemClicked(PlayerRowData PlayerRowData)
    {
		//clientFactory.getEventBus().fireEvent(new EditPlayerEvent(PlayerRowData.getId()));    
    	
    	//show popup
    	playerID = PlayerRowData.getId();
    	fetchPlayerPopupData();
    	
		//goTo(new Browse(clientFactory.getHomeID() + "+" + groupTypeID.name() + "+" +groupID + "+" + playerID));
    }
    
	@Override
	public boolean onItemSelected(PlayerRowData PlayerRowData) {
	      if (selectionModel.isSelected(PlayerRowData)) {
	        selectionModel.removeSelection(PlayerRowData);
	      }

	      else {
	        selectionModel.addSelection(PlayerRowData);
	      }
	      
	      return true;
	    }

	  public class SelectionModel<T> {
		    List<T> selectedItems = new ArrayList<T>();

		    public List<T> getSelectedItems() {
		      return selectedItems;
		    }

		    public void addSelection(T item) {
		      selectedItems.add(item);
		    }

		    public void removeSelection(T item) {
		      selectedItems.remove(item);
		    }

		    public boolean isSelected(T item) {
		      return selectedItems.contains(item);
		    }
		  }

	@Override
	public void onCloseButtonClicked() {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void onStop() {
		((Widget)playerListView).setVisible(false);
		
	}

	@Override
	public void onSaveGroupInfoClicked(String info) {
	    rpcService.updateGroupInfo(groupID, info, new AsyncCallback<Group>() {
		      public void onSuccess(Group result) {
		    	  Window.alert("Group info saved.");
		    	  
		      }
		      
		      public void onFailure(Throwable caught) {
		        Window.alert("Error saving group info.");
		      }
		    });

		
	}

	@Override
	public void showDraftAnalysis() {

	    rpcService.getGroupInfo(groupID, new AsyncCallback<String>() {
		      public void onSuccess(String result) {
		  		ShowContent contentD = new ShowContent(result);
				contentD.center();
		      }
		      
		      public void onFailure(Throwable caught) {
		        Window.alert("Error fetching draft analysis");
		      }
		    });	

		
	}


}
