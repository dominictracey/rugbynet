/**
 * 
 */
package net.rugby.foundation.admin.client.ui;

import net.rugby.foundation.admin.client.place.AdminTaskPlace;
import net.rugby.foundation.model.shared.IPlayer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * @author home
 *
 */
public class EditPlayer extends Composite {



	interface EditPlayerUiBinder extends UiBinder<Widget, EditPlayer> {
	}
	
	private static EditPlayerUiBinder uiBinder = GWT
			.create(EditPlayerUiBinder.class);

	public interface Presenter {
		void savePlayerInfo(IPlayer player);

		IPlayer getNewPlayer();
	} 
	
	public EditPlayer() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Button save;
	@UiField
	TextBox displayName;
	@UiField
	TextBox country;
	@UiField
	DatePicker birthDate;
	@UiField
	TextBox numCaps;

	
	IPlayer player = null;
	private Presenter listener;
	
	@UiHandler("save")
	void onClick(ClickEvent e) {
		if (player == null) {
			player = listener.getNewPlayer();
		}
		((IPlayer)player).setDisplayName(displayName.getText());
		//player.setCountry(country.getText());
		player.setNumCaps(Integer.parseInt(numCaps.getText()));
		player.setBirthDate(birthDate.getHighlightedDate());
		listener.savePlayerInfo(player);
	}

	public void ShowPlayer(IPlayer result) {
		player = result;
		displayName.setText(result.getDisplayName());
		//country.setText(result.getCountry());
		birthDate.setValue(result.getBirthDate());
		if (result.getNumCaps() != null) {
			numCaps.setText(result.getNumCaps().toString());
		}
	}
	
	public void ShowPlace(AdminTaskPlace place) {
		//displayName.setText(place.getName());
	}

	public void SetPresenter(Presenter p) {
		listener = p;
	}

}
