package net.rugby.foundation.topten.client.activity;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Anchor;
import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.core.client.Identity;
import net.rugby.foundation.core.client.Identity.Presenter;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.LoginInfo;
import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.place.TopTenListPlace;
import net.rugby.foundation.topten.client.ui.toptenlistview.EditTTIText.EditTTITextPresenter;
import net.rugby.foundation.topten.client.ui.toptenlistview.TopTenItemView;
import net.rugby.foundation.topten.client.ui.toptenlistview.TopTenListView;
import net.rugby.foundation.topten.client.ui.toptenlistview.TopTenListView.TopTenListViewPresenter;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.ITopTenItem;

public class TopTenListActivity extends AbstractActivity implements Presenter, EditTTITextPresenter, TopTenListViewPresenter  { 
	/**
	 * Used to obtain views, eventBus, placeController.
	 * Alternatively, could be injected via GIN.
	 */
	private ClientFactory clientFactory;
	private TopTenListPlace place;
	private ICoreConfiguration coreConfig;

	private TopTenListView<ITopTenItem> view;

	public TopTenListActivity(TopTenListPlace place, ClientFactory clientFactory) {

		this.clientFactory = clientFactory;
		view = clientFactory.getListView();

		if (place.getToken() != null) {
			this.place = place;
		}
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		panel.setWidget(view.asWidget());
		view.setPresenter(this);
		if (place != null) {
			if (place.getListId() != null) {
				clientFactory.getRpcService().getTopTenList(place.getListId(), new AsyncCallback<ITopTenList>() {
					@Override
					public void onFailure(Throwable caught) {
						// fail silently
						//Window.alert("Failed to fetch top ten list.");
					}

					@Override
					public void onSuccess(ITopTenList result) {
						view.setList(result);
						doSetup();
					}	
				});
			} else { // no listId
				// do we have a comp?
				if (place.getCompId() == null) {
					Core.getInstance().getConfiguration(new AsyncCallback<ICoreConfiguration>() {

						@Override
						public void onFailure(Throwable caught) {
							Logger.getLogger("Game1Activity").log(Level.SEVERE, "start.getConfiguration: " + caught.getMessage(), caught);
						}

						@Override
						public void onSuccess(final ICoreConfiguration config) {
							coreConfig = config;
							TopTenListPlace newPlace = new TopTenListPlace();
							newPlace.setCompId(coreConfig.getDefaultCompId());
							clientFactory.getPlaceController().goTo(newPlace);
//							showListForComp(coreConfig.getDefaultCompId());
//							view.setComps(coreConfig.getCompetitionMap()); // do this twice by calling doSetup?
//							doSetup();

						}
					});
				} else {
					// just use the comp from place
					showListForComp(place.getCompId());
				}
			}
		}
	}

	private void showListForComp(Long compId) {
		clientFactory.getRpcService().getLatestForComp(compId, new AsyncCallback<ITopTenList>() {
			@Override
			public void onFailure(Throwable caught) {
				// fail silently
				//Window.alert("Failed to fetch top ten list.");
			}

			@Override
			public void onSuccess(ITopTenList result) {
				view.setList(result);
				doSetup();
			}	
		});
	}

