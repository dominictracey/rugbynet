package net.rugby.foundation.admin.client.activity;

import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HTML;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.place.PromotePlace;
import net.rugby.foundation.admin.client.ui.AdminView;
import net.rugby.foundation.admin.client.ui.SmartBar;
import net.rugby.foundation.admin.client.ui.promote.PromoteView;
import net.rugby.foundation.admin.client.ui.promote.PromoteView.PromoteViewPresenter;
import net.rugby.foundation.admin.client.ui.seriesconfiguration.SeriesConfigPopupView.Presenter;
import net.rugby.foundation.admin.shared.IBlurb;

import org.gwtbootstrap3.extras.bootbox.client.Bootbox;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

public class PromoteActivity extends AbstractActivity implements  
AdminView.Presenter,  
SmartBar.Presenter,
SmartBar.PromotePresenter,
PromoteViewPresenter<IBlurb>
, Presenter<IBlurb>
{ 
	/**
	 * Used to obtain views, eventBus, placeController.
	 * Alternatively, could be injected via GIN.
	 */
	private ClientFactory clientFactory;
	private PromotePlace place;
	SelectionModel<IBlurb> selectionModel;
	int index; 
	private PromoteView<IBlurb> view;
	private MenuItemDelegate menuItemDelegate;
	protected Timer t = null;

	public PromoteActivity(PromotePlace place, ClientFactory clientFactory) {
		selectionModel = new SelectionModel<IBlurb>();

		this.clientFactory = clientFactory;
		view = clientFactory.getPromoteView();
		
		this.place = place;
		
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {

		view.setPresenter(this);
		panel.setWidget(view.asWidget());

		if (place != null) {
			clientFactory.getRpcService().getAllBlurbs(place.getActive(), new AsyncCallback<List<IBlurb>>() {
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Failed to fetch blurb list");
				}

				@Override
				public void onSuccess(List<IBlurb> result) {
					view.showList(result);
				}
			});
		}
	}



	@Override
	public String mayStop()
	{
		return null;

	}

	@Override
	public ClientFactory getClientFactory() {
		return clientFactory;
	}

	@Override
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}

	@Override
	public void compPicked(Long id) {
		// TODO Auto-generated method stub

	}


	@Override
	public void flushAllPipelineJobs() {
		clientFactory.flushAllPipelineJobs();

	}



	@Override
	public void createContent() {
		clientFactory.createContent();
	}


	@Override
	public void cleanUp() {
		getMenuItemDelegate().cleanUp();

	}

	private MenuItemDelegate getMenuItemDelegate() {
		if (menuItemDelegate == null) {
			menuItemDelegate = new MenuItemDelegate(clientFactory);
		}

		return menuItemDelegate;
	}

	
	@Override
	public void onCancelConfigClicked() {
		//((DialogBox) clientFactory.getSeriesConfigrPopupView()).hide();
		
	}

	@Override
	public void onSaveConfigClicked(IBlurb player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void DeleteBlurb(IBlurb blurb) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void Deactivate(List<IBlurb> blurbs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createDigestEmail() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newBlurb(String url, String linkText, String bodyText) {
		view.getBlurbModal().hide();
		clientFactory.getRpcService().addBlurb(url, linkText, bodyText, new AsyncCallback<List<IBlurb>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to fetch blurb list");
			}

			@Override
			public void onSuccess(List<IBlurb> result) {
				Notify.notify("blurb saved");
				view.showList(result);
			}
		});
		
	}

	@Override
	public Boolean onItemSelected(IBlurb c) {
		if (selectionModel.isSelected(c)) {
			selectionModel.removeSelection(c);
		}

		else {
			selectionModel.addSelection(c);
		}

		return true;
	}

	@Override
	public Boolean isSelected(IBlurb c) {
		return selectionModel.isSelected(c);
	}

	@Override
	public Boolean onItemClicked(IBlurb c, int row) {
		view.showBlurb(c);
		return true;
	}

	@Override
	public void deleteSelected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<IBlurb> getSelected() {
		return selectionModel.selectedItems;
	}

	@Override
	public void digestPreview(String message, List<Long> blurbIds) {
		clientFactory.getRpcService().getDigestPreview(message, blurbIds, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to fetch blurb list");
			}

			@Override
			public void onSuccess(String result) {

				view.showPreview(result);
			}
		});
		
	}

	@Override
	public void sendDigest(String message, List<Long> blurbIds) {
		clientFactory.getRpcService().sendDigestEmail(message, blurbIds, new AsyncCallback<Integer>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to fetch blurb list");
			}

			@Override
			public void onSuccess(Integer result) {
				Bootbox.alert("Digest email queued for sending to " + result + " users.");
			}
		});
		
	}

}
