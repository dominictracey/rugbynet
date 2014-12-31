package net.rugby.foundation.topten.client.activity;

import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.place.FeatureListPlace;
import net.rugby.foundation.topten.client.place.LegacyListPlace;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;


public class LegacyListActivity extends AbstractActivity {

	private ClientFactory clientFactory;
	private LegacyListPlace place;

	public LegacyListActivity(LegacyListPlace place, ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		this.place = place;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		// we just want to create a feature out of this list
		FeatureListPlace newPlace = new FeatureListPlace();
		
		newPlace.setListId(place.getListId());
		newPlace.setCompId(place.getCompId());
		
		clientFactory.getPlaceController().goTo(newPlace);
		
	}

}
