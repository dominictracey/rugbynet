/**
 * 
 */
package net.rugby.foundation.admin.client.ui.portal;

import java.util.HashMap;
import java.util.Map;

import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.model.shared.IPlayer;

import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author home
 *
 */
public class EditTTLInfo extends DialogBox {

	public int numPlayersPerTeam = 10;
	
	private static EditTTLInfoUiBinder uiBinder = GWT
			.create(EditTTLInfoUiBinder.class);

	interface EditTTLInfoUiBinder extends UiBinder<Widget, EditTTLInfo> {
	}

	public interface EditTTLInfoPresenter {
		void saveTTIText(TopTenSeedData tti);
		void cancelTTITextEdit(TopTenSeedData tti);
	} 
	
	public EditTTLInfo() {
		setWidget(uiBinder.createAndBindUi(this));
		
//		for (int i=0; i<10; ++i) {
//			final int _i = i;
//			AnchorListItem ali = new AnchorListItem();
//			ali.setText(Integer.toString(i));
//			ali.addClickHandler(new ClickHandler() {
//			    @Override
//			    public void onClick(ClickEvent clickEvent) {
//			    	numPlayersPerTeam = _i;
//			    }
//			  });
//			playersPerTeam.add(ali);
//		}
	}

	@UiField Button save;
	@UiField Button cancel;
	@UiField TextArea description;
	@UiField TextBox title;
//	@UiField ListBox playersPerTeam;
	@UiField HTMLPanel twitterHandles;
	
	Map<IPlayer,TextBox> twitterDictionary = new HashMap<IPlayer,TextBox>();
	
	TopTenSeedData v = null;
	private EditTTLInfoPresenter listener;
	
	@UiHandler("save")
	void onClickSave(ClickEvent e) {
		v.setTitle(title.getText());
		v.setDescription(description.getText());
		numPlayersPerTeam = 10;// playersPerTeam.getSelectedIndex()+1;
		v.setPlayersPerTeam(numPlayersPerTeam);
		listener.saveTTIText(v);
	}
	
	
	@UiHandler("cancel")
	void onClickCacnel(ClickEvent e) {
		listener.cancelTTITextEdit(v);
	}


	public void showTTI(TopTenSeedData v) {
		this.v = v;
		title.setText(v.getTitle());
		description.setText(v.getDescription());
		show();
	}

	public void setPresenter(EditTTLInfoPresenter p) {
		listener = p;
	}


	public void addTwitterPlayer(IPlayer p) {
		twitterHandles.add(new Label(p.getDisplayName()));
		TextBox textBox = new TextBox();
		twitterHandles.add(textBox);
		if (p.getTwitterHandle() != null) {
			textBox.setText(p.getTwitterHandle());
		}
		twitterDictionary.put(p,textBox);
	}
	
	public Map<IPlayer,String> getTwitterDictionary() {
		 Map<IPlayer,String> retval = new  HashMap<IPlayer,String>();
		 
		 for (IPlayer p: twitterDictionary.keySet()) {
			 // only add an entry to the return dictionary if the user added text.
			 if (!twitterDictionary.get(p).getText().isEmpty())  {
				 retval.put(p,twitterDictionary.get(p).getText());
			 }
		 }
		 
		 return retval;
	}


	public void removePlayers() {		
		twitterHandles.clear();
		
		twitterDictionary = new HashMap<IPlayer,TextBox>();
	}
	

}
