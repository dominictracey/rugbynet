package net.rugby.foundation.game1.client;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.core.client.CoreClientFactory;
import net.rugby.foundation.game1.client.ui.Game1View;
import net.rugby.foundation.game1.client.ui.Game1ViewImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;

/**
 * Sample implementation of {@link ClientFactory}.
 */
public class ClientFactoryImpl implements ClientFactory {
  
	private static final EventBus eventBus = new SimpleEventBus();
	@SuppressWarnings("deprecation")
	private static final PlaceController placeController = new PlaceController(eventBus);
	private static Game1View view = null;
	//private static final CoreClientFactory ccf = new CoreClientFactoryImpl();
	private static final Game1ServiceAsync rpcService = GWT.create(Game1Service.class);
	private Boolean isJoining = false;
	private Long clubhouseToJoinId;
//	private Place postCoreDestination = null;
	
	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public PlaceController getPlaceController() {
		return placeController;
	}

	@Override
	public Game1View getGame1View() {
		if (view == null) {
			view = new Game1ViewImpl();
		}
		return view;
	}
	
	@Override
	public CoreClientFactory getCoreClientFactory() {
		return Core.getCore().getClientFactory();
	}

	@Override
	public Game1ServiceAsync getRpcservice() {
		return rpcService;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ClientFactory#isJoiningClubhouse()
	 */
	@Override
	public Boolean isJoiningClubhouse() {
		return isJoining;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ClientFactory#setJoiningClubhouse()
	 */
	@Override
	public void setJoiningClubhouse(Boolean joining, Long clubhouseId) {
		this.isJoining = joining;
		this.clubhouseToJoinId = clubhouseId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ClientFactory#getClubhouseToJoinId()
	 */
	@Override
	public Long getClubhouseToJoinId() {
		
		return clubhouseToJoinId;
	}
//	@Override
//	public Place getPostCoreDestination() {
//		return postCoreDestination;
//	}
//	@Override
//	public void setPostCoreDestination(Place postCoreDestination) {
//		this.postCoreDestination = postCoreDestination;
//	}
	
	

}
