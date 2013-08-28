package net.rugby.foundation.server;

import net.rugby.foundation.client.PlayersService;
import net.rugby.foundation.model.shared.AppUser;
import net.rugby.foundation.model.shared.CoreConfiguration;
import net.rugby.foundation.model.shared.CoreConfiguration.selectionType;
import net.rugby.foundation.model.shared.DraftWizardState;
import net.rugby.foundation.model.shared.Group;
import net.rugby.foundation.model.shared.LoginInfo;
import net.rugby.foundation.model.shared.ManagementEngine;
import net.rugby.foundation.model.shared.ManagementEngineFactory;
import net.rugby.foundation.model.shared.MatchGroup;
//import net.rugby.foundation.model.shared.PlayerPopupData.MatchPopupData;
import net.rugby.foundation.model.shared.Competition;
import net.rugby.foundation.model.shared.CompetitionTeam;
import net.rugby.foundation.model.shared.Content;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.Feature;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICompetitionTeam;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.IGroup;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchRating;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.Clubhouse;
import net.rugby.foundation.model.shared.ClubhouseMembership;
import net.rugby.foundation.model.shared.MatchPopupData;
import net.rugby.foundation.model.shared.MatchRating;
import net.rugby.foundation.model.shared.MyGroup;
import net.rugby.foundation.model.shared.Player;
import net.rugby.foundation.model.shared.PlayerPopupData;
import net.rugby.foundation.model.shared.PlayerRowData;
import net.rugby.foundation.model.shared.PoolDraftManagementEngine;
import net.rugby.foundation.model.shared.Group.GroupType;
import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.model.shared.PositionGroup;
import net.rugby.foundation.model.shared.Round;
import net.rugby.foundation.model.shared.Stage.stageType;
import net.rugby.foundation.model.shared.TeamGroup;
import net.rugby.foundation.model.shared.TeamMembership;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;


