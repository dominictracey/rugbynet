package net.rugby.foundation.admin.client.activity;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.ui.SmartBar;

import org.gwtbootstrap3.extras.bootbox.client.Bootbox;
import org.gwtbootstrap3.extras.bootbox.client.callback.ConfirmCallback;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MenuItemDelegate implements SmartBar.Presenter {

	ClientFactory clientFactory;
	
	MenuItemDelegate(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}
	
	@Override
	public void goTo(Place place) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void compPicked(Long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flushAllPipelineJobs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createContent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanUp() {
		Bootbox.confirm("This will remove admin detail information from PlayerRatings and flush all Fetcher Pipelines. Proceed?", new ConfirmCallback() {	
			@Override
			public void callback(boolean result) {
				
				if (result) {
					clientFactory.getRpcService().cleanUp(new AsyncCallback<String>() {
		
						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Problem with clean up:" + caught.getLocalizedMessage());
							
						}
		
						@Override
						public void onSuccess(String result) {
							Notify.notify("Clean up underway");
						}
						
					});
				}
			}
		});
		
	}

}
