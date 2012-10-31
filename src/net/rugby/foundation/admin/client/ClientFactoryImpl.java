package net.rugby.foundation.admin.client;

import net.rugby.foundation.admin.client.ui.AdminView;
import net.rugby.foundation.admin.client.ui.AdminViewImpl;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;

/**
 * Sample implementation of {@link ClientFactory}.
 */
public class ClientFactoryImpl implements ClientFactory {
  
	private static final EventBus eventBus = new SimpleEventBus();
	@SuppressWarnings("deprecation")
	private static final PlaceController placeController = new PlaceController(eventBus);
	private static final AdminView view = new AdminViewImpl();
	private static final RugbyAdminServiceAsync rpcService = GWT.create(RugbyAdminService.class);

	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public PlaceController getPlaceController() {
		return placeController;
	}

	@Override
	public AdminView getAdminView() {
		return view;
	}

	@Override
	public RugbyAdminServiceAsync getRpcService() {
		return rpcService;
	}
}
