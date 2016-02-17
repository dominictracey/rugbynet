package net.rugby.foundation.core.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import net.rugby.foundation.core.client.ui.CreateClubhouse;
import net.rugby.foundation.core.client.ui.CreateClubhouse.Presenter;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.IClubhouseMembership;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ISponsor;
import net.rugby.foundation.model.shared.LoginInfo;
import net.rugby.foundation.model.shared.Sponsor;
import net.rugby.foundation.model.shared.UniversalRound;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Core implements CoreServiceAsync, EntryPoint {

	public interface CompChangeListener {
		void compChanged(Long id);
	}

	public interface RoundChangeListener {
		void roundChanged(UniversalRound roundOrdinal);
	}

	public interface GuidChangeListener {
		void guidChanged(String guid);
	}

	private CoreClientFactory clientFactory = new CoreClientFactoryImpl();
	private static Core _i = null;

	// we need to have Configuration, compMap with at least one ICompetition and a currentCompID before we are ready to go
	private boolean initialized = false;

	/**
	 * if (config != null && currentCompId != 0L && compMap.get(currentCompId) != null) return true
	 */
	public boolean isInitialized() {
		if (config != null && currentCompId != 0L && compMap.get(currentCompId) != null)
			initialized = true;
		return initialized;
	}

	// the Core Configuration (which contains, amongst other things, a list of underway Comps)
	//	Login agnostic
	private ICoreConfiguration config = null;

	// the currently selected competition
	//	Cleared to default without login
	private Long currentCompId = 0L;

	private String currentGuid = "";

	// a map of all competitions (underway or not). key id, value ICompetition.
	//	Login agnostic
	private Map<Long, ICompetition> compMap = new HashMap<Long, ICompetition>();

	// components that are interested in getting notified when the current CompId and roundId changes
	//	Login agnostic
	private ArrayList<CompChangeListener> compChangeListeners = new ArrayList<CompChangeListener>();
	private ArrayList<RoundChangeListener> roundChangeListeners = new ArrayList<RoundChangeListener>();
	private ArrayList<GuidChangeListener> guidChangeListeners = new ArrayList<GuidChangeListener>();

	// map that contains the last completed round for a comp. key compId, value IRound of previous round. 
	//	Login agnostic
	//private Map<Long, IRound> prevRoundMap = new HashMap<Long, IRound>();

	// the currently selected clubhouse
	//	Cleared to default without login
	private Long currentClubhouseId = 0L;	

	// a list of the currently logged on user's IClubhouses
	//	Cleared to default without login
	private List<IClubhouse> clubhouseList = null;

	// a cache of IClubhouses. key clubhouseId, value IClubhouse.
	//	Login agnostic
	private Map<Long,IClubhouse> clubhouseMap = null;

	// the UI component for creating a new clubhouse
	//	Login agnostic
	private CreateClubhouse createClubhouse = null;

	// map that contains content
	private Map<Long, IContent> contentMap = null;

	private int currentRoundOrdinal = -1; 

	// sponsor map
	private Map<Long, ISponsor> sponsorMap = null;
	ISponsor noSponsor = new Sponsor();
	private Map<Integer, UniversalRound> universalRoundMap = new HashMap<Integer, UniversalRound>();

	/**
	 * Use the static getInstance factory method
	 */
	private Core()
	{
		//CssBundle.INSTANCE.bootstrapOverridesCss().ensureInjected();
		//CssBundle.INSTANCE.defaultCss().ensureInjected();
		noSponsor.setAbbr("NON");
		noSponsor.setActive(true);
		noSponsor.setName("None");
		noSponsor.setTagline("");
	}

	/** Singleton 
	 * 
	 */
	public static CoreServiceAsync getInstance() {
		if (_i == null) {
			_i = new Core();
		}
		return _i;

	}

	/** Singleton 
	 * 
	 */
	public static Core getCore() {
		if (_i == null) {
			_i = new Core();
		}
		return _i;

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */
	@Override
	public void onModuleLoad() {
		//		GWT.setUncaughtExceptionHandler(new
		//		        GWT.UncaughtExceptionHandler() {
		//		        public void onUncaughtException(Throwable e) {
		//		          Window.
		//		      }
		//		});
		Core.exportShowLoggedIn();
		Core.exportSetIdentityParent();

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreServiceAsync#getComp(java.lang.Long, com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	@Override
	public void getComp(final Long compId, final AsyncCallback<ICompetition> cb) {
		if (compMap.containsKey(compId)) {
			cb.onSuccess(compMap.get(compId));
		} else {
			clientFactory.getRpcService().getComp(compId, new AsyncCallback<ICompetition>() {
				@Override
				public void onFailure(Throwable caught) {
					cb.onFailure(caught);
				}

				@Override
				public void onSuccess(ICompetition result) {	
					compMap.put(compId, result);
					cb.onSuccess(result);
				}

			});
		}
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreServiceAsync#login(java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	@Override
	public void login(final AsyncCallback<LoginInfo> cb) {
		// we need to check for the case where they are still logged in to rugby.net but not logged into their external authenticator
		clientFactory.getRpcService().login(new AsyncCallback<LoginInfo> () {
			@Override
			public void onFailure(Throwable caught) {
				LoginInfo loginInfo = new LoginInfo();
				clientFactory.setLoginInfo(loginInfo);
				clientFactory.getIdentityManager().actionsComplete(loginInfo);
				cb.onFailure(caught);
			}

			@Override
			public void onSuccess(LoginInfo result) {	
				clientFactory.setLoginInfo(result);
				clientFactory.getIdentityManager().showLoggedIn();
				cb.onSuccess(result);  
			}

		});

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreServiceAsync#logOff(net.rugby.foundation.model.shared.LoginInfo, com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	@Override
	public void logOff(LoginInfo info, final AsyncCallback<LoginInfo> cb) {
		clientFactory.getRpcService().logOff(info, new AsyncCallback<LoginInfo> () {
			@Override
			public void onFailure(Throwable caught) {
				cb.onFailure(caught);
			}

			@Override
			public void onSuccess(LoginInfo result) {	
				clubhouseList = null;
				currentClubhouseId = null;
				cb.onSuccess(result);
			}

		});
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreServiceAsync#nativeLogin(java.lang.String, java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	@Override
	public void nativeLogin(String emailAddress, String password,
			final AsyncCallback<LoginInfo> cb) {
		clientFactory.getRpcService().nativeLogin(emailAddress, password, new AsyncCallback<LoginInfo> () {
			@Override
			public void onFailure(Throwable caught) {
				cb.onFailure(caught);
			}

			@Override
			public void onSuccess(LoginInfo result) {	
				cb.onSuccess(result);
			}

		});
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreServiceAsync#createAccount(java.lang.String, java.lang.String, java.lang.String, boolean, boolean, com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	@Override
	public void createAccount(String emailAddress, String nickName,
			String password, boolean isGoogle, boolean isFacebook, boolean isOAuth2,
			final AsyncCallback<LoginInfo> cb) {
		clientFactory.getRpcService().createAccount(emailAddress,  nickName,
				password,  isGoogle,  isFacebook, isOAuth2, new AsyncCallback<LoginInfo> () {
			@Override
			public void onFailure(Throwable caught) {
				cb.onFailure(caught);
			}

			@Override
			public void onSuccess(LoginInfo result) {	
				cb.onSuccess(result);
			}

		});
	}

	public Long getCurrentCompId() {			 
		return currentCompId;
	}

	public void setCurrentCompId(final Long currentCompId) {
		if (!this.currentCompId.equals(currentCompId)) {
			this.currentCompId = currentCompId;
			getComp(currentCompId, new AsyncCallback<ICompetition>() {
				public void onFailure(Throwable caught) {
					//ignore
					//cb.onFailure(caught);
				}

				public void onSuccess(ICompetition comp) {
					currentRoundOrdinal = config.getCurrentUROrdinal();
					for (CompChangeListener l : compChangeListeners) {
						l.compChanged(currentCompId);
					}
				}
			});
		}


	}

	public void setCurrentClubhouseId(Long currentClubhouseId) {
		if (this.currentClubhouseId != currentClubhouseId) {
			this.currentClubhouseId  = currentClubhouseId;
		}

		clientFactory.getLoginInfo().setLastClubhouseId(currentClubhouseId);

		clientFactory.getRpcService().updatePreferences(clientFactory.getLoginInfo(), new AsyncCallback<LoginInfo> () {
			@Override
			public void onFailure(Throwable caught) {
				//ignore
				//cb.onFailure(caught);
			}

			@Override
			public void onSuccess(LoginInfo result) {	
				//ignore
				//cb.onSuccess(result);
			}

		});

	}
	public void registerCompChangeListener(CompChangeListener l) {
		compChangeListeners.add(l);
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreServiceAsync#updatePreferences(net.rugby.foundation.model.shared.LoginInfo, com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	@Override
	public void updatePreferences(LoginInfo loginInfo,	final AsyncCallback<LoginInfo> cb) {
		clientFactory.getRpcService().updatePreferences(loginInfo, new AsyncCallback<LoginInfo> () {
			@Override
			public void onFailure(Throwable caught) {
				cb.onFailure(caught);
			}

			@Override
			public void onSuccess(LoginInfo result) {	
				cb.onSuccess(result);
			}

		});		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreServiceAsync#createClubhouse(java.lang.String, java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	@Override
	public void createClubhouse(String name, String description,
			Boolean publicClubhouse, final AsyncCallback<IClubhouse> cb) {
		clientFactory.getRpcService().createClubhouse(name, description, publicClubhouse, new AsyncCallback<IClubhouse> () {
			@Override
			public void onFailure(Throwable caught) {
				cb.onFailure(caught);
			}

			@Override
			public void onSuccess(final IClubhouse result) {	
				currentClubhouseId = result.getId();
				getClubhouseMap().put(result.getId(), result);
				clubhouseList.add(result);
				cb.onSuccess(result);

			}

		});		

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreServiceAsync#joinClubhouse(java.lang.Long, com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	@Override
	public void joinClubhouse(Long clubhouseId, final AsyncCallback<IClubhouse> cb) {
		clientFactory.getRpcService().joinClubhouse(clubhouseId, new AsyncCallback<IClubhouse> () {
			@Override
			public void onFailure(Throwable caught) {
				cb.onFailure(caught);
			}

			@Override
			public void onSuccess(IClubhouse chm) {	
				if (clubhouseList == null) {
					clubhouseList = new ArrayList<IClubhouse>();
				}
				clubhouseList.add(chm);
				cb.onSuccess(chm);
			}

		});

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreServiceAsync#getConfiguration(com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	@Override
	public void getConfiguration(final AsyncCallback<ICoreConfiguration> cb) {
		if (config != null) {
			cb.onSuccess(config);
		} else {
			clientFactory.getRpcService().getConfiguration(new AsyncCallback<ICoreConfiguration>() {
				@Override
				public void onFailure(Throwable caught) {
					cb.onFailure(caught);
				}

				@Override
				public void onSuccess(ICoreConfiguration result) {	
					config = result;
					//setCurrentCompId(config.getDefaultCompId());
					cb.onSuccess(result);
				}

			});
		}

	}

	/**
	 * @return
	 */
	public CoreClientFactory getClientFactory() {
		return clientFactory;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreServiceAsync#getClubhouses(com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	@Override
	public void getClubhouses(final AsyncCallback<List<IClubhouse>> cb) {
		if (clubhouseList == null) {
			clientFactory.getRpcService().getClubhouses(new AsyncCallback<List<IClubhouse>>() {
				@Override
				public void onFailure(Throwable caught) {
					cb.onFailure(caught);
				}

				@Override
				public void onSuccess(List<IClubhouse> result) {	
					clubhouseList = result;
					cb.onSuccess(result);
				}

			});
		} else {
			cb.onSuccess(clubhouseList);
		}

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreServiceAsync#getClubhouse(java.lang.Long, com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	@Override
	public void getClubhouse(final Long clubhouseId, final AsyncCallback<IClubhouse> cb) {
		if (getClubhouseMap().containsKey(clubhouseId)) {
			cb.onSuccess(getClubhouseMap().get(clubhouseId));
		} else {
			clientFactory.getRpcService().getClubhouse(clubhouseId, new AsyncCallback<IClubhouse> () {
				@Override
				public void onFailure(Throwable caught) {
					cb.onFailure(caught);
				}

				@Override
				public void onSuccess(IClubhouse result) {	
					getClubhouseMap().put(clubhouseId, result);
					cb.onSuccess(result);
				}

			});		
		}
	}

	/**
	 * @return
	 */
	public ClickHandler showCreateClubhouse(final Presenter presenter) {

		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (createClubhouse == null)
					createClubhouse = new CreateClubhouse();
				createClubhouse.init(presenter);
			}
		};
	}

	/**
	 * @param asyncCallback 
	 * @return Asynchronously return the current ICompetition. Core must be initialized before returning.
	 */
	public void getCurrentComp(final AsyncCallback<ICompetition> asyncCallback) {
		if (!isInitialized()) {
			// wait for it
			Timer t = new Timer() {
			      @Override
			      public void run() {
			        if (isInitialized()) {
			        	asyncCallback.onSuccess(compMap.get(currentCompId));
			        } else {
			        	this.schedule(500);
			        }
			      }
			    };

			    // Schedule the timer to run once in a half second.
			    t.schedule(500);
		} else {
			asyncCallback.onSuccess(compMap.get(currentCompId));
		}
	}

	/**
	 * @return Synchronously return the current IClubhouse. Core must be initialized before calling.
	 */
	public IClubhouse getCurrentClubhouse() {
		assert (isInitialized());
		return getClubhouseMap().get(currentClubhouseId);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreServiceAsync#getClubhouseMembers(java.lang.Long, com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	@Override
	public void getClubhouseMembers(Long clubhouseId, final AsyncCallback<List<IClubhouseMembership>> cb) {
		// don't cache this as it may not show you as having joined... @REX
		clientFactory.getRpcService().getClubhouseMembers(clubhouseId, new AsyncCallback<List<IClubhouseMembership>> () {
			@Override
			public void onFailure(Throwable caught) {
				cb.onFailure(caught);
			}

			@Override
			public void onSuccess(List<IClubhouseMembership> result) {	
				cb.onSuccess(result);
			}

		});			

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreServiceAsync#getOpenIdUrl(java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	@Override
	public void getOpenIdUrl(final LoginInfo.Selector selector, final String destination,
			final AsyncCallback<String> cb) {
		clientFactory.getRpcService().getOpenIdUrl(selector, destination, new AsyncCallback<String> () {
			@Override
			public void onFailure(Throwable caught) {
				cb.onFailure(caught);
			}

			@Override
			public void onSuccess(String result) {	
				cb.onSuccess(result);
			}

		});			
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreServiceAsync#updateAccount(java.lang.String, java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	@Override
	public void updateAccount(String email, String screenName,
			final AsyncCallback<LoginInfo> cb) {
		clientFactory.getRpcService().updateAccount(email, screenName, new AsyncCallback<LoginInfo> () {
			@Override
			public void onFailure(Throwable caught) {
				cb.onFailure(caught);
			}

			@Override
			public void onSuccess(LoginInfo result) {	
				cb.onSuccess(result);
			}

		});			
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreServiceAsync#getFacebookLoginUrl(java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	@Override
	public void getFacebookLoginUrl(String destination,	final AsyncCallback<String> cb) {
		clientFactory.getRpcService().getFacebookLoginUrl(destination, new AsyncCallback<String> () {
			@Override
			public void onFailure(Throwable caught) {
				cb.onFailure(caught);
			}

			@Override
			public void onSuccess(String result) {	
				cb.onSuccess(result);
			}

		});	

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreServiceAsync#changePassword(java.lang.String, java.lang.String, java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	@Override
	public void changePassword(String email, String oldPassword,
			String newPassword, AsyncCallback<LoginInfo> asyncCallback) {
		assert false;

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreServiceAsync#forgotPassword(java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	@Override
	public void forgotPassword(String email,
			AsyncCallback<LoginInfo> asyncCallback) {
		assert false;

	}

	@Override
	public void getContent(final Long contentId, final AsyncCallback<IContent> cb) {

		if (getContentMap().containsKey(contentId)) {
			cb.onSuccess(getContentMap().get(contentId));
		} else {
			clientFactory.getRpcService().getContent(contentId, new AsyncCallback<IContent> () {
				@Override
				public void onFailure(Throwable caught) {
					cb.onFailure(caught);
				}

				@Override
				public void onSuccess(IContent result) {	
					getContentMap().put(contentId, result);
					cb.onSuccess(result);
				}
			});
		}	
	}
	@Override
	public void saveContent(IContent content, final AsyncCallback<IContent> cb) {
		clientFactory.getRpcService().saveContent(content, new AsyncCallback<IContent> () {
			@Override
			public void onFailure(Throwable caught) {
				cb.onFailure(caught);
			}

			@Override
			public void onSuccess(IContent content) {	
				getContentMap().put(content.getId(), content);
				cb.onSuccess(content);
			}

		});

	}
	private Map<Long, IContent> getContentMap() {
		if (contentMap == null) {
			contentMap = new HashMap<Long,IContent>();
		}
		return contentMap;
	}

	private Map<Long,IClubhouse> getClubhouseMap() {
		if (clubhouseMap == null) {
			clubhouseMap = new HashMap<Long,IClubhouse>();
		}

		return clubhouseMap;
	}

	public void setCurrentRoundOrdinal(final int j, final boolean force) {
		final Core _this = this;
		// first off, does this round make sense for this comp? Is it within the time bounds of the comp? If it is before the first, set it to the first round; if it is after the last set it to the last
		getCurrentComp(new AsyncCallback<ICompetition>() {
			@Override
			public void onSuccess(ICompetition comp) {
				int i = j;
				if (comp != null && comp.getRounds() != null && !comp.getRounds().isEmpty()) {
					if (i < comp.getRounds().get(0).getUrOrdinal()) {
						i = comp.getRounds().get(0).getUrOrdinal();
					} else if (i > comp.getRounds().get(comp.getRounds().size()-1).getUrOrdinal()) {
						i = comp.getRounds().get(comp.getRounds().size()-1).getUrOrdinal();
					}
				} 
		
				if (_this.currentRoundOrdinal != i || force) {
					_this.currentRoundOrdinal = i;
					final int ord = i;
					if (!universalRoundMap.containsKey(i)) {
						clientFactory.getRpcService().getUniversalRound(i, new AsyncCallback<UniversalRound>() {
		
							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
		
							}
		
							@Override
							public void onSuccess(UniversalRound result) {
								universalRoundMap.put(ord,result);
								for (RoundChangeListener l : roundChangeListeners) {
									l.roundChanged(result);
								}
							}
		
						});
					} else {
						for (RoundChangeListener l : roundChangeListeners) {
							l.roundChanged(universalRoundMap.get(i));
						}
					}		
				}
		}
		@Override
		public void onFailure(Throwable caught) {
			Logger.getLogger("Core").log(Level.SEVERE, "Caught in isInitialed()" + caught.getLocalizedMessage());
			
		}



			
		
		});

	}

	public int getCurrentRoundOrdinal() {
		return currentRoundOrdinal;
	}

	public void registerRoundChangeListener(RoundChangeListener l) {
		roundChangeListeners.add(l);
	}

	public void registerGuidChangeListener(GuidChangeListener l) {
		guidChangeListeners.add(l);
	}

	public void changeGuid(String guid) {
		if (this.currentGuid != guid) {
			this.currentGuid = guid;

			for (GuidChangeListener l : guidChangeListeners) {
				l.guidChanged(currentGuid);
			}
		}
	}


	@Override
	public void getSponsor(final Long id, final AsyncCallback<ISponsor> asyncCallback) {
		if (id == null) {
			// return empty sponsor
			asyncCallback.onSuccess(noSponsor);
			return;
		}

		if (sponsorMap == null) {
			sponsorMap = new HashMap<Long, ISponsor>();
		}

		if (sponsorMap.containsKey(id)) {
			asyncCallback.onSuccess(sponsorMap.get(id));
		} else {
			clientFactory.getRpcService().getSponsor(id, new AsyncCallback<ISponsor>() {

				@Override
				public void onFailure(Throwable caught) {
					asyncCallback.onFailure(caught);
				}

				@Override
				public void onSuccess(ISponsor result) {
					sponsorMap.put(id, result);
					asyncCallback.onSuccess(result);					
				}

			});
		}

	}

	HashMap<Long, HashMap<Integer, ArrayList<IMatchGroup>>> virtualCompResultMap = new HashMap<Long, HashMap<Integer, ArrayList<IMatchGroup>>>();

	@Override
	public void getResultsForOrdinal(final int ordinal, final Long virtualCompId, final AsyncCallback<ArrayList<IMatchGroup>> asyncCallback) {
		HashMap<Integer, ArrayList<IMatchGroup>> vCompMap = null;

		// get the map related to the virtual comp in question
		if (virtualCompResultMap.containsKey(virtualCompId)) {
			vCompMap = virtualCompResultMap.get(virtualCompId);
		} else {
			vCompMap = new HashMap<Integer, ArrayList<IMatchGroup>>();
			virtualCompResultMap.put(virtualCompId, vCompMap);
		}

		// do we have the match set we want already?
		if (vCompMap.containsKey(ordinal)) {
			asyncCallback.onSuccess(vCompMap.get(ordinal));
		} else {
			// fetch and cache
			clientFactory.getRpcService().getResultsForOrdinal(ordinal, virtualCompId, new AsyncCallback<ArrayList<IMatchGroup>>() {

				@Override
				public void onFailure(Throwable caught) {
					asyncCallback.onFailure(caught);

				}

				@Override
				public void onSuccess(ArrayList<IMatchGroup> result) {
					virtualCompResultMap.get(virtualCompId).put(ordinal, result);

					asyncCallback.onSuccess(result);

				}

			});
		}


	}

	@Override
	public void getUniversalRound(final int ordinal,
			AsyncCallback<UniversalRound> asyncCallback) {
		if (!universalRoundMap.containsKey(ordinal)) {
			clientFactory.getRpcService().getUniversalRound(ordinal, new AsyncCallback<UniversalRound>() {

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onSuccess(UniversalRound result) {
					universalRoundMap.put(ordinal,result);
					for (RoundChangeListener l : roundChangeListeners) {
						l.roundChanged(result);
					}
				}

			});
		} else {
			for (RoundChangeListener l : roundChangeListeners) {
				l.roundChanged(universalRoundMap.get(ordinal));
			}
		}

	}

	@Override
	public void getContent(String string, final AsyncCallback<IContent> cb) {
		clientFactory.getRpcService().getContent(string, new AsyncCallback<IContent>() {

			@Override
			public void onFailure(Throwable caught) {
				cb.onFailure(caught);
			}

			@Override
			public void onSuccess(IContent result) {
				cb.onSuccess(result); // don't bother caching? @REX

			}

		});

	}

	@Override
	public void getOAuth2Url(String destination, final AsyncCallback<String> cb) {
		clientFactory.getRpcService().getOAuth2Url(destination, new AsyncCallback<String> () {
			@Override
			public void onFailure(Throwable caught) {
				cb.onFailure(caught);
			}

			@Override
			public void onSuccess(String result) {	
				cb.onSuccess(result);
			}

		});	

	}

	public static native void exportShowLoggedIn() /*-{
	   $wnd.showLoggedIn =
	      $entry(@net.rugby.foundation.core.client.Core::showLoggedIn());
	}-*/;

	public static native void exportSetIdentityParent() /*-{
	   //$wnd.setIdentityParent =
	   //   $entry(@net.rugby.foundation.core.client.Core::findParent());
	}-*/;

	public static void showLoggedIn() {
		getCore().getClientFactory().getIdentityManager().showLoggedIn();
	}

	@Override
	public void getContentItems(final AsyncCallback<HashMap<String, Long>> cb) {
		clientFactory.getRpcService().getContentItems(new AsyncCallback<HashMap<String, Long>> () {
			@Override
			public void onFailure(Throwable caught) {
				cb.onFailure(caught);
			}

			@Override
			public void onSuccess(HashMap<String, Long> result) {    
				cb.onSuccess(result);
			}

		});        

	}

	@Override
	public void validateEmail(String email, String validationCode, final AsyncCallback<LoginInfo> cb) {
		clientFactory.getRpcService().validateEmail(email, validationCode, new AsyncCallback<LoginInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				cb.onFailure(caught);			
			}

			@Override
			public void onSuccess(LoginInfo result) {
				cb.onSuccess(result);				
			}
			
		});
		
	}

}
