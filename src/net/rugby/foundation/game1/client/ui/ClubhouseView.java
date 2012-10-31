/**
 * 
 */
package net.rugby.foundation.game1.client.ui;

import java.util.List;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

import net.rugby.foundation.game1.shared.ILeaderboard;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.IClubhouseMembership;

/**
 * @author home
 *
 */
public interface ClubhouseView  extends IsWidget {
	public interface Presenter {
		/**
		 * Navigate to a new Place in the browser.
		 */
		void goTo(Place place);
	}
	
	/**
	 * @param listener
	 */
	void setPresenter(Presenter listener);
	/**
	 * @param data
	 */
	void setData(ILeaderboard data, IClubhouse clubhouse, List<IClubhouseMembership> members);
	
	/**
	 * delete page content
	 */
	void clear();
	/**
	 * @param show
	 */
	void showCreateClubhouse(boolean show);
}
