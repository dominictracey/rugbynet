package net.rugby.foundation.topten.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import net.rugby.foundation.admin.shared.IBlurb;
import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.core.server.factory.ICachingFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IContentFactory;
import net.rugby.foundation.core.server.factory.IPlaceFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingMatrixFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.promote.IPromoter;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.IServerPlace;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.ITopTenUser;
import net.rugby.foundation.model.shared.LoginInfo;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.topten.client.TopTenListService;
import net.rugby.foundation.topten.model.shared.Feature;
import net.rugby.foundation.topten.model.shared.INote;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.ITopTenList.ITopTenListSummary;
import net.rugby.foundation.topten.server.factory.INoteFactory;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class TopTenServiceImpl extends RemoteServiceServlet implements TopTenListService {

	private IAppUserFactory auf;
	private ITopTenListFactory ttlf;
	private IContentFactory ctf;
	private IConfigurationFactory ccf;
	private IPlayerRatingFactory prf;
	private IRatingSeriesFactory rsf;
	private IRatingQueryFactory rqf;
	private IRatingGroupFactory rgf;
	private IRatingMatrixFactory rmf;
	private IPlaceFactory spf;
	private INoteFactory nf;
	private IPlayerFactory pf;
	private ITeamGroupFactory tgf;
	private IPromoter twitter;

	private static final long serialVersionUID = 1L;
	public TopTenServiceImpl() {


	}

	@Inject
	public void setFactories(ITopTenListFactory ttlf, IAppUserFactory auf, IContentFactory ctf, IPlayerRatingFactory prf,
			IRatingSeriesFactory rsf, IRatingQueryFactory rqf, IRatingGroupFactory rgf, IRatingMatrixFactory rmf, IPlaceFactory spf,
			INoteFactory nf, IPlayerFactory pf, ITeamGroupFactory tgf, IConfigurationFactory ccf, IPromoter twitter) {
		try {
			this.ttlf = ttlf;
			this.auf = auf;
			this.ctf = ctf;
			this.prf = prf;
			this.rsf = rsf;
			this.rqf = rqf;
			this.rgf = rgf;
			this.rmf = rmf;
			this.spf = spf;
			this.nf = nf;
			this.pf = pf;
			this.tgf = tgf;
			this.ccf = ccf;
			this.twitter = twitter;

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

	@Override
	public Long getLatestListIdForComp(Long compId) {
		try {
			ITopTenList ttl = null;
			IAppUser u = getAppUser();
			if (u instanceof ITopTenUser && (((ITopTenUser)u).isTopTenContentContributor() || ((ITopTenUser)u).isTopTenContentEditor())) {
				ttl = ttlf.getLastCreatedForComp(compId);
			} else {
				ttl = ttlf.getLatestForComp(compId);
			}
			if (ttl != null) {
				return ttl.getId();
			} else {
				return null;
			}
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

	//	@Override
	//	public HashMap<String,Long> getContentItems() {
	//		try {
	//
	//			if (ctf instanceof IContentFactory) {
	//				return ctf.getMenuMap(true);
	//			}
	//			return null;
	//		}  catch (Throwable e) {
	//			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
	//			return null;
	//		}
	//	}

	@Override
	public ITopTenList saveTopTenList(ITopTenList list) {
		try {
			list = ttlf.put(list);
			return list;
		}  catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

	@Override
	public IPlayerRating getPlayerRating(Long playerRatingId) {
		try {
			IPlayerRating pr = prf.get(playerRatingId);
			return pr;
		}  catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

	@Override
	public IRatingSeries getRatingSeries(Long seriesId) {
		try {
			return rsf.get(seriesId);
		}  catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

	@Override
	public IRatingSeries getRatingSeries(Long compId, RatingMode mode) {
		try {
			return rsf.get(compId, mode);
		}  catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

	@Override
	public ITopTenList getTopTenListForRatingQuery(Long id) {
		try {
			IRatingQuery rq = rqf.get(id);
			if (rq != null && rq.getTopTenListId() != null) {
				return ttlf.get(rq.getTopTenListId());
			} else {
				return null;
			}
		}  catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

	@Override
	public IRatingGroup getRatingGroup(Long ratingGroupId) {
		try {
			IRatingGroup rg = rgf.get(ratingGroupId);
			return rg;
		}  catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

	@Override
	public IRatingMatrix getRatingMatrix(Long ratingMatrixId) {
		try {
			IRatingMatrix rm = rmf.get(ratingMatrixId);
			return rm;
		}  catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

	@Override
	public List<IRatingQuery> getRatingQueriesForMatrix(Long ratingMatrixId) {
		try {
			List<IRatingQuery> rqs = rqf.getForMatrix(ratingMatrixId);
			return rqs;
		}  catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

	@Override
	public Map<RatingMode, Long> getAvailableSeries(Long compId) {
		try {
			Map<RatingMode, Long> map = rsf.getModesForComp(compId);
			return map;
		}  catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}
	//
	//	@Override
	//	public IServerPlace getPlace(String guid) {
	//		try {
	//			return spf.getForGuid(guid);
	//		}  catch (Throwable e) {
	//			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
	//			return null;
	//		}
	//	}

	@Override
	public IRatingSeries getDefaultRatingSeries(Long compId) {
		try {
			return rsf.get(compId, RatingMode.BY_POSITION);
		}  catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

	@Override
	public List<INote> getNotesForList(Long id) {
		try {
			ITopTenList ttl = ttlf.get(id);
			if (ttl != null) {
				return nf.getForList(ttl, false);
			} else {
				return null;
			}
		}  catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

	@Override
	public Map<Long, String> getPlayerNames(List<Long> needPlayerNames) {
		try {
			Map<Long, String> retval = new HashMap<Long, String>();

			for (Long id : needPlayerNames) {
				IPlayer p = pf.get(id);
				if (p != null) {
					retval.put(id, p.getDisplayName());
				}
			}
			return retval;
		}  catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

	@Override
	public Map<Long, String> getTTLNames(List<Long> needTTLNames) {
		try {
			Map<Long, String> retval = new HashMap<Long, String>();

			for (Long id : needTTLNames) {
				ITopTenList ttl = ttlf.get(id);
				if (ttl != null) {
					retval.put(id, ttl.getTitle());
				}
			}
			return retval;
		}  catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

	@Override
	public Map<Long, String> getTTLContexts(List<Long> needTTLContexts) {
		try {
			Map<Long, String> retval = new HashMap<Long, String>();

			for (Long id : needTTLContexts) {
				ITopTenList ttl = ttlf.get(id);
				if (ttl != null) {
					retval.put(id, ttl.getContext());
				}
			}
			return retval;
		}  catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}


	@Override
	public IServerPlace getPlace(String guid) {
		try {
			return spf.getForGuid(guid);
		}  catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

	@Override
	public IServerPlace createFeature(Long compId, Long queryId) {
		try {
			IAppUser u = getAppUser();
			if (u instanceof ITopTenUser && (((ITopTenUser)u).isTopTenContentContributor() || ((ITopTenUser)u).isTopTenContentEditor())) {
				if (queryId != null) {
					ITopTenList list = getTopTenListForRatingQuery(queryId);
					IServerPlace place = ttlf.makeFeature(list);

					return place;
				}

				return null;

			} else {
				String user = "a not logged on user ";
				if (u != null) {
					user = "The user " + u.getEmailAddress() + " (" + u.getId() + ")";
				}
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, user + " is trying to create a feature from queryId" + queryId);
				return null;
			}
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}

	}

	@Override
	public List<Feature> getLatestFeatures() {
		try {
			return ttlf.getLatestFeatures();
		}  catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

	@Override
	public HashMap<Long, String> getTeamLogoStyleMap() {
		try {
			return tgf.getTeamLogoStyleMap();
		}  catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

	@Override
	public Boolean deleteNote(Long noteId) {
		try {
			IAppUser u = getAppUser();
			if (u instanceof ITopTenUser && (((ITopTenUser)u).isTopTenContentContributor() || ((ITopTenUser)u).isTopTenContentEditor())) {

				INote note = nf.get(noteId);
				if (note != null) {
					return nf.delete(note);
				} else {
					return false;
				}
			} else {
				String user = "a not logged on user ";
				if (u != null) {
					user = "The user " + u.getEmailAddress() + " (" + u.getId() + ")";
				}
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, user + " is trying to delete a note " + noteId);
				return null;
			}
		}  catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}


	@Override
	public List<IPlayer> sendTweets(Long ttlId) {
		if (isAdmin()) {
			return twitter.promoteList(ttlId);
		} else {
			return null;
		}
	}
	
	@Override
	public String sendTweet(ITopTenItem tti, ITopTenList ttl) {
		if (isAdmin()) {
			return twitter.promoteItem(tti, ttl);
		} else {
			return null;
		}
	}


	private boolean isAdmin() {
		IAppUser u = getAppUser();
		if (u instanceof ITopTenUser && (((ITopTenUser)u).isTopTenContentContributor() || ((ITopTenUser)u).isTopTenContentEditor())) {
			return true;
		}
		return false;
	}

	@Override
	public Boolean savePlayer(IPlayer p) {
		if (isAdmin()) {
			try {				
				pf.put(p);
				return true;
			}  catch (Throwable e) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
				return false;
			}
		} else {
			return false;
		}
	}

}
