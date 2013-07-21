package net.rugby.foundation.admin.client.ui.teammatchstatspopup;

import java.util.List;

import net.rugby.foundation.admin.client.ui.FieldDefinition;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TeamMatchStatsPopupViewImpl<T> extends DialogBox implements TeamMatchStatsPopupView<T>
{
	private static TeamPopupViewImplUiBinder uiBinder = GWT.create(TeamPopupViewImplUiBinder.class);

	@UiTemplate("TeamMatchStatsPopupViewImpl.ui.xml")

	interface TeamPopupViewImplUiBinder extends UiBinder<Widget, TeamMatchStatsPopupViewImpl<?>>
	{
	}

	@UiField Label   nameAndId;
	@UiField Anchor  teamAndId;
	@UiField Label   matchAndId;
	@UiField TextBox tries;
	@UiField TextBox conversionsAttempted;  
	@UiField TextBox conversionsMade;           
	@UiField TextBox penaltiesAttempted;    
	@UiField TextBox penaltiesMade;                               
	@UiField TextBox dropGoalsAttempted;    
	@UiField TextBox dropGoalsMade;         

	@UiField TextBox runs;
	@UiField TextBox passes;
	@UiField TextBox kicks;
	@UiField TextBox possesion;
	@UiField TextBox territory;
	@UiField TextBox metersRun;
	@UiField TextBox cleanBreaks;
	@UiField TextBox defendersBeaten;
	@UiField TextBox offloads;
	@UiField TextBox turnovers;
	@UiField TextBox rucks;                
	@UiField TextBox rucksWon;             	                      
	@UiField TextBox mauls;                
	@UiField TextBox maulsWon;                        	                      
	@UiField TextBox scrumsPutIn;          
	@UiField TextBox scrumsWonOnOwnPut;                          
	@UiField TextBox lineoutsThrownIn;     
	@UiField TextBox lineoutsWonOnOwnThrow;
	@UiField TextBox tacklesMade;
	@UiField TextBox tacklesMissed;
	@UiField TextBox penaltiesConceded;
	@UiField TextBox yellowCards;
	@UiField TextBox redCards;
	@UiField Anchor  scrumLink;
	@UiField Button saveButton;
	@UiField Button refetchButton;
	@UiField Button cancelButton;

	private List<FieldDefinition<T>> fieldDefinitions;
	@Override
	public void setFieldDefinitions(List<FieldDefinition<T>> FieldDefinition) {
		this.fieldDefinitions = FieldDefinition;
		fieldDefinitions.get(0).bind( tries);                
		fieldDefinitions.get(1).bind( conversionsAttempted);    
		fieldDefinitions.get(2).bind( conversionsMade);                         
		fieldDefinitions.get(3).bind( penaltiesAttempted);              
		fieldDefinitions.get(4).bind( penaltiesMade);        
		fieldDefinitions.get(5).bind( dropGoalsAttempted);   
		fieldDefinitions.get(6).bind( dropGoalsMade);                 
		fieldDefinitions.get(7).bind(runs);                 
		fieldDefinitions.get(8).bind(passes);               
		fieldDefinitions.get(9).bind(kicks);                
		fieldDefinitions.get(11).bind(possesion);            
		fieldDefinitions.get(12).bind(territory);            
		fieldDefinitions.get(10).bind(metersRun);            
		fieldDefinitions.get(13).bind(cleanBreaks);          
		fieldDefinitions.get(14).bind(defendersBeaten);      
		fieldDefinitions.get(15).bind(offloads);             
		fieldDefinitions.get(16).bind(rucks);                
		fieldDefinitions.get(17).bind(rucksWon);             
		fieldDefinitions.get(18).bind(mauls);                
		fieldDefinitions.get(19).bind(maulsWon);         
		fieldDefinitions.get(20).bind(turnovers);  
		fieldDefinitions.get(21).bind(tacklesMade);          
		fieldDefinitions.get(22).bind(tacklesMissed); 
		fieldDefinitions.get(23).bind(scrumsPutIn);          
		fieldDefinitions.get(24).bind(scrumsWonOnOwnPut);    
		fieldDefinitions.get(25).bind(lineoutsThrownIn);     
		fieldDefinitions.get(26).bind(lineoutsWonOnOwnThrow);   
		fieldDefinitions.get(27).bind(penaltiesConceded);    
		fieldDefinitions.get(28).bind(yellowCards);          
		fieldDefinitions.get(29).bind(redCards);             
		fieldDefinitions.get(30).bind(scrumLink);  
		fieldDefinitions.get(31).bind( nameAndId); 
		fieldDefinitions.get(32).bind( teamAndId); 
		fieldDefinitions.get(33).bind( matchAndId); 
	}

	@UiHandler("teamAndId")
	public void onShowTeamPopUp(ClickEvent event) {
		//presenter.showTeamPopup(target);
	}
	
	private TeamMatchStatsPopupViewPresenter<T> presenter;
	private T target;

	public TeamMatchStatsPopupViewImpl()
	{
		setWidget(uiBinder.createAndBindUi(this));
		this.setText("Edit Team Match Stats");
	}

	@UiHandler("cancelButton")
	void onCancelButtonClicked(ClickEvent event) {	
		if (presenter != null) {
			presenter.onCancelEditTeamMatchStatsClicked();
		}
	}

	@UiHandler("refetchButton")
	void onRefetchButtonClicked(ClickEvent event) {	
		if (presenter != null) {
			presenter.onRefetchEditTeamMatchStatsClicked(target);
		}
	}
	
	@UiHandler("saveButton")
	void onSaveButtonClicked(ClickEvent event) {	
		if (presenter != null) {
			boolean bad = false;
			for (FieldDefinition<T> field : fieldDefinitions) {
				try { 
					field.update(target);
				} catch (Exception e) {
					Window.alert(e.getLocalizedMessage());
					bad = true;
					break;
				}
			}
			
			if (!bad) {
				presenter.onSaveTeamMatchStatsClicked(target);
			}
		}
	}

	@Override
	public void setTarget(T target)
	{
		clearContents();
		this.target = target;
		for (int j = 0; j < fieldDefinitions.size(); ++j) {
			FieldDefinition<T> fieldDefinition = fieldDefinitions.get(j);
			fieldDefinition.render(target);
		}	
	}


	private void clearContents() {
		for (int j = 0; j < fieldDefinitions.size(); ++j) {
			FieldDefinition<T> fieldDefinition = fieldDefinitions.get(j);
			fieldDefinition.clear();
		}
	}

	@Override
	public void setPresenter(TeamMatchStatsPopupViewPresenter<T> listener) {
		presenter = listener;	
	}

	@Override
	public void clear() {
		clearContents();	
	}
}
