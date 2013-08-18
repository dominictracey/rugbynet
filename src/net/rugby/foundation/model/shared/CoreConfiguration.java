package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;

import net.rugby.foundation.model.shared.Stage.stageType;

@Entity
public class CoreConfiguration implements ICoreConfiguration, Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	
	@Transient
	// competition name map
	private Map<Long, String> compNameMap = null;
	
	private List<Long> compsUnderway = new ArrayList<Long>();
	
	// default compId
	private Long defaultCompId;
	
	// environments
	public enum Environment { LOCAL, DEV, BETA, PROD }
	private Environment environment;
	
	public enum selectionType { POOLROSTER, POOLROUND, KNOCKOUTROSTER, KNOCKOUTROUND }
	
	private static final Long MAXPOINTS_POOL_ROSTER = 15000L;
	private static final int MAXPERTEAM_POOL_ROSTER = 5;
	private static final int DRAFTSIZE_POOL_ROSTER = 30;
	private static final String WELCOMESTRING_POOL_ROSTER = "Start here";
	
	private static final Long MAXPOINTS_POOL_ROUND = 30000L;
	private static final int MAXPERTEAM_POOL_ROUND = 4;
	private static final int DRAFTSIZE_POOL_ROUND = 15;
	private static final String WELCOMESTRING_POOL_ROUND = "Start here";

	private static final Long MAXPOINTS_KNOCKOUT_ROSTER = 15000L;
	private static final int MAXPERTEAM_KNOCKOUT_ROSTER = 5;
	private static final int DRAFTSIZE_KNOCKOUT_ROSTER = 30;
	private static final String WELCOMESTRING_KNOCKOUT_ROSTER = "Start here";
	
	private static final Long MAXPOINTS_KNOCKOUT_ROUND = 30000L;
	private static final int MAXPERTEAM_KNOCKOUT_ROUND = 4;
	private static final int DRAFTSIZE_KNOCKOUT_ROUND = 15;
	private static final String WELCOMESTRING_KNOCKOUT_ROUND = "Start here";

	// number of players to draft
	private static final int NUM_PROPS_POOL_ROSTER = 4;
	private static final int NUM_PROPS_POOL_ROUND = 2;
	private static final int NUM_PROPS_KNOCKOUT_ROSTER = 4;
	private static final int NUM_PROPS_KNOCKOUT_ROUND = 2;

	private static final int NUM_HOOKERS_POOL_ROSTER = 2;
	private static final int NUM_HOOKERS_POOL_ROUND = 1;
	private static final int NUM_HOOKERS_KNOCKOUT_ROSTER = 2;
	private static final int NUM_HOOKERS_KNOCKOUT_ROUND = 1;

	private static final int NUM_LOCKS_POOL_ROSTER = 4;
	private static final int NUM_LOCKS_POOL_ROUND = 2;
	private static final int NUM_LOCKS_KNOCKOUT_ROSTER = 4;
	private static final int NUM_LOCKS_KNOCKOUT_ROUND = 2;

	private static final int NUM_FLANKERS_POOL_ROSTER = 4;
	private static final int NUM_FLANKERS_POOL_ROUND = 2;
	private static final int NUM_FLANKERS_KNOCKOUT_ROSTER = 4;
	private static final int NUM_FLANKERS_KNOCKOUT_ROUND = 2;

	private static final int NUM_NUMBER8S_POOL_ROSTER = 2;
	private static final int NUM_NUMBER8S_POOL_ROUND = 1;
	private static final int NUM_NUMBER8S_KNOCKOUT_ROSTER = 2;
	private static final int NUM_NUMBER8S_KNOCKOUT_ROUND = 1;

	private static final int NUM_SCRUMHALVES_POOL_ROSTER = 2;
	private static final int NUM_SCRUMHALVES_POOL_ROUND = 1;
	private static final int NUM_SCRUMHALVES_KNOCKOUT_ROSTER = 2;
	private static final int NUM_SCRUMHALVES_KNOCKOUT_ROUND = 1;

	private static final int NUM_FLYHALVES_POOL_ROSTER = 2;
	private static final int NUM_FLYHALVES_POOL_ROUND = 1;
	private static final int NUM_FLYHALVES_KNOCKOUT_ROSTER = 2;
	private static final int NUM_FLYHALVES_KNOCKOUT_ROUND = 1;

	private static final int NUM_CENTERS_POOL_ROSTER = 4;
	private static final int NUM_CENTERS_POOL_ROUND = 2;
	private static final int NUM_CENTERS_KNOCKOUT_ROSTER = 4;
	private static final int NUM_CENTERS_KNOCKOUT_ROUND = 2;

	private static final int NUM_WINGS_POOL_ROSTER = 4;
	private static final int NUM_WINGS_POOL_ROUND = 2;
	private static final int NUM_WINGS_KNOCKOUT_ROSTER = 4;
	private static final int NUM_WINGS_KNOCKOUT_ROUND = 2;

	private static final int NUM_FULLBACKS_POOL_ROSTER = 2;
	private static final int NUM_FULLBACKS_POOL_ROUND = 1;
	private static final int NUM_FULLBACKS_KNOCKOUT_ROSTER = 2;
	private static final int NUM_FULLBACKS_KNOCKOUT_ROUND = 1;

	private static final int CURRENTROUND = 8;
	private static final boolean LOCKED_DOWN = false;
	private static final stageType CURRENTSTAGE = stageType.KNOCKOUT;

	private static final int NUMBERTEAMS = 20;
	private static final int NUMBERPOSITIONS = 10;

	private static final Long NO_LOGIN_CONTENT_ID = 1L;
	private static final Long NO_DRAFT_CONTENT_ID = 2L;
	private static final Long NO_ROUND_CONTENT_ID = 3L;
	private static final Long COMPLETE_CONTENT_ID = 4L;

	//i18n?
	private final static String CREATEACCT_ERROR_EXISTS = "Error creating: Email in use"; 
	private final static String CREATEACCT_ERROR_PASSWORD_TOO_SHORT = "Error creating: Password must be at least 5 characters"; 
	private final static String CREATEACCT_ERROR_INVALID_EMAIL = "Error creating: Not a valid email address"; 
	private final static String CREATEACCT_ERROR__NICKNAME_EXISTS = "Error creating: That screen name is in use"; 
	private final static String CREATEACCT_ERROR__NICKNAME_CANT_BE_NULL = "Error: Screen name required";
	private final static String CREATEACCT_OK = "Congratulations - account created!";
	
	private static final String DEFAULT_COMPETITION_SHORT_NAME = "2011 RWC Knockout"; 
	
	// Facebook
	private final static String LOCAL_BASE_TOPTEN_URL = "http://127.0.0.1:8888/topten.html?gwt.codesvr=127.0.0.1:9997";
	private final static String DEV_BASE_TOPTEN_URL = "http://dev.rugby.net/topten.html";
	private final static String BETA_BASE_TOPTEN_URL = "http://beta.rugby.net/topten.html";
	private final static String PROD_BASE_TOPTEN_URL = "http://www.rugby.net/topten.html";
	
	private final static String FACEBOOK_APPID = "499268570161982";
