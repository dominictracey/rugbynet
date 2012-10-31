package net.rugby.foundation.client.ui;

import net.rugby.foundation.client.ClientFactory;
import net.rugby.foundation.client.ui.HomeView.Presenter;
import net.rugby.foundation.model.shared.LoginInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class NativeLogin<T> extends DialogBox
{
	private static CreateAccountUiBinder uiBinder = GWT.create(CreateAccountUiBinder.class);
	
	//@UiTemplate("CreateAccount.ui.xml")

	interface CreateAccountUiBinder extends UiBinder<Widget, NativeLogin>
	{
	}


	@UiField TextBox emailAddress;
	@UiField PasswordTextBox password1;
	@UiField Button submit;
	@UiField Button cancel;
	@UiField Label error;
	
	private ClientFactory clientFactory;
	private LoginInfo loginInfo;
	
	Presenter<T> presenter;
	
	public Presenter<T> getPresenter() {
		return presenter;
	}

	public void setPresenter(Presenter<T> presenter) {
		this.presenter = presenter;
	}

	public NativeLogin()
	{
		setWidget(uiBinder.createAndBindUi(this));
		setText("Login");
		error.setVisible(false);

	}
	  @UiHandler("password1")
	  void onKeyPress(KeyPressEvent event) {
	      if (event.getCharCode() == KeyCodes.KEY_ENTER) {
	          doLogin();
	        }
	  }
	  
	  @UiHandler("submit")
	  void onSubmitButtonClicked(ClickEvent event) {
		doLogin();
	}	  
	  
  private void doLogin() {
	  error.setVisible(false);

	  clientFactory.getRpcService().nativeLogin(emailAddress.getText(), password1.getText(),
			  new AsyncCallback<LoginInfo>() {
	      		public void onSuccess(LoginInfo result) {
	      			if (!result.isLoggedIn()) {
		      			  error.setText("Incorrect email or password");
		      			  error.setVisible(true);
		      			  emailAddress.setText("");
		      			  password1.setText("");
		      			  emailAddress.setFocus(true);
	      			} else { 
	      				assert result.isLoggedIn();
	      				clientFactory.setLoginInfo(result);
	      				clientFactory.getIdentityManager().showLoggedIn();
	      				hide();
	      				presenter.onLoginComplete();
	      			}
	      			
	      		}
	
				@Override
				public void onFailure(Throwable caught) {
	      			  error.setText("Problems logging in, please try again later.");
	      			  error.setVisible(true);

	      			  hide();
	      			  presenter.onLoginComplete();

							
		}});		
	}


  
  @UiHandler("cancel")
  void onCancelButtonClicked(ClickEvent event) {
	  hide();
	  presenter.onLoginComplete();
	  error.setVisible(false);

  }

public void init(){
	assert clientFactory!=null;

	center(); //@TODO PREZI NO LIKE
}
public ClientFactory getClientFactory() {
	return clientFactory;
}

public void setClientFactory(ClientFactory clientFactory) {
	this.clientFactory = clientFactory;
}

}
