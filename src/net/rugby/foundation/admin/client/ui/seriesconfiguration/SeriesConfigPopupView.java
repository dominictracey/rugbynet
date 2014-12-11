package net.rugby.foundation.admin.client.ui.seriesconfiguration;

import net.rugby.foundation.admin.client.ClientFactory;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget. 
 */
public interface SeriesConfigPopupView<T> extends IsWidget
{
	void setConfig(T config);
	void setPresenter(Presenter<T> listener);
	void setFieldDefinitions(SeriesConfigPopupViewFieldDefinitions<T> fieldDefinitions, ClientFactory clientFactory);
	void clear();
	
	public interface Presenter<T>
	{
		ClientFactory getClientFactory();
		void onSaveConfigClicked(T player);
		void onCancelConfigClicked();

	}
	
}