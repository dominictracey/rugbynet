package net.rugby.foundation.topten.client.activity;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.core.client.Identity.Presenter;
import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.IServerPlace;
import net.rugby.foundation.model.shared.LoginInfo;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.place.FeatureListPlace;
import net.rugby.foundation.topten.client.place.SeriesPlace;
import net.rugby.foundation.topten.client.ui.SidebarViewImpl;
import net.rugby.foundation.topten.client.ui.notes.NoteView;
import net.rugby.foundation.topten.client.ui.notes.NoteView.NoteViewPresenter;
import net.rugby.foundation.topten.client.ui.toptenlistview.SeriesListView;
import net.rugby.foundation.topten.client.ui.toptenlistview.SeriesListView.SeriesListViewPresenter;
import net.rugby.foundation.topten.client.ui.toptenlistview.TopTenListView;
import net.rugby.foundation.topten.client.ui.toptenlistview.TopTenListView.TopTenListViewPresenter;
import net.rugby.foundation.topten.model.shared.INote;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;

public class SeriesActivity extends AbstractActivity /*extends TopTenListActivity*/ implements SeriesListViewPresenter, TopTenListViewPresenter, Presenter, NoteViewPresenter<INote> { 
	private SeriesPlace sPlace;
	private SeriesListView<IRatingSeries> view;
	private TopTenListView<ITopTenItem> listView;
	private NoteView<INote> noteView;
	protected ClientFactory clientFactory;
	protected ICoreConfiguration _coreConfig;
	protected int detailCount=0;
	private SidebarViewImpl sidebarView;


	public SeriesActivity(SeriesPlace sPlace, ClientFactory clientFactory) {
		//super(null,clientFactory);
		this.clientFactory = clientFactory;
		view = clientFactory.getSeriesView();
		view.setPresenter(this);
		listView = clientFactory.getSimpleView();
		listView.setPresenter(this);

		view.setListView(listView);
		sidebarView = clientFactory.getSidebarView();
		
		if (sPlace.getToken() != null) {
			this.sPlace = sPlace;
		}
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		final NoteViewPresenter<INote> _this = this;
		
		clientFactory.console("SeriesActivity.start");
		
		clientFactory.RegisterIdentityPresenter(this);

		panel.setWidget(view.asWidget());

		clientFactory.doSetup(new AsyncCallback<ICoreConfiguration>() {
			@Override
			public void onFailure(Throwable caught) {
				// suffer in silence
			}

			@Override
			public void onSuccess(final ICoreConfiguration coreConfig) {
				noteView = clientFactory.getNoteView();
				noteView.setPresenter(_this);
				noteView.asWidget().setVisible(false);
				
				clientFactory.console("SeriesActivity doSetup complete");
				_coreConfig = coreConfig;
//				if (sPlace != null) {
//					view.copyPlace(sPlace);
//				} else {
//					 //@REX WE SHOULD NEVER GET HERE
//					 //if we have nothing - start with default compId
//					assert(false);
//					SeriesPlace newPlace = new SeriesPlace();
//					newPlace.setCompId(coreConfig.getDefaultCompId());
//					view.setCompId(coreConfig.getDefaultCompId());
//					clientFactory.getPlaceController().goTo(newPlace);
//				}
				
				// compare the requested place to the place currently displayed in the view and clear the view at the appropriate clip level
				view.prepareForHere(sPlace);

				process(sPlace);
			}
		});
	}


