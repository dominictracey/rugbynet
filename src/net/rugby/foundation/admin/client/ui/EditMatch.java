/**
 * 
 */
package net.rugby.foundation.admin.client.ui;

import java.util.Date;

import org.gwtbootstrap3.extras.notify.client.constants.NotifyType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.Status;
import net.rugby.foundation.model.shared.IMatchGroup.WorkflowStatus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
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

		void fetchLineups(IMatchGroup matchGroup);
		
		/**
		 * @param matchGroup
		 */
		void fetchScore(IMatchGroup matchGroup);

		/**
		 * @param matchGroup - match we want to have the players stats re-rated WITHOUT going and re-fetching the stats
		 */
		void fetchMatchStats(IMatchGroup matchGroup);

		void showHomeTeamMatchStats(IMatchGroup matchGroup);

		void showVisitingTeamMatchStats(IMatchGroup matchGroup);

		void saveScore(Long matchId, int hS, int vS, Status status);

		void showWorkflowLog(IMatchGroup match);
	} 

	public EditMatch() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Button save;
	@UiField
	Button lock;
	@UiField
	Button fetchLineups;
	@UiField
	Button fetchScore;
	@UiField
	Button fetchMatchStats;
	@UiField
	Button showHomeTeamMatchStats;
	@UiField
	Button showVisitingTeamMatchStats;
	@UiField
	TextBox displayName;
	@UiField
	TextBox scrumId;
	@UiField
	TextBox scheduled;
	@UiField
	ListBox status;
	@UiField
	ListBox workFlowStatus;
	@UiField
	CheckBox locked;
	@UiField
	TextBox homeScore;
	@UiField
	TextBox visitorScore;
	@UiField
	TextBox espnStreamId;
	@UiField
	Button saveScore;
	@UiField
	Button showLog;
	@UiField
	Anchor pipelineLink;
	@UiField
	Anchor scrumLink;

	IMatchGroup matchGroup = null;
	private Presenter listener;

	@UiHandler("save")
	void onClickSave(ClickEvent e) {
		try {
			matchGroup.setDisplayName(displayName.getText());
			matchGroup.setDate(new Date(scheduled.getText()));
			matchGroup.setLocked(locked.getValue());
			if (scrumId.getText() != null && !scrumId.getText().isEmpty() && scrumId.getText().matches("[0-9]*")) {
				matchGroup.setForeignId(Long.parseLong(scrumId.getText()));
			}
			int selected = status.getSelectedIndex();
			matchGroup.setStatus(Status.valueOf(Status.class, status.getItemText(selected)));
			selected = workFlowStatus.getSelectedIndex();
			matchGroup.setWorkflowStatus(WorkflowStatus.valueOf(WorkflowStatus.class,workFlowStatus.getItemText(selected)));
			if (espnStreamId.getText() != null && !espnStreamId.getText().isEmpty() && espnStreamId.getText().matches("[0-9]*")) {
				matchGroup.setESPNStreamId(Long.parseLong(espnStreamId.getText()));
			}
			listener.saveMatchInfo(matchGroup);
		} catch(Exception ex) {
			Notify.notify("Bad data. Not saved. " , NotifyType.DANGER);
		}
	}

	@UiHandler("lock")
	void onClickLock(ClickEvent e) {
		listener.lockMatch(!matchGroup.getLocked(), matchGroup);
	}

	@UiHandler("fetchLineups")
	void onClickFetchLineups(ClickEvent e) {
		listener.fetchLineups(matchGroup);
	}
	
	@UiHandler("fetchScore")
	void onClickFetchScore(ClickEvent e) {
		listener.fetchScore(matchGroup);
	}

	@UiHandler("fetchMatchStats")
	void onClickFetchMatchStats(ClickEvent e) {
		listener.fetchMatchStats(matchGroup);
	}

	@UiHandler("showHomeTeamMatchStats")
	void onClickShowHomeTeamMatchStats(ClickEvent e) {
		listener.showHomeTeamMatchStats(matchGroup);
	}

	@UiHandler("showVisitingTeamMatchStats")
	void onClickShowVisitingTeamMatchStats(ClickEvent e) {
		listener.showVisitingTeamMatchStats(matchGroup);
	}
	
	@UiHandler("showLog")
	void onClickShowLog(ClickEvent e) {
		listener.showWorkflowLog(matchGroup);
	}
	public void ShowMatch(IMatchGroup match) {
		matchGroup = match;
		if (match.getESPNStreamId() != null) {
			espnStreamId.setText(match.getESPNStreamId().toString());
		} else {
			espnStreamId.setText("");
		}
		displayName.setText(match.getDisplayName());
		scheduled.setText(match.getDate().toString());
		int selectedIndex = 0;
		status.clear();
		for (Status s : Status.values()) {
			status.addItem(s.toString());
			if (match.getStatus().equals(s)) {
				selectedIndex = status.getItemCount()-1;
			}
		}

		status.setSelectedIndex(selectedIndex);
		
		workFlowStatus.clear();
		for (WorkflowStatus s : WorkflowStatus.values()) {
			workFlowStatus.addItem(s.toString());
			if (match.getWorkflowStatus().equals(s)) {
				selectedIndex = workFlowStatus.getItemCount()-1;
			}
		}
		
		workFlowStatus.setSelectedIndex(selectedIndex);
		
		if (match.getForeignId() != null || !match.getForeignId().equals(0L)) {
			scrumId.setText(match.getForeignId().toString());
		} else {
			scrumId.setText("");
		}

		
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

		pipelineLink.setHref("/_ah/pipeline/status.html?root=" + match.getFetchMatchStatsPipelineId());
		pipelineLink.setTarget("top");
		pipelineLink.setText("Fetch Match Stats Pipeline Status");
		
		scrumLink.setHref(match.getForeignUrl());
		scrumLink.setTarget("_blank");
		scrumLink.setText(match.getDisplayName() + " on espn.co.uk");
	}

	public void SetPresenter(Presenter p) {
		listener = p;
	}

	@UiHandler("saveScore")
	void onClickSaveScore(ClickEvent e) {
		try {
			int hS = Integer.parseInt(homeScore.getText());
			int vS = Integer.parseInt(visitorScore.getText());
			listener.saveScore(matchGroup.getId(), hS, vS, Status.values()[status.getSelectedIndex()]);
		} catch (Throwable ex) {
			Window.alert("Score must be a valid number");
		}

	}

	public void setPipelineId(String result) {

		pipelineLink.setHref("/_ah/pipeline/status.html?root=" + result);
		pipelineLink.setTarget("top");
		String val = "Fetch Match Stats Pipeline Status";
		if (result != null) {
			val += " (running)";
		}
		pipelineLink.setText(val);
	}


}
