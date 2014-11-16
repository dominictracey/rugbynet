/**
 * 
 */
package net.rugby.foundation.topten.client.ui.toptenlistview;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextArea;
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
		void saveTTIText(TopTenItemView tti);
		void cancelTTITextEdit(TopTenItemView tti);
	} 
	
	public EditTTIText() {
		setWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Button save;
	@UiField
	Button cancel;
	@UiField
	TextArea text;
	
	
	TopTenItemView v = null;
	private EditTTITextPresenter listener;
	
	@UiHandler("save")
	void onClickSave(ClickEvent e) {
		v.getItem().setText(text.getText());
		listener.saveTTIText(v);
	}
	
	
	@UiHandler("cancel")
	void onClickCacnel(ClickEvent e) {
		listener.cancelTTITextEdit(v);
	}


	public void showTTI(TopTenItemView v) {
		this.v = v;
		this.setText(this.v.getItem().getPlayer().getDisplayName());
		text.setText(v.getItem().getText());
	}

	public void setPresenter(EditTTITextPresenter p) {
		listener = p;
	}

}
