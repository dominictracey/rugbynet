package net.rugby.foundation.game1.client.ui;

import java.util.List;
import java.util.Map;

import net.rugby.foundation.game1.shared.IConfiguration;
import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.model.shared.IClubhouse;
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
		
		/**
		 * @param id - entryId picked
		 */
		void entryPicked(IEntry e);
		
		/**
		 * @param id - clubhouseId picked
		 */
		void clubhousePicked(Long id);

	}

	/**
	 * @param compMap - hash table key: compId, value: ICompetition
	 */
	void setComps(Map<Long, String> compMap);
	
	/**
	 * @param entries - hash table key: clubhouseId, value: IClubhouse
	 */
	void setClubhouses(List<IClubhouse> entries, IConfiguration conf);

	/**
	 * @param entries - value: IEntry
	 */
	void setEntries(List<IEntry> entries);

}
