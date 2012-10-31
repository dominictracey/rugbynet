package net.rugby.foundation.game1.client.ui;

import java.util.Map;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.game1.client.activity.JoinClubhouseActivity;
import net.rugby.foundation.game1.client.place.Game1Place;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Sample implementation of {@link Game1View}.
 */
public class Game1ViewImpl extends Composite implements Game1View, ValueChangeHandler<String>  {
	
	private final int HOME_TAB = 0;
	private final int PLAY_TAB = 1;
	private final int LEADERBOARD_TAB = 2;
	private final int CLUBHOUSE_TAB = 3;
	private final int ADMIN_TAB = 4;
	

	interface Binder extends UiBinder<Widget, Game1ViewImpl> {
	}
	
	private static final Binder binder = GWT.create(Binder.class);

	private Presenter listener;
	@UiField
	TabPanel tabPanel;
	@UiField
	SmartBarImpl smartBar;
	private HomeView homeView = null;
	private PlayView playView = null;
	private LeaderboardView leaderboardView = null;
	private ClubhouseView clubhouseView = null;
	private Game1ConfigurationView configurationView = null;
	private boolean clubhouseTabDisabled = false;
	private boolean leaderboardTabDisabled = false;
	private String locationString = "";
	private JoinClubhouseActivity joinPresenter;
	//private Integer currentTab = 0;
	
	public Game1ViewImpl() {
		initWidget(binder.createAndBindUi(this));
		
		getElement().addClassName("playarea");
		homeView = new HomeViewImpl();
		playView = new PlayViewImpl();
		leaderboardView = new LeaderboardViewImpl();
		clubhouseView = new ClubhouseViewImpl();
		configurationView = new Game1ConfigurationViewImpl();
		
		tabPanel.add(homeView, "Home");
		tabPanel.add(playView, "Play");
		tabPanel.add(leaderboardView, "Leaderboard");
		tabPanel.add(clubhouseView, "Clubhouse");
		//tabPanel.add(configurationView, "Admin");
		
		tabPanel.getElement().addClassName("apparea");
		

		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				
			    // Fire an history event corresponding to the tab selected
			    switch (event.getSelectedItem()) {
			    case 0:
			        History.newItem(locationString.split("\\;")[0] + ";0", false);
			        break;
			    case 1:
			        History.newItem(locationString.split("\\;")[0] + ";1", false);
			        break;
			    case 2:
			        History.newItem(locationString.split("\\;")[0] + ";2", false);
			        break;
			    case 3:
			        History.newItem(locationString.split("\\;")[0] + ";3", false);
			        break;
			    case 4:
			        History.newItem(locationString.split("\\;")[0] + ";4", false);
		    		if (listener != null)
		    			if (listener instanceof Game1ConfigurationView.Presenter)
		    				((Game1ConfigurationView.Presenter)listener).populateView();
			    		
			        break;
			    }		
			}
			
		});
		
		tabPanel.getTabBar().addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {

			public void onBeforeSelection(BeforeSelectionEvent<Integer> event)  {

				if (event.getItem().intValue() == LEADERBOARD_TAB && leaderboardTabDisabled) {
			          event.cancel();
				}
				else if (event.getItem().intValue() == CLUBHOUSE_TAB && clubhouseTabDisabled) {
			          event.cancel();
				}
			}
		});
		
		
		
		History.addValueChangeHandler(this);

	}


	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
		Game1Place.Tokenizer tokenizer = new Game1Place.Tokenizer();
		locationString = "Game1Place:" + tokenizer.getToken(listener.getPlace());
		
		if (listener instanceof SmartBar.Presenter)
			smartBar.setPresenter((SmartBar.Presenter)listener);
		if (listener instanceof HomeView.Presenter)
			homeView.setPresenter((HomeView.Presenter)listener);
		if (listener instanceof PlayView.Presenter)
			playView.setPresenter((PlayView.Presenter)listener);
		if (listener instanceof LeaderboardView.Presenter)
			leaderboardView.setPresenter((LeaderboardView.Presenter)listener);
		if (listener instanceof ClubhouseView.Presenter)
			clubhouseView.setPresenter((ClubhouseView.Presenter)listener);
		
		if (listener.isAdmin()) {
			configurationView.setPresenter((Game1ConfigurationView.Presenter)listener);
		} else {
			//tabPanel.remove(4);
		}

	}
	
	@Override
	public void selectTab(int index) {
	    if (index >=0 && index < tabPanel.getWidgetCount()) {

	        if (index != tabPanel.getTabBar().getSelectedTab()) {
	        	if (listener != null)
	        		listener.setCurrentTab(index);
	        	tabPanel.selectTab(index);
	        }
	    }	
	}

	
	@Override
	public HomeView getHomeView() {
		return homeView;
	}

	
	@Override
	public PlayView getPlayView() {
		return playView;
	}

	
	@Override
	public LeaderboardView getLeaderboardView() {
		return leaderboardView;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.Game1View#getSmartBar()
	 */
	@Override
	public SmartBar getSmartBar() {
		
		return smartBar;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.Game1View#setComps(java.util.Map)
	 */
	@Override
	public void setComps(Map<Long, String> result) {
		smartBar.setComps(result);
		
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.Game1View#getClubhouseView()
	 */
	@Override
	public ClubhouseView getClubhouseView() {
		return clubhouseView;
	}


	/* (non-Javadoc)
	 * @see com.google.gwt.event.logical.shared.ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)
	 */
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		locationString = event.getValue();
		
		//everything after the semicolon is the tab number
		if (locationString.split("\\;").length == 2) {
			int index = Integer.parseInt(locationString.split("\\;")[1]);
			//int tab = Integer.parseInt(event.getValue())-1;
		    if (index >=0 && index < tabPanel.getWidgetCount()) {
		        if (index != tabPanel.getTabBar().getSelectedTab()) {
		        	//currentTab = index;
		        	listener.setCurrentTab(index);
		        	tabPanel.selectTab(listener.getCurrentTab(), true);
		        }
		    }
		}

	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.Game1View#clear()
	 */
	@Override
	public void clear(boolean needLogin) {
		if (needLogin) {
			smartBar.clubhouseBar.clearItems();
			smartBar.entryBar.clearItems();
		}
		homeView.setEntries(null);
		homeView.showAccountButtons(needLogin);
		clubhouseView.clear();
		playView.clear();
		
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.Game1View#getConfigurationView()
	 */
	@Override
	public Game1ConfigurationView getConfigurationView() {
		return configurationView;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.Game1View#disableClubhouseView()
	 */
	@Override
	public void clubhouseViewDisabled(Boolean disable) {
		
		clubhouseTabDisabled = disable;
		
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.Game1View#leaderboardViewDisabled(java.lang.Boolean)
	 */
	@Override
	public void leaderboardViewDisabled(Boolean disable) {
		leaderboardTabDisabled = disable;	
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.Game1View#setJoinPresenter(net.rugby.foundation.game1.client.activity.JoinClubhouseActivity)
	 */
	@Override
	public void setJoinPresenter(JoinClubhouseActivity joinClubhouseActivity) {
		this.joinPresenter = joinClubhouseActivity;
		homeView.setJoinClubhousePresenter(joinClubhouseActivity);
		
	}
	
	@Override
	public void addAdminTab() {
		if (Core.getCore().getClientFactory().getLoginInfo().isAdmin()) {
			tabPanel.add(configurationView, "Admin");
		}
	}
}
