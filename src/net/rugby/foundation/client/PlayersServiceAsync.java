package net.rugby.foundation.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;

import net.rugby.foundation.model.shared.CoreConfiguration.selectionType;
import net.rugby.foundation.model.shared.Feature;
import net.rugby.foundation.model.shared.Group;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ClubhouseMembership;
import net.rugby.foundation.model.shared.LoginInfo;
import net.rugby.foundation.model.shared.MyGroup;
import net.rugby.foundation.model.shared.Player;
import net.rugby.foundation.model.shared.PlayerPopupData;
import net.rugby.foundation.model.shared.PlayerRowData;
import net.rugby.foundation.model.shared.Stage.stageType;

public interface PlayersServiceAsync {

  public void addPlayer(Player player, Long teamID, AsyncCallback<Player> callback);
  public void deletePlayer(Long id, AsyncCallback<Boolean> callback);
  public void deletePlayers(ArrayList<Long> ids, AsyncCallback<ArrayList<PlayerRowData>> callback);
  public void getPlayerRowData(AsyncCallback<ArrayList<PlayerRowData>> callback);
  public void getPlayer(Long id, AsyncCallback<Player> callback);
  public void updatePlayer(Player Player, AsyncCallback<Player> callback);
  public void getPlayerRowDataByGroup(Long groupID, boolean poolMatch, AsyncCallback<ArrayList<PlayerRowData>> callback);
  public void getPlayerRowDataByPositionAndComp(Long groupID, Long compID, boolean showInactive, boolean isPool, AsyncCallback<ArrayList<PlayerRowData>> callback);
  public void getGroupInfo(Long name, AsyncCallback<String> asyncCallback);
  public void getGroupsByGroupType(Group.GroupType type, AsyncCallback<ArrayList<Group>> asyncCallback);
  public void getGroupsByGroupTypeByComp(Group.GroupType type, Long compID, AsyncCallback<ArrayList<Group>> asyncCallback);
  public void getPlayerPopupData(Long id, AsyncCallback<PlayerPopupData> callback);
  public void endRound(ArrayList<Long> matchIDs, AsyncCallback<ArrayList<String>> callback); 
  public void endStage(Long compID, AsyncCallback<ArrayList<String>> callback); 
  public void login(String requestUri, AsyncCallback<LoginInfo> async);
  public void createAccount(String emailAddress, String nickName, String password, boolean isGoogle, boolean isFacebook, AsyncCallback<LoginInfo> callback);
  public void nativeLogin(String emailAddress, String password, AsyncCallback<LoginInfo> callback);
  public void logOff(LoginInfo sessiond, AsyncCallback<LoginInfo> callback);
  public void submitDraftTeam(MyGroup my, selectionType type, Long compID, AsyncCallback<Long> callback);
  public void getMyGroup(stageType st, int round, AsyncCallback<ArrayList<PlayerRowData>> callback);
  public void updateGroupInfo(Long groupID, String info, AsyncCallback<Group> callback);
  public void doRandomDraft(stageType stage, Long compID, AsyncCallback<Long> callback);
  public void makeAdmin(String email, Boolean superAdmin, AsyncCallback<Boolean> callback);
  public void updateRoundScores(Integer round, AsyncCallback<String> callback);
  public void setContent(Long id, String text, AsyncCallback<Boolean> callback);
  public void getContent(Long id, AsyncCallback<String> callback);
  public void getCurrentlyFeaturedGroup(AsyncCallback<Feature> callback);
  public void addFeature(Feature feature, AsyncCallback<Boolean> callback);
  public void getLeaderBoard(Long leagueID, AsyncCallback<ArrayList<ClubhouseMembership>> callback);
  public void createClubhouse(IClubhouse league, AsyncCallback<IClubhouse> callback);
  public void joinLeague(Long leagueID, AsyncCallback<String> callback);
  public void getGroup(Long groupID, AsyncCallback<Group> callback);
  public void getNickname(Long id, AsyncCallback<String> callback);
  public void getMyClubhouse(Long id, AsyncCallback<IClubhouse> callback);
  public void setupKnockOut(AsyncCallback<Boolean> callback);
  public void getCompetitionByShortName(String shortName, AsyncCallback<ICompetition> callback);
  public void copyLastRoundsPick(AsyncCallback<Boolean> callback);
  public void sendEmails(Long compID, AsyncCallback<String> asyncCallback);
}

