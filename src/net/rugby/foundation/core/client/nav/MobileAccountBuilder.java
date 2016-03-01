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

	private ListGroupItem dropdown = null;
	private AnchorListItem signUpLink;
	private AnchorListItem signInLink;
	private AnchorListItem signOutLink;
	private AnchorListItem editProfileLink;
	protected Nav parent;
	private DropDownMenu accountManagement = null;
	private Anchor tog = null;
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

		this.dropdown = new ListGroupItem();
		dropdown.setStyleName("dropdown");
		
	}

	@Override
	public void build() {
		try {
			//clear any existing widget links
			if (accountManagement != null) {
				accountManagement.removeFromParent();
			}
			
			if (tog != null) {
				tog.removeFromParent();
			}

			accountManagement = new DropDownMenu(); //HorizontalPanel();
			accountManagement.addStyleName("dropdown-menu-right");
			tog = new Anchor();
			tog.setDataToggle(Toggle.DROPDOWN);
			//tog.setIcon(IconType.USER);
			


			if (clientFactory.getLoginInfo() != null && clientFactory.getLoginInfo().isLoggedIn()) {
//				if (clientFactory.getLoginInfo().getProviderType() == null) {// || !clientFactory.getLoginInfo().getProviderType().equals(ProviderType.facebook)) {
					// native, google+ or facebook
					tog.setHTML(clientFactory.getLoginInfo().getNickname() + "<b class=\"caret\"></b>");
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
//				} else {
//
//					 //Get login status - will update the Facebook UI element in the header appropriately
//					fbCore.getLoginStatus(loginStatusCallback);
//				}

			}
			else {
				tog.setHTML("Account" + "<b class=\"caret\"></b>");
				
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
			if (dropdown != null) {
				dropdown.add(tog);
				dropdown.add(accountManagement);
			}
			
			if (parent != null) {
				parent.add(dropdown);
			}
			
		} catch (Exception e) {
			clientFactory.console(e.getLocalizedMessage());
		}
	}

	public Nav getParent() {
		return parent;
	}

	public void setParent(Nav parent) {
		this.parent = parent;
	}
}
