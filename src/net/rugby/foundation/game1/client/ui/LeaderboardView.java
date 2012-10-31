package net.rugby.foundation.game1.client.ui;

import net.rugby.foundation.game1.shared.ILeaderboard;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View base interface.
 * Extends IsWidget so a view impl can easily provide its container widget.
 */
public interface LeaderboardView extends IsWidget {
  
	void setData(ILeaderboard lb);

	void setPresenter(Presenter listener);

	public interface Presenter {
		/**
		 * Navigate to a new Place in the browser.
		 */
		void goTo(Place place);
	}
}
