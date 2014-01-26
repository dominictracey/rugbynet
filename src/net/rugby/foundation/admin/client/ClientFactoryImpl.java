package net.rugby.foundation.admin.client;

import java.util.List;

import net.rugby.foundation.admin.client.ui.ColumnDefinition;
import net.rugby.foundation.admin.client.ui.CompetitionView;
import net.rugby.foundation.admin.client.ui.CompetitionViewImpl;
import net.rugby.foundation.admin.client.ui.EditContent;
import net.rugby.foundation.admin.client.ui.EditContent.EditContentPresenter;
import net.rugby.foundation.admin.client.ui.FieldDefinition;
import net.rugby.foundation.admin.client.ui.OrchestrationConfigurationView;
import net.rugby.foundation.admin.client.ui.OrchestrationConfigurationViewImpl;
import net.rugby.foundation.admin.client.ui.SmartBar;
import net.rugby.foundation.admin.client.ui.SmartBarImpl;
import net.rugby.foundation.admin.client.ui.matchratingengineschemapopup.MatchRatingEngineSchemaPopupView;
import net.rugby.foundation.admin.client.ui.matchratingengineschemapopup.MatchRatingEngineSchemaPopupViewFieldDefinitions;
import net.rugby.foundation.admin.client.ui.matchratingengineschemapopup.MatchRatingEngineSchemaPopupViewImpl;
import net.rugby.foundation.admin.client.ui.playerlistview.PlayerListView;
import net.rugby.foundation.admin.client.ui.playerlistview.PlayerListViewColumnDefinitions;
import net.rugby.foundation.admin.client.ui.playerlistview.PlayerListViewImpl;
import net.rugby.foundation.admin.client.ui.playerlistview.RatingListViewColumnDefinitions;
import net.rugby.foundation.admin.client.ui.playerlistview.RatingListViewImpl;
import net.rugby.foundation.admin.client.ui.playermatchstatspopup.PlayerMatchStatsPopupView;
import net.rugby.foundation.admin.client.ui.playermatchstatspopup.PlayerMatchStatsPopupViewFieldDefinitions;
import net.rugby.foundation.admin.client.ui.playermatchstatspopup.PlayerMatchStatsPopupViewImpl;
import net.rugby.foundation.admin.client.ui.playerpopup.PlayerPopupView;
import net.rugby.foundation.admin.client.ui.playerpopup.PlayerPopupViewFieldDefinitions;
import net.rugby.foundation.admin.client.ui.playerpopup.PlayerPopupViewImpl;
import net.rugby.foundation.admin.client.ui.portal.PortalView;
import net.rugby.foundation.admin.client.ui.portal.PortalViewImpl;
import net.rugby.foundation.admin.client.ui.task.TaskView;
import net.rugby.foundation.admin.client.ui.task.TaskViewColumnDefinitions;
import net.rugby.foundation.admin.client.ui.task.TaskViewImpl;
import net.rugby.foundation.admin.client.ui.teammatchstatspopup.TeamMatchStatsPopupView;
import net.rugby.foundation.admin.client.ui.teammatchstatspopup.TeamMatchStatsPopupViewFieldDefinitions;
import net.rugby.foundation.admin.client.ui.teammatchstatspopup.TeamMatchStatsPopupViewImpl;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.admin.shared.ScrumMatchRatingEngineSchema20130713;
import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.Position.position;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Sample implementation of {@link ClientFactory}.
 */
public class ClientFactoryImpl implements ClientFactory {
  
	private static final EventBus eventBus = new SimpleEventBus();
	@SuppressWarnings("deprecation")
	private static final PlaceController placeController = new PlaceController(eventBus);
	private static final CompetitionView compView = new CompetitionViewImpl();
	private static OrchestrationConfigurationView orchView = null;
	private static TaskView<IAdminTask> taskView = null;
	private static PortalView<IPlayerMatchInfo> portalView = null;
	private static final RugbyAdminServiceAsync rpcService = GWT.create(RugbyAdminService.class);
	private PlayerPopupView<IPlayer> playerPopupView;
	private List<FieldDefinition<IPlayer>> playerPopupViewFieldDefinitions;
	private PlayerMatchStatsPopupView<IPlayerMatchStats> playerMatchStatsPopupView;
	private List<FieldDefinition<IPlayerMatchStats>> playerMatchStatsPopupViewFieldDefinitions;
	private TeamMatchStatsPopupView<ITeamMatchStats> teamMatchStatsPopupView;
	private List<FieldDefinition<ITeamMatchStats>> teamMatchStatsPopupViewFieldDefinitions;
	private MatchRatingEngineSchemaPopupView<ScrumMatchRatingEngineSchema20130713> matchRatingEngineSchemaPopupView;
	private MatchRatingEngineSchemaPopupViewFieldDefinitions<ScrumMatchRatingEngineSchema20130713> matchRatingEngineSchemaPopupViewFieldDefinitions;
	
