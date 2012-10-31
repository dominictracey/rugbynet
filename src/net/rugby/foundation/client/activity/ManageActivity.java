package net.rugby.foundation.client.activity;

import java.util.ArrayList;
import java.util.Iterator;

import net.rugby.foundation.client.ClientFactory;
import net.rugby.foundation.client.PlayersServiceAsync;
import net.rugby.foundation.client.ClientFactory.GetManagementEngineAsyncCallback;
import net.rugby.foundation.client.place.Home;
import net.rugby.foundation.client.place.Manage;
import net.rugby.foundation.client.place.Home.actions;
import net.rugby.foundation.client.place.Manage.step;
import net.rugby.foundation.client.ui.ManageView;
import net.rugby.foundation.client.ui.PlayerListView;
import net.rugby.foundation.model.shared.CoreConfiguration.selectionType;
import net.rugby.foundation.model.shared.CoreConfiguration;
import net.rugby.foundation.model.shared.DraftWizardState;
import net.rugby.foundation.model.shared.ManagementEngine;
import net.rugby.foundation.model.shared.MyGroup;
import net.rugby.foundation.model.shared.PlayerRowData;
import net.rugby.foundation.model.shared.PoolDraftManagementEngine;
import net.rugby.foundation.model.shared.Group.GroupType;
import net.rugby.foundation.model.shared.Stage.stageType;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.RootPanel;

