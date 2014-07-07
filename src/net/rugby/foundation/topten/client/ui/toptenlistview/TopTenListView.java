package net.rugby.foundation.topten.client.ui.toptenlistview;

import java.util.List;

import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.ITopTenItem;

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
		void showRatingDetails(ITopTenItem value);
		void setFBListLike(ITopTenList list, String baseUrl);
	}
	void setPresenter(TopTenListViewPresenter presenter); 
	void setList(ITopTenList result, String baseUrl);
	void setClientFactory(ClientFactory clientFactory);
	List<TopTenItemView> getItemViews();
	ITopTenList getList();

	void hasNext(boolean has);
	void hasPrev(boolean has);

	int getItemCount();
	void setItemCount(int i);

	
}