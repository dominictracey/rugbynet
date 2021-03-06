package net.rugby.foundation.admin.client.ui.seriesconfiguration;

import java.util.List;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.ClientFactory.GetCompListCallback;
import net.rugby.foundation.admin.client.ClientFactory.GetCountryListCallback;
import net.rugby.foundation.admin.client.ClientFactory.GetUniversalRoundsListCallback;
import net.rugby.foundation.admin.client.ui.FieldDefinition;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.UniversalRound;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.InlineRadio;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
	
	@UiField InlineRadio roundMinMinutes;
	@UiField InlineRadio totalMinMinutes;
	@UiField TextBox minMinutes;

	
	@UiField CheckBox round;
	@UiField CheckBox inForm;
	@UiField CheckBox bestYear;
	@UiField CheckBox bestAllTime;
	@UiField CheckBox average;
	
	@UiField CheckBox isLive;
	
	@UiField Button saveButton;
	@UiField Button cancelButton;

	private SeriesConfigPopupViewFieldDefinitions<T> fieldDefinitions;
	protected ClientFactory clientFactory;
	
	@Override
	public void setFieldDefinitions(SeriesConfigPopupViewFieldDefinitions<T> FieldDefinition, final ClientFactory clientFactory) {

		this.fieldDefinitions = FieldDefinition;
		this.clientFactory = clientFactory;
		
		// need the list of countries and comps first
		if (fieldDefinitions.getCountryList() == null) {
			clientFactory.getCountryListAsync(new GetCountryListCallback() {
	
				@Override
				public void onCountryListFetched(List<ICountry> countryList) {
					fieldDefinitions.setCountryList(countryList);
					clientFactory.getCoreConfigurationAsync(true, new AsyncCallback<ICoreConfiguration>() {
	
						@Override
						public void onSuccess(ICoreConfiguration coreConfig) {
							
							fieldDefinitions.setConfig(coreConfig);

							clientFactory.getUniversalRoundsListAsync(52, new GetUniversalRoundsListCallback() {

								@Override
								public void onUniversalRoundListFetched(List<UniversalRound> urs) {
									fieldDefinitions.setUrList(urs);
									int i = 0;
									fieldDefinitions.getFieldDefinitions().get(i++).bind(id);      
									fieldDefinitions.getFieldDefinitions().get(i++).bind(displayName);        
									fieldDefinitions.getFieldDefinitions().get(i++).bind(comps);  
									fieldDefinitions.getFieldDefinitions().get(i++).bind(countries);    
									fieldDefinitions.getFieldDefinitions().get(i++).bind(mode); 
									fieldDefinitions.getFieldDefinitions().get(i++).bind(hostComp);
									fieldDefinitions.getFieldDefinitions().get(i++).bind(startDate);	
									fieldDefinitions.getFieldDefinitions().get(i++).bind(round); 
									fieldDefinitions.getFieldDefinitions().get(i++).bind(inForm); 
									fieldDefinitions.getFieldDefinitions().get(i++).bind(bestYear); 
									fieldDefinitions.getFieldDefinitions().get(i++).bind(bestAllTime); 
									fieldDefinitions.getFieldDefinitions().get(i++).bind(average); 
									fieldDefinitions.getFieldDefinitions().get(i++).bind(isLive); 
									fieldDefinitions.getFieldDefinitions().get(i++).bind(roundMinMinutes);
									fieldDefinitions.getFieldDefinitions().get(i++).bind(totalMinMinutes); 
									fieldDefinitions.getFieldDefinitions().get(i++).bind(minMinutes);
								}
							});
						}

						@Override
						public void onFailure(Throwable caught) {
							Notify.notify("Failed to load core configuration so can't display series configurationd dialog", NotifyType.DANGER);
						}
					});
				}
			});
		}

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
			for (FieldDefinition<ISeriesConfiguration> field : fieldDefinitions.getFieldDefinitions()) {
				field.update((ISeriesConfiguration) sConfig);
			}
			presenter.onSaveConfigClicked(sConfig);
		}
	}
	
	@UiHandler("average")
	void onAverageClicked(ClickEvent event) {	

			roundMinMinutes.setEnabled(average.getValue());
			totalMinMinutes.setEnabled(average.getValue());
			minMinutes.setEnabled(average.getValue());
	}
	
	@Override
	public void setConfig(final T config)
	{
		clearContents();
		this.sConfig = config;
		
		// need the list of countries and comps first
		if (fieldDefinitions.getCountryList() == null) {
			clientFactory.getCountryListAsync(new GetCountryListCallback() {
	
				@Override
				public void onCountryListFetched(List<ICountry> countries) {
					fieldDefinitions.setCountryList(countries);
					
					clientFactory.getCoreConfigurationAsync(true, new AsyncCallback<ICoreConfiguration>() {
	
						@Override
						public void onSuccess(ICoreConfiguration coreConfig) {
							
							fieldDefinitions.setConfig(coreConfig);
							
							clientFactory.getUniversalRoundsListAsync(52, new GetUniversalRoundsListCallback() {

								@Override
								public void onUniversalRoundListFetched(List<UniversalRound> urs) {
									fieldDefinitions.setUrList(urs);
								
									for (int j = 0; j < fieldDefinitions.getFieldDefinitions().size(); ++j) {
										FieldDefinition<ISeriesConfiguration> fieldDefinition = fieldDefinitions.getFieldDefinitions().get(j);
										fieldDefinition.render((ISeriesConfiguration)config);
									}
								}
							});
						}

						@Override
						public void onFailure(Throwable caught) {
							Notify.notify("Failed to load core configuration so can't display series configurationd dialog", NotifyType.DANGER);
						}
					});
				}
			});
		} else if (fieldDefinitions.getConfig() == null) {
			clientFactory.getCoreConfigurationAsync(true, new AsyncCallback<ICoreConfiguration>() {
				
				@Override
				public void onSuccess(ICoreConfiguration coreConfig) {
					
					fieldDefinitions.setConfig(coreConfig);
					clientFactory.getUniversalRoundsListAsync(52, new GetUniversalRoundsListCallback() {

						@Override
						public void onUniversalRoundListFetched(List<UniversalRound> urs) {
							fieldDefinitions.setUrList(urs);
						
							for (int j = 0; j < fieldDefinitions.getFieldDefinitions().size(); ++j) {
								FieldDefinition<ISeriesConfiguration> fieldDefinition = fieldDefinitions.getFieldDefinitions().get(j);
								fieldDefinition.render((ISeriesConfiguration)config);
							}
						}
					});
				}


				@Override
				public void onFailure(Throwable caught) {
					Notify.notify("Failed to load core configuration so can't display series configurationd dialog", NotifyType.DANGER);
				}
			});
		} else if (fieldDefinitions.getUrList() == null) {
			clientFactory.getUniversalRoundsListAsync(52, new GetUniversalRoundsListCallback() {

				@Override
				public void onUniversalRoundListFetched(List<UniversalRound> urs) {
					fieldDefinitions.setUrList(urs);
				
					for (int j = 0; j < fieldDefinitions.getFieldDefinitions().size(); ++j) {
						FieldDefinition<ISeriesConfiguration> fieldDefinition = fieldDefinitions.getFieldDefinitions().get(j);
						fieldDefinition.render((ISeriesConfiguration)config);
					}
				}
			});
		} else {
			for (int j = 0; j < fieldDefinitions.getFieldDefinitions().size(); ++j) {
				FieldDefinition<ISeriesConfiguration> fieldDefinition = fieldDefinitions.getFieldDefinitions().get(j);
				fieldDefinition.render((ISeriesConfiguration)config);
			}
		}
		
	
	}


	private void clearContents() {
		for (int j = 0; j < fieldDefinitions.getFieldDefinitions().size(); ++j) {
			FieldDefinition<ISeriesConfiguration> fieldDefinition = fieldDefinitions.getFieldDefinitions().get(j);
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
