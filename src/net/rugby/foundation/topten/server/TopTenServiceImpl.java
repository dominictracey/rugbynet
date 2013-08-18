package net.rugby.foundation.topten.server;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.ITopTenUser;
import net.rugby.foundation.model.shared.LoginInfo;
import net.rugby.foundation.topten.client.TopTenListService;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.ITopTenList.ITopTenListSummary;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class TopTenServiceImpl extends RemoteServiceServlet implements TopTenListService {

	private IAppUserFactory auf;
	private ITopTenListFactory ttlf;

	private static final long serialVersionUID = 1L;
	public TopTenServiceImpl() {


	}

	@Inject
	public void setFactories(ITopTenListFactory ttlf, IAppUserFactory auf) {
		try {
			this.ttlf = ttlf;
			this.auf = auf;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
		}
	}

	@Override
	public ITopTenList getTopTenList(Long id) {
		try {
			ITopTenList ttl = ttlf.get(id);

			if (ttl.getLive()) {
				return ttl;
			} else {
				IAppUser u = getAppUser();
				if (u instanceof ITopTenUser) {
					if (((ITopTenUser)u).isTopTenContentContributor() || ((ITopTenUser)u).isTopTenContentEditor()) {
						return ttl;
					} else {
						// something funny going on
						String user = "a not logged on user ";
						if (u != null) {
							user = "The user " + u.getEmailAddress() + " (" + u.getId() + ")";
						}
						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, user + " is trying to access the unpublished TTL " + ttl.getTitle());
						return null;
					}
				}
			}
			return null;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}

	}

	@Override
	public List<ITopTenListSummary> getTopTenListSummariesForComp(Long compId) {
		try {
			return ttlf.getSummariesForComp(compId);
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

	@Override
	public ITopTenItem saveTopTenItem(ITopTenItem item) {
		try {
			IAppUser u = getAppUser();
			if (u instanceof ITopTenUser && (((ITopTenUser)u).isTopTenContentContributor() || ((ITopTenUser)u).isTopTenContentEditor())) {
				return ttlf.put(item);
			} else {
				String user = "a not logged on user ";
				if (u != null) {
					user = "The user " + u.getEmailAddress() + " (" + u.getId() + ")";
				}
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, user + " is trying to save the TTI " + item.getId());
				return null;
			}
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

	@Override
	public ITopTenList deleteTopTenList(ITopTenList list) {
		try {
			IAppUser u = getAppUser();
			if (u instanceof ITopTenUser && ((ITopTenUser)u).isTopTenContentEditor()) {
				return ttlf.delete(list);
			} else {
				String user = "a not logged on user ";
				if (u != null) {
					user = "The user " + u.getEmailAddress() + " (" + u.getId() + ")";
				}
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, user + " is trying to delete the TTL " + list.getTitle());
				return null;
			}
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

	@Override
	public ITopTenList publishTopTenList(ITopTenList list) {
		try {
			IAppUser u = getAppUser();
			if (u instanceof ITopTenUser && ((ITopTenUser)u).isTopTenContentEditor()) {
				return ttlf.publish(list);
			} else {
				String user = "a not logged on user ";
				if (u != null) {
					user = "The user " + u.getEmailAddress() + " (" + u.getId() + ")";
				}
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, user + " is trying to publish the TTL " + list.getTitle());
				return null;
			}
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

	@Override
	public ITopTenItem submitTopTenItem(ITopTenItem item) {
		try {
			IAppUser u = getAppUser();
			if (u instanceof ITopTenUser && (((ITopTenUser)u).isTopTenContentContributor() || ((ITopTenUser)u).isTopTenContentEditor())) {
				return ttlf.submit(item);
			} else {
				String user = "a not logged on user ";
				if (u != null) {
					user = "The user " + u.getEmailAddress() + " (" + u.getId() + ")";
				}
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, user + " is trying to submit the item " + item.getId());
				return null;
			}
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}



	private IAppUser getAppUser()
	{
		try {
		// do they have a session going?
		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession session = request.getSession(false);
		if (session != null) {
			LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
			if (loginInfo.isLoggedIn()) {
				auf.setEmail(loginInfo.getEmailAddress());
				return auf.get();
			}
		}

		return null;
	} catch (Throwable e) {
		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
		return null;
	}

	}

	@Override
	public ITopTenList getLatestForComp(Long compId) {
		try {
			IAppUser u = getAppUser();
			if (u instanceof ITopTenUser && (((ITopTenUser)u).isTopTenContentContributor() || ((ITopTenUser)u).isTopTenContentEditor())) {
				return ttlf.getLastCreatedForComp(compId);
			} else {
				return ttlf.getLatestForComp(compId);
			}
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

}
