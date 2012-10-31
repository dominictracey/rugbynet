package net.rugby.foundation.game1.client.mvp;

import net.rugby.foundation.game1.client.ClientFactory;
import net.rugby.foundation.game1.client.activity.Game1Activity;
import net.rugby.foundation.game1.client.activity.JoinClubhouseActivity;
import net.rugby.foundation.game1.client.activity.ProfileActivity;
import net.rugby.foundation.game1.client.place.Game1Place;
import net.rugby.foundation.game1.client.place.JoinClubhouse;
import net.rugby.foundation.game1.client.place.Profile;

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
		this.clientFactory = clientFactory;
	}

	@Override
	public Activity getActivity(Place place) {
	  
		if (place instanceof Game1Place)
			return new Game1Activity((Game1Place) place, clientFactory);
		else if (place instanceof JoinClubhouse)
			return new JoinClubhouseActivity((JoinClubhouse) place, clientFactory);
		else if (place instanceof Profile)
			return new ProfileActivity((Profile) place, clientFactory);

		return null;
	}

}
