package net.rugby.foundation.admin.client;

import java.util.List;

import net.rugby.foundation.admin.client.ui.AdminView;
import net.rugby.foundation.admin.client.ui.AdminViewImpl;
import net.rugby.foundation.client.ui.FieldDefinition;
import net.rugby.foundation.client.ui.PlayerPopupView;
import net.rugby.foundation.client.ui.PlayerPopupViewFieldDefinitions;
import net.rugby.foundation.client.ui.PlayerPopupViewImpl;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.PlayerPopupData;

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
	private PlayerPopupView<IPlayer> playerPopupView;
	private Object editPlayerViewFieldDefinitions;
	private List<FieldDefinition<IPlayer>> playerPopupViewFieldDefinitions;

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
	
	@Override
	public PlayerPopupView<IPlayer> getPlayerPopupView()
	{
        // lazily initialize our views, and keep them around to be reused
        //
        if (playerPopupView == null) {
        	playerPopupView = new PlayerPopupViewImpl<IPlayer>();
			if (editPlayerViewFieldDefinitions == null) {
				playerPopupViewFieldDefinitions = new PlayerPopupViewFieldDefinitions<IPlayer>().getFieldDefinitions();
	          }
		
            playerPopupView.setFieldDefinitions(playerPopupViewFieldDefinitions);
        }

		return playerPopupView;
	}
}
