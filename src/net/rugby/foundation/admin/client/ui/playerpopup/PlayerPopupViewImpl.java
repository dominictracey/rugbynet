package net.rugby.foundation.admin.client.ui.playerpopup;

import java.util.List;

import net.rugby.foundation.admin.client.ui.FieldDefinition;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public class PlayerPopupViewImpl<T> extends DialogBox implements PlayerPopupView<T>
{
	private static PlayerPopupViewImplUiBinder uiBinder = GWT.create(PlayerPopupViewImplUiBinder.class);

	@UiTemplate("PlayerPopupViewImpl.ui.xml")

	interface PlayerPopupViewImplUiBinder extends UiBinder<Widget, PlayerPopupViewImpl<?>>
	{
	}

	@UiField Label id;
	@UiField TextBox scrumId;	
	@UiField TextBox displayName;
	@UiField DatePicker birthDate;
	@UiField TextBox height;
	@UiField TextBox weight;
	@UiField TextBox imageUri;
	@UiField ListBox country;
	@UiField ListBox position;
	@UiField TextBox numCaps;
	@UiField TextBox givenName;
	@UiField TextBox surName;
	@UiField TextBox shortName;
	@UiField Anchor scrumLink;

	@UiField Button saveButton;
	@UiField Button cancelButton;

	private List<FieldDefinition<T>> fieldDefinitions;
	@Override
	public void setFieldDefinitions(List<FieldDefinition<T>> FieldDefinition) {
		this.fieldDefinitions = FieldDefinition;
		fieldDefinitions.get(0).bind(id);
		fieldDefinitions.get(1).bind(scrumId);        
		fieldDefinitions.get(2).bind(displayName);        
		fieldDefinitions.get(3).bind(birthDate);        
		fieldDefinitions.get(4).bind(height);    
		fieldDefinitions.get(5).bind(weight); 
		fieldDefinitions.get(6).bind(imageUri); 
		fieldDefinitions.get(7).bind(country); 
		fieldDefinitions.get(8).bind(position); 
		fieldDefinitions.get(9).bind(numCaps); 
		fieldDefinitions.get(10).bind(givenName); 
		fieldDefinitions.get(11).bind(surName); 
		fieldDefinitions.get(12).bind(shortName); 
		fieldDefinitions.get(13).bind(scrumLink);
	}

	private Presenter<T> presenter;
	private T player;

	public PlayerPopupViewImpl()
	{
		setWidget(uiBinder.createAndBindUi(this));
		this.setText("Edit Player Information");
	}

	@UiHandler("cancelButton")
	void onCancelButtonClicked(ClickEvent event) {	
		if (presenter != null) {
			presenter.onCancelEditPlayerClicked();
		}
	}

	@UiHandler("saveButton")
	void onSaveButtonClicked(ClickEvent event) {	
		if (presenter != null) {
			for (FieldDefinition<T> field : fieldDefinitions) {
				field.update(player);
			}
			presenter.onSaveEditPlayerClicked(player);
		}
	}

	@Override
	public void setPlayer(T player)
	{
		clearContents();
		this.player = player;
		for (int j = 0; j < fieldDefinitions.size(); ++j) {
			FieldDefinition<T> fieldDefinition = fieldDefinitions.get(j);
			fieldDefinition.render(player);
		}	
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
