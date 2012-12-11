package net.rugby.foundation.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.rugby.foundation.client.ClientFactory;
import net.rugby.foundation.client.ClientFactory.GetCompetitionCallback;
import net.rugby.foundation.client.event.DoneNavPaintEvent;
import net.rugby.foundation.client.event.DoneStackInitEvent;
import net.rugby.foundation.client.event.DoneStackInitEventHandler;
import net.rugby.foundation.client.place.Browse;
import net.rugby.foundation.client.place.Home;
import net.rugby.foundation.client.place.Home.actions;
import net.rugby.foundation.client.place.Manage;
import net.rugby.foundation.client.ui.groupStack.GroupStack;
import net.rugby.foundation.model.shared.Group;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IGroup;
import net.rugby.foundation.model.shared.MatchGroup;
import net.rugby.foundation.model.shared.Group.GroupType;
import net.rugby.foundation.model.shared.MyGroup;
import net.rugby.foundation.model.shared.PositionGroup;
import net.rugby.foundation.model.shared.TeamGroup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class GroupBrowser extends Composite implements DoneStackInitEventHandler {
	private static GroupBrowserUiBinder uiBinder = GWT.create(GroupBrowserUiBinder.class);

	@UiField StackLayoutPanel stackPanel  = new StackLayoutPanel(Unit.EM);
	@UiField(provided=true)
	CellList<MyGroup> myList;
	@UiField 
	Label myListHeader;
	@UiField(provided=true)
	CellList<TeamGroup> teamList;
	@UiField 
	Label teamListHeader;
	@UiField(provided=true)
	CellList<PositionGroup> positionList;
	@UiField 
	Label positionListHeader;
	@UiField(provided=true)
	CellList<MatchGroup> matchList;
	@UiField 
	Label matchListHeader;
	@UiField(provided=true)
	CellList<MatchGroup> poolMatchList;
	@UiField 
	Label poolMatchListHeader;
	@UiField(provided=true)
	CellList<TeamGroup> poolTeamList;
	@UiField 
	Label poolTeamListHeader;
	@UiField(provided=true)
	CellList<PositionGroup> poolPositionList;
	@UiField 
	Label poolPositionListHeader;
	
	private Long groupID; //currently selected group (0 = none)
	private GroupType groupTypeID = GroupType.NONE; 
	
	private FlowPanel parent;
	//@UiTemplate("StackPanel.ui.xml")
	
	GroupStack<MyGroup> myStack;
	GroupStack<TeamGroup> teamStack;
	GroupStack<PositionGroup> positionStack;
	GroupStack<MatchGroup> matchStack;
	GroupStack<MatchGroup> poolMatchStack;
	GroupStack<TeamGroup> poolTeamStack;
	GroupStack<PositionGroup> poolPositionStack;
	
	private int initCount = 0;
	private static final int NUM_STACKS = 7;
	private ClientFactory clientFactory;
	private static HashMap<Long,Group> groupsByID = new HashMap<Long,Group>();
	
	public GroupType getGroupTypeID() {
		return groupTypeID;
	}

	public void setGroupTypeID(GroupType groupTypeID) {
		this.groupTypeID = groupTypeID;
	}

	public Long getGroupID() {
		return groupID;
	}

	public void setGroupID(Long groupID) {
		this.groupID = groupID;
	}
	
	interface GroupBrowserUiBinder extends UiBinder<Widget, GroupBrowser>
	{
	}
	
	public HashMap<Long, Group> getGroupsByID() {
		return groupsByID;
	}
	  
	private void initWidget()
	{
		if (initCount++ == NUM_STACKS) {
			myList = myStack.getCellList();
			teamList = teamStack.getCellList();
			positionList = positionStack.getCellList(); 
			matchList = matchStack.getCellList();
			poolTeamList = poolTeamStack.getCellList();
			poolMatchList = poolMatchStack.getCellList();
			poolPositionList = poolPositionStack.getCellList(); 
			
			initWidget(uiBinder.createAndBindUi(this));
			//TODO Lazy load?
			myStack.addCellList();
			teamStack.addCellList();
			positionStack.addCellList();
			matchStack.addCellList();
			poolTeamStack.addCellList();
			poolMatchStack.addCellList();
			poolPositionStack.addCellList();
			
			if (parent != null) {
				parent.add(stackPanel);	
				DOM.setElementAttribute(stackPanel.getElement(), "id", "stackPanel");
				
				//fire an event to let the app know it can add the content panel to the layout
				clientFactory.getEventBus().fireEvent(new DoneNavPaintEvent(stackPanel)); 
			}	
		}
	}
	
	public void Init(final ClientFactory cf, FlowPanel mainContent)
	{		
		clientFactory = cf;

		myStack = new GroupStack<MyGroup>(this, clientFactory);
		teamStack = new GroupStack<TeamGroup>(this, clientFactory);
		positionStack = new GroupStack<PositionGroup>(this, clientFactory);
		matchStack = new GroupStack<MatchGroup>(this, clientFactory);
		poolMatchStack = new GroupStack<MatchGroup>(this,clientFactory);
		poolTeamStack = new GroupStack<TeamGroup>(this,clientFactory);
		poolPositionStack = new GroupStack<PositionGroup>(this, clientFactory);
		
		parent = mainContent;
		
		// the stacks will emit events when they are done
		cf.getEventBus().addHandler(DoneStackInitEvent.TYPE, this);

		
		// set the knockout contents
		clientFactory.getCompetitionAsync("2011 RWC Knockout", new GetCompetitionCallback() {

			@Override
			public void onCompetitionFetched(ICompetition comp) {
				setStackGroupContents(GroupType.MY, myStack,comp.getId());

				setStackGroupContents(GroupType.POSITION,positionStack,comp.getId());
				setStackGroupContentsByCompetition(GroupType.MATCH,matchStack,comp.getId());
				setStackGroupContentsByCompetition(GroupType.TEAM,teamStack,comp.getId());
			}
			
		});
		
		// set the pool contents
		clientFactory.getCompetitionAsync("2011 RWC Pool", new GetCompetitionCallback() {

			@Override
			public void onCompetitionFetched(ICompetition comp) {
				setStackGroupContents(GroupType.POSITION,poolPositionStack,comp.getId());
				setStackGroupContentsByCompetition(GroupType.MATCH,poolMatchStack,comp.getId());
				setStackGroupContentsByCompetition(GroupType.TEAM,poolTeamStack,comp.getId());
			}
			
		});
		
		initWidget();
		registerForPlaceChanges();

	}

//the header callbacks
  @UiHandler("myListHeader")
  void onMyListHeaderClicked(ClickEvent event) {
	  groupTypeID = GroupType.MY;
	  clientFactory.getPlaceController().goTo(new Home(clientFactory.getHomeID(), GroupType.MY, 0L, 0L, actions.NONE));		  
	  String style = DOM.getElementAttribute(stackPanel.getElement(), "style");
	  style += " height:530px;";
	  DOM.setElementAttribute(stackPanel.getElement(), "style", style);

  }

  @UiHandler("teamListHeader")
  void onteamListHeaderClicked(ClickEvent event) {
	  groupTypeID = GroupType.TEAM;
	  clientFactory.getPlaceController().goTo(new Browse(clientFactory.getHomeID(), groupTypeID,0L,0L));		  
	  String style = DOM.getElementAttribute(stackPanel.getElement(), "style");
	  style += " height:530px;";
	  DOM.setElementAttribute(stackPanel.getElement(), "style", style);

  }

  @UiHandler("poolTeamListHeader")
  void onpoolTeamListHeaderClicked(ClickEvent event) {
	  groupTypeID = GroupType.TEAM;
	  clientFactory.getPlaceController().goTo(new Browse(clientFactory.getHomeID(), groupTypeID,0L,0L));		  
	  String style = DOM.getElementAttribute(stackPanel.getElement(), "style");
	  style += " height:610px;";
	  DOM.setElementAttribute(stackPanel.getElement(), "style", style);

  }
  @UiHandler("positionListHeader")
  void onPositionHeaderClicked(ClickEvent event) {
	  groupTypeID = GroupType.POSITION;
	  clientFactory.getPlaceController().goTo(new Browse(clientFactory.getHomeID(),groupTypeID,0L,0L));		  
	  String style = DOM.getElementAttribute(stackPanel.getElement(), "style");
	  style += " height:530px;";
	  DOM.setElementAttribute(stackPanel.getElement(), "style", style);
  }

  @UiHandler("poolPositionListHeader")
  void onPoolPositionHeaderClicked(ClickEvent event) {
	  groupTypeID = GroupType.POSITION;
	  clientFactory.getPlaceController().goTo(new Browse(clientFactory.getHomeID(),groupTypeID,0L,0L));		  
	  String style = DOM.getElementAttribute(stackPanel.getElement(), "style");
	  style += " height:530px;";
	  DOM.setElementAttribute(stackPanel.getElement(), "style", style);
  }
  
  @UiHandler("matchListHeader")
  void onMatchHeaderClicked(ClickEvent event) {
	  groupTypeID = GroupType.MATCH;
	  clientFactory.getPlaceController().goTo(new Browse(clientFactory.getHomeID(),groupTypeID,0L,0L));	
	  
	  String style = DOM.getElementAttribute(stackPanel.getElement(), "style");
	  style += " height:530px;";
	  DOM.setElementAttribute(stackPanel.getElement(), "style", style);
  }

  @UiHandler("poolMatchListHeader")
  void onPoolMatchHeaderClicked(ClickEvent event) {
	  groupTypeID = GroupType.MATCH;
	  clientFactory.getPlaceController().goTo(new Browse(clientFactory.getHomeID(),groupTypeID,0L,0L));	
	  
	  String style = DOM.getElementAttribute(stackPanel.getElement(), "style");
	  style += " height:1100px;";
	  DOM.setElementAttribute(stackPanel.getElement(), "style", style);
  }
  
	private void registerForPlaceChanges() {
		clientFactory.getEventBus().addHandler(PlaceChangeEvent.TYPE,	new PlaceChangeEvent.Handler() {
			
			@Override
			public void onPlaceChange(PlaceChangeEvent event) {		
				
				clearSelections();
				
				if (event.getNewPlace() instanceof Browse) {
					// home shouldn't change for this go around
					// set groupType
					if (groupTypeID != ((Browse)event.getNewPlace()).getGroupTypeID()) {
						groupTypeID = ((Browse)event.getNewPlace()).getGroupTypeID();
						//open up the right stack
						if (groupTypeID == GroupType.TEAM)
							stackPanel.showWidget(teamList);
						else if (groupTypeID == GroupType.POSITION)
							stackPanel.showWidget(positionList);
						else if (groupTypeID == GroupType.MATCH)
							stackPanel.showWidget(matchList);
					}

					// set group
					if (groupID != ((Browse)event.getNewPlace()).getGroupID()) {
						groupID = ((Browse)event.getNewPlace()).getGroupID(); 
						if (groupID > 0L) {
							IGroup group = groupsByID.get(groupID);
							if (group != null) {
								String name = group.getDisplayName();								
							} else {
								clearSelections();
							}
							
						}
					}
					
				} else if (event.getNewPlace() instanceof Home || event.getNewPlace() instanceof Manage) {
					if (clientFactory.getLoginInfo() != null) {
						if (!clientFactory.getLoginInfo().getTeamIDs().isEmpty()) {
							stackPanel.showWidget(myList);
						} else {
							stackPanel.showWidget(teamList);
						}
					} else {
						stackPanel.showWidget(teamList);
					}
				}  

			}
			
		});

		
	}
	
	private void clearSelections() {
		teamStack.clear();
		positionStack.clear();
		matchStack.clear();
		myStack.clear();
		
	}

	public StackLayoutPanel getStackPanel() {
		return stackPanel;
	}
	

	@Override
	public void onDoneStackInit(DoneStackInitEvent event) {
			initWidget();
	}

	public void refreshMyStack()
	{

  	    // get groups of proper type to populate stack
		clientFactory.getRpcService().getGroupsByGroupType(GroupType.MY, new AsyncCallback<ArrayList<Group>>() {
		      public void onSuccess(ArrayList<Group> result) {

		    	  ArrayList<MyGroup> arrayList = new ArrayList<MyGroup>();
		    	  Iterator<Group> iter = result.iterator();
		    	  while (iter.hasNext()) 	  {
		    		  Group g = iter.next();
			    		  arrayList.add((MyGroup) g);
			    		  getGroupsByID().put(g.getId(), g);
		    	  }

		    	  myStack.getCellList().setRowData(arrayList);

		      }
				public void onFailure(Throwable caught) {
			    	  Window.alert("Error getting groups of type " + GroupType.MY.name());
			      }
			});

	}
	
	private <T extends IGroup> void setStackGroupContents(final GroupType type, final GroupStack<T> stack, final Long compID) {
  	    // get groups of proper type to populate stack
		clientFactory.getRpcService().getGroupsByGroupType(type, new AsyncCallback<ArrayList<Group>>() {
		      @SuppressWarnings("unchecked")
			public void onSuccess(ArrayList<Group> result) {

		    	  ArrayList<T> arrayList = new ArrayList<T>();
		    	  Iterator<Group> iter = result.iterator();
		    	  while (iter.hasNext()) 	  {
		    		  Group g = iter.next();
			    		  arrayList.add((T) g);
			    		  groupsByID.put(g.getId(), g);
		    	  }
		    	  
		    	  stack.Init(type, arrayList, compID);

		    	  //fire an event to let the app know it can add the content panel to the layout
		      	  if (!stack.isInitialized())
		      	  {
		      		  stack.setInitialized(true);
			    	  clientFactory.getEventBus().fireEvent(new DoneStackInitEvent(type)); 
		      	  }
	    	  

		      }

			public void onFailure(Throwable caught) {
		    	  Window.alert("Error getting groups of type " + type.name());
		      }
		});
	}
	
	private <T extends IGroup> void setStackGroupContentsByCompetition(final GroupType type, final GroupStack<T> stack, final Long compID) {
  	    // get groups of proper type to populate stack
		clientFactory.getRpcService().getGroupsByGroupTypeByComp(type, compID, new AsyncCallback<ArrayList<Group>>() {
		      @SuppressWarnings("unchecked")
			public void onSuccess(ArrayList<Group> result) {

		    	  ArrayList<T> arrayList = new ArrayList<T>();
		    	  Iterator<Group> iter = result.iterator();
		    	  while (iter.hasNext()) 	  {
		    		  Group g = iter.next();
			    		  arrayList.add((T) g);
			    		  groupsByID.put(g.getId(), g);
		    	  }
		    	  
		    	  stack.Init(type, arrayList, compID);

		    	  //fire an event to let the app know it can add the content panel to the layout
		      	  if (!stack.isInitialized())
		      	  {
		      		  stack.setInitialized(true);
			    	  clientFactory.getEventBus().fireEvent(new DoneStackInitEvent(type)); 
		      	  }
	    	  

		      }

			public void onFailure(Throwable caught) {
		    	  Window.alert("Error getting groups of type " + type.name());
		      }
		});		
	}
}
