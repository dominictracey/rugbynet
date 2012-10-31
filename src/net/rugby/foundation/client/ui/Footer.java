package net.rugby.foundation.client.ui;

import net.rugby.foundation.client.ClientFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class Footer extends Composite  {

	private static footerUiBinder uiBinder = GWT.create(footerUiBinder.class);

	interface footerUiBinder extends UiBinder<Widget, Footer> {
	}

	@UiField Anchor contact;
	@UiField Anchor admin;
	
	private ClientFactory clientFactory;
	
	public Footer() {
		initWidget(uiBinder.createAndBindUi(this));
		contact.setHref("mailto:contact@rugby.net"); //("<a href='mailto:contact@rugby.net'>Contact Us</a><br/>");
	}
	
	@UiHandler("admin")
	public void onClickEvent(ClickEvent event) {
		Admin adminView = new Admin();
		assert clientFactory != null;
		adminView.setClientFactory(clientFactory);
				
		adminView.setSize("500", "400");
		adminView.center();
		
	}

	public ClientFactory getClientFactory() {
		return clientFactory;
	}

	public void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}
	
	public void showAdminLink(Boolean show) {
		admin.setVisible(show);
	}

}
