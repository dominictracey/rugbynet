package net.rugby.foundation.core.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.client.nav.DesktopAccountBuilder;
import net.rugby.foundation.core.client.ui.ExternalAuthenticatorPanel;
import net.rugby.foundation.core.client.ui.ChangePasswordPanel;
import net.rugby.foundation.core.client.ui.Login;
import net.rugby.foundation.core.client.ui.ManageProfile;
import net.rugby.foundation.core.shared.IdentityTypes.Actions;
import net.rugby.foundation.model.shared.CoreConfiguration;
import net.rugby.foundation.model.shared.LoginInfo;
import org.gwtbootstrap3.client.ui.Nav;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

public class Identity implements ManageProfile.Presenter, Login.Presenter, ExternalAuthenticatorPanel.Presenter, ChangePasswordPanel.Presenter {

	public interface Presenter {
		/**
		 * Notifications of login status are sent here.
		 */
		void onLoginComplete(String destination);
		void showFacebookComments(boolean show);
	}

//	public enum Actions { login, logout, createFacebook, createOpenId, mergeFacebook, mergeOpenId, done, updateScreenName }
//	public enum Keys { action, selector, destination, providerType }
//
//	public static LoginInfo.ProviderType getProviderType(String string) {
//		if (string.toLowerCase().equals("openid")) {
//			return LoginInfo.ProviderType.openid;
//		} else if (string.toLowerCase().equals("facebook")) {
//			return LoginInfo.ProviderType.facebook;
//		}
//		return null;
//	}
//
//	public static LoginInfo.Selector getSelector(String string) {
//		if (string.toLowerCase().equals("google")) {
//			return LoginInfo.Selector.google;
//		} else if (string.toLowerCase().equals("yahoo")) {
//			return LoginInfo.Selector.yahoo;
//		} if (string.toLowerCase().equals("myspace")) {
//			return LoginInfo.Selector.myspace;
//		} if (string.toLowerCase().equals("aol")) {
//			return LoginInfo.Selector.aol;
//		} if (string.toLowerCase().equals("myopenid_com")) {
//			return LoginInfo.Selector.myopenid_com;
//		}
//		return null;
//	}

//	private FBCore fbCore;


	CoreClientFactory clientFactory = null;

	Label sep = new HTML("&nbsp;&nbsp;| ");
	private Presenter presenter = null;
	Login login = null;
	ManageProfile manageProfile = null;
	private String destination;

	// Facebook stuff
	private boolean status = true;
	private boolean xfbml = true;
	private boolean cookie = true;
	private final String FACEBOOK_DEV_APP_ID="341652969207503";
	private final String FACEBOOK_BETA_APP_ID="401094413237618";
	private final String FACEBOOK_PROD_APP_ID="191288450939341";
//	LoginStatusCallback loginStatusCallback = null;

	protected Nav nav;
	
	public interface checkLoginStatusCallback {
		void onLoginStatusChecked(LoginInfo loginInfo);
	}

//	DropDownMenu accountManagement = null;
//	Anchor tog = null;

	public Identity(CoreClientFactory clientFactory) {
		super();
		this.clientFactory = clientFactory;

//		fbCore = GWT.create(FBCore.class);
		String appId = "";
		
		if (!GWT.isProdMode()) {
			appId = FACEBOOK_DEV_APP_ID;
		} else if (GWT.getModuleBaseURL().contains("beta")) {
			appId = FACEBOOK_BETA_APP_ID;
		} else {
			appId = FACEBOOK_PROD_APP_ID;
		}

		// commented out because no internet at lake on 8/9/13
//		fbCore.init(appId, status, cookie, xfbml);
		
		
		// if they are showing logged in via Facebook we need to check they are still logged in to facebook
		// using the FB.getLoginStatus JS call.
		//loginStatusCallback = new LoginStatusCallback();
	}

	protected ManageProfile getManageProfileDialog() {
		if (manageProfile == null) {
			manageProfile = new ManageProfile();
			manageProfile.setPresenter(this);
		}
		return manageProfile;
	}