	private PlayerListView<IPlayerMatchInfo> playerListView = null;
	private List<ColumnDefinition<IPlayerMatchInfo>> playerListViewColumnDefinitions =  null; 
	private PlayerListView<IPlayerRating> ratingListView = null;
	private List<ColumnDefinition<IPlayerRating>> ratingListViewColumnDefinitions =  null;
	
	private EditContent editContent = null;
	
	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public PlaceController getPlaceController() {
		return placeController;
	}

	@Override
	public CompetitionView getCompView() {
		compView.setClientFactory(this);
		return compView;
	}

	@Override
	public RugbyAdminServiceAsync getRpcService() {
		return rpcService;
	}
	
	@Override
	public PlayerPopupView<IPlayer> getPlayerPopupView()
	{
        // lazily initialize our views, and keep them around to be reused
        //
        if (playerPopupView == null) {
        	playerPopupView = new PlayerPopupViewImpl<IPlayer>();
			if (playerPopupViewFieldDefinitions == null) {
				playerPopupViewFieldDefinitions = new PlayerPopupViewFieldDefinitions<IPlayer>(this).getFieldDefinitions();
	          }
		
            playerPopupView.setFieldDefinitions(playerPopupViewFieldDefinitions);
        }

		return playerPopupView;
	}

	@Override
	public void getCountryListAsync(final GetCountryListCallback cb) {
		getRpcService().fetchCountryList(new AsyncCallback<List<ICountry>>() {

			@Override
			public void onFailure(Throwable caught) {


			}

			@Override
			public void onSuccess(List<ICountry> result) {
				cb.onCountryListFetched(result);

			}
		});
		
	}

	@Override
	public void getPositionListAsync(final GetPositionListCallback cb) {
		getRpcService().fetchPositionList(new AsyncCallback<List<position>>() {

			@Override
			public void onFailure(Throwable caught) {


			}

			@Override
			public void onSuccess(List<position> result) {
				cb.onPositionListFetched(result);

			}
		});
		
	}

	@Override
	public PlayerMatchStatsPopupView<IPlayerMatchStats> getPlayerMatchStatsPopupView() {
        // lazily initialize our views, and keep them around to be reused
        //
        if (playerMatchStatsPopupView == null) {
        	playerMatchStatsPopupView = new PlayerMatchStatsPopupViewImpl<IPlayerMatchStats>();
			if (playerMatchStatsPopupViewFieldDefinitions == null) {
				playerMatchStatsPopupViewFieldDefinitions = new PlayerMatchStatsPopupViewFieldDefinitions<IPlayerMatchStats>(this).getFieldDefinitions();
	          }
		
			playerMatchStatsPopupView.setFieldDefinitions(playerMatchStatsPopupViewFieldDefinitions);
        }

		return playerMatchStatsPopupView;
	}

	@Override
	public void getMatchGroupAsync(Long id, final GetMatchGroupCallback cb) {
		getRpcService().getMatch(id, new AsyncCallback<IMatchGroup>() {

			@Override
			public void onFailure(Throwable caught) {


			}

			@Override
			public void onSuccess(IMatchGroup result) {
				cb.onMatchGroupFetched(result);

			}
		});		
	}

	@Override
	public OrchestrationConfigurationView getOrchestrationConfigurationView() {
		if (orchView == null) {
			orchView = new OrchestrationConfigurationViewImpl();
			orchView.setClientFactory(this);
		}
		return orchView;
	}

	@Override
	public TaskView<IAdminTask> getTaskView() {
		if (taskView == null) {
			taskView = new TaskViewImpl<IAdminTask>();
			taskView.setColumnDefinitions(new TaskViewColumnDefinitions<IAdminTask>());
			taskView.setClientFactory(this);
		}
		return taskView;
	}

	@Override
	public SmartBar getMenuBar() {
		return new SmartBarImpl();
	}

	@Override
	public PlayerListView<IPlayerMatchInfo> getPlayerListView() {
		if (playerListView == null) {
			playerListView = new PlayerListViewImpl<IPlayerMatchInfo>();

			  if (playerListViewColumnDefinitions == null) {
				PlayerListViewColumnDefinitions<?> plvcd =  new PlayerListViewColumnDefinitions<IPlayerMatchInfo>();
			    playerListViewColumnDefinitions = plvcd.getColumnDefinitions();
			  }

			  playerListView.setColumnDefinitions(playerListViewColumnDefinitions);
			  playerListView.setColumnHeaders(PlayerListViewColumnDefinitions.getHeaders());
		}
		return playerListView;
	}
	
	@Override
	public PlayerListView<IPlayerRating> getRatingListView() {
		if (ratingListView == null) {
			ratingListView = new RatingListViewImpl<IPlayerRating>();

			  if (ratingListViewColumnDefinitions == null) {
				RatingListViewColumnDefinitions<?> plvcd =  new RatingListViewColumnDefinitions<IPlayerRating>();
			    ratingListViewColumnDefinitions = plvcd.getColumnDefinitions();
			  }

			  ratingListView.setColumnDefinitions(ratingListViewColumnDefinitions);
			  ratingListView.setColumnHeaders(RatingListViewColumnDefinitions.getHeaders());
		}
		return ratingListView;
	}

	@Override
	public PortalView<IPlayerMatchInfo> getPortalView() {
		if (portalView == null) {
			portalView = new PortalViewImpl<IPlayerMatchInfo>();
			portalView.setClientFactory(this);
		}
		return portalView;
	}

	@Override
	public TeamMatchStatsPopupView<ITeamMatchStats> getTeamMatchStatsPopupView() {
        if (teamMatchStatsPopupView == null) {
        	teamMatchStatsPopupView = new TeamMatchStatsPopupViewImpl<ITeamMatchStats>();
			if (teamMatchStatsPopupViewFieldDefinitions == null) {
				teamMatchStatsPopupViewFieldDefinitions = new TeamMatchStatsPopupViewFieldDefinitions<ITeamMatchStats>(this).getFieldDefinitions();
	          }
		
			teamMatchStatsPopupView.setFieldDefinitions(teamMatchStatsPopupViewFieldDefinitions);
        }

		return teamMatchStatsPopupView;
	}

	@Override
	public MatchRatingEngineSchemaPopupView<ScrumMatchRatingEngineSchema20130713> getMatchRatingEngineSchemaPopupView() {
        if (matchRatingEngineSchemaPopupView == null) {
        	matchRatingEngineSchemaPopupView = new MatchRatingEngineSchemaPopupViewImpl();
			if (matchRatingEngineSchemaPopupViewFieldDefinitions == null) {
				matchRatingEngineSchemaPopupViewFieldDefinitions = new MatchRatingEngineSchemaPopupViewFieldDefinitions<ScrumMatchRatingEngineSchema20130713>(this);
	          }
		
			matchRatingEngineSchemaPopupView.setFieldDefinitions(matchRatingEngineSchemaPopupViewFieldDefinitions.getFieldDefinitions());
        }

		return matchRatingEngineSchemaPopupView;	}

	@Override
	public void flushAllPipelineJobs() {
		getRpcService().flushAllPipelineJobs(new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error: Pipeline jobs not flushed: " + caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result != null && result) {
					Window.alert("Pipeline jobs flushed");
				} else {
					Window.alert("Pipeline jobs not flushed");
				}

			}
		});		
	}
	
	@Override
	public void createContent() {
		String content = "";
		Long id = null;
		final EditContentPresenter listener = this;
		getRpcService().createContent(id, content, new AsyncCallback<IContent>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Create content failure: " + caught.getLocalizedMessage());

			}

			@Override
			public void onSuccess(IContent result) {
				if (result != null) {
					getEditContent().setContent(result, listener);
					getEditContent().center();
				} else {
					Window.alert("Content not created. See logs for details");
				}

			}

		});
		
	}
	
	@Override
	public EditContent getEditContent() {
		if (editContent == null) {
			editContent = new EditContent();
		}
		
		return editContent;
	}

	@Override
	public void saveContent(IContent content) {
		Core.getCore().saveContent(content, new AsyncCallback<IContent>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Save content failure: " + caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(IContent result) {
				if (result != null) {
					getEditContent().hide();
				} else {
					Window.alert("Content not saved. See logs for details");
				}

			}

		});
		
	}

	@Override
	public void cancelEditContent() {
		editContent.hide();
		
	}

	@Override
	public void editContent(IContent content) {

		final EditContentPresenter listener = this;
		
		Core.getCore().getContent(content.getId(), new AsyncCallback<IContent>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Get content failure: " + caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(IContent result) {
				if (result != null) {
					getEditContent().setContent(result, listener);
					getEditContent().center();
				} else {
					Window.alert("Content not fetched for editing. See logs for details");
				}

			}

		});	}


}
