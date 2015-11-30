/**
 * 
 */
package net.rugby.foundation.admin.client.ui;

import net.rugby.foundation.model.shared.IContent;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
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
		void createContent();
		void editContent(IContent content);
	} 
	
	public EditContent() {
		setWidget(uiBinder.createAndBindUi(this));
	}

	@UiField Button save;
	@UiField Button cancel;
	@UiField TextBox title;
	@UiField TextArea body;
	@UiField CheckBox active;
	@UiField TextBox div;
	@UiField TextBox menuOrder;
	@UiField CheckBox showInMenu;
	@UiField CheckBox showInFooter;
	
	
	IContent content = null;
	private EditContentPresenter listener;
	
	@UiHandler("save")
	void onClickSave(ClickEvent e) {
		content.setTitle(title.getText());
		content.setBody(body.getText());
		content.setActive(active.getValue());
		content.setDiv(div.getText());
		int mo = 0;
		try {
			if (!menuOrder.getText().isEmpty()) {
				mo = Integer.parseInt(menuOrder.getText());
			} else {
				mo = -1;
			}
			
		} catch (NumberFormatException ex) {
			Window.alert("Menu Order must be an integer.");
			return;
		}
 		content.setMenuOrder(mo);
 		content.setShowInFooter(showInFooter.getValue());
 		content.setShowInMenu(showInMenu.getValue());
		listener.saveContent(content);
	}
	
	
	@UiHandler("cancel")
	void onClickCacnel(ClickEvent e) {
		listener.cancelEditContent();
	}


	public void setContent(IContent content, EditContentPresenter listener) {
		this.content = content;
		this.listener = listener;
		title.setText(content.getTitle());
		body.setText(content.getBody());
		active.setValue(content.isActive());
		div.setText(content.getDiv());
		if (content.getMenuOrder() != null) {
			menuOrder.setText(content.getMenuOrder().toString());
		}
		showInMenu.setValue(content.isShowInMenu());
		showInFooter.setValue(content.isShowInFooter());
		this.setText(content.getId().toString());
	}

	public void setPresenter(EditContentPresenter p) {
		listener = p;
	}

}
