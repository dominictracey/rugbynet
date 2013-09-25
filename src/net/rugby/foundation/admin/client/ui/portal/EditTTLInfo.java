/**
 * 
 */
package net.rugby.foundation.admin.client.ui.portal;

import net.rugby.foundation.admin.shared.TopTenSeedData;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.DropdownButton;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
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
public class EditTTLInfo extends DialogBox {

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
	}

	@UiField Button save;
	@UiField Button cancel;
	@UiField TextArea description;
	@UiField TextBox title;
	@UiField DropdownButton playersPerTeam;
	
	TopTenSeedData v = null;
	private EditTTLInfoPresenter listener;
	
	@UiHandler("save")
	void onClickSave(ClickEvent e) {
		v.setTitle(title.getText());
		v.setDescription(description.getText());
		int ppt = Integer.parseInt(playersPerTeam.getLastSelectedNavLink().getText());
		v.setPlayersPerTeam(ppt);
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

}
