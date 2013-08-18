package net.rugby.foundation.topten.client;

import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.topten.client.ui.toptenlistview.EditTTIText;
import net.rugby.foundation.topten.client.ui.toptenlistview.TopTenListView;
import net.rugby.foundation.topten.client.ui.toptenlistview.TopTenListViewImpl;
import net.rugby.foundation.topten.model.shared.ITopTenItem;

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
	private static final TopTenListView<ITopTenItem> listView = new TopTenListViewImpl();
	private static final TopTenListServiceAsync rpcService = GWT.create(TopTenListService.class);
	private static EditTTIText editTTIText = null;
	
	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public PlaceController getPlaceController() {
		return placeController;
	}

	@Override
	public TopTenListView<ITopTenItem> getListView() {
		return listView;
	}

	@Override
	public TopTenListServiceAsync getRpcService() {
		return rpcService;
	}
//	
//	@Override
//	public PlayerPopupView<IPlayer> getPlayerPopupView()
//	{
//        // lazily initialize our views, and keep them around to be reused
//        //
//        if (playerPopupView == null) {
//        	playerPopupView = new PlayerPopupViewImpl<IPlayer>();
//			if (playerPopupViewFieldDefinitions == null) {
//				playerPopupViewFieldDefinitions = new PlayerPopupViewFieldDefinitions<IPlayer>(this).getFieldDefinitions();
//	          }
//		
//            playerPopupView.setFieldDefinitions(playerPopupViewFieldDefinitions);
//        }
//
//		return playerPopupView;
//	}
//
//	@Override
//	public void getCountryListAsync(final GetCountryListCallback cb) {
//		getRpcService().fetchCountryList(new AsyncCallback<List<ICountry>>() {
//
//			@Override
//			public void onFailure(Throwable caught) {
//
//
//			}
//
//			@Override
//			public void onSuccess(List<ICountry> result) {
//				cb.onCountryListFetched(result);
//
//			}
//		});
//		
//	}
//
//	@Override
//	public void getPositionListAsync(final GetPositionListCallback cb) {
//		getRpcService().fetchPositionList(new AsyncCallback<List<position>>() {
//
//			@Override
//			public void onFailure(Throwable caught) {
//
//
//			}
//
//			@Override
//			public void onSuccess(List<position> result) {
//				cb.onPositionListFetched(result);
//
//			}
//		});
//		
//	}
//
//	@Override
//	public PlayerMatchStatsPopupView<IPlayerMatchStats> getPlayerMatchStatsPopupView() {
//        // lazily initialize our views, and keep them around to be reused
//        //
//        if (playerMatchStatsPopupView == null) {
//        	playerMatchStatsPopupView = new PlayerMatchStatsPopupViewImpl<IPlayerMatchStats>();
//			if (playerMatchStatsPopupViewFieldDefinitions == null) {
//				playerMatchStatsPopupViewFieldDefinitions = new PlayerMatchStatsPopupViewFieldDefinitions<IPlayerMatchStats>(this).getFieldDefinitions();
//	          }
//		
//			playerMatchStatsPopupView.setFieldDefinitions(playerMatchStatsPopupViewFieldDefinitions);
//        }
//
//		return playerMatchStatsPopupView;
//	}
//
//	@Override
//	public void getMatchGroupAsync(Long id, final GetMatchGroupCallback cb) {
//		getRpcService().getMatch(id, new AsyncCallback<IMatchGroup>() {
//
//			@Override
//			public void onFailure(Throwable caught) {
//
//
//			}
//
//			@Override
//			public void onSuccess(IMatchGroup result) {
//				cb.onMatchGroupFetched(result);
//
//			}
//		});		
//	}
//
//	@Override
//	public OrchestrationConfigurationView getOrchestrationConfigurationView() {
//		if (orchView == null) {
//			orchView = new OrchestrationConfigurationViewImpl();
//			orchView.setClientFactory(this);
//		}
//		return orchView;
//	}
//
//	@Override
//	public TaskView<IAdminTask> getTaskView() {
//		if (taskView == null) {
//			taskView = new TaskViewImpl<IAdminTask>();
//			taskView.setColumnDefinitions(new TaskViewColumnDefinitions<IAdminTask>());
//			taskView.setClientFactory(this);
//		}
//		return taskView;
//	}
//
//	@Override
//	public SmartBar getMenuBar() {
//		return new SmartBarImpl();
//	}
//
//	@Override
//	public PlayerListView<IPlayerMatchInfo> getPlayerListView() {
//		if (playerListView == null) {
//			playerListView = new PlayerListViewImpl<IPlayerMatchInfo>();
//
//			//editMatchStats = new PlayerListViewImpl<IPlayerMatchInfo>();
//			  if (playerListViewColumnDefinitions == null) {
//				PlayerListViewColumnDefinitions<?> plvcd =  new PlayerListViewColumnDefinitions<IPlayerMatchInfo>();
//			    playerListViewColumnDefinitions = plvcd.getColumnDefinitions();
//			  }
//
//			  playerListView.setColumnDefinitions(playerListViewColumnDefinitions);
//			  playerListView.setColumnHeaders(PlayerListViewColumnDefinitions.getHeaders());
//		}
//		return playerListView;
//	}
//
//	@Override
//	public PortalView<IPlayerMatchInfo> getPortalView() {
//		if (portalView == null) {
//			portalView = new PortalViewImpl<IPlayerMatchInfo>();
//			portalView.setClientFactory(this);
//		}
//		return portalView;
//	}
//
//	@Override
//	public TeamMatchStatsPopupView<ITeamMatchStats> getTeamMatchStatsPopupView() {
//        if (teamMatchStatsPopupView == null) {
//        	teamMatchStatsPopupView = new TeamMatchStatsPopupViewImpl<ITeamMatchStats>();
//			if (teamMatchStatsPopupViewFieldDefinitions == null) {
//				teamMatchStatsPopupViewFieldDefinitions = new TeamMatchStatsPopupViewFieldDefinitions<ITeamMatchStats>(this).getFieldDefinitions();
//	          }
//		
//			teamMatchStatsPopupView.setFieldDefinitions(teamMatchStatsPopupViewFieldDefinitions);
//        }
//
//		return teamMatchStatsPopupView;
//	}
//
//	@Override
//	public MatchRatingEngineSchemaPopupView<ScrumMatchRatingEngineSchema20130713> getMatchRatingEngineSchemaPopupView() {
//        if (matchRatingEngineSchemaPopupView == null) {
//        	matchRatingEngineSchemaPopupView = new MatchRatingEngineSchemaPopupViewImpl();
//			if (matchRatingEngineSchemaPopupViewFieldDefinitions == null) {
//				matchRatingEngineSchemaPopupViewFieldDefinitions = new MatchRatingEngineSchemaPopupViewFieldDefinitions<ScrumMatchRatingEngineSchema20130713>(this);
//	          }
//		
//			matchRatingEngineSchemaPopupView.setFieldDefinitions(matchRatingEngineSchemaPopupViewFieldDefinitions.getFieldDefinitions());
//        }
//
//		return matchRatingEngineSchemaPopupView;	}
//
//	@Override
//	public void flushAllPipelineJobs() {
//		getRpcService().flushAllPipelineJobs(new AsyncCallback<Boolean>() {
//
//			@Override
//			public void onFailure(Throwable caught) {
//				Window.alert("Error: Pipeline jobs not flushed: " + caught.getLocalizedMessage());
//			}
//
//			@Override
//			public void onSuccess(Boolean result) {
//				if (result != null && result) {
//					Window.alert("Pipeline jobs flushed");
//				} else {
//					Window.alert("Pipeline jobs not flushed");
//				}
//
//			}
//		});		
//	}

	@Override
	public EditTTIText getEditTTITextDialog() {
		if (editTTIText == null) {
			editTTIText = new EditTTIText();
		}
		return editTTIText;
	}

}
