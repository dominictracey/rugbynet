package net.rugby.foundation.result.client;

import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelHeader;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;


public class MatchScore extends Composite 
{
	private static MatchScoreUiBinder uiBinder = GWT.create(MatchScoreUiBinder.class);


	@UiField PanelHeader header;
	@UiField PanelBody body;
	@UiField Panel scorePanel;

	@UiTemplate("MatchScore.ui.xml")

	interface MatchScoreUiBinder extends UiBinder<Widget, MatchScore>
	{
	}


	public MatchScore()
	{
		initWidget(uiBinder.createAndBindUi(this));
		scorePanel.addStyleName("no-padding");
	}


	public PanelHeader getHeader() {
		return header;
	}


	public void setHeader(PanelHeader header) {
		this.header = header;
	}


	public PanelBody getBody() {
		return body;
	}


	public void setBody(PanelBody body) {
		this.body = body;
	}

}