package net.rugby.foundation.topten.client;


import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.core.client.Identity;
import net.rugby.foundation.core.client.Identity.Presenter;
import net.rugby.foundation.game1.client.place.Game1Place;
import net.rugby.foundation.game1.client.place.JoinClubhouse;
import net.rugby.foundation.model.shared.LoginInfo;
import net.rugby.foundation.topten.client.mvp.AppActivityMapper;
import net.rugby.foundation.topten.client.mvp.AppPlaceHistoryMapper;
import net.rugby.foundation.topten.client.place.TopTenListPlace;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class TopTen implements EntryPoint {
  
	private SimplePanel appWidget = new SimplePanel();
	private Place defaultPlace = new TopTenListPlace("List:");
	private ClientFactory clientFactory = null;
	final Identity i = Core.getCore().getClientFactory().getIdentityManager();		

	public void onModuleLoad() {
		
		// Create ClientFactory using deferred binding so we can replace with 
		// different impls in gwt.xml
		clientFactory = GWT.create(ClientFactory.class);
		EventBus eventBus = clientFactory.getEventBus();
		PlaceController placeController = clientFactory.getPlaceController();
		// Start ActivityManager for the main widget with our ActivityMapper
		ActivityMapper activityMapper = new AppActivityMapper(clientFactory);
		ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
		activityManager.setDisplay(appWidget);
		// Start PlaceHistoryHandler with our PlaceHistoryMapper
		AppPlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
		final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
		historyHandler.register(placeController, eventBus, defaultPlace);
		historyHandler.handleCurrentHistory();	
		RootPanel.get("app").add(appWidget);

		Logger.getLogger("").log(Level.FINE, this.getClass().toString() + "Creating Identity manager");
		


	}



}
