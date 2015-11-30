package net.rugby.foundation.core.client.ui;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.TextBox;

import net.rugby.foundation.model.shared.LoginInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
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
	@UiField Button submit;
	@UiField Button cancel;
	@UiField Label error;
	@UiField ExternalAuthenticatorPanel nonNativeLogins;
	@UiField Anchor forgotPassword;
	@UiField Label orLabel;
	
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
		this.setTitle("Login");
		
		orLabel.addStyleName("popupCaption");
		orLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

	}
	
	@UiHandler("password1")
	void onKeyUp(KeyUpEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			doLogin();
		}
	}

	@UiHandler("submit")
	void onSubmitButtonClicked(ClickEvent event) {
		doLogin();
	}	  

	@UiHandler("forgotPassword")
	void onForgotPasswordLinkClicked(ClickEvent event) {
		error.setVisible(true);
		error.setText(" Please enter your email above.");
		cancel.setVisible(false);
		submit.setText("Reset password");
		resettingPassword = true;
		forgotPassword.setVisible(false);
	}	  

	private void doLogin() {
		error.setVisible(false);

		if (!resettingPassword) {
			presenter.doLogin(emailAddress.getText(), password1.getText());
		} else {
			presenter.forgotPassword(emailAddress.getText());
		}
	}



	@UiHandler("cancel")
	void onCancelButtonClicked(ClickEvent event) {
		//hide();
		presenter.onCancelLogin();
		error.setVisible(false);

	}

	public void init(){
		cancel.setVisible(true);
		submit.setText("Login");
		resettingPassword = false;
		forgotPassword.setVisible(true);
		
		center(); 
	}

	public void showError(String errorMessage) {
		error.setText(errorMessage);
		error.setVisible(true);
		emailAddress.setText("");
		password1.setText("");
		//emailAddress.setFocus(true);
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
		presenter.doFacebookLogin();
		
	}

	@Override
	public void doOAuth2Login() {
		presenter.doOAuth2Login();
		
	}
	
}
