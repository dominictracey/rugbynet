package net.rugby.foundation.admin.client.activity;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.ui.SmartBar;

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
		if (Window.confirm("This will delete all PlayerRatings, RatingQueries and Fetcher Pipelines. Proceed?")) {			
			clientFactory.getRpcService().cleanUp(new AsyncCallback<String>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Problem with clean up:" + caught.getLocalizedMessage());
					
				}

				@Override
				public void onSuccess(String result) {
					Window.alert(result);
				}
				
			});
		}
		
	}

}
