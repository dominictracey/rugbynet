package net.rugby.foundation.client.activity;

import java.util.ArrayList;

import net.rugby.foundation.client.ClientFactory;
import net.rugby.foundation.client.PlayersServiceAsync;
import net.rugby.foundation.client.place.Details;
import net.rugby.foundation.client.place.Browse;
import net.rugby.foundation.client.ui.EditPlayerView;
import net.rugby.foundation.model.shared.Player;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class EditPlayerActivity extends AbstractActivity implements
	EditPlayerView.Presenter<Player> {
	
	private ClientFactory clientFactory;
//	private Player player;
	private ArrayList<Long> playerIDs;
//	private Long groupID;
//	private GroupType groupTypeID;
	private final PlayersServiceAsync rpcService;
	private Details place;
	private boolean isNew = false;
	
	public EditPlayerActivity(Details place, ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		this.rpcService = clientFactory.getRpcService();
		this.place = place;
		playerIDs = new ArrayList<Long>();
		for (Long p : place.getEditPlayerIDs())
		{
			playerIDs.add(Long.valueOf(p));
		}
		
		if (playerIDs.size() == 0)
			isNew = true;
	}
	
	private Player player;
	
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		//get PlayerRowData from service
		
		EditPlayerView<Player> editPlayerView = clientFactory.getEditPlayerView();
		editPlayerView.setPresenter(this);
		containerWidget.setWidget(editPlayerView.asWidget());
		if (!isNew) {
			fetchPlayerRowData(0);
		}
		else {
			clientFactory.getEditPlayerView().clear(); 
		}
			
	}

	private void fetchPlayerRowData(int index) {
		rpcService.getPlayer(playerIDs.get(index), new AsyncCallback<Player>() {
		      public void onSuccess(Player result) {
		    	  player = result;
		    	  clientFactory.getEditPlayerView().setPlayer(player);
		      }
		      
		      public void onFailure(Throwable caught) {
		        Window.alert("Error fetching Player details");
		      }
		    });		
	}

	@Override
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}

	@Override
	public void onSaveButtonClicked() {
		if (player == null) {
			player = new Player();
		}
		player = clientFactory.getEditPlayerView().getPlayer(player);
		
		if (!isNew)
		{
			
			rpcService.updatePlayer(player, new AsyncCallback<Player>() {
			      public void onSuccess(Player result) {
			    	 // Window.alert("Player " + result.getDisplayName() + " " + " saved.");
			    	  
			      }
			      
			      public void onFailure(Throwable caught) {
			        Window.alert("Error fetching Player details");
			      }
			    });		
			
			if (playerIDs.size() > 1) {
				playerIDs.remove(0);
				goTo(new Details(clientFactory.getHomeID(), place.getGroupTypeID(), place.getGroupID(), playerIDs));
			}
			else {
		        Window.alert("Done editing");
				goTo(new Browse(clientFactory.getHomeID(), place.getGroupTypeID(), place.getGroupID(), 0L));			
			}
		}
		else {
			Long teamID = player.getTeamID();
			player.setTeamID(null);
			player.setOverallRating(player.getOrigRating());
			rpcService.addPlayer(player, teamID, new AsyncCallback<Player>() {
			      public void onSuccess(Player result) {
			    	 // Window.alert("Player " + result.getDisplayName() + " " + " saved.");
			    	  //add the team membership record too
			    	  
			    	  
			      }
			      
			      public void onFailure(Throwable caught) {
			        Window.alert("Error fetching Player details");
			      }
			    });		
			goTo(new Browse(clientFactory.getHomeID(), place.getGroupTypeID(), place.getGroupID(), 0L));
		}
		
	}

	@Override
	public void onCancelButtonClicked() {
		goTo(new Browse(clientFactory.getHomeID(), place.getGroupTypeID(), place.getGroupID(), 0L));
		
	}

}