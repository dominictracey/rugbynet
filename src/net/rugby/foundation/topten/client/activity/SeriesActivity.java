package net.rugby.foundation.topten.client.activity;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import net.rugby.foundation.core.client.Identity.Presenter;
import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.place.SeriesPlace;
import net.rugby.foundation.topten.client.ui.toptenlistview.SeriesListView;
import net.rugby.foundation.topten.client.ui.toptenlistview.SeriesListView.SeriesListViewPresenter;
import net.rugby.foundation.topten.client.ui.toptenlistview.TopTenListView;
import net.rugby.foundation.topten.client.ui.toptenlistview.TopTenListView.TopTenListViewPresenter;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;

public class SeriesActivity extends TopTenListActivity implements SeriesListViewPresenter, TopTenListViewPresenter, Presenter { 
	private SeriesPlace sPlace;
	private SeriesListView<IRatingSeries> view;
	private TopTenListView<ITopTenItem> listView;

	public SeriesActivity(SeriesPlace sPlace, ClientFactory clientFactory) {
		super(null,clientFactory);
		view = clientFactory.getSeriesView();
		view.setPresenter(this);
		listView = clientFactory.getSimpleView();
		listView.setPresenter(this);
		view.setListView(listView);

		if (sPlace.getToken() != null) {
			this.sPlace = sPlace;
		}
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		panel.setWidget(view.asWidget());

		clientFactory.doSetup(new AsyncCallback<ICoreConfiguration>() {
			@Override
			public void onFailure(Throwable caught) {
				// suffer in silence
			}

			@Override
			public void onSuccess(final ICoreConfiguration coreConfig) {
				_coreConfig = coreConfig;
				if (sPlace != null) {
					view.copyPlace(sPlace);
				} else {
					// @REX WE SHOULD NEVER GET HERE
					// if we have nothing - start with default compId
					assert(false);
					SeriesPlace newPlace = new SeriesPlace();
					newPlace.setCompId(coreConfig.getDefaultCompId());
					view.setCompId(coreConfig.getDefaultCompId());
					clientFactory.getPlaceController().goTo(newPlace);
				}

				// do we have a comp?
				if (sPlace.getCompId() == null) {
					view.setCompId(_coreConfig.getDefaultCompId());
				} else if (!view.isRatingModesSet()) {
					getAvailableSeries(sPlace.getCompId());
				} else if (sPlace.getCompId() == null) {
					setCompId(coreConfig.getDefaultCompId());
				} else if (sPlace.getSeriesId() == null) {
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
					setURL();
					Logger.getLogger("SeriesActivity").log(Level.INFO, "Happy");
				}
		}
	});
}



protected void getAvailableSeries(Long compId) {
	Logger.getLogger("SeriesActivity").log(Level.INFO, "CALL getAvailableSeries");
	clientFactory.getRpcService().getAvailableSeries(sPlace.getCompId(), new AsyncCallback<List<RatingMode>>() {

		@Override
		public void onFailure(Throwable caught) {
			Window.alert("We're in trouble here...");								
		}

		@Override
		public void onSuccess(List<RatingMode> result) {
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
				clientFactory.setPlaceInUrl(sPlace);
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

	view.setQuery(rq);
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
			listView.setList(result, _coreConfig.getBaseToptenUrlForFacebook());
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
	view.setGroup(latest, true);	
}

protected void setURL() {
	String newURL = Window.Location.createUrlBuilder().setPath("/s/" + view.getList().getGuid()).buildString(); //setHash("newhash").buildString();
	updateURLWithoutReloading(newURL);
}

private static native void updateURLWithoutReloading(String newUrl) /*-{
$wnd.history.pushState(newUrl, "", newUrl);
}-*/;

@Override
public void setButtons() {
	// TODO Auto-generated method stub

}
//
//	@Override
//	public void showCriteria(Criteria inForm) {
//		// TODO Auto-generated method stub
//		
//	}

//	@Override
//	public void showRatingQuery(IRatingQuery rq) {
//		clientFactory.getRpcService().getTopTenListForRatingQuery(rq.getId(), new AsyncCallback<ITopTenList>() {
//
//			@Override
//			public void onFailure(Throwable caught) {
//				Window.alert("Failed to fetch data.");								
//			}
//
//			@Override
//			public void onSuccess(ITopTenList result) {				
//				listView.setList(result, _coreConfig.getBaseToptenUrlForFacebook());
//			}
//			
//		});
//		
//	}

@Override 
public void gotoPlace(Place place) {
	clientFactory.getPlaceController().goTo(place);
}
//	sPlace.setSeriesId(result.getId());
//	
//	//view.setSeries(result, sPlace.getGroupId(), sPlace.getCriteria(), sPlace.getQueryId() );
//	view.setSeries(result, _coreConfig.getBaseToptenUrl() );	
//	
//	boolean send = false; // set to true if we haven't landed yet
//	
//	IRatingGroup defaultGroup = null;
//	// default week is latest
//	if (sPlace.getGroupId() == null) {
//		send = true;
//		sPlace.setGroupId(result.getRatingGroups().get(0).getId());
//		defaultGroup = result.getRatingGroups().get(0);
//	}
//	
//	assert (sPlace.getGroupId() != null || defaultGroup != null);
//	
//	IRatingMatrix defaultMatrix = null;
//	// default rating scheme is best of last year
//	if (sPlace.getMatrixId() == null) {
//		send = true;
//		for (IRatingMatrix m : defaultGroup.getRatingMatrices()) {
//			if (m.getCriteria() == Criteria.BEST_YEAR) {
//				sPlace.setMatrixId(m.getId());
//				defaultMatrix = m;
//				break;
//			}
//		}
//	}
//	
//	assert (sPlace.getMatrixId() != null || defaultMatrix != null);
//	
//	IRatingQuery defaultQuery = null;
//	// default query is first
//	if (sPlace.getQueryId() == null) {
//		send = true;
//		sPlace.setQueryId(defaultMatrix.getRatingQueries().get(0).getId());
//		defaultQuery = defaultMatrix.getRatingQueries().get(0);
//	}
//	
//	assert (sPlace.getQueryId() != null || defaultQuery != null);
//	
////	// default player is first
////	Long defaultPlayerId = null;
////	if (place.getPlayerId() == null) {
////		send = true; //?
////		place.setPlayerId(defaultQuery.getT)
////	}
//	if (send) {
//		SeriesPlace newPlace = new SeriesPlace(sPlace.getCompId(),sPlace.getSeriesId(),sPlace.getGroupId(),sPlace.getMatrixId(),sPlace.getQueryId(),sPlace.getPlayerId());
//		clientFactory.getPlaceController().goTo(newPlace);
//	}
//}
//
//});
//}
//}

@Override
public void switchMode(final Long compId, final RatingMode mode) {
	clientFactory.getRpcService().getRatingSeries(compId, mode, new AsyncCallback<IRatingSeries>() {

		@Override
		public void onFailure(Throwable caught) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSuccess(IRatingSeries result) {
			//				view.setSeriesId(result.getId());
			view.setSeries(result,_coreConfig.getBaseToptenUrl());
		}

	});



}
}