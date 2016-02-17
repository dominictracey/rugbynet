package net.rugby.foundation.core.client.ui;

import net.rugby.foundation.model.shared.CoreConfiguration;
import net.rugby.foundation.model.shared.LoginInfo;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Span;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class ManageProfile extends DialogBox implements ExternalAuthenticatorPanel.Presenter//, FacebookRegistrationPanel.Presenter
{
	private static ManageProfileUiBinder uiBinder = GWT.create(ManageProfileUiBinder.class);

	interface ManageProfileUiBinder extends UiBinder<Widget, ManageProfile>
	{
	}

	@UiField TextBox emailAddress;
	@UiField TextBox nickName;
	@UiField Input password1;
	@UiField Input password2;
	@UiField Label password1Label;
	@UiField Label password2Label;
	
	@UiField Button submit;
	@UiField Button cancel;
	@UiField Label error;
	@UiField Panel topLevel;
	@UiField LayoutPanel nonNativeLayer;
	@UiField PanelBody nativeLayer;
//	@UiField LayoutPanel facebookLayer;
	@UiField LayoutPanel changePasswordLayer;
	@UiField ExternalAuthenticatorPanel nonNativeLogins;
//	@UiField FacebookRegistrationPanel facebookPanel;
	@UiField ChangePasswordPanel changePasswordPanel;
	@UiField Anchor showNativeLink;
	@UiField Column captionPanel;
	@UiField Image close;
	@UiField Span title;
	@UiField PanelHeader header;
	
	//email validation panel
	@UiField PanelBody emailValidationLayer;
	@UiField TextBox emailValidationCode;
	@UiField Button emailValidationSubmit;
	@UiField Button emailValidationCancel;
	@UiField TextBox emailValidationEmail;
	
	Presenter presenter;
	private boolean editing;
	

	public interface Presenter {
		void doCreate(String email, String nickName, String password);
		void doUpdate(String email, String nickName);
		void doCancel();
		void doValidateEmail(String email, String emailValidationCode);
	}
	
	public ManageProfile()
	{
		super.setGlassEnabled(true);
		super.setGlassStyleName("dialogGlass");
		super.setAnimationEnabled(true);

		setWidget(uiBinder.createAndBindUi(this));
		error.setVisible(false);
		//setText("Account");

		

		nonNativeLogins.setPresenter(this);
		nativeLayer.setSize("50em", "35em");
		nonNativeLayer.setSize("40em", "30em");
		changePasswordLayer.setSize("50em", "30em");
		emailValidationLayer.setSize("50em", "30em");
		showPanels(true,false, false, false, false);

		topLevel.addStyleName("col-md-12"); 
		error.addStyleName("col-md-12"); 
		error.setVisible(false);
		nonNativeLogins.setPresenter(this);
		
		nativeLayer.addStyleName("col-md-8"); 

		title.addStyleName("popupCaption");//padding
		title.setText("Sign Up");
		close.addStyleName("popupCloseButton");//float:right
		captionPanel.addStyleName("panel-default");
		header.addStyleName("panel-header");
		
		emailValidationEmail.addStyleName("hidden");
	}

	@UiHandler("showNativeLink")
	void onShowNativeButtonClicked(ClickEvent event) {

		showPanels(false, true, false, false, false);

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
					submit.getElement().removeClassName("btn-default"); 
					submit.getElement().addClassName("btn-primary");
					//submit.getElement().setAttribute("type","PRIMARY");
				} else {
					submit.getElement().addClassName("btn-default"); 
					submit.getElement().removeClassName("btn-primary");
					//submit.getElement().removeAttribute("type");
				}
			}
		}
	}
	
	@UiHandler("emailValidationSubmit")
	void onValidateEmail(ClickEvent event) {
		presenter.doValidateEmail(emailValidationEmail.getText(), emailValidationCode.getText());
	}

	@UiHandler("emailValidationCancel") 
	void onValidateCancel(ClickEvent event) {
		presenter.doCancel();
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

				showPanels(false, true, false, false, false);
			} else if (loginInfo.getMustChangePassword()) {
				showPanels(false,false,false,true, false);
				error.setText(" Check your email for your temporary password.");
				error.setVisible(true);
				submit.setText("Change Password");
				title.setText("Change Password");
			} else if (!loginInfo.isEmailValidated() && loginInfo.getEmailAddress() != null && !loginInfo.getEmailAddress().isEmpty()) {
				showPanels(false, false, false, false, true);
				title.setText("Validate");
				emailValidationCode.setEnabled(true);
				emailValidationCode.setFocus(true);
			} else  {
				showPanels(true, false, false, false, false);
				submit.setText("Sign Up");
				emailAddress.setEnabled(true);
				emailAddress.setFocus(true);
			}
		}	else {
			// should never get here
			assert(false);
			showPanels(true, false, false, false, false);
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
		} else if (!password1.getText().equals(password2.getText())) {			
			error.setText("Passwords don't match!");
			error.setVisible(true);
			password1.setText("");
			password2.setText("");
			password1.setFocus(true);
		} else {
			emailValidationEmail.setText(emailAddress.getText());
			presenter.doCreate(emailAddress.getText(), nickName.getText(), password1.getText());
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
		if (errorMessage.equals("")) {
			error.setText("");
			error.setVisible(false);
		} else if (errorMessage != null) {
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
		
			error.setVisible(true);
		}
	
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
	
	private void showPanels(boolean showNonNative, boolean showNative, boolean showFacebook, boolean changePassword, boolean emailValidation) {
		if (showNonNative)
			nonNativeLayer.removeStyleName("hidden");
		else
			nonNativeLayer.addStyleName("hidden");
			
		if (!showNative) {			
			nativeLayer.addStyleName("hidden");
		} else {
			nativeLayer.removeStyleName("hidden");
		}
		
		if (changePassword)
			changePasswordLayer.removeStyleName("hidden");
		else
			changePasswordLayer.addStyleName("hidden");
		
		if (emailValidation) 
			emailValidationLayer.removeStyleName("hidden");
		else 
			emailValidationLayer.addStyleName("hidden");
	}

	@Override
	public void doOAuth2Login() {
		if (presenter instanceof ExternalAuthenticatorPanel.Presenter)
			((ExternalAuthenticatorPanel.Presenter)presenter).doOAuth2Login();
		
	}

	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event)
	{
		 NativeEvent nativeEvent = event.getNativeEvent();
		  
		 if (!event.isCanceled()
		 && (event.getTypeInt() == Event.ONCLICK)
		 && isCloseEvent(nativeEvent))
		 {
			 this.hide();
		 }
		 super.onPreviewNativeEvent(event);
	}

	private boolean isCloseEvent(NativeEvent event)
	{
		return event.getEventTarget().equals(close.getElement());//compares equality of the underlying DOM elements
	}
}
