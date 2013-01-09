/**
 * 
 */
package net.rugby.foundation.game1.client.activity;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.game1.client.ClientFactory;
import net.rugby.foundation.game1.client.place.Profile;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * @author home
 *
 */
public class ProfileActivity implements Activity {

	private Profile place;
	private ClientFactory clientFactory;

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
		
		Core.getCore().getClientFactory().getIdentityManager().setDestination(place.getDestination());
		Core.getCore().getClientFactory().getIdentityManager().startProfileProcess(place.getAction(),place.getProviderType(),place.getSelector());
	}

}
