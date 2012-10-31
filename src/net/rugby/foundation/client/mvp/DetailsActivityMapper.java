package net.rugby.foundation.client.mvp;

import net.rugby.foundation.client.ClientFactory;
import net.rugby.foundation.client.activity.HomeActivity;
import net.rugby.foundation.client.activity.ManageActivity;
import net.rugby.foundation.client.place.Home;
import net.rugby.foundation.client.place.Manage;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class DetailsActivityMapper implements ActivityMapper {

	private ClientFactory clientFactory;

	/**
	 * AppActivityMapper associates each Place with its corresponding
	 * {@link Activity}
	 * 
	 * @param clientFactory
	 *            Factory to be passed to activities
	 */
	public DetailsActivityMapper(ClientFactory clientFactory) {
		super();
		this.clientFactory = clientFactory;
	}

	/**
	 * Map each Place to its corresponding Activity. This would be a great use
	 * for GIN.
	 */
	@Override
	public Activity getActivity(Place place) {
		if (place instanceof Home)
			return new HomeActivity((Home) place, clientFactory);
		if (place instanceof Manage)
			return new ManageActivity((Manage) place, clientFactory);


		return null;
	}

}
