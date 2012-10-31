package net.rugby.foundation.client.ui;

import net.rugby.foundation.client.ClientFactory;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ShowContent extends DialogBox
{
	private static CreateAccountUiBinder uiBinder = GWT.create(CreateAccountUiBinder.class);
	
	//@UiTemplate("CreateAccount.ui.xml")

	interface CreateAccountUiBinder extends UiBinder<Widget, ShowContent>
	{
	}


	@UiField HTML content;
	@UiField Button close;

	
	private ClientFactory clientFactory = null;	

	public ShowContent(String str )
	{

		setWidget(uiBinder.createAndBindUi(this));
		String style = DOM.getElementAttribute(this.getElement(), "style");
		style += " width:500px; font-variant: normal; text-transform: none;";
		DOM.setElementAttribute(this.getElement(), "style", style);
		content.setHTML(str);
	}


	public ShowContent(Long contentID )
	{
		assert clientFactory != null;
		setWidget(uiBinder.createAndBindUi(this));
		String style = DOM.getElementAttribute(this.getElement(), "style");
		style += " width:500px; font-variant: normal; text-transform: none;";
		DOM.setElementAttribute(this.getElement(), "style", style);
		
	    clientFactory.getRpcService().getContent(contentID, new AsyncCallback<String>() {
		      public void onSuccess(String result) {
		    	  content.setHTML(result);
		      }		      
		      public void onFailure(Throwable caught) {
		    	  content.setHTML("System down for maintenance, check back soon! (002)");
		      }
		    });	

	}	
	  
	  @UiHandler("close")
	  void onCloseButtonClicked(ClickEvent event) {
		hide();
	}


	public ClientFactory getClientFactory() {
		return clientFactory;
	}


	public void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}	  
	  
  
}
