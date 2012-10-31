/**
 * 
 */
package net.rugby.foundation.admin.client.ui;

import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.TeamGroup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author home
 *
 */
public class EditTeam extends Composite {

	private static EditTeamUiBinder uiBinder = GWT
			.create(EditTeamUiBinder.class);

	interface EditTeamUiBinder extends UiBinder<Widget, EditTeam> {
	}

	public interface Presenter {
		void saveTeamInfo(ITeamGroup teamGroup);
	} 
	
	public EditTeam() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Button save;
	@UiField
	TextBox displayName;
	@UiField
	TextBox shortName;
	@UiField
	TextBox abbr;
	@UiField
	TextBox color;

	
	ITeamGroup teamGroup = null;
	private Presenter listener;
	
	@UiHandler("save")
	void onClick(ClickEvent e) {
		((TeamGroup)teamGroup).setDisplayName(displayName.getText());
		teamGroup.setShortName(shortName.getText());
		teamGroup.setAbbr(abbr.getText());
		teamGroup.setColor(color.getText());
		listener.saveTeamInfo(teamGroup);
	}

	public void ShowTeam(ITeamGroup result) {
		teamGroup = result;
		displayName.setText(result.getDisplayName());
		shortName.setText(result.getShortName());
		abbr.setText(result.getAbbr());
	}

	public void SetPresenter(Presenter p) {
		listener = p;
	}

}