public class ManageActivity extends AbstractActivity implements
	ManageView.Presenter<DraftWizardState>, PlayerListView.Listener<PlayerRowData>{
	
	private ClientFactory clientFactory;
	private final PlayersServiceAsync rpcService;
	private Manage place;

	private ManageView<DraftWizardState> manageView;
	private PoolDraftManagementEngine engine;
	
	public ManageActivity(Manage place, ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		this.rpcService = clientFactory.getRpcService();
		this.place = place;
	}

	
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		manageView = clientFactory.getManageView();
		if (manageView instanceof ManageView<?>)
			manageView = clientFactory.getManageView();

		manageView.setPresenter(this);
		clientFactory.getPlayerListView().setListener(this);
		containerWidget.setWidget(manageView);
		
		// sanity check - only POOL round is open
		if (place.getStage() != stageType.KNOCKOUT) {
			Window.alert("Only the knockout stage competition is currently available");
			goTo( new Home(clientFactory.getHomeID(),GroupType.NONE,0L,0L,actions.NONE));
			return;
		}
		
		// sanity check - only draft or current round is available for managing
		if (!(place.getRound() == 5 || place.getRound() == clientFactory.getCurrentround())) {
			Window.alert("Only the draft or round " + clientFactory.getCurrentround() + " is currently available for editing.");
			goTo( new Home(clientFactory.getHomeID(),GroupType.NONE,0L,0L,actions.NONE));
			return;
		}
				
		// sanity check - you have to be logged on
		if (clientFactory.getLoginInfo() == null) {
			Window.alert("You need to sign in or create an account before you can pick a team.");
			goTo( new Home(clientFactory.getHomeID(),GroupType.NONE,0L,0L,actions.NONE));
			return;			
		}
		
		// sanity check - you can only draft once
		if (clientFactory.getLoginInfo().getRoundsComplete().get(5) == true && place.getRound() == 5) {
			Window.alert("You have already completed the draft.");
			goTo( new Home(clientFactory.getHomeID(),GroupType.MY,0L,0L,actions.NONE));
			return;			
		}		
		
		//sanity check - you can't pick for a round without a draft
		if (!clientFactory.getLoginInfo().getRoundsComplete().get(5) && place.getRound() > 5) {
			Window.alert("You have to complete your full roster before you pick a side.");
			goTo( new Manage(clientFactory.getHomeID(),GroupType.MY,CoreConfiguration.getCurrentstage(),5,step.START,true));
			return;			
		}	
		
		if (place.getStep() == step.START) {
			clientFactory.trashDraftEngines();
		}
		
		if (place.getStep() != step.RANDOM) {
			if (place.getStep() != step.ROUND) {
				
				clientFactory.getManagementEngineAsync(selectionType.KNOCKOUTROSTER, place.getRootID(), new GetManagementEngineAsyncCallback() {

					@Override
					public void onEngineStarted(ManagementEngine<PlayerRowData, DraftWizardState> eng) {
						engine = (PoolDraftManagementEngine) eng;
						if (engine != null && engine.getState() != null) {
							engine.getState().setInstructions(place.getStep().getInstructions());
							manageView.showPoints(true);
							manageView.setData(engine.getState());
						} else {
							//something wacky is happening - probably need to delete the engine.
							goTo(new Home(clientFactory.getHomeID(),GroupType.MY,0L,0L,actions.UPDATE));
						}						
					}
					
				});

			} else {
				
				clientFactory.getManagementEngineAsync(selectionType.KNOCKOUTROUND, place.getRootID(), new GetManagementEngineAsyncCallback() {

					@Override
					public void onEngineStarted(ManagementEngine<PlayerRowData, DraftWizardState> eng) {
						engine = (PoolDraftManagementEngine) eng;
						if (engine != null && engine.getState() != null) {
							engine.getState().setInstructions(place.getStep().getInstructions());
							manageView.showPoints(false);
							manageView.setData(engine.getState());
						} else {
							//something wacky is happening - probably need to delete the engine.
							goTo(new Home(clientFactory.getHomeID(),GroupType.MY,0L,0L,actions.UPDATE));
						}						
					}
					
				});
				
			}

		} else {  // do a random pickle
			if (place.getStep() != step.ROUND) {
				doRandom();
			} else {
				Window.alert("You can't randomly select for a Round.");
				goTo( new Manage(clientFactory.getHomeID(),GroupType.MY,CoreConfiguration.getCurrentstage(),clientFactory.getCurrentround(),step.START,true));
			}
		}
	}


	@Override
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}

	private void goToStep(Manage.step s)
	{
		Manage newPlace = new Manage(place.getRootID(),place.getGroupType(),place.getStage(),place.getRound(),s,true);
		goTo(newPlace);
	}

	@Override
	public void onPropClicked() {
		goToStep(step.PROP);
	}

	@Override
	public void onHookerClicked() {
		goToStep(step.HOOKER);
	}

	@Override
	public void onLockClicked() {
		goToStep(step.LOCK);
	}

	@Override
	public void onFlankerClicked() {
		goToStep(step.FLANKER);
	}

	@Override
	public void onNumber8Clicked() {
		goToStep(step.NUMBER8);
	}

	@Override
	public void onScrumhalfClicked() {
		goToStep(step.SCRUMHALF);
	}

	@Override
	public void onFlyhalfClicked() {
		goToStep(step.FLYHALF);
	}

	@Override
	public void onCenterClicked() {
		goToStep(step.CENTER);
	}

	@Override
	public void onWingClicked() {
		goToStep(step.WING);
	}

	@Override
	public void onFullbackClicked() {
		goToStep(step.FULLBACK);
	}

	@Override
	public void onPreviousButtonClicked() {
		goToStep(place.getStep().getPrev());
	}

	@Override
	public void onNextButtonClicked() {
		goToStep(place.getStep().getNext());		
	}


	@Override
	public boolean onItemSelected(PlayerRowData player) {
		if (engine.isSelected(player)) {
			engine.remove(player);
			manageView.setData(engine.getState());
			return false;
		} else {
			// not in there yet
			if (!engine.canAdd(player))  {
				Window.alert(engine.getError());
				return false;
			}
			else {
				if (engine.add(player)) {
					manageView.setData(engine.getState());
				}
			}
		}
		
		return true;
		
	}

	@Override
	public boolean isSelected(PlayerRowData player) {
		if (engine != null)
			return engine.isSelected(player);
		else
			return false;
	}


	@Override
	public void onDoneButtonClicked() {
		MyGroup group = new MyGroup();
		ArrayList<Long> pkeys = new ArrayList<Long>();
		
		// pull the selected players' ids out of the engine
		Iterator<PlayerRowData> it = engine.getSelected().iterator();
		while (it.hasNext())
		{
			pkeys.add(it.next().getId());
		}
		RootPanel.get().addStyleName("globalWaitCursor"); 
		group.setPlayerIds(pkeys);
		group.setRound(place.getRound());
		
		selectionType type;
		if (place.getStage() == stageType.POOL) {
			if (place.getRound() == 0) {
				type = selectionType.POOLROSTER;
				group.setDisplayName("Pool Roster");
			} else {
				type = selectionType.POOLROUND;				
				group.setDisplayName("Round " + place.getRound());
			}
		} else {// KNOCKOUT 
			assert place.getStage() == stageType.KNOCKOUT;
			if (place.getRound() == 5) {
				type = selectionType.KNOCKOUTROSTER;
				group.setDisplayName("Knockout Roster");
			} else {
				type = selectionType.KNOCKOUTROUND;		
				if (place.getRound() == 6)
					group.setDisplayName("Quarterfinals");
				else if (place.getRound() == 7)
					group.setDisplayName("Semifinals");
				else if (place.getRound() == 8)
					group.setDisplayName("Finals");
			}
		}
		
	    clientFactory.getPlayerListView().setListener(null);
			
	    rpcService.submitDraftTeam(group, type, clientFactory.getHomeID(), new AsyncCallback<Long>() {
		      public void onSuccess(Long result) {
		    	  RootPanel.get().removeStyleName("globalWaitCursor"); 
		    	  goTo(new Home(clientFactory.getHomeID(),GroupType.MY,result,0L,actions.UPDATE));
		      }		      
		      public void onFailure(Throwable caught) {
		    	  RootPanel.get().removeStyleName("globalWaitCursor"); 
		    	  Window.alert("Well, this is emabarrassing... there was a problem saving your team. Please let us know what you were doing so we can fix the issue. We're very sorry!");
		    	  goTo(new Home(clientFactory.getHomeID(),GroupType.MY,0L,0L,actions.NONE));
		      }
		    });	
	}
	
	private void doRandom() {
		manageView.showWait();
	    clientFactory.getPlayerListView().setListener(null);

	    rpcService.doRandomDraft(place.getStage(), clientFactory.getHomeID(), new AsyncCallback<Long>() {
		      public void onSuccess(Long result) {
		    	  RootPanel.get().removeStyleName("globalWaitCursor"); 
		    	  goTo(new Home(clientFactory.getHomeID(),GroupType.MY,result,0L,actions.UPDATE));
		      }		      
		      public void onFailure(Throwable caught) {
		    	  RootPanel.get().removeStyleName("globalWaitCursor"); 
		    	  Window.alert("There was a problem creating your team. Please try again!");
		    	  goTo(new Home(clientFactory.getHomeID(),GroupType.MY,0L,0L,actions.NONE));
		      }
		    });			
	}
	

