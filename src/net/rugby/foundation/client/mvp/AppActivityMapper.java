package net.rugby.foundation.client.mvp;

import net.rugby.foundation.client.ClientFactory;
import net.rugby.foundation.client.activity.EditPlayerActivity;
import net.rugby.foundation.client.activity.PlayerListActivity;
import net.rugby.foundation.client.place.Details;
import net.rugby.foundation.client.place.Browse;
import net.rugby.foundation.client.place.Home;
import net.rugby.foundation.client.place.Manage;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class AppActivityMapper implements ActivityMapper {

	private ClientFactory clientFactory;

	/**
	 * AppActivityMapper associates each Place with its corresponding
	 * {@link Activity}
	 * 
	 * @param clientFactory
	 *            Factory to be passed to activities
	 */
	public AppActivityMapper(ClientFactory clientFactory) {
		super();
		this.clientFactory = clientFactory;
	}

	/**
	 * Map each Place to its corresponding Activity. This would be a great use
	 * for GIN.
	 */
	@Override
	public Activity getActivity(Place place) {
		// This is begging for GIN
		if (place instanceof Browse)
			return new PlayerListActivity((Browse) place, clientFactory);
		if (place instanceof Details)
			return new EditPlayerActivity((Details) place, clientFactory);
		if (place instanceof Manage)
			return new PlayerListActivity((Manage) place, clientFactory);
		if (place instanceof Home)
			return new PlayerListActivity((Home) place, clientFactory);

		return null;
	}

}
