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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ChangePasswordPanel extends Composite
{
	private static CreateAccountUiBinder uiBinder = GWT.create(CreateAccountUiBinder.class);

	interface CreateAccountUiBinder extends UiBinder<Widget, ChangePasswordPanel>
	{
	}

	public interface Presenter {
		void doChangePassword(String oldPassword, String newPassword);
		void onCancelChangePassword();
	}
	
	@UiField PasswordTextBox oldPassword;
	@UiField PasswordTextBox password1;
	@UiField PasswordTextBox password2;
	@UiField Button submit;
	@UiField Button cancel;
	@UiField Label error;
	
	Presenter presenter;

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public ChangePasswordPanel()
	{
		initWidget(uiBinder.createAndBindUi(this));
		error.setVisible(false);
	}
	
	@UiHandler("password2")
	void onKeyUp(KeyUpEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			doChangePassword();
		}
	}

	@UiHandler("submit")
	void onSubmitButtonClicked(ClickEvent event) {
		if (password1.getText() != null && password1.getText().equals(password2.getText())) {
			doChangePassword();
		} else {
			error.setText("New passwords don't match!");
			error.setVisible(true);
			password1.setText("");
			password2.setText("");
			password1.setFocus(true);
		}
	}	  	  

	private void doChangePassword() {
		error.setVisible(false);
		presenter.doChangePassword(oldPassword.getText(), password1.getText());
	}



	@UiHandler("cancel")
	void onCancelButtonClicked(ClickEvent event) {
		presenter.onCancelChangePassword();
		error.setVisible(false);
	}

	public void init(){

	}

	public void showError(String errorMessage) {
		error.setText(errorMessage);
		error.setVisible(true);

		oldPassword.setText("");

		password1.setText("");
		password2.setText("");

	}
	
}
