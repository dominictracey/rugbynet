package net.rugby.foundation.game1.client.ui;

import java.util.ArrayList;
import java.util.List;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.game1.shared.Configuration;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.IConfiguration;
import net.rugby.foundation.model.shared.ICompetition;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Sample implementation of {@link OrchestrationConfigurationView}.
 */
public class Game1ConfigurationViewImpl extends Composite implements Game1ConfigurationView {

	interface Binder extends UiBinder<Widget, Game1ConfigurationViewImpl> {
	}
	
	private static final Binder binder = GWT.create(Binder.class);

	private Presenter listener;
	@UiField
	FlexTable compTable;
	@UiField 
	Button Save;
	@UiField
	Button redoStats;
	@UiField 
	Label status;
	

	IConfiguration config = new Configuration();
	int row = 1;
	
	public Game1ConfigurationViewImpl() {
		initWidget(binder.createAndBindUi(this));
		addHeaderRow();

	}
	
	/**
	 * 
	 */
	private void addHeaderRow() {
		compTable.setHTML(0,0,"Competition");
		compTable.setHTML(0,1,"CompetitionId");
		compTable.setHTML(0,2,"Activate");
		compTable.setHTML(0,3,"ClubhouseId");
		compTable.setHTML(0,4,"LeagueId");
		compTable.setHTML(0, 5, "Build Stats");
	
		compTable.getRowFormatter().addStyleName(0, "compListHeader");
		compTable.setStylePrimaryName("compTable");		
	}

	@Override
	public void setConfig(IConfiguration config, net.rugby.foundation.model.shared.ICoreConfiguration coreConf) {

		this.config = config;
		compTable.removeAllRows();
		addHeaderRow();
		row = 1;
		
		if(!(config == null)) {
			// first get a list of all the current comps
			//net.rugby.foundation.model.shared.IConfiguration coreConf = confF.get();

			for (Long compId : coreConf.getCompetitionMap().keySet()) {
				if (config.getLeagueIdMap().containsKey(compId)) {
					for (IClubhouseLeagueMap clm : config.getCompetitionClubhouseLeageMapList()) {
						if (clm.getCompId().equals(compId)) {
							addComp(clm);
						}
					}	
				} else { // hasn't been activated yet
					addInactiveComp(compId);
				}
			}
		}
	}

	/**
	 * @param compId
	 */
	private void addInactiveComp(Long compId) {

		Core.getCore().getComp(compId, new AsyncCallback<ICompetition>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(ICompetition comp) {
				
				compTable.setHTML(row, 0, comp.getShortName());	
				compTable.setHTML(row, 1, comp.getId().toString());	
				
				CheckBox ssmr = new CheckBox();
				ssmr.setValue(false);
				compTable.setWidget(row, 2, ssmr);
				compTable.setHTML(row, 3, "--");
				compTable.setHTML(row, 4, "--");
				CheckBox redoStats = new CheckBox();
				redoStats.setValue(false);
				compTable.setWidget(row, 5, redoStats);
				row++;
			}
			
		});
		
	}
	

	/**
	 * @param c is the competition name - which is the key to the hashmap.
	 */
	private void addComp(final IClubhouseLeagueMap clm) {
		Core.getCore().getComp(clm.getCompId(), new AsyncCallback<ICompetition>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(ICompetition comp) {

				compTable.setHTML(row, 0, comp.getShortName());	
				compTable.setHTML(row, 1, comp.getId().toString());	
				
				CheckBox ssmr = new CheckBox();
				ssmr.setValue(true);
				compTable.setWidget(row, 2, ssmr);
				compTable.setHTML(row, 3, clm.getClubhouseId().toString());
				compTable.setHTML(row, 4, clm.getLeagueId().toString());
				CheckBox redoStats = new CheckBox();
				redoStats.setValue(false);
				compTable.setWidget(row, 5, redoStats);
				row++;
			}
		
		});

	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}

	@UiHandler("Save")
	void onButtonClick(ClickEvent event) {
		List<Long> compsToAdd = new ArrayList<Long>();
		List<Long> compsToDrop = new ArrayList<Long>();
				
		for (int i = 1; i < row; ++i) {
			if (((CheckBox)compTable.getWidget(i, 2)).getValue()) {
				if ((compTable.getHTML(i, 4)).equals("--")) {
					compsToAdd.add(Long.parseLong(compTable.getHTML(i, 1)));
				}
			} else { // did they de-select?
				if (!(compTable.getHTML(i, 4)).equals("--")) {
					compsToDrop.add(Long.parseLong(compTable.getHTML(i, 1)));
				}
			}
		}
		
		listener.saveClicked(config, compsToAdd, compsToDrop);
	}
	
	@UiHandler("redoStats")
	void onButtonRedoStatsClick(ClickEvent event) {
		List<Long> compsToRedo = new ArrayList<Long>();
		for (int i = 1; i < row; ++i) {
			if (((CheckBox)compTable.getWidget(i, 5)).getValue()) {
				compsToRedo.add(Long.parseLong(compTable.getHTML(i, 1)));
			}
		}
		
		listener.redoMatchStatsClicked(config, compsToRedo);

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.ui.OrchestrationConfigurationView#showStatus(java.lang.String)
	 */
	@Override
	public void showStatus(String msg) {
		status.setText(msg);
		
	}
}
