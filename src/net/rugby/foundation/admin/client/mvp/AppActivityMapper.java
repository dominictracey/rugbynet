package net.rugby.foundation.admin.client.mvp;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.activity.CompActivity;
import net.rugby.foundation.admin.client.activity.OrchestrationConfigurationActivity;
import net.rugby.foundation.admin.client.activity.PortalActivity;
import net.rugby.foundation.admin.client.activity.SeriesActivity;
import net.rugby.foundation.admin.client.activity.TaskActivity;
import net.rugby.foundation.admin.client.place.AdminCompPlace;
import net.rugby.foundation.admin.client.place.AdminOrchPlace;
import net.rugby.foundation.admin.client.place.AdminTaskPlace;
import net.rugby.foundation.admin.client.place.PortalPlace;
import net.rugby.foundation.admin.client.place.SeriesPlace;

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
	  
		if (place instanceof AdminCompPlace)
			return new CompActivity((AdminCompPlace) place, clientFactory);
		if (place instanceof AdminTaskPlace)
			return new TaskActivity((AdminTaskPlace) place, clientFactory);
		if (place instanceof AdminOrchPlace)
			return new OrchestrationConfigurationActivity((AdminOrchPlace) place, clientFactory);
		if (place instanceof PortalPlace)
			return new PortalActivity((PortalPlace) place, clientFactory);
		if (place instanceof SeriesPlace)
			return new SeriesActivity((SeriesPlace) place, clientFactory);

		return null;
	}

}
