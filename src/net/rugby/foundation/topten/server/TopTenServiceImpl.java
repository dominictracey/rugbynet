package net.rugby.foundation.topten.server;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.LoginInfo;
import net.rugby.foundation.topten.client.TopTenListService;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.ITopTenList.ITopTenListSummary;
import net.rugby.foundation.topten.model.shared.ITopTenUser;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class TopTenServiceImpl extends RemoteServiceServlet implements TopTenListService {

	private IAppUserFactory auf;
	private ICompetitionFactory cf;
	private IPlayerFactory pf;
	private ITopTenListFactory ttlf;

	private static final long serialVersionUID = 1L;
	public TopTenServiceImpl() {


	}

	@Inject
	public void setFactories(ITopTenListFactory ttlf, IAppUserFactory auf, ICompetitionFactory cf, IPlayerFactory pf) {
		try {
			this.ttlf = ttlf;
			this.auf = auf;
			this.cf = cf;
			this.pf = pf;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage());
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
						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "The user " + u.getEmailAddress() + " (" + u.getId() + ") is trying to access the unpublished TTL " + ttl.getTitle());
						return null;
					}
				}
			}
			return null;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage());
			return null;
		}

	}

	@Override
	public List<ITopTenListSummary> getTopTenListSummariesForComp(Long compId) {
		try {
			return ttlf.getSummariesForComp(compId);
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage());
			return null;
		}
	}

	@Override
	public ITopTenItem saveTopTenItem(ITopTenItem item) {
		try {
			return ttlf.put(item);
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage());
			return null;
		}
	}

	@Override
	public ITopTenList deleteTopTenList(ITopTenList list) {
		try {
			return ttlf.delete(list);
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage());
			return null;
		}
	}

	@Override
	public ITopTenList publishTopTenList(ITopTenList list) {
		try {
			return ttlf.publish(list);
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage());
			return null;
		}
	}

	@Override
	public ITopTenItem submitTopTenItem(ITopTenItem item) {
		try {
			return ttlf.submit(item);
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage());
			return null;
		}
	}



	private IAppUser getAppUser()
	{
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

	}

	@Override
	public ITopTenList getLatestForComp(Long compId) {
		try {
			return ttlf.getLatestForComp(compId);
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage());
			return null;
		}
	}

}