//	private final static String FACEBOOK_APPSECRET = "c3550da86a7233c5398129a2b1317495";
	
	
	public CoreConfiguration() {
		compNameMap = new HashMap<Long, String>();
	}
	
	public static Long getMaxpointsPoolRoster() {
		return MAXPOINTS_POOL_ROSTER;
	}


	public static int getMaxperteamPoolRoster() {
		return MAXPERTEAM_POOL_ROSTER;
	}


	public static int getDraftsizePoolRoster() {
		return DRAFTSIZE_POOL_ROSTER;
	}


	public static String getWelcomestringPoolRoster() {
		return WELCOMESTRING_POOL_ROSTER;
	}


	public static Long getMaxpointsPoolRound() {
		return MAXPOINTS_POOL_ROUND;
	}


	public static int getMaxperteamPoolRound() {
		return MAXPERTEAM_POOL_ROUND;
	}


	public static int getDraftsizePoolRound() {
		return DRAFTSIZE_POOL_ROUND;
	}


	public static String getWelcomestringPoolRound() {
		return WELCOMESTRING_POOL_ROUND;
	}


	public static Long getMaxpointsKnockoutRoster() {
		return MAXPOINTS_KNOCKOUT_ROSTER;
	}


	public static int getMaxperteamKnockoutRoster() {
		return MAXPERTEAM_KNOCKOUT_ROSTER;
	}


	public static int getDraftsizeKnockoutRoster() {
		return DRAFTSIZE_KNOCKOUT_ROSTER;
	}


	public static String getWelcomestringKnockoutRoster() {
		return WELCOMESTRING_KNOCKOUT_ROSTER;
	}


	public static Long getMaxpointsKnockoutRound() {
		return MAXPOINTS_KNOCKOUT_ROUND;
	}


	public static int getMaxperteamKnockoutRound() {
		return MAXPERTEAM_KNOCKOUT_ROUND;
	}


	public static int getDraftsizeKnockoutRound() {
		return DRAFTSIZE_KNOCKOUT_ROUND;
	}


	public static String getWelcomestringKnockoutRound() {
		return WELCOMESTRING_KNOCKOUT_ROUND;
	}


	public static int getCurrentround() {
		return CURRENTROUND;
	}


	public static int getNumberteams() {
		return NUMBERTEAMS;
	}


	public static int getNumberpositions() {
		return NUMBERPOSITIONS;
	}


	public static int getNumPropsPoolRoster() {
		return NUM_PROPS_POOL_ROSTER;
	}


	public static int getNumPropsPoolRound() {
		return NUM_PROPS_POOL_ROUND;
	}


	public static int getNumPropsKnockoutRoster() {
		return NUM_PROPS_KNOCKOUT_ROSTER;
	}


	public static int getNumPropsKnockoutRound() {
		return NUM_PROPS_KNOCKOUT_ROUND;
	}


	public static int getNumHookersPoolRoster() {
		return NUM_HOOKERS_POOL_ROSTER;
	}


	public static int getNumHookersPoolRound() {
		return NUM_HOOKERS_POOL_ROUND;
	}


	public static int getNumHookersKnockoutRoster() {
		return NUM_HOOKERS_KNOCKOUT_ROSTER;
	}


	public static int getNumHookersKnockoutRound() {
		return NUM_HOOKERS_KNOCKOUT_ROUND;
	}


	public static int getNumLocksPoolRoster() {
		return NUM_LOCKS_POOL_ROSTER;
	}


	public static int getNumLocksPoolRound() {
		return NUM_LOCKS_POOL_ROUND;
	}


	public static int getNumLocksKnockoutRoster() {
		return NUM_LOCKS_KNOCKOUT_ROSTER;
	}


	public static int getNumLocksKnockoutRound() {
		return NUM_LOCKS_KNOCKOUT_ROUND;
	}


	public static int getNumFlankersPoolRoster() {
		return NUM_FLANKERS_POOL_ROSTER;
	}


	public static int getNumFlankersPoolRound() {
		return NUM_FLANKERS_POOL_ROUND;
	}


	public static int getNumFlankersKnockoutRoster() {
		return NUM_FLANKERS_KNOCKOUT_ROSTER;
	}


	public static int getNumFlankersKnockoutRound() {
		return NUM_FLANKERS_KNOCKOUT_ROUND;
	}


	public static int getNumNumber8sPoolRoster() {
		return NUM_NUMBER8S_POOL_ROSTER;
	}


	public static int getNumNumber8sPoolRound() {
		return NUM_NUMBER8S_POOL_ROUND;
	}


	public static int getNumNumber8sKnockoutRoster() {
		return NUM_NUMBER8S_KNOCKOUT_ROSTER;
	}


	public static int getNumNumber8sKnockoutRound() {
		return NUM_NUMBER8S_KNOCKOUT_ROUND;
	}


	public static int getNumScrumhalvesPoolRoster() {
		return NUM_SCRUMHALVES_POOL_ROSTER;
	}


	public static int getNumScrumhalvesPoolRound() {
		return NUM_SCRUMHALVES_POOL_ROUND;
	}


	public static int getNumScrumhalvesKnockoutRoster() {
		return NUM_SCRUMHALVES_KNOCKOUT_ROSTER;
	}


	public static int getNumScrumhalvesKnockoutRound() {
		return NUM_SCRUMHALVES_KNOCKOUT_ROUND;
	}


	public static int getNumFlyhalvesPoolRoster() {
		return NUM_FLYHALVES_POOL_ROSTER;
	}


	public static int getNumFlyhalvesPoolRound() {
		return NUM_FLYHALVES_POOL_ROUND;
	}


	public static int getNumFlyhalvesKnockoutRoster() {
		return NUM_FLYHALVES_KNOCKOUT_ROSTER;
	}


	public static int getNumFlyhalvesKnockoutRound() {
		return NUM_FLYHALVES_KNOCKOUT_ROUND;
	}


	public static int getNumCentersPoolRoster() {
		return NUM_CENTERS_POOL_ROSTER;
	}


	public static int getNumCentersPoolRound() {
		return NUM_CENTERS_POOL_ROUND;
	}


	public static int getNumCentersKnockoutRoster() {
		return NUM_CENTERS_KNOCKOUT_ROSTER;
	}


	public static int getNumCentersKnockoutRound() {
		return NUM_CENTERS_KNOCKOUT_ROUND;
	}


	public static int getNumWingsPoolRoster() {
		return NUM_WINGS_POOL_ROSTER;
	}


	public static int getNumWingsPoolRound() {
		return NUM_WINGS_POOL_ROUND;
	}


	public static int getNumWingsKnockoutRoster() {
		return NUM_WINGS_KNOCKOUT_ROSTER;
	}


	public static int getNumWingsKnockoutRound() {
		return NUM_WINGS_KNOCKOUT_ROUND;
	}


	public static int getNumFullbacksPoolRoster() {
		return NUM_FULLBACKS_POOL_ROSTER;
	}


	public static int getNumFullbacksPoolRound() {
		return NUM_FULLBACKS_POOL_ROUND;
	}


	public static int getNumFullbacksKnockoutRoster() {
		return NUM_FULLBACKS_KNOCKOUT_ROSTER;
	}


	public static int getNumFullbacksKnockoutRound() {
		return NUM_FULLBACKS_KNOCKOUT_ROUND;
	}


	public static Long getNoLoginContentId() {
		return NO_LOGIN_CONTENT_ID;
	}


	public static Long getNoDraftContentId() {
		return NO_DRAFT_CONTENT_ID;
	}


	public static Long getNoRoundContentId() {
		return NO_ROUND_CONTENT_ID;
	}


	public static Long getCompleteContentId() {
		return COMPLETE_CONTENT_ID;
	}


	public static String getCreateacctErrorExists() {
		return CREATEACCT_ERROR_EXISTS;
	}


	public static String getCreateacctErrorPasswordTooShort() {
		return CREATEACCT_ERROR_PASSWORD_TOO_SHORT;
	}


	public static String getCreateacctErrorInvalidEmail() {
		return CREATEACCT_ERROR_INVALID_EMAIL;
	}


	public static String getCreateacctErrorNicknameExists() {
		return CREATEACCT_ERROR__NICKNAME_EXISTS;
	}


	public static String getCreateacctOk() {
		return CREATEACCT_OK;
	}


	public static String getDefaultCompetitionShortName() {
		
		return DEFAULT_COMPETITION_SHORT_NAME;
	}


	public static stageType getCurrentstage() {
		return CURRENTSTAGE;
	}


	public static boolean isLockedDown() {
		return LOCKED_DOWN;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IConfiguration#addCompetition(java.lang.Long, java.lang.String)
	 */
	@Override
	public void addCompetition(Long id, String name) {
		compNameMap.put(id, name);
		
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IConfiguration#getCompetitionMap(java.lang.Long)
	 */
	@Override
	public final Map<Long, String> getCompetitionMap() {
		
		return compNameMap;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IConfiguration#getDefaultCompId()
	 */
	@Override
	public Long getDefaultCompId() {
		return defaultCompId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IConfiguration#setDefaultCompId()
	 */
	@Override
	public void setDefaultCompId(Long defaultCompId) {
		this.defaultCompId = defaultCompId; 
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public List<Long> getCompsUnderway() {
		return compsUnderway;
	}
	
	@Override
	public void setCompsUnderway(List<Long> compsUnderway) {
		this.compsUnderway = compsUnderway;
	}
	
	@Override
	public void addCompUnderway(Long compId) {
		if (!compsUnderway.contains(compId))
			compsUnderway.add(compId);
	}

	@Override
	public void removeCompUnderway(Long compId) {
		if (compsUnderway.contains(compId))
			compsUnderway.remove(compId);
	}
	
	public static String getCreateacctErrorNicknameCantBeNull() {
		return CREATEACCT_ERROR__NICKNAME_CANT_BE_NULL;
	}

	@Override
	public boolean deleteComp(Long compId) {
		if (compsUnderway.contains(compId)) {
			compsUnderway.remove(compId);
			if (defaultCompId.equals(compId)) {
				defaultCompId = null;
			}
			return true;
		}
		
		//didn't find it
		return false;
	}

	@Override
	public Environment getEnvironment() {
		return environment;
	}
	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public String getBaseToptenUrl() {
		if (environment == Environment.PROD) {
			return PROD_BASE_TOPTEN_URL;
		} else if (environment == Environment.BETA){
			return BETA_BASE_TOPTEN_URL;
		} else if (environment == Environment.DEV){
			return DEV_BASE_TOPTEN_URL;
		} else if (environment == Environment.LOCAL){
			return LOCAL_BASE_TOPTEN_URL;
		} else {
			throw (new RuntimeException("Environment not set"));
		}
	}
	@Override
	public String getFacebookAppid() {
		return FACEBOOK_APPID;
	}


}
