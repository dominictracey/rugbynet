package net.rugby.foundation.topten.client;


import net.rugby.foundation.model.shared.IServerPlace;
import net.rugby.foundation.model.shared.IServerPlace.PlaceType;
import net.rugby.foundation.topten.client.mvp.AppActivityMapper;
import net.rugby.foundation.topten.client.mvp.AppPlaceHistoryMapper;
import net.rugby.foundation.topten.client.place.FeatureListPlace;
import net.rugby.foundation.topten.client.place.SeriesPlace;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class TopTen implements EntryPoint {

	private SimplePanel appWidget = new SimplePanel();
	private final Place defaultPlace = new FeatureListPlace();
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
		String guid = clientFactory.getPlaceFromURL();

		clientFactory.getRpcService().getPlace(guid, new AsyncCallback<IServerPlace>() {
			@Override
			public void onFailure(Throwable caught) {
				historyHandler.register(placeController, eventBus, defaultPlace);					
			}

			@Override
			public void onSuccess(IServerPlace result) {
				clientFactory.console("getPlace.onSuccess " + result.toString());
				Place sp = null;
				if (result.getType().equals(PlaceType.SERIES)) {
					sp = new SeriesPlace(result.getCompId(), result.getSeriesId(), result.getGroupId(), result.getMatrixId(), result.getQueryId(), result.getItemId());
				} else {
					sp = new FeatureListPlace(result.getCompId(), result.getListId(), result.getItemId());
				}
				historyHandler.register(placeController, eventBus, sp);	
				historyHandler.handleCurrentHistory();
			}

		});



		RootPanel.get("app").add(appWidget);

		//Logger.getLogger("").log(Level.FINE, this.getClass().toString() + "Creating Identity manager");
	}
}

