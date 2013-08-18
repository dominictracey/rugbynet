package net.rugby.foundation.topten.client.ui.toptenlistview;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.topten.model.shared.ITopTenItem;

import com.github.gwtbootstrap.client.ui.NavWidget;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;


public class TopTenItemView extends Composite
{
	private static TopTenItemViewUiBinder uiBinder = GWT.create(TopTenItemViewUiBinder.class);


	@UiField HTML name;
	@UiField Paragraph text;
	@UiField HTML fblike;
	@UiField NavWidget buttonBar;


	private ITopTenItem item;


	private int index;


	private Long listId;

	@UiTemplate("TopTenItemView.ui.xml")

	interface TopTenItemViewUiBinder extends UiBinder<Widget, TopTenItemView>
	{
	}


	public TopTenItemView()
	{
		initWidget(uiBinder.createAndBindUi(this));
		//playersTable.getRowFormatter().addStyleName(0, "groupListHeader");
		//			playersTable.addStyleName("groupList");
		//playersTable.getCellFormatter().addStyleName(0, 1, "groupListNumericColumn");

	}

	public TopTenItemView(ITopTenItem item, int index, Long listId, Long playerId, String baseUrl) {
		initWidget(uiBinder.createAndBindUi(this));
		this.listId = listId;
		setItem(item, index, playerId, baseUrl);
	}

	public void setItem(ITopTenItem item, int index, Long playerId, String baseUrl) {
		this.item = item;
		this.setIndex(index);
		if (item instanceof ITopTenItem) {
			if (item != null) {
				if (((ITopTenItem)item).getPlayer() != null) {
					name.setText(index+1 + ". " + item.getPlayer().getDisplayName() + " (" +  item.getTeamName() + ")");
				}
				text.setText(item.getText());

				Element e = fblike.getElement().getFirstChildElement();
				if (e != null) {
					e.setAttribute("id", "fbPlayerLike-"+index);
					//Logger.getLogger("ItemView").log(Level.WARNING,"<div class=\"fb-like\" id=\"fbPlayerLike-" + (index+1) + " data-width=\"450\" data-layout=\"button_count\" data-show-faces=\"false\" data-send=\"false\" data-href=\"http://dev.rugby.net/topten.html#List:listId=" + listId + "&playerId=" + playerId +"></div>");
					e.setAttribute("data-href", baseUrl +"#List:listId=" + listId + "&playerId=" + playerId);
				}
			}
		}
	}

	public NavWidget getButtonBar() {
		return buttonBar;
	}

	public ITopTenItem getItem() {
		return item;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Long getListId() {
		return listId;
	}

	public void setListId(Long long1) {
		this.listId = long1;
	}
}
