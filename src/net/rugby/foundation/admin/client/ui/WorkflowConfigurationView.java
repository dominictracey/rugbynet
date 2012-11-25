package net.rugby.foundation.admin.client.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rugby.foundation.admin.server.workflow.IWorkflow;
import net.rugby.foundation.admin.shared.CompetitionWorkflow;
import net.rugby.foundation.admin.shared.IWorkflowConfiguration;
import net.rugby.foundation.model.shared.ICompetition;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class WorkflowConfigurationView extends Composite {
	
	public interface Presenter {
		void saveWorkflowConfiguration(List<IWorkflow> workflows);
	}

	private static WorkflowConfigurationViewUiBinder uiBinder = GWT
			.create(WorkflowConfigurationViewUiBinder.class);

	interface WorkflowConfigurationViewUiBinder extends
			UiBinder<Widget, WorkflowConfigurationView> {
	}

	Map<Long,ICompetition> competitions = new HashMap<Long,ICompetition>();
	
	public WorkflowConfigurationView() {
		initWidget(uiBinder.createAndBindUi(this));

	}

	@UiField
	Button Save;
	@UiField
	FlexTable compTable;
	@UiField
	FlexTable game1Table;
	@UiField
	ListBox compsComplete;
	@UiField 
	Label status;
	
	private Presenter wfclistener;
	private CompetitionView.Presenter cvlistener;
	
	private List<IWorkflow> workflows;

	
	@UiHandler("Save")
	void onSaveClick(ClickEvent e) {
		ArrayList<Long> newUnderwayList = new ArrayList<Long>();
		
		for (int i=1; i<compTable.getRowCount(); ++i) {
			// has the checkbox changed?
			if (((CheckBox)(compTable.getWidget(i, 0))).getValue()) {// != competitions.get(Long.parseLong(((CheckBox)compTable.getWidget(i, 0)).getFormValue())).getUnderway()) {
				ICompetition c = competitions.get(Long.parseLong(((CheckBox)compTable.getWidget(i, 0)).getFormValue()));
				//c.setUnderway(((CheckBox)compTable.getWidget(i, 0)).getValue());
				//cvlistener.saveCompetitionClicked(c, null);
				//if (c.getUnderway()) 
						newUnderwayList.add(c.getId());
			}
		}
		
		//wfConfig.setUnderwayCompetitions(newUnderwayList);
		//wfclistener.saveWorkflowConfiguration(wfConfig);
		
		Window.alert("Save successful");
	}
	
	public void setPresenter(final CompetitionView.Presenter cvlistener, final WorkflowConfigurationView.Presenter wfclistener) {
		this.cvlistener = cvlistener;
		this.wfclistener = wfclistener;
	}
	
	public void setCompetitions(List<ICompetition> result, List<IWorkflow> workflows) {
		//wfConfig = wfc;
		AddHeaders();
		if(!result.isEmpty()) {
			Date today = new Date();
			compsComplete.clear();
			for (ICompetition c : result) {
				compsComplete.addItem(c.getLongName(), c.getId().toString());
				compsComplete.setItemSelected(compsComplete.getItemCount()-1, today.after(c.getEnd()));
				//addComp(c,wfc.getUnderwayCompetitions().contains(c.getId()));
				competitions.put(c.getId(), c);
			}
		}
	}
	
	
	private void addComp(IWorkflow wf, Boolean underwayB) {
		int row = compTable.getRowCount();
//		CheckBox underway = new CheckBox();
//		underway.setFormValue(c.getId().toString());
//
//		if (underwayB == null)
//			underway.setValue(false);
//		else
//			underway.setValue(underwayB);
//			
//		underway.addClickHandler(underwayChanged);
//		compTable.setWidget(row, 0, underway);

		
//		compTable.setHTML(row, 1, c.getLongName());
//		compTable.setHTML(row, 2, c.getBegin().toString());
//		compTable.setHTML(row, 3, c.getEnd().toString());
//		compTable.setHTML(row, 4, Integer.toString(c.getRoundIds().size()));
		
		if (wf instanceof CompetitionWorkflow) {
			CompetitionWorkflow cwf = (CompetitionWorkflow) wf;

//			CheckBox initiated = new CheckBox();
//			compTable.setWidget(row,0,initiated);
			compTable.setHTML(row,1,"CompName");
			compTable.setHTML(row,2,"Status");
			//Initiated, CronCheck, ResultFound, ResultNotFound, MatchLocked, RoundAdvanced, Archived, Terminated
			compTable.setHTML(row,3,"CronCheck");
			compTable.setHTML(row,4,"ResultFound");
			compTable.setHTML(row,5,"ResultNotFound");
			compTable.setHTML(row,6,"RoundAdvanced");
			compTable.setHTML(row,7,"Archived");
			compTable.setHTML(row,8,"Terminated");
		}

	}
	
	ClickHandler underwayChanged = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			
			
		}
	};

	public void showStatus(String message) {
		status.setText(message);
		
	}
	
	private void AddHeaders() {
		compTable.removeAllRows();
		compTable.setHTML(0,0,"Active");
		compTable.setHTML(0,1,"CompName");
		compTable.setHTML(0,2,"Status");
		//Initiated, CronCheck, ResultFound, ResultNotFound, MatchLocked, RoundAdvanced, Archived, Terminated
		compTable.setHTML(0,3,"CronCheck");
		compTable.setHTML(0,4,"ResultFound");
		compTable.setHTML(0,5,"ResultNotFound");
		compTable.setHTML(0,6,"RoundAdvanced");
		compTable.setHTML(0,7,"Archived");
		compTable.setHTML(0,8,"Terminated");
	
		compTable.getRowFormatter().addStyleName(0, "compListHeader");
		compTable.setStylePrimaryName("compTable");

	}
	

}