	protected Login getLoginDialog() {
		if (login == null) {
			login = new Login();
			login.setPresenter(this);
		}
		return login;
	}
	protected ClickHandler signUpHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			// get a session going to avoid the jsessionid url re-writing problem
			startSession();
			
			//everything after the #
			String href = Window.Location.getHref();
			destination = href.substring(href.indexOf('#')+1);
			if (getManageProfileDialog().getAbsoluteTop() < 100) {
				getManageProfileDialog().setPopupPosition(getManageProfileDialog().getAbsoluteTop(), 100);
			}
			presenter.showFacebookComments(false);
			getManageProfileDialog().init(clientFactory.getLoginInfo());
		}	
	};

	private ClickHandler signInHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			// get a session going to avoid the jsessionid url re-writing problem
			startSession();
			
			//everything after the #
			String href = Window.Location.getHref();
			destination = href.substring(href.indexOf('#')+1);
			if (getLoginDialog().getAbsoluteTop() < 100) {
				getLoginDialog().setPopupPosition(getLoginDialog().getAbsoluteTop(), 100);
			}
			presenter.showFacebookComments(false);
			getLoginDialog().init();			
		}	
	};

	private ClickHandler signOutHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			//everything after the #
			String href = Window.Location.getHref();
			destination = href.substring(href.indexOf('#')+1);
			Core.getCore().logOff(clientFactory.getLoginInfo(),
					new AsyncCallback<LoginInfo>() {
				public void onSuccess(LoginInfo result) {
					actionsComplete(result);
				}

				@Override
				public void onFailure(Throwable caught) {
					actionsComplete(new LoginInfo());		
				}
			});		
		}	
	};

	private ClickHandler editProfileHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			presenter.showFacebookComments(false);
			getManageProfileDialog().init(clientFactory.getLoginInfo());			
		}	
	};

	private DesktopAccountBuilder dab;


	public CoreClientFactory getClientFactory() {
		return clientFactory;
	}

	protected void startSession() {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "/session/");

	    try {
	      Request response = builder.sendRequest(null, new RequestCallback() {
	        public void onError(Request request, Throwable exception) {
	          // Code omitted for clarity
	        }

	        public void onResponseReceived(Request request, Response response) {
	          Logger.getLogger("Identity").log(Level.INFO,"started session");
	        }
	      });
	    } catch (RequestException e) {
	      // Code omitted for clarity
	    }
		
	}

	public void setClientFactory(CoreClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	public void showLoggedIn() {
		if (dab == null) {
			dab = new DesktopAccountBuilder(clientFactory, signInHandler, signUpHandler, editProfileHandler, signOutHandler);
			dab.setParent(nav);
		}
		
		dab.build();

	}

	public Presenter getPresenter() {
		return presenter;
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public void doLoginDev(String email, String password) {
		clientFactory.getRpcService().nativeLogin(email, password,
				new AsyncCallback<LoginInfo>() {
			public void onSuccess(LoginInfo result) {
				if (result.isLoggedIn()) {
					actionsComplete(result);
				} 
			}

			@Override
			public void onFailure(Throwable caught) {
				//bail
				actionsComplete(new LoginInfo());						
			}
		});		
	}

	public ClickHandler getSignInHandler() {
		return signInHandler;
	}

	public ClickHandler getSignUpHandler() {
		return signUpHandler;
	}

	/**
	 * @param action
	 * @param selector
	 */
	public void startProfileProcess(Actions action, LoginInfo.ProviderType providerType, LoginInfo.Selector selector) {
		if (action == Actions.updateScreenName) {

			console("Start Profile Process: Update Screen name");

			// this is where we get sent after we sign up with a non-native ID
			// because that process uses the non-RPC servlet we don't have a client-side copy of 
			// the LoginInfo instance yet. Is is in our session so we have to make a call to get it.
			clientFactory.getRpcService().login(new AsyncCallback<LoginInfo>() {
				@Override
				public void onSuccess(LoginInfo result) {
					if (result.isLoggedIn()) {
						clientFactory.setLoginInfo(result);
						//showLoggedIn();
						//getManageProfileDialog().setNative(false);
						presenter.showFacebookComments(false);
						getManageProfileDialog().init(result);
					} else {
						console("Not logged in so can't update profile");
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					// if we get here not sure what is happening - bail?
					actionsComplete(new LoginInfo());
				}
			});		

		} else if (action == Actions.done) {
			// this is where we get sent after we sign up with a non-native ID
			// because that process uses the non-RPC servlet we don't have a client-side copy of 
			// the LoginInfo instance yet. Is is in our session so we have to make a call to get it.
			clientFactory.getRpcService().login(new AsyncCallback<LoginInfo>() {
				@Override
				public void onSuccess(LoginInfo result) {
					actionsComplete(result);
					console("Profile processing complete");
				}

				@Override
				public void onFailure(Throwable caught) {
					// if we get here not sure what is happening - bail?
					actionsComplete(new LoginInfo());
				}
			});


		}
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		Logger.getLogger("Identity").log(Level.FINE, this.getClass().toString() + "Destination: " + destination);

		this.destination = destination;
	}



	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.ui.ManageProfile.Presenter#doCancel()
	 */
	@Override
	public void doCancel() {
		actionsComplete(new LoginInfo());
		getManageProfileDialog().hide();

	}

	/**
	 * @param string
	 */
	@Override
	public void doOpenIdLogin(LoginInfo.Selector selector) {
		// we want to preserve the url string in case they are trying to do something like join a clubhouse
		clientFactory.getRpcService().getOpenIdUrl(selector, destination, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {				
				getLoginDialog().showError("Something went wrong. Try a different login method.");
			}

			@Override
			public void onSuccess(String result) {
				redirect(result);

			}

		});

	}

	@Override
	public void doLogin(String emailAddress, String password) {
		clientFactory.getRpcService().nativeLogin(emailAddress, password, new AsyncCallback<LoginInfo>() {
			public void onSuccess(LoginInfo result) {
				if (result==null) {
					getLoginDialog().showError("Incorrect email or password");

				} else if (!result.isLoggedIn()) {
					if (result.isOpenId() && !result.isLoggedIn()){ // 2a. use your external authenticator
						getLoginDialog().showNonNativeLogins(true);
						getLoginDialog().showError("You don't need your password! Just click on the button you signed up with above.");
						//actionsComplete(result);							
					} else if (result.isFacebook() && !result.isLoggedIn()){ // 2b. use your external authenticator
						getLoginDialog().showNonNativeLogins(true);
						getLoginDialog().showError("You don't need your password! Just click on the Facebook button above.");
						//actionsComplete(result);							
					}
				} else { 
					assert result.isLoggedIn();
					actionsComplete(result);
					getLoginDialog().hide();
				}

			}

			@Override
			public void onFailure(Throwable caught) {
				getLoginDialog().showError("Problems logging in, please try again later.");

			}});		
	}


	//redirect the browser to the given url
	public static native void redirect(String url)/*-{
	      $wnd.location = url;
	  }-*/;

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.ui.Login.Presenter#onCancelLogin()
	 */
	@Override
	public void onCancelLogin() {
		getLoginDialog().hide();
		actionsComplete(new LoginInfo());
	}

	public void actionsComplete(LoginInfo loginInfo) {
		clientFactory.setLoginInfo(loginInfo);
		showLoggedIn();
		//			
		//		if (destination != null) {
		//			setDestination(null);
		//		}
		presenter.onLoginComplete(destination);	
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.ui.Login.Presenter#doFacebookLogin()
	 */
	@Override
	public void doFacebookLogin() {

		// we want to preserve the url string in case they are trying to do something like join a clubhouse
		clientFactory.getRpcService().getFacebookLoginUrl(destination, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {				
				getLoginDialog().showError("Something went wrong. Try a different login method.");
			}

			@Override
			public void onSuccess(String result) {
				redirect(result);

			}

		});
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.ui.ManageProfile.Presenter#doCreate(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void doCreate(String email, String nickName, String password) {

		LoginInfo loginInfo = clientFactory.getLoginInfo();

		if (!loginInfo.isLoggedIn()) {
			clientFactory.getRpcService().createAccount(email, nickName, password, false, false, false, new AsyncCallback<LoginInfo>() {
				public void onSuccess(LoginInfo result) {
					if (!result.isLoggedIn()) {
						if (!result.isEmailValidated() && (result.getStatus() == null || result.getStatus().isEmpty())) {
							getManageProfileDialog().init(result); // show email validation panel
						} else {
							getManageProfileDialog().showError(result.getStatus());
						}

					} else { 
						actionsComplete(result);
						getManageProfileDialog().hide();

					}

				}

				@Override
				public void onFailure(Throwable caught) {
					getManageProfileDialog().showError("Problems creating account, try again later.");
				}
			});

		} 
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.ui.ManageProfile.Presenter#doUpdate(java.lang.String, java.lang.String)
	 */
	@Override
	public void doUpdate(String email, String nickName) {

		if (nickName.length() == 0) {
			getManageProfileDialog().showError(CoreConfiguration.getCreateacctErrorNicknameCantBeNull());
		} 
		else {
			clientFactory.getRpcService().updateAccount(email, nickName, new AsyncCallback<LoginInfo>() {
				public void onSuccess(LoginInfo result) {
					if (!result.isLoggedIn()) {
						getManageProfileDialog().showError(result.getStatus());
					} else { 
						getManageProfileDialog().hide();
						actionsComplete(result);							
					}

				}

				@Override
				public void onFailure(Throwable caught) {
					clientFactory.setLoginInfo(new LoginInfo());
					getManageProfileDialog().showError("Problems updating account, try again later.");						
				}

			});			  
		}

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.ui.Login.Presenter#forgotPassword()
	 */
	@Override
	public void forgotPassword(final String email) {
		clientFactory.getRpcService().forgotPassword(email, new AsyncCallback<LoginInfo>() {
			public void onSuccess(LoginInfo result) {
				clientFactory.setLoginInfo(result);
				
				// so we can get three results here:
				// 1. Native account exists, password reset and email sent
				//		-> show ManageProfile with change password panel
				// 2. The email provided is not native (externally authenticated)
				//		-> set error on Login panel telling them to pick authenticator button
				// 3. The email is not recognized
				//		-> show ManageProfile saying we couldn't find the email and asking them to sign up
				
				if (result.getEmailAddress() == null) {
					// 3.
					getLoginDialog().hide();
					getManageProfileDialog().init(result);
					getManageProfileDialog().showError("We don't have an account with that email address. Click a button above to sign in.");
				}  else	if (result.getEmailAddress().equals(email) && result.getMustChangePassword()) {
					// 1. changed it - check your email
					getLoginDialog().hide();
					getManageProfileDialog().init(result);
				} else if (result.isOpenId() && !result.isLoggedIn()){ // 2a. use your external authenticator
					getLoginDialog().showNonNativeLogins(true);
					getLoginDialog().showError("You don't need your password! Just click on the button you signed up with above.");
					//actionsComplete(result);							
				} else if (result.isFacebook() && !result.isLoggedIn()){ // 2b. use your external authenticator
					getLoginDialog().showNonNativeLogins(true);
					getLoginDialog().showError("You don't need your password! Just click on the Facebook button above.");
					//actionsComplete(result);							
				}

			}

			@Override
			public void onFailure(Throwable caught) {
				clientFactory.setLoginInfo(new LoginInfo());
				getManageProfileDialog().showError("Problems updating account, try again later.");						
			}

		});			  
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.ui.ChangePasswordPanel.Presenter#doChangePassword(java.lang.String, java.lang.String)
	 */
	@Override
	public void doChangePassword(String oldPassword, String newPassword) {
		String email = clientFactory.getLoginInfo().getEmailAddress();
		getManageProfileDialog().showError("");
		clientFactory.getRpcService().changePassword(email, oldPassword, newPassword, new AsyncCallback<LoginInfo>() {
			public void onSuccess(LoginInfo result) {
				if (!result.isLoggedIn()) {
					// bad email or password (probably password)
					//getManageProfileDialog().init(result);
					getManageProfileDialog().showError("Old password incorrect");
				} else {
					getManageProfileDialog().hide();
					actionsComplete(result);
				}			
			}

			@Override
			public void onFailure(Throwable caught) {
				clientFactory.setLoginInfo(new LoginInfo());
				getManageProfileDialog().showError("Problems updating password, try again later.");						
			}

		});			  
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.ui.ChangePasswordPanel.Presenter#onCancelChangePassword()
	 */
	@Override
	public void onCancelChangePassword() {
		getManageProfileDialog().hide();
		
	}

	@Override
	public void doOAuth2Login() {
		// we want to preserve the url string in case they are trying to do something like join a clubhouse
			clientFactory.getRpcService().getOAuth2Url(destination, new AsyncCallback<String>() {

				@Override
				public void onFailure(Throwable caught) {				
					getLoginDialog().showError("Something went wrong. Try a different login method.");
				}

				@Override
				public void onSuccess(String result) {
					redirect(result);

				}

			});	
			
	}

	public native void console(String text)
	/*-{
	    console.log(text);
	}-*/;

	public Nav getNav() {
		return nav;
	}

	public void setNav(Nav nav) {
		this.nav = nav;
	}


	@Override
	public void doValidateEmail(String email, String emailValidationCode) {
		clientFactory.getRpcService().validateEmail(email,emailValidationCode, new AsyncCallback<LoginInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				getManageProfileDialog().showError("Problem validating email. Please contact support: info@rugby.net");
				
			}

			@Override
			public void onSuccess(LoginInfo result) {
				if (result != null && result.isLoggedIn()) { //result.getStatus().equals("Email successfully validated")) {
					getManageProfileDialog().hide();
					actionsComplete(result);
				} else {
					getManageProfileDialog().showError(result.getStatus() + ". Please try again and if the problem persists contact support: info@rugby.net");
				}
				
			}
			
		});
	}
	
//	private void showLoggedInMobile()
//	{
//		Widget parent = RootPanel.get("sidebar-profile");
//		
//		final ListGroupItem li = new ListGroupItem();
//		Anchor a = new Anchor();
//		a.setHTML("<i class=\"fa fa-laptop\"></i><span>" + compName + "</span><b class=\"caret\"></b>");
//		anchorMap.put(compId, a);
//		liMap.put(compId, li);
//		li.add(a);
//		dashboardMenu.add(li);
//		li.removeStyleName("list-group-item");
//		
//		if (modeMap.isEmpty()) {
//			a.addClickHandler(new ClickHandler() {
//
//				@Override
//				public void onClick(ClickEvent event) {
//					// TODO Auto-generated method stub
//
//				}
//
//			});
//		} else {
//			// first create submenu
//			ListGroup submenu = new ListGroup();
//			submenuMap.put(compId, submenu);
//			submenu.setStyleName("submenu");
//			for (RatingMode mode: modeMap.keySet()) {
//				ListGroupItem lgi = new ListGroupItem();
//				lgi.removeStyleName("list-group-item");
//				Anchor modeLink = new Anchor();
//				modeLink.setText(mode.getMenuName());
//				final RatingMode _mode = mode;
//				modeLink.addClickHandler(new ClickHandler() {
//
//					@Override
//					public void onClick(ClickEvent event) {
//						SeriesPlace place = new SeriesPlace();
//						place.setCompId(compId);
//						place.setSeriesId(modeMap.get(_mode));
//						
//						// remove the carat if it is somewhere else
//						if (caratParent != null && carat != null) {
//							caratParent.remove(carat);
//						}
//						
//						// add the carat
//						li.add(carat);
//						caratParent = li;
//						
//						clientFactory.getPlaceController().goTo(place);
//					}
//					
//				});
//				lgi.add(modeLink);
//				submenu.add(lgi);
//			}
//			li.add(submenu);
//			a.setStyleName("dropdown-toggle");
//
//		}
//	}
}
