/**
 * 
 */
package net.rugby.foundation.admin.client.ui.portal;

import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.topten.model.shared.ITopTenItem;

import com.github.gwtbootstrap.client.ui.Button;
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
public class EditTTIText extends DialogBox {

	private static EditTTITextUiBinder uiBinder = GWT
			.create(EditTTITextUiBinder.class);

	interface EditTTITextUiBinder extends UiBinder<Widget, EditTTIText> {
	}

	public interface EditTTITextPresenter {
		void saveTTIText(TopTenSeedData tti);
		void cancelTTITextEdit(TopTenSeedData tti);
	} 
	
	public EditTTIText() {
		setWidget(uiBinder.createAndBindUi(this));
	}

	@UiField Button save;
	@UiField Button cancel;
	@UiField TextArea description;
	@UiField TextBox title;
	
	TopTenSeedData v = null;
	private EditTTITextPresenter listener;
	
	@UiHandler("save")
	void onClickSave(ClickEvent e) {
		v.setTitle(title.getText());
		v.setDescription(description.getText());
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

	public void setPresenter(EditTTITextPresenter p) {
		listener = p;
	}

}
