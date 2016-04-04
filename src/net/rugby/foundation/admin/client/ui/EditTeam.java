/**
 * 
 */
package net.rugby.foundation.admin.client.ui;

import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.TeamGroup;

import org.gwtbootstrap3.extras.bootbox.client.Bootbox;
import org.gwtbootstrap3.extras.bootbox.client.callback.ConfirmCallback;

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
		void saveTeamInfo(ITeamGroup teamGroup, boolean updateMatches);
	} 
	
	public EditTeam() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Button save;
	@UiField
	TextBox displayName;
	@UiField
	TextBox espnName;
	@UiField
	TextBox scrumName;
	@UiField
	TextBox shortName;
	@UiField
	TextBox abbr;
	@UiField
	TextBox twitter;
	@UiField
	TextBox twitterChannel;
	@UiField
	TextBox color;

	private String _displayName = "";
	private boolean updateMatches = false;
	
	ITeamGroup teamGroup = null;
	private Presenter listener;
	
	@UiHandler("save")
	void onClick(ClickEvent e) {
		((TeamGroup)teamGroup).setDisplayName(displayName.getText());
		teamGroup.setShortName(shortName.getText());
		teamGroup.setAbbr(abbr.getText());
		teamGroup.setEspnName(espnName.getText());
		teamGroup.setScrumName(scrumName.getText());
		teamGroup.setTwitter(twitter.getText());
		teamGroup.setTwitterChannel(twitterChannel.getText());
		teamGroup.setColor(color.getText());
		
		setUpdateMatches(false);
		if (!displayName.getText().equals(_displayName)) {
			Bootbox.confirm("You've updated the team's displayName, would you like to update the displayName of all this team's future matches?", new ConfirmCallback() {
			    @Override
			    public void callback(boolean result) {
			      setUpdateMatches(result);
			      listener.saveTeamInfo(teamGroup, updateMatches);
			    }
			  });
		} else {
			listener.saveTeamInfo(teamGroup, updateMatches);
		}
	}

	public void ShowTeam(ITeamGroup result) {
		teamGroup = result;
		if (result != null) {
			_displayName = result.getDisplayName();
			displayName.setText(result.getDisplayName());
			shortName.setText(result.getShortName());
			espnName.setText(result.getEspnName());
			scrumName.setText(result.getScrumName());
			abbr.setText(result.getAbbr());
			twitter.setText(result.getTwitter());
			twitterChannel.setText(result.getTwitterChannel());
			color.setText(result.getColor());
		}
	}

	public void SetPresenter(Presenter p) {
		listener = p;
	}

	public boolean isUpdateMatches() {
		return updateMatches;
	}

	public void setUpdateMatches(boolean updateMatches) {
		this.updateMatches = updateMatches;
	}

}
