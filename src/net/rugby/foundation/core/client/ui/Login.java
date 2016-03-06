package net.rugby.foundation.core.client.ui;

import org.gwtbootstrap3.client.ui.Alert;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.AlertType;
import org.gwtbootstrap3.client.ui.html.Span;
import org.gwtbootstrap3.client.ui.html.Strong;
import org.gwtbootstrap3.client.ui.html.Text;

import net.rugby.foundation.model.shared.LoginInfo;
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
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Login extends DialogBox implements net.rugby.foundation.core.client.ui.ExternalAuthenticatorPanel.Presenter
{
	private static CreateAccountUiBinder uiBinder = GWT.create(CreateAccountUiBinder.class);

	interface CreateAccountUiBinder extends UiBinder<Widget, Login>
	{
	}

	public interface Presenter {

		void doOpenIdLogin(LoginInfo.Selector selector);
		void doLogin(String emailAddress, String password);
		void onCancelLogin();
		/**
		 * 
		 */
		void doFacebookLogin();
		/**
		 * 
		 */
		void forgotPassword(String email);
		void doOAuth2Login();
	}
	
	@UiField Panel topPanel;
	@UiField Panel nativePanel;
	@UiField TextBox emailAddress;
	@UiField Input password1;
	@UiField Button login;
	@UiField Button forgot;
	@UiField Button cancel;
	@UiField Row buttonRow;
	@UiField Row forgotLinkRow;
	
	@UiField ExternalAuthenticatorPanel nonNativeLogins;
	@UiField FormGroup passwordGroup;
	@UiField Anchor forgotPassword;
	@UiField Label orLabel;
	@UiField Column captionPanel;
	@UiField Image close;
	@UiField Span title;
	@UiField PanelHeader header;
	
	@UiField Alert error;
	@UiField Strong alertStrong;
	@UiField Text alertText;
	
	Presenter presenter;
	boolean resettingPassword = false;

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public Login()
	{
		setWidget(uiBinder.createAndBindUi(this));
		
		super.setGlassEnabled(true);
		super.setGlassStyleName("dialogGlass");
		super.setAnimationEnabled(true);
		
		topPanel.addStyleName("col-md-8"); 
		topPanel.addStyleName("col-sm-10");
		topPanel.addStyleName("col-xs-12");
		topPanel.addStyleName("col-md-offset-2");
		topPanel.addStyleName("col-xs-offset-1");
		
		
		error.setVisible(false);
		nonNativeLogins.setPresenter(this);
		
		//this.setWidth("1150px");
		nativePanel.setPaddingBottom(20);
		nativePanel.setPaddingTop(20);
		nativePanel.setPaddingLeft(20);
		nativePanel.setPaddingRight(20);
		nativePanel.getElement().getStyle().setBackgroundColor("#eeeeee");
		orLabel.addStyleName("popupCaption");
		orLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		title.addStyleName("popupCaption");//padding
		title.setText("Sign In");
		close.addStyleName("popupCloseButton");//float:right
		captionPanel.addStyleName("panel-default");
		header.addStyleName("panel-header");
		
		buttonRow.addStyleName("text-center");
		forgotLinkRow.addStyleName("text-center");
		forgotLinkRow.addStyleName("padding-top");
		login.addStyleName("text-center");
		forgot.addStyleName("text-center");
		cancel.addStyleName("text-center");
		forgotPassword.addStyleName("text-center");
		
		forgotPassword.addStyleName("padding-left");
		forgotPassword.addStyleName("padding-top");
	}
	
	@UiHandler("password1")
	void onKeyUp(KeyUpEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			doLogin();
		}
	}

	@UiHandler("login")
	void onLoginButtonClicked(ClickEvent event) {
		doLogin();
	}	 
	
	@UiHandler("forgot")
	void onForgotButtonClicked(ClickEvent event) {
		doLogin();
	}	

	@UiHandler("forgotPassword")
	void onForgotPasswordLinkClicked(ClickEvent event) {
		this.setWidth("30em");
		title.setText("Forgot Password");
		error.removeStyleName("alert-danger");
		error.addStyleName("alert-success");
		alertStrong.setText("Enter email: ");
		alertText.setText(" Tell us the email of the account for which you would like to reset the password.");
		error.setVisible(true);
		error.setText("");
		nonNativeLogins.setVisible(false);
		passwordGroup.setVisible(false);
		orLabel.setVisible(false);
		//nativePanel.setWidth("30em");
		cancel.setVisible(false);
		login.setVisible(false);
		forgot.state().reset();
		forgot.setVisible(true);
		
		resettingPassword = true;
		forgotPassword.setVisible(false);
	}	  

	private void doLogin() {
		error.setVisible(false);
		
		if (!resettingPassword) {
			login.state().loading();
			presenter.doLogin(emailAddress.getText(), password1.getText());
		} else {
			forgot.state().loading();
			presenter.forgotPassword(emailAddress.getText());
		}
	}



	@UiHandler("cancel")
	void onCancelButtonClicked(ClickEvent event) {
		//hide();
		presenter.onCancelLogin();
		error.setVisible(false);

	}

	/**
	 * Show as the default login view
	 */
	public void init(){
		this.removeStyleName("disabled");
		title.setText("Sign In");
		passwordGroup.setVisible(true);
		nonNativeLogins.setVisible(true);
		cancel.setVisible(true);
		orLabel.setVisible(true);

		login.state().reset();
		login.setVisible(true);
		
		resettingPassword = false;
		forgotPassword.setVisible(true);
		forgot.state().reset();
		forgot.setVisible(false);
		
		error.setText("");
		error.setVisible(false);
		
		this.setWidth("35em");
		center(); 
	}

	public void showError(String errorMessage) {
		this.removeStyleName("disabled");
		error.setType(AlertType.DANGER);
		login.state().reset();
		forgot.state().reset();
		
		alertStrong.setText("Error ");
		alertText.setText(errorMessage);
		error.setVisible(true);
		emailAddress.setText("");
		password1.setText("");
		
		emailAddress.setFocus(true);

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.ui.ExternalAuthenticatorPanel.Presenter#doOpenIdLogin(java.lang.String)
	 */
	@Override
	public void doOpenIdLogin(LoginInfo.Selector selector) {
		presenter.doOpenIdLogin(selector);
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.ui.ExternalAuthenticatorPanel.Presenter#doFacebookLogin()
	 */
	@Override
	public void doFacebookLogin() {
		alertStrong.setText("Logging in... ");
		alertText.setText(" just a moment while we secure your session.");
		error.setVisible(true);
		error.setType(AlertType.SUCCESS);
		presenter.doFacebookLogin();
		
	}

	@Override
	public void doOAuth2Login() {
		alertStrong.setText("Logging in... ");
		alertText.setText(" just a moment while we secure your session.");
		error.setVisible(true);
		error.setType(AlertType.SUCCESS);
		this.addStyleName("disabled");
		presenter.doOAuth2Login();
		
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

	public void showNonNativeLogins(boolean show) {
		nonNativeLogins.setVisible(show);
		
	}
	
}