public class PlayersServiceImpl extends RemoteServiceServlet implements
    PlayersService {

	private static final long serialVersionUID = 1L;
	private static HashMap<Long,TeamGroup> teamCache = new HashMap<Long,TeamGroup>();
	
	private Objectify ofy;
  

	
  private final boolean initialize = false;
  
  public PlayersServiceImpl() {
	  
	  ofy = DataStoreFactory.getOfy();

	if (initialize) {
		//initPositions();
	   // initPlayers();
	    initTeams();
	}
	
	Query<Group> qt = ofy.query(Group.class).filter("groupType", "TEAM");
	for (IGroup t : qt) {
		if (t instanceof TeamGroup)
			teamCache.put(t.getId(),(TeamGroup)t);
	}
			
  }
  
  private void initTeams() {
	  
//
//	Iterable<Key<MatchRating>> allPKeys = ofy.query(MatchRating.class).fetchKeys();
//	// deleting old  items
//	ofy.delete(allPKeys);
	
//	  
//	Iterable<Key<Group>> allKeys = ofy.query(Group.class).fetchKeys();
//	// deleting old  items
//	ofy.delete(allKeys);
//	
//	ArrayList<Group> glist = new ArrayList<Group>();
//	  
//	TeamGroup nz = new TeamGroup();
//	nz.setDisplayName("New Zealand");
////	nz.setMembers(nzPlayers);
//	nz.setPool("A");
//	nz.setAbbr("NZL");
//	glist.add(nz);
//	
//	TeamGroup au = new TeamGroup();
//	au.setDisplayName("Australia");
////	au.setMembers(auPlayers);
//	au.setPool("C");
//	au.setAbbr("AUS");
//	glist.add(au);	
//	
//	TeamGroup sa = new TeamGroup();
//	sa.setDisplayName("South Africa");
////	sa.setMembers(saPlayers);
//	sa.setPool("D");
//	sa.setAbbr("RSA");
//	glist.add(sa);	
//	
//
//
//	// Position groups
//	//ArrayList<Position> positionArrayList = new ArrayList<Position>();
//	  for (position p : position.values())
//	  {
//		  if (p != position.NONE) {
//			  Position pos = new PositionEnUs();
//			  pos.setPrimary(p);
//	
//			  PositionGroup g = new PositionGroup();
//			  g.setPosition(p);
//			  g.setDisplayName(pos.getName(p));
//			  glist.add(g);
//		  }
//	  }
//	  
//	ofy.put(glist);
//	
//	glist.clear();
//	Key<Group> nzKey = new Key<Group>(Group.class,nz.getId());
//	Key<Group> auKey = new Key<Group>(Group.class,au.getId());
//	Key<Group> saKey = new Key<Group>(Group.class,sa.getId());
//	
//	MatchGroup m1 = new MatchGroup();
//	m1.setHomeTeam(nzKey);
//	m1.setVisitingTeam(auKey);
//	m1.setDisplayName(nz, au);
//	glist.add(m1);
//	
//	MatchGroup m2 = new MatchGroup();
//	m2.setHomeTeam(auKey);
//	m2.setVisitingTeam(saKey);
//	m2.setDisplayName(au, sa);
//	glist.add(m2);
//
//	MatchGroup m3 = new MatchGroup();
//	m3.setHomeTeam(saKey);
//	m3.setVisitingTeam(nzKey);
//	m3.setDisplayName(sa, nz);
//	glist.add(m3);
//	
//	ofy.put(glist);
//	
//
//	Key<Group> nzKey = new Key<Group>(Group.class,12L);
//	Key<Group> auKey = new Key<Group>(Group.class,2L);
//	Key<Group> saKey = new Key<Group>(Group.class,17L);
//
//	Player nz1 = new Player(null, 12L, "New Zealand", "NZL", "A", "Richie", "McCaw", "Richie McCaw", position.FLANKER, null, new Date("1/1/1980"), 190, 98, 93, "home club", "He's a good guy...", "http://wikipedia.com", "Wikipedia", false, false, "http://news.com", "http://photos.com/", "http://video.com/", 220L, 220L, 220L, 220L, 220L);
//	Player nz2 = new Player(null, 12L, "New Zealand", "NZL","A", "Dan", "Carter", "Dan Carter", position.FLYHALF, null, new Date("1/1/1982"), 180, 92, 86, "home club", "He's a good guy...", "http://wikipedia.com", "Wikipedia", false, false, "http://news.com", "http://photos.com/", "http://video.com/", 220L, 220L, 220L, 220L, 220L);
//	Player nz3 = new Player(null, 12L, "New Zealand", "NZL","A", "Ma'a", "Nonu", "Ma'a Nonu", position.CENTER, null, new Date("1/1/1981"), 180, 95, 62, "home club", "He's a good guy...", "http://wikipedia.com", "Wikipedia", false, false, "http://news.com", "http://photos.com/", "http://video.com/", 220L, 220L, 220L, 220L, 220L);
//	
//	Player au1 = new Player(null, 2L, "Australia", "AUS","C", "Quade", "Cooper", "Quade Cooper", position.FLYHALF, null, new Date("1/1/1980"), 180, 91, 45, "home club", "He's a good guy...", "http://wikipedia.com", "Wikipedia", false, false, "http://news.com", "http://photos.com/", "http://video.com/", 210L, 220L, 220L, 230L, 220L);
//	Player au2 = new Player(null, 2L, "Australia", "AUS","C", "Rocky", "Elsom", "Rocky Elsom", position.FLANKER, null, new Date("1/1/1977"), 210, 101, 87, "home club", "He's a good guy...", "http://wikipedia.com", "Wikipedia", false, false, "http://news.com", "http://photos.com/", "http://video.com/", 210L, 220L, 220L, 230L, 220L);
//	Player au3 = new Player(null, 2L, "Australia", "AUS","C", "Kurtley", "Beale", "Kurtley Beale", position.FULLBACK, null, new Date("1/1/1981"), 180, 95, 93, "home club", "He's a good guy...", "http://wikipedia.com", "Wikipedia", false, false, "http://news.com", "http://photos.com/", "http://video.com/", 210L, 220L, 220L, 230L, 220L);
//
//	Player sa1 = new Player(null, 17L, "South Africa", "RSA","D", "Victor", "Matfield", "Victor Matfield", position.LOCK, null, new Date("1/1/1980"), 180, 91, 45, "home club", "He's a good guy...", "http://wikipedia.com", "Wikipedia", false, false, "http://news.com", "http://photos.com/", "http://video.com/", 200L, 220L, 220L, 200L, 220L);
//	Player sa2 = new Player(null, 17L, "South Africa", "RSA","D", "Percy", "Montgomery", "Percy Montgomery", position.FULLBACK, null, new Date("1/1/1977"), 210, 101, 87, "home club", "He's a good guy...", "http://wikipedia.com", "Wikipedia", false, false, "http://news.com", "http://photos.com/", "http://video.com/", 200L, 220L, 220L, 200L, 220L);
//	Player sa3 = new Player(null, 17L, "South Africa", "RSA","D", "Shalk", "Burger", "Shalk Burger", position.FLANKER, null, new Date("1/1/1981"), 180, 95, 93, "home club", "He's a good guy...", "http://wikipedia.com", "Wikipedia", false, false, "http://news.com", "http://photos.com/", "http://video.com/", 200L, 220L, 220L, 200L, 220L);
//
//	ofy.put(nz1,nz2,nz3,au1,au2,au3,sa1,sa2,sa3);
	  
	  
}

  
  public Player addPlayer(Player player, Long teamID) { 
	  
    ofy.put(player);
    
    //@TODO should be a transaction
    TeamMembership tm = new TeamMembership();
    tm.setStart(new Date());
    tm.setPlayerID(player.getId());
    tm.setTeamID(teamID);
    tm.setActive(true);
    ofy.put(tm);
    
    return player;
  }

  public Player updatePlayer(Player player) {
	    ofy.put(player);
	    return player;
  }

  public Group updateGroupInfo(Long id, String info) {
	    Group g = ofy.get(Group.class,id);
	    
	    if (g != null) {
	    	g.setGroupInfo(info);
	    	ofy.put(g);
	    	return g;
	    }
	    return null;

}
  
  public Boolean deletePlayer(Long id) {
	  ofy.delete(id);
    //Players.remove(id);
    return true;
  }
  
  public ArrayList<PlayerRowData> deletePlayers(ArrayList<Long> ids) {

    for (int i = 0; i < ids.size(); ++i) {
      deletePlayer(ids.get(i));
    }
    
    //TODO what are we returning here?
    return getPlayerRowData();
  }
  
  //don't call this.
  public ArrayList<PlayerRowData> getPlayerRowData() {
    ArrayList<PlayerRowData> PlayerRowData = new ArrayList<PlayerRowData>();
    
//    Iterator<Long> it = Players.keySet().iterator();
//    while(it.hasNext()) { 
//      Player Player = Players.get(it.next());          
//      PlayerRowData.add(Player.getPlayerRowData());
//    }
   
    	
    return PlayerRowData;
  }

  
  public ArrayList<PlayerRowData> getPlayerRowDataByGroup(Long groupID, boolean poolMatch) {
	    ArrayList<PlayerRowData> PlayerRowData = new ArrayList<PlayerRowData>();
	    
	    IGroup group = ofy.get(Group.class, groupID);
	    if (group instanceof PositionGroup) {
	    	// we build the member list on the fly - though we could maintain them also?
	    	position pos = ((PositionGroup) group).getPosition();
	    	Query<Player> q = null;
	    	if (!poolMatch)
	    		q = ofy.query(Player.class).filter("position",pos.name()).order("-overallRating");
	    	else
	    		q = ofy.query(Player.class).filter("position",pos.name()).order("-poolStageRating");
	    		
	    	for (Player p : q)
	    	{
	    		//@REX for now assume only one team per player - not valid when domestic comps start
	    		TeamGroup team = getPlayersTeam(p.getId());
	    		PlayerRowData.add(p.getPlayerRowData(team));
	    	}
	    	
	    }
	    else if (group instanceof MatchGroup) {
	    	// we build the member list on the fly - though we could maintain them also?
	    	IGroup home = ofy.get(new Key<Group>(Group.class,((IMatchGroup)group).getHomeTeam().getId()));
	    	IGroup visit = ofy.get(new Key<Group>(Group.class,((IMatchGroup)group).getVisitingTeam().getId()));
	    	
//	    	ArrayList<Long>ids = new ArrayList<Long>();
//	    	ids.add(home.getId());
//	    	ids.add(visit.getId());
	    	
	    	// build the lists of playerIDs
	    	Query<TeamMembership> homeqtm = ofy.query(TeamMembership.class).filter("teamID",home.getId());
	    	ArrayList<Player> homePlayers = new ArrayList<Player>();
	    	for (TeamMembership tm : homeqtm) {
	    		homePlayers.add(ofy.get(new Key<Player>(Player.class,tm.getPlayerID())));
	    	}
	    	
	    	Query<TeamMembership> visitqtm = ofy.query(TeamMembership.class).filter("teamID",visit.getId());
	    	ArrayList<Player> visitPlayers = new ArrayList<Player>();
	    	for (TeamMembership tm : visitqtm) {
	    		visitPlayers.add(ofy.get(new Key<Player>(Player.class,tm.getPlayerID())));
	    	}
	    	
	    	Query<MatchRating> qr = ofy.query(MatchRating.class).filter("matchID",groupID);
	    	ArrayList<MatchRating> mrList = (ArrayList<MatchRating>) qr.list();
	    	boolean firstIn = true;
	    	for (Player p : homePlayers)
	    	{
	    		//@TODO is is quicker to do a linear search on the result set or do individual DB queries?
	    		Iterator<MatchRating> it = mrList.iterator();
	    		boolean found = false;
	    		while (it.hasNext() && !found) {
	    			IMatchRating r = it.next();
	    			if (r.getPlayerID().equals(p.getId()) ) {
	    				found = true;
	    				if (poolMatch) 
		    				p.setPoolStageRating(r.getPlayerRating());
	    				else
		    				p.setOverallRating(r.getPlayerRating());
	    			}
	    		}
	    		
	    		//TeamGroup team = getPlayersTeam(p.getId());
	    		PlayerRowData prd = p.getPlayerRowData((TeamGroup)home);

	    		if (firstIn) {  // add the first one in to start the sorting
	    			PlayerRowData.add(prd);
	    			firstIn = false;
	    		} else {
		    		//insert in descending order
		    		Iterator<PlayerRowData> prdit = PlayerRowData.iterator();
		    		boolean inserted = false;
		    		int index = 0;
		    		Long sortOn = prd.getOverallRating();
		    		if (poolMatch)
		    			sortOn = prd.getPoolRating(); 
		    		while (prdit.hasNext() && !inserted) {
		    			Long checkAt; 
		    			if (!poolMatch)
		    				checkAt = prdit.next().getOverallRating(); 
		    			else
		    				checkAt = prdit.next().getPoolRating(); 		    				
		    			if (sortOn > checkAt) {
		    				PlayerRowData.add(index,prd);
		    				inserted = true;
		    			}

		    			index++;
		    		}
		    		
		    		if (!inserted) { //tack it on the end
		    			PlayerRowData.add(index,prd);
		    		}
	    		}
	    	}
	    	for (Player p : visitPlayers)
	    	{
	    		//@TODO is is quicker to do a linear search on the result set or do individual DB queries?
	    		Iterator<MatchRating> it = mrList.iterator();
	    		boolean found = false;
	    		while (it.hasNext() && !found) {
	    			IMatchRating r = it.next();
	    			if (r.getPlayerID().equals(p.getId()) ) {
	    				found = true;
	    				if (poolMatch) 
		    				p.setPoolStageRating(r.getPlayerRating());
	    				else
		    				p.setOverallRating(r.getPlayerRating());
	    			}
	    		}
	    		
	    		PlayerRowData prd = p.getPlayerRowData((TeamGroup)visit);

	    		//insert in descending order
	    		Iterator<PlayerRowData> prdit = PlayerRowData.iterator();
	    		boolean inserted = false;
	    		int index = 0;
	    		Long sortOn = prd.getOverallRating();
	    		if (poolMatch)
	    			sortOn = prd.getPoolRating(); 
	    		while (prdit.hasNext() && !inserted) {
	    			Long checkAt; 
	    			if (!poolMatch)
	    				checkAt = prdit.next().getOverallRating(); 
	    			else
	    				checkAt = prdit.next().getPoolRating(); 		    				
	    			if (sortOn > checkAt) {
	    				PlayerRowData.add(index,prd);
	    				inserted = true;
	    			}

	    			index++;
	    		}
	    		
	    		if (!inserted) { //tack it on the end
	    			PlayerRowData.add(index,prd);
	    		}
	    		
	    	}
	    }
	    else if (group instanceof TeamGroup) { 
	    	// we build the member list on the fly - though we could maintain them also?
	    	//Key<Group> groupKey = new Key<Group>(Group.class,groupID);
	    	//@TODO So this isn't sorted by rating. Need to add them in ordered
	    	Query<TeamMembership> q = ofy.query(TeamMembership.class).filter("teamID",groupID);
	    			
	    	//ArrayList<Player> players = new ArrayList<Player>();
	    	boolean firstIn = false;
	    	for (TeamMembership tm : q)
	    	{
	    		Player p = ofy.get(new Key<Player>(Player.class,tm.getPlayerID()));
	    		if (!firstIn)  {
	    			PlayerRowData.add(p.getPlayerRowData(teamCache.get(tm.getTeamID())));
	    			firstIn = true;
	    		} else {
		    		//insert in descending order
		    		Iterator<PlayerRowData> prdit = PlayerRowData.iterator();
		    		boolean inserted = false;
		    		int index = 0;
		    		Long sortOn = p.getOverallRating();
		    		if (poolMatch)
		    			sortOn = p.getPoolStageRating();
		    		while (prdit.hasNext() && !inserted) {
		    			Long checkAt; 
		    			if (!poolMatch)
		    				checkAt = prdit.next().getOverallRating(); 
		    			else
		    				checkAt = prdit.next().getPoolRating(); 		    				
		    			if (sortOn > checkAt) {
		    				PlayerRowData.add(index,p.getPlayerRowData(teamCache.get(tm.getTeamID())));
		    				inserted = true;
		    			}

		    			index++;
		    		}
		    		
		    		if (!inserted) { //tack it on the end
		    			PlayerRowData.add(index,p.getPlayerRowData(teamCache.get(tm.getTeamID())));
		    		}
	    		}
	    	}
	    } else if (group instanceof MyGroup) { 	    	
	    	Map<Long, Player> players = ofy.get(Player.class,((MyGroup)group).getPlayerIds());
	    	
	    	Iterator<Player> ip = players.values().iterator();
	    	
	    	while (ip.hasNext()) {	    
	    		Player p = ip.next();
	    		TeamGroup team = getPlayersTeam(p.getId());
	    		PlayerRowData.add(p.getPlayerRowData(team));
	    	}

	    } else {
	    	throw new IllegalArgumentException("Bad group ID request in getPlayerRowDataByGroup: " + groupID);
	    }
	    
	    return PlayerRowData;
	  }


private TeamGroup getPlayersTeam(Long id) {
	TeamMembership tm = ofy.query(TeamMembership.class).filter("playerID", id).get();
	return teamCache.get(tm.getTeamID());
}

private TeamGroup getPlayersTeamInComp(Long playerID, Long compID) {
	
	//first check if team is in comp
	TeamMembership tm = ofy.query(TeamMembership.class).filter("playerID", playerID).get();
	ICompetitionTeam ct = ofy.query(CompetitionTeam.class).filter("teamID", tm.getTeamID()).filter("competitionID", compID).get();
	
	if (ct != null)
		return teamCache.get(tm.getTeamID());
	else
		return null; // the player's team isn't in the competition
}

@Override
  public Player getPlayer(Long id) {
	Key<Player> key = new Key<Player>(Player.class,id);
    return ofy.get(key);
  }

@Override
public String getGroupInfo(Long id) {
	Key<Group> key = new Key<Group>(Group.class,id);
	IGroup g = ofy.get(key);
	if (g != null) {
		if (g.getGroupInfo() != null)
			return g.getGroupInfo();
		else
			return "";
	}
	else {
		throw new InvalidParameterException();
	}
 }

@Override
public ArrayList<Group> getGroupsByGroupType(GroupType type) {
	ArrayList<Group> l = new ArrayList<Group>();
	Query<Group> q;
	if (type.equals(GroupType.MY)) {
		IAppUser u = getAppUser();
		if (u == null) {
			return l;
		}
		q = ofy.query(Group.class).filter("groupType", type.name()).filter("AppUserID", u.getId()).order("round");
	} else {
		q = ofy.query(Group.class).filter("groupType", type.name() );		
	}
		
	for (Group g: q) {
		l.add(g);
	}
	return l;
}

@Override
public PlayerPopupData getPlayerPopupData(Long playerid) {
	Key<Player> key = new Key<Player>(Player.class,playerid);
    Player p =  ofy.get(key);
    TeamGroup team = getPlayersTeam(playerid);
    PlayerPopupData ppd = p.getPlayerPopupData(team);
    
    ppd.setMatchData(getMatchPopupData(playerid));
    
    return ppd;
}


@Override
public LoginInfo login(String requestUri) {
//    UserService userService = UserServiceFactory.getUserService();
//    // this sees if they are a gmail user.
//    User user = userService.getCurrentUser();
    LoginInfo loginInfo = new LoginInfo();
    //Logger logger = Logger.getLogger("net.rugby.foundation.server");
    //logger.setLevel(Level.FINEST);

//    if (user != null) {
//    	Query<AppUser> q = ofy.query(AppUser.class).filter("emailAddress", user.getEmail() );
//    	if (q.count() > 0)   {
//	      loginInfo.setLoggedIn(true);
//	      loginInfo.setEmailAddress(user.getEmail());
//	      loginInfo.setNickname(user.getNickname());
//	      loginInfo.setLogoutUrl(userService.createLogoutURL(requestUri));
//	      loginInfo.setGoogle(true);
//	      loginInfo.setFacebook(false);
//	      //logger.log(Level.FINE,"Gmail user is signed up and logged on:" + user.getEmail());
//    	} else { // gmail user that hasn't signed up yet
//  	      loginInfo.setLoggedIn(false);
//  	      loginInfo.setEmailAddress(user.getEmail());
//  	      loginInfo.setNickname(user.getNickname());
//  	      // giving them a login url doesn't make sense yet
//  	      //loginInfo.setLoginUrl(userService.createLoginURL(requestUri));
//  	      loginInfo.setGoogle(true);
//  	      loginInfo.setFacebook(false);
//	      //logger.log(Level.FINE,"Gmail user is NOT signed up but is logged on:" + user.getEmail());
//
//   		
//    	}
//            
//    } else {
    	// do they have a session going?
    	HttpServletRequest request = this.getThreadLocalRequest();
    	HttpSession session = request.getSession();
    	IAppUser u = (IAppUser) session.getAttribute("AppUser");

    	if (u != null)
    	{
    		loginInfo = getLoginInfo(u);
    	} else 
    	{
	      loginInfo.setLoggedIn(false);
	      loginInfo.setLoginUrl("#Home:1+TEAM+0+0+LOGIN");
	      loginInfo.setSignupUrl("#Home:1+TEAM+0+0+CREATE");
	      //logger.log(Level.FINE,"User has no google session or system session");

    	}

    return loginInfo;
  }


@Override
public LoginInfo createAccount(String emailAddress, String nickName, String password, boolean isGoogle, boolean isFacebook) {

	LoginInfo info = new LoginInfo();
	info.setLoggedIn(false);
	
	boolean error = false;
	String hash = "";
	//valid email address?
	Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
	Matcher m = p.matcher(emailAddress);
	boolean matchFound = m.matches();
	if (!matchFound) {
		info.setStatus(CoreConfiguration.getCreateacctErrorInvalidEmail());
	} else {
		if (!isGoogle && !isFacebook) {
			//password length
			if (password.length() < 5) {
				info.setStatus(CoreConfiguration.getCreateacctErrorPasswordTooShort());
				error = true;
			} else {
			
				//create pw hash
				hash = DigestUtils.md5Hex(password);
			}
		}
		
		if (error != true) {
			//is this already in use?
			Query<AppUser> q = ofy.query(AppUser.class).filter("emailAddress", emailAddress);
			if (q.count() > 0) {
				info.setStatus(CoreConfiguration.getCreateacctErrorExists());
			} else {
				q = ofy.query(AppUser.class).filter("nickname", nickName );
				if (q.count() > 0)
					info.setStatus(CoreConfiguration.getCreateacctErrorNicknameExists());
				else {
				
					AppUser u = new AppUser(emailAddress, nickName, hash, true, false, false, isGoogle, isFacebook, true, null);
					ofy.put(u);
					
					HttpServletRequest request = this.getThreadLocalRequest();
					HttpSession session = request.getSession();
					session.setAttribute("AppUser", u);
					info = getLoginInfo(u);
				}
			}
		}
	}
	
	sendAdminEmail("New Fantasy Rugby user", info.getEmailAddress() + "\n" + info.getNickname());
	return info;
	
}

@Override
public LoginInfo nativeLogin(String emailAddress, String password) {
    LoginInfo loginInfo = new LoginInfo();

	Query<AppUser> q = ofy.query(AppUser.class).filter("emailAddress", emailAddress );
	if (q.count() == 0) {
		return loginInfo; //empty
	}
	
	IAppUser u = q.get();
	String hash = DigestUtils.md5Hex(password);
	if (!u.getPwHash().equals(hash))  {
		return loginInfo; //empty		
	}
		
	HttpServletRequest request = this.getThreadLocalRequest();
	HttpSession session = request.getSession();
	session.setAttribute("AppUser", u);	

	return getLoginInfo(u);
	
}

@Override
public LoginInfo logOff(LoginInfo info) {
	HttpServletRequest request = this.getThreadLocalRequest();
	HttpSession session = request.getSession();
	session.setAttribute("AppUser", null);	
	if (info == null)
		info = new LoginInfo();
	info.setLoggedIn(false);
	return info;
}

@Override
public Long submitDraftTeam(MyGroup my, net.rugby.foundation.model.shared.CoreConfiguration.selectionType type, Long compID) {

	IAppUser u = getAppUser();
	if (u == null) {
		return 0L;
	}
	
	// run the squad through the validator.
	ManagementEngine<PlayerRowData,DraftWizardState> managementEngine = new PoolDraftManagementEngine();
	
	if (type == selectionType.POOLROSTER) {
		managementEngine = ManagementEngineFactory.getManagementEngine(type, CoreConfiguration.getMaxperteamPoolRoster(), CoreConfiguration.getDraftsizePoolRoster(), CoreConfiguration.getCurrentround(), CoreConfiguration.getMaxpointsPoolRoster(), getGroupsByGroupTypeByComp(GroupType.TEAM, compID));
	} else if (type == selectionType.POOLROUND) {
		managementEngine = ManagementEngineFactory.getManagementEngine(type, CoreConfiguration.getMaxperteamPoolRound(), CoreConfiguration.getDraftsizePoolRound(), CoreConfiguration.getCurrentround(), CoreConfiguration.getMaxpointsPoolRound(), getGroupsByGroupTypeByComp(GroupType.TEAM, compID));
	} else if (type == selectionType.KNOCKOUTROSTER) {
		managementEngine = ManagementEngineFactory.getManagementEngine(type, CoreConfiguration.getMaxperteamKnockoutRoster(), CoreConfiguration.getDraftsizeKnockoutRoster(), CoreConfiguration.getCurrentround(), CoreConfiguration.getMaxpointsKnockoutRoster(), getGroupsByGroupTypeByComp(GroupType.TEAM, compID));
	} else if (type == selectionType.KNOCKOUTROUND) {
		managementEngine = ManagementEngineFactory.getManagementEngine(type, CoreConfiguration.getMaxperteamKnockoutRound(), CoreConfiguration.getDraftsizeKnockoutRound(), CoreConfiguration.getCurrentround(), CoreConfiguration.getMaxpointsKnockoutRound(), getGroupsByGroupTypeByComp(GroupType.TEAM, compID));
	}
	// cycle through the list they gave us and feed them into the new engine to see if they check out.
	List<Key<Player>> keys = new ArrayList<Key<Player>>();

	Iterator<Long> it = my.getPlayerIds().iterator();
	int count = 0;
	while (it.hasNext())	{
		keys.add(new Key<Player>(Player.class,it.next()));
		count++;
	}
	
	assert count==30;
	
	Map<Key<Player>,Player> fetched = ofy.get(keys);
	
	Iterator<Player> ip = fetched.values().iterator();
	while (ip.hasNext()) {
		Player player= ip.next();
	    TeamGroup team = getPlayersTeam(player.getId());

		PlayerRowData p = player.getPlayerRowData(team);
		
		if (managementEngine.canAdd(p)) {
			managementEngine.add(p);
		} else {
			return 0L;
		}
	}
	
	my.setAppUserID(u.getId());
	
	ofy.put(my);
	
	return my.getId();
}


private IAppUser getAppUser()
{
	// do they have a session going?
	HttpServletRequest request = this.getThreadLocalRequest();
	HttpSession session = request.getSession();
	return (IAppUser) session.getAttribute("AppUser");
	
}

@Override
public ArrayList<PlayerRowData> getMyGroup(stageType st, int round) {
    ArrayList<PlayerRowData> PlayerRowData = new ArrayList<PlayerRowData>();
    
    IGroup group = ofy.query(Group.class).filter("stageT",st).filter("round",round).get();
    
    if (group != null) {
	    if (group instanceof MyGroup) {	    	
	    	Iterator<Long> ip = ((MyGroup)group).getPlayerIds().iterator();
	    	while (ip.hasNext()) {	  
	    		Long playerID = ip.next();
	    	    TeamGroup team = getPlayersTeam(playerID);
	    		PlayerRowData.add(ofy.get(new Key<Player>(Player.class,ip.next())).getPlayerRowData(team));
	    	}	    	
	    }
    }
	return PlayerRowData;
}

private LoginInfo getLoginInfo(IAppUser u) {
	LoginInfo loginInfo = new LoginInfo();
	loginInfo.setLoggedIn(true);
    loginInfo.setEmailAddress(u.getEmailAddress());
    loginInfo.setNickname(u.getNickname());
    loginInfo.setLogoutUrl("");    	
    loginInfo.setIsOpenId(false);
    loginInfo.setFacebook(false);
    loginInfo.setAdmin(u.isAdmin());
    // see if they have done the draft and round picks yet.
    ArrayList<Group> groups = getGroupsByGroupType(GroupType.MY);
	  for (int i=0; i<10; i++) {
		loginInfo.getRoundsComplete().add(new Boolean(false));
	  }
    if (!groups.isEmpty()) {
  	  for (IGroup g : groups) {
  		  //@TODO grep 5 fix
  		  if (((MyGroup)g).getRound() == 5L)
  		  	loginInfo.setDraftID(g.getId());
  		  loginInfo.getTeamIDs().add(g.getId());
  		  loginInfo.getRoundsComplete().set(((MyGroup)g).getRound(), true);
  	  }
    }
    
    return loginInfo;
}

@Override
public Long doRandomDraft(stageType stage, Long compID) {
	IAppUser u = getAppUser();
	if (u == null) {
		return null;
	}
	
	Long groupID = null;
	boolean redraft = true;
	int redraftTries = 0;
	int maxPerTeam;
	if (stage == stageType.POOL) {
		maxPerTeam = CoreConfiguration.getMaxperteamPoolRoster();
	} else if (stage == stageType.KNOCKOUT) {
		maxPerTeam = CoreConfiguration.getMaxperteamKnockoutRoster();
	} else 
		return groupID;  // bad argument	
	
	int draftSize =0;
	if (stage == stageType.POOL) {
		draftSize = CoreConfiguration.getDraftsizePoolRoster();
	} else if (stage == stageType.KNOCKOUT) {
		draftSize = CoreConfiguration.getDraftsizeKnockoutRoster();
	} 
	
	ManagementEngine<PlayerRowData,DraftWizardState> managementEngine = new PoolDraftManagementEngine();
	
	while (redraft && redraftTries < 10)
	{	
			
		if (stage == stageType.POOL) {
			managementEngine = ManagementEngineFactory.getManagementEngine(selectionType.POOLROSTER, maxPerTeam, draftSize, CoreConfiguration.getCurrentround(), CoreConfiguration.getMaxpointsPoolRoster(), getGroupsByGroupTypeByComp(GroupType.TEAM, compID));
		} else if (stage == stageType.KNOCKOUT) {
			managementEngine = ManagementEngineFactory.getManagementEngine(selectionType.KNOCKOUTROSTER, maxPerTeam, draftSize, CoreConfiguration.getCurrentround(), CoreConfiguration.getMaxpointsKnockoutRoster(), getGroupsByGroupTypeByComp(GroupType.TEAM, compID));
		} 
		
		// players selected need to average less that 500 so we keep track of how we are doing with that.
		Long surplus = 0L;
		// for each position
		for (int i=position.PROP.ordinal(); i<position.FULLBACK.ordinal()+1; ++i)
		{
			// find out how many we need
			position pos = position.getAt(i);
			int numNeeded = pos.getNumberRequired(stage, 0);
			
			getServletContext().log("Looking for " + pos.getName() + " with a surplus of " + surplus);
			
			// get the players for this position
			ArrayList<Player> players = new ArrayList<Player>();
	    	Query<Player> q = ofy.query(Player.class).filter("position",pos.name()).order("-overallRating");
	    	for (Player p : q)
	    	{
	    		//don't take inactive players
	    		if (p.isActive()) {
	    			// don't take players from teams not in the comp
	    			IGroup team = getPlayersTeamInComp(p.getId(),compID);
		    		if (team != null) {
			    		// don't take players from maxed teams
			    		if (managementEngine.getNumSelectedTeam(team.getId()) < maxPerTeam) {
			    			// if we have been picking expensive players we need to get back on track
			    			if (!(surplus < 0 && p.getOverallRating() > 480-surplus)) {
			    				players.add(p);
			    			}  
			    		}
		    		}
	    		}    		
	    	}
	    	
			// pick a random player and try to enter him
			boolean added = false;
			int attempts = 0;
	    	Player p1 = null;
			while (added == false && attempts < 10)  {
				// pick P1 randomly	    		    	
		    	int count = players.size();
		    	getServletContext().log("\tFirst Player Pick: Player mix of " + count + " on attempt number " + attempts);
		    	// pick a random number from 0-size
		    	int index = (int)(Math.random() * count);
		    	p1 = players.get(index);
		    	//try to add him; repeat as necessary or 10 times to fail.
	    	    TeamGroup team = getPlayersTeam(p1.getId());
		    	PlayerRowData prd = p1.getPlayerRowData(team);
		    	if (managementEngine.canAdd(prd)) {
		    		managementEngine.add(prd);
		    		added = true;
		    		// take the picked player out
		    		players.remove(index);
		    		surplus += 500-prd.getOverallRating();
		    		getServletContext().log("\t\tAdding " + prd.getDisplayName() + " at " + prd.getOverallRating());
		    	} else {
		    		attempts++;
		    	}
			}
			
			// tried 10 players and failed
			if (!added) 
				break;
			
			// now try to get the rest needed. Pick players with ratings that are 
			// less than 1000-maxFoundSoFar. So if we just picked a 700, the rest of our choices have to be less than 300.
			//int j=0;
			Long maxRating = p1.getOverallRating()+100;
			for (int numLeft=numNeeded-1;numLeft>0;--numLeft) {
				// remove the players who are rated above 1000 - P1.rating
				
				ArrayList<Player> copy = new ArrayList<Player>();
		    	getServletContext().log("\t2nd Player Pick: need " + numLeft + " with surplus of " + surplus);
	
				for (Player p : players) {
					if (!(surplus < 0 && p.getOverallRating() < 500+surplus)) {
						copy.add(p);
					}
					//j++;
				}
	
				players = copy;
				attempts = 0;
		    	p1 = null;
		    	added = false;
				while (added == false && attempts < 10)  {
	
					// pick P1 randomly	    		    	
			    	int count = players.size();
			    	if (count == 0) break;
			    	getServletContext().log("\t2nd Player Pick: Player mix of " + count + " on attempt number " + attempts);
			    	// pick a random number from 0-size
			    	int index = (int)(Math.random() * count);
			    	p1 = players.get(index);
			    	//try to add him; repeat as necessary or 10 times to fail.
		    	    TeamGroup team = getPlayersTeam(p1.getId());
			    	PlayerRowData prd = p1.getPlayerRowData(team);
			    	if (managementEngine.canAdd(prd)) {
			    		managementEngine.add(prd);
			    		added = true;
			    		if (prd.getOverallRating() > maxRating)
			    			maxRating = prd.getOverallRating();
			    		surplus += 500-prd.getOverallRating();
			    		// take the picked player out
			    		players.remove(index);
			    		getServletContext().log("\t\tAdding " + prd.getDisplayName() + " at " + prd.getOverallRating());
			    	} else {
			    		attempts++;
			    	}
				}
			}
	
		}
		if (managementEngine.getSelected().size() == draftSize) {
			redraft = false;
		}
		redraftTries++;
	}

	// so now we have a candidate list if we made it this far. Try to submit it.
	MyGroup group = new MyGroup();
	ArrayList<Long> pkeys = new ArrayList<Long>();
	
	// pull the selected players' ids out of the engine
	Iterator<PlayerRowData> it = managementEngine.getSelected().iterator();
	while (it.hasNext())
	{
		pkeys.add(it.next().getId());
	}
	group.setPlayerIds(pkeys);

	
	selectionType type;
	if (stage == stageType.POOL) {
			type = selectionType.POOLROSTER;
			group.setDisplayName("Pool Roster (Random)");
			group.setRound(0);

	} else {// KNOCKOUT 
			type = selectionType.KNOCKOUTROSTER;
			group.setDisplayName("Knockout Roster (Random)");
			group.setRound(5);
	}	
	
	group.setAppUserID(u.getId());
	groupID = submitDraftTeam(group,type, compID);
	
	return groupID;
}


private ArrayList<MatchPopupData> getMatchPopupData(Long playerid) {
	 ArrayList<MatchPopupData> retval = new  ArrayList<MatchPopupData>();
    Query<MatchRating> q = ofy.query(MatchRating.class).filter("playerID", playerid);
    
    for (IMatchRating mr : q) {
    	Key<Group> key = new Key<Group>(Group.class,mr.getMatchID());
    	IGroup match = ofy.get(key);
    	//@TODO implement match url 
    	retval.add(new MatchPopupData(match.getDisplayName(),"", mr.getPlayerRating(), 0L, 0L));
    }
	
	return retval;
}

@Override
public ArrayList<String> endRound(ArrayList<Long> matchIDs) {

	// admin only
	IAppUser u = getAppUser();
	if (u == null) {
		return null;
	}
	
	if(!u.isSuperadmin()) {
		return null;
	}
	
	BasicRoundEnderEngine e = new BasicRoundEnderEngine();

	// get the current competition teams
	ICompetition comp = getCompetitionByShortName(CoreConfiguration.getDefaultCompetitionShortName());
	Query<CompetitionTeam> qct = ofy.query(CompetitionTeam.class).filter("competitionID", comp.getId());
	
	ArrayList<Player> players = new ArrayList<Player>();

// getting rid of ICT DPT 7/31/31
//	for(ICompetitionTeam ct : qct) {
//		Query<TeamMembership> qtm = ofy.query(TeamMembership.class).filter("teamID", ct.getTeamID());
//		for (TeamMembership tm : qtm) {
//			Key<Player> pKey = new Key<Player>(Player.class,tm.getPlayerID());
//			Player p = ofy.get(pKey);
//			if (p.isActive()) {
//				players.add(p);
//				e.addRating(p.getId(), p.getOverallRating().floatValue());
//			}
//		}
//	}
	

	//@TODO hardcode 6
	Query<MatchRating> qr = ofy.query(MatchRating.class).filter("round >", 5);
	for (IMatchRating r : qr) {
		e.addRating(r.getPlayerID(), r.getPlayerRating().floatValue());
	}
	
	e.calculate();
	
	Map<Long, Float> results = e.getRatings();
	
	Float maxRating = 0F;
	Float minRating = 5000F;
	Long maxID =0L;
	Long minID = 0L;
 	for (Long playerID : results.keySet()) {
 		if (results.get(playerID) > maxRating) {
 			maxRating = results.get(playerID);
 			maxID = playerID;
 		}
 		if (results.get(playerID) <minRating) {
 			minRating = results.get(playerID);
 			minID = playerID;
 		}
 			
 	}
	ArrayList<String>retval = new ArrayList<String>();
	retval.add("Max value is " + maxRating + " for ID " + maxID);
	retval.add("Min value is " + minRating + " for ID " + minID);
	
	ArrayList<Player> newPlayers = new ArrayList<Player>();
	for (Player np : players) {
		Player newPlayer = np;
		newPlayers.add(newPlayer);
		newPlayer.setLastOverallRating(newPlayer.getOverallRating());
		newPlayer.setOverallRating(results.get(newPlayer.getId()).longValue());
	}
	
	ofy.put(newPlayers);
	return retval;
}

@Override
public ArrayList<String> endStage(Long compID) {

	// admin only
	IAppUser u = getAppUser();
	if (u == null) {
		return null;
	}
	
	
	if(!u.isSuperadmin()) {
		return null;
	}
	
	if (compID == 0L) {
		compID = getCompetitionByShortName(CoreConfiguration.getDefaultCompetitionShortName()).getId();
	}
	
	return Utility.getInstance().setupFinals(ofy);
	
//	BasicRoundEnderEngine e = new BasicRoundEnderEngine();
//
//	Query<Player> q = ofy.query(Player.class);
//	ArrayList<Player> shortList = new ArrayList<Player>();
//	for (Player p : q) {
//		if (getPlayersTeamInComp(p.getId(),compID) != null)
//			if (p.isActive()) {
//				if (p.getOverallRating() < 150L) {
//					p.setOverallRating(150L);
//				}
//				//cleaning up last rating error in knockout round
//				//e.addRating(p.getId(), p.getOverallRating().floatValue());
//				e.addRating(p.getId(), p.getPoolStageRating().floatValue());
//				shortList.add(p);
//			}
//	}
//	
//	e.calculate();
//	
//	Map<Long, Float> results = e.getRatings();
//	
//	Float maxRating = 0F;
//	Float minRating = 5000F;
//	Long maxID =0L;
//	Long minID = 0L;
// 	for (Long playerID : results.keySet()) {
// 		if (results.get(playerID) > maxRating) {
// 			maxRating = results.get(playerID);
// 			maxID = playerID;
// 		}
// 		if (results.get(playerID) <minRating) {
// 			minRating = results.get(playerID);
// 			minID = playerID;
// 		}
// 			
// 	}
//	ArrayList<String>retval = new ArrayList<String>();
//	retval.add("Max value is " + maxRating + " for ID " + maxID);
//	retval.add("Min value is " + minRating + " for ID " + minID);
//	
//	ArrayList<Player> newPlayers = new ArrayList<Player>();
//	for (Player np : shortList) {
//		Player newPlayer = np;
//		newPlayers.add(newPlayer);
////		newPlayer.setLastOverallRating(newPlayer.getOverallRating());
////		newPlayer.setOverallRating(results.get(newPlayer.getId()).longValue());
//		
//		//cleaning up mess
//		newPlayer.setLastOverallRating(results.get(newPlayer.getId()).longValue());
//	}
//	
//	ofy.put(newPlayers);
//	return retval;
}

@Override
public Boolean makeAdmin(String email, Boolean superAdmin) {
	
	// admin only
	IAppUser u = getAppUser();
	if (u == null) {
		return false;
	}
	
	if(!u.isSuperadmin()) {
		return false;
	}
	
	Query<AppUser> q = ofy.query(AppUser.class).filter("emailAddress", email);
	
	AppUser newAdmin = q.get();
	if (newAdmin != null) {
		if (superAdmin)
			newAdmin.setSuperadmin(true);
		else
			newAdmin.setAdmin(true);
			
		ofy.put(newAdmin);
		return true;
	}
			
	return false;
}

@Override
public String updateRoundScores(Integer round) {
	
	// admin only
	IAppUser u = getAppUser();
	if (u == null) {
		return "No access";
	}
	
	if(!u.isSuperadmin()) {
		return "No access";
	}
	
	HashMap<Long,Group> roundPicks = new HashMap<Long,Group>();
	HashMap<Long,Long> roundScores = new HashMap<Long,Long>();
	
	String retval = "";
	// if people are active and they haven't picked a round side yet and have picked on in the past then 
	// copy that round side forward into the current round.
	copyLastRoundsPick();
	
	Query<Group> qg = ofy.query(Group.class).filter("groupType", "MY").filter("round", CoreConfiguration.getCurrentround()-1);	
	Long winningScore = 0L;
	// update the scores for the sides
	for (Group g : qg) {
		if (g instanceof MyGroup) {
			// total up their score
			Long teamScore = 0L;
			for (Long playerID : (((MyGroup)g).getPlayerIds())) {
		    	Key<Player> key = new Key<Player>(Player.class,playerID);
		    	teamScore += ofy.get(key).getOverallRating();
		    	g.setGroupInfo("<strong>Team score: " + teamScore + "</strong>");
		    	ofy.put(g);
		    	roundPicks.put(((MyGroup) g).getAppUserID(),g);
		    	roundScores.put(((MyGroup) g).getAppUserID(),teamScore);
		    	if (teamScore >= winningScore) {
		    		if (teamScore > winningScore) {
		    			retval = "Winning score: " + teamScore + "<br>" + "Winning user: " + g.getDisplayName() + " (" + g.getId() + ")";
		    			winningScore = teamScore;
		    		} else { // multiple winners
		    			retval += "Winning score: " + teamScore + "<br>" + "Winning user: " + g.getDisplayName() + " (" + g.getId() + ")" + "<br>" ;		
		    		}
		    	}
			}
		}
	}
	
	//finally, update the league standings.
	Query<ClubhouseMembership> qlm = ofy.query(ClubhouseMembership.class);
	
	for (ClubhouseMembership lm : qlm) {
		if (roundPicks.containsKey(lm.getAppUserID())) {
			//lm.setCurrentGroupID(roundPicks.get(lm.getAppUserID()).getId());
			//lm.setCurrentRoundScore(roundScores.get(lm.getAppUserID()));
			ofy.put(lm);
		}
	}
	
	
	return retval;
}

@Override
public Boolean setContent(Long id, String text) {
	// admin only
	IAppUser u = getAppUser();
	if (u == null) {
		return false;
	}
	
	if(!u.isAdmin()) {
		return false;
	}

	
	Content c = new Content(id,text);
	ofy.put(c);
	
	return true;
}

@Override
public String getContent(Long id) {
	Key<Content> key = new Key<Content>(Content.class,id);
	IContent c = ofy.get(key);
	if (c != null)
		return c.getBody();
	else
		return null;
}

@Override
public Feature getCurrentlyFeaturedGroup() {
	Query<Feature> qcf = ofy.query(Feature.class).filter("today", true);
	Feature curr = qcf.get();
	return curr;
}

@Override
public Boolean addFeature(Feature feature) {
	// admin only
	IAppUser u = getAppUser();
	if (u == null) {
		return false;
	}
	
	if(!u.isAdmin()) {
		return false;
	}
	//see if this is an update to an existing group
	Query<Feature> qEdit = ofy.query(Feature.class).filter("groupID", feature.getGroupID());
	Feature exists = qEdit.get();
	Boolean found = false;
	if (exists != null) {
		found = true;
		exists.setTitle(feature.getTitle());
		exists.setToday(feature.getToday());
		ofy.put(exists);
	}
	
	//only let there be one Current
	if (feature.getToday() && !found) {
		Query<Feature> qcf = ofy.query(Feature.class).filter("today", true);
		Feature curr = qcf.get();
		if (curr != null) {
			curr.setToday(false);
			ofy.put(curr);
		}
	}
	
	ofy.put(feature);
	return true;
}

@Override
public ArrayList<ClubhouseMembership> getLeaderBoard(Long leagueID) {
	IAppUser u = getAppUser();
	if (u == null) {
		return null;
	}	
	// for now punt on membership in multiple leagues.
	Query<ClubhouseMembership> qu = ofy.query(ClubhouseMembership.class).filter("appUserID", u.getId());
	
	// just take the first one they are a member of
	leagueID = qu.get().getClubhouseID();
	Query<ClubhouseMembership> q = ofy.query(ClubhouseMembership.class).filter("leagueID", leagueID).order("-currentRoundScore");

	ArrayList<ClubhouseMembership> list = new ArrayList<ClubhouseMembership>();
	for (ClubhouseMembership lm : q) {
		list.add(lm);
	}
	return list;
}

@Override
public IClubhouse createClubhouse(IClubhouse league) {
	IAppUser u = getAppUser();
	if (u == null) {
		return null;
	}
	
	league.setOwnerID(u.getId());
	HttpServletRequest req = this.getThreadLocalRequest();
	ofy.put(league); // store it to get the id set
	league.setJoinLink(req.getScheme()+"://"+req.getServerName()+"/#Home:" + league.getHomeID() + "+MY+" +league.getId()+"+0+JOIN");
	ofy.put(league); // store it again to add the join link
	joinLeague(league.getId());  // add the owner to the new league
	return league;
}

@Override
public String joinLeague(Long leagueID) {
	IAppUser u = getAppUser();
	if (u == null) {
		return "Not logged on";
	}

	// check if they are already a member of a league - currently only one membership allowed
	Query<ClubhouseMembership> q = ofy.query(ClubhouseMembership.class).filter("appUserID", u.getId()); //.filter("leagueID", leagueID);
	if (q.get() != null) {
		return "already a member of a league";
	}
	ClubhouseMembership lm = new ClubhouseMembership(); //u.getId(), leagueID, u.getNickname(), new Date(), 0L, 0L);
	
	ofy.put(lm);
	
	return "Success";
}

@Override
public Group getGroup(Long groupID) {
	Key<Group> key = new Key<Group>(Group.class,groupID);
	Group g = ofy.get(key);

	return g;
}

@Override
public String getNickname(Long appUserID) {
	Key<AppUser> key = new Key<AppUser>(AppUser.class,appUserID);
	IAppUser u = ofy.get(key);

	return u.getNickname();
}

@Override
public IClubhouse getMyClubhouse(Long id) {
	IAppUser u = getAppUser();
	if (u == null) {
		return null;
	}
	
	// if they pass in null, give them the best guess
	if (id == null) {
		Query<ClubhouseMembership> qlm = ofy.query(ClubhouseMembership.class).filter("appUserID", u.getId());
		if (qlm.count() > 0) {
			Long leagueID = qlm.get().getClubhouseID();
			
			Key<Clubhouse> key = new Key<Clubhouse>(Clubhouse.class,leagueID);
			
			return ofy.get(key);
		} else {
			return null;
		}
	} else { //give them what they ask for. 
		//@ REX should we check if they are a member first?
		Key<Clubhouse> key = new Key<Clubhouse>(Clubhouse.class,id);
		
		return ofy.get(key);
	}
}

@Override
public Boolean setupKnockOut() {

	Utility.getInstance().updateTeamMembership(ofy);
	Utility.getInstance().createCompetitions(ofy);

	//endStage(getCompetitionByShortName(Configuration.getDefaultCompetitionShortName()).getId());
	return true;
}

@Override
public ICompetition getCompetitionByShortName(String shortName) {
	ICompetition comp = ofy.query(Competition.class).filter("shortName", shortName).get();
	
	//@TODO take this out once we've upgraded
//	if (comp == null) {
//		setupKnockOut();
//		comp = ofy.query(Competition.class).filter("shortName", shortName).get();
//	}
	
	assert comp != null;
	
	return comp;
}

@Override
public ArrayList<Group> getGroupsByGroupTypeByComp(GroupType type, Long compID) {
	
	ArrayList<Group> l = new ArrayList<Group>();
	Query<Group> q = null;
	if (type.equals(GroupType.MY)) {
		IAppUser u = getAppUser();
		if (u == null) {
			return l;
		}
		q = ofy.query(Group.class).filter("groupType", type.name()).filter("AppUserID", u.getId()).order("round");
		for (Group g: q) {
			l.add(g);
		}
	} 
//	else if (type.equals(GroupType.TEAM)) {
//		Query<CompetitionTeam> ctq = ofy.query(CompetitionTeam.class).filter("competitionID",compID);
//		for( ICompetitionTeam ct : ctq) {
//			l.add(ofy.get(new Key<Group>(Group.class,ct.getTeamID())));	
//		}
//	} 
	else if (type.equals(GroupType.MATCH)) {
		// go through the rounds of the competition, appending the matches as we go.
		ICompetition comp = ofy.get(new Key<Competition>(Competition.class,compID));
		for (Long rid : comp.getRoundIds()) {
			IRound r = ofy.get(new Key<Round>(Round.class,rid));
			for (Long mid : r.getMatchIDs()) {
				l.add(ofy.get(new Key<MatchGroup>(MatchGroup.class,mid)));
			}
		}
	} else if (type.equals(GroupType.POSITION)) {
		return null; // doesn't really make sense - they all play in every comp. Filtering is done at team level
	}
		

	return l;
}

@Override
public ArrayList<PlayerRowData> getPlayerRowDataByPositionAndComp(Long groupID, Long compID, boolean showInactive, boolean isPool) {
    ArrayList<PlayerRowData> PlayerRowData = new ArrayList<PlayerRowData>();

    IGroup group = ofy.get(Group.class, groupID);
    if (group instanceof PositionGroup) {
    	// we build the member list on the fly - though we could maintain them also?
    	position pos = ((PositionGroup) group).getPosition();
    	//@TODO each comp should have its own rating so we don't have to do this
    	Query<Player> q = null;
    	if (!isPool)
    		q = ofy.query(Player.class).filter("position",pos.name()).order("-overallRating");
    	else
    		q = ofy.query(Player.class).filter("position",pos.name()).order("-poolStageRating");
    		
    	for (Player p : q)
    	{
    		//@REX for now assume only one team per player - not valid when domestic comps start
    		TeamGroup team = getPlayersTeamInComp(p.getId(), compID);
    		if (team != null) {
    			// !!!!
    			if (!(!showInactive && !p.isActive())) 
    				PlayerRowData.add(p.getPlayerRowData(team));
    		}
    	}
    	
    }
	return PlayerRowData;
}



private void sendAdminEmail(String subject, String message) {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);
    
    try {
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("dominic.tracey@gmail.com", "rugby.net "));
        msg.addRecipient(Message.RecipientType.TO,
                         new InternetAddress("dominic.tracey@gmail.com", "Dominic Tracey"));
        msg.setSubject(subject);
        msg.setText(message);
        Transport.send(msg);
        Logger.getLogger(PlayersServiceImpl.class.getName()).log(Level.INFO,"Sent mail to " + msg.getRecipients(RecipientType.TO)[0].toString());

    } catch (AddressException e) {
    	e.printStackTrace();
    } catch (MessagingException e) {
    	e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	}
}

