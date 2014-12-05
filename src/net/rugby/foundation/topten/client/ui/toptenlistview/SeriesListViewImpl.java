package net.rugby.foundation.topten.client.ui.toptenlistview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.place.SeriesPlace;
import net.rugby.foundation.topten.client.ui.notes.NoteView;
import net.rugby.foundation.topten.model.shared.INote;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;

import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.TabContent;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.TabPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;


public class SeriesListViewImpl extends Composite implements SeriesListView<IRatingSeries>
{
	private static SeriesListViewImplUiBinder uiBinder = GWT.create(SeriesListViewImplUiBinder.class);

	@UiField DropDownMenu criteriaDropDown;
	@UiField DropDownMenu weekDropDown;
	@UiField TabPanel tabPanel;
	@UiField NavTabs navTabs;
	@UiField TabContent tabContent;
	@UiField HTML listTitle;
	
	// admin buttons
	@UiField Panel adminButtons;
	@UiField Button createFeature;
	@UiField Button promote;

	private Long compId;
	private Long seriesId;
	private Long groupId;
	private Long matrixId;
	private Long queryId;
	private IRatingSeries series;
	private IRatingGroup group;
	private IRatingMatrix matrix;
	private IRatingQuery query;
	private ITopTenList list;
	private Long itemId;

	
	private SeriesListViewPresenter presenter;
	private ClientFactory clientFactory;

	protected TopTenListView<ITopTenItem> ttlView;

	private TopTenListView<ITopTenItem> listView;

	private RatingMode mode;
	private Map<Long, TabListItem> tabIndexMap = new HashMap<Long, TabListItem>();
	private Map<Long, TabPane> paneIndexMap = new HashMap<Long, TabPane>();
	
	private boolean ratingModesSet;


	@UiTemplate("SeriesListViewImpl.ui.xml")

	interface SeriesListViewImplUiBinder extends UiBinder<Widget, SeriesListViewImpl>
	{
	}


