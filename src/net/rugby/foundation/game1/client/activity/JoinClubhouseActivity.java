/**
 * 
 */
package net.rugby.foundation.game1.client.activity;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.game1.client.ClientFactory;
import net.rugby.foundation.game1.client.place.Game1Place;
import net.rugby.foundation.game1.client.place.JoinClubhouse;
import net.rugby.foundation.game1.client.ui.Game1View;
import net.rugby.foundation.game1.client.ui.HomeView;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * @author home
 *
 */
public class JoinClubhouseActivity extends AbstractActivity implements Game1View.JoinClubhousePresenter, HomeView.JoinClubhousePresenter {

	private JoinClubhouse place;
	private ClientFactory clientFactory;
	private ICoreConfiguration coreConfig;
	
	public JoinClubhouseActivity(JoinClubhouse place, ClientFactory clientFactory) {
		this.place = place;
		this.clientFactory = clientFactory;
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.activity.shared.Activity#start(com.google.gwt.user.client.ui.AcceptsOneWidget, com.google.gwt.event.shared.EventBus)
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		clientFactory.setJoiningClubhouse(true, place.getClubhouseId());
		
		final Game1View view = clientFactory.getGame1View();
		view.setJoinPresenter(this);
		
		view.clear(true);
		
		// are they logged in yet?
		// get the core configuration, competition and entries
		Core.getInstance().getConfiguration(new AsyncCallback<ICoreConfiguration>() {

			@Override
			public void onFailure(Throwable caught) {
				Logger.getLogger("JoinClubhouseActivity").log(Level.SEVERE, "start.getConfiguration: " + caught.getMessage());
			}

			@Override
			public void onSuccess(final ICoreConfiguration config) {
				coreConfig = config;
				view.setComps(coreConfig.getCompetitionMap());
				
				// figure out a competition to work with
				Long compId = null;
				
				// save off our current place in case the user does account actions
				String url = "JoinClubhouse:"; 
				JoinClubhouse.Tokenizer tokenizer = new JoinClubhouse.Tokenizer();
				Core.getCore().getClientFactory().getIdentityManager().setDestination(url+tokenizer.getToken(place));

				// is anyone logged in?
				if (Core.getCore().getClientFactory().getLoginInfo().isLoggedIn()) {

					// go to the user's last competition if there is one or the site default if not
					compId = clientFactory.getCoreClientFactory().getLoginInfo().getLastCompetitionId();
					if (compId == null)
						compId = coreConfig.getDefaultCompId();
				} else {
					compId = coreConfig.getDefaultCompId();
				}
				
				Core.getCore().setCurrentCompId(compId);					
				Core.getCore().getComp(compId, new AsyncCallback<ICompetition>() {

					@Override
					public void onFailure(Throwable caught) {
						Logger.getLogger("JoinClubhouseActivity").log(Level.SEVERE,"start.getComp: " +  caught.getMessage());						
					}

					@Override
					public void onSuccess(final ICompetition comp) {
						clientFactory.getGame1View().getHomeView().showCreateClubhouse(false);						
						assert(Core.getCore().isInitialized());
						// if no-one is logged on we are done
						if (Core.getCore().getClientFactory().getLoginInfo().isLoggedIn()) {
							// if they are logged in, add them to the clubhouse
							Core.getCore().joinClubhouse(clientFactory.getClubhouseToJoinId(), new AsyncCallback<IClubhouse>() {

								@Override
								public void onFailure(Throwable caught) {
									Logger.getLogger("JoinClubhouseActivity").log(Level.SEVERE,"start.joinClubhouse: " +  caught.getMessage());
									
								}

								@Override
								public void onSuccess(IClubhouse result) {
									Core.getCore().getClientFactory().getLoginInfo().setLastClubhouseId(result.getId());
									clientFactory.setJoiningClubhouse(false, 0L);

									clientFactory.getPlaceController().goTo(new Game1Place(clientFactory.getCoreClientFactory().getLoginInfo()));

									
								}
								
							});
						} else {
							view.selectTab(0);
							view.clear(true);
						}
					}
				});
			}
		});		
		

		
		containerWidget.setWidget(view.asWidget());

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.HomeView.JoinClubhousePresenter#getClientFactory()
	 */
	@Override
	public ClientFactory getClientFactory() {
		return clientFactory;
	}
	
//	/* (non-Javadoc)
//	 * @see com.google.gwt.activity.shared.Activity#mayStop()
//	 */
//	@Override
//	public String mayStop() {
//		// TODO Auto-generated method stub
//		Window.alert("leaving");
//		return null;
//	}

}
