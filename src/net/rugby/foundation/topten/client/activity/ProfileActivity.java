/**
 * 
 */
package net.rugby.foundation.topten.client.activity;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.core.client.Identity;
import net.rugby.foundation.core.client.Identity.Presenter;
import net.rugby.foundation.core.shared.IdentityTypes.Actions;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.place.ContentPlace;
import net.rugby.foundation.topten.client.place.FeatureListPlace;
import net.rugby.foundation.topten.client.place.Profile;
import net.rugby.foundation.topten.client.place.SeriesPlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
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
				Identity identity = Core.getCore().getClientFactory().getIdentityManager();
				identity.setPresenter((Identity.Presenter)clientFactory);
				identity.setDestination(place.getDestination());
				if (place.getAction() == Actions.updateScreenName || place.getAction() == Actions.done) {
					identity.startProfileProcess(place.getAction(),place.getProviderType(),place.getSelector());
				} else if (place.getAction() == Actions.validateEmail) {
					identity.doValidateEmail(place.getEmail(),place.getValidationCode());
				} else if (place.getAction() == Actions.changePassword) {
					identity.handlePasswordReset(place.getEmail(),place.getTemporaryPassword(), place.getDestination());
				}
					
			}
		});
	}

	@Override
	public void onLoginComplete(String destination) {
		Place place = null;
		String tokens = URL.decodePathSegment(destination);
		if (tokens.startsWith("Fx")) {
			place = new FeatureListPlace(tokens.substring(3));
		} else if (tokens.startsWith("Tx")) {
			place = new SeriesPlace(tokens.substring(3));
		} else if (tokens.startsWith("Content")) {
			place = new ContentPlace(tokens.substring(8));
		} else if (tokens.startsWith("Profile")) {
			place = new Profile(tokens.substring(8));
		} else {
			place = new FeatureListPlace();
			((FeatureListPlace)place).setCompId(_coreConfig.getDefaultCompId());
		}
		clientFactory.getPlaceController().goTo(place);

	}


	@Override
	public void showFacebookComments(boolean show) {
		clientFactory.showFacebookComments(show);
		
	}
}
