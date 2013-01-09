package net.rugby.foundation.game1.client;

import net.rugby.foundation.core.client.CoreClientFactory;
import net.rugby.foundation.game1.client.ui.Game1View;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;

/**
 * ClientFactory helpful to use a factory or dependency injection framework like GIN to obtain 
 * references to objects needed throughout your application like the {@link EventBus},
 * {@link PlaceController} and views.
 */
public interface ClientFactory {

	EventBus getEventBus();

	PlaceController getPlaceController();

	/**
	 * @return Access to the clientFactory for shared services such as identity management, competition data, etc.
	 */
	CoreClientFactory getCoreClientFactory();

	/**
	 * @return
	 */
	public Game1View getGame1View();

	/**
	 * @return
	 */
	public Game1ServiceAsync getRpcservice();
	
	/**
	 * @return whether the user has come in via a "Join Clubhouse" link that has not yet been fulfilled
	 */
	public Boolean isJoiningClubhouse();
	
	public void setJoiningClubhouse(Boolean joining, Long clubhouseToJoinId);

	/**
	 * @return id from the URL the user came in the door with
	 */
	Long getClubhouseToJoinId();

//	/**
//	 * @return
//	 */
//	Place getPostCoreDestination();
//
//	/**
//	 * @param postCoreDestination
//	 */
//	void setPostCoreDestination(Place postCoreDestination);
}
