package net.rugby.foundation.topten.client;


import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.core.client.Identity;
import net.rugby.foundation.topten.client.mvp.AppActivityMapper;
import net.rugby.foundation.topten.client.mvp.AppPlaceHistoryMapper;
import net.rugby.foundation.topten.client.place.TopTenListPlace;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class TopTen implements EntryPoint {
  
	private SimplePanel appWidget = new SimplePanel();
	private Place defaultPlace = new TopTenListPlace("List:");
	private ClientFactory clientFactory = null;
	//final Identity i = Core.getCore().getClientFactory().getIdentityManager();		

	@SuppressWarnings("deprecation")
	public void onModuleLoad() {
		

		
		// Create ClientFactory using deferred binding so we can replace with 
		// different impls in gwt.xml
		clientFactory = GWT.create(ClientFactory.class);
		
		// Get rid of the dynamically generated page as quick as we can, it just causes problems when the hash fragment changes.
		if (clientFactory.isDualParamString()) {
			UrlBuilder builder = Location.createUrlBuilder().removeParameter("listId").removeParameter("compId").removeParameter("playerId").setPath("topten.html");
			Window.Location.replace(builder.buildString());
			return;
		}
				
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

		DOM.getElementById("loadProgress").getStyle().setWidth(80, Unit.PCT);
		
		Logger.getLogger("").log(Level.FINE, this.getClass().toString() + "Creating Identity manager");
		


	}



}
