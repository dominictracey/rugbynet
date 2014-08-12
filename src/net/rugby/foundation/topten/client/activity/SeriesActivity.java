package net.rugby.foundation.topten.client.activity;

import java.util.List;

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
					if (sPlace.getCompId() == null) {
						view.setCompId(coreConfig.getDefaultCompId());
						if (coreConfig.getDefaultCompId() == null) {
							throw new RuntimeException("No default configuration defined for site.");
						}
						clientFactory.getPlaceController().goTo(view.getPlace());
						return;
					} else if (sPlace.getSeriesId() == null) {
						// default series for comp is By Position
						clientFactory.getRpcService().getRatingSeries(sPlace.getCompId(), RatingMode.BY_POSITION, new AsyncCallback<IRatingSeries>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Failed to fetch data.");								
							}

							@Override
							public void onSuccess(IRatingSeries result) {
								view.setSeries(result, _coreConfig.getBaseToptenUrl() );
							}

						});
						return;
					}  else if (view.getSeries() == null) { // the view is waiting for us to give them the requested series
						clientFactory.getRpcService().getRatingSeries(sPlace.getSeriesId(), new AsyncCallback<IRatingSeries>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Failed to fetch series data.");								
							}

							@Override
							public void onSuccess(IRatingSeries result) {
								view.setSeries(result, _coreConfig.getBaseToptenUrl());
							}
						});
						return;
					} else if (sPlace.getGroupId() == null) {
						// default group is latest
						IRatingSeries series = view.getSeries();
						IRatingGroup latest = series.getRatingGroups().get(0);
						assert (series != null);
						for (IRatingGroup g : series.getRatingGroups()) {
							if (g.getDate().after(latest.getDate())) {
								latest = g;
							}
						}
						view.setGroup(latest);
						return;
					} else if (view.getGroup() == null) { // view wants a different one
						clientFactory.getRpcService().getRatingGroup(sPlace.getGroupId(), new AsyncCallback<IRatingGroup>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Failed to fetch group data.");	
							}

							@Override
							public void onSuccess(IRatingGroup result) {
								view.setGroup(result);
							}
						});
						return;
					} else if (sPlace.getMatrixId() == null) {
						// default matrix is BEST_YEAR
						IRatingGroup g = view.getGroup();
						assert (g != null);
						for (IRatingMatrix m : g.getRatingMatrices()) {
							if (m.getCriteria() == Criteria.BEST_YEAR) {
								view.setMatrix(m);
								break;
							}
						}
						return;
					} else if (view.getMatrix() == null) { // view wants a different one
						clientFactory.getRpcService().getRatingMatrix(sPlace.getMatrixId(), new AsyncCallback<IRatingMatrix>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Failed to fetch matrix data.");
							}

							@Override
							public void onSuccess(IRatingMatrix result) {
								view.setMatrix(result);
							}

						});
						return;
					} else if (view.getMatrix().getRatingQueries().size() == 0) {
						// we need to have the RQs for this matrix
						clientFactory.getRpcService().getRatingQueriesForMatrix(view.getMatrix().getId(), new AsyncCallback<List<IRatingQuery>>() {

							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void onSuccess(List<IRatingQuery> result) {
								view.setRatingQueries(view.getMatrix(),result);  // graft them on
							}
							
						});
					
					} else if (sPlace.getQueryId() == null) {
						
						
						// default query is first
						view.setQuery(view.getMatrix().getRatingQueries().get(0));
						//clientFactory.getPlaceController().goTo(view.getPlace());
						return;
					} else if (view.getList() == null) { // changing tabs
//						for (IRatingQuery q : view.getMatrix().getRatingQueries()) {
//							if (q.getId().equals(view.getQueryId())) {
//								view.setQuery(q);
//							}
//						}
						// and the top ten list
						clientFactory.getRpcService().getTopTenListForRatingQuery(view.getQueryId(), new AsyncCallback<ITopTenList>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Failed to fetch list data.");								
							}

							@Override
							public void onSuccess(ITopTenList result) {		
								view.setList(result);
								listView.setList(result, _coreConfig.getBaseToptenUrlForFacebook());
							}

						});
						return;
					}
				} else {
					// if we have nothing - start with default compId
					SeriesPlace newPlace = new SeriesPlace();
					newPlace.setCompId(coreConfig.getDefaultCompId());
					view.setCompId(coreConfig.getDefaultCompId());
					clientFactory.getPlaceController().goTo(newPlace);
					return;
				}
			}	
		});

	}



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
}