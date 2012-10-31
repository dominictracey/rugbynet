package net.rugby.foundation.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class Sponsors extends Composite {

	@UiField HTMLPanel sponsor;
	
	private static SponsorsUiBinder uiBinder = GWT
			.create(SponsorsUiBinder.class);

	interface SponsorsUiBinder extends UiBinder<Widget, Sponsors> {
	}
	

	
	public Sponsors() {
		initWidget(uiBinder.createAndBindUi(this));
	}




	public Sponsors(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));

	}


}
