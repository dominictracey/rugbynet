package net.rugby.foundation.topten.client.ui.toptenlistview;

import java.util.List;
import java.util.Map;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.ISponsor;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.place.SeriesPlace;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;

import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.PanelFooter;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.html.Div;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;


public class SeriesListViewImpl extends Composite implements SeriesListView<IRatingSeries>
{
	protected static SeriesListViewImplUiBinder uiBinder = GWT.create(SeriesListViewImplUiBinder.class);

	@UiField
	protected Row sponsorRow;
	
	@UiField 
	protected Column compCol;
	@UiField 
	protected Column dropdownCol;
	@UiField 
	protected Column sponsorCol;
	
	@UiField 
	protected ButtonGroup matrixGroup;
	@UiField 
	protected ButtonGroup criteriaGroup;
	@UiField 
	protected ButtonGroup weekGroup;
	@UiField 
	protected Button matrixButton;
	@UiField 
	protected Button matrixToggle;
	@UiField 
	protected Button criteriaButton;
	@UiField 
	protected Button weekButton;	
	@UiField 
	protected DropDownMenu matrixDropDown;
	@UiField 
	protected DropDownMenu criteriaDropDown;
	@UiField 
	protected DropDownMenu weekDropDown;

	@UiField 
	protected Div content;

	@UiField 
	protected PanelHeader seriesHeader;
	
	@UiField
	protected Div compSpacer;
	@UiField 
	Div sponsorDiv;
	@UiField
	protected Div sponsorSpacer;
	@UiField
	protected HTML sponsorTag;
	
	@UiField 
	protected HTML listTitle;

	// admin buttons
	@UiField 
	protected PanelFooter adminButtons;
	@UiField 
	protected Button createFeature;
	@UiField 
	protected Button promote;

	protected Long compId;
	protected Long seriesId;
	protected Long groupId;
	protected Long matrixId;
	protected Long queryId;
	protected IRatingSeries series;
	protected IRatingGroup group;
	protected IRatingMatrix matrix;
	protected IRatingQuery query;
	protected ITopTenList list;
	protected Long itemId;


	protected SeriesListViewPresenter presenter;
	protected ClientFactory clientFactory;

	protected TopTenListView<ITopTenItem> listView;

	protected RatingMode mode;


	protected boolean ratingModesSet;

	@UiTemplate("SeriesListViewImpl.ui.xml")

	interface SeriesListViewImplUiBinder extends UiBinder<Widget, SeriesListViewImpl>
	{
	}


	public SeriesListViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
		showButtons(false);
		ratingModesSet = false;

		criteriaGroup.setVisible(false);
		seriesHeader.setPaddingBottom(5);
		
		sponsorTag.addStyleName("font-size:.5em");	
		sponsorRow.addStyleName("sponsor");
		
		compCol.setStylePrimaryName("comp-logo");
		compCol.addStyleName("col-xs-3");
		sponsorCol.setStylePrimaryName("sponsor-logo");
		sponsorCol.addStyleName("col-xs-3");
		compSpacer.setHeight("38px");
		sponsorSpacer.setHeight("38px");
		dropdownCol.addStyleName("sponsor-buttons");
	}



	@Override
	public void setSeries(final IRatingSeries result, final String baseUrl) {
		series = result;

		if (result != null) {
			seriesId = series.getId();
			showContent(true);

			weekDropDown.clear();
			if (series.isGlobal()) {
				weekButton.setText("Week");
			} else {
				weekButton.setText("Round");
			}

			if (result.getMode().equals(RatingMode.BY_COMP)) {
				matrixButton.setVisible(false);
				matrixToggle.setVisible(false);
			} else {
				matrixButton.setVisible(true);
				matrixToggle.setVisible(true);
				matrixButton.setText(result.getMode().getPlural());
			}

			
			

			for (IRatingGroup rg : series.getRatingGroups()) {
				// add item to date dropdown
				final AnchorListItem nl = new AnchorListItem(rg.getLabel());
				final Long gid = rg.getId();
				final String label = rg.getLabel();
				nl.setText(rg.getLabel());
				nl.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						// flush everything below the new group we want.
						group = null;
						groupId = gid;
						setMatrix(null);
						setQuery(null);
						setList(null);
						setItemId(null);
						//Core.getCore().setCurrentRoundId(rid);
						presenter.gotoPlace(getPlace());
						
						weekButton.setText(label);
					}

				});
				weekDropDown.add(nl);
			}
		} else {
			// no info to display
			showContent(false);
		}
		presenter.process(getPlace());

	}

	protected void showContent(boolean show) {

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
	}

	@Override
	public void setListView(TopTenListView<ITopTenItem> listView) {
		this.listView = listView;
		content.add(listView);
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

		presenter.process(getPlace());
	}

	@Override
	public void setAvailableModes(final Map<RatingMode, Long> modeMap) {
		criteriaDropDown.clear();
		// populate Rate by drop down
		for (RatingMode mode : modeMap.keySet()) {

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

	@Override	
	public void setMatrix(final IRatingMatrix matrix) {
		if (matrix != null) {

			this.matrix = matrix;
			matrixId = matrix.getId();
			matrixDropDown.clear();
			for (IRatingQuery q : matrix.getRatingQueries()) {
				AnchorListItem nl = new AnchorListItem(q.getLabel());
				final IRatingQuery _query = q;

				nl.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {

						setQuery(null);
						queryId = _query.getId();
						setList(null);
						setItemId(null);
						presenter.gotoPlace(getPlace());
					}

				});
				matrixDropDown.add(nl);
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

	protected String currentCompStyle = "";
	@Override
	public void setCompId(Long compId) {
		if (compId != this.compId) {
			this.compId = compId;
			ratingModesSet = false;
			// @REX clear everything else?
			Core.getCore().getComp(compId, new AsyncCallback<ICompetition>() {

				@Override
				public void onFailure(Throwable caught) {
					if (!currentCompStyle.isEmpty()) {
						compCol.setStyleDependentName(currentCompStyle, false);
					}
					compCol.setStyleDependentName("NON", true);
					currentCompStyle = "NON";
				}

				@Override
				public void onSuccess(ICompetition result) {
					// remove any style we already have before adding the new one
					if (!currentCompStyle.isEmpty()) {
						compCol.setStyleDependentName(currentCompStyle, false);
					}
					if (result.getAbbr() == null || result.getAbbr().isEmpty()) {
						compCol.setStyleDependentName("NON", true);
						currentCompStyle = "NON";
					} else {
						compCol.setStyleDependentName(result.getAbbr(), true);
						currentCompStyle = result.getAbbr();
					}
				}
				
			});
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

	private String currentSponsorStyle = "";
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
			listTitle.setHTML("<center><h5>"+list.getTitle()+"</h5></center>");
			//listView.setList(list, "");

			if (list.getFeatureGuid() != null) {
				createFeature.setText("View Feature");
			} else {
				createFeature.setText("Create Feature");
			}
			
			clientFactory.getSponsorForList(list, new AsyncCallback<ISponsor>() {

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(ISponsor result) {
					
					
					// remove any style we already have before adding the new one
					if (!currentSponsorStyle.isEmpty()) {
						sponsorCol.setStyleDependentName(currentSponsorStyle, false);
					}
					if (result == null || result.getAbbr() == null || result.getAbbr().isEmpty()) {
						sponsorCol.setStyleDependentName("NON", true);
						currentSponsorStyle = "NON";
						sponsorTag.setHTML("");
					} else {
						sponsorCol.setStyleDependentName(result.getAbbr(), true);
						currentSponsorStyle = result.getAbbr();
						sponsorTag.setHTML("<center>"+ result.getTagline() + "</center>");
						clientFactory.recordAnalyticsEvent("sponsor", "show", result.getName(), 1);
					}
					
				}
				
			});
			
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
	void onFeatureClicked(ClickEvent event) {	
		presenter.clickFeature(getPlace());
	}

}