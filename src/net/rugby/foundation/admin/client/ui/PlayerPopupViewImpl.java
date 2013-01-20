package net.rugby.foundation.admin.client.ui;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class PlayerPopupViewImpl<T> extends DialogBox implements PlayerPopupView<T>
{
	private static PlayerPopupViewImplUiBinder uiBinder = GWT.create(PlayerPopupViewImplUiBinder.class);
	
	@UiTemplate("PlayerPopupViewImpl.ui.xml")

	interface PlayerPopupViewImplUiBinder extends UiBinder<Widget, PlayerPopupViewImpl<?>>
	{
	}

	@UiField DialogBox popupBox;
	@UiField HTML picture;
	@UiField HTML details;
	@UiField HTML ratingTable;
	@UiField HTML matchRatingTable;
	@UiField HTML footer;
	@UiField Button closeButton;
	
	private List<FieldDefinition<T>> fieldDefinitions;
	@Override
    public void setFieldDefinitions(List<FieldDefinition<T>> FieldDefinition) {
        this.fieldDefinitions = FieldDefinition;
        fieldDefinitions.get(0).bind(picture);
        fieldDefinitions.get(1).bind(details);        
        fieldDefinitions.get(2).bind(ratingTable);        
        fieldDefinitions.get(3).bind(matchRatingTable);        
        fieldDefinitions.get(4).bind(footer);    
        fieldDefinitions.get(5).bind(popupBox);  
      }
    
	private Presenter<T> presenter;

	public PlayerPopupViewImpl()
	{
		setWidget(uiBinder.createAndBindUi(this));
		//popupBox.setVisible(false);
	}
	
  @UiHandler("closeButton")
  void onCloseButtonClicked(ClickEvent event) {	
	    if (presenter != null) {
	        popupBox.setVisible(false);

	      presenter.onCloseButtonClicked();
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

        popupBox.center();

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				popupBox.hide();
				presenter.onCloseButtonClicked();
			}
		});
		
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

	public DialogBox getPopupBox() {
		return popupBox;
	}

	public void setPopupBox(DialogBox popupBox) {
		this.popupBox = popupBox;
	}


	
}
