package net.rugby.foundation.topten.client.activity;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
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
import net.rugby.foundation.topten.client.ui.notes.NoteView;
import net.rugby.foundation.topten.client.ui.notes.NoteView.NoteViewPresenter;
import net.rugby.foundation.topten.client.ui.toptenlistview.SeriesListView;
import net.rugby.foundation.topten.client.ui.toptenlistview.SeriesListView.SeriesListViewPresenter;
import net.rugby.foundation.topten.client.ui.toptenlistview.TopTenListView;
import net.rugby.foundation.topten.client.ui.toptenlistview.TopTenListView.TopTenListViewPresenter;
import net.rugby.foundation.topten.model.shared.INote;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;

public class SeriesActivity extends TopTenListActivity implements SeriesListViewPresenter, TopTenListViewPresenter, Presenter, NoteViewPresenter<INote> { 
	private SeriesPlace sPlace;
	private SeriesListView<IRatingSeries> view;
	private TopTenListView<ITopTenItem> listView;
	private NoteView<INote> noteView;
//	private boolean starting;
//	private static boolean ignore = false;

	public SeriesActivity(SeriesPlace sPlace, ClientFactory clientFactory) {
		super(null,clientFactory);
		view = clientFactory.getSeriesView();
		view.setPresenter(this);
		listView = clientFactory.getSimpleView();
		listView.setPresenter(this);
		noteView = clientFactory.getNoteView();
		noteView.setPresenter(this);
		view.setListView(listView);
		view.setNotesView(noteView);

		if (sPlace.getToken() != null) {
			this.sPlace = sPlace;
		}
		
//		final HandlerRegistration reg =  History.addValueChangeHandler(new ValueChangeHandler<String>() {
//			
//			public void onValueChange(ValueChangeEvent<String> event) {
//				String placeToken = event.getValue();
//				SeriesPlace p = new SeriesPlace(event.getValue());
//				Logger.getLogger("HistChangeHandler").log(Level.INFO,"history changed " + placeToken);
//				//view.prepareForHere(p);
//				//SeriesPlace place = new SeriesPlace(placeToken);
//				//gotoPlace(place);
////				if (selectedContactName == null || selectedContact.equals(��)){
////					// do nothing
////				}else{
////					Contact contact = findContactByName(selectedContactName);
////					
////					/*
////					 * Prior to refactoring, this was on the click-handler.  
////					 * To provide 'Back' support, we will now do this on a URL 
////					 * change.  All the click-handler does now is change the URL
////					 * which, in turn, calls this History ValueChangeHandler. 
////					 */
////					eventBus.fireEvent(new ContactSelectedEvent(contact));
////					showContactAsSelected(contact);	
////				}
//			}
//		});
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		
//		if (ignore) {  // allows us to manipulate the hash fragment manually in setUrl
//			ignore = false;
//			return;
//		}
//		starting = true;
		panel.setWidget(view.asWidget());

		clientFactory.doSetup(new AsyncCallback<ICoreConfiguration>() {
			@Override
			public void onFailure(Throwable caught) {
				// suffer in silence
			}

			@Override
			public void onSuccess(final ICoreConfiguration coreConfig) {
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
		} else if (!view.isRatingModesSet()) {
			getAvailableSeries(sPlace.getCompId());
		} else if (sPlace.getCompId() == null) {
			setCompId(_coreConfig.getDefaultCompId());
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
			// we jigger around with a lot of stuff so get the final state back in here
			sPlace = view.getPlace();
			
			setURL();
//			if (!starting) {
//				
//				History.newItem(sPlace.getToken(), false);
//			}
//			starting = false;
			Logger.getLogger("SeriesActivity").log(Level.INFO, "Happy");
		}
		
	}

	protected void getAvailableSeries(Long compId) {
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
				getNotes(result);
			}

		});
	}

	protected void getNotes(final ITopTenList result) {
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
		view.setGroup(latest, true);	
	}

	protected void setURL() {
		
		Document.get().setTitle(view.getList().getTitle());
		UrlBuilder builder = Window.Location.createUrlBuilder().setPath("/s/" + view.getList().getGuid());
//		if (!Window.Location.getHash().equals(sPlace.getToken())) {
//			ignore = true;
//			builder.setHash(sPlace.getToken()); 
//		}
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addNote(INote note) {
		// TODO Auto-generated method stub

	}

	@Override
	public void editNote(INote note) {
		// TODO Auto-generated method stub

	}

	public static native void recordAnalyticsEvent(String cat, String action, String label, int val) /*-{

		$wnd.ganew('send', 'event', cat, action, label, val);
	}-*/;
}