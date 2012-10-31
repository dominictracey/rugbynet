package net.rugby.foundation.core.client.ui;

import net.rugby.foundation.model.shared.LoginInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Login extends PopupPanel implements net.rugby.foundation.core.client.ui.ExternalAuthenticatorPanel.Presenter
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
	}
	
	@UiField TextBox emailAddress;
	@UiField PasswordTextBox password1;
	@UiField Button submit;
	@UiField Button cancel;
	@UiField Label error;
	@UiField ExternalAuthenticatorPanel nonNativeLogins;
	@UiField Anchor forgotPassword;
	
	Presenter presenter;
	boolean resettingPassword = false;

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public Login()
	{

		setAutoHideEnabled(true);
		setModal(true);

		setWidget(uiBinder.createAndBindUi(this));
		error.setVisible(false);
		nonNativeLogins.setPresenter(this);
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
		hide();
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
		presenter.doFacebookLogin();
		
	}
	
}
