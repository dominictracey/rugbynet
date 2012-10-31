package net.rugby.foundation.admin.client.ui;

import java.util.Map;

import net.rugby.foundation.admin.shared.IOrchestrationConfiguration;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View base interface.
 * Extends IsWidget so a view impl can easily provide its container widget.
 */
public interface OrchestrationConfigurationView extends IsWidget {
  
	void setOrchConfigs(Map<String,IOrchestrationConfiguration> configs);

	void setPresenter(Presenter listener);
	void showStatus(String msg);
	
	public interface Presenter {
		/**
		 * Navigate to a new Place in the browser.
		 */
		Map<String,IOrchestrationConfiguration> saveClicked(Map<String,IOrchestrationConfiguration> configs);
	}
}
