package net.rugby.foundation.game1.client.ui;

import java.util.Map;

import net.rugby.foundation.game1.client.activity.JoinClubhouseActivity;
import net.rugby.foundation.game1.client.place.Game1Place;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View base interface.
 * Extends IsWidget so a view impl can easily provide its container widget.
 */
public interface Game1View extends IsWidget {
  
	/**
	 * @author home
	 *
	 */
	public interface JoinClubhousePresenter {
		// marker
	}

	void setPresenter(Presenter listener);

	public interface Presenter {
		/**
		 * Navigate to a new Place in the browser.
		 */
		void goTo(Place place);
		void compPicked(Long compId);
		Game1Place getPlace();
		void setCurrentTab(int tab);
		int getCurrentTab();
		/**
		 * @return whether the currently logged on user is an admin
		 */
		boolean isAdmin();
	}
	
	HomeView getHomeView();
	PlayView getPlayView();
	LeaderboardView getLeaderboardView();
	Game1ConfigurationView getConfigurationView();
	SmartBar getSmartBar();
	/**
	 * @param result
	 */
	void setComps(Map<Long, String> result);
	/**
	 * @param index
	 */
	void selectTab(int index);
	/**
	 * @param event
	 */
	//void onSelection(SelectionEvent<Integer> event);
	/**
	 * @return
	 */
	ClubhouseView getClubhouseView();
	/**
	 * Present the site to a user depending on whether they are logged in or not
	 */
	void clear(boolean needLogin);

	/**
	 * @param disable
	 */
	void clubhouseViewDisabled(Boolean disable);
	/**
	 * @param b
	 */
	void leaderboardViewDisabled(Boolean disable);
	/**
	 * @param joinClubhouseActivity
	 */
	void setJoinPresenter(JoinClubhouseActivity joinClubhouseActivity);
	/**
	 * 
	 */
	void addAdminTab();
}
