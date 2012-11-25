/**
 * 
 */
package net.rugby.foundation.admin.client.ui;

import com.google.appengine.tools.pipeline.JobInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author home
 *
 */
public class ShowJob extends Composite {

	private static EditTeamUiBinder uiBinder = GWT
			.create(EditTeamUiBinder.class);

	interface EditTeamUiBinder extends UiBinder<Widget, ShowJob> {
	}

	public interface Presenter {
		void runJob(JobInfo job);
	} 
	
	public ShowJob() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Button run;
	@UiField
	Label jobId;
	@UiField
	Label jobStatus;
	@UiField
	Label jobTiming;
	@UiField
	Label jobOutputs;

	
	JobInfo info = null;
	private Presenter listener;
	
	@UiHandler("run")
	void onClick(ClickEvent e) {
//		((TeamGroup)teamGroup).setDisplayName(displayName.getText());
//		teamGroup.setShortName(shortName.getText());
//		teamGroup.setAbbr(abbr.getText());
//		teamGroup.setColor(color.getText());
//		listener.saveTeamInfo(teamGroup);
	}

	public void ShowJobInfo(String pipelineId, JobInfo jobInfo) {
		info = jobInfo;
		jobId.setText(pipelineId);
		jobStatus.setText(info.getJobState().toString());
		jobOutputs.setText(info.getOutput().toString());
	}

	public void SetPresenter(Presenter p) {
		listener = p;
	}

}
