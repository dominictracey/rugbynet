package net.rugby.foundation.core.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class FacebookRegistrationPanel extends Composite
{
	private static CreateAccountUiBinder uiBinder = GWT.create(CreateAccountUiBinder.class);

	interface CreateAccountUiBinder extends UiBinder<Widget, FacebookRegistrationPanel>
	{
	}

	public interface Presenter {
		String getDestination();
		String getAppId();
	}
	
	@UiField HTMLPanel iframe;

	Presenter presenter;

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;

	}

	public FacebookRegistrationPanel()
	{
		initWidget(uiBinder.createAndBindUi(this));

		HTML html = new HTML ("<iframe	src='https://www.facebook.com/plugins/registration.php?client_id=341652969207503" + //presenter.getAppId() + 
				"&redirect_uri=http://127.0.0.1:8888/login/facebook?gwt.codesvr=127.0.0.1%3A9997" +
				"&fields=name,birthday,gender,location,email' scrolling='auto' frameborder='no' style='border:none' allowTransparency='true' width='100%' height='330' ></iframe>");
		
		iframe.add(html);
	}
	
}
