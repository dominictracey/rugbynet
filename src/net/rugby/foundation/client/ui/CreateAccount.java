package net.rugby.foundation.client.ui;

import net.rugby.foundation.client.ClientFactory;
import net.rugby.foundation.client.ui.HomeView.Presenter;
import net.rugby.foundation.model.shared.CoreConfiguration;
import net.rugby.foundation.model.shared.LoginInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class CreateAccount<T> extends DialogBox
{
	private static CreateAccountUiBinder uiBinder = GWT.create(CreateAccountUiBinder.class);
	
	@UiTemplate("CreateAccount.ui.xml")

	interface CreateAccountUiBinder extends UiBinder<Widget, CreateAccount<?>>
	{
	}

	@UiField TextBox emailAddress;
	@UiField TextBox nickName;
	@UiField PasswordTextBox password1;
	@UiField PasswordTextBox password2;
	@UiField Button submit;
	@UiField Button cancel;
	@UiField Label error;
	private ClientFactory clientFactory;
	private LoginInfo loginInfo;
	Presenter<?> presenter;
	
	public CreateAccount()
	{
		setWidget(uiBinder.createAndBindUi(this));
		setText("Create Team");
		error.setVisible(false);
		  String style = DOM.getElementAttribute(this.getElement(), "style");
		  style += " width:450px;";
		  DOM.setElementAttribute(this.getElement(), "style", style);
	}
	
  @UiHandler("submit")
  void onSubmitButtonClicked(ClickEvent event) {
	  doCreate();
  }
  
  @UiHandler("cancel")
  void onCancelButtonClicked(ClickEvent event) {
	  hide();
	  presenter.onLoginComplete();  // cancel listening for IM event for account creation
	  error.setVisible(false);
  }

  @UiHandler("password2")
  void onKeyPressed(KeyPressEvent event) {
      if (event.getCharCode() == KeyCodes.KEY_ENTER) {
          doCreate();
        }
  }
public ClientFactory getClientFactory() {
	return clientFactory;
}

public void setClientFactory(ClientFactory clientFactory) {
	this.clientFactory = clientFactory;
}

public void init(Presenter<?> p) {
	presenter = p;
	assert clientFactory!= null;

	error.setVisible(false);
	loginInfo = clientFactory.getLoginInfo();
	
	if(loginInfo != null) {
		if (loginInfo.isOpenId())
		{
			emailAddress.setText(loginInfo.getEmailAddress());
			emailAddress.setEnabled(false);
			nickName.setText(loginInfo.getNickname());
			nickName.setSelectionRange(0, loginInfo.getNickname().length());
			password1.setVisible(false);
			password2.setVisible(false);
			nickName.setFocus(true);
			  
		}
		else  {
			emailAddress.setFocus(true);
		}
	}	else {
		emailAddress.setFocus(true);
	}
	
   center();

}

void doCreate() {
	  boolean google = false;
	  error.setVisible(false);
	  if (loginInfo != null) {
		  google = loginInfo.isOpenId();
	  }
	  if (!google) {
		  if (password1.getText().equals(password2.getText()))
		  {
			  clientFactory.getRpcService().createAccount(emailAddress.getText(), nickName.getText(), password1.getText(), false, false,
					  new AsyncCallback<LoginInfo>() {
			      		public void onSuccess(LoginInfo result) {
			      			if (!result.isLoggedIn()) {
			      				error.setText(result.getStatus());
			      				error.setVisible(true);
			      				if (result.getStatus() == CoreConfiguration.getCreateacctErrorExists() ||
			      						result.getStatus() == CoreConfiguration.getCreateacctErrorInvalidEmail()) {
			      					emailAddress.setText("");
			      					emailAddress.setFocus(true);
			      				} else if (result.getStatus() == CoreConfiguration.getCreateacctErrorNicknameExists()) {
			      					nickName.setText("");
			      					nickName.setFocus(true);
			      				} else if (result.getStatus() == CoreConfiguration.getCreateacctErrorPasswordTooShort()) {
				      				password1.setText("");
				      				password2.setText("");
				      				password1.setFocus(true);
			      				}
			      			} else { 
			      				clientFactory.setLoginInfo(result);
			      				clientFactory.getIdentityManager().showLoggedIn();
			      				hide();
				      			presenter.onLoginComplete();

			      			}
			      			
			      		}
	
						@Override
						public void onFailure(Throwable caught) {
			      			hide();
			      			presenter.onLoginComplete();
		      				error.setText("Problems creating account, try again later.");
		      				error.setVisible(true);
							
						}});
			  
		  } else {
			  error.setText("Passwords don't match, try again");
			  error.setVisible(true);
			  password1.setText("");
			  password2.setText("");
			  password1.setFocus(true);
		  }
		  //NO GOOGLE RIGHT NOW
//	  } else {
//		  if (nickName.getText().length() > 0) {
//			  error.setText("Screen name required");
//			  error.setVisible(true);
//			  nickName.setFocus(true);			  
//		  } else {
//			  clientFactory.getRpcService().createAccount(emailAddress.getText(), nickName.getText(), "", true, false, 
//					  new AsyncCallback<String>() {
//			      		public void onSuccess(String result) {
//			      			if (!result.equalsIgnoreCase("Congratulations - account created!")) {
//			      			  Window.alert(result);
//			      			  emailAddress.setText("");
//			      			  password1.setText("");
//			      			  password2.setText("");
//			      			  emailAddress.setFocus(true);
//			      			} else { 
//			      				assert result.equals("Congratulations - account created!");
//			      				clientFactory.getIdentityManager().checkLoginStatus();
//			      			 	hide();
//			      				//presenter.onLoginComplete(); 
//			      			}
//			      			
//			      		}
//	
//						@Override
//						public void onFailure(Throwable caught) {
//			      			Window.alert("Problems creating account, try again later.");
//			      			hide();
//			      			presenter.onLoginComplete(); // cancel listening for IM event for account creation
//
//							
//						}});			  
//		  }
	  }
}

}
