package net.rugby.foundation.topten.client.ui.content;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.topten.client.ui.content.EditContent.ContentPresenter;

import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelFooter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;


public class ContentView extends Composite
{
	private static ContentViewUiBinder uiBinder = GWT.create(ContentViewUiBinder.class);

	@UiField Panel panel;
	@UiField HTML header;
	@UiField PanelBody contentBody;
	@UiField HTML content;
	@UiField PanelFooter panelFooter;
	@UiField ButtonGroup buttonBar;

	private ContentPresenter listener;

	private IContent item;
	
	@UiTemplate("ContentView.ui.xml")

	interface ContentViewUiBinder extends UiBinder<Widget, ContentView>
	{
	}


	public ContentView()
	{
		initWidget(uiBinder.createAndBindUi(this));
		panelFooter.setVisible(false);
	}

	public void setContent(IContent item) {
		this.item = item;
		if (item != null) {
			header.setHTML("<strong>" + item.getTitle() + "</strong>");
			content.setHTML(item.getBody());
		} else {
			content.setHTML("<div class=\"alert\">  <button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>  <strong>Error!</strong> Content not found.</div>");
		}
		
	}
	
	public ButtonGroup getButtonBar() {
		return buttonBar;
	}
	
	@UiHandler("editButton")
	void onClickCacnel(ClickEvent e) {
		listener.editContent(item);
	}
	
	public void setPresenter(ContentPresenter listener) {
		this.listener= listener;
	}
	
	public void showFooter(boolean show) {
		panelFooter.setVisible(show);
	}

}