//	private void testSubmit() {
//
//		
//		  
//		MyGroup group = new MyGroup();
//		ArrayList<Long> pkeys = new ArrayList<Long>();
//		//	[, , , , , , , , , , , , , , , , , , , , , , , , , , , , , ] 
//		pkeys.add(331L);pkeys.add(334L);pkeys.add(123L);pkeys.add(302L);pkeys.add(337L);pkeys.add(397L);pkeys.add(341L);pkeys.add(39L);
//		pkeys.add(308L);pkeys.add(399L);pkeys.add(343L);pkeys.add(494L);pkeys.add(315L);pkeys.add(405L);pkeys.add(496L);pkeys.add(76L);
//		pkeys.add(497L);pkeys.add(439L);pkeys.add(51L);pkeys.add(319L);pkeys.add(53L);pkeys.add(505L);pkeys.add(323L);pkeys.add(85L);
//		pkeys.add(57L);	pkeys.add(507L);pkeys.add(567L);pkeys.add(86L);pkeys.add(176L);pkeys.add(420L);
//
//
//
//		group.setPlayerIds(pkeys);
//		group.setStageT(stageType.POOL);
//		group.setRound(0);
//		group.setDisplayName("Random Roster");
//		RootPanel.get().addStyleName("globalWaitCursor"); 
//		
//		selectionType type;
//		if (place.getStage() == stageType.POOL) {
//			if (place.getRound() == 0) {
//				type = selectionType.POOLROSTER;
//			} else {
//				type = selectionType.POOLROUND;				
//			}
//		} else {// KNOCKOUT 
//			assert place.getStage() == stageType.KNOCKOUT;
//			if (place.getRound() == 0) {
//				type = selectionType.KNOCKOUTROSTER;
//			} else {
//				type = selectionType.KNOCKOUTROUND;				
//			}
//		}
//	    rpcService.submitDraftTeam(group, type, new AsyncCallback<Boolean>() {
//	    	
//		      public void onSuccess(Boolean result) {
//		    	  if (result) {
//		    		  RootPanel.get().removeStyleName("globalWaitCursor"); 
//		    		  Window.alert("ok");
//		    		} else {
//		    			RootPanel.get().removeStyleName("globalWaitCursor"); 
//		    		
//		    			 Window.alert("random combination not found - try again.");
//		    		}	 
//		    	  goTo(new Home(clientFactory.getHomeID(),GroupType.MY,0L,0L,actions.UPDATE));
//		      }		      
//		      public void onFailure(Throwable caught) {
//		        Window.alert("Well, this is emabarrassing... there was a problem saving your team. Please let us know what you were doing so we can fix the issue. We're very sorry!");
//    			RootPanel.get().removeStyleName("globalWaitCursor"); 
//
//		        goTo(new Home(clientFactory.getHomeID(),GroupType.MY,0L,0L,actions.NONE));
//		      }
//		    });		
//
//	}


	@Override
	public step getStep() {
		return place.getStep();
	}


	@Override
	public stageType getStageType() {
		return place.getStage();
	}


	@Override
	public int getRound() {
		return place.getRound();
	}


}