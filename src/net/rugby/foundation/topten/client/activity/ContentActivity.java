package net.rugby.foundation.topten.client.activity;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.LoginInfo;
import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.place.ContentPlace;
import net.rugby.foundation.topten.client.ui.content.ContentView;
import net.rugby.foundation.topten.client.ui.content.EditContent;
import net.rugby.foundation.topten.client.ui.content.EditContent.EditContentPresenter;

public class ContentActivity extends AbstractActivity implements EditContentPresenter { 
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

		Core.getCore().getContent(place.getContentId(), new AsyncCallback<IContent>() {
			@Override
			public void onFailure(Throwable caught) {
				// fail silently
				//Window.alert("Failed to fetch top ten list.");
			}

			@Override
			public void onSuccess(final IContent result) {
				view.setContent(result);
				LoginInfo login = Core.getCore().getClientFactory().getLoginInfo();
				view.getButtonBar().clear();
				if (login != null && login.isTopTenContentEditor()) {
					Button edit = new Button("Edit");
				
					edit.setType(ButtonType.DANGER);

					view.getButtonBar().add(edit);
					edit.addClickHandler( new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							editContent(result);
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

}