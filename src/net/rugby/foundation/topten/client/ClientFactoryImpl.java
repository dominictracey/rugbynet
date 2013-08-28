package net.rugby.foundation.topten.client;

import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.topten.client.ui.content.ContentView;
import net.rugby.foundation.topten.client.ui.content.EditContent;
import net.rugby.foundation.topten.client.ui.toptenlistview.EditTTIText;
import net.rugby.foundation.topten.client.ui.toptenlistview.TopTenListView;
import net.rugby.foundation.topten.client.ui.toptenlistview.TopTenListViewImpl;
import net.rugby.foundation.topten.model.shared.ITopTenItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;

/**
 * Sample implementation of {@link ClientFactory}.
 */
public class ClientFactoryImpl implements ClientFactory {
  
	private static final EventBus eventBus = new SimpleEventBus();
	@SuppressWarnings("deprecation")
	private static final PlaceController placeController = new PlaceController(eventBus);
	private static final TopTenListView<ITopTenItem> listView = new TopTenListViewImpl();
	private static ContentView contentView = null;
	private static EditContent editContent = null;
	private static final TopTenListServiceAsync rpcService = GWT.create(TopTenListService.class);
	private static EditTTIText editTTIText = null;
	
	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public PlaceController getPlaceController() {
		return placeController;
	}

	@Override
	public TopTenListView<ITopTenItem> getListView() {
		return listView;
	}

	@Override
	public TopTenListServiceAsync getRpcService() {
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

}
