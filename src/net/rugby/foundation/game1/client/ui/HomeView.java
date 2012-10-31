package net.rugby.foundation.game1.client.ui;

import java.util.List;

import net.rugby.foundation.game1.client.ClientFactory;
import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.model.shared.ICompetition;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View base interface.
 * Extends IsWidget so a view impl can easily provide its container widget.
 */
public interface HomeView extends IsWidget {
  
	void setEntries(List<IEntry> entries);
	void setPresenter(Presenter listener);
	public interface JoinClubhousePresenter {
		/**
		 * @return clientFactory - ClientFactory for use by view
		 */
		ClientFactory getClientFactory();		
	}
	
	public interface Presenter {
		/**
		 * Navigate to a new Place in the browser.
		 */
		void goTo(Place place);

		/**
		 * 
		 */
		void newEntryClicked();

		/**
		 * @param e - Entry the user wants to inspect
		 */
		void entryClicked(IEntry e);
		
		/**
		 * @return clientFactory - ClientFactory for use by view
		 */
		ClientFactory getClientFactory();


	}

	/**
	 * Show the Sign In/ Sign Up buttons if true or the Create New Entry button if false
	 */
	void showAccountButtons(boolean show);
	/**
	 * @param show - whether to show
	 */
	void showCreateClubhouse(boolean show);
	
	void setJoinClubhousePresenter(JoinClubhousePresenter presenter);
	/**
	 * @param comp - if the comp is not Underway, let the user know they can't create new entries
	 */
	void showCompOver(ICompetition comp);
}
