package net.rugby.foundation.topten.client.ui.toptenlistview;

import java.util.List;
import java.util.Map;

import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.ITopTenItem;

import com.github.gwtbootstrap.client.ui.NavPills;
import com.github.gwtbootstrap.client.ui.NavWidget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget.
 *
 */
public interface TopTenListView<T extends ITopTenItem> extends IsWidget
{
	public interface TopTenListViewPresenter {
		void showNext();
		void showPrev();
		void setTTIButtons(TopTenItemView itemView);
		void parse(Widget widget);
	}
	void setPresenter(TopTenListViewPresenter presenter);
 
	void setList(ITopTenList result, String baseUrl);

	void setComps(Map<Long, String> competitionMap, List<Long> compsUnderway);

	void addLoginPanel(HorizontalPanel acct);

	NavPills getLoginPanel();

	List<TopTenItemView> getItemViews();

	ITopTenList getList();

	NavWidget getButtonBar();
	
	void hasNext(boolean has);
	void hasPrev(boolean has);

	void setClientFactory(ClientFactory clientFactory);

	int getItemCount();
	void setItemCount(int i);

	void setContent(List<IContent> result);

}