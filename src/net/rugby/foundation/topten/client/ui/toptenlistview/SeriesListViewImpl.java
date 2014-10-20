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
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.SplitDropdownButton;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;


public class SeriesListViewImpl extends Composite implements SeriesListView<IRatingSeries>
{
	private static SeriesListViewImplUiBinder uiBinder = GWT.create(SeriesListViewImplUiBinder.class);

	@UiField SplitDropdownButton criteriaDropDown;
	@UiField SplitDropdownButton weekDropDown;
	@UiField TabPanel tabPanel;
	@UiField HTML notes;
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
	private Long playerId;

	private SeriesListViewPresenter presenter;
	private ClientFactory clientFactory;

	protected TopTenListView<ITopTenItem> ttlView;

	private TopTenListView<ITopTenItem> listView;

	private RatingMode mode;
	private Map<Integer, Tab> tabIndexMap = new HashMap<Integer, Tab>();

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
			tabPanel.setVisible(true);
			//clientFactory.getHeaderView().setHeroListInfo(result.getTitle(),result.getContent() + "<div id=\"fbListLike\"/>");

			tabPanel.clear();
			weekDropDown.clear();
			for (IRatingGroup rg : series.getRatingGroups()) {
				// add item to date dropdown
				final NavLink nl = new NavLink(rg.getLabel());
				nl.setTarget(rg.getId().toString());
				nl.setText(rg.getLabel());
				nl.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						//presenter.showRatingGroup(Long.parseLong(nl.getTarget()));

						// flush everything below the new group we want.
						group = null;
						groupId = Long.parseLong(nl.getTarget());
						setMatrix(null);
						setQuery(null);
						setList(null);
						setPlayerId(null);
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
			//					final Tab t = new Tab();
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
		presenter.gotoPlace(getPlace());

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
	//		NavLink nl = new NavLink("In Form");
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
	//		nl = new NavLink("Best in Last Year");
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

	@Override	public void setGroup(IRatingGroup group) {
		this.group = group;
		if (group != null) {
			groupId = group.getId();
			assert (group.getRatingSeriesId().equals(seriesId));
		}
		
		setMatrix(null);
		setQuery(null);
		setPlayerId(null);
		presenter.gotoPlace(getPlace());
	}

	@Override
	public void setAvailableModes(final List<RatingMode> modeMap) {
		criteriaDropDown.clear();
		// populate Rate by drop down
		for (RatingMode mode : modeMap) {
			final RatingMode _mode = mode;
			final NavLink nl = new NavLink(mode.name());
			
			nl.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					// go to the new series
					series = null;
					seriesId = null;
					group = null;
					groupId = null;
					setMatrix(null);
					setQuery(null);
					setList(null);
					setPlayerId(null);
					presenter.switchMode(compId, _mode);
				}

			});
			criteriaDropDown.add(nl);
		}
		ratingModesSet = true;
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
					final Tab t = new Tab();
					final int i = count;
					tabIndexMap.put(count++, t);

					t.setHeading(rq.getLabel());

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
							setPlayerId(null);
							presenter.gotoPlace(getPlace());
							//presenter.showRatingQuery(rq);
						}

					});

					tabPanel.add(t);

				}
			
			}
			presenter.gotoPlace(getPlace());
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
					tabPanel.selectTab(ordinal);
				}
				if (q.getTopTenListId() != null) {
					ordinal++;
				}
			}
			presenter.gotoPlace(getPlace());
		} else {
			queryId = null;
		}
	}

	@Override
	public Long getPlayerId() {
		return playerId;
	}

	@Override
	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
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
		return new SeriesPlace(compId, seriesId, groupId, matrixId, queryId, playerId);
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
		playerId = sPlace.getPlayerId();
	}

	@Override
	public boolean isRatingModesSet() {
		return ratingModesSet;
	}



}