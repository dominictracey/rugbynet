package net.rugby.foundation.admin.client;

import java.util.Arrays;
import java.util.List;

import net.rugby.foundation.admin.client.mvp.AppActivityMapper;
import net.rugby.foundation.admin.client.mvp.AppPlaceHistoryMapper;
import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.place.AdminCompPlace;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class Admin implements EntryPoint {
  
	private SimplePanel appWidget = new SimplePanel();
	private Place defaultPlace = new AdminCompPlace("1");
	
	public void onModuleLoad() {
		
//		ScriptInjector.fromUrl("theme/js/notify/jquery-ui.js").setCallback(
//				  new Callback<Void, Exception>() {
//				     public void onFailure(Exception reason) {
//				       Window.alert("Script load failed.");
//				     }
//				    public void onSuccess(Void result) {
//				      Window.alert("Script load success.");
//				     }
//				  }).inject();
		
		// Create ClientFactory using deferred binding so we can replace with 
		// different impls in gwt.xml
		ClientFactory clientFactory = GWT.create(ClientFactory.class);
		EventBus eventBus = clientFactory.getEventBus();
		PlaceController placeController = clientFactory.getPlaceController();
		// Start ActivityManager for the main widget with our ActivityMapper
		ActivityMapper activityMapper = new AppActivityMapper(clientFactory);
		ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
		activityManager.setDisplay(appWidget);
		// Start PlaceHistoryHandler with our PlaceHistoryMapper
		AppPlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
		PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
		historyHandler.register(placeController, eventBus, defaultPlace);

		RootPanel.get().add(appWidget);
		// Goes to place represented on URL or default place
		historyHandler.handleCurrentHistory();
		   loadScripts();
	  }
	  private void loadScripts() {
	    // ScriptInjector.fromString("public/js/jquery-ui-1.10.4.custom.min.js").inject();
	    // ScriptInjector.fromString("public/js/nprogress.js").inject();
	    List<String> scripts = Arrays.asList( //"//cdnjs.cloudflare.com/ajax/libs/jquery/1.11.0/jquery.js",
	                                          "//cdnjs.cloudflare.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js",
	                                          "//cdnjs.cloudflare.com/ajax/libs/nprogress/0.1.2/nprogress.min.js");
	    injectScriptsInOrder(scripts);
	  }

	  private void injectScriptsInOrder(final List<String> scripts) {
	    if (scripts.size() > 0) {
	      ScriptInjector.fromUrl(scripts.get(0))
	      .setRemoveTag(false)
	      .setWindow(ScriptInjector.TOP_WINDOW)
	      .setCallback(new Callback<Void, Exception>() {

	        @Override
	        public void onFailure(Exception reason) {
	          GWT.log("The script " + scripts.get(0) + " did not install correctly");
	        }

	        @Override
	        public void onSuccess(Void result) {
	          GWT.log("The script " + scripts.get(0) + " installed correctly");
	          injectScriptsInOrder(scripts.subList(1, scripts.size()));
	        }
	      }).inject();
	    }
	  }
}
