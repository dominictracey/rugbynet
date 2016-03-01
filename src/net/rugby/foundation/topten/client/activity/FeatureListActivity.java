package net.rugby.foundation.topten.client.activity;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.core.client.Identity.Presenter;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.LoginInfo;
import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.place.FeatureListPlace;
import net.rugby.foundation.topten.client.ui.toptenlistview.EditTTLInfo.EditTTLInfoPresenter;
import net.rugby.foundation.topten.client.ui.toptenlistview.FeatureListView;
import net.rugby.foundation.topten.client.ui.toptenlistview.FeatureListView.FeatureListViewPresenter;
import net.rugby.foundation.topten.model.shared.ITopTenList;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class FeatureListActivity extends AbstractActivity implements FeatureListViewPresenter, Presenter, EditTTLInfoPresenter  {

	protected ClientFactory clientFactory;
	private FeatureListPlace place;

	private FeatureListView<ITopTenList> view;
	protected ICoreConfiguration _coreConfig;
	protected int detailCount=0;
	private boolean editing = false;
	public FeatureListActivity(FeatureListPlace place, ClientFactory clientFactory) {

		this.clientFactory = clientFactory;
		view = clientFactory.getFeatureListView();
		//clientFactory.RegisterIdentityPresenter(this);

		if (place != null && place.getToken() != null) {
			this.place = place;
		}
	}


	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		final FeatureListViewPresenter This = this;
		panel.setWidget(view.asWidget());
		final AcceptsOneWidget _panel = panel;
		view.setClientFactory(clientFactory);
		view.setPresenter(this);
		//final Identity.Presenter identityPresenter = this;
		clientFactory.RegisterIdentityPresenter(this);
		clientFactory.console("FeatureActivity.start");
		clientFactory.getNoteView().asWidget().setVisible(false);
		clientFactory.doSetup(new AsyncCallback<ICoreConfiguration>() {


			@Override
			public void onFailure(Throwable caught) {
				// suffer in silence
			}

			@Override
			public void onSuccess(final ICoreConfiguration coreConfig) {
				clientFactory.console("FeatureActivity.start doSetup complete with place " + place.getToken());
				_coreConfig = coreConfig;
				if (place != null) {
					if (place.getListId() != null) {
						clientFactory.getRpcService().getTopTenList(place.getListId(), new AsyncCallback<ITopTenList>() {
							@Override
							public void onFailure(Throwable caught) {
								// fail silently
								view.setList(null,"");
							}

							@Override
							public void onSuccess(final ITopTenList ttl) {
								LoginInfo login = Core.getCore().getClientFactory().getLoginInfo();

								view.setPresenter(This);
								view.setClientFactory(clientFactory);
								_panel.setWidget(view.asWidget());
								

								if (ttl != null) {
									view.setList(ttl, coreConfig.getBaseToptenUrl());
									// allow other elements to display this comp/round
									Long compId = ttl.getCompId();
									//if (!Core.getCore().getCurrentCompId().equals(compId)) {
									Core.getCore().setCurrentCompId(compId);
									//}
									
									//if (ttl.getRoundOrdinal() != null) {
									//clientFactory.console("FeatureActivity.start calling setCurrentRoundOrdinal with " + ttl.getRoundOrdinal());
										Core.getCore().setCurrentRoundOrdinal(ttl.getRoundOrdinal(), false);
									//} else {
										// show either the latest or last results
									//	setRoundForLegacyComp(ttl);
									//}
									
									refreshButtons(login, ttl);
									
									_panel.setWidget(view.asWidget());

									setURL();

									// show facebook comments				
									clientFactory.showFacebookComments(_coreConfig.getBaseToptenUrl() + ttl.getFeatureGuid() + "/");
									
								} else {
									Window.alert("Failed to fetch top ten list.");
									view.setList(null,"");
								}


							}

						});
					} else { // no listId
						// do we have a comp?
						if (place.getCompId() == null) {
							clientFactory.console("FeatureActivity.start for no ListId");
							FeatureListPlace newPlace = new FeatureListPlace();
							newPlace.setCompId(coreConfig.getDefaultCompId());
							if (coreConfig.getDefaultCompId() == null) {
								throw new RuntimeException("No default configuration defined for site.");
							}
							clientFactory.getPlaceController().goTo(newPlace);
						} else {
							// just use the comp from place
							showListForComp(place.getCompId());
						}
					}
				}
			}
		});
	}


	protected void refreshButtons(LoginInfo login, ITopTenList ttl) {
		if (login.isLoggedIn()) {
			view.showContributorButtons(login.isTopTenContentContributor());
			view.showEditorButtons(login.isTopTenContentEditor());
			if (login.isTopTenContentContributor() || login.isTopTenContentEditor()) {
				if (ttl.getNextId() != null) {
					clientFactory.setTTLName(ttl.getNextId(), view.getNextLabel());
				}
				if (ttl.getPrevId() != null) {
					clientFactory.setTTLName(ttl.getPrevId(), view.getPrevLabel());
				}
				view.hasNext(ttl.getNextId() != null);
				view.hasPrev(ttl.getPrevId() != null);
			} else {
				if (ttl.getNextPublishedId() != null) {
					clientFactory.setTTLName(ttl.getNextId(), view.getNextLabel());
				}
				if (ttl.getPrevPublishedId() != null) {
					clientFactory.setTTLName(ttl.getPrevId(), view.getPrevLabel());
				}
			}
		} else {
			if (ttl.getNextPublishedId() != null) {
				clientFactory.setTTLName(ttl.getNextPublishedId(), view.getNextLabel());
			}
			if (ttl.getPrevPublishedId() != null) {
				clientFactory.setTTLName(ttl.getPrevPublishedId(), view.getPrevLabel());
			}
			view.hasNext(ttl.getNextPublishedId() != null);
			view.hasPrev(ttl.getPrevPublishedId() != null);
		}

		// 


	}	

	private void showListForComp(final Long compId) {
				
		// allow other elements to display this comp
		if (!Core.getCore().getCurrentCompId().equals(compId)) {
			Core.getCore().setCurrentCompId(compId);
		}
		
		clientFactory.console("Looking for a list for compId " + compId);
		clientFactory.getRpcService().getLatestListIdForComp(compId, new AsyncCallback<Long>() {
			@Override
			public void onFailure(Throwable caught) {
				// fail silently
				//Window.alert("Failed to fetch top ten list.");
			}

			@Override
			public void onSuccess(Long result) {

				clientFactory.console("Got this listId as latest for compId " + result);
				if (result != null) {
					FeatureListPlace newPlace = new FeatureListPlace();
					newPlace.setCompId(compId);
					newPlace.setListId(result);
					clientFactory.getPlaceController().goTo(newPlace);
				} 
			}	
		});
	}

	@Override
	public void showNext() {
		if (view.getList() != null) {
			Long targetId = null;
			LoginInfo loginInfo = clientFactory.getLoginInfo();
			if (loginInfo.isLoggedIn()) {
				if (loginInfo.isTopTenContentContributor() || loginInfo.isTopTenContentEditor()) {
					targetId = view.getList().getNextId();
				} else {
					targetId = view.getList().getNextPublishedId();
				}
			} else {
				targetId = view.getList().getNextPublishedId();
			}

			FeatureListPlace newPlace = new FeatureListPlace(place.getCompId(), targetId, null);
			clientFactory.getPlaceController().goTo(newPlace);
		}		
	}

	@Override
	public void showPrev() {
		if (view.getList() != null) {
			Long targetId = null;
			LoginInfo loginInfo = clientFactory.getLoginInfo();
			if (loginInfo.isLoggedIn()) {
				if (loginInfo.isTopTenContentContributor() || loginInfo.isTopTenContentEditor()) {
					targetId = view.getList().getPrevId();
				} else {
					targetId = view.getList().getPrevPublishedId();
				}
			} else {
				targetId = view.getList().getPrevPublishedId();
			}

			FeatureListPlace newPlace = new FeatureListPlace(place.getCompId(), targetId, null);
			clientFactory.getPlaceController().goTo(newPlace);

			//			clientFactory.getRpcService().getTopTenList(targetId, new AsyncCallback<ITopTenList>() {
			//
			//				@Override
			//				public void onFailure(Throwable caught) {
			//					Window.alert("Could not find list. Try again later.");
			//
			//				}
			//
			//				@Override
			//				public void onSuccess(ITopTenList result) {
			//					view.setList(result, _coreConfig.getBaseToptenUrl());
			//					place = new FeatureListPlace(result.getCompId(), result.getId(), null);
			//					setURL();
			//				}
			//
			//			});
		}
	}

	@Override
	public void publish(ITopTenList list) {
		clientFactory.getRpcService().publishTopTenList(list, new AsyncCallback<ITopTenList>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Publish failed. See server log for details.");

			}

			@Override
			public void onSuccess(ITopTenList result) {
				view.setList(result, _coreConfig.getBaseToptenUrl());				
			}


		});

	}

	@Override
	public void unpublish(ITopTenList list) {
		clientFactory.getRpcService().publishTopTenList(list, new AsyncCallback<ITopTenList>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Unpublish failed. See server log for details.");

			}

			@Override
			public void onSuccess(ITopTenList result) {
				view.setList(result, _coreConfig.getBaseToptenUrl());				
			}


		});

	}

	@Override
	public void promote(ITopTenList list) {
		// TODO Auto-generated method stub

	}

	@Override
	public void edit(final ITopTenList list) {
		clientFactory.getEditTTLInfo().setPresenter(this);
		clientFactory.getFeatureListView().expandView(true);
		clientFactory.getFeatureListView().editList(list);
		editing = true;
		//		loadWYSIWYGEditor("BODY:" );
		//		
		//		Timer t = new Timer()  {
		//			@Override
		//			public void run() {
		//				loadWYSIWYGEditor("BODY:" ); //+ list.getContent());
		//			}
		//		};
		//		
		//		t.schedule(3000);
	}

	@Override
	public void delete(ITopTenList list) {
		clientFactory.getRpcService().deleteTopTenList(list, new AsyncCallback<ITopTenList>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Publish failed. See server log for details.");

			}

			@Override
			public void onSuccess(ITopTenList result) {
				view.setList(result, _coreConfig.getBaseToptenUrl());				
			}


		});

	}


	@Override
	public void onLoginComplete(String destination) {
		if (place != null) {
			if (place.getListId() != null) {
				clientFactory.getRpcService().getTopTenList(place.getListId(), new AsyncCallback<ITopTenList>() {
					@Override
					public void onFailure(Throwable caught) {
						view.setList(null,"");
					}

					@Override
					public void onSuccess(final ITopTenList ttl) {
						LoginInfo login = Core.getCore().getClientFactory().getLoginInfo();

						view.setClientFactory(clientFactory);

						if (ttl != null) {
							view.setList(ttl, _coreConfig.getBaseToptenUrl());
						} else {
							Window.alert("Failed to fetch top ten list.");
							view.setList(null,"");
						}

						view.showContributorButtons(login.isTopTenContentContributor());
						view.showEditorButtons(login.isTopTenContentEditor());
					}	
				});
			}	
		}
	} 

	@Override
	public void saveTTLInfo(ITopTenList list) {
		clientFactory.getRpcService().saveTopTenList(list, new AsyncCallback<ITopTenList>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to save Top Ten List. See event log for details.");
			}

			@Override
			public void onSuccess(ITopTenList result) {
				clientFactory.getFeatureListView().expandView(false);
				view.setList(result, "");
				editing = false;
			}
		});		
	}



	@Override
	public void cancelEditTTLInfo(ITopTenList ttl) {
		clientFactory.getFeatureListView().expandView(false);
		view.setList(view.getList(), _coreConfig.getBaseToptenUrl());
		editing = false;
	}


	protected void setURL() {

		Document.get().setTitle(view.getList().getTitle());
		UrlBuilder builder = Window.Location.createUrlBuilder().setPath("/s/" + view.getList().getFeatureGuid());
		updateURLWithoutReloading(place.getToken(), view.getList().getTitle(), builder.buildString());
	}

	private static native void updateURLWithoutReloading(String token, String title, String newUrl) /*-{
		$wnd.history.replaceState(token, title, newUrl);
	}-*/;

	@Override
	public String mayStop() {
		if (editing) {
			return "Do you really want to leave this page? (you may have unsaved content!)";
		} else {
			return null;		
		}
	}

	@Override
	public void showFacebookComments(boolean show) {
		clientFactory.showFacebookComments(show);
		
	}
}