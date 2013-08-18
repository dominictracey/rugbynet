package net.rugby.foundation.core.client.ui;

import net.rugby.foundation.model.shared.CoreConfiguration;
import net.rugby.foundation.model.shared.LoginInfo;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ManageProfile extends DialogBox implements ExternalAuthenticatorPanel.Presenter, FacebookRegistrationPanel.Presenter
{
	private static CreateAccountUiBinder uiBinder = GWT.create(CreateAccountUiBinder.class);

	interface CreateAccountUiBinder extends UiBinder<Widget, ManageProfile>
	{
	}

	@UiField TextBox emailAddress;
	@UiField TextBox nickName;
	@UiField PasswordTextBox password1;
	@UiField PasswordTextBox password2;
	@UiField ControlLabel password1Label;
	@UiField ControlLabel password2Label;
	@UiField Button submit;
	@UiField Button cancel;
	@UiField Label error;
	@UiField FlowPanel topLevel;
	@UiField LayoutPanel nonNativeLayer;
	@UiField LayoutPanel nativeLayer;
	@UiField LayoutPanel facebookLayer;
	@UiField LayoutPanel changePasswordLayer;
	@UiField ExternalAuthenticatorPanel nonNativeLogins;
	@UiField FacebookRegistrationPanel facebookPanel;
	@UiField ChangePasswordPanel changePasswordPanel;
	@UiField Anchor showNativeLink;
	
	Presenter presenter;
	private boolean editing;
	

	public interface Presenter {
		void doCreate(String email, String nickName, String password);
		void doUpdate(String email, String nickName);
		void doCancel();
	}
	
	public ManageProfile()
	{
//		setModal(true);
//		setAutoHideEnabled(true);

		setWidget(uiBinder.createAndBindUi(this));
		error.setVisible(false);
		setText("Create Rugby Network Account");
//		String style = DOM.getElementAttribute(this.getElement(), "style");
//		style += " width:550px;";
//		DOM.setElementAttribute(this.getElement(), "style", style);
		

		nonNativeLogins.setPresenter(this);
		showPanels(true,false, false, false);
		//nativeLayer.setWidgetTopHeight(nativeSon, 0, Style.Unit.EM, 200, Style.Unit.EM);	
		//nativeLayer.forceLayout();
	}

	@UiHandler("showNativeLink")
	void onShowNativeButtonClicked(ClickEvent event) {
		//nativeLayer.setWidgetTopHeight(nativeLayer.getW), 0, Style.Unit.EM, 40, Style.Unit.EM);	
		//nonNativeLayer.setSize("0em", "0em");
		//nonNativeLayer.animate(3000);
		showPanels(false, true, false, false);
		//nativeLayer.animate(3000);
	}

	@UiHandler("submit")
	void onSubmitButtonClicked(ClickEvent event) {
		doCreate();
	}
	
	@UiHandler("cancel")
	void onCancelButtonClicked(ClickEvent event) {
		presenter.doCancel();
	}

	@UiHandler("password2")
	void onKeyUp(KeyUpEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			doCreate();
		} else {
			if (!password2.getText().isEmpty()) {
				if (password2.getText().equals(password1.getText())) {
					submit.getElement().setAttribute("type","PRIMARY");
				} else {
					submit.getElement().removeAttribute("type");
				}
			}
		}
	}

	public void init(LoginInfo loginInfo) {

		assert(presenter != null);
		error.setVisible(false);

		editing = false;
		
		if(loginInfo != null) {
			if (loginInfo.isLoggedIn())	{
				// if they are logged in - allow them to only edit their screen name
				editing = true;
				emailAddress.setText(loginInfo.getEmailAddress());
				emailAddress.setEnabled(false);
				nickName.setText(loginInfo.getNickname());
				if (loginInfo.getNickname() != null) {
					nickName.setSelectionRange(0, loginInfo.getNickname().length());
				}
				password1.setVisible(false);
				password2.setVisible(false);
				password1Label.setVisible(false);
				password2Label.setVisible(false);
				nickName.setFocus(true);
				
				submit.setText("Update");

				showPanels(false, true, false, false);
			} else if (loginInfo.getMustChangePassword()) {
				showPanels(false,false,false,true);
				error.setText(" Check your email for your temporary password.");
				error.setVisible(true);
				submit.setText("Change Password");
			} else  {
				showPanels(true, false, false, false);
				submit.setText("Sign Up");
				emailAddress.setFocus(true);
			}
		}	else {
			// should never get here
			assert(false);
			showPanels(true, false, false, false);
			submit.setText("Sign Up");
			emailAddress.setFocus(true);
		}

		center();

	}


	void doCreate() {
		if (nickName.getText().isEmpty()) {
			showError(CoreConfiguration.getCreateacctErrorNicknameCantBeNull());
		} else if (editing) {
			presenter.doUpdate(emailAddress.getText(), nickName.getText());
		} else if (password1.getText().equals(password2.getText())) {
			presenter.doCreate(emailAddress.getText(), nickName.getText(), password1.getText());
		} else {
			error.setText("Passwords don't match!");
			error.setVisible(true);
			password1.setText("");
			password2.setText("");
			password1.setFocus(true);
		}
	}
	
	public void setPresenter(Presenter p) {
		presenter = p;
		if (p instanceof ChangePasswordPanel.Presenter) {
			changePasswordPanel.setPresenter((ChangePasswordPanel.Presenter)p);
		}
	}

