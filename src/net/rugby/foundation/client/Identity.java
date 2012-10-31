package net.rugby.foundation.client;

import net.rugby.foundation.client.event.AccountActionCompleteEvent;
import net.rugby.foundation.client.place.Home;
import net.rugby.foundation.client.place.Home.actions;
import net.rugby.foundation.model.shared.Group.GroupType;
import net.rugby.foundation.model.shared.LoginInfo;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class Identity {
	ClientFactory clientFactory = null;
	HorizontalPanel parent = null;
	Anchor signUpLink;
	Anchor signInLink;
	Anchor signOutLink;
	Label sep = new HTML("&nbsp;&nbsp;| ");

	interface checkLoginStatusCallback {
		void onLoginStatusChecked(LoginInfo loginInfo);
	}
	
	public HorizontalPanel getParent() {
		return parent;
	}

	public void setParent(HorizontalPanel parent) {
		this.parent = parent;
	}

	HorizontalPanel accountManagement = null;
	
	public Identity(ClientFactory clientFactory, HorizontalPanel widget) {
		super();
		this.clientFactory = clientFactory;
		this.parent = widget;
		


	}
	
	public Identity() {

	}

	public void checkLoginStatusAsync(final checkLoginStatusCallback cb) {
		// Check login status
		clientFactory.getRpcService().login(Document.get().getURL() + "#Home:1+MY+0+0+NONE", new AsyncCallback<LoginInfo>() {


			public void onFailure(Throwable error) {
				LoginInfo loginInfo = new LoginInfo();
				clientFactory.setLoginInfo(loginInfo);
				cb.onLoginStatusChecked(loginInfo);
				//clientFactory.getPlaceController().goTo(new Home(clientFactory.getHomeID(),GroupType.MY,0L,0L,actions.NONE));
		      }

		      public void onSuccess(LoginInfo result) {
		    	clientFactory.setLoginInfo(result);
		    	showLoggedIn();
				cb.onLoginStatusChecked(result);

			  	clientFactory.getEventBus().fireEvent(new AccountActionCompleteEvent(actions.LOGIN));
		      }
		    });
	    

	}
	

	protected ClickHandler signUpHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			clientFactory.getPlaceController().goTo(new Home(clientFactory.getHomeID(),GroupType.MY,0L,0L,actions.CREATE));
		}	
	};
	
	private ClickHandler signInHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			clientFactory.getPlaceController().goTo(new Home(clientFactory.getHomeID(),GroupType.MY,0L,0L,actions.LOGIN));
		}	
	};
	
	private ClickHandler signOutHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			clientFactory.getPlaceController().goTo(new Home(clientFactory.getHomeID(),GroupType.TEAM,0L,0L,actions.LOGOUT));
		}	
	};


	public ClientFactory getClientFactory() {
		return clientFactory;
	}

	public void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	public void showLoggedIn() {
	  	//clear any existing widget links
    	if (accountManagement != null) {
    		accountManagement.removeFromParent();
    	}
    	  
    	accountManagement = new HorizontalPanel();
		parent.add(accountManagement);
		
		if (clientFactory.getLoginInfo().isLoggedIn()) {
  			signOutLink = new Anchor("sign out");
  			signOutLink.addClickHandler(signOutHandler);
  			Label name = new Label(clientFactory.getLoginInfo().getNickname());
  			accountManagement.add(name);			  		
  			accountManagement.add(sep);
	  		accountManagement.add(signOutLink);
  			signOutLink.setVisible(true);
  			if (clientFactory.getLoginInfo().isAdmin()) {
  				clientFactory.getFooter().showAdminLink(true);
  			} else {
  				clientFactory.getFooter().showAdminLink(false);			  				
  			}

  		}
  		else {
  			signInLink = new Anchor("sign in");
  			signInLink.addClickHandler(signInHandler);
  			signUpLink = new Anchor("sign up");
  			signUpLink.addClickHandler(signUpHandler);
  			
	  		accountManagement.add(signUpLink);
	  		accountManagement.add(sep);
	  		accountManagement.add(signInLink);

	  		clientFactory.getFooter().showAdminLink(false);	
	  		
  			signUpLink.setVisible(true);
  			signInLink.setVisible(true);

  		};		
	}
	
}
