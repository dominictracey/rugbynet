/**
 * 
 */
package net.rugby.foundation.admin.client.ui;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.Round;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author home
 *
 */
public class AddMatchPopup extends DialogBox {

	public int numPlayersPerTeam = 10;

	private static AddMatchPopupUiBinder uiBinder = GWT
			.create(AddMatchPopupUiBinder.class);

	interface AddMatchPopupUiBinder extends UiBinder<Widget, AddMatchPopup> {
	}

	public interface AddMatchPopupPresenter {
		void cancelAddMatch();
		void addMatch(Round round, Long homeTeamId, Long visitingTeamId);
	} 

	public AddMatchPopup() {
		setWidget(uiBinder.createAndBindUi(this));

		setText("Add Match");
	}

	@UiField Button save;
	@UiField Button cancel;
	@UiField TextBox homeTeamId;
	@UiField TextBox visitingTeamId;
	

	private AddMatchPopupPresenter listener;

	private Round round;
	
	public void setClientFactory(ClientFactory clientFactory) {
		
	}


	@UiHandler("save")
	void onClickSave(ClickEvent e) {
		listener.addMatch(round, Long.parseLong(homeTeamId.getValue()), Long.parseLong(visitingTeamId.getValue()));
	}


	@UiHandler("cancel")
	void onClickCacnel(ClickEvent e) {
		listener.cancelAddMatch();
	}	

	public void setPresenter(AddMatchPopupPresenter p) {
		listener = p;
	}


	public void setRound(Round round) {
		this.round = round;
	}

}
