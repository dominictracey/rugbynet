package net.rugby.foundation.client.ui;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.datepicker.client.DateBox;

public class EditPlayerViewImpl<T> extends DecoratorPanel implements EditPlayerView<T>
{
	private static EditPlayerViewImplUiBinder uiBinder = GWT.create(EditPlayerViewImplUiBinder.class);
	
	@UiTemplate("EditPlayerViewImpl.ui.xml")

	interface EditPlayerViewImplUiBinder extends UiBinder<Widget, EditPlayerViewImpl<?>>
	{
	}
//	@UiField DialogBox editPlayer;
	@UiField TextBox firstName;
	@UiField TextBox lastName;
	@UiField Button saveButton;
	@UiField Button cancelButton;
	@UiField TextBox displayName;
	@UiField ListBox position;
	@UiField TextBox numCaps;
	@UiField DateBox dateOfBirth;
	@UiField TextBox club;
	@UiField TextBox height;
	@UiField TextBox weight;
	@UiField CheckBox active;
	@UiField TextBox origRating;
	@UiField TextBox bioCredit;
	@UiField TextBox bioUrl;
	@UiField TextArea bioSnippet;
	@UiField ListBox team;
	
	private List<FieldDefinition<T>> fieldDefinitions;
	
	@Override
    public void setFieldDefinitions(List<FieldDefinition<T>> FieldDefinition) {
        this.fieldDefinitions = FieldDefinition;
        fieldDefinitions.get(0).bind(firstName);
        fieldDefinitions.get(1).bind(lastName);  
        fieldDefinitions.get(2).bind(displayName);        
        fieldDefinitions.get(3).bind(position);        
        fieldDefinitions.get(4).bind(numCaps);        
        fieldDefinitions.get(5).bind(dateOfBirth);        
        fieldDefinitions.get(6).bind(club);        
        fieldDefinitions.get(7).bind(height);        
        fieldDefinitions.get(8).bind(weight);   
        fieldDefinitions.get(9).bind(active);
        fieldDefinitions.get(10).bind(origRating);
        fieldDefinitions.get(11).bind(bioCredit);        
        fieldDefinitions.get(12).bind(bioUrl);        
        fieldDefinitions.get(13).bind(bioSnippet);        
        fieldDefinitions.get(14).bind(team);        

      }
    
	private Presenter<T> presenter;

	public EditPlayerViewImpl()
	{
		setWidget(uiBinder.createAndBindUi(this));
	}
	
  @UiHandler("saveButton")
  void onSaveButtonClicked(ClickEvent event) {	
	    if (presenter != null) {
	    	presenter.onSaveButtonClicked();
	    }
  }

  @UiHandler("cancelButton")
  void onCancelButtonClicked(ClickEvent event) {
    if (presenter != null) {
    	presenter.onCancelButtonClicked();
    }
  }
	@Override
	public void setPlayer(T player)
	{
		clearContents();
        for (int j = 0; j < fieldDefinitions.size(); ++j) {
	          FieldDefinition<T> fieldDefinition = fieldDefinitions.get(j);
	          fieldDefinition.render(player);
	        }	
	}

	@Override
	public T getPlayer(T player)
	{
        for (int j = 0; j < fieldDefinitions.size(); ++j) {
	          FieldDefinition<T> fieldDefinition = fieldDefinitions.get(j);
	          fieldDefinition.update(player);
	        }
		return player;
	}
	
	private void clearContents() {
        for (int j = 0; j < fieldDefinitions.size(); ++j) {
	          FieldDefinition<T> fieldDefinition = fieldDefinitions.get(j);
	          fieldDefinition.clear();
	        }
	}

	@Override
	public void setPresenter(Presenter<T> listener) {
		presenter = listener;
	}

	@Override
	public void clear() {
		clearContents();		
	}


	
}
