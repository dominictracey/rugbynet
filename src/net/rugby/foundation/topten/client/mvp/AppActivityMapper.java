package net.rugby.foundation.topten.client.mvp;

import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.activity.ContentActivity;
import net.rugby.foundation.topten.client.activity.FeatureListActivity;
import net.rugby.foundation.topten.client.activity.SeriesActivity;
import net.rugby.foundation.topten.client.place.ContentPlace;
import net.rugby.foundation.topten.client.place.FeatureListPlace;
import net.rugby.foundation.topten.client.place.SeriesPlace;
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
	  
		if (place instanceof FeatureListPlace)
			return new FeatureListActivity((FeatureListPlace) place, clientFactory);
		if (place instanceof ContentPlace)
			return new ContentActivity((ContentPlace) place, clientFactory);
		if (place instanceof SeriesPlace)
			return new SeriesActivity((SeriesPlace) place, clientFactory);
		return null;
	}

}