@Override
public Boolean copyLastRoundsPick() {

	IAppUser au = getAppUser();
	if (au.isActive()) {
		Query<Group> gq = ofy.query(Group.class).filter("AppUserID", au.getId()).order("-round");
		IGroup latestPick = gq.get();
		if (latestPick != null) {
			if (latestPick instanceof MyGroup) {
				//@TODO Let's clean this up and get the Configuration's idea of currentRound lined up with the Round class instances 
				if (((MyGroup)latestPick).getRound() > 5) { // need for it to be non-draft
					if (((MyGroup)latestPick).getRound() < CoreConfiguration.getCurrentround()) {
						MyGroup letItRide = new MyGroup();
						letItRide.setAppUserID(((MyGroup) latestPick).getAppUserID());
						if (CoreConfiguration.getCurrentround() == 7)
							letItRide.setDisplayName("Semifinals");
						else	
							letItRide.setDisplayName("Finals");
						//letItRide.setMembers(latestPick.getMembers());
						letItRide.setPlayerIds(((MyGroup) latestPick).getPlayerIds());
						letItRide.setStageT(((MyGroup) latestPick).getStageT());
						letItRide.setRound(CoreConfiguration.getCurrentround());
						ofy.put(letItRide);
						return true;
					}
				}
			}
		}
	}
	return false;
}

