package net.rugby.foundation.admin.client;

import net.rugby.foundation.admin.client.ui.AdminView;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;

/**
 * ClientFactory helpful to use a factory or dependency injection framework like GIN to obtain 
 * references to objects needed throughout your application like the {@link EventBus},
 * {@link PlaceController} and views.
 */
public interface ClientFactory {

	EventBus getEventBus();

	PlaceController getPlaceController();
	public AdminView getAdminView();
	RugbyAdminServiceAsync getRpcService();
}
