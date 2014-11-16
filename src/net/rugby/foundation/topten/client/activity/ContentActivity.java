package net.rugby.foundation.topten.client.activity;

import java.util.Iterator;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.core.client.Identity.Presenter;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.LoginInfo;
import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.place.ContentPlace;
import net.rugby.foundation.topten.client.ui.content.ContentView;
import net.rugby.foundation.topten.client.ui.content.EditContent;
import net.rugby.foundation.topten.client.ui.content.EditContent.EditContentPresenter;

public class ContentActivity extends AbstractActivity implements EditContentPresenter, Presenter { 
	private ContentPlace place;
	private ContentView view;
	private ClientFactory clientFactory;
	
	public ContentActivity(ContentPlace place, ClientFactory clientFactory) {

		this.clientFactory = clientFactory;
		view = clientFactory.getContentView();

		if (place.getToken() != null) {
			this.place = place;
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
		
//		Core.getCore().getContent(place.getContentId(), new AsyncCallback<IContent>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				// fail silently
//				//Window.alert("Failed to fetch top ten list.");
//			}
//
//			@Override
//			public void onSuccess(final IContent result) {
				//linear search ick
				Iterator<IContent> it =  clientFactory.getContentList().iterator();
				boolean found = false;
				IContent result = null;
				while (it.hasNext()) {
					result = it.next();
					if (result.getId().equals(place.getContentId())) {
						found = true;
						break;
					}
				}
				
				if (found) {
					view.setContent(result);
				}
				
				final IContent content = result;
				LoginInfo login = clientFactory.getLoginInfo();
				view.getButtonBar().clear();
//				clientFactory.getHeaderView().collapseHero(true);
				Element loadPanel = DOM.getElementById("loadPanel");
				if (loadPanel != null && loadPanel.hasParentElement()) {
					loadPanel.removeFromParent();
				}
				if (login != null && login.isTopTenContentEditor()) {
					Button edit = new Button("Edit");
				
					edit.setType(ButtonType.DANGER);

					view.getButtonBar().add(edit);
					edit.addClickHandler( new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							editContent(content);
						}

					});
				}
			}	
		});
		
	}
	

	private void editContent(IContent result) {
		EditContent ec = clientFactory.getEditContentDialog();
		ec.setContent(result, this);
		ec.center();
		
	}

	@Override
	public void saveContent(IContent content) {
		Core.getCore().saveContent(content, new AsyncCallback<IContent>() {
			@Override
			public void onFailure(Throwable caught) {
				// fail silently
				//Window.alert("Failed to fetch top ten list.");
			}

			@Override
			public void onSuccess(final IContent result) {
				view.setContent(result);
				clientFactory.getEditContentDialog().hide();
			}
		});
	}

	@Override
	public void cancelEditContent() {
		clientFactory.getEditContentDialog().hide();
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.Identity.Presenter#onLoginComplete()
	 */
	@Override
	public void onLoginComplete(String destination) {
		clientFactory.getPlaceController().goTo(place);
	}
}