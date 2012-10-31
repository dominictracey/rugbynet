package net.rugby.foundation.admin.client.ui;

import java.util.Map;

import net.rugby.foundation.admin.shared.IOrchestrationConfiguration;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions.CompActions;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions.MatchActions;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Sample implementation of {@link OrchestrationConfigurationView}.
 */
public class OrchestrationConfigurationViewImpl extends Composite implements OrchestrationConfigurationView {

	interface Binder extends UiBinder<Widget, OrchestrationConfigurationViewImpl> {
	}
	
	private static final Binder binder = GWT.create(Binder.class);

	private Presenter listener;
	@UiField
	FlexTable compTable;
	@UiField 
	Button Save;
	@UiField 
	Label status;
	
	int numCompActions;
	int numMatchActions;
	
	Map<String,IOrchestrationConfiguration> oc;
	int row = 1;
	
	public OrchestrationConfigurationViewImpl() {
		initWidget(binder.createAndBindUi(this));
		addHeaderRow();

	}

	/**
	 * 
	 */
	private void addHeaderRow() {
		compTable.setHTML(0,0,"Competition");
		
		int count = 1;

		for (CompActions action : CompActions.values()) {			
			compTable.setHTML(0,count,"COMP:"+action.name());
			++count;
		}
		
		numMatchActions = 1;
		for (MatchActions action : MatchActions.values()) {			
			compTable.setHTML(0,count,"MATCH:"+action.name());
			++count;
		}
		
		compTable.setHTML(0,count,"Admin");
		compTable.setHTML(0,count+1,"CompId");
		compTable.setHTML(0,count+2,"Scrum SS");
		
		compTable.getRowFormatter().addStyleName(0, "compListHeader");
		compTable.setStylePrimaryName("compTable");		
	}

	@Override
	public void setOrchConfigs(Map<String,IOrchestrationConfiguration> configs) {
		oc = configs;
		compTable.removeAllRows();
		addHeaderRow();
		row = 1;
		if(!oc.isEmpty()) {
			for (String c : oc.keySet()) {
				addComp(c);
			}
		}
	}

	/**
	 * @param c is the competition name - which is the key to the hashmap.
	 */
	private void addComp(String c) {
		compTable.setHTML(row, 0, c);		
		
		int count = 1;
		for (CompActions action : CompActions.values()) {	
			CheckBox cb = new CheckBox();
			cb.setValue(oc.get(c).getCompActions().get(action.getValue()));
			compTable.setWidget(row,count,cb);
			++count;
		}
		
		numMatchActions = 1;
		for (MatchActions action : MatchActions.values()) {		
			CheckBox cb = new CheckBox();
			cb.setValue(oc.get(c).getMatchActions().get(action.getValue()));
			compTable.setWidget(row,count,cb);
			++count;
		}
		

		TextBox adminEmail = new TextBox();
		if (oc.get(c) != null)
			adminEmail.setText(oc.get(c).getAdminEmail());
		compTable.setWidget(row,count, adminEmail);
		if (oc.get(c) != null)
			compTable.setHTML(row,count+1, oc.get(c).getCompID().toString());
		else
			compTable.setHTML(row, count+1, "0");
		
		CheckBox ssmr = new CheckBox();
		if (oc.get(c) != null)
			ssmr.setValue(oc.get(c).isSimpleMatchResultScrum());
		compTable.setWidget(row, count+2, ssmr);
		row++;
		
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}

	@UiHandler("Save")
	void onButtonClick(ClickEvent event) {
		//Map<String, IOrchestrationConfiguration> newMap = new HashMap<String, IOrchestrationConfiguration>();
		
		for (int i = 1; i < row; ++i) {
			IOrchestrationConfiguration oConf = oc.get(compTable.getHTML(i, 0));
			
			int count = 1;
			for (CompActions action : CompActions.values()) {	
				oConf.getCompActions().put(action.getValue(), ((CheckBox)compTable.getWidget(i,count)).getValue());
				count++;
			}
			
			numMatchActions = 1;
			for (MatchActions action : MatchActions.values()) {	
				oConf.getMatchActions().put(action.getValue(), ((CheckBox)compTable.getWidget(i,count)).getValue());
				count++;
			}
			
			
			oConf.setAdminEmail(((TextBox)compTable.getWidget(i, count)).getText());
			oConf.setCompID(Long.parseLong(compTable.getHTML(i, count+1)));
			oConf.setSimpleMatchResultScrum(((CheckBox)compTable.getWidget(i, count+2)).getValue());
			
			//newMap.put(compTable.getHTML(i,0), oConf);
		}
		
		listener.saveClicked(oc);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.OrchestrationConfigurationView#showStatus(java.lang.String)
	 */
	@Override
	public void showStatus(String msg) {
		status.setText(msg);
		
	}
}
