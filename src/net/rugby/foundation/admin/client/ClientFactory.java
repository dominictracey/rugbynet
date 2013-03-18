package net.rugby.foundation.admin.client;

import java.util.List;
import net.rugby.foundation.admin.client.ui.CompetitionView;
import net.rugby.foundation.admin.client.ui.OrchestrationConfigurationView;
import net.rugby.foundation.admin.client.ui.SmartBar;
import net.rugby.foundation.admin.client.ui.playermatchstatspopup.PlayerMatchStatsPopupView;
import net.rugby.foundation.admin.client.ui.playerpopup.PlayerPopupView;
import net.rugby.foundation.admin.client.ui.task.TaskView;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.Position.position;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;

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
	RugbyAdminServiceAsync getRpcService();

	PlayerPopupView<IPlayer> getPlayerPopupView();
	void getCountryListAsync(GetCountryListCallback cb);
	void getPositionListAsync(GetPositionListCallback cb);
	void getMatchGroupAsync(Long id, GetMatchGroupCallback cb);
	
	PlayerMatchStatsPopupView<IPlayerMatchStats> getPlayerMatchStatsPopupView();

	CompetitionView getCompView();

	OrchestrationConfigurationView getOrchestrationConfigurationView();

	TaskView<IAdminTask> getTaskView();

	SmartBar getMenuBar();
}
