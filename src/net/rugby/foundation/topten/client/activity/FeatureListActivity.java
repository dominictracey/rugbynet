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

		clientFactory.doSetup(new AsyncCallback<ICoreConfiguration>() {


			@Override
			public void onFailure(Throwable caught) {
				// suffer in silence
			}

			@Override
			public void onSuccess(final ICoreConfiguration coreConfig) {
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
									view.setList(ttl, coreConfig.getBaseToptenUrlForFacebook());
								} else {
									Window.alert("Failed to fetch top ten list.");
									view.setList(null,"");
								}

								refreshButtons(login, ttl);
								setURL();

							}

						});
					} else { // no listId
						// do we have a comp?
						if (place.getCompId() == null) {
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
					clientFactory.setTTLName(ttl.getNextId(), view.getPrevLabel());
				}
				view.hasNext(ttl.getNextId() != null);
				view.hasPrev(ttl.getPrevId() != null);
			} else {
				if (ttl.getNextPublishedId() != null) {
					clientFactory.setTTLName(ttl.getNextId(), view.getNextLabel());
				}
				if (ttl.getPrevPublishedId() != null) {
					clientFactory.setTTLName(ttl.getNextId(), view.getPrevLabel());
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
		if (Core.getCore().getCurrentCompId() != compId) {
			Core.getCore().setCurrentCompId(compId);
		}
		
		clientFactory.getRpcService().getLatestListIdForComp(compId, new AsyncCallback<Long>() {
			@Override
			public void onFailure(Throwable caught) {
				// fail silently
				//Window.alert("Failed to fetch top ten list.");
			}

			@Override
			public void onSuccess(Long result) {

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
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

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
							view.setList(ttl, _coreConfig.getBaseToptenUrlForFacebook());
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
			}
		});		
	}



	@Override
	public void cancelEditTTLInfo(ITopTenList ttl) {
		clientFactory.getFeatureListView().expandView(false);
		view.setList(view.getList(), _coreConfig.getBaseToptenUrl());
	}


	protected void setURL() {

		Document.get().setTitle(view.getList().getTitle());
		UrlBuilder builder = Window.Location.createUrlBuilder().setPath("/s/" + view.getList().getFeatureGuid());
		updateURLWithoutReloading(place.getToken(), view.getList().getTitle(), builder.buildString());
	}

	private static native void updateURLWithoutReloading(String token, String title, String newUrl) /*-{
		$wnd.history.replaceState(token, title, newUrl);
	}-*/;

	//	@Override
	//	public void start(AcceptsOneWidget panel, EventBus eventBus) {
	//		final TopTenListViewPresenter This = this;
	//		panel.setWidget(view.asWidget());
	//		final AcceptsOneWidget Panel = panel;
	//		view.setClientFactory(clientFactory);
	//		view.setPresenter(this);
	//		//final Identity.Presenter identityPresenter = this;
	//		clientFactory.RegisterIdentityPresenter(this);
	//
	//		clientFactory.doSetup(new AsyncCallback<ICoreConfiguration>() {
	//
	//
	//			@Override
	//			public void onFailure(Throwable caught) {
	//				// suffer in silence
	//			}
	//
	//			@Override
	//			public void onSuccess(final ICoreConfiguration coreConfig) {
	//				_coreConfig = coreConfig;
	//				if (place != null) {
	//					if (place.getListId() != null) {
	//						clientFactory.getRpcService().getTopTenList(place.getListId(), new AsyncCallback<ITopTenList>() {
	//							@Override
	//							public void onFailure(Throwable caught) {
	//								// fail silently
	//								//Window.alert("Failed to fetch top ten list.");
	//							}
	//
	//							@Override
	//							public void onSuccess(final ITopTenList ttl) {
	//								if (ttl != null) {
	//									LoginInfo login = Core.getCore().getClientFactory().getLoginInfo();
	//									// are we simple list or feature?
	//									boolean simple = true;
	//									if (login.isTopTenContentContributor() || login.isTopTenContentEditor()) {
	//										simple = false;
	//									} 
	//
	//									//									else {
	//									//	 									Iterator<ITopTenItem> it = ttl.getList().iterator();
	//									//										while (it.hasNext())  {
	//									//											ITopTenItem i = it.next();
	//									//											if (i.getText() != null && !i.getText().isEmpty()) {
	//									//												simple = false; 
	//									//												break;
	//									//											}
	//									//										}
	//									//									}
	//
	//									if (!simple) {
	//										view.setItemCount(0);
	//										//clientFactory.getHeaderView().collapseHero(false);
	//										if ( clientFactory.getLoginInfo().isTopTenContentEditor()) {
	//											clientFactory.getHeaderView().getButtonBar().clear();
	//										}
	//										clientFactory.getHeaderView().setContent(clientFactory.getContentList(), clientFactory.getLoginInfo().isTopTenContentEditor());
	//										view.setList(ttl, coreConfig.getBaseToptenUrlForFacebook());
	//									} else {
	//										view = clientFactory.getSimpleView();
	//										view.setPresenter(This);
	//										view.setClientFactory(clientFactory);
	//										Panel.setWidget(view.asWidget());
	//										//clientFactory.getHeaderView().collapseHero(false);
	//										if (clientFactory.getLoginInfo().isTopTenContentEditor())
	//											clientFactory.getHeaderView().getButtonBar().clear();
	//										clientFactory.getHeaderView().setContent(clientFactory.getContentList(), clientFactory.getLoginInfo().isTopTenContentEditor());
	//										view.setList(ttl, coreConfig.getBaseToptenUrlForFacebook());
	//									}
	//								} else {
	//									Window.alert("Failed to fetch top ten list.");
	//								}
	//
	//							}	
	//						});
	//					} else { // no listId
	//						// do we have a comp?
	//						if (place.getCompId() == null) {
	//							FeatureListPlace newPlace = new FeatureListPlace();
	//							newPlace.setCompId(coreConfig.getDefaultCompId());
	//							if (coreConfig.getDefaultCompId() == null) {
	//								throw new RuntimeException("No default configuration defined for site.");
	//							}
	//							clientFactory.getPlaceController().goTo(newPlace);
	//						} else {
	//							// just use the comp from place
	//							showListForComp(place.getCompId());
	//						}
	//					}
	//				}
	//			}
	//		});
	//
	//	}
	//
	//	
	//
	//
	//	/* (non-Javadoc)
	//	 * @see net.rugby.foundation.core.client.Identity.Presenter#onLoginComplete()
	//	 */
	//	@Override
	//	public void onLoginComplete(String destination) {
	//		clientFactory.getPlaceController().goTo(place);
	//	}
	//
	//	private void refreshButtons() {
	//
	//		LoginInfo login = Core.getCore().getClientFactory().getLoginInfo();
	//		if (view.getItemViews() != null && view.getList() != null) {
	//
	//			// next and prev buttons
	//			if (login == null || login.isLoggedIn() == false || (login.isTopTenContentContributor() == false && login.isTopTenContentEditor() == false)) {
	//				view.hasNext(view.getList().getNextPublishedId() != null);
	//				view.hasPrev(view.getList().getPrevPublishedId() != null);
	//			} else {
	//				view.hasNext(view.getList().getNextId() != null);
	//				view.hasPrev(view.getList().getPrevId() != null);
	//			}
	//
	//			Iterator<TopTenItemView> it = view.getItemViews().iterator();
	//
	//
	//			if (login != null) {
	//				boolean allSubmitted = true;
	//				while (it.hasNext()) {
	//					final TopTenItemView v = it.next();
	//					//v.getButtonBar().clear();
	//					if (!v.getItem().isSubmitted()) {
	//						allSubmitted = false;
	//					}
	//				}
	//
	//				// Editor buttons
	//				if (login.isTopTenContentEditor()) {				
	//					Button publish;
	//					if (view.getList().getPublished() != null) {
	//						publish = new Button("Unpublish");
	//					} else {
	//						publish = new Button("Publish");
	//					}
	//					if (allSubmitted) {
	//						publish.setType(ButtonType.DANGER);
	//					} else /*if (view.getList().getLive().equals(true))*/ {
	//						publish.setEnabled(false);
	//					}
	//					clientFactory.getHeaderView().getButtonBar().add(publish);
	//					publish.addClickHandler( new ClickHandler() {
	//
	//						@Override
	//						public void onClick(ClickEvent event) {
	//							publishTTL(view);
	//						}
	//
	//
	//
	//					});
	//					Button delete = new Button("Delete");
	//					delete.setType(ButtonType.DANGER);
	//					clientFactory.getHeaderView().getButtonBar().add(delete);
	//					delete.addClickHandler( new ClickHandler() {
	//
	//						@Override
	//						public void onClick(ClickEvent event) {
	//							deleteTTL(view);
	//						}
	//
	//					});
	//
	//					Button edit = new Button("Edit TTL Info");
	//					edit.setType(ButtonType.DANGER);
	//					clientFactory.getHeaderView().getButtonBar().add(edit);
	//					edit.addClickHandler( new ClickHandler() {
	//
	//						@Override
	//						public void onClick(ClickEvent event) {
	//							editTTL(view);
	//						}
	//
	//					});
	//				}
	//			}
	//		}
	//	}
	//
	//	private void editTTI(TopTenItemView edit) {
	//		clientFactory.getEditTTITextDialog().setPresenter(this);
	//		clientFactory.getEditTTITextDialog().showTTI(edit);	
	//		clientFactory.getEditTTITextDialog().center();
	//		clientFactory.getEditTTITextDialog().show();
	//	}
	//
	//
	//	private void submitTTI(final TopTenItemView v) {
	//		clientFactory.getRpcService().submitTopTenItem(v.getItem(), new AsyncCallback<ITopTenItem>() {
	//			@Override
	//			public void onFailure(Throwable caught) {
	//				String name = "no name returned";
	//				if (v != null && v.getItem() != null && v.getItem().getPlayer() != null && v.getItem().getPlayer().getDisplayName() != null) {
	//					name = v.getItem().getPlayer().getDisplayName();
	//				}
	//				Window.alert("Failed to submit/retract Top Ten Item for " + name + ". See event log for details.");
	//			}
	//
	//			@Override
	//			public void onSuccess(ITopTenItem result) {
	//
	//				v.setItem(result, v.getIndex(), result.getPlayerId(), clientFactory.getCoreConfig().getBaseToptenUrlForFacebook());
	//				view.getList().getList().set(v.getIndex(), result);  // if we don't do this the references diverge
	//
	//				setTTIButtons(v); // admin buttons
	//				parse(v); //fb like
	//
	//			}
	//		});	
	//	}
	//
	//	private void publishTTL(final TopTenListView<ITopTenItem> list) {
	//		clientFactory.getRpcService().publishTopTenList(list.getList(), new AsyncCallback<ITopTenList>() {
	//			@Override
	//			public void onFailure(Throwable caught) {
	//				String name = "no name returned";
	//				if (list != null && list.getList() != null && list.getList().getTitle() != null) {
	//					name = list.getList().getTitle();
	//				}
	//				Window.alert("Failed to publish/retract Top Ten List \"" + name + "\". See event log for details.");
	//			}
	//
	//			@Override
	//			public void onSuccess(ITopTenList result) {
	//				list.setList(result, clientFactory.getCoreConfig().getBaseToptenUrlForFacebook());
	//				clientFactory.getHeaderView().getButtonBar().clear();
	//				clientFactory.getHeaderView().setContent(clientFactory.getContentList(), clientFactory.getLoginInfo().isTopTenContentEditor());
	//				//refreshButtons();
	//			}
	//		});	}
	//
	//
	//	private void deleteTTL(final TopTenListView<ITopTenItem> list) {
	//		clientFactory.getRpcService().deleteTopTenList(list.getList(), new AsyncCallback<ITopTenList>() {
	//			@Override
	//			public void onFailure(Throwable caught) {
	//				String name = "no name returned";
	//				if (list != null && list.getList() != null && list.getList().getTitle() != null) {
	//					name = list.getList().getTitle();
	//				}
	//				Window.alert("Failed to delete Top Ten List \"" + name + "\". See event log for details.");
	//			}
	//
	//			@Override
	//			public void onSuccess(ITopTenList result) {
	//				list.setList(result, clientFactory.getCoreConfig().getBaseToptenUrlForFacebook());
	//			}
	//		});
	//	}
	//
	//
	//
	//	@Override
	//	public void saveTTIText(final TopTenItemView v) {
	//		clientFactory.getRpcService().saveTopTenItem(v.getItem(), new AsyncCallback<ITopTenItem>() {
	//			@Override
	//			public void onFailure(Throwable caught) {
	//				String name = "no name returned";
	//				if (v != null && v.getItem() != null && v.getItem().getPlayer() != null && v.getItem().getPlayer().getDisplayName() != null) {
	//					name = v.getItem().getPlayer().getDisplayName();
	//				}
	//				Window.alert("Failed to save Top Ten Item for " + name + ". See event log for details.");
	//			}
	//
	//			@Override
	//			public void onSuccess(ITopTenItem result) {
	//				v.setItem(result,v.getIndex(), result.getPlayerId(), clientFactory.getCoreConfig().getBaseToptenUrlForFacebook());
	//				view.getList().getList().set(v.getIndex(), result);  // if we don't do this the references diverge
	//				clientFactory.getEditTTITextDialog().hide();
	//			}
	//		});
	//	}
	//
	//	@Override
	//	public void cancelTTITextEdit(TopTenItemView v) {
	//		clientFactory.getEditTTITextDialog().hide();
	//	}
	//
	//
	//	@Override
	//	public void showNext() {
	//		LoginInfo login = Core.getCore().getClientFactory().getLoginInfo();
	//		if (login.isTopTenContentContributor() || login.isTopTenContentEditor()) {
	//			clientFactory.getPlaceController().goTo(new FeatureListPlace(view.getList().getNextId()));
	//		} else {
	//			clientFactory.getPlaceController().goTo(new FeatureListPlace(view.getList().getNextPublishedId()));
	//		}
	//
	//	}
	//
	//	@Override
	//	public void showPrev() {
	//		LoginInfo login = Core.getCore().getClientFactory().getLoginInfo();
	//		if (login.isTopTenContentContributor() || login.isTopTenContentEditor()) {
	//			clientFactory.getPlaceController().goTo(new FeatureListPlace(view.getList().getPrevId()));
	//		} else {
	//			clientFactory.getPlaceController().goTo(new FeatureListPlace(view.getList().getPrevPublishedId()));
	//		}	}
	//
	//	@Override
	//	public void setTTIButtons(final TopTenItemView v) {
	//		LoginInfo login = Core.getCore().getClientFactory().getLoginInfo();
	//
	//		view.setItemCount(view.getItemCount() +1);
	//
	//		//v.getButtonBar().clear();
	//		if (login.isTopTenContentContributor()) {
	//			//Badge edit = new Badge("Edit");				
	//			Button edit = new Button("Edit");
	//			if (!v.getItem().isSubmitted()) {
	//				if (v.getItem().getText() == null || v.getItem().getText().isEmpty()) {
	//					edit.setType(ButtonType.PRIMARY);
	//				}
	//				edit.addClickHandler( new ClickHandler() {
	//
	//					@Override
	//					public void onClick(ClickEvent event) {
	//						editTTI(v);
	//					}
	//
	//				});
	//			} else {
	//				// have to retract before editing
	//				edit.setEnabled(false);
	//			}
	//			//v.getButtonBar().add(edit);
	//
	//			// Submit/Retract
	//			Button submit;
	//			if (v.getItem().isSubmitted()) {
	//				submit = new Button("Retract");
	//				submit.setType(ButtonType.WARNING);
	//				// if already published can't retract
	//				if (view.getList().getLive().equals(true)) { 
	//					submit.setEnabled(false);
	//				}
	//			} else {
	//				submit = new Button("Submit");					
	//				if (v.getItem().getText() != null && !v.getItem().getText().isEmpty()) {
	//					submit.setType(ButtonType.PRIMARY);
	//				}
	//			}
	//
	//			submit.addClickHandler( new ClickHandler() {
	//
	//				@Override
	//				public void onClick(ClickEvent event) {
	//					submitTTI(v);
	//				}
	//
	//
	//			});
	//			//v.getButtonBar().add(submit);
	//
	//			// Match Report
	//			Anchor report = new Anchor("Match Report");
	//			report.setHref(v.getItem().getMatchReportLink());
	//			report.setTarget("blank");
	//			//v.getButtonBar().add(report);
	//		} 
	//		//		else {
	//		//			v.getButtonBar().clear();
	//		//		}
	//
	//		// FB like for the whole list once all the items are set
	//		if (view.getItemCount()>9) {
	//			//setFBListLike(view.getList(),clientFactory.getCoreConfig().getBaseToptenUrlForFacebook());
	//			refreshButtons();
	//			view.setItemCount(0);
	//			Element loadPanel = DOM.getElementById("loadPanel");
	//			if (loadPanel != null && loadPanel.hasParentElement()) {
	//				loadPanel.removeFromParent();
	//			}
	//		}
	//
	//	}
	//
	//	@Override
	//	public void setFBListLike(ITopTenList list, String baseUrl) {
	//		// set the fbLike div property
	//		// data-href="http://dev.rugby.net/topten.html#List:listId=159002" 
	//		Element e = Document.get().getElementById("fbListLike");
	//		if (e != null) {
	//			Element oldChild = e.getFirstChildElement();
	//			if (oldChild != null) {
	//				e.removeChild(oldChild);
	//			}
	//
	//
	//			// so we have to include the specifics both in the GET query parameters and the hash fragment to support the FB linting. See:
	//			// https://developers.facebook.com/tools/debug/og/object?q=http%3A%2F%2Fdev.rugby.net%2Ffb%2Ftopten.html%3FlistId%3D159002%26playerId%3D148002%23List%3AlistId%3D159002%26playerId%3D148002
	//
	//			//String encodedUrl= URL.encode(baseUrl + "?listId=" + list.getId() + "#List:listId=" + list.getId());
	//
	//			HTML fb = new HTML("<br/><div class=\"fb-like\" id=\"fbListLike\" data-width=\"450\" data-layout=\"button_count\" data-show-faces=\"true\" data-send=\"true\" data-href=\"http://facebook.com/therugbynet\"></div>");
	//
	//			e.appendChild(fb.getElement());
	//			parse("fbListLike");
	//		}
	//	}
	//
	//	/**
	//	 * Wrapper method
	//	 * @see http://developers.facebook.com/docs/reference/javascript/FB.XFBML.parse
	//	 */
	//	public static native void parse () /*-{
	//            $wnd.FB.XFBML.parse();
	//    }-*/;
	//
	//	/**
	//	 * Wrapper method
	//	 * @widget widget to parse
	//	 */
	//	@Override
	//	public void parse (Widget widget) {
	//		parse(widget.getElement().getId());
	//	};
	//
	//	/**
	//	 * Wrapper method
	//	 * @see http://developers.facebook.com/docs/reference/javascript/FB.XFBML.parse
	//	 */
	//	public static native void parse (String domelementid) /*-{
	//            $wnd.FB.XFBML.parse(document.getElementById('domelementid'));
	//    }-*/;
	//
	//	private void editTTL(final TopTenListView<ITopTenItem> list) {
	//		clientFactory.getEditTTLInfoDialog().setPresenter(this);
	//		clientFactory.getEditTTLInfoDialog().showTTL(list.getList());
	//		clientFactory.getEditTTLInfoDialog().center();
	//	}
	//
	//
	//
	//	@Override
	//	public void saveTTLInfo(ITopTenList list) {
	//		clientFactory.getRpcService().saveTopTenList(list, new AsyncCallback<ITopTenList>() {
	//			@Override
	//			public void onFailure(Throwable caught) {
	//				Window.alert("Failed to save Top Ten List. See event log for details.");
	//			}
	//
	//			@Override
	//			public void onSuccess(ITopTenList result) {
	//				//clientFactory.getHeaderView().setHeroListInfo(result.getTitle(), result.getContent());
	//				clientFactory.getEditTTLInfoDialog().hide();
	//			}
	//		});		
	//	}
	//
	//	@Override
	//	public void cancelEditTTLInfo(ITopTenList ttl) {
	//		clientFactory.getEditTTLInfoDialog().hide();
	//	}
	//
	//	@Override
	//	public void showRatingDetails(final ITopTenItem value) {
	//		++detailCount;
	//		clientFactory.getRpcService().getPlayerRating(value.getPlayerRatingId(), new AsyncCallback<IPlayerRating>() {
	//			@Override
	//			public void onFailure(Throwable caught) {
	//				Window.alert("Failed to save Top Ten List. See event log for details.");
	//			}
	//
	//			@Override
	//			public void onSuccess(IPlayerRating result) {
	//				clientFactory.getRatingPopup().setRating(result);
	//				clientFactory.getRatingPopup().center();
	//				recordAnalyticsEvent("ratingDetails", "click", result.getPlayer().getShortName(), 1);
	//			}
	//		});	
	//
	//
	//	}
	//
	//	@Override
	//	public String mayStop()
	//	{
	//		if (view != null && view.getList() != null && view.getList().getTitle() != null) {
	//			recordAnalyticsEvent("ratingDetailsCount", "click", view.getList().getTitle(), detailCount);
	//		}
	//		return null;
	//	}
	//

}