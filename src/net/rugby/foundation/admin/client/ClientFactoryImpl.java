package net.rugby.foundation.admin.client;

import java.util.List;
import net.rugby.foundation.admin.client.ui.AdminView;
import net.rugby.foundation.admin.client.ui.AdminViewImpl;
import net.rugby.foundation.admin.client.ui.FieldDefinition;
import net.rugby.foundation.admin.client.ui.playermatchstatspopup.PlayerMatchStatsPopupView;
import net.rugby.foundation.admin.client.ui.playermatchstatspopup.PlayerMatchStatsPopupViewFieldDefinitions;
import net.rugby.foundation.admin.client.ui.playermatchstatspopup.PlayerMatchStatsPopupViewImpl;
import net.rugby.foundation.admin.client.ui.playerpopup.PlayerPopupView;
import net.rugby.foundation.admin.client.ui.playerpopup.PlayerPopupViewFieldDefinitions;
import net.rugby.foundation.admin.client.ui.playerpopup.PlayerPopupViewImpl;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.Position.position;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;

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
	private PlayerMatchStatsPopupView<IPlayerMatchStats> playerMatchStatsPopupView;

	private List<FieldDefinition<IPlayer>> playerPopupViewFieldDefinitions;
	private List<FieldDefinition<IPlayerMatchStats>> playerMatchStatsPopupViewFieldDefinitions;

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
		view.setClientFactory(this);
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
			if (playerPopupViewFieldDefinitions == null) {
				playerPopupViewFieldDefinitions = new PlayerPopupViewFieldDefinitions<IPlayer>(this).getFieldDefinitions();
	          }
		
            playerPopupView.setFieldDefinitions(playerPopupViewFieldDefinitions);
        }

		return playerPopupView;
	}

	@Override
	public void getCountryListAsync(final GetCountryListCallback cb) {
		getRpcService().fetchCountryList(new AsyncCallback<List<ICountry>>() {

			@Override
			public void onFailure(Throwable caught) {


			}

			@Override
			public void onSuccess(List<ICountry> result) {
				cb.onCountryListFetched(result);

			}
		});
		
	}

	@Override
	public void getPositionListAsync(final GetPositionListCallback cb) {
		getRpcService().fetchPositionList(new AsyncCallback<List<position>>() {

			@Override
			public void onFailure(Throwable caught) {


			}

			@Override
			public void onSuccess(List<position> result) {
				cb.onPositionListFetched(result);

			}
		});
		
	}

	@Override
	public PlayerMatchStatsPopupView<IPlayerMatchStats> getPlayerMatchStatsPopupView() {
        // lazily initialize our views, and keep them around to be reused
        //
        if (playerMatchStatsPopupView == null) {
        	playerMatchStatsPopupView = new PlayerMatchStatsPopupViewImpl<IPlayerMatchStats>();
			if (playerMatchStatsPopupViewFieldDefinitions == null) {
				playerMatchStatsPopupViewFieldDefinitions = new PlayerMatchStatsPopupViewFieldDefinitions<IPlayerMatchStats>(this).getFieldDefinitions();
	          }
		
			playerMatchStatsPopupView.setFieldDefinitions(playerMatchStatsPopupViewFieldDefinitions);
        }

		return playerMatchStatsPopupView;
	}

	@Override
	public void getMatchGroupAsync(Long id, final GetMatchGroupCallback cb) {
		getRpcService().getMatch(id, new AsyncCallback<IMatchGroup>() {

			@Override
			public void onFailure(Throwable caught) {


			}

			@Override
			public void onSuccess(IMatchGroup result) {
				cb.onMatchGroupFetched(result);

			}
		});		
	}
}
