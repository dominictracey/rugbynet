package net.rugby.foundation.client.ui;

import net.rugby.foundation.client.ClientFactory;
import net.rugby.foundation.client.ui.HomeView.Presenter;
import net.rugby.foundation.model.shared.Clubhouse;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.LoginInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class CreateLeague<T> extends DialogBox
{
	private static CreateAccountUiBinder uiBinder = GWT.create(CreateAccountUiBinder.class);
	
	interface CreateAccountUiBinder extends UiBinder<Widget, CreateLeague<?>>
	{
	}

	@UiField TextBox leagueName;
	@UiField Button submit;
	@UiField Button cancel;
	@UiField Label linkStatus;
	private ClientFactory clientFactory;
	private LoginInfo loginInfo;
	private Presenter<?> presenter;
	
	public CreateLeague()
	{
		setWidget(uiBinder.createAndBindUi(this));
		setText("Create League");
		linkStatus.setVisible(false);
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
	  linkStatus.setVisible(false);
	  presenter.onCreateLeagueFinished();
  }

public ClientFactory getClientFactory() {
	return clientFactory;
}

public void setClientFactory(ClientFactory clientFactory) {
	this.clientFactory = clientFactory;
}

public void init(Presenter<?> p) {
	setPresenter(p);
	assert clientFactory!= null;
	
   center();

}

void doCreate() {

		  if (!leagueName.getText().isEmpty())
		  {
			  IClubhouse league = new Clubhouse();
			  league.setName(leagueName.getText());
			  league.setHomeID(clientFactory.getHomeID());
			  
			  clientFactory.getRpcService().createClubhouse(league,
					  new AsyncCallback<IClubhouse>() {
			      		public void onSuccess(IClubhouse result) {
			      			if (result == null) { //name isn't good
			      				linkStatus.setText("That name isn't valid, please try another");
			      				leagueName.setText("");
			      				leagueName.setFocus(true);
			      			} else { 
			      				assert !result.getJoinLink().isEmpty();
			      				//linkStatus.setText("Email this link to your friends to let them into your new league: " + result.getJoinLink());
			      				//linkStatus.setVisible(true);
			      				//submit.setVisible(false);
			      				//cancel.setText("Close");
			      				
			      				hide();
			      				presenter.onCreateLeagueFinished();
			      				
			      				//clientFactory.getEventBus().fireEvent(new AccountActionCompleteEvent(actions.CREATE));
			      				//presenter.onLoginComplete(); 
			      			}
			      			
			      		}
	
						@Override
						public void onFailure(Throwable caught) {
			      			hide();
			      			linkStatus.setText("Problems creating league, try again later.");
			      			linkStatus.setVisible(true);
							
						}});
			  
		  } else {
			  linkStatus.setText("Please enter a different name.");
			  linkStatus.setVisible(true);

			  leagueName.setFocus(true);
		  }
}

public Presenter<?> getPresenter() {
	return presenter;
}

public void setPresenter(Presenter<?> presenter) {
	this.presenter = presenter;
}

}