@Override
public String sendEmails(Long compID) {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);
    
    try {
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("dominic.tracey@gmail.com", "rugby.net "));
        msg.addRecipient(Message.RecipientType.TO,
                         new InternetAddress("dominic.tracey@gmail.com", "Dominic Tracey"));
        msg.setSubject("Fantasy Rugby - RWC Quarterfinals Update");

        // This HTML mail has 2 parts, the BODY and the embedded image
        MimeMultipart multipart = new MimeMultipart();
        BodyPart messageBodyPart = new MimeBodyPart();
        String htmlText = "<html><img src=\"cid:image\"><table><tr><td>What a great weekend of rugby!<td><td>LEADERBOARD</td></tr></table>";
        messageBodyPart.setContent(htmlText, "text/html");
        // add it
        multipart.addBodyPart(messageBodyPart);
        
        // second part (the image)
        messageBodyPart = new MimeBodyPart();
        DataSource fds = new FileDataSource("/resources/images/bodyBg-update.gif");
        messageBodyPart.setDataHandler(new DataHandler(fds));
        messageBodyPart.setHeader("Content-ID","<image>");

        // add it
        multipart.addBodyPart(messageBodyPart);

        // put everything together
        msg.setContent(multipart);

        Transport.send(msg);
        getServletContext().log("Sent mail to " + msg.getRecipients(RecipientType.TO)[0].toString());

    } catch (AddressException e) {
    	e.printStackTrace();
    	return e.getMessage();
    } catch (MessagingException e) {
    	e.printStackTrace();
    	return e.getMessage();
    } catch (UnsupportedEncodingException e) {
		e.printStackTrace();
		return e.getMessage();
	}
    
    return "sent";
    		
}
}
