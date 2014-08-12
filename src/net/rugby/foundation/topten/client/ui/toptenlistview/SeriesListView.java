package net.rugby.foundation.topten.client.ui.toptenlistview;

import java.util.List;

import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.place.SeriesPlace;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.ITopTenItem;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget.
 *
 */
public interface SeriesListView<T extends IRatingSeries> extends IsWidget
{
	public interface SeriesListViewPresenter {
		void setButtons();
		void parse(Widget widget);
		void showRatingDetails(ITopTenItem value);
		void setFBListLike(ITopTenList list, String baseUrl);
		//void showCriteria(Criteria inForm);
		//void showRatingQuery(IRatingQuery rq);
		//void showRatingGroup(long gid);
		void gotoPlace(Place place);
	}
	
	void setSeries(IRatingSeries series, String baseUrl);
	/// sets the criteria dropdown
	//void setCriteria(Criteria crit);

	void setClientFactory(ClientFactory clientFactory);
	void setListView(TopTenListView<ITopTenItem> listView);
	IRatingSeries getSeries();
	void setPresenter(SeriesListViewPresenter presenter);
	Criteria getCriteria();
	void setCriteria(Criteria criteria);
	void setCompId(Long compId);
	IRatingQuery getQuery();
	void setQuery(IRatingQuery query);
	Long getQueryId();
	Long getPlayerId();
	void setPlayerId(Long playerId);
	Long getCompId();
	void setMatrix(IRatingMatrix matrix);
	IRatingMatrix getMatrix();
	void setGroup(IRatingGroup group);
	IRatingGroup getGroup();
	SeriesPlace getPlace();
	Long getSeriesId();
	void setSeriesId(Long seriesId);

	ITopTenList getList();

	void setList(ITopTenList list);

	void setRatingQueries(IRatingMatrix matrix, List<IRatingQuery> result);

	void copyPlace(SeriesPlace sPlace);

	
}