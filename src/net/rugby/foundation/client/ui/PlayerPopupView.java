package net.rugby.foundation.client.ui;

import java.util.List;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget. 
 */
public interface PlayerPopupView<T> extends IsWidget
{
	void setPlayer(T player);
	void setPresenter(Presenter<T> listener);
	void setFieldDefinitions(List<FieldDefinition<T>> fieldDefinitions);
	void clear();
	
	public interface Presenter<T>
	{
		void goTo(Place place);
		void onCloseButtonClicked();

	}



	
}