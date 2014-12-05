package net.rugby.foundation.result.client;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Span;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;


public class ResultPanel extends Composite 
{
	private static ResultPanelUiBinder uiBinder = GWT.create(ResultPanelUiBinder.class);

	Element header;

	@UiField HTMLPanel root;
	@UiField Panel scores;

	@UiField Anchor left_arrow;
	@UiField Anchor right_arrow;
	@UiField Row row;
	@UiField Column left;
	@UiField Column mid;
	@UiField Column right;
	
	@UiTemplate("ResultPanel.ui.xml")

	interface ResultPanelUiBinder extends UiBinder<Widget, ResultPanel>
	{
	}


	public ResultPanel()
	{
		initWidget(uiBinder.createAndBindUi(this));
		left_arrow.setIcon(IconType.CHEVRON_LEFT);
		left.addStyleName("col-xs-1");
		left.addStyleName("vertical-center");
		left.addStyleName("pull-left");
		right_arrow.setIcon(IconType.CHEVRON_RIGHT);
		right.addStyleName("col-xs-1");
		mid.addStyleName("col-xs-10");
		right.addStyleName("vertical-center");
		right.addStyleName("pull-right");
		RootPanel resultPanel = RootPanel.get("resultPanel");
		resultPanel.addStyleName("no-padding");
		root.addStyleName("no-padding");
		resultPanel.add(this);
		header = DOM.getElementById("resultHeader");
	}


	public Element getHeader() {
		return header;
	}



//	public Panel getScores() {
//		return scores;
//	}
//
//
//	public void setScores(Panel scores) {
//		this.scores = scores;
//	}
//
//
	public Anchor getLeft_arrow() {
		return left_arrow;
	}
//
//
//	public void setLeft_arrow(Anchor left_arrow) {
//		this.left_arrow = left_arrow;
//	}
//
//
	public Anchor getRight_arrow() {
		return right_arrow;
	}
//
//
//	public void setRight_arrow(Anchor right_arrow) {
//		this.right_arrow = right_arrow;
//	}

}