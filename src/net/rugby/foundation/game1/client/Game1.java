package net.rugby.foundation.game1.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.core.client.Identity;
import net.rugby.foundation.core.client.Identity.Presenter;
import net.rugby.foundation.game1.client.mvp.AppActivityMapper;
import net.rugby.foundation.game1.client.mvp.AppPlaceHistoryMapper;
import net.rugby.foundation.game1.client.ClientFactory;
import net.rugby.foundation.game1.client.place.Game1Place;
import net.rugby.foundation.game1.client.place.JoinClubhouse;
import net.rugby.foundation.model.shared.LoginInfo;


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

public class Game1 implements EntryPoint, Presenter {
  
	private SimplePanel appWidget = new SimplePanel();
	private Place defaultPlace = new Game1Place(0L,0L,0L, 0L);
	private ClientFactory clientFactory = null;
	
	@SuppressWarnings("deprecation")
	public void onModuleLoad() {
		// Create ClientFactory using deferred binding so we can replace with 
		// different impls in gwt.xml
		clientFactory = GWT.create(ClientFactory.class);
		EventBus eventBus = clientFactory.getEventBus();
		PlaceController placeController = clientFactory.getPlaceController();
		// Start ActivityManager for the main widget with our ActivityMapper
		ActivityMapper activityMapper = new AppActivityMapper(clientFactory);
		ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
		RootPanel.get("playArea").add(appWidget);
		
		activityManager.setDisplay(appWidget);
		// Start PlaceHistoryHandler with our PlaceHistoryMapper
		AppPlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
		final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
		historyHandler.register(placeController, eventBus, defaultPlace);
		Logger.getLogger("").log(Level.FINE, this.getClass().toString() + "Creating Identity manager");
		final Identity i = clientFactory.getCoreClientFactory().getIdentityManager();		
		// where we keep the sign in/sign out
		HorizontalPanel acct = new HorizontalPanel();

		i.setParent(acct);
		i.setPresenter(this);
		RootPanel.get("loginPanel").add(acct);
		Logger.getLogger("").log(Level.FINE, this.getClass().toString() + "Ready to call login");
		Core.getCore().login(new AsyncCallback<LoginInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				//dealt with in the Core
			}

			@Override
			public void onSuccess(LoginInfo result) {

				//TODO take out for upstream environments
//				if (true)
					i.doLoginDev("a16@test.com", "asdasd");
//				else  // this is kicked off at the end of the login process as well, so when we auto-login the test user we don't need to do it.
				Logger.getLogger("").log(Level.FINE, this.getClass().toString() + "login sucessful, ready to handleHistory");
				historyHandler.handleCurrentHistory();							
			}
			
		});				


	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.Identity.Presenter#onLoginComplete()
	 */
	@Override
	public void onLoginComplete(String destination) {

		Logger.getLogger("").log(Level.FINE, this.getClass().toString() + "onLoginComplete callback with destination: " + destination);

		if (Core.getCore().getClientFactory().getLoginInfo().isAdmin()) {
			clientFactory.getGame1View().addAdminTab();
		}
		
		if (destination != null) {
			// we can pass in everthing after the colon into the constructors for the Places.
			// so we get JoinClubhouse:42350 and we just pass in the 42350 to the JoinClubhouse constructor
			String[] tok = destination.split(":");
			if (tok.length == 2) {
				if (destination.contains("Game1Place"))
					clientFactory.getPlaceController().goTo(new Game1Place(tok[1]));
				else if (destination.contains("JoinClubhouse")) {
					clientFactory.getPlaceController().goTo(new JoinClubhouse(tok[1]));
				} 
			} else {
				clientFactory.getPlaceController().goTo(defaultPlace);
			}
		} else {
			clientFactory.getPlaceController().goTo(defaultPlace);
		}
		

//		
//		if (clientFactory.getPostCoreDestination() != null) {
//			clientFactory.getPlaceController().goTo(clientFactory.getPostCoreDestination());
//			clientFactory.setPostCoreDestination(null);
//		} else {
//			clientFactory.getPlaceController().goTo(defaultPlace);
//		}
		
//		if (clientFactory.isJoiningClubhouse())
//			clientFactory.getPlaceController().goTo(new JoinClubhouse(clientFactory.getClubhouseToJoinId().toString()));
//		else {
//			clientFactory.getPlaceController().goTo(new Game1Place( clientFactory.getCoreClientFactory().getLoginInfo()));
//			if (Core.getCore().getClientFactory().getLoginInfo().isAdmin()) {
//				clientFactory.getGame1View().addAdminTab();
//			}
//		}
		
	}
		
}
