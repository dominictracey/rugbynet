package net.rugby.foundation.topten.client.ui.toptenlistview;

import net.rugby.foundation.topten.model.shared.ITopTenItem;

import com.github.gwtbootstrap.client.ui.NavWidget;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.core.client.GWT;
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
	@UiField NavWidget buttonBar;


	private ITopTenItem item;


	private int index;
	
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

	public TopTenItemView(ITopTenItem item, int index) {
		initWidget(uiBinder.createAndBindUi(this));
		setItem(item, index);
	}

	public void setItem(ITopTenItem item, int index) {
		this.item = item;
		this.setIndex(index);
		if (item instanceof ITopTenItem) {
			if (item != null) {
				if (((ITopTenItem)item).getPlayer() != null) {
					name.setText(item.getPlayer().getDisplayName() + "(" +  item.getTeamName() + ")");
				}
				text.setText(item.getText());
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
}
