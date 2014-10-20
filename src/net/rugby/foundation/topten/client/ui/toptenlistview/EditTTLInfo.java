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
import com.google.gwt.user.client.Window;
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
	@UiField TextBox twitter;
	
	private EditTTLInfoPresenter listener;
	private ITopTenList list;
	
	@UiHandler("save")
	void onClickSave(ClickEvent e) {
		if (twitter.getText().length() < 50)  {
			list.setTitle(title.getText());
			list.setContent(description.getText());
			list.setTwitterDescription(twitter.getText());
			listener.saveTTLInfo(list);
		} else {
			Window.alert("Twitter description must be less than 50 chars. Current length is " + twitter.getText().length() + ".");
		}
	}
	
	
	@UiHandler("cancel")
	void onClickCacnel(ClickEvent e) {
		listener.cancelEditTTLInfo(list);
	}


	public void showTTL(ITopTenList list) {
		this.list = list;
		title.setText(list.getTitle());
		description.setText(list.getContent());
		twitter.setText(list.getTwitterDescription());
		this.setWidth("800px");
		show();
	}

	public void setPresenter(EditTTLInfoPresenter p) {
		listener = p;
	}

}