//	/**
//	 * @param isNative set to false if you want to hide password and disable email
//	 */
//	public void setNative(boolean isNative) {
//		this.isNative = isNative;
//
//	}
	
	public void showError(String errorMessage) {
		if (errorMessage != null) {
			error.setText(errorMessage);
			 if (errorMessage.equals(CoreConfiguration.getCreateacctErrorExists()) ||
					 errorMessage.equals(CoreConfiguration.getCreateacctErrorInvalidEmail())) {
				 emailAddress.setText("");
				 emailAddress.setFocus(true);
			 } else if (errorMessage.equals(CoreConfiguration.getCreateacctErrorNicknameExists())) {
				 nickName.setText("");
				 nickName.setFocus(true);
			 } else if (errorMessage.equals(CoreConfiguration.getCreateacctErrorPasswordTooShort())) {
				 password1.setText("");
				 password2.setText("");
				 password1.setFocus(true);
			 } else if (errorMessage.equals(CoreConfiguration.getCreateacctErrorNicknameCantBeNull())) {
				 nickName.setFocus(true);
			 }
		} else {
			error.setText("Unknown Error. Please try again later.");
		}
		
		error.setVisible(true);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.ui.ExternalAuthenticatorPanel.Presenter#doOpenIdLogin(java.lang.String)
	 */
	@Override
	public void doOpenIdLogin(LoginInfo.Selector selector) {
		if (presenter instanceof ExternalAuthenticatorPanel.Presenter)
			((ExternalAuthenticatorPanel.Presenter)presenter).doOpenIdLogin(selector);
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.ui.ExternalAuthenticatorPanel.Presenter#doFacebookLogin()
	 */
	@Override
	public void doFacebookLogin() {
		if (presenter instanceof ExternalAuthenticatorPanel.Presenter)
			((ExternalAuthenticatorPanel.Presenter)presenter).doFacebookLogin();
			//showPanels(false,false,true);
	}
	
	private void showPanels(boolean showNonNative, boolean showNative, boolean showFacebook, boolean changePassword) {
		if (showNonNative)
			nonNativeLayer.setSize("40em", "30em");
		else
			nonNativeLayer.setSize("0em", "0em");
			
		
		if (showNative)
			nativeLayer.setSize("50em", "35em");
		else
			nativeLayer.setSize("0em", "0em");
		
		if (showFacebook)
			facebookLayer.setSize("50em", "20em");
		else
			facebookLayer.setSize("0em", "0em");
		
		if (changePassword)
			changePasswordLayer.setSize("50em", "20em");
		else
			changePasswordLayer.setSize("0em", "0em");
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.ui.FacebookRegistrationPanel.Presenter#getDestination()
	 */
	@Override
	public String getDestination() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.ui.FacebookRegistrationPanel.Presenter#getAppId()
	 */
	@Override
	public String getAppId() {
		// TODO Auto-generated method stub
		return null;
	}


}
