package net.rugby.foundation.admin.client.ui.seriesconfiguration;

import java.util.List;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.ui.FieldDefinition;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget. 
 */
public interface SeriesConfigPopupView<T> extends IsWidget
{
	void setConfig(T config);
	void setPresenter(Presenter<T> listener);
	void setFieldDefinitions(List<FieldDefinition<T>> fieldDefinitions);
	void clear();
	
	public interface Presenter<T>
	{
		ClientFactory getClientFactory();
		void onSaveConfigClicked(T player);
		void onCancelConfigClicked();

	}
	
}