	public SeriesListViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
		ratingModesSet = false;
	}



	@Override
	public void setSeries(final IRatingSeries result, final String baseUrl) {
		series = result;

		if (result != null) {
			seriesId = series.getId();
			navTabs.setVisible(true);
			tabContent.setVisible(true);
			
			navTabs.clear();
			weekDropDown.clear();
			for (IRatingGroup rg : series.getRatingGroups()) {
				// add item to date dropdown
				final AnchorListItem nl = new AnchorListItem(rg.getLabel());
				final Long gid = rg.getId();
				nl.setText(rg.getLabel());
				nl.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						//presenter.showRatingGroup(Long.parseLong(nl.getTarget()));

						// flush everything below the new group we want.
						group = null;
						groupId = gid;
						setMatrix(null);
						setQuery(null);
						setList(null);
						setItemId(null);
						//Core.getCore().setCurrentRoundId(rid);
						presenter.gotoPlace(getPlace());
					}

				});
				weekDropDown.add(nl);
			}
		} else {
			// no info to display
			navTabs.setVisible(false);
			tabContent.setVisible(false);
		}
		presenter.process(getPlace());

	}

	@Override
	public IRatingSeries getSeries() {
		return series;
	}

	@Override
	public void setPresenter(SeriesListViewPresenter presenter) {
		this.presenter = presenter;
	}


	@Override
	public void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		ttlView = clientFactory.getSimpleView();  // changed from getListView() on 11/20/14
	}

	@Override
	public void setListView(TopTenListView<ITopTenItem> listView) {
		this.listView = listView;
	}

	@Override
	public IRatingGroup getGroup() {
		return group;
	}

	@Override	public void setGroup(IRatingGroup group, boolean flush) {
		this.group = group;
		if (group != null) {
			groupId = group.getId();
			assert (group.getRatingSeriesId().equals(seriesId));
		}
		
		if (flush) {
			setMatrix(null);
			setQuery(null);
			setItemId(null);
		}
//		} else {	
			presenter.process(getPlace());
//		}
	}

	@Override
	public void setAvailableModes(final Map<RatingMode, Long> modeMap) {
		criteriaDropDown.clear();
		// populate Rate by drop down
		for (RatingMode mode : modeMap.keySet()) {
			//final RatingMode _mode = mode;
			final AnchorListItem nl = new AnchorListItem(mode.name());
			final Long _seriesId = modeMap.get(mode);
			nl.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					// go to the new series
					series = null;
					seriesId = _seriesId;
					group = null;
					groupId = null;
					setMatrix(null);
					setQuery(null);
					setList(null);
					setItemId(null);
					//presenter.switchMode(compId, _mode);
					presenter.gotoPlace(getPlace());
				}

			});
			criteriaDropDown.add(nl);
		}
		ratingModesSet = true;
		presenter.process(getPlace());
	}
	
	@Override	public IRatingMatrix getMatrix() {
		return matrix;
	}

	@Override	public void setMatrix(IRatingMatrix matrix) {
		this.matrix = matrix;
		if (matrix != null) {
			matrixId = matrix.getId();
			navTabs.clear();
			tabContent.clear();
			tabIndexMap.clear();
			paneIndexMap.clear();
			for (IRatingQuery query : matrix.getRatingQueries()) {	
				final IRatingQuery rq = query;
				if (rq.getTopTenListId() != null) {

					final TabListItem t = new TabListItem(rq.getLabel());
					
					// the paired TabPane to show when the tab is clicked
					final TabPane pane = new TabPane();
					tabIndexMap.put(rq.getId(), t);
					paneIndexMap.put(rq.getId(), pane);
					tabContent.add(pane);

					t.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {

							pane.add(listView);
							setQuery(null);
							queryId = rq.getId();
							setList(null);
							setItemId(null);
							presenter.gotoPlace(getPlace());
						}

					});


					
					t.setDataTargetWidget(pane);
					navTabs.add(t);
					


				}
			
			}
			presenter.process(getPlace());
		} else {
			matrixId = null;
		}

	}



	@Override	public IRatingQuery getQuery() {
		return query;
	}

	@Override	
	public void setQuery(IRatingQuery query) {
		this.query = query;
		if (query != null) {
			queryId = query.getId();
			
			tabIndexMap.get(query.getId()).showTab();
			paneIndexMap.get(query.getId()).add(listView);

			presenter.process(getPlace());
		} else {
			queryId = null;
		}
	}

	@Override
	public Long getItemId() {
		return itemId;
	}

	@Override
	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}


	@Override
	public Long getCompId() {
		return compId;
	}

	@Override
	public void setCompId(Long compId) {
		if (compId != this.compId) {
			this.compId = compId;
			ratingModesSet = false;
			// @REX clear everything else?
			presenter.process(getPlace());
		}
	}

	@Override
	public void setMode(RatingMode mode) {
		this.mode = mode;
	}

	@Override
	public RatingMode getMode() {
		return mode;
	}

	@Override
	public SeriesPlace getPlace() {
		return new SeriesPlace(compId, seriesId, groupId, matrixId, queryId, itemId);
	}

	@Override
	public void setSeriesId(Long seriesId) {
		this.seriesId = seriesId;
	}

	@Override
	public Long getSeriesId() {
		return seriesId;
	}

	@Override
	public Long getQueryId() {
		return queryId;
	}

	@Override
	public ITopTenList getList() {
		return list;
	}

	@Override
	public void setList(ITopTenList list) {
		this.list = list;
		if (list != null) {
			// have to bump up and look at the matrix's query list to pick the right one.
			for (IRatingQuery rq : matrix.getRatingQueries()) {
				if (rq.getId().equals(queryId)) {
					setQuery(rq);
				}
			}
			listTitle.setHTML(list.getTitle());
			listView.setList(list, "");
		}
	}

	@Override
	public void setRatingQueries(IRatingMatrix matrix, List<IRatingQuery> result) {
		assert (matrix == this.matrix);

		matrix.setRatingQueries(result);

		setMatrix(matrix);

	}



	@Override
	public void copyPlace(SeriesPlace sPlace) {
		compId = sPlace.getCompId();
		seriesId = sPlace.getSeriesId();
		groupId = sPlace.getGroupId();
		matrixId = sPlace.getMatrixId();
		queryId = sPlace.getQueryId();
		itemId = sPlace.getItemId();
	}

	@Override
	public boolean isRatingModesSet() {
		return ratingModesSet;
	}



//	@Override
//	public void setNotes(String n) {
////		SafeHtmlBuilder builder = new SafeHtmlBuilder();
////		builder.appendHtmlConstant(n);
////		notes.setHTML(builder.toSafeHtml());
//		
//	}
//
//
//
//	@Override
//	public void setNotesView(NoteView<INote> noteView) {
//		this.noteView = noteView;
//		notes.add(noteView.asWidget());
//	}



	@Override
	public void reset() {
		series = null;
		seriesId = null;
		group = null;
		groupId = null;
		setMatrix(null);
		setQuery(null);
		setList(null);
		setItemId(null);
		this.ratingModesSet = false;
	}



	@Override
	public void prepareForHere(SeriesPlace sPlace) {
		// so we want to flush anything that doesn't align with the new place
		if (sPlace.getSeriesId() == null || !sPlace.getSeriesId().equals(seriesId)) {
			series = null;
			seriesId = sPlace.getSeriesId();
		} 
		
		if (sPlace.getGroupId() == null || !sPlace.getGroupId().equals(groupId)) {
			group = null;
			groupId = sPlace.getGroupId();
		}

		if (sPlace.getMatrixId() == null || !sPlace.getMatrixId().equals(matrixId)) {
			matrix = null;
			matrixId = sPlace.getMatrixId();
		}
		
		if (sPlace.getQueryId() == null || !sPlace.getQueryId().equals(queryId == null)) {
			query = null;
			queryId = sPlace.getQueryId();
			list = null;
			itemId = null;
		}
		
	}

	@Override
	public void showButtons(boolean show) {
		adminButtons.setVisible(show);	
	}

	@UiHandler("createFeature")
	void onNextButtonClicked(ClickEvent event) {	
		presenter.createFeature(getPlace());
	}

}