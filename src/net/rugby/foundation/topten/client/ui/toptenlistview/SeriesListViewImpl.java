package net.rugby.foundation.topten.client.ui.toptenlistview;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IPlayer;
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

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Badge;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelFooter;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Div;
import org.gwtbootstrap3.client.ui.html.Span;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
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
	protected Anchor compLink;
	@UiField 
	Div sponsorDiv;
	@UiField
	protected Div sponsorSpacer;
	@UiField
	protected HTML sponsorTag;
	@UiField
	protected Anchor sponsorLink;
	@UiField 
	protected HTML listTitle;

	// admin buttons
	@UiField 
	protected PanelFooter adminButtons;
	@UiField 
	protected Button createFeature;
	@UiField 
	protected Button promote;

	// promote results
	@UiField
	protected Modal promoteModal;
	@UiField
	protected Button promoteSave;
	@UiField
	protected Div promoteHtml;


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
	protected int lastPosition;

	private Criteria lastCriteria;

	@UiTemplate("SeriesListViewImpl.ui.xml")

	interface SeriesListViewImplUiBinder extends UiBinder<Widget, SeriesListViewImpl>
	{
	}


	public SeriesListViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
		showButtons(false);
		ratingModesSet = false;
		lastPosition = -1;
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

		sponsorLink.addStyleName("trn-sponsor-link");
		compLink.addStyleName("trn-comp-link");
	}

	@UiHandler("promote")
	void onPromoteButtonClicked(ClickEvent event) {	
		presenter.promote(list);
	}

	@Override
	public void setSeries(final IRatingSeries result) {
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




			//for (IRatingGroup rg : series.getRatingGroups()) {
			for (Long rgId : series.getRatingGroupIds()) {
				
				// add item to date dropdown
				final String label = series.getRatingGroupNameMap().get(rgId);
				final AnchorListItem nl = new AnchorListItem(label);
				final Long gid = rgId;		
				nl.setText(label);
				
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

						//weekButton.setText(label);
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

		weekButton.setText(group.getLabel());

		criteriaGroup.setVisible(false);
		criteriaDropDown.clear();
		if (group.getRatingMatrices().size() > 1) {
			criteriaGroup.setVisible(true);
			for (IRatingMatrix m : group.getRatingMatrices()) {
				final AnchorListItem nl = new AnchorListItem(m.getCriteria().getMenuName());
				final IRatingMatrix _m = m;
				nl.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						// go to the new series
						//setMatrix(_m);
						lastCriteria = _m.getCriteria();
						matrixId = _m.getId();
						matrix = null;
						setQuery(null);
						setList(null);
						setItemId(null);

						presenter.gotoPlace(getPlace());
					}

				});
				criteriaDropDown.add(nl);
			}
		}

		presenter.process(getPlace());
	}

	@Override
	public void setAvailableModes(final Map<RatingMode, Long> modeMap) {
		criteriaDropDown.clear();
		//		// populate Rate by drop down
		//		for (RatingMode mode : modeMap.keySet()) {
		//
		//			final AnchorListItem nl = new AnchorListItem(mode.name());
		//			final Long _seriesId = modeMap.get(mode);
		//			nl.addClickHandler(new ClickHandler() {
		//
		//				@Override
		//				public void onClick(ClickEvent event) {
		//					// go to the new series
		//					series = null;
		//					seriesId = _seriesId;
		//					group = null;
		//					groupId = null;
		//					setMatrix(null);
		//					setQuery(null);
		//					setList(null);
		//					setItemId(null);
		//
		//					presenter.gotoPlace(getPlace());
		//				}
		//
		//			});
		//			criteriaDropDown.add(nl);
		//		}

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
			lastCriteria = matrix.getCriteria();
			for (IRatingQuery q : matrix.getRatingQueries()) {
				if (q.getTopTenListId() != null) {
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
			}

			criteriaButton.setText(matrix.getCriteria().getMenuName());
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

			if (series != null && series.getMode() == RatingMode.BY_POSITION) {
				lastPosition = query.getRatingMatrix().getRatingQueries().indexOf(query);
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
						sponsorLink.setHref("#");
					} else {
						sponsorCol.setStyleDependentName(result.getAbbr(), true);
						currentSponsorStyle = result.getAbbr();
						sponsorTag.setHTML("<center>"+ result.getTagline() + "</center>");
						clientFactory.recordAnalyticsEvent("sponsor", "show", result.getName(), 1);
						sponsorLink.setHref(result.getUrl());
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



	@Override
	public int getLastPosition() {
		return lastPosition;
	}


	@Override
	public Criteria getLastCriteria() {
		return lastCriteria;
	}
	protected List<IPlayer> playerList = null;
	protected Map<IPlayer, TextBox> playerMap = new HashMap<IPlayer, TextBox>();
	protected Map<IPlayer, CheckBox> checkBoxMap = new HashMap<IPlayer, CheckBox>();

	@Override
	public void showPromoteResults(List<IPlayer> result) {
		playerList = result; 
		promoteHtml.clear();
		for (IPlayer p: result){
			Row row = new Row();
			Column columnLabel = new Column("MD-3");
			Column columnBadge = new Column("MD-1");
			Column columnCheckbox = new Column("md-2");
			Column columnLink = new Column("md-1");
			Column columnField = new Column("md-4");

			columnBadge.addStyleName("col-md-2");
			columnLink.addStyleName("col-md-1");
			columnField.addStyleName("col-md-4");
			columnCheckbox.addStyleName("col-md-2");
			columnLabel.add(new Span(p.getDisplayName()));
			columnLabel.addStyleName("col-md-3");
			columnLabel.addStyleName("twitterDialougeRow");

			row.add(columnLabel);
			row.add(columnBadge);
			row.add(columnCheckbox);
			row.add(columnLink);
			row.add(columnField);

			if (p.getTwitterHandle() != null && !p.getTwitterHandle().isEmpty()){
				Badge b = new Badge();
				Span spanLink = new Span("&nbsp;");
				Span spanField = new Span("&nbsp;");
				columnLink.add(spanLink);
				columnField.add(spanField);
				b.addStyleName("greenBadge");
				b.setText("Success");
				columnBadge.add(b);
			} else if (p.getTwitterNotAvailable() != null && p.getTwitterNotAvailable().equals(true)){
				Badge b = new Badge();
				Span spanLink = new Span("&nbsp;");
				Span spanField = new Span("&nbsp;");
				columnLink.add(spanLink);
				columnField.add(spanField);
				b.addStyleName("redBadge");
				b.setText("No Twitter");
				columnBadge.add(b);
			} else {
				Badge b = new Badge();
				b.addStyleName("redBadge");
				b.setText("Fail");
				CheckBox checkBox = new CheckBox();
				checkBoxMap.put(p, checkBox);
				checkBox.setText("No Twitter");
				columnCheckbox.add(checkBox);

				Button button = new Button();
				button.setIcon(IconType.SEARCH);
				Anchor a = new Anchor();
				a.setText("Google");
				String target;

				target = "https://www.google.com/search?sourceid=chrome-psyapi2&ion=1&espv=2&ie=UTF-8&q="+ URL.encode(p.getDisplayName() + " rugby twitter ");
				a.setHref(target);
				a.setTarget("_blank");

				TextBox tb = new TextBox();
				playerMap.put(p, tb);
				tb.setName(p.getDisplayName());
				tb.setPlaceholder("@twitter");
				columnBadge.add(b);
				columnLink.add(a);
				columnField.add(tb);
			}

			promoteHtml.add(row);

		}
		promoteModal.show();

	}
	@UiHandler("promoteSave")
	protected void onPromoteSaveClicked (ClickEvent event) {
		for (final IPlayer p: playerList){
			if (playerMap.containsKey(p) || checkBoxMap.containsKey(p)){
				TextBox tb = playerMap.get(p);
				CheckBox cb = checkBoxMap.get(p);
				final String twitter = tb.getText();
				final Boolean noTwitter = cb.getValue();

				Boolean dirty = false;

				if (twitter != null && !twitter.isEmpty()) {
					p.setTwitterHandle(twitter);
					dirty = true;
				}
				if (noTwitter != null && noTwitter.equals(true)){
					p.setTwitterNotAvailable(noTwitter);
					dirty = true;
				}
				if (dirty) {
					clientFactory.getRpcService().savePlayer(p, new AsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onSuccess(Boolean result) {
							if (twitter.isEmpty() && noTwitter.equals(true)){
								Notify.notify(p.getDisplayName() + " no twitter");
							} else {
								Notify.notify(p.getDisplayName() + " saved!");
								ITopTenItem tti = null;
								//list.getList();
								//Look through the Top Ten List for the tti that has a playerId equal to the id of the player.
								if (!twitter.isEmpty() && noTwitter.equals(false)){
									for (ITopTenItem i: list.getList()){
										if (i.getPlayerId().equals(p.getId())){
											tti=i;
											break;
										}
										//Notify.notify(tti.getPlayer().getDisplayName());
									}
									clientFactory.getRpcService().sendTweet(tti, list, new AsyncCallback<String>(){

										@Override
										public void onFailure(Throwable caught) {
											// TODO Auto-generated method stub

										}

										@Override
										public void onSuccess(String result) {
											Notify.notify(result);

										}

									});
								}
							}
						}
					});
				}
			}

		}
		promoteModal.hide();
	}

}