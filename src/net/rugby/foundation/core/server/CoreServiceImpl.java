/**
 * 
 */
package net.rugby.foundation.core.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;

import net.rugby.foundation.core.client.CoreService;
import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.core.server.factory.IClubhouseFactory;
import net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IContentFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlaceFactory;
import net.rugby.foundation.core.server.factory.ISponsorFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.CoreConfiguration;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.IClubhouseMembership;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.ISponsor;
import net.rugby.foundation.model.shared.LoginInfo;
import net.rugby.foundation.model.shared.CoreConfiguration.Environment;
import net.rugby.foundation.model.shared.ICompetition.CompetitionType;
import net.rugby.foundation.model.shared.UniversalRound;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Dominic Tracey
 *
 */
@Singleton
public class CoreServiceImpl extends RemoteServiceServlet implements CoreService {



	private static final long serialVersionUID = 1L;
	//	private Objectify ofy;
	private final IConfigurationFactory configF;
	private final ICompetitionFactory cf;
	private final IAppUserFactory auf;
	private IClubhouseFactory chf;
	private IClubhouseMembershipFactory chmf;
	private IAccountManager am;
	private IExternalAuthticatorProviderFactory eapf;
	private IContentFactory ctf;
	private IPlaceFactory plf;
	private ISponsorFactory spf;
	private IMatchGroupFactory mf;
	private IUniversalRoundFactory urf;


	@Inject
	public CoreServiceImpl(ICompetitionFactory cf, IAppUserFactory auf, IClubhouseFactory chf,  IClubhouseMembershipFactory chmf, 
			IConfigurationFactory configF, IAccountManager am, IExternalAuthticatorProviderFactory eapf, IContentFactory ctf,
			IPlaceFactory plf, ISponsorFactory spf, IMatchGroupFactory mf, IUniversalRoundFactory urf) {
		this.cf = cf;
		this.auf = auf;
		this.chf = chf;
		this.chmf = chmf;
		this.configF = configF;
		this.am = am;
		this.eapf = eapf;
		this.ctf = ctf;
		this.plf = plf;
		this.spf = spf;
		this.mf = mf;
		this.urf = urf;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreService#getComp(java.lang.Long)
	 */
	@Override
	public ICompetition getComp(Long compId) {
		try {
			return cf.get(compId);
		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public ICoreConfiguration getConfiguration() {
		try {
			ICoreConfiguration conf = configF.get();
			// check that environment is set
			if (conf.getEnvironment() == null) {
				HttpServletRequest req = this.getThreadLocalRequest();
				if (req.getServerName().contains("127.0.0.1")) {
					conf.setEnvironment(Environment.LOCAL);
				} else if (req.getServerName().contains("dev")) {
					conf.setEnvironment(Environment.DEV);
				} else if (req.getServerName().contains("beta")) {
					conf.setEnvironment(Environment.BETA);
				} else if (req.getServerName().contains("www")) {
					conf.setEnvironment(Environment.PROD);
				} else {
					throw new RuntimeException("Could not determine environment for configuration");
				}
				conf = configF.put(conf);
			}
			return conf;
		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreService#login(java.lang.String)
	 */
	@Override
	public LoginInfo login() {
		try {
			LoginInfo info = null; 
			HttpServletRequest request = this.getThreadLocalRequest();
			HttpSession session = request.getSession(false);
			if (session != null) {
				info = (LoginInfo) session.getAttribute("loginInfo");
			} 
			if (info == null) {
				info = new LoginInfo();
			}

			return info;
		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}



	@Override
	public LoginInfo createAccount(String emailAddress, String nickName, String password, String destination, boolean isGoogle, boolean isFacebook, boolean isOAuth2) {
		try {
			LoginInfo info = new LoginInfo();
			IAppUser u = null;
			Pattern p = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$");
			Matcher m = p.matcher(emailAddress.toUpperCase());
			boolean matchFound = m.matches();
			
			if (!matchFound) {
				// TODO this may be a merge
				info.setStatus(CoreConfiguration.getCreateacctErrorInvalidEmail());
				return info;
			}
			
			info = am.createAccount(emailAddress, nickName, password, destination, null,  isGoogle, isFacebook, isOAuth2, this.getThreadLocalRequest());
			if (info.isLoggedIn()) {
				auf.setEmail(info.getEmailAddress());
				u = auf.get();
			} else {
				return info;
			}
			
			if (u != null) {
				HttpServletRequest request = this.getThreadLocalRequest();
				HttpSession session = request.getSession();
				info = am.getLoginInfo(u);
				session.setAttribute("loginInfo", info);
				return info;
			}
		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
		return null;
	}

	@Override
	public LoginInfo nativeLogin(String emailAddress, String password) {
		try {
			//LoginInfo loginInfo = new LoginInfo();

			auf.setEmail(emailAddress.toLowerCase());
			IAppUser u = auf.get();

			if (u == null) {
				return null;  // bad userId or password
			}
		
			
			// first check if this is a non-native account - if they try to log on with a password for an 
			// account that has already been established with an external authenticator return a non logged in status
			// with the hint set.
			if (u.isFacebook() || u.isOath2() || u.isOpenId()) {
				LoginInfo info = new LoginInfo();
				info.setFacebook(u.isFacebook());
				info.setIsOpenId(u.isOpenId());
				info.setIsOauth2(u.isOath2());
				info.setLoggedIn(false);
				return info;
			}
			
			// now confirm they have validated their email
			if ((u.isNative() && !u.isFacebook() && !u.isOath2()) && !u.getEmailValidated()) {
				LoginInfo info = am.getLoginInfo(u);
				info.setStatus("You must validate your email address before you sign in.");
				info.setLoggedIn(false);
				return info;
			}
	
			String hash = DigestUtils.md5Hex(password);
			if (u.getPwHash() == null || !u.getPwHash().equals(hash))  {
				return null;  // bad userId or password		
			}

			// also confirm they aren't running a temporary password
			if (u.isMustChangePassword()) {
				LoginInfo info = am.getLoginInfo(u);
				info.setStatus("You must pick a new password to sign in.");
				info.setLoggedIn(false);
				return info;
			}
			

			HttpServletRequest request = this.getThreadLocalRequest();
			HttpSession session = request.getSession();
			LoginInfo info = am.getLoginInfo(u);
			info.setLoggedIn(true);
			session.setAttribute("loginInfo", info);

			u.setLastLogin(new Date());
			auf.put(u);
			
			return info;
		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public LoginInfo logOff(LoginInfo info) {
		try {
			HttpServletRequest request = this.getThreadLocalRequest();
			HttpSession session = request.getSession();
			//session.setAttribute("AppUser", null);	
			session.invalidate();
			if (info == null)
				info = new LoginInfo();
			info.setLoggedIn(false);

			// uncheck various roles
			info.setAdmin(false);
			info.setTopTenContentContributor(false);
			info.setTopTenContentEditor(false);

			return info;
		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * Return the currently logged on user
	 * @return
	 */
	private IAppUser getAppUser()
	{
		LoginInfo info = null;
		// do they have a session going?
		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession session = request.getSession(false);
		if (session != null) {
			info = (LoginInfo) session.getAttribute("loginInfo");
		} else {
			return null;  // no session means no LoginInfo which means no available AppUser
		}

		IAppUser au = null;

		if (info != null && info.isLoggedIn() == true) {
			auf.setEmail(info.getEmailAddress());				
			au = auf.get();	
		}

		// 2/20/2012 - clean up any old sessions where we had the AppUser in the session instead of LoginInfo
		if (session.getAttribute("AppUser") != null )
			session.setAttribute("AppUser", null);

		return au;
	}

	@Override
	public LoginInfo updatePreferences(LoginInfo loginInfo) {
		try {
			// confirm the user is logged on and who they say they are
			IAppUser u = getAppUser();
			if (u != null) {
				if (!u.getEmailAddress().equals(loginInfo.getEmailAddress()))
					return null;

				// update their account for lastComp, lastClubhouse and lastEntry
				auf.setId(u.getId());
				u = auf.get();
				u.setLastEntryId(loginInfo.getLastEntryId());
				u.setLastClubhouseId(loginInfo.getLastClubhouseId());
				u.setLastCompetitionId(loginInfo.getLastCompetitionId());

				loginInfo = am.getLoginInfo(u);
				HttpServletRequest request = this.getThreadLocalRequest();
				HttpSession session = request.getSession();
				session.setAttribute("loginInfo", loginInfo);
			}

			return loginInfo;
		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public IClubhouse createClubhouse(String name, String description, Boolean publicClubhouse) {

		try {
			// confirm the user is logged on
			IAppUser u = getAppUser();
			if (u == null) {
				return null;
			}

			chf.setId(null);
			IClubhouse clubhouse = chf.get();
			clubhouse.setActive(true);
			clubhouse.setDescription(description);
			clubhouse.setName(name);
			clubhouse.setOwnerID(u.getId());
			clubhouse.setPublicClubhouse(publicClubhouse);
			chf.put(clubhouse);

			//join url
			HttpServletRequest req = this.getThreadLocalRequest();
			clubhouse.setJoinLink(req.getScheme()+"://"+req.getServerName()+"/#JoinClubhouse:" + clubhouse.getId());

			chf.put(clubhouse);

			// the owner is also a member
			chmf.setId(null);
			IClubhouseMembership chm = chmf.get();
			chm.setAppUserID(u.getId());
			chm.setClubhouseID(clubhouse.getId());
			chm.setJoined(new Date());
			chm.setUserName(u.getNickname());
			chmf.put(chm);



			return clubhouse;
		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public IClubhouse joinClubhouse(Long clubhouseId) {

		try {
			// confirm the user is logged on
			IAppUser u = getAppUser();
			if (u == null) {
				return null;
			}

			if (clubhouseId == null) {
				return null;
			}

			chf.setId(clubhouseId);
			IClubhouse clubhouse = chf.get();
			//IClubhouseMembership retval = null;

			if (clubhouse != null) {

				//get the user's clubhouse memberships and see if they are already a member
				chmf.setAppUserId(u.getId());
				List<IClubhouseMembership> chml = chmf.getList();  
				boolean found = false;
				for (IClubhouseMembership chm : chml) {
					if (chm.getClubhouseID().equals(clubhouseId)) {
						found = true;
						//retval = chm;
						break;
					}
				}

				// if we didn't find the clubhouse membership for this user, add it.
				if (!found) {
					chmf.setId(null);
					IClubhouseMembership chm = chmf.get();
					chm.setAppUserID(u.getId());
					chm.setClubhouseID(clubhouseId);
					chm.setUserName(u.getNickname());
					chm.setJoined(new Date());
					chmf.put(chm);
				} else {
					Logger.getLogger("Core Service").log(Level.WARNING, "Tried to re-add user " + u.getNickname() + " (" + u.getId() + ") to clubhouse " + clubhouse.getName() + " (" + clubhouse.getId() + ")");
				}
			}

			return clubhouse;
		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreService#getClubhouses()
	 */
	@Override
	public List<IClubhouse>  getClubhouses() {
		try {
			// confirm the user is logged on
			IAppUser u = getAppUser();
			if (u == null) {
				return null;
			}

			chmf.setAppUserId(u.getId());	
			List<IClubhouseMembership> list = chmf.getList();

			List<IClubhouse> retval = new ArrayList<IClubhouse>();
			for (IClubhouseMembership chm : list) {
				chf.setId(chm.getClubhouseID());
				retval.add(chf.get());
			}
			return retval;

		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreService#getClubhouse(java.lang.Long)
	 */
	@Override
	public IClubhouse getClubhouse(Long clubhouseId) {
		try {

			chf.setId(clubhouseId);		
			return chf.get();

		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public List<IClubhouseMembership> getClubhouseMembers(Long clubhouseId) {
		try {
			// confirm the user is logged on
			IAppUser u = getAppUser();
			if (u == null) {
				return null;
			}

			chmf.setClubhouseId(clubhouseId);	

			// you should only be able to list members of a clubhouse to which you belong 
			List<IClubhouseMembership> list = chmf.getList();
			for (IClubhouseMembership chm: list) {
				if (chm.getAppUserID().equals(u.getId()))
					return list;
			}

			// if we get here they aren't a member
			return null;

		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreService#getOpenIdUrl(java.lang.String)
	 */
	@Override
	public String getOpenIdUrl(LoginInfo.Selector selector, String destination) {
		try {
			return eapf.get(LoginInfo.ProviderType.openid,selector, destination).getLocalURL();
		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreService#updateAccount(java.lang.String, java.lang.String)
	 */
	@Override
	public LoginInfo updateAccount(String email, String screenName, List<CompetitionType> compList, Boolean optOut) {
		try {
			// check they are who they say they are
			// confirm the user is logged on
			
			IAppUser u = getAppUser();
			if (u == null) {
				return new LoginInfo();
			}
			
			u.setCompList(compList);
			u.setOptOut(optOut);

			LoginInfo loginInfo = am.updateAccount(u, email, screenName, this.getThreadLocalRequest());

			// set in accountManager
			//			HttpServletRequest request = this.getThreadLocalRequest();
			//			HttpSession session = request.getSession();
			//			session.setAttribute("loginInfo", loginInfo);

			return loginInfo;
		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreService#getFacebookLoginUrl(java.lang.String)
	 */
	@Override
	public String getFacebookLoginUrl(String destination) {
		try {
			return eapf.get(LoginInfo.ProviderType.facebook,null, destination).getLocalURL();
		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}	
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreService#changePassword(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public LoginInfo changePassword(String email, String oldPassword, String newPassword) {
		try {

			LoginInfo loginInfo = am.changePassword(email, oldPassword, newPassword);
			HttpServletRequest request = this.getThreadLocalRequest();
			HttpSession session = request.getSession();
			session.setAttribute("loginInfo", loginInfo);

			return loginInfo;
		}  catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}	
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreService#forgotPassword(java.lang.String)
	 */
	@Override
	public LoginInfo forgotPassword(String email, String destination) {
		try {
			LoginInfo loginInfo = am.forgotPassword(email, destination);
			HttpServletRequest request = this.getThreadLocalRequest();
			HttpSession session = request.getSession();
			session.setAttribute("loginInfo", loginInfo);

			return loginInfo;
		}  catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}	
	}

	@Override
	public IContent getContent(Long contentId) {
		try {
			return ctf.get(contentId);
		}  catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public IContent getContent(String title) {
		try {
			return ctf.get(title);
		}  catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public IContent saveContent(IContent content) {
		try {
			content = ctf.put(content);
			return content;
		}  catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public ISponsor getSponsor(Long id) {
		try {
			return spf.get(id);
		}  catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public ArrayList<IMatchGroup> getResultsForOrdinal(int ordinal, Long virtualCompId) {
		try {
			return (ArrayList<IMatchGroup>) mf.getMatchesForVirualComp(ordinal, virtualCompId);
		}  catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public UniversalRound getUniversalRound(int ordinal) {
		try {
			return urf.get(ordinal);
		}  catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public String getOAuth2Url(String destination) {
		try {		
			return eapf.get(LoginInfo.ProviderType.oauth2,null, destination).getLocalURL();
		}  catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}


	@Override
	public HashMap<String,Long> getContentItems() {
		try {

			if (ctf instanceof IContentFactory) {
				return ctf.getMenuMap(true);
			}
			return null;
		}  catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

	@Override
	public LoginInfo validateEmail(String email, String validationCode) {
		try {
			LoginInfo loginInfo = am.validateEmail(email,validationCode);
			HttpServletRequest request = this.getThreadLocalRequest();
			HttpSession session = request.getSession();
			session.setAttribute("loginInfo", loginInfo);
			
			return loginInfo;
		}  catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

	@Override
	public LoginInfo resendValidationEmail(String email) {
		try {
			LoginInfo loginInfo = am.resendValidationEmail(email);
			HttpServletRequest request = this.getThreadLocalRequest();
			HttpSession session = request.getSession();
			session.setAttribute("loginInfo", loginInfo);
			
			return loginInfo;
		}  catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

}