	@Override
	public void process(SeriesPlace place) {
		sPlace = place;
		// do we have a comp?
		if (sPlace.getCompId() == null) {
			view.setCompId(_coreConfig.getDefaultCompId());
		} else if (!sPlace.getCompId().equals(view.getCompId())) {
			view.setCompId(sPlace.getCompId());
		} else if (!view.isRatingModesSet()) {
			getAvailableSeries(sPlace.getCompId());
		} 
//		else if (sPlace.getCompId() == null) {
//			setCompId(_coreConfig.getDefaultCompId());
//		} 
		else if (sPlace.getSeriesId() == null) {
			// the service knows what the default series is
			getDefaultRatingSeries(sPlace.getCompId());
		}  else if (view.getSeries() == null) { 
			// the view is waiting for us to give them the requested series
			getRatingSeries(sPlace.getSeriesId());
		} else if (sPlace.getGroupId() == null) {
			selectRatingGroup(view.getSeries().getRatingGroupIds());
		} else if (view.getGroup() == null) { 
			// view wants a different one
			getRatingGroup(sPlace.getGroupId());
		} else if (sPlace.getMatrixId() == null) {
			// pick the best available Criteria
			selectDefaultMatrix();
		} else if (view.getMatrix() == null) { 
			// view wants a different one
			getRatingMatrix(sPlace.getMatrixId());
		} else if (view.getMatrix().getRatingQueries().size() == 0) {
			// need to hydrate the RQs
			getQueriesForMatrix(view.getMatrix().getId());
		} else if (sPlace.getQueryId() == null) {
			selectDefaultQuery(view.getMatrix().getRatingQueries());
		} else if (view.getQuery() == null || view.getList() == null)  {
			getList(view.getQueryId());
		} else {
			// we jigger around with a lot of stuff so get the final state back in here
			sPlace = view.getPlace();
			
			setURL();
//			if (!starting) {
//				
//				History.newItem(sPlace.getToken(), false);
//			}
//			starting = false;
			Logger.getLogger("SeriesActivity").log(Level.INFO, "Happy");
			
			if (view.getSeries() != null && view.getSeries().getSponsor() != null && view.getSeries().getSponsor().getAbbr() != null)
				clientFactory.recordAnalyticsEvent("sponsor", "show", view.getSeries().getSponsor().getAbbr(), 1);
			
			refreshButtons();
		}
		
	}

