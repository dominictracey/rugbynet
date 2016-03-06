package net.rugby.foundation.core.client.ui;

import java.util.ArrayList;
import java.util.List;

import net.rugby.foundation.model.shared.CoreConfiguration;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICompetition.CompetitionType;
import net.rugby.foundation.model.shared.LoginInfo;

import org.gwtbootstrap3.client.ui.Alert;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Span;
import org.gwtbootstrap3.client.ui.html.Strong;
import org.gwtbootstrap3.client.ui.html.Text;

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
	
	@UiField Button create;
	@UiField Button update;
	@UiField Button changePassword;
	@UiField Button cancel;
	
	@UiField Panel topLevel;
	@UiField LayoutPanel nonNativeLayer;
	@UiField PanelBody nativeLayer;
	@UiField LayoutPanel changePasswordLayer;
	@UiField ExternalAuthenticatorPanel nonNativeLogins;
	@UiField ChangePasswordPanel changePasswordPanel;
	@UiField Anchor showNativeLink;
	@UiField Column captionPanel;
	@UiField Image close;
	@UiField Span title;
	@UiField PanelHeader header;
	@UiField FormGroup compList;
	@UiField Column compListA;
	@UiField Column compListB;
	@UiField FormGroup optOutGroup;
	@UiField CheckBox optOut;
	
	//email validation panel
	@UiField PanelBody emailValidationLayer;
	@UiField TextBox emailValidationCode;
	@UiField Button emailValidationSubmit;
	@UiField Button emailValidationResend;
	@UiField Button emailValidationCancel;
	@UiField TextBox emailValidationEmail;
	
	@UiField Alert alert;
	@UiField Strong alertStrong;
	@UiField Text alertText;
	
//	@UiField Alert success;
//	@UiField Strong successStrong;
//	@UiField Text successText;
	
	Presenter presenter;
	//private boolean editing;
	private boolean compListInitialized;
	private Mode mode;
	
	public enum Mode { CREATE, UPDATE, VALIDATE, CHANGE_PASSWORD }

	public interface Presenter {
		void doCreate(String email, String nickName, String password);
		void doUpdate(String email, String nickName, List<CompetitionType> newList, Boolean optOut);
		void doCancel();
		void doValidateEmail(String email, String emailValidationCode);
		void doResendValidationEmail(String email);
	}
	
	public ManageProfile()
	{
		super.setGlassEnabled(true);
		super.setGlassStyleName("dialogGlass");
		super.setAnimationEnabled(true);

		setWidget(uiBinder.createAndBindUi(this));
		alert.setVisible(false);
		//setText("Account");

		

		nonNativeLogins.setPresenter(this);
		nativeLayer.setSize("50em", "35em");
		nonNativeLayer.setSize("40em", "30em");
		changePasswordLayer.setSize("50em", "40em");
		emailValidationLayer.setSize("50em", "30em");
		showPanels(true,false, false, false, false);

		topLevel.addStyleName("col-md-12"); 
		alert.addStyleName("col-md-12"); 
		alert.setVisible(false);
		alertStrong.setText("Error: ");
//		success.addStyleName("col-md-12"); 
//		success.setVisible(false);
//		successStrong.setText("Success: ");
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
		password1.setVisible(true);
		password2.setVisible(true);
		password1Label.setVisible(true);
		password2Label.setVisible(true);

		showButtons(Mode.CREATE);
		
		emailAddress.setFocus(true);
		showPanels(false, true, false, false, false);
	}

	private void showButtons(Mode m) {
		this.mode = m;
		
		if (m == Mode.CREATE) {
			create.state().reset();
			create.setVisible(true);
			update.setVisible(false);
			changePassword.setVisible(false);
		} else if (m == Mode.UPDATE) {
			update.state().reset();
			create.setVisible(false);
			update.setVisible(true);
			changePassword.setVisible(false);
		} else if (m == Mode.VALIDATE) {
			emailValidationSubmit.state().reset();
			create.setVisible(false);
			update.setVisible(false);
			changePassword.setVisible(false);
		}  else if (m == Mode.CHANGE_PASSWORD) {
			changePassword.state().reset();
			create.setVisible(false);
			update.setVisible(false);
			changePassword.setVisible(true);
		} 
		
	}

	@UiHandler("create")
	void onCreateButtonClicked(ClickEvent event) {
		create.state().loading();
		doCreate();
	}
	
	@UiHandler("update")
	void onUpdateButtonClicked(ClickEvent event) {
		update.state().loading();
		List<CompetitionType> cList = readCompList();
		presenter.doUpdate(emailAddress.getText(), nickName.getText(), cList, optOut.getValue());
	}
	


	@UiHandler("cancel")
	void onCancelButtonClicked(ClickEvent event) {
		presenter.doCancel();
	}

	@UiHandler("password2")
	void onKeyUp(KeyUpEvent event) {
		assert (mode == Mode.CREATE || mode == Mode.CHANGE_PASSWORD);
		
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			if (mode == Mode.CREATE) {
				create.state().loading();
			} else if (mode == Mode.CHANGE_PASSWORD) {
				changePassword.state().loading();
			}
			doCreate();
		} else {
			if (!password2.getText().isEmpty()) {
				if (password2.getText().equals(password1.getText())) {
					if (mode == Mode.CREATE) {
						create.getElement().removeClassName("btn-default"); 
						create.getElement().addClassName("btn-primary");
					} else if (mode == Mode.CHANGE_PASSWORD) {
						changePassword.getElement().removeClassName("btn-default"); 
						changePassword.getElement().addClassName("btn-primary");
					}
				} else {	
					if (mode == Mode.CREATE) {
						create.getElement().addClassName("btn-default"); 
						create.getElement().removeClassName("btn-primary");
					} else  if (mode == Mode.CHANGE_PASSWORD) {
						changePassword.getElement().addClassName("btn-default"); 
						changePassword.getElement().removeClassName("btn-primary");
					}
				}
			}
		}
	}
	
	@UiHandler("emailValidationSubmit")
	void onValidateEmail(ClickEvent event) {
		emailValidationSubmit.state().loading();
		presenter.doValidateEmail(emailValidationEmail.getText(), emailValidationCode.getText());
	}
	
	@UiHandler("emailValidationResend")
	void onResendValidationEmail(ClickEvent event) {
		emailValidationResend.state().loading();
		presenter.doResendValidationEmail(emailValidationEmail.getText());
	}
	
	@UiHandler("emailValidationCancel") 
	void onValidateCancel(ClickEvent event) {
		presenter.doCancel();
	}
	
	public void init(LoginInfo loginInfo) {

		assert(presenter != null);
		alert.setVisible(false);

		compList.setVisible(false);
		optOutGroup.setVisible(false);
		
		if(loginInfo != null) {
			if (loginInfo.isLoggedIn())	{
				// if they are logged in - allow them to only edit their screen name
				// also the comp list
				mode = Mode.UPDATE;
				showButtons(mode);
				emailAddress.setText(loginInfo.getEmailAddress());
				emailAddress.setEnabled(false);
				nickName.setText(loginInfo.getNickname());
				title.setText("Update Profile");
				if (loginInfo.getNickname() != null) {
					nickName.setSelectionRange(0, loginInfo.getNickname().length());
				}
				password1.setVisible(false);
				password2.setVisible(false);
				password1Label.setVisible(false);
				password2Label.setVisible(false);
				nickName.setFocus(true);
				
				nativeLayer.setSize("50em", "45em");
				initCompList(loginInfo);
				compList.setVisible(true);
				optOutGroup.setVisible(true);
				optOut.setValue(loginInfo.getOptOut());
				
				update.addStyleName("btn-primary");	

				showPanels(false, true, false, false, false);
			} else if (loginInfo.getMustChangePassword()) {
				showPanels(false,false,false,true, false);
				
				mode = Mode.CHANGE_PASSWORD;
				showButtons(mode);
				changePasswordPanel.init(loginInfo);
				changePasswordPanel.oldPassword.setText("");
				changePasswordPanel.password1.setText("");
				changePasswordPanel.password2.setText("");
				
				if (loginInfo.getStatus() != null && !loginInfo.getStatus().isEmpty()) {
					showMessage(loginInfo.getStatus());
				}
				changePassword.removeStyleName("btn-primary");		
				
				title.setText("Pick a new password");
			} else if (!loginInfo.isEmailValidated() && loginInfo.getEmailAddress() != null && !loginInfo.getEmailAddress().isEmpty()) {
				showPanels(false, false, false, false, true);
				title.setText("Validate");
				
				mode = Mode.VALIDATE;
				emailValidationResend.state().reset();
				emailValidationSubmit.state().reset();
				
				if (loginInfo.getStatus() != null && !loginInfo.getStatus().isEmpty()) {
					showMessage(loginInfo.getStatus());
				}
				
				//showButtons(mode);
				emailValidationEmail.setText(loginInfo.getEmailAddress());
				emailValidationCode.setEnabled(true);
				emailValidationCode.setFocus(true);
			} else  {
				showPanels(true, false, false, false, false);
				create.removeStyleName("btn-primary");
				mode = Mode.CREATE;
				showButtons(mode);
				
				emailAddress.setEnabled(true);
				emailAddress.setFocus(true);
			}
		}	else {
			// should never get here?
			assert(false);
			showPanels(true, false, false, false, false);
			password1.setVisible(true);
			password2.setVisible(true);
			password1Label.setVisible(true);
			password2Label.setVisible(true);
			
			create.removeStyleName("btn-primary");
			mode = Mode.CREATE;
			showButtons(mode);
			
			emailAddress.setFocus(true);
		}

		center();

	}

	private void initCompList(LoginInfo loginInfo) {
		if (!compListInitialized) {
			boolean left = true;
			for (ICompetition.CompetitionType ct : ICompetition.CompetitionType.values()) {
				if (ct.getShowToClient()) {
					//Label label = new Label(ct.getDisplayName());
					CheckBox cb = new CheckBox();
					cb.setText(ct.getDisplayName());
					if (left) {
						compListA.add(cb);
					} else {
						compListB.add(cb);
					}
					left = !left;
					
					cb.setFormValue(Integer.toString(ct.ordinal()));
					if (loginInfo.getCompList().contains(ct)) {
						cb.setValue(true);
					} else {
						cb.setValue(false);
					}
				}
			}
			compListInitialized = true;
		}
		
	}

	private List<CompetitionType> readCompList() {
		// collect the compList
		List<CompetitionType> newList = new ArrayList<CompetitionType>();
		for (int i=0; i<compListA.getWidgetCount(); ++i) {
			CheckBox cb = (CheckBox) compListA.getWidget(i);
	
			if (cb != null) {
				if (cb.getValue()) {
					newList.add(CompetitionType.values()[Integer.parseInt(cb.getFormValue())]);
				}
			}
		}
		
		for (int i=0; i<compListB.getWidgetCount(); ++i) {
			CheckBox cb = (CheckBox) compListB.getWidget(i);
			if (cb != null) {
				if (cb.getValue()) {
					newList.add(CompetitionType.values()[Integer.parseInt(cb.getFormValue())]);
				}
			}
		}
		return newList;
	}
	/*
	 * When the user clicks a link we email them with the new password, it starts a profile activity which calls Identity.handlePasswordReset, which calls this
	 * We need to show the changePassword panel, with the temp password filled in.
	 */
	public void collectNewPassword(String email, String tempPassword) {
		showPanels(false,false,false,true, false);
		
		mode = Mode.CHANGE_PASSWORD;
		showButtons(mode);
		
		alert.setVisible(false);
		if (email != null && !email.isEmpty()) {
			changePasswordPanel.emailAddress.setText(email);
			changePasswordPanel.emailAddress.setEnabled(false);
		} else {
			changePasswordPanel.emailAddress.setEnabled(true);
		}
		changePasswordPanel.oldPassword.setText(tempPassword);
		changePasswordPanel.password1.setText("");
		changePasswordPanel.password2.setText("");
		
		title.setText("Change Password");
		password1.setFocus(true);
		
		center();
	}
	
	public void collectNewPassword(String email, String tempPassword, String strong, String message) {
		changePasswordPanel.alertStrong.setText(strong);
		changePasswordPanel.alertText.setText(message);
		collectNewPassword(email, tempPassword);
	}
	
	private void doCreate() {
		if (nickName.getText().isEmpty()) {
			showError(CoreConfiguration.getCreateacctErrorNicknameCantBeNull());
		} else if (!password1.getText().equals(password2.getText())) {			
			alertStrong.setText("Error: ");
			alertText.setText("Passwords don't match!");
			alert.removeStyleName("alert-success");
			alert.addStyleName("alert-error");
			alert.setVisible(true);
			
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
		emailValidationSubmit.state().reset();
		emailValidationResend.state().reset();
		create.state().reset();
		update.state().reset();
		changePassword.state().reset();
		
		if (errorMessage.equals("")) {
			alertStrong.setText("");
			alertText.setText("");
			alert.setVisible(false);
		} else if (errorMessage != null) {
			alertStrong.setText("Error: ");
			alertText.setText(errorMessage);
			alert.removeStyleName("alert-success");
			alert.addStyleName("alert-error");
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
		
			alert.setVisible(true);
		}
		center();
	}
	
	public void showMessage(String message) {
		alertStrong.setText("");
		alertText.setText(message);
		alert.removeStyleName("alert-error");
		alert.addStyleName("alert-success");
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

	public ChangePasswordPanel getChangePasswordPanel() {
		return changePasswordPanel;
	}
}