	private void doSetup() {

		final Identity i = Core.getCore().getClientFactory().getIdentityManager();		
		// where we keep the sign in/sign out
		if (i.getParent() == null) {
			view.setClientFactory(clientFactory);
			i.setParent(view.getLoginPanel());
			i.setPresenter(this);

			Core.getCore().login(new AsyncCallback<LoginInfo>() {

				@Override
				public void onFailure(Throwable caught) {
					//dealt with in the Core
				}

				@Override
				public void onSuccess(LoginInfo result) {

					//TODO take out for upstream environments
					//								if (true)
					i.doLoginDev("editor@test.com", "asdasd");
					//								else  // this is kicked off at the end of the login process as well, so when we auto-login the test user we don't need to do it.
					Logger.getLogger("").log(Level.FINE, this.getClass().toString() + "login sucessful, ready to handleHistory");

				}

			});		


			if (coreConfig == null) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {    
					@Override
					public void execute() {
						Core.getInstance().getConfiguration(new AsyncCallback<ICoreConfiguration>() {

							@Override
							public void onFailure(Throwable caught) {
								Logger.getLogger("Game1Activity").log(Level.SEVERE, "start.getConfiguration: " + caught.getMessage(), caught);
							}

							@Override
							public void onSuccess(final ICoreConfiguration config) {
								coreConfig = config;
								view.setComps(coreConfig.getCompetitionMap());

							}
						});
					}
				});
			}
		} else {
			refreshButtons();
		}
	}




	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.Identity.Presenter#onLoginComplete()
	 */
	@Override
	public void onLoginComplete(String destination) {

		refreshButtons();
	}

	private void refreshButtons() {
		//Logger.getLogger("").log(Level.FINE, this.getClass().toString() + "onLoginComplete callback with destination: " + destination);
		LoginInfo login = Core.getCore().getClientFactory().getLoginInfo();
		view.getButtonBar().clear();
		if (view.getItemViews() != null) {

			// next and prev buttons
			if (login == null || login.isLoggedIn() == false || (login.isTopTenContentContributor() == false && login.isTopTenContentEditor() == false)) {
				view.hasNext(view.getList().getNextPublishedId() != null);
				view.hasPrev(view.getList().getPrevPublishedId() != null);
			} else {
				view.hasNext(view.getList().getNextId() != null);
				view.hasPrev(view.getList().getPrevId() != null);
			}

			Iterator<?> it = view.getItemViews().iterator();


			if (login != null) {
				boolean allSubmitted = true;
				while (it.hasNext()) {
					final TopTenItemView v = (TopTenItemView) it.next();
					v.getButtonBar().clear();
					if (!v.getItem().isSubmitted()) {
						allSubmitted = false;
					}
				}

				// Editor buttons
				if (login.isTopTenContentEditor()) {				
					Button publish;
					if (view.getList().getPublished() != null) {
						publish = new Button("Unpublish");
					} else {
						publish = new Button("Publish");
					}
					if (allSubmitted) {
						publish.setType(ButtonType.DANGER);
					} else if (view.getList().getLive().equals(true)) {
						publish.setEnabled(false);
					}
					view.getButtonBar().add(publish);
					publish.addClickHandler( new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							publishTTL(view);
						}



					});
					Button delete = new Button("Delete");
					delete.setType(ButtonType.DANGER);
					view.getButtonBar().add(delete);
					delete.addClickHandler( new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							deleteTTL(view);
						}




					});
				}

				// Content Contributor buttons
				it = view.getItemViews().iterator();
				while (it.hasNext()) {
					final TopTenItemView v = (TopTenItemView) it.next();
					if (login.isTopTenContentContributor()) {
						//Badge edit = new Badge("Edit");				
						Button edit = new Button("Edit");
						if (!v.getItem().isSubmitted()) {
							if (v.getItem().getText() == null || v.getItem().getText().isEmpty()) {
								edit.setType(ButtonType.PRIMARY);
							}
							edit.addClickHandler( new ClickHandler() {

								@Override
								public void onClick(ClickEvent event) {
									editTTI(v);
								}

							});
						} else {
							// have to retract before editing
							edit.setEnabled(false);
						}
						v.getButtonBar().add(edit);

						// Submit/Retract
						Button submit;
						if (v.getItem().isSubmitted()) {
							submit = new Button("Retract");
							submit.setType(ButtonType.WARNING);
							// if already published can't retract
							if (view.getList().getLive().equals(true)) { 
								submit.setEnabled(false);
							}
						} else {
							submit = new Button("Submit");					
							if (v.getItem().getText() != null && !v.getItem().getText().isEmpty()) {
								submit.setType(ButtonType.PRIMARY);
							}
						}

						submit.addClickHandler( new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								submitTTI(v);
							}


						});
						v.getButtonBar().add(submit);

						// Match Report
						Anchor report = new Anchor("Match Report");
						report.setHref(v.getItem().getMatchReportLink());
						report.setTarget("blank");
						v.getButtonBar().add(report);
					}
				}
			}
		}
	}

	private void editTTI(TopTenItemView edit) {
		clientFactory.getEditTTITextDialog().setPresenter(this);
		clientFactory.getEditTTITextDialog().showTTI(edit);	
		clientFactory.getEditTTITextDialog().center();
		clientFactory.getEditTTITextDialog().show();
	}


	private void submitTTI(final TopTenItemView v) {
		clientFactory.getRpcService().submitTopTenItem(v.getItem(), new AsyncCallback<ITopTenItem>() {
			@Override
			public void onFailure(Throwable caught) {
				String name = "no name returned";
				if (v != null && v.getItem() != null && v.getItem().getPlayer() != null && v.getItem().getPlayer().getDisplayName() != null) {
					name = v.getItem().getPlayer().getDisplayName();
				}
				Window.alert("Failed to submit/retract Top Ten Item for " + name + ". See event log for details.");
			}

			@Override
			public void onSuccess(ITopTenItem result) {
				v.setItem(result, v.getIndex());
				view.getList().getList().set(v.getIndex(), result);  // if we don't do this the references diverge
				refreshButtons();
			}
		});	
	}

	private void publishTTL(final TopTenListView<ITopTenItem> list) {
		clientFactory.getRpcService().publishTopTenList(list.getList(), new AsyncCallback<ITopTenList>() {
			@Override
			public void onFailure(Throwable caught) {
				String name = "no name returned";
				if (list != null && list.getList() != null && list.getList().getTitle() != null) {
					name = list.getList().getTitle();
				}
				Window.alert("Failed to publish/retract Top Ten List \"" + name + "\". See event log for details.");
			}

			@Override
			public void onSuccess(ITopTenList result) {
				list.setList(result);
				refreshButtons();
			}
		});	}


	private void deleteTTL(final TopTenListView<ITopTenItem> list) {
		clientFactory.getRpcService().deleteTopTenList(list.getList(), new AsyncCallback<ITopTenList>() {
			@Override
			public void onFailure(Throwable caught) {
				String name = "no name returned";
				if (list != null && list.getList() != null && list.getList().getTitle() != null) {
					name = list.getList().getTitle();
				}
				Window.alert("Failed to delete Top Ten List \"" + name + "\". See event log for details.");
			}

			@Override
			public void onSuccess(ITopTenList result) {
				list.setList(result);
			}
		});
	}

	@Override
	public void saveTTIText(final TopTenItemView v) {
		clientFactory.getRpcService().saveTopTenItem(v.getItem(), new AsyncCallback<ITopTenItem>() {
			@Override
			public void onFailure(Throwable caught) {
				String name = "no name returned";
				if (v != null && v.getItem() != null && v.getItem().getPlayer() != null && v.getItem().getPlayer().getDisplayName() != null) {
					name = v.getItem().getPlayer().getDisplayName();
				}
				Window.alert("Failed to save Top Ten Item for " + name + ". See event log for details.");
			}

			@Override
			public void onSuccess(ITopTenItem result) {
				v.setItem(result,v.getIndex());
				view.getList().getList().set(v.getIndex(), result);  // if we don't do this the references diverge
				clientFactory.getEditTTITextDialog().hide();
			}
		});
	}

	@Override
	public void cancelTTITextEdit(TopTenItemView v) {
		clientFactory.getEditTTITextDialog().hide();
	}


	@Override
	public void showNext() {
		LoginInfo login = Core.getCore().getClientFactory().getLoginInfo();
		if (login.isTopTenContentContributor() || login.isTopTenContentEditor()) {
			clientFactory.getPlaceController().goTo(new TopTenListPlace(view.getList().getNextId()));
		} else {
			clientFactory.getPlaceController().goTo(new TopTenListPlace(view.getList().getNextPublishedId()));
		}

	}

	@Override
	public void showPrev() {
		LoginInfo login = Core.getCore().getClientFactory().getLoginInfo();
		if (login.isTopTenContentContributor() || login.isTopTenContentEditor()) {
			clientFactory.getPlaceController().goTo(new TopTenListPlace(view.getList().getPrevId()));
		} else {
			clientFactory.getPlaceController().goTo(new TopTenListPlace(view.getList().getPrevPublishedId()));
		}	}
}