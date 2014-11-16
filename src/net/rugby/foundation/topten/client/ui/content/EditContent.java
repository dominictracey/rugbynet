/**
 * 
 */
package net.rugby.foundation.topten.client.ui.content;

import net.rugby.foundation.model.shared.IContent;

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
public class EditContent extends DialogBox {

	private static EditContentUiBinder uiBinder = GWT
			.create(EditContentUiBinder.class);

	interface EditContentUiBinder extends UiBinder<Widget, EditContent> {
	}

	public interface EditContentPresenter {
		void saveContent(IContent content);
		void cancelEditContent();
	} 
	
	public EditContent() {
		setWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Button save;
	@UiField
	Button cancel;
	@UiField
	TextArea text;
	
	
	IContent content = null;
	private EditContentPresenter listener;
	
	@UiHandler("save")
	void onClickSave(ClickEvent e) {
		content.setBody(text.getText());
		listener.saveContent(content);
	}
	
	
	@UiHandler("cancel")
	void onClickCacnel(ClickEvent e) {
		listener.cancelEditContent();
	}


	public void setContent(IContent content, EditContentPresenter listener) {
		this.content = content;
		this.listener = listener;
		text.setText(content.getBody());
		this.setText(content.getId().toString());
	}

	public void setPresenter(EditContentPresenter p) {
		listener = p;
	}

}
