/**
 * 
 */
package net.rugby.foundation.topten.client.activity;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.core.client.Identity;
import net.rugby.foundation.core.client.Identity.Presenter;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.place.FeatureListPlace;
import net.rugby.foundation.topten.client.place.Profile;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * @author home
 *
 */
public class ProfileActivity implements Activity, Presenter {

	private Profile place;
	private ClientFactory clientFactory;
	private ICoreConfiguration _coreConfig;
	/**
	 * @param place
	 * @param clientFactory
	 */
	public ProfileActivity(Profile place, ClientFactory clientFactory) {
		this.place = place;
		this.clientFactory = clientFactory;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.activity.shared.Activity#mayStop()
	 */
	@Override
	public String mayStop() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.activity.shared.Activity#onCancel()
	 */
	@Override
	public void onCancel() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.activity.shared.Activity#onStop()
	 */
	@Override
	public void onStop() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.activity.shared.Activity#start(com.google.gwt.user.client.ui.AcceptsOneWidget, com.google.gwt.event.shared.EventBus)
	 */
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		clientFactory.RegisterIdentityPresenter(this);
		
		clientFactory.doSetup(new AsyncCallback<ICoreConfiguration>() {


			

			@Override
			public void onFailure(Throwable caught) {
				// suffer in silence
			}

			@Override
			public void onSuccess(final ICoreConfiguration coreConfig) {
				//clientFactory.console("FeatureActivity.start doSetup complete with place " + place.getToken());
				_coreConfig = coreConfig;
				Core.getCore().getClientFactory().getIdentityManager().setPresenter((Identity.Presenter)clientFactory);
				Core.getCore().getClientFactory().getIdentityManager().setDestination(place.getDestination());
				Core.getCore().getClientFactory().getIdentityManager().startProfileProcess(place.getAction(),place.getProviderType(),place.getSelector());
			}
		});
	}

	@Override
	public void onLoginComplete(String destination) {
		FeatureListPlace home = new FeatureListPlace();
		home.setCompId(_coreConfig.getDefaultCompId());
		clientFactory.getPlaceController().goTo(home);
	}

}
