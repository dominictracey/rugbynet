package net.rugby.foundation.topten.client;

import java.util.List;

import net.rugby.foundation.core.client.Identity.Presenter;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.LoginInfo;
import net.rugby.foundation.topten.client.ui.NavBarView;
import net.rugby.foundation.topten.client.ui.NavBarViewImpl;
import net.rugby.foundation.topten.client.ui.content.ContentView;
import net.rugby.foundation.topten.client.ui.content.EditContent;
import net.rugby.foundation.topten.client.ui.toptenlistview.CompactTopTenListViewImpl;
import net.rugby.foundation.topten.client.ui.toptenlistview.EditTTIText;
import net.rugby.foundation.topten.client.ui.toptenlistview.EditTTLInfo;
import net.rugby.foundation.topten.client.ui.toptenlistview.TopTenListView;
import net.rugby.foundation.topten.client.ui.toptenlistview.TopTenListViewImpl;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.core.client.Identity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Sample implementation of {@link ClientFactory}.
 */
public class ClientFactoryImpl implements ClientFactory, Presenter {

	private static final EventBus eventBus = new SimpleEventBus();

	private static PlaceController placeController = null;
	private static  TopTenListView<ITopTenItem> listView = null;
	private static  TopTenListView<ITopTenItem> simpleView = null;
	private static ContentView contentView = null;
	private static EditContent editContent = null;
	private static TopTenListServiceAsync rpcService = null;
	private static EditTTIText editTTIText = null;
	private static NavBarView navBarView = null;
	private static EditTTLInfo editTTLInfo = null;
	private static Presenter identityPresenter = null;
	private static LoginInfo loginInfo = null;
	private static ICoreConfiguration coreConfig = null;
	private static List<IContent> contentList = null;

	@Override
	public EventBus getEventBus() {
		return eventBus;
	}
	@SuppressWarnings("deprecation")
	@Override
	public PlaceController getPlaceController() {
		if (placeController == null) {
			placeController = new PlaceController(eventBus);
		}
		return placeController;
	}

	@Override
	public TopTenListView<ITopTenItem> getListView() {
		if (listView == null) {
			listView = new TopTenListViewImpl();
		}
		return listView;
	}

	@Override
	public TopTenListServiceAsync getRpcService() {
		if (rpcService == null) {
			rpcService = GWT.create(TopTenListService.class);
		}
		return rpcService;
	}

	@Override
	public EditTTIText getEditTTITextDialog() {
		if (editTTIText == null) {
			editTTIText = new EditTTIText();
		}
		return editTTIText;
	}

	@Override
	public ContentView getContentView() {
		if (contentView == null) {
			contentView = new ContentView();
		}
		return contentView;
	}

	@Override
	public EditContent getEditContentDialog() {
		if (editContent == null) {
			editContent = new EditContent();
		}

		return editContent;
	}

	@Override
	public boolean isDualParamString() {
		return Location.getPath().contains("fb");
	}
	@Override
	public NavBarView getNavBarView() {
		if (navBarView == null) {
			navBarView = new NavBarViewImpl();
			RootPanel.get("navbar").add((NavBarViewImpl)navBarView);
			navBarView.setClientFactory(this);
		}
		return navBarView; 
	}
	@Override
	public EditTTLInfo getEditTTLInfoDialog() {
		if (editTTLInfo == null) {
			editTTLInfo = new EditTTLInfo();
		}
		return editTTLInfo;
	}
	@Override
	public void onLoginComplete(final String destination) {
		Core.getCore().login(new AsyncCallback<LoginInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				// ?
			}

			@Override
			public void onSuccess(LoginInfo result) {

				loginInfo = result;
				if (identityPresenter != null) {

					identityPresenter.onLoginComplete(destination);
				}
			}
		});

	}

	@Override
	public void doSetup(final AsyncCallback<ICoreConfiguration> cb) {
		final Identity.Presenter iPresenter = this;

		if (coreConfig == null) {
			Core.getInstance().getConfiguration(new AsyncCallback<ICoreConfiguration>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Hmm, something is wrong...");
				}

				@Override
				public void onSuccess(final ICoreConfiguration config) {
					final Identity i = Core.getCore().getClientFactory().getIdentityManager();		
					// where we keep the sign in/sign out
					if (i.getParent() == null) {
						i.setParent(getNavBarView().getLoginPanel());
						i.setPresenter(iPresenter);
					}
					Core.getCore().login(new AsyncCallback<LoginInfo>() {

						@Override
						public void onFailure(Throwable caught) {
							cb.onFailure(caught);
						}

						@Override
						public void onSuccess(LoginInfo result) {

							loginInfo = result;
							coreConfig = config;
							getNavBarView().setComps(coreConfig.getCompetitionMap(), coreConfig.getCompsUnderway());

							// set up content 
							getRpcService().getContentItems( new AsyncCallback<List<IContent>>() {



								@Override
								public void onFailure(Throwable caught) {
									cb.onFailure(caught);
								}

								@Override
								public void onSuccess(List<IContent> contentList) {
									ClientFactoryImpl.contentList = contentList;
									//getNavBarView().getButtonBar().clear();
									getNavBarView().setContent(contentList, loginInfo.isTopTenContentEditor());	
									cb.onSuccess(coreConfig);
								}

							});
						}
					});
				}
			});
		} else {
			cb.onSuccess(coreConfig);
		}
	}


	@Override
	public void RegisterIdentityPresenter(Presenter identityPresenter) {
		ClientFactoryImpl.identityPresenter = identityPresenter;
	}


	@Override
	public ICoreConfiguration getCoreConfig() {
		return coreConfig;
	}
	@Override
	public List<IContent> getContentList() {
		return contentList;
	}
	@Override
	public LoginInfo getLoginInfo() {
		return loginInfo;
	}
	@Override
	public TopTenListView<ITopTenItem> getSimpleView() {
		if (simpleView == null) {
			simpleView = new CompactTopTenListViewImpl();
			simpleView.setClientFactory(this);
		}
		return simpleView;
	}


}
