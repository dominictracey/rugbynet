package net.rugby.foundation.topten.client.ui.toptenlistview;

import net.rugby.foundation.topten.model.shared.ITopTenItem;

import com.github.gwtbootstrap.client.ui.NavWidget;
import com.github.gwtbootstrap.client.ui.Row;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;


public class TopTenItemView extends Composite
{
	private static TopTenItemViewUiBinder uiBinder = GWT.create(TopTenItemViewUiBinder.class);

	@UiField HTML image;
	@UiField HTML name;
	@UiField HTML text;
	@UiField HTMLPanel fblike;
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
				if (item.getTeamId() != null) {
					image.setHTML( "<image src=\"/resources/" + item.getTeamId() + "/200.png\" width=\"75\"/>");
				}
				
				if (((ITopTenItem)item).getPlayer() != null) {
					String pos = "";
					if (item.getPosition() != null) {
						pos = " (" +  item.getPosition().getName() + ")";
					}
					name.setText(index+1 + ". " + item.getPlayer().getDisplayName() + pos);
				}

				//HTML html = new HTML(item.getText());
				text.setHTML(item.getText());

				//Element e = fblike.getElement().getFirstChildElement();
				fblike.clear();
				String encodedUrl= URL.encode(baseUrl + "?listId=" + listId + "&playerId=" + playerId +"#List:listId=" + listId + "&playerId=" + playerId);
				fblike.add(new HTML("<div class=\"fb-like\" id=\"fbPlayerLike" + index + "\" data-width=\"450\" data-layout=\"button_count\" data-show-faces=\"false\" data-send=\"false\" data-href=\"" + encodedUrl + "\"></div>"));
				//				if (e != null) {
//					e.setAttribute("id", "fbPlayerLike-"+index);
//					//Logger.getLogger("ItemView").log(Level.WARNING,"<div class=\"fb-like\" id=\"fbPlayerLike-" + (index+1) + " data-width=\"450\" data-layout=\"button_count\" data-show-faces=\"false\" data-send=\"false\" data-href=\"http://dev.rugby.net/topten.html#List:listId=" + listId + "&playerId=" + playerId +"></div>");
//					e.setAttribute("data-href", baseUrl +"#List:listId=" + listId + "&playerId=" + playerId);
//				}
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
