package net.rugby.foundation.client.ui;

import net.rugby.foundation.model.shared.Group.GroupType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class GroupSplash extends Composite {

	private static GroupSplashUiBinder uiBinder = GWT
			.create(GroupSplashUiBinder.class);

	interface GroupSplashUiBinder extends UiBinder<Widget, GroupSplash> {
	}

	public GroupSplash() {
		initWidget(uiBinder.createAndBindUi(this));
		teamPanel.setVisible(false);
		positionPanel.setVisible(false);
		matchPanel.setVisible(false);
	}

	@UiField HTMLPanel teamPanel;
	@UiField HTMLPanel positionPanel;
	@UiField HTMLPanel matchPanel;

	public void show(GroupType type) {
		teamPanel.setVisible(false);
		positionPanel.setVisible(false);
		matchPanel.setVisible(false);	
		if (type == GroupType.TEAM)
			teamPanel.setVisible(true);
		else if (type == GroupType.POSITION)
			positionPanel.setVisible(true);
		else if (type == GroupType.MATCH)		
			matchPanel.setVisible(true);	
	}


}
