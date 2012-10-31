package net.rugby.foundation.client.ui;

import net.rugby.foundation.client.ClientFactory;
import net.rugby.foundation.client.ClientFactory.AppUserNicknameCallback;
import net.rugby.foundation.client.ClientFactory.GroupCallback;
import net.rugby.foundation.client.place.Browse;
import net.rugby.foundation.client.place.Home;
import net.rugby.foundation.client.place.Home.actions;
import net.rugby.foundation.client.place.Manage;
import net.rugby.foundation.client.place.Manage.step;
import net.rugby.foundation.model.shared.Group;
import net.rugby.foundation.model.shared.Group.GroupType;
import net.rugby.foundation.model.shared.MyGroup;
import net.rugby.foundation.model.shared.Player;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;

public class BreadcrumbsTrail extends Composite  {

	@UiField Anchor home;
	@UiField Label alligator1;
	@UiField Anchor groupType;
	@UiField Label alligator2;
	@UiField Anchor group;
	@UiField Label alligator3;
	@UiField Anchor player;

	private Long playerID;
	private Long groupID;
	private GroupType groupTypeID;
	
	private static BreadcrumbsTrailUiBinder uiBinder = GWT
			.create(BreadcrumbsTrailUiBinder.class);

	interface BreadcrumbsTrailUiBinder extends
			UiBinder<Widget, BreadcrumbsTrail> {
	}
	
	private ClientFactory clientFactory;

	public BreadcrumbsTrail() {

	}

	public void Init(ClientFactory cf)
	{
		clientFactory = cf;
		initWidget(uiBinder.createAndBindUi(this));
		registerForPlaceChanges();	
		groupType.setVisible(false);
		alligator1.setVisible(false);
		group.setVisible(false);
		alligator2.setVisible(false);
		player.setVisible(false);
		alligator3.setVisible(false);
	}

	@UiHandler("home")
	void onHomeClick(ClickEvent event) {
		clientFactory.getPlaceController().goTo(new Home(clientFactory.getHomeID(), GroupType.MY , 0L , 0L, actions.NONE));
		
	}
	
	@UiHandler("groupType")
	void onGroupTypeClick(ClickEvent event) {
			clientFactory.getPlaceController().goTo(new Browse(clientFactory.getHomeID(), groupTypeID, 0L,0L));		
	}
	
	@UiHandler("group")
	void onGroupClick(ClickEvent event) {
			clientFactory.getPlaceController().goTo(new Browse(clientFactory.getHomeID(),groupTypeID,groupID,0L));
	}
	
	@UiHandler("player")
	void onPlayerClick(ClickEvent event) {
		clientFactory.getPlaceController().goTo(new Browse(clientFactory.getHomeID(),groupTypeID,groupID,playerID));		
	}
	
    private void registerForPlaceChanges() {
		clientFactory.getEventBus().addHandler(PlaceChangeEvent.TYPE,	new PlaceChangeEvent.Handler() {
			
			@Override
			public void onPlaceChange(PlaceChangeEvent event) {		
				
				if (event.getNewPlace() instanceof Browse) {
					groupTypeID = ((Browse)event.getNewPlace()).getGroupTypeID(); 
					if (groupTypeID != GroupType.NONE) {
						groupType.setVisible(true);
						alligator1.setVisible(true);
					}
					
					groupID = ((Browse)event.getNewPlace()).getGroupID(); 
					if (groupID != 0L) {
						group.setVisible(true);
						alligator2.setVisible(true);
					}
					
					playerID = ((Browse)event.getNewPlace()).getPlayerID();
					if (playerID != 0L)  {
						player.setVisible(true);
						alligator3.setVisible(true);
					}
					
					if (groupTypeID == GroupType.TEAM) {
						groupType.setText("Teams");
					}
					else if (groupTypeID == GroupType.POSITION) {
						groupType.setText("Positions");
					}
					else if (groupTypeID == GroupType.MATCH) {
						groupType.setText("Matches");
					} else if (groupTypeID == GroupType.MY) {
						groupType.setText("My");
					}	else  
					{
						groupType.setText("");
					}			

					if (groupTypeID != GroupType.NONE) {
				    	  groupType.setVisible(true);
				    	  alligator1.setVisible(true);
					} else {
				    	  groupType.setVisible(false);
				    	  alligator1.setVisible(false);
					}
					
					if (groupID > 0L) {
							Group g = clientFactory.getGroupBrowser().getGroupsByID().get(groupID);
							if (g == null) {
								// we need to get the group info because this isn't in the stack panel
								// get it from the server. Currently this is when the user is browsing the leaderboard
								clientFactory.getGroupAsync(groupID, new GroupCallback() {								
									@Override
									public void onGroupFetched(Group g) {
										group.setVisible(true);
										alligator2.setVisible(true);
										if (g != null) {
											group.setText(g.getDisplayName());
											// this is another user's round pick so we want to put their name in slot 2.
											clientFactory.getAppUserNameAsync(((MyGroup)g).getAppUserID(), new AppUserNicknameCallback() {

												@Override
												public void onNicknameFetched(String name) {
													groupType.setText(name);
													
												}
											});
											
										}
										
									}
								});
							} else {
								group.setVisible(true);
								alligator2.setVisible(true);
						    	group.setText(g.getDisplayName());		
							}
					} else {
				    	  group.setVisible(false);
				    	  alligator2.setVisible(false);
						
					}
					//TODO this shouldn't be off box
					if (playerID > 0L) {
						clientFactory.getRpcService().getPlayer(playerID, new AsyncCallback<Player>() {
						      public void onSuccess(Player result) {
						    	  player.setVisible(true);
						    	  alligator3.setVisible(true);
						    	  player.setText(result.getDisplayName());
						      }
	
							@Override
							public void onFailure(Throwable caught) {
								player.setText("Error");
	
								
							}
						});
					} else {
				    	  player.setVisible(false);
				    	  alligator3.setVisible(false);
						
					}

				} else if (event.getNewPlace() instanceof Home) {
					if (clientFactory.getLoginInfo() != null) {
						groupType.setText(clientFactory.getLoginInfo().getNickname());
					}
//					if (((Home)event.getNewPlace()).getAction() == )
					group.setVisible(false);
					alligator2.setVisible(false);
					player.setVisible(false);
					alligator3.setVisible(false);
					//((Home)event.getNewPlace()).getGroupID()
				} else if (event.getNewPlace() instanceof Manage) {
					if (clientFactory.getLoginInfo() != null) {
						groupType.setText("Draft");
						groupType.setVisible(true);
						alligator1.setVisible(true);
					}
					
					int round = ((Manage)event.getNewPlace()).getRound();
					if ( round == 0)
						group.setText("Roster");
					else {
						if (round < 5) {
							group.setText("Round " + round);
						} else if (round == 5) {
							group.setText("Knockout Rounds Roster");
						} else if (round == 6) {
							group.setText("Quarterfinals");
						}else if (round == 7) {
							group.setText("Semifinals");
						}else if (round == 8) {
							group.setText("Finals");
						}
					}
						
					group.setVisible(true);
					alligator2.setVisible(true);
					player.setVisible(true);
					alligator3.setVisible(true);
					step s = ((Manage)event.getNewPlace()).getStep();
					if (s != step.ROUND)
						player.setText(s.name());
					//((Home)event.getNewPlace()).getGroupID()
				}

			}
			
		});

		
	}
}
