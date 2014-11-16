package net.rugby.foundation.topten.client.ui.content;

import net.rugby.foundation.model.shared.IContent;

import org.gwtbootstrap3.client.ui.ButtonGroup;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;


public class ContentView extends Composite
{
	private static ContentViewUiBinder uiBinder = GWT.create(ContentViewUiBinder.class);

	@UiField HTML content;
	@UiField ButtonGroup buttonBar;
	
	@UiTemplate("ContentView.ui.xml")

	interface ContentViewUiBinder extends UiBinder<Widget, ContentView>
	{
	}


	public ContentView()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setContent(IContent item) {
		if (item != null) {
			content.setHTML(item.getBody());
		} else {
			content.setHTML("<div class=\"alert\">  <button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>  <strong>Error!</strong> Content not found.</div>");
		}
		
	}
	
	public ButtonGroup getButtonBar() {
		return buttonBar;
	}

}
