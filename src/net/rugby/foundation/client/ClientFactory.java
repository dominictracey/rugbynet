package net.rugby.foundation.client;

import java.util.ArrayList;

import net.rugby.foundation.client.ui.CreateAccount;
import net.rugby.foundation.client.ui.EditPlayerView;
import net.rugby.foundation.client.ui.Footer;
import net.rugby.foundation.client.ui.GroupBrowser;
import net.rugby.foundation.client.ui.HomeView;
import net.rugby.foundation.client.ui.ManageView;
import net.rugby.foundation.client.ui.NativeLogin;
import net.rugby.foundation.client.ui.PlayerListView;
import net.rugby.foundation.client.ui.PlayerPopupView;
import net.rugby.foundation.model.shared.CoreConfiguration.selectionType;
import net.rugby.foundation.model.shared.DraftWizardState;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IGroup;
import net.rugby.foundation.model.shared.LoginInfo;
import net.rugby.foundation.model.shared.ManagementEngine;
import net.rugby.foundation.model.shared.Player;
import net.rugby.foundation.model.shared.PlayerPopupData;
import net.rugby.foundation.model.shared.PlayerRowData;
import net.rugby.foundation.model.shared.Position;
import net.rugby.foundation.model.shared.Position.position;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;

public interface ClientFactory
{

	public interface GroupCallback {
		void onGroupFetched(IGroup g);
	}
	
	public interface AppUserNicknameCallback {
		void onNicknameFetched(String name);
	}

	public interface GetCompetitionCallback {
		void onCompetitionFetched(ICompetition comp);
	}
	
	public interface SetHomeAsyncCallback {
		void onSetHomeComplete(Long id);
	}

	public interface GetManagementEngineAsyncCallback {
		void onEngineStarted(ManagementEngine<PlayerRowData,DraftWizardState> engine);
	}
	
	EventBus getEventBus();
	PlaceController getPlaceController();
	HomeView<LoginInfo> getHomeView();
	PlayerListView<PlayerRowData> getPlayerListView();
	PlayerListView<PlayerRowData> getPlayerListViewNoSelect();
	PlayerListView<PlayerRowData> getPlayerListViewPool();
	EditPlayerView<Player> getEditPlayerView();
	PlayerPopupView<PlayerPopupData> getPlayerPopupView();
	PlayersServiceAsync getRpcService();
	Position getPosition();
	Position getPosition(position p);
	Position getPosition(position p, ArrayList<position> l);
	Long getHomeID();
	GroupBrowser getGroupBrowser();
	LoginInfo getLoginInfo();
	void setLoginInfo(LoginInfo loginInfo);
	Identity getIdentityManager();
	CreateAccount<LoginInfo> getCreateAccount();
	NativeLogin<LoginInfo> getNativeLogin();
	void getManagementEngineAsync(selectionType type, Long compID, GetManagementEngineAsyncCallback cb);
	ManageView<DraftWizardState> getManageView();
	int getNumberTeams();
	int getNumberPositions();
	int getCurrentround();
	Footer getFooter();
	void getGroupAsync(Long id, GroupCallback cb);
	void getAppUserNameAsync(Long id, AppUserNicknameCallback cb);
	void getCompetitionAsync(String shortName, GetCompetitionCallback cb);
	void setHomeAsync(SetHomeAsyncCallback cb);
	void trashDraftEngines();
}
