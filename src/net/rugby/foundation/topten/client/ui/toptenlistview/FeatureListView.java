package net.rugby.foundation.topten.client.ui.toptenlistview;

import org.gwtbootstrap3.client.ui.Anchor;

import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

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

	void setList(T result);
	void setClientFactory(ClientFactory clientFactory);

	ITopTenList getList();
	void showContent(boolean show);

	void hasNext(boolean has);
	void hasPrev(boolean has);

	void setPresenter(FeatureListViewPresenter presenter);
	void editList(ITopTenList list);
	
	void showEditorButtons(boolean show);
	void showContributorButtons(boolean show);
	void expandView(boolean expand);
	Anchor getNextLabel();
	Anchor getPrevLabel();
}