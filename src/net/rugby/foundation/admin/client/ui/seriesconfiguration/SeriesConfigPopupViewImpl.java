package net.rugby.foundation.admin.client.ui.seriesconfiguration;

import java.util.List;

import net.rugby.foundation.admin.client.ui.FieldDefinition;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

public class SeriesConfigPopupViewImpl<T> extends DialogBox implements SeriesConfigPopupView<T>
{
	private static SeriesConfigPopupViewImplUiBinder uiBinder = GWT.create(SeriesConfigPopupViewImplUiBinder.class);

	@UiTemplate("SeriesConfigPopupViewImpl.ui.xml")

	interface SeriesConfigPopupViewImplUiBinder extends UiBinder<Widget, SeriesConfigPopupViewImpl<?>>
	{
	}

	@UiField Label id;
	@UiField TextBox displayName;
	@UiField ListBox startDate;
	@UiField ListBox countries;
	@UiField ListBox comps;
	@UiField ListBox hostComp;
	@UiField ListBox mode;
	
	@UiField CheckBox round;
	@UiField CheckBox inForm;
	@UiField CheckBox bestYear;
	@UiField CheckBox bestAllTime;
	@UiField CheckBox average;
	
	@UiField CheckBox isLive;
	
	@UiField Button saveButton;
	@UiField Button cancelButton;

	private List<FieldDefinition<T>> fieldDefinitions;
	@Override
	public void setFieldDefinitions(List<FieldDefinition<T>> FieldDefinition) {
		this.fieldDefinitions = FieldDefinition;
		int i = 0;
		fieldDefinitions.get(i++).bind(id);      
		fieldDefinitions.get(i++).bind(displayName);        
		fieldDefinitions.get(i++).bind(comps);  
		fieldDefinitions.get(i++).bind(countries);    
		fieldDefinitions.get(i++).bind(mode); 
		fieldDefinitions.get(i++).bind(hostComp);
		fieldDefinitions.get(i++).bind(startDate);	
		fieldDefinitions.get(i++).bind(round); 
		fieldDefinitions.get(i++).bind(inForm); 
		fieldDefinitions.get(i++).bind(bestYear); 
		fieldDefinitions.get(i++).bind(bestAllTime); 
		fieldDefinitions.get(i++).bind(average); 
		fieldDefinitions.get(i++).bind(isLive); 

	}

	private Presenter<T> presenter;
	private T sConfig;

	public SeriesConfigPopupViewImpl()
	{
		setWidget(uiBinder.createAndBindUi(this));
		this.setText("Edit Series Config Information");
	}

	@UiHandler("cancelButton")
	void onCancelButtonClicked(ClickEvent event) {	
		if (presenter != null) {
			presenter.onCancelConfigClicked();
		}
	}

	@UiHandler("saveButton")
	void onSaveButtonClicked(ClickEvent event) {	
		if (presenter != null) {
			for (FieldDefinition<T> field : fieldDefinitions) {
				field.update(sConfig);
			}
			presenter.onSaveConfigClicked(sConfig);
		}
	}

	@Override
	public void setConfig(T config)
	{
		clearContents();
		this.sConfig = config;
		for (int j = 0; j < fieldDefinitions.size(); ++j) {
			FieldDefinition<T> fieldDefinition = fieldDefinitions.get(j);
			fieldDefinition.render(config);
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
