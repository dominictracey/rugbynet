package net.rugby.foundation.game1.client.ui;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * View base interface.
 * Extends IsWidget so a view impl can easily provide its container widget.
 */
public interface NewEntryView extends IsWidget {
  
	void setUserName(String userName);
	void setCompName(String compName);

	void setPresenter(Presenter listener);

	public interface Presenter {

		void onCreate(String entryName);
		void onCancel();
	}

	/**
	 * @param count - send in how many entries the user has for this comp plus 1.
	 */
	void setCount(int count);
}
