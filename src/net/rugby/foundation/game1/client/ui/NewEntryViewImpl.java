package net.rugby.foundation.game1.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Sample implementation of {@link NewEntryView}.
 */
public class NewEntryViewImpl extends PopupPanel implements NewEntryView {

	interface Binder extends UiBinder<Widget, NewEntryViewImpl> {
	}
	
	private static final Binder binder = GWT.create(Binder.class);

	private Presenter listener;
	
	@UiField
	Label info;
	@UiField 
	TextBox entryName;
	@UiField
	Button create;
	@UiField
	Button cancel;

	protected String userName;
	protected String compName;
	private int count = 1;
	
	public NewEntryViewImpl() {
		setWidget(binder.createAndBindUi(this));
		setTitle("Create Entry");
	}


	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}

	@UiHandler("create")
	void onCreateClick(ClickEvent event) {
		listener.onCreate(entryName.getText());
	}

	@UiHandler("cancel")
	void onCancelClick(ClickEvent event) {
		listener.onCancel();
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.NewEntryView#setUserName(java.lang.String)
	 */
	@Override
	public void setUserName(String userName) {
		this.userName = userName;
		entryName.setText(userName +  "-" + compName + "-" + count);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.NewEntryView#setCompName(java.lang.String)
	 */
	@Override
	public void setCompName(String compName) {
		this.compName = compName;
	}
	
	@Override
	public void setCount(int count) {
		this.count = count;
		entryName.setText(userName +  "-" + compName + "-" + count);
	}
}
