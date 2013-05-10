/**
 * 
 */
package net.rugby.foundation.admin.client.ui;

import java.util.Date;

import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.Status;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author home
 *
 */
public class EditMatch extends Composite {

	private static EditTeamUiBinder uiBinder = GWT
			.create(EditTeamUiBinder.class);

	interface EditTeamUiBinder extends UiBinder<Widget, EditMatch> {
	}

	public interface Presenter {
		void saveMatchInfo(IMatchGroup matchGroup);

		/**
		 * @param matchGroup
		 */
		void lockMatch(boolean lock, IMatchGroup matchGroup);

		/**
		 * @param matchGroup
		 */
		void fetchScore(IMatchGroup matchGroup);

		/**
		 * @param matchGroup
		 */
		void fetchPlayers(IMatchGroup matchGroup);

		/**
		 * @param matchGroup
		 */
		void fetchMatchStats(IMatchGroup matchGroup);

		void fetchPlayerStats(IMatchGroup matchGroup);

	} 
	
	public EditMatch() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Button save;
	@UiField
	Button lock;
	@UiField
	Button fetchScore;
	@UiField
	Button fetchPlayers;
	@UiField
	Button fetchMatchStats;
	@UiField
	Button fetchPlayerStats;
	@UiField
	TextBox displayName;
	@UiField
	TextBox scheduled;
	@UiField
	ListBox status;
	@UiField
	CheckBox locked;
	@UiField
	TextBox homeScore;
	@UiField
	TextBox visitorScore;
	
	IMatchGroup matchGroup = null;
	private Presenter listener;
	
	@UiHandler("save")
	void onClickSave(ClickEvent e) {
		matchGroup.setDisplayName(displayName.getText());
		matchGroup.setDate(new Date(scheduled.getText()));
		matchGroup.setLocked(locked.getValue());
		int selected = status.getSelectedIndex();
		matchGroup.setStatus(Status.valueOf(Status.class, status.getItemText(selected)));
		listener.saveMatchInfo(matchGroup);
	}
	
	@UiHandler("lock")
	void onClickLock(ClickEvent e) {
		listener.lockMatch(!matchGroup.getLocked(), matchGroup);
	}
	
	@UiHandler("fetchScore")
	void onClickFetchScore(ClickEvent e) {
		listener.fetchScore(matchGroup);
	}

	@UiHandler("fetchPlayers")
	void onClickFetchPlayers(ClickEvent e) {
		listener.fetchPlayers(matchGroup);
	}
	
	@UiHandler("fetchMatchStats")
	void onClickFetchMatchStats(ClickEvent e) {
		listener.fetchMatchStats(matchGroup);
	}
	
	@UiHandler("fetchPlayerStats")
	void onClickFetchPlayerStats(ClickEvent e) {
		listener.fetchPlayerStats(matchGroup);
	}
	public void ShowMatch(IMatchGroup match) {
		matchGroup = match;
		
		displayName.setText(match.getDisplayName());
		scheduled.setText(match.getDate().toString());
		int selectedIndex = 0;
		for (Status s : Status.values()) {
			status.addItem(s.toString());
			if (match.getStatus().equals(s)) {
				selectedIndex = status.getItemCount()-1;
			}
		}
		status.setSelectedIndex(selectedIndex);
		locked.setValue(match.getLocked());
		if (match.getLocked()) {
			lock.setText("Unlock");
		} else {
			lock.setText("Lock");
		}
		
		if (match.getSimpleScoreMatchResult() != null) {
			homeScore.setText(Integer.toString(match.getSimpleScoreMatchResult().getHomeScore()));
			visitorScore.setText(Integer.toString(match.getSimpleScoreMatchResult().getVisitScore()));
		} else {
			homeScore.setText("");
			visitorScore.setText("");
		}
	}

	public void SetPresenter(Presenter p) {
		listener = p;
	}

}
