package net.rugby.foundation.admin.client.activity;

import java.util.ArrayList;
import java.util.List;

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

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

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

	@Override
	public void archive() {
		List<Long> blurbIds = getBlurbIds();
		clientFactory.getRpcService().archive(blurbIds, new AsyncCallback<Integer>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to archive blurb list");
			}

			@Override
			public void onSuccess(Integer result) {
				selectionModel.selectedItems.clear();
				Notify.notify("Archived " + result + " blurbs.");
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
		});
	}

	private List<Long> getBlurbIds() {
		List<Long> retval = new ArrayList<Long>();
		for (IBlurb b : selectionModel.selectedItems) {
			retval.add(b.getId());
		}
		return retval;
	}

	@Override
	public void facebook() {
		List<Long> blurbIds = getBlurbIds();
		clientFactory.getRpcService().facebook(blurbIds, new AsyncCallback<Integer>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to post blurbs to facebook");
			}

			@Override
			public void onSuccess(Integer result) {
				Bootbox.alert("Buffered " + result + " facebook posts.");
			}
		});
	}

	@Override
	public void twitter() {
		final List<Long> blurbIds = getBlurbIds();
		clientFactory.getRpcService().twitter(blurbIds, new AsyncCallback<List<Long>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to fetch blurb list");
			}

			@Override
			public void onSuccess(List<Long> result) {
				Bootbox.alert("Buffered " + (blurbIds.size() - result.size()) + " tweets.");

				//@TODO allow user to edit players without twitter handles to add them in for next time.
			}
		});

	}

	@Override
	public void onBulkUploadSaved(String text) {
		String[] emails = text.split("[\n| |\t]");
		RegExp p = RegExp.compile("\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\\b");
		List<String> emailsValid = new ArrayList<String>(emails.length);


		for (String s: emails){
			MatchResult m = p.exec(s.toUpperCase());
			if (m != null){
				Notify.notify(s);
				emailsValid.add(s);

			}
			else{
				Notify.notify("Bad");


			}	
		}

		clientFactory.getRpcService().bulkUploadEmails(emailsValid, new AsyncCallback<List<String>>() {
			int successCount = 0;
			int failCount = 0;
			@Override
			public void onSuccess(List<String> result) {
				for(String r: result){	
					if(r.contains("success")){
						successCount ++;
					}
					if(r.contains("already in use")){
						failCount ++;
					}
				}
				Bootbox.alert("Bulk user email upload results: " + result + " Succeeded: " + successCount + " Failed: " + failCount);
			}
			@Override
			public void onFailure(Throwable caught) {		
				Window.alert("Failed to fetch blurb list");
			}


		});

	}
}




