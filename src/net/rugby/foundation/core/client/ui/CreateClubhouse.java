package net.rugby.foundation.core.client.ui;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.model.shared.IClubhouse;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class CreateClubhouse extends PopupPanel
{
	private static CreateClubhouseUiBinder uiBinder = GWT.create(CreateClubhouseUiBinder.class);
	
	//@UiTemplate("CreateClubhouse.ui.xml")

	interface CreateClubhouseUiBinder extends UiBinder<Widget, CreateClubhouse>
	{
	}

	public interface Presenter {
		void clubhouseCreated(IClubhouse clubhouse);
		void clubhouseCreationCanceled();
	}
	
	@UiField TextBox clubhouseName;
	@UiField TextBox clubhouseDescription;
	@UiField Button submit;
	@UiField Button cancel;
	@UiField Label error;
	Presenter presenter;
	
	public CreateClubhouse()
	{
		setWidget(uiBinder.createAndBindUi(this));
		//setText("Create Team");
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
	  presenter.clubhouseCreationCanceled();  // cancel listening for IM event for account creation
	  error.setVisible(false);
  }


public void init(Presenter p) {
	presenter = p;

	error.setVisible(false);

	clubhouseName.setFocus(true);

	
   center();

}

void doCreate() {
	Core.getCore().createClubhouse(clubhouseName.getText(), clubhouseDescription.getText(), false,
		  new AsyncCallback<IClubhouse>() {
      		public void onSuccess(IClubhouse result) {
      			hide();
      			
      			presenter.clubhouseCreated(result);
      		}

			@Override
			public void onFailure(Throwable caught) {
   				error.setText("Problems creating clubhouse, try again later.");
  				error.setVisible(true);
				
			}});

}

}
