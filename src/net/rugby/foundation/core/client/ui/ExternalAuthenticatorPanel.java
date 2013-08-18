package net.rugby.foundation.core.client.ui;

import net.rugby.foundation.model.shared.LoginInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ExternalAuthenticatorPanel extends Composite
{
	private static CreateAccountUiBinder uiBinder = GWT.create(CreateAccountUiBinder.class);

	interface CreateAccountUiBinder extends UiBinder<Widget, ExternalAuthenticatorPanel>
	{
	}

	public interface Presenter {
		void doOpenIdLogin(LoginInfo.Selector selector);
		void doFacebookLogin();
	}
	

	//@UiField Anchor facebook;
	@UiField Anchor google;
	@UiField Anchor aol;
	@UiField Anchor openid_com;
	@UiField Anchor yahoo;

	Presenter presenter;

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public ExternalAuthenticatorPanel()
	{
		initWidget(uiBinder.createAndBindUi(this));

	}
	

	@UiHandler("google")
	void onGoogleButtonClicked(ClickEvent event) {
		presenter.doOpenIdLogin(LoginInfo.Selector.google);
	}	 
	
	@UiHandler("aol")
	void onAOLButtonClicked(ClickEvent event) {
		presenter.doOpenIdLogin(LoginInfo.Selector.aol);
	}	
	
	@UiHandler("openid_com")
	void onOpenIdComButtonClicked(ClickEvent event) {
		presenter.doOpenIdLogin(LoginInfo.Selector.myopenid_com);
	}	
	
	@UiHandler("yahoo")
	void onYahooButtonClicked(ClickEvent event) {
		presenter.doOpenIdLogin(LoginInfo.Selector.yahoo);
	}	
	
//	@UiHandler("facebook")
//	void onFacebookButtonClicked(ClickEvent event) {
//		presenter.doFacebookLogin();
//	}	
	
}
