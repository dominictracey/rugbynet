package net.rugby.foundation.core.client.ui;

import net.rugby.foundation.model.shared.LoginInfo;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.*;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
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
	
	@UiField Panel topLevel;
	@UiField Form form;
	@UiField TextBox emailAddress;
	@UiField Input oldPassword;
	@UiField Input password1;
	@UiField Input password2;
	@UiField Button submit;
	@UiField Button cancel;
	@UiField Label error;
	@UiField Strong alertStrong;
	@UiField Text alertText;
	
	Presenter presenter;

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public ChangePasswordPanel()
	{
		initWidget(uiBinder.createAndBindUi(this));
		error.addStyleName("hidden");
		emailAddress.setEnabled(false);
		form.addStyleName("col-md-12");
		topLevel.removeStyleName("panel-default");
	}

	@UiHandler("password2")
	void onKeyUp(KeyUpEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			doChangePassword();
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

	public void init(LoginInfo info){
		emailAddress.setText(info.getEmailAddress());
	}

	public void showError(String errorMessage) {
		error.setText(errorMessage);
		error.setVisible(true);

		oldPassword.setText("");

		password1.setText("");
		password2.setText("");

	}
	
	public void setAlert(String strong, String text) {
		alertStrong.setText(strong+" ");
		alertText.setText(text);
	}
	
}
