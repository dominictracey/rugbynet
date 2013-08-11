package net.rugby.foundation.topten.client.mvp;

import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.activity.TopTenListActivity;
import net.rugby.foundation.topten.client.place.TopTenListPlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

/**
 * ActivityMapper associates each {@link Place} with its corresponding {@link Activity}.
 */
public class AppActivityMapper implements ActivityMapper {

	/**
	 * Provided for {@link Activitie}s.
	 */
	private ClientFactory clientFactory;

	public AppActivityMapper(ClientFactory clientFactory) {
		super();
		this.clientFactory = clientFactory;
	}

	@Override
	public Activity getActivity(Place place) {
	  
		if (place instanceof TopTenListPlace)
			return new TopTenListActivity((TopTenListPlace) place, clientFactory);

		return null;
	}

}
