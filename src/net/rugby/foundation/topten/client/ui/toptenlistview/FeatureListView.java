package net.rugby.foundation.topten.client.ui.toptenlistview;

import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget.
 *
 */
public interface FeatureListView<T extends ITopTenList> extends IsWidget
{
	public interface FeatureListViewPresenter {
		void showNext();
		void showPrev();
		void publish(ITopTenList list);
		void unpublish(ITopTenList list);
		void promote(ITopTenList list);
		void edit(ITopTenList list);
		void delete(ITopTenList list);
	}

	void setList(T result, String baseUrl);
	void setClientFactory(ClientFactory clientFactory);

	ITopTenList getList();
	void showContent(boolean show);

	void hasNext(boolean has);
	void hasPrev(boolean has);

	void setPresenter(FeatureListViewPresenter presenter);
	
}