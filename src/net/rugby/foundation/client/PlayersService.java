package net.rugby.foundation.client;

import com.google.gwt.user.client.rpc.RemoteService; 
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
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

@RemoteServiceRelativePath("PlayersService")
public interface PlayersService extends RemoteService {
	
  Player addPlayer(Player Player, Long teamID);
  Boolean deletePlayer(Long id); 
  ArrayList<PlayerRowData> deletePlayers(ArrayList<Long> ids);
  ArrayList<PlayerRowData> getPlayerRowData();
  Player getPlayer(Long id);
  Player updatePlayer(Player Player);
  ArrayList<PlayerRowData> getPlayerRowDataByGroup(Long groupID, boolean poolMatch);
  ArrayList<PlayerRowData> getPlayerRowDataByPositionAndComp(Long groupID, Long compID, boolean showInactive, boolean isPool);
  String getGroupInfo(Long id);
  ArrayList<Group> getGroupsByGroupType(Group.GroupType type);
  ArrayList<Group> getGroupsByGroupTypeByComp(Group.GroupType type, Long compID);
  PlayerPopupData getPlayerPopupData(Long playerid);
  ArrayList<String> endRound(ArrayList<Long> matchIDs);
  ArrayList<String> endStage(Long compID);
 LoginInfo login(String requestUri);
  LoginInfo createAccount(String emailAddress, String nickName, String password, boolean isGoogle, boolean isFacebook);
  LoginInfo nativeLogin(String emailAddress, String password);
  LoginInfo logOff(LoginInfo session);
  Long submitDraftTeam(MyGroup my, selectionType type, Long compID);
  ArrayList<PlayerRowData> getMyGroup(stageType st, int round);
  Group updateGroupInfo(Long groupID, String info);
  Long doRandomDraft(stageType stage, Long compID);
  Boolean makeAdmin(String email, Boolean superAdmin);
  String updateRoundScores(Integer round);
  Boolean setContent(Long id, String text);
  String getContent(Long id);
  Feature getCurrentlyFeaturedGroup();
  Boolean addFeature(Feature feature);
  ArrayList<ClubhouseMembership> getLeaderBoard(Long leagueID);
  IClubhouse createClubhouse(IClubhouse league);
  String joinLeague(Long leagueID);
  Group getGroup(Long groupID);
  String getNickname(Long appUserID);
  IClubhouse getMyClubhouse(Long id);
  Boolean setupKnockOut();
  ICompetition getCompetitionByShortName(String shortName);
  Boolean copyLastRoundsPick();
  String sendEmails(Long compID);
}
