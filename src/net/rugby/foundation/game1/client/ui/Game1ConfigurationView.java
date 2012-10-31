package net.rugby.foundation.game1.client.ui;

import java.util.List;

import net.rugby.foundation.game1.shared.IConfiguration;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * View base interface.
 * Extends IsWidget so a view impl can easily provide its container widget.
 */
public interface Game1ConfigurationView extends IsWidget {
  
	void setConfig(IConfiguration config, net.rugby.foundation.model.shared.ICoreConfiguration coreConf);

	void setPresenter(Presenter listener);
	void showStatus(String msg);
	
	public interface Presenter {
		/**
		 * Navigate to a new Place in the browser.
		 */
		IConfiguration saveClicked(IConfiguration config, List<Long> compsToAdd, List<Long> compsToDrop);

		/**
		 * asking to have setConfig called on it
		 */
		void populateView();

		/**
		 * @param config
		 * @param compsToRedo
		 */
		void redoMatchStatsClicked(IConfiguration config, List<Long> compsToRedo);
	}
}
