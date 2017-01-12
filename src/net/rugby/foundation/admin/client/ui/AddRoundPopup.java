/**
 * 
 */
package net.rugby.foundation.admin.client.ui;

import java.util.List;
import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.ClientFactory.GetUniversalRoundsListCallback;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.UniversalRound;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ListBox;
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
public class AddRoundPopup extends DialogBox {

	public int numPlayersPerTeam = 10;

	private static AddRoundPopupUiBinder uiBinder = GWT
			.create(AddRoundPopupUiBinder.class);

	interface AddRoundPopupUiBinder extends UiBinder<Widget, AddRoundPopup> {
	}

	public interface AddRoundPopupPresenter {
		void addRound(ICompetition comp, int uri, String name);
		void scrapeRound(ICompetition comp, int uri, String name);
		void cancelAddRound();
	} 

	public AddRoundPopup() {
		setWidget(uiBinder.createAndBindUi(this));

		setText("Add Round");
	}

	@UiField Button save;
	@UiField Button scrape;
	@UiField Button cancel;
	@UiField TextBox name;
	@UiField ListBox urList;
	
	List<UniversalRound> universalRounds = null;

	private AddRoundPopupPresenter listener;

	private ICompetition comp;
	
	public void setClientFactory(ClientFactory clientFactory) {
		clientFactory.getUniversalRoundsListAsync(52, new GetUniversalRoundsListCallback() {

			@Override
			public void onUniversalRoundListFetched(List<UniversalRound> urs) {
				universalRounds = urs;
				for (UniversalRound c : urs) {
					urList.addItem(c.shortDesc, Integer.toString(c.ordinal));
				}
			}
		});

	}


	@UiHandler("save")
	void onClickSave(ClickEvent e) {
		int index = urList.getSelectedIndex();
		//UniversalRound ur = universalRounds.get(index);
		listener.addRound(comp, Integer.parseInt(urList.getValue(index),10), name.getValue());
	}

	@UiHandler("scrape")
	void onClickScrape(ClickEvent e) {
		int index = urList.getSelectedIndex();
		//UniversalRound ur = universalRounds.get(index);
		int what = Integer.parseInt(urList.getValue(index),10);
		listener.scrapeRound(comp, Integer.parseInt(urList.getValue(index),10), name.getValue());
	}
	
	@UiHandler("cancel")
	void onClickCacnel(ClickEvent e) {
		listener.cancelAddRound();
	}	

	public void setPresenter(AddRoundPopupPresenter p) {
		listener = p;
	}


	public void setComp(ICompetition comp) {
		this.comp = comp;
		
	}


}
