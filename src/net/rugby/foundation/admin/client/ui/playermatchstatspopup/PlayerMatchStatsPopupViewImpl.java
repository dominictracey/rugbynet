package net.rugby.foundation.admin.client.ui.playermatchstatspopup;

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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public class PlayerMatchStatsPopupViewImpl<T> extends DialogBox implements PlayerMatchStatsPopupView<T>
{
	private static PlayerPopupViewImplUiBinder uiBinder = GWT.create(PlayerPopupViewImplUiBinder.class);

	@UiTemplate("PlayerMatchStatsPopupViewImpl.ui.xml")

	interface PlayerPopupViewImplUiBinder extends UiBinder<Widget, PlayerMatchStatsPopupViewImpl<?>>
	{
	}

	@UiField Label   nameAndId;
	@UiField Anchor   playerAndId;
	@UiField Label   matchAndId;
	@UiField Label   slot;
	@UiField ListBox position;
	@UiField TextBox timePlayed;
	@UiField TextBox tries;
	@UiField TextBox tryAssists;
	@UiField TextBox points;
	@UiField TextBox runs;
	@UiField TextBox passes;
	@UiField TextBox kicks;
	@UiField TextBox metersRun;
	@UiField TextBox cleanBreaks;
	@UiField TextBox defendersBeaten;
	@UiField TextBox offloads;
	@UiField TextBox turnovers;
	@UiField TextBox tacklesMade;
	@UiField TextBox tacklesMissed;
	@UiField TextBox lineoutsWonOnThrow;
	@UiField TextBox lineoutsStolenOnOppThrow;
	@UiField TextBox penaltiesConceded;
	@UiField TextBox yellowCards;
	@UiField TextBox redCards;
	@UiField Anchor scrumLink;
	@UiField Button saveButton;
	@UiField Button cancelButton;

	private List<FieldDefinition<T>> fieldDefinitions;
	@Override
	public void setFieldDefinitions(List<FieldDefinition<T>> FieldDefinition) {
		this.fieldDefinitions = FieldDefinition;
		fieldDefinitions.get(0).bind( nameAndId);    
		fieldDefinitions.get(1).bind( playerAndId);                   
		fieldDefinitions.get(2).bind( matchAndId);                  
		fieldDefinitions.get(3).bind( slot);                            
		fieldDefinitions.get(4).bind( position);                 
		fieldDefinitions.get(5).bind( tries);                   
		fieldDefinitions.get(6).bind( tryAssists);              
		fieldDefinitions.get(7).bind( points);                  
		fieldDefinitions.get(8).bind( runs);                    
		fieldDefinitions.get(9).bind( passes);                  
		fieldDefinitions.get(10).bind(kicks);                    
		fieldDefinitions.get(11).bind(metersRun);               
		fieldDefinitions.get(12).bind(cleanBreaks);              
		fieldDefinitions.get(13).bind(defendersBeaten);          
		fieldDefinitions.get(14).bind(offloads);                
		fieldDefinitions.get(15).bind(turnovers);               
		fieldDefinitions.get(16).bind(tacklesMade);             
		fieldDefinitions.get(17).bind(tacklesMissed);           
		fieldDefinitions.get(18).bind(lineoutsWonOnThrow);      
		fieldDefinitions.get(19).bind(lineoutsStolenOnOppThrow);
		fieldDefinitions.get(20).bind(penaltiesConceded); 
		fieldDefinitions.get(21).bind(yellowCards);             
		fieldDefinitions.get(22).bind(redCards);   
		fieldDefinitions.get(23).bind(timePlayed);   
		fieldDefinitions.get(24).bind(scrumLink);                
		
	}

	@UiHandler("playerAndId")
	public void onShowPlayerPopUp(ClickEvent event) {
		presenter.showPlayerPopup(target);
	}
	
	private PlayerMatchStatsPopupViewPresenter<T> presenter;
	private T target;

	public PlayerMatchStatsPopupViewImpl()
	{
		setWidget(uiBinder.createAndBindUi(this));
		this.setText("Edit Player Match Stats");
	}

	@UiHandler("cancelButton")
	void onCancelButtonClicked(ClickEvent event) {	
		if (presenter != null) {
			presenter.onCancelEditPlayerMatchStatsClicked();
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
				presenter.onSavePlayerMatchStatsClicked(target);
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
	public void setPresenter(PlayerMatchStatsPopupViewPresenter<T> listener) {
		presenter = listener;	
	}

	@Override
	public void clear() {
		clearContents();	
	}
}
