package net.rugby.foundation.core.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.client.ui.ExternalAuthenticatorPanel;
import net.rugby.foundation.core.client.ui.ChangePasswordPanel;
import net.rugby.foundation.core.client.ui.Login;
import net.rugby.foundation.core.client.ui.ManageProfile;
import net.rugby.foundation.core.shared.IdentityTypes.Actions;
import net.rugby.foundation.model.shared.CoreConfiguration;
import net.rugby.foundation.model.shared.LoginInfo;
import net.rugby.foundation.model.shared.LoginInfo.ProviderType;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.NavPills;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.gwtfb.client.Callback;
import com.gwtfb.client.JSOModel;
import com.gwtfb.sdk.FBCore;

public class Identity implements ManageProfile.Presenter, Login.Presenter, ExternalAuthenticatorPanel.Presenter, ChangePasswordPanel.Presenter {

	public interface Presenter {
		/**
		 * Notifications of login status are sent here.
		 */
		void onLoginComplete(String destination);
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

	private FBCore fbCore;


	CoreClientFactory clientFactory = null;
	NavPills parent = null;
	Button signUpLink;
	Button signInLink;
	Button signOutLink;
	Button editProfileLink;

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
	LoginStatusCallback loginStatusCallback = null;

	public interface checkLoginStatusCallback {
		void onLoginStatusChecked(LoginInfo loginInfo);
	}

	public NavPills getParent() {
		return parent;
	}

	public void setParent(NavPills parent) {
		this.parent = parent;
	}

	HorizontalPanel accountManagement = null;

	public Identity(CoreClientFactory clientFactory) {
		super();
		this.clientFactory = clientFactory;

		fbCore = GWT.create(FBCore.class);
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
		loginStatusCallback = new LoginStatusCallback();
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
			getManageProfileDialog().init(clientFactory.getLoginInfo());
		}	
	};

	private ClickHandler signInHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			getLoginDialog().init();			
		}	
	};

	private ClickHandler signOutHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {

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
			getManageProfileDialog().init(clientFactory.getLoginInfo());			
		}	
	};

	public CoreClientFactory getClientFactory() {
		return clientFactory;
	}

	public void setClientFactory(CoreClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	public void showLoggedIn() {
		//clear any existing widget links
		if (accountManagement != null) {
			accountManagement.removeFromParent();
		}

		accountManagement = new HorizontalPanel();
		parent.add(accountManagement);

		if (clientFactory.getLoginInfo() != null && clientFactory.getLoginInfo().isLoggedIn()) {
			if (clientFactory.getLoginInfo().getProviderType() == null || !clientFactory.getLoginInfo().getProviderType().equals(ProviderType.facebook)) {
				// native or openid
				signOutLink = new Button("sign out");
				signOutLink.setIcon(IconType.UNLOCK);
				signOutLink.addClickHandler(signOutHandler);
				editProfileLink = new Button(clientFactory.getLoginInfo().getNickname());
				editProfileLink.addClickHandler(editProfileHandler);
				editProfileLink.setIcon(IconType.COG);
				accountManagement.add(editProfileLink);			  		
				accountManagement.add(signOutLink);
				signOutLink.setVisible(true);
				editProfileLink.setVisible(true);
			} else {

				// Get login status - will update the Facebook UI element in the header appropriately
				fbCore.getLoginStatus(loginStatusCallback);
			}

		}
		else {
			signInLink = new Button("sign in");
			signInLink.setIcon(IconType.LOCK);
			signInLink.addClickHandler(signInHandler);
			signUpLink = new Button("sign up");
			signUpLink.setIcon(IconType.PLUS_SIGN);
			signUpLink.addClickHandler(signUpHandler);

			accountManagement.add(signInLink);
			accountManagement.add(signUpLink);
			signUpLink.setVisible(true);
			signInLink.setVisible(true);

		};		
	}

	// Callback used when checking login status at facebook
	class LoginStatusCallback extends Callback<JavaScriptObject> {
		public void onSuccess ( JavaScriptObject response ) {
			JSOModel jso = response.cast ();
			if ( jso.hasKey ( "error" ) ) {
				actionsComplete(new LoginInfo());
				return;
			}

			if (accountManagement != null) {
				accountManagement.removeFromParent();
			}
			accountManagement = new HorizontalPanel();
			parent.add(accountManagement);
			
			String status = jso.get("status");

			if (status.equals("connected")) {
				// @TODO show the facebook user's name in the top bar & sign out link
				signOutLink = new Button("sign out");
				signOutLink.setIcon(IconType.UNLOCK);
				signOutLink.addClickHandler(signOutHandler);
				editProfileLink = new Button(clientFactory.getLoginInfo().getNickname());
				editProfileLink.setIcon(IconType.COG);
				editProfileLink.addClickHandler(editProfileHandler);	
				accountManagement.add(editProfileLink);
				accountManagement.add(signOutLink);
				signOutLink.setVisible(true);
				editProfileLink.setVisible(true);
			} else if (status.equals("disconnected")) {
				// @TODO show login button in the top bar
				HTML facebookLogin = new HTML ( "<div style='margin-top: 2px; float: right;'><div class='fb-login-button' autologoutlink='true' scope='email' /> </div>");
				accountManagement.add(facebookLogin);
				accountManagement.add(sep);				
			} else {
				// @TODO unknown - so they have de-authorized us. Just show the normal sign in | sign up
				signInLink = new Button("sign in");
				signInLink.setIcon(IconType.LOCK);
				signInLink.addClickHandler(signInHandler);
				signUpLink = new Button("sign up");
				signUpLink.setIcon(IconType.PLUS_SIGN);
				signUpLink.addClickHandler(signUpHandler);

				accountManagement.add(signInLink);
				accountManagement.add(signUpLink);
				signUpLink.setVisible(true);
				signInLink.setVisible(true);
			}
		}
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
						getManageProfileDialog().init(result);
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
		Logger.getLogger("").log(Level.FINE, this.getClass().toString() + "Destination: " + destination);

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
				if (result==null || !result.isLoggedIn()) {
					getLoginDialog().showError("Incorrect email or password");

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
			clientFactory.getRpcService().createAccount(email, nickName, password, false, false, new AsyncCallback<LoginInfo>() {
				public void onSuccess(LoginInfo result) {
					if (!result.isLoggedIn()) {
						getManageProfileDialog().showError(result.getStatus());

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
					getLoginDialog().showError("You don't need your password! Just click on the button you signed up with to the left.");
					//actionsComplete(result);							
				} else if (result.isFacebook() && !result.isLoggedIn()){ // 2b. use your external authenticator
					getLoginDialog().showError("You don't need your password! Just click on the Facebook button to the left.");
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
		
		clientFactory.getRpcService().changePassword(email, oldPassword, newPassword, new AsyncCallback<LoginInfo>() {
			public void onSuccess(LoginInfo result) {
				if (!result.isLoggedIn()) {
					// bad email or password (probably password)
					getManageProfileDialog().init(result);
					getManageProfileDialog().showError("Old password incorrect");
				} else {
					getManageProfileDialog().hide();
				}
				
				actionsComplete(result);
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
}