	private void getAvailableSeries(Long compId) {
		
		// allow other elements to display this comp
		if (!Core.getCore().getCurrentCompId().equals(compId)) {
			Core.getCore().setCurrentCompId(compId);
			view.setCompId(compId);
		}
		
		Logger.getLogger("SeriesActivity").log(Level.INFO, "CALL getAvailableSeries");
		clientFactory.getRpcService().getAvailableSeries(sPlace.getCompId(), new AsyncCallback<Map<RatingMode, Long>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("We're in trouble here...");								
			}

			@Override
			public void onSuccess(Map<RatingMode, Long> result) {
				Logger.getLogger("SeriesActivity").log(Level.INFO, "RESPONSE getAvailableSeries");
				if (result != null) {
					view.setAvailableModes(result);
				}
			}

		});
	}

	protected void setCompId(Long defaultCompId) {
		Logger.getLogger("SeriesActivity").log(Level.INFO, "setCompId");
		if (defaultCompId == null) {
			throw new RuntimeException("No default configuration defined for site.");
		}
		view.setCompId(defaultCompId);
	}

	private void getDefaultRatingSeries(Long compId) {
		// default series for comp is By Position
		Logger.getLogger("SeriesActivity").log(Level.INFO, "CALL getDefaultRatingSeries");
		clientFactory.getRpcService().getDefaultRatingSeries(compId, new AsyncCallback<IRatingSeries>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to fetch data.");								
			}

			@Override
			public void onSuccess(IRatingSeries result) {
				Logger.getLogger("SeriesActivity").log(Level.INFO, "RESPONSE getDefaultRatingSeries");
				if (result != null) {
					view.setSeries(result, _coreConfig.getBaseToptenUrl() );
//					clientFactory.setPlaceInUrl(sPlace);
				}
			}

		});
	}

	private void getRatingSeries(Long seriesId) {
		Logger.getLogger("SeriesActivity").log(Level.INFO, "CALL getRatingSeries");
		clientFactory.getRpcService().getRatingSeries(seriesId, new AsyncCallback<IRatingSeries>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to fetch series data.");								
			}

			@Override
			public void onSuccess(IRatingSeries result) {
				Logger.getLogger("SeriesActivity").log(Level.INFO, "RESPONSE getRatingSeries");
				if (result != null) {
					view.setSeries(result, _coreConfig.getBaseToptenUrl());
				}
			}
		});
	}

	private void getRatingGroup(Long groupId) {
		Logger.getLogger("SeriesActivity").log(Level.INFO, "CALL getRatingGroup");
		clientFactory.getRpcService().getRatingGroup(groupId, new AsyncCallback<IRatingGroup>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to fetch group data.");	
			}

			@Override
			public void onSuccess(IRatingGroup result) {
				Logger.getLogger("SeriesActivity").log(Level.INFO, "RESPONSE getRatingGroup");
				Core.getCore().setCurrentRoundOrdinal(result.getUniversalRoundOrdinal(), true);
				view.setGroup(result, false);
			}
		});
	}

	private void selectDefaultMatrix() {
		// default matrix selection order
		//	ROUND,
		//	IN_FORM,
		//	BEST_YEAR,
		//	BEST_ALLTIME,
		//	AVERAGE_IMPACT
		Logger.getLogger("SeriesActivity").log(Level.INFO, "selectDefaultMatrix");
		IRatingSeries s = view.getSeries();
		assert (s != null);
		Criteria defaultCriteria = null;
		if (s.getActiveCriteria() != null && !s.getActiveCriteria().isEmpty()) {
			if (s.getActiveCriteria().contains(Criteria.ROUND)) {
				defaultCriteria = Criteria.ROUND;
			} else if (s.getActiveCriteria().contains(Criteria.IN_FORM)) {
				defaultCriteria = Criteria.IN_FORM;
			} else if (s.getActiveCriteria().contains(Criteria.BEST_ALLTIME)) {
				defaultCriteria = Criteria.BEST_ALLTIME;
			} else if (s.getActiveCriteria().contains(Criteria.BEST_YEAR)) {
				defaultCriteria = Criteria.BEST_YEAR;
			} else if (s.getActiveCriteria().contains(Criteria.AVERAGE_IMPACT)) {
				defaultCriteria = Criteria.AVERAGE_IMPACT;
			}
		} 

		boolean found = false;
		for (IRatingMatrix m : view.getGroup().getRatingMatrices()) {
			if (m.getCriteria().equals(defaultCriteria)) {
				found = true;
				view.setMatrix(m);
				break;
			}
		}

		// if this doesn't work then fuck it just grab the first one.
		if (!found && !view.getGroup().getRatingMatrices().isEmpty()) {
			view.setMatrix(view.getGroup().getRatingMatrices().get(0));
		}
	}

	private void getRatingMatrix(Long matrixId) {
		Logger.getLogger("SeriesActivity").log(Level.INFO, "CALL getRatingMatrix");
		clientFactory.getRpcService().getRatingMatrix(matrixId, new AsyncCallback<IRatingMatrix>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to fetch matrix data.");
			}

			@Override
			public void onSuccess(IRatingMatrix result) {
				Logger.getLogger("SeriesActivity").log(Level.INFO, "RESPONSE getRatingMatrix");
				view.setMatrix(result);
			}

		});
	}

	private void getQueriesForMatrix(Long id) {
		// we need to have the RQs for this matrix
		Logger.getLogger("SeriesActivity").log(Level.INFO, "CALL getQueriesForMatrix");
		clientFactory.getRpcService().getRatingQueriesForMatrix(id, new AsyncCallback<List<IRatingQuery>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(List<IRatingQuery> result) {
				Logger.getLogger("SeriesActivity").log(Level.INFO, "RESPONSE getQueriesForMatrix");
				view.setRatingQueries(view.getMatrix(),result);  // graft them on
			}

		});
	}

	private void selectDefaultQuery(List<IRatingQuery> ratingQueries) {
		// default query is first populated one
		Logger.getLogger("SeriesActivity").log(Level.INFO, "selectDefaultQuery");
		IRatingQuery rq = null;
		for (IRatingQuery rql: ratingQueries) {
			if (rql.getTopTenListId() != null) {
				rq = rql;
				break;
			}
		}
		if (rq != null) {
			view.setQuery(rq);
		}
	}

	private void getList(Long queryId) {
		// and the top ten list
		Logger.getLogger("SeriesActivity").log(Level.INFO, "CALL getList");
		clientFactory.getRpcService().getTopTenListForRatingQuery(queryId, new AsyncCallback<ITopTenList>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to fetch list data.");								
			}

			@Override
			public void onSuccess(ITopTenList result) {		
				Logger.getLogger("SeriesActivity").log(Level.INFO, "RESPONSE getList");
				view.setList(result);
				listView.setList(result, _coreConfig.getBaseToptenUrl());
				getNotes(result);
				refreshButtons();
				Core.getCore().setCurrentRoundOrdinal(result.getRoundOrdinal(), false);
				
				// show facebook comments				
				clientFactory.showFacebookComments(_coreConfig.getBaseToptenUrl() + result.getGuid() + "/");
			}

		});
	}

	private void getNotes(final ITopTenList result) {
		Logger.getLogger("SeriesActivity").log(Level.INFO, "CALL getNotes");
		clientFactory.getRpcService().getNotesForList(result.getId(), new AsyncCallback<List<INote>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to fetch list notes.");								
			}

			@Override
			public void onSuccess(List<INote> notes) {		
				Logger.getLogger("SeriesActivity").log(Level.INFO, "RESPONSE getNotes");			

				clientFactory.renderNotes(notes, result, new AsyncCallback<List<INote>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(List<INote> notes) {

						noteView.setNotes(notes);
						clientFactory.getNoteView().asWidget().setVisible(true);
					}

				});

			}

		});
	}

	protected void selectRatingGroup(List<Long> ratingGroupIds) {
		// default group is latest
		Logger.getLogger("SeriesActivity").log(Level.INFO, "selectRatingGroup");
		IRatingSeries series = view.getSeries();
		IRatingGroup latest = series.getRatingGroups().get(0);
		assert (series != null);
		for (IRatingGroup g : series.getRatingGroups()) {
			if (g.getUniversalRoundOrdinal() > latest.getUniversalRoundOrdinal()) {
				latest = g;
			}
		}
		Core.getCore().setCurrentRoundOrdinal(latest.getUniversalRoundOrdinal(), true);

		view.setGroup(latest, true);	
	}

	protected void setURL() {
		
		Document.get().setTitle(view.getList().getTitle());
		UrlBuilder builder = Window.Location.createUrlBuilder().setPath("/s/" + view.getList().getGuid());
		updateURLWithoutReloading(sPlace.getToken(), view.getList().getTitle(), builder.buildString());
	}

	private static native void updateURLWithoutReloading(String token, String title, String newUrl) /*-{
		$wnd.history.replaceState(token, title, newUrl);
	}-*/;

	@Override
	public void setButtons() {
		// TODO Auto-generated method stub

	}


	@Override 
	public void gotoPlace(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}


	@Override
	public void switchMode(final Long compId, final RatingMode mode) {

		clientFactory.getRpcService().getRatingSeries(compId, mode, new AsyncCallback<IRatingSeries>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(IRatingSeries result) {
				//view.setSeries(result,_coreConfig.getBaseToptenUrl());
				SeriesPlace newPlace = new SeriesPlace();
				newPlace.setCompId(view.getCompId());
				newPlace.setSeriesId(result.getId());
				gotoPlace(newPlace);
			}

		});



	}

	@Override
	public ClientFactory getClientFactory() {
		return clientFactory;
	}

	@Override
	public Boolean deleteNote(Long noteId) {
		clientFactory.getRpcService().deleteNote(noteId, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(Boolean result) {
				getNotes(listView.getList());	
				
			}
			
		}); 
			
		return true;
	}

	@Override
	public void addNote(INote note) {
		// TODO Auto-generated method stub

	}

	@Override
	public void editNote(INote note) {
		// TODO Auto-generated method stub

	}



	@Override
	public void onLoginComplete(String destination) {
		refreshButtons();	
	}
	
	@Override
	public void showNext() {
//		LoginInfo login = Core.getCore().getClientFactory().getLoginInfo();
//		if (login.isTopTenContentContributor() || login.isTopTenContentEditor()) {
//			clientFactory.getPlaceController().goTo(new TopTenListPlace(view.getList().getNextId()));
//		} else {
//			clientFactory.getPlaceController().goTo(new TopTenListPlace(view.getList().getNextPublishedId()));
//		}

	}

	@Override
	public void showPrev() {
//		LoginInfo login = Core.getCore().getClientFactory().getLoginInfo();
//		if (login.isTopTenContentContributor() || login.isTopTenContentEditor()) {
//			clientFactory.getPlaceController().goTo(new TopTenListPlace(view.getList().getPrevId()));
//		} else {
//			clientFactory.getPlaceController().goTo(new TopTenListPlace(view.getList().getPrevPublishedId()));
//		}	
	}


	@Override
	public void showRatingDetails(final ITopTenItem value) {
		++detailCount;
		clientFactory.getRpcService().getPlayerRating(value.getPlayerRatingId(), new AsyncCallback<IPlayerRating>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to save Top Ten List. See event log for details.");
			}

			@Override
			public void onSuccess(IPlayerRating result) {
				clientFactory.getRatingPopup().setRating(result);
				clientFactory.getRatingPopup().center();
				if (clientFactory.getRatingPopup().getAbsoluteTop() < 100) {
					clientFactory.getRatingPopup().setPopupPosition(clientFactory.getRatingPopup().getAbsoluteLeft(), 100);
				}
				clientFactory.recordAnalyticsEvent("ratingDetails", "click", result.getPlayer().getShortName(), 1);
			}
		});	


	}

	@Override
	public String mayStop()
	{
		if (view != null && view.getList() != null && view.getList().getTitle() != null) {
			clientFactory.recordAnalyticsEvent("ratingDetailsCount", "click", view.getList().getTitle(), detailCount);
		}
		return null;
	}


	private void refreshButtons() {

		LoginInfo login = Core.getCore().getClientFactory().getLoginInfo();

		// create feature
		view.showButtons(!(login == null || login.isLoggedIn() == false || (login.isTopTenContentContributor() == false && login.isTopTenContentEditor() == false)));
	}

	@Override
	public void clickFeature(final SeriesPlace place) {
		if (place.getCompId() == null || place.getQueryId() == null) {
			Window.alert("Bad competition or query Id");
			return;
		}
		
		if (view.getList() != null && view.getList().getSeries() == true) {
			// there is no feature associated with this list so create one.
			clientFactory.getRpcService().createFeature(place.getCompId(), place.getQueryId(), new AsyncCallback<IServerPlace>() {
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Failed to create feature. See event log for details.");
				}
	
				@Override
				public void onSuccess(IServerPlace result) {
					// go to the Feature view
					FeatureListPlace fPlace = new FeatureListPlace(result);
					clientFactory.getPlaceController().goTo(fPlace);
				}
			});		
		} else if (view.getList() != null && view.getList().getSeries() == false && view.getList().getFeatureGuid() != null) {
			// go to the feature place for the list (list.featureGuid)
			clientFactory.getRpcService().getPlace(view.getList().getFeatureGuid(), new AsyncCallback<IServerPlace>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Couldn't find feature place for list");
				}

				@Override
				public void onSuccess(IServerPlace result) {
					FeatureListPlace place = new FeatureListPlace(result);
					clientFactory.getPlaceController().goTo(place);
				}
				
			});
		} else {
			Window.alert("Bad list in view.");
		}
	}

	@Override
	public void promote(SeriesPlace place) {
		// TODO Auto-generated method stub
		
	}
	

}