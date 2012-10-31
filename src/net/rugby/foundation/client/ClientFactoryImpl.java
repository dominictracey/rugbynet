package net.rugby.foundation.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.rugby.foundation.client.ui.ColumnDefinition;
import net.rugby.foundation.client.ui.CreateAccount;
import net.rugby.foundation.client.ui.EditPlayerView;
import net.rugby.foundation.client.ui.EditPlayerViewFieldDefinitions;
import net.rugby.foundation.client.ui.EditPlayerViewImpl;
import net.rugby.foundation.client.ui.FieldDefinition;
import net.rugby.foundation.client.ui.Footer;
import net.rugby.foundation.client.ui.GroupBrowser;
import net.rugby.foundation.client.ui.HomeView;
import net.rugby.foundation.client.ui.HomeViewImpl;
import net.rugby.foundation.client.ui.ManageView;
import net.rugby.foundation.client.ui.ManageViewImpl;
import net.rugby.foundation.client.ui.NativeLogin;
import net.rugby.foundation.client.ui.PlayerListView;
import net.rugby.foundation.client.ui.PlayerListViewColumnDefinitions;
import net.rugby.foundation.client.ui.PlayerListViewColumnDefinitionsNoSelect;
import net.rugby.foundation.client.ui.PlayerListViewImpl;
import net.rugby.foundation.client.ui.PlayerListViewPoolColumnDefinitions;
import net.rugby.foundation.client.ui.PlayerPopupView;
import net.rugby.foundation.client.ui.PlayerPopupViewFieldDefinitions;
import net.rugby.foundation.client.ui.PlayerPopupViewImpl;
import net.rugby.foundation.model.shared.CoreConfiguration;
import net.rugby.foundation.model.shared.CoreConfiguration.selectionType;
import net.rugby.foundation.model.shared.Group.GroupType;
import net.rugby.foundation.model.shared.DraftWizardState;
import net.rugby.foundation.model.shared.Group;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.LoginInfo;
import net.rugby.foundation.model.shared.ManagementEngine;
import net.rugby.foundation.model.shared.ManagementEngineFactory;
import net.rugby.foundation.model.shared.Player;
import net.rugby.foundation.model.shared.PlayerPopupData;
import net.rugby.foundation.model.shared.PlayerRowData;
import net.rugby.foundation.model.shared.Position;
import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.model.shared.PositionEnUs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ClientFactoryImpl implements ClientFactory
{
	
	private static final EventBus eventBus = new SimpleEventBus();
	@SuppressWarnings("deprecation")
	private static final PlaceController placeController = new PlaceController(eventBus);
	private static PlayerListView<PlayerRowData> playerListView = null;
	private static PlayerListView<PlayerRowData> playerListViewNoSelect = null;
	private static PlayerListView<PlayerRowData> playerListViewPool = null;
	private static HomeView<LoginInfo> homeView = null;
	private List<ColumnDefinition<PlayerRowData>> playerListViewColumnDefinitions = null;
	private List<ColumnDefinition<PlayerRowData>> playerListViewColumnDefinitionsNoSelect = null;
	private List<ColumnDefinition<PlayerRowData>> playerListViewColumnDefinitionsPool = null;
	List<FieldDefinition<Player>>editPlayerViewFieldDefinitions = null;
	List<FieldDefinition<PlayerPopupData>>playerPopupViewFieldDefinitions = null;
	private CreateAccount<LoginInfo> createAccount = null;
	private NativeLogin<LoginInfo> nativeLogin = null;
	private Footer footer = null;
	
	private ManagementEngine<PlayerRowData,DraftWizardState> poolRostermanagementEngine;
	private ManagementEngine<PlayerRowData,DraftWizardState> poolRoundmanagementEngine;
	private ManagementEngine<PlayerRowData,DraftWizardState> knockoutRostermanagementEngine;
	private ManagementEngine<PlayerRowData,DraftWizardState> knockoutRoundmanagementEngine;
	
	
	private static EditPlayerView<Player> editPlayerView = null;
	private static PlayerPopupView<PlayerPopupData> playerPopupView = null;
	private static final PlayersServiceAsync rpcService = GWT.create(PlayersService.class);
	private static final GroupBrowser groupBrowser = new GroupBrowser();
	private static LoginInfo loginInfo = null;
	private static Identity identity = null;
	private static ManageView<DraftWizardState> manageView = null;
	private static HashMap<Long,Group> groupCache = new HashMap<Long,Group>();
	private static HashMap<Long,String> nicknameCache = new HashMap<Long,String>();
	private static HashMap<String,ICompetition> competitionCache = new HashMap<String,ICompetition>();
	
	private Long homeID = 0L;
	
	@Override
	public EventBus getEventBus()
	{
		return eventBus;
	}


	@Override
	public HomeView<LoginInfo> getHomeView() {
        // lazily initialize our views, and keep them around to be reused
        //
        if (homeView == null) {
        	homeView = new HomeViewImpl<LoginInfo>();
        }
		return homeView;	
	}
	
	@Override
	public PlayerListView<PlayerRowData> getPlayerListView()
	{
        // lazily initialize our views, and keep them around to be reused
        //
        if (playerListView == null) {
        	  playerListView = new PlayerListViewImpl<PlayerRowData>();
			  if (playerListViewColumnDefinitions == null) {
				PlayerListViewColumnDefinitions<PlayerRowData> plvcd =  new PlayerListViewColumnDefinitions<PlayerRowData>();
			    playerListViewColumnDefinitions = plvcd.getColumnDefinitions();
			  }

			  playerListView.setColumnDefinitions(playerListViewColumnDefinitions);
			  playerListView.setColumnHeaders(PlayerListViewColumnDefinitions.getHeaders());
        }
		return playerListView;
	}

	@Override
	public PlaceController getPlaceController()
	{
		return placeController;
	}

	@Override
	public EditPlayerView<Player> getEditPlayerView()
	{
        // lazily initialize our views, and keep them around to be reused
        //
        if (editPlayerView == null) {
        	editPlayerView = new EditPlayerViewImpl<Player>();

			if (editPlayerViewFieldDefinitions == null) {
				//pass in a reference to the clientFactory so it can call for list of teams
				EditPlayerViewFieldDefinitions<Player> epvfd = new EditPlayerViewFieldDefinitions<Player>(this);
	        	editPlayerViewFieldDefinitions = epvfd.getFieldDefinitions();
			}
        	
            editPlayerView.setFieldDefinitions(editPlayerViewFieldDefinitions);
        }
		return editPlayerView;
	}
	
	@Override
	public PlayerPopupView<PlayerPopupData> getPlayerPopupView()
	{
        // lazily initialize our views, and keep them around to be reused
        //
        if (playerPopupView == null) {
        	playerPopupView = new PlayerPopupViewImpl<PlayerPopupData>();
			if (editPlayerViewFieldDefinitions == null) {
				playerPopupViewFieldDefinitions = new PlayerPopupViewFieldDefinitions<PlayerPopupData>().getFieldDefinitions();
	          }
		
            playerPopupView.setFieldDefinitions(playerPopupViewFieldDefinitions);
        }

		return playerPopupView;
	}

	@Override
	public PlayersServiceAsync getRpcService() {
		return rpcService;
	}

	@Override
	public Position getPosition() {		
		return new PositionEnUs();
	}

	@Override
	public Position getPosition(position p) {
		return new PositionEnUs(p);
	}

	@Override
	public Position getPosition(position p, ArrayList<position> l) {
		return new PositionEnUs(p,l);
	}

	@Override
	public Long getHomeID() {
		return homeID;
	}

	@Override
	public GroupBrowser getGroupBrowser() {
		return groupBrowser;
	}


	@Override
	public LoginInfo getLoginInfo() {
		return loginInfo;
	}


	@Override
	public void setLoginInfo(LoginInfo loginInfo) {
		ClientFactoryImpl.loginInfo = loginInfo;
		
	}


	@Override
	public Identity getIdentityManager() {
		if (identity == null)
		{
			identity = new Identity();
		}
		
		return identity;
	}


	@Override
	public CreateAccount<LoginInfo> getCreateAccount() {
        if (createAccount == null) {
        	createAccount = new CreateAccount<LoginInfo>();
        }
		return createAccount ;	
	}


	@Override
	public NativeLogin<LoginInfo> getNativeLogin() {
        if (nativeLogin == null) {
        	nativeLogin = new NativeLogin<LoginInfo>();
        }
		return nativeLogin ;	
	}

	@Override
	public ManageView<DraftWizardState> getManageView() {
		if (manageView == null)
		{
			manageView = new ManageViewImpl<DraftWizardState>();
			manageView.setClientFactory(this);
		}
		return manageView;
	}


	@Override
	public int getNumberTeams() {
		return CoreConfiguration.getNumberteams();
	}


	@Override
	public int getNumberPositions() {
		return CoreConfiguration.getNumberpositions();
	}

	@Override
	public int getCurrentround() {
		return CoreConfiguration.getCurrentround();
	}


	@Override
	public void getManagementEngineAsync(final selectionType type, Long compID, final GetManagementEngineAsyncCallback cb) {
		getRpcService().getGroupsByGroupTypeByComp(GroupType.TEAM, compID, new AsyncCallback<ArrayList<Group>>() {
			public void onSuccess(ArrayList<Group> result) {
				if (type == selectionType.POOLROSTER) {
					if (poolRostermanagementEngine == null) 
						poolRostermanagementEngine = ManagementEngineFactory.getManagementEngine(type, CoreConfiguration.getMaxperteamPoolRoster(), CoreConfiguration.getDraftsizePoolRoster(), CoreConfiguration.getCurrentround(), CoreConfiguration.getMaxpointsPoolRoster(),  result);
					cb.onEngineStarted( poolRostermanagementEngine);
				} else if (type == selectionType.POOLROUND) {
					if (poolRoundmanagementEngine == null) 
						poolRoundmanagementEngine = ManagementEngineFactory.getManagementEngine(type, CoreConfiguration.getMaxperteamPoolRound(), CoreConfiguration.getDraftsizePoolRound(), CoreConfiguration.getCurrentround(), CoreConfiguration.getMaxpointsPoolRound(), result);
					cb.onEngineStarted( poolRoundmanagementEngine);
				} else if (type == selectionType.KNOCKOUTROSTER) {
					if (knockoutRostermanagementEngine == null) 
						knockoutRostermanagementEngine = ManagementEngineFactory.getManagementEngine(type, CoreConfiguration.getMaxperteamKnockoutRoster(), CoreConfiguration.getDraftsizeKnockoutRoster(), CoreConfiguration.getCurrentround(), CoreConfiguration.getMaxpointsKnockoutRoster(), result);
					cb.onEngineStarted( knockoutRostermanagementEngine);
				} else if (type == selectionType.KNOCKOUTROUND) {
					if (knockoutRoundmanagementEngine == null) 
						knockoutRoundmanagementEngine = ManagementEngineFactory.getManagementEngine(type, CoreConfiguration.getMaxperteamKnockoutRound(), CoreConfiguration.getDraftsizeKnockoutRound(), CoreConfiguration.getCurrentround(), CoreConfiguration.getMaxpointsKnockoutRound(), result);
					cb.onEngineStarted( knockoutRoundmanagementEngine);
				}

		    	  
		      }		      
		      public void onFailure(Throwable caught) {
		    	  cb.onEngineStarted(null);//err...
		      }
		    });	
	}


	@Override
	public PlayerListView<PlayerRowData> getPlayerListViewNoSelect() {
        // lazily initialize our views, and keep them around to be reused
        //
        if (playerListViewNoSelect == null) {
        	  playerListViewNoSelect = new PlayerListViewImpl<PlayerRowData>();

			  if (playerListViewColumnDefinitionsNoSelect == null) {
				  playerListViewColumnDefinitionsNoSelect = new PlayerListViewColumnDefinitionsNoSelect<PlayerRowData>().getColumnDefinitions();
			  }

			  playerListViewNoSelect.setColumnDefinitions(playerListViewColumnDefinitionsNoSelect);
			  playerListViewNoSelect.setColumnHeaders(PlayerListViewColumnDefinitionsNoSelect.getHeaders());

        }
		return playerListViewNoSelect;
	}

	@Override
	public PlayerListView<PlayerRowData> getPlayerListViewPool() {
        if (playerListViewPool == null) {
        	playerListViewPool = new PlayerListViewImpl<PlayerRowData>();

			  if (playerListViewColumnDefinitionsPool == null) {
				  playerListViewColumnDefinitionsPool = new PlayerListViewPoolColumnDefinitions<PlayerRowData>().getColumnDefinitions();
			  }

			  playerListViewPool.setColumnDefinitions(playerListViewColumnDefinitionsPool);
			  playerListViewPool.setColumnHeaders(PlayerListViewPoolColumnDefinitions.getHeaders());

      }
		return playerListViewPool;
	}
	
	@Override
	public Footer getFooter() {
	
		if (footer == null) {
			footer = new Footer();
			footer.setClientFactory(this);
		}
		
		return footer;
	}


	@Override
	public void getGroupAsync(final Long id, final GroupCallback cb) {
		if (groupCache.containsKey(id))
			cb.onGroupFetched(groupCache.get(id));
		else {
		    rpcService.getGroup(id, new AsyncCallback<Group>() {
			      public void onSuccess(Group result) {
			    	  groupCache.put(id,result);
			    	  cb.onGroupFetched(groupCache.get(id));
			      }		      
			      public void onFailure(Throwable caught) {
			    	  cb.onGroupFetched(null);//err...
			      }
			    });	
		}
	}


	@Override
	public void getAppUserNameAsync(final Long id, final AppUserNicknameCallback cb) {
		if (nicknameCache.containsKey(id))
			cb.onNicknameFetched(nicknameCache.get(id));
		else {
		    rpcService.getNickname(id, new AsyncCallback<String>() {
			      public void onSuccess(String result) {
			    	  nicknameCache.put(id,result);
			    	  cb.onNicknameFetched(result);
			      }		      
			      public void onFailure(Throwable caught) {
			    	  cb.onNicknameFetched("");
			      }
			    });	
		}
	}


	@Override
	public void getCompetitionAsync(final String shortName, final GetCompetitionCallback cb) {
		if (competitionCache.containsKey(shortName))
			cb.onCompetitionFetched(competitionCache.get(shortName));
		else {
		    rpcService.getCompetitionByShortName(shortName, new AsyncCallback<ICompetition>() {
			      public void onSuccess(ICompetition result) {
			    	  competitionCache.put(shortName,result);
			    	  cb.onCompetitionFetched(result);
			      }		      
			      public void onFailure(Throwable caught) {
			    	  cb.onCompetitionFetched(null);
			      }
			    });	
		}
	}


	@Override
	public void setHomeAsync(final SetHomeAsyncCallback cb) {
		getCompetitionAsync(CoreConfiguration.getDefaultCompetitionShortName(), new GetCompetitionCallback() {

			@Override
			public void onCompetitionFetched(ICompetition comp) {
				if (comp != null) {
					homeID = comp.getId();
					cb.onSetHomeComplete(homeID);
				}	
			}			
		});
		
	}


	@Override
	public void trashDraftEngines() {
		poolRostermanagementEngine = null;
		poolRoundmanagementEngine = null;
		knockoutRostermanagementEngine = null;
		knockoutRoundmanagementEngine = null;
	}





}
