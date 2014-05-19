/**
 * 
 */
package net.rugby.foundation.topten.client.ui.toptenlistview;

import net.rugby.foundation.topten.model.shared.ITopTenList;

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
public class EditTTLInfo extends DialogBox {

	private static EditTTLInfoUiBinder uiBinder = GWT
			.create(EditTTLInfoUiBinder.class);

	interface EditTTLInfoUiBinder extends UiBinder<Widget, EditTTLInfo> {
	}

	public interface EditTTLInfoPresenter {
		void saveTTLInfo(ITopTenList ttl);
		void cancelEditTTLInfo(ITopTenList ttl);
	} 
	
	public EditTTLInfo() {
		setWidget(uiBinder.createAndBindUi(this));
	}

	@UiField Button save;
	@UiField Button cancel;
	@UiField TextArea description;
	@UiField TextBox title;
	
	private EditTTLInfoPresenter listener;
	private ITopTenList list;
	
	@UiHandler("save")
	void onClickSave(ClickEvent e) {
		list.setTitle(title.getText());
		list.setContent(description.getText());
		listener.saveTTLInfo(list);
	}
	
	
	@UiHandler("cancel")
	void onClickCacnel(ClickEvent e) {
		listener.cancelEditTTLInfo(list);
	}


	public void showTTL(ITopTenList list) {
		this.list = list;
		title.setText(list.getTitle());
		description.setText(list.getContent());
		this.setWidth("800px");
		show();
	}

	public void setPresenter(EditTTLInfoPresenter p) {
		listener = p;
	}

}
