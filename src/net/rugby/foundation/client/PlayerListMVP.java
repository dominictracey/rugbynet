package net.rugby.foundation.client;

import org.cobogw.gwt.user.client.CSS;
import org.cobogw.gwt.user.client.Color;
import org.cobogw.gwt.user.client.ui.RoundedLinePanel;
import org.cobogw.gwt.user.client.ui.RoundedPanel;

import net.rugby.foundation.client.ClientFactory.SetHomeAsyncCallback;
import net.rugby.foundation.client.Identity.checkLoginStatusCallback;
import net.rugby.foundation.client.event.DoneNavPaintEvent;
import net.rugby.foundation.client.event.DoneNavPaintEventHandler;
import net.rugby.foundation.client.mvp.AppActivityMapper;
import net.rugby.foundation.client.mvp.AppPlaceHistoryMapper;
import net.rugby.foundation.client.mvp.DetailsActivityMapper;
import net.rugby.foundation.client.place.Home;
import net.rugby.foundation.client.place.Home.actions;
import net.rugby.foundation.client.ui.BreadcrumbsTrail;
import net.rugby.foundation.client.ui.Footer;
import net.rugby.foundation.client.ui.Sponsors;
import net.rugby.foundation.model.shared.Group.GroupType;
import net.rugby.foundation.model.shared.LoginInfo;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PlayerListMVP  implements EntryPoint
{
	private Place defaultPlace =  null;
	private RootPanel topLevel = null;//new FlowPanel();
	private FlowPanel mainContent = new FlowPanel();
	private VerticalPanel appWidget = new VerticalPanel();
	private SimplePanel listWidget = new SimplePanel();
	private SimplePanel detailsWidget = new SimplePanel();
	private BreadcrumbsTrail breadcrumbs = new BreadcrumbsTrail();
	private RootPanel header = null; //new SimplePanel();
	private SimplePanel footer = new SimplePanel();
	private SimplePanel ads = new SimplePanel();
	private ClientFactory clientFactory;
	private PlaceHistoryHandler historyHandler = null;
//	private HandlerRegistration reg;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{
		// Create ClientFactory using deferred binding so we can replace with different
		// impls in gwt.xml
		clientFactory = GWT.create(ClientFactory.class);
		final EventBus eventBus = clientFactory.getEventBus();
		final PlaceController placeController = clientFactory.getPlaceController();

		// Start ActivityManager for the list widget with our ActivityMapper
		ActivityMapper listActivityMapper = new AppActivityMapper(clientFactory);
		ActivityManager listActivityManager = new ActivityManager(listActivityMapper, eventBus);
		listActivityManager.setDisplay(listWidget);

		// Start ActivityManager for the list widget with our ActivityMapper
		ActivityMapper detailsActivityMapper = new DetailsActivityMapper(clientFactory);
		ActivityManager detailsActivityManager = new ActivityManager(detailsActivityMapper, eventBus);
		detailsActivityManager.setDisplay(detailsWidget);
		
		// Set the default competition (home)
		clientFactory.setHomeAsync( new SetHomeAsyncCallback() {

			@Override
			public void onSetHomeComplete(Long id) {
				// Start PlaceHistoryHandler with our PlaceHistoryMapper
				AppPlaceHistoryMapper historyMapper= GWT.create(AppPlaceHistoryMapper.class);
				historyHandler = new PlaceHistoryHandler(historyMapper);
				defaultPlace = new Home(id,GroupType.NONE,0L,0L,actions.NONE);
				historyHandler.register(placeController, eventBus, defaultPlace);
						
				// wait on the stackpanel to render before we can... do all the rest of the screen paints.
				eventBus.addHandler(DoneNavPaintEvent.TYPE,	doneNavPaintCallback);
				
				Identity i = clientFactory.getIdentityManager();
				i.setClientFactory(clientFactory);
				
				// where we keep the sign in/sign out
				HorizontalPanel acct = new HorizontalPanel();
				i.setParent(acct);
				header = RootPanel.get("header");
				header.add(acct);
				topLevel = RootPanel.get("topLevel");

				clientFactory.getIdentityManager().checkLoginStatusAsync(new checkLoginStatusCallback() {

					@Override
					public void onLoginStatusChecked(LoginInfo loginInfo) {
						clientFactory.getGroupBrowser().Init(clientFactory, mainContent);
						
					}
					
				});				
			}
			
		});


	}


	private DoneNavPaintEventHandler doneNavPaintCallback = new DoneNavPaintEventHandler() {
		
		@Override
		public void onDoneNavPaint(DoneNavPaintEvent event) {
			//have to wait to add the content widget last
			

			
			//nav = event.getNavPanel();

			//Label f = new Label("FOOTER");	
			//Label a = new Label("ADS");
			Footer foot = clientFactory.getFooter();

			footer.add(foot);

			DOM.setElementAttribute(footer.getElement(), "id", "footer");
			Sponsors adContent = new Sponsors();
			ads.add(adContent);
			DOM.setElementAttribute(ads.getElement(), "id", "ads");

		
			breadcrumbs.Init(clientFactory);
			appWidget.add(breadcrumbs);
			appWidget.add(detailsWidget);
			appWidget.add(listWidget);
			DOM.setElementAttribute(mainContent.getElement(), "id", "mainContent");
			DOM.setElementAttribute(appWidget.getElement(), "id", "appWidget");
			DOM.setElementAttribute(detailsWidget.getElement(), "id", "detailsWidget");
			DOM.setElementAttribute(listWidget.getElement(), "id", "listWidget");
			DOM.setElementAttribute(breadcrumbs.getElement(), "id", "breadCrumbs");

//			mainContent.setElement(DOM.getElementById("mainContent"));
			mainContent.add(appWidget);	
			mainContent.add(ads);
			mainContent.add(footer);
			TabPanel p = new TabPanel();
			FlowPanel f = new FlowPanel();
			Label l1 = new Label("this content is really awesome");
			
			RoundedLinePanel p1 = new RoundedLinePanel(l1, RoundedPanel.TOP);
			p1.setCornerColor(Color.GREY, Color.GREY);
			CSS.setProperty(p1.getWidget(), CSS.A.BACKGROUND, "url(\"resources/images/tab_header_back.png\") repeat-x scroll right top #2222E9");
			CSS.setProperty(p1.getWidget(), CSS.A.WIDTH, "200px");
			CSS.setProperty(p1.getWidget(), CSS.A.HEIGHT, "12px");
			CSS.setProperty(p1.getWidget(), CSS.A.FONT_WEIGHT, "bold");
//			p1.setCornerColor("grey");
//			p1.setBorderColor("grey");
			//p1.setStyleName("blueBG");
			//HTML one = ;
			RoundedLinePanel p2 = new RoundedLinePanel(new Label("Among the 25 Pine Bluff players sitting out are leading receiver Desmond Beverly, No. 2 tackler and sack leader Joe Dalton and No. 2 rusher Stephen Jones. Beverly and Jones are out for two games. Jones is leading the team in scoring with nine touchdowns. No. 4 tackler Ryan Shaw is also suspended."), RoundedPanel.BOTTOM);

			CSS.setProperty(p2.getWidget(), CSS.A.BACKGROUND_COLOR, "#FFFFFF");
			CSS.setProperty(p2.getWidget(), CSS.A.WIDTH, "200px");
			
			p2.setCornerColor(Color.GREY, Color.WHITE);
			//p2.setBorderColor(Color.WHITE);
			//p2.setStyleName("blueBG");
			//HTML two = ;
//			p1.add(one);
//			p2.add(two);
			f.add(p1);
			f.add(p2);
			p.add(f, "Home");
			p.add(mainContent, "Fantasy");

			
			topLevel.add(p);

			//DOM.setElementAttribute(topLevel.getElement(), "id", "toplevel");
			//RootPanel.get().add(topLevel);

			// Goes to place represented on URL or default place
			historyHandler.handleCurrentHistory();
			//clientFactory.getIdentityManager().setSilent(false);
		}
		
	};

}
