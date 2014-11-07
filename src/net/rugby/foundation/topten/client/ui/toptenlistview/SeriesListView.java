package net.rugby.foundation.topten.client.ui.toptenlistview;

import java.util.List;
import java.util.Map;

import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.place.SeriesPlace;
import net.rugby.foundation.topten.client.ui.notes.NoteView;
import net.rugby.foundation.topten.model.shared.INote;
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
		void gotoPlace(Place place);
		void switchMode(Long compId, RatingMode _mode);
		void process(SeriesPlace place);
	}
	
	void setSeries(IRatingSeries series, String baseUrl);
	void setClientFactory(ClientFactory clientFactory);
	void setListView(TopTenListView<ITopTenItem> listView);
	IRatingSeries getSeries();
	void setPresenter(SeriesListViewPresenter presenter);
	RatingMode getMode();
	void setMode(RatingMode mode);
	void setCompId(Long compId);
	IRatingQuery getQuery();
	void setQuery(IRatingQuery query);
	Long getQueryId();
	Long getItemId();
	void setItemId(Long itemId);
	Long getCompId();
	void setMatrix(IRatingMatrix matrix);
	IRatingMatrix getMatrix();
	void setGroup(IRatingGroup group, boolean flush);
	IRatingGroup getGroup();
	SeriesPlace getPlace();
	Long getSeriesId();
	void setSeriesId(Long seriesId);

	ITopTenList getList();

	void setList(ITopTenList list);

	void setRatingQueries(IRatingMatrix matrix, List<IRatingQuery> result);

	void copyPlace(SeriesPlace sPlace);

	void setAvailableModes(Map<RatingMode, Long> modeMap);

	boolean isRatingModesSet();

	void setNotes(String notes);
	void setNotesView(NoteView<INote> noteView);
	
	/**
	 * Clear all UI elements and backing data members to prepare for a link jump
	 */
	void reset();
	void prepareForHere(SeriesPlace sPlace);
	
}