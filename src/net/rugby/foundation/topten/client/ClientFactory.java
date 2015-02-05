package net.rugby.foundation.topten.client;

import java.util.HashMap;
import java.util.List;

import org.gwtbootstrap3.client.ui.Anchor;

import net.rugby.foundation.core.client.Identity.Presenter;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.ISponsor;
import net.rugby.foundation.model.shared.LoginInfo;
import net.rugby.foundation.topten.client.ui.content.ContentView;
import net.rugby.foundation.topten.client.ui.content.EditContent;
import net.rugby.foundation.topten.client.ui.notes.NoteView;
import net.rugby.foundation.topten.client.ui.toptenlistview.EditTTLInfo;
import net.rugby.foundation.topten.client.ui.toptenlistview.FeatureListView;
import net.rugby.foundation.topten.client.ui.toptenlistview.SeriesListView;
import net.rugby.foundation.topten.client.ui.toptenlistview.TopTenListView;
import net.rugby.foundation.topten.client.ui.HeaderView;
import net.rugby.foundation.topten.client.ui.RatingPopupViewImpl;
import net.rugby.foundation.topten.client.ui.SidebarViewImpl;
import net.rugby.foundation.topten.model.shared.INote;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

/**
 * ClientFactory helpful to use a factory or dependency injection framework like GIN to obtain 
 * references to objects needed throughout your application like the {@link EventBus},
 * {@link PlaceController} and views.
 */
public interface ClientFactory {

//	public interface GetCountryListCallback {
//		void onCountryListFetched(List<ICountry> countries);
//	}
//	

	EventBus getEventBus();

	PlaceController getPlaceController();
	TopTenListServiceAsync getRpcService();

//	TopTenListView<ITopTenItem> getListView();
//	EditTTIText getEditTTITextDialog();
	ContentView getContentView();
	EditContent getEditContentDialog();
	EditTTLInfo getEditTTLInfo();

	boolean isDualParamString();
	HeaderView getHeaderView();
	
	void RegisterIdentityPresenter(Presenter identityPresenter);
	void doSetup(AsyncCallback<ICoreConfiguration> cb);

	ICoreConfiguration getCoreConfig();

	HashMap<String, Long> getContentList();

	LoginInfo getLoginInfo();

	TopTenListView<ITopTenItem> getSimpleView();

	RatingPopupViewImpl<IPlayerRating> getRatingPopup();

	SeriesListView<IRatingSeries> getSeriesView();

	String getPlaceFromURL();
//
//	void setPlaceInUrl(SeriesPlace place);

	void renderNotes(List<INote> notes, final ITopTenList ttl, AsyncCallback<List<INote>> cb);

	Widget render(INote note, ITopTenList context, boolean includeDetails);
	NoteView<INote> getNoteView();
	String getPlayerName(long playerId);

	SidebarViewImpl getSidebarView();

	FeatureListView<ITopTenList> getFeatureListView();

	/**
	 *  given a widget and a listId, will fetch the name (caching as needed) and setText on the anchor the name of the list
	 * @param nextId
	 * @param nextLabel
	 */
	void setTTLName(Long nextId, Anchor nextLabel);

	void showFacebookComments(String url);

	String getTeamLogoStyle(Long teamId);
	
	void getSponsorForList(ITopTenList list, AsyncCallback<ISponsor> cb);

	void console(String text);

	void recordAnalyticsEvent(String cat, String action, String label, int val);
}
