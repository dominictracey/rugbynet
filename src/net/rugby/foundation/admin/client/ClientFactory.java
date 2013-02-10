package net.rugby.foundation.admin.client;

import java.util.List;
import net.rugby.foundation.admin.client.ui.AdminView;
import net.rugby.foundation.admin.client.ui.playermatchstatspopup.PlayerMatchStatsPopupView;
import net.rugby.foundation.admin.client.ui.playerpopup.PlayerPopupView;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.Position.position;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;

/**
 * ClientFactory helpful to use a factory or dependency injection framework like GIN to obtain 
 * references to objects needed throughout your application like the {@link EventBus},
 * {@link PlaceController} and views.
 */
public interface ClientFactory {

	public interface GetCountryListCallback {
		void onCountryListFetched(List<ICountry> countries);
	}
	
	public interface GetPositionListCallback {
		void onPositionListFetched(List<position> result);
	}
	
	public interface GetMatchGroupCallback {
		void onMatchGroupFetched(IMatchGroup result);
	}
	
	EventBus getEventBus();

	PlaceController getPlaceController();
	public AdminView getAdminView();
	RugbyAdminServiceAsync getRpcService();

	PlayerPopupView<IPlayer> getPlayerPopupView();
	void getCountryListAsync(GetCountryListCallback cb);
	void getPositionListAsync(GetPositionListCallback cb);
	void getMatchGroupAsync(Long id, GetMatchGroupCallback cb);
	
	PlayerMatchStatsPopupView<IPlayerMatchStats> getPlayerMatchStatsPopupView();
}
