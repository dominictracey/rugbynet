package net.rugby.foundation.topten.client;


import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.model.shared.IServerPlace;
import net.rugby.foundation.topten.client.mvp.AppActivityMapper;
import net.rugby.foundation.topten.client.mvp.AppPlaceHistoryMapper;
import net.rugby.foundation.topten.client.place.SeriesPlace;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class TopTen implements EntryPoint {

	private SimplePanel appWidget = new SimplePanel();
	private final Place defaultPlace = new SeriesPlace("TX:c=1");
	private ClientFactory clientFactory = null;

	@SuppressWarnings("deprecation")
	public void onModuleLoad() {



		// Create ClientFactory using deferred binding so we can replace with 
		// different impls in gwt.xml
		clientFactory = GWT.create(ClientFactory.class);

		final EventBus eventBus = clientFactory.getEventBus();
		final PlaceController placeController = clientFactory.getPlaceController();
		// Start ActivityManager for the main widget with our ActivityMapper
		ActivityMapper activityMapper = new AppActivityMapper(clientFactory);
		ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
		activityManager.setDisplay(appWidget);
		// Start PlaceHistoryHandler with our PlaceHistoryMapper
		AppPlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
		final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
		// Get rid of the dynamically generated page as quick as we can, it just causes problems when the hash fragment changes.
		String guid = clientFactory.getPlaceFromURL();
		if (guid != null && !guid.isEmpty()) {
			//			UrlBuilder builder = Location.createUrlBuilder().removeParameter("listId").removeParameter("compId").removeParameter("playerId").setPath("topten.html");
			//			Window.Location.replace(builder.buildString());
			clientFactory.getRpcService().getPlace(guid, new AsyncCallback<IServerPlace>() {

				@Override
				public void onFailure(Throwable caught) {
					historyHandler.register(placeController, eventBus, defaultPlace);					
				}

				@Override
				public void onSuccess(IServerPlace result) {
					SeriesPlace sp = new SeriesPlace(result.getCompId(), result.getSeriesId(), result.getGroupId(), result.getMatrixId(), result.getQueryId(), result.getItemId());
					historyHandler.register(placeController, eventBus, sp);	
					historyHandler.handleCurrentHistory();
				}

			});
		} else {
			historyHandler.register(placeController, eventBus, defaultPlace);
			historyHandler.handleCurrentHistory();
		}


		RootPanel.get("app").add(appWidget);

		//Logger.getLogger("").log(Level.FINE, this.getClass().toString() + "Creating Identity manager");
	}
}

