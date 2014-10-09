package net.rugby.foundation.admin.client.ui.seriesconfiguration;

import java.util.List;

import net.rugby.foundation.admin.client.ui.FieldDefinition;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

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
	@UiField ListBox mode;
	
	
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
 
//		fieldDefinitions.get(i++).bind(endDate); 
		fieldDefinitions.get(i++).bind(mode); 
		fieldDefinitions.get(i++).bind(startDate);	
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

//	@Override
//	public void setComps(ICoreConfiguration result) {
//		config = result;
//		//if (!timeSeries.isToggled()) {
//		comps.clear();
//		comps.addItem("All","-1");
//
//		for (Long id: result.getCompetitionMap().keySet()) {
//			comps.addItem(result.getCompetitionMap().get(id), id.toString());
//		}
//
//		comps.addChangeHandler(new ChangeHandler() {
//
//			@Override
//			public void onChange(ChangeEvent event) {
////				rq = null;
////				compIds = null;
////				roundIds = null;
////				teamIds = null;
////				round.clear();
////				team.clear();
////				for (int i=0; i<pos.getItemCount(); i++) {
////					pos.setItemSelected(i, false);
////				}
////
////				for (int i=0; i<country.getItemCount(); i++) {
////					country.setItemSelected(i, false);
////				}
//
//				presenter.seriesConfigViewCompSelected(Long.parseLong(comps.getValue((comps.getSelectedIndex()))));			
//			}
//
//		});
//	}
	@Override
	public void setPresenter(Presenter<T> listener) {
		presenter = listener;	
	}

	@Override
	public void clear() {
		clearContents();	
	}
}
