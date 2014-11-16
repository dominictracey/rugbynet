package net.rugby.foundation.topten.client.ui.toptenlistview;

import java.util.HashMap;
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
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;

import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabContent;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class SeriesListViewImpl extends Composite implements SeriesListView<IRatingSeries>
{
	private static SeriesListViewImplUiBinder uiBinder = GWT.create(SeriesListViewImplUiBinder.class);

	@UiField DropDownMenu criteriaDropDown;
	@UiField DropDownMenu weekDropDown;
	@UiField TabPanel tabPanel;
	@UiField NavTabs navTabs;
	@UiField TabContent tabContent;
	@UiField VerticalPanel notes;
	@UiField HTML listTitle;

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
	private Map<Integer, TabListItem> tabIndexMap = new HashMap<Integer, TabListItem>();

	private boolean ratingModesSet;

	private NoteView<INote> noteView;


	@UiTemplate("SeriesListViewImpl.ui.xml")

	interface SeriesListViewImplUiBinder extends UiBinder<Widget, SeriesListViewImpl>
	{
	}


	public SeriesListViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
		ratingModesSet = false;
		//notes = DOM.getElementById("notes");
	}



	@Override
	public void setSeries(final IRatingSeries result, final String baseUrl) {
		series = result;

		if (result != null) {
			seriesId = series.getId();
			tabPanel.setVisible(true);
			//clientFactory.getHeaderView().setHeroListInfo(result.getTitle(),result.getContent() + "<div id=\"fbListLike\"/>");

			tabPanel.clear();
			weekDropDown.clear();
			for (IRatingGroup rg : series.getRatingGroups()) {
				// add item to date dropdown
				final AnchorListItem nl = new AnchorListItem(rg.getLabel());
				final Long gid = rg.getId();
				//nl.setId(rg.getId().toString());
				//nl.setTarget(rg.getId().toString());
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
						presenter.gotoPlace(getPlace());
					}

				});
				weekDropDown.add(nl);
				//group = rg;
			}


			//			if (series.getMode() == RatingMode.BY_POSITION) {
			//				// start with the latest ratingGroup
			//				IRatingGroup group = series.getRatingGroups().get(series.getRatingGroups().size()-1);
			//				assert (group != null);
			//
			//				// default to best of year
			//				IRatingMatrix matrix = null;
			//				for (IRatingMatrix m : group.getRatingMatrices()) {
			//					if (m.getCriteria() == Criteria.BEST_YEAR) {
			//						matrix = m;
			//					}
			//				}
			//				assert (matrix != null);
			//
			//				for (IRatingQuery query : matrix.getRatingQueries()) {	
			//					final IRatingQuery rq = query;
			//					position fp = query.getPositions().get(0);
			//					final TabContent t = new TabContent();
			//					t.setHeading(fp.getName());
			//					final SeriesListViewImpl _this = this;
			//					t.addClickHandler(new ClickHandler() {
			//
			//						@Override
			//						public void onClick(ClickEvent event) {
			//							//_this.setRatingQuery(series.getRatingGroups().get(0).getRatingMatrices().get(0).getRatingQueries().get(fp.ordinal()));
			//							t.add(_this.listView.asWidget());
			//							presenter.showRatingQuery(rq);
			//						}
			//
			//					});
			//					tabPanel.add(t);
			//				}
			//			}

			//			int count = 0;
			//			if (it != null) {
			//				itemList = new ArrayList<TopTenItemView>();
			//
			//				while (it.hasNext()) {
			//					final ITopTenItem item = it.next();
			//
			//					final int fCount = count++;
			//					Scheduler.get().scheduleDeferred(new ScheduledCommand() {    
			//						@Override
			//						public void execute() {
			//
			//							TopTenItemView itemView = new TopTenItemView(item, fCount, result.getId(), item.getPlayerId(), baseUrl);
			//							itemList.add(itemView);
			//							items.add(itemView);
			//							presenter.setTTIButtons(itemView);
			//						}
			//					});
			//				}
			//			}


			//setVisible(true);
		} else {
			// no info to display
			tabPanel.setVisible(false);
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
		ttlView = clientFactory.getListView();
	}

	//	private void populateCriteria() {
	//		ButtonGroup nl = new ButtonGroup("In Form");
	//		nl.addClickHandler(new ClickHandler() {
	//
	//			@Override
	//			public void onClick(ClickEvent event) {
	//				presenter.showCriteria(Criteria.IN_FORM);
	//
	//			}
	//
	//		});
	//		criteriaDropDown.add(nl);
	//
	//		nl = new ButtonGroup("Best in Last Year");
	//		nl.addClickHandler(new ClickHandler() {
	//
	//			@Override
	//			public void onClick(ClickEvent event) {
	//				presenter.showCriteria(Criteria.BEST_YEAR);
	//
	//			}
	//
	//		});
	//		criteriaDropDown.add(nl);
	//
	//		nl.setActive(true);
	//	}

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
			tabPanel.clear();
			tabIndexMap.clear();
			int count = 0;
			for (IRatingQuery query : matrix.getRatingQueries()) {	
				final IRatingQuery rq = query;
				if (rq.getTopTenListId() != null) {
//					position fp = null;
//					if (query.getPositions() != null && !query.getPositions().isEmpty()) {
//						fp = query.getPositions().get(0);
//					}
					final TabListItem t = new TabListItem(rq.getLabel());
					final int i = count;
					tabIndexMap.put(count++, t);

					final SeriesListViewImpl _this = this;
					t.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							//_this.setRatingQuery(series.getRatingGroups().get(0).getRatingMatrices().get(0).getRatingQueries().get(fp.ordinal()));
							//t.add(_this.listView.asWidget());

							_this.addListView(i);
							setQuery(null);
							queryId = rq.getId();
							setList(null);
							setItemId(null);
							presenter.gotoPlace(getPlace());
							//presenter.showRatingQuery(rq);
						}

					});

					tabPanel.add(t);

				}
			
			}
			presenter.process(getPlace());
		} else {
			matrixId = null;
		}

	}

	protected void addListView(int index) {
		tabIndexMap.get(index).add(listView.asWidget());
	}



	@Override	public IRatingQuery getQuery() {
		return query;
	}

	@Override	
	public void setQuery(IRatingQuery query) {
		this.query = query;
		if (query != null) {
			queryId = query.getId();
			int ordinal = 0;
			for (IRatingQuery q : matrix.getRatingQueries()) {
				if (q.getId().equals(query.getId())) {
					addListView(ordinal);
					//if (tabPanel.get > ordinal)
					tabIndexMap.get(ordinal).showTab();
				}
				if (q.getTopTenListId() != null) {
					ordinal++;
				}
			}
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
		}
	}

	@Override
	public void setRatingQueries(IRatingMatrix matrix, List<IRatingQuery> result) {
		assert (matrix == this.matrix);

		matrix.setRatingQueries(result);

		// now pick a rating query - first for now
		//setQuery(matrix.getRatingQueries().get(0));

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



	@Override
	public void setNotes(String n) {
//		SafeHtmlBuilder builder = new SafeHtmlBuilder();
//		builder.appendHtmlConstant(n);
//		notes.setHTML(builder.toSafeHtml());
		
	}



	@Override
	public void setNotesView(NoteView<INote> noteView) {
		this.noteView = noteView;
		notes.add(noteView.asWidget());
	}



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



}