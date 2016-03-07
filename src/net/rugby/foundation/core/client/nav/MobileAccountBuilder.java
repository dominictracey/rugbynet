package net.rugby.foundation.core.client.nav;

import net.rugby.foundation.core.client.CoreClientFactory;
import net.rugby.foundation.model.shared.LoginInfo.ProviderType;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.DropDown;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.ListGroupItem;
import org.gwtbootstrap3.client.ui.Nav;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import com.google.gwt.event.dom.client.ClickHandler;


public class MobileAccountBuilder extends AccountBuilder {

	private AnchorListItem signUpLink;
	private AnchorListItem signInLink;
	private AnchorListItem signOutLink;
	private AnchorListItem editProfileLink;
	private DropDownMenu accountManagement = null;

	private CoreClientFactory clientFactory;
	private ClickHandler signInHandler;
	private ClickHandler signUpHandler;
	private ClickHandler signOutHandler;
	private ClickHandler editProfileHandler;
	
	@SuppressWarnings("unused")
	private MobileAccountBuilder() {}; //use the ctor with params please
	
	public MobileAccountBuilder(CoreClientFactory clientFactory, ClickHandler signInHandler, ClickHandler signUpHandler, ClickHandler editProfileHandler, ClickHandler signOutHandler) {
		this.clientFactory = clientFactory;
		this.signInHandler = signInHandler;
		this.signUpHandler = signUpHandler;
		this.signOutHandler = signOutHandler;
		this.editProfileHandler = editProfileHandler;		
	}

	@Override
	public void build() {
		try {
			assert(accountManagement != null);
			accountManagement.clear();
			
			if (clientFactory.getLoginInfo() != null && clientFactory.getLoginInfo().isLoggedIn()) {

					signOutLink = new AnchorListItem("sign out");
					signOutLink.setIcon(IconType.UNLOCK);
					signOutLink.addClickHandler(signOutHandler);
					signOutLink.addStyleDependentName("IdentityButton");
					editProfileLink = new AnchorListItem("Profile"); 				
					editProfileLink.addClickHandler(editProfileHandler);
					editProfileLink.addStyleDependentName("IdentityButton");
					editProfileLink.setIcon(IconType.COG);
					accountManagement.add(editProfileLink);			  		
					accountManagement.add(signOutLink);
					signOutLink.setVisible(true);
					editProfileLink.setVisible(true);


			}
			else {

				
				signInLink = new AnchorListItem("sign in");
				signInLink.setIcon(IconType.KEY);
				signInLink.addClickHandler(signInHandler);
				signInLink.addStyleDependentName("IdentityButton");
				signUpLink = new AnchorListItem("sign up");
				signUpLink.addStyleDependentName("IdentityButton");
				signUpLink.setIcon(IconType.LOCK);
				signUpLink.addClickHandler(signUpHandler);

				accountManagement.add(signInLink);
				accountManagement.add(signUpLink);
				signUpLink.setVisible(true);
				signInLink.setVisible(true);

			}	

			
		} catch (Exception e) {
			clientFactory.console(e.getLocalizedMessage());
		}
	}

	public DropDownMenu getParent() {
		return accountManagement;
	}

	public void setParent(DropDownMenu mobileParent) {
		this.accountManagement = mobileParent;
	}
}
