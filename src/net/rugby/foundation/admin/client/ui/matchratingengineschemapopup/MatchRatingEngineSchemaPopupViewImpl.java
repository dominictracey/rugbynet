package net.rugby.foundation.admin.client.ui.matchratingengineschemapopup;

import java.util.List;

import net.rugby.foundation.admin.client.ui.FieldDefinition;
import net.rugby.foundation.admin.shared.IMatchRatingEngineSchema;
import net.rugby.foundation.admin.shared.ScrumMatchRatingEngineSchema20130713;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class MatchRatingEngineSchemaPopupViewImpl extends DialogBox implements MatchRatingEngineSchemaPopupView<ScrumMatchRatingEngineSchema20130713>
{
	private static PlayerPopupViewImplUiBinder uiBinder = GWT.create(PlayerPopupViewImplUiBinder.class);

	@UiTemplate("MatchRatingEngineSchemaPopupViewImpl.ui.xml")

	interface PlayerPopupViewImplUiBinder extends UiBinder<Widget, MatchRatingEngineSchemaPopupViewImpl>
	{
	}

	@UiField Label   id;
	@UiField TextBox name;
	@UiField TextBox description;

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
	
	@UiField TextBox scrumShareWeight; // .4F;
	@UiField TextBox lineoutShareWeight; // .4F;
	@UiField TextBox ruckShareWeight; // .3F;
	@UiField TextBox maulShareWeight; // .3F;
	@UiField TextBox minutesShareWeight; // .2F;
	@UiField TextBox pointsDifferentialWeight; // .2F;
	
	@UiField Button saveButton;
	@UiField Button saveAsCopyButton;
	@UiField Button deleteRatingsButton;
	@UiField Button deleteButton;
	@UiField Button setAsDefaultButton;
	@UiField Button cancelButton;
	
	private List<FieldDefinition<ScrumMatchRatingEngineSchema20130713>> fieldDefinitions;
	@Override
	public void setFieldDefinitions(List<FieldDefinition<ScrumMatchRatingEngineSchema20130713>> FieldDefinition) {
		int i = 0;
		this.fieldDefinitions = FieldDefinition;
		fieldDefinitions.get(i++).bind( id);    
		fieldDefinitions.get(i++).bind( name);                   
		fieldDefinitions.get(i++).bind( description);                  
             
		fieldDefinitions.get(i++).bind( tries);                   
		fieldDefinitions.get(i++).bind( tryAssists);              
		fieldDefinitions.get(i++).bind( points);                  
		fieldDefinitions.get(i++).bind( runs);                    
		fieldDefinitions.get(i++).bind( passes);                  
		fieldDefinitions.get(i++).bind(kicks);                    
		fieldDefinitions.get(i++).bind(metersRun);               
		fieldDefinitions.get(i++).bind(cleanBreaks);              
		fieldDefinitions.get(i++).bind(defendersBeaten);          
		fieldDefinitions.get(i++).bind(offloads);                
		fieldDefinitions.get(i++).bind(turnovers);               
		fieldDefinitions.get(i++).bind(tacklesMade);             
		fieldDefinitions.get(i++).bind(tacklesMissed);           
		fieldDefinitions.get(i++).bind(lineoutsWonOnThrow);      
		fieldDefinitions.get(i++).bind(lineoutsStolenOnOppThrow);
		fieldDefinitions.get(i++).bind(penaltiesConceded); 
		fieldDefinitions.get(i++).bind(yellowCards);             
		fieldDefinitions.get(i++).bind(redCards);   
              
		fieldDefinitions.get(i++).bind(scrumShareWeight);
		fieldDefinitions.get(i++).bind(lineoutShareWeight);
		fieldDefinitions.get(i++).bind(ruckShareWeight); //
		fieldDefinitions.get(i++).bind(maulShareWeight); //
		fieldDefinitions.get(i++).bind(minutesShareWeight);
		fieldDefinitions.get(i++).bind(pointsDifferentialWeight);
	}
	
	private MatchRatingEngineSchemaPopupViewPresenter<ScrumMatchRatingEngineSchema20130713> presenter;
	private ScrumMatchRatingEngineSchema20130713 target;

	public MatchRatingEngineSchemaPopupViewImpl()
	{
		setWidget(uiBinder.createAndBindUi(this));
		this.setText("Edit Match Rating Engine Schema");
	}

	@UiHandler("cancelButton")
	void onCancelButtonClicked(ClickEvent event) {	
		if (presenter != null) {
			presenter.onCancelEditMatchRatingEngineSchemaClicked();
		}
	}
	
	@UiHandler("saveButton")
	void onSaveButtonClicked(ClickEvent event) {	
		if (presenter != null) {
			boolean bad = false;
			for (FieldDefinition<ScrumMatchRatingEngineSchema20130713> field : fieldDefinitions) {
				try { 
					field.update(target);
				} catch (Exception e) {
					Window.alert(e.getLocalizedMessage());
					bad = true;
					break;
				}
			}
			
			if (!bad) {
				presenter.onSaveMatchRatingEngineSchemaClicked(target);
			}
		}
	}

	@UiHandler("saveAsCopyButton")
	void onSaveAsCopyButtonClicked(ClickEvent event) {	
		if (presenter != null) {
			boolean bad = false;
			for (FieldDefinition<ScrumMatchRatingEngineSchema20130713> field : fieldDefinitions) {
				try { 
					field.update(target);
				} catch (Exception e) {
					Window.alert(e.getLocalizedMessage());
					bad = true;
					break;
				}
			}
			
			if (!bad) {
				presenter.onSaveAsCopyMatchRatingEngineSchemaClicked(target);
			}
		}
	}
	
	@UiHandler("deleteRatingsButton")
	void onDeleteRatingsButtonClicked(ClickEvent event) {	
		if (presenter != null) {
			presenter.onDeleteRatingsForMatchRatingEngineSchemaClicked(target);
		}
	}
	
	@UiHandler("deleteButton")
	void onDeleteButtonClicked(ClickEvent event) {	
		if (presenter != null) {
			presenter.onDeleteMatchRatingEngineSchemaClicked(target);
		}
	}

	@UiHandler("setAsDefaultButton")
	void onSetAsDefaultButtonClicked(ClickEvent event) {	
		if (presenter != null) {
			presenter.onSetMatchRatingEngineSchemaAsDefaultClicked(target);
		}
	}
	
	@Override
	public void setTarget(ScrumMatchRatingEngineSchema20130713 target)
	{
		clearContents();
		this.target = target;
		for (int j = 0; j < fieldDefinitions.size(); ++j) {
			FieldDefinition<ScrumMatchRatingEngineSchema20130713> fieldDefinition = fieldDefinitions.get(j);
			fieldDefinition.render(target);
		}	
	}


	private void clearContents() {
		for (int j = 0; j < fieldDefinitions.size(); ++j) {
			FieldDefinition<ScrumMatchRatingEngineSchema20130713> fieldDefinition = fieldDefinitions.get(j);
			if (fieldDefinition != null) {
				fieldDefinition.clear();
			}
		}
	}

	@Override
	public void setPresenter(MatchRatingEngineSchemaPopupViewPresenter<ScrumMatchRatingEngineSchema20130713> listener) {
		presenter = listener;	
	}

	@Override
	public void clear() {
		clearContents();	
	}
}
