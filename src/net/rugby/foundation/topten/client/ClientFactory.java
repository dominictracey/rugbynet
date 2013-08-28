package net.rugby.foundation.topten.client;

import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.topten.client.ui.content.ContentView;
import net.rugby.foundation.topten.client.ui.content.EditContent;
import net.rugby.foundation.topten.client.ui.toptenlistview.EditTTIText;
import net.rugby.foundation.topten.client.ui.toptenlistview.TopTenListView;
import net.rugby.foundation.topten.model.shared.ITopTenItem;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;

/**
 * ClientFactory helpful to use a factory or dependency injection framework like GIN to obtain 
 * references to objects needed throughout your application like the {@link EventBus},
 * {@link PlaceController} and views.
 */
public interface ClientFactory {

//	public interface GetCountryListCallback {
//		void onCountryListFetched(List<ICountry> countries);
//	}
//	

	EventBus getEventBus();

	PlaceController getPlaceController();
	TopTenListServiceAsync getRpcService();

	TopTenListView<ITopTenItem> getListView();
	EditTTIText getEditTTITextDialog();
	ContentView getContentView();
	EditContent getEditContentDialog();
}
