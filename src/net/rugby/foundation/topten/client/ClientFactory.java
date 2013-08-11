package net.rugby.foundation.topten.client;

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
//	public interface GetPositionListCallback {
//		void onPositionListFetched(List<position> result);
//	}
//	
//	public interface GetMatchGroupCallback {
//		void onMatchGroupFetched(IMatchGroup result);
//	}
//	
	EventBus getEventBus();

	PlaceController getPlaceController();
	TopTenListServiceAsync getRpcService();

//	PlayerPopupView<IPlayer> getPlayerPopupView();
//	void getCountryListAsync(GetCountryListCallback cb);
//	void getPositionListAsync(GetPositionListCallback cb);
//	void getMatchGroupAsync(Long id, GetMatchGroupCallback cb);
//	
//	PlayerMatchStatsPopupView<IPlayerMatchStats> getPlayerMatchStatsPopupView();
//
	TopTenListView<ITopTenItem> getListView();
//
//	OrchestrationConfigurationView getOrchestrationConfigurationView();
//
//	TaskView<IAdminTask> getTaskView();
//
//	SmartBar getMenuBar();
//
//	PlayerListView<IPlayerMatchInfo> getPlayerListView();
//
//	PortalView<IPlayerMatchInfo> getPortalView();
//	
//	TeamMatchStatsPopupView<ITeamMatchStats> getTeamMatchStatsPopupView();
//	
//	MatchRatingEngineSchemaPopupView<ScrumMatchRatingEngineSchema20130713> getMatchRatingEngineSchemaPopupView();
//
//	void flushAllPipelineJobs();
	EditTTIText getEditTTITextDialog();
}
