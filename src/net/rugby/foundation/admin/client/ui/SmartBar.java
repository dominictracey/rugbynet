package net.rugby.foundation.admin.client.ui;

import java.util.Map;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View base interface.
 * Extends IsWidget so a view impl can easily provide its container widget.
 */
public interface SmartBar extends IsWidget {
  
	void setName(String helloName);

	void setPresenter(Presenter listener);

	public interface Presenter {
		/**
		 * Navigate to a new Place in the browser.
		 */
		void goTo(Place place);

		/**
		 * @param id - compId picked
		 */
		void compPicked(Long id);

	}

	/**
	 * @param compMap - hash table key: compId, value: ICompetition
	 */
	void setComps(Map<Long, String> compMap);

}
