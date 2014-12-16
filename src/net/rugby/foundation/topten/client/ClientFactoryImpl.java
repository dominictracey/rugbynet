package net.rugby.foundation.topten.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.html.Div;
import org.gwtbootstrap3.client.ui.html.Span;

import net.rugby.foundation.core.client.Core.CompChangeListener;
import net.rugby.foundation.core.client.Core.GuidChangeListener;
import net.rugby.foundation.core.client.Core.RoundChangeListener;
import net.rugby.foundation.core.client.Identity.Presenter;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.IServerPlace;
import net.rugby.foundation.model.shared.LoginInfo;
import net.rugby.foundation.topten.client.place.SeriesPlace;
import net.rugby.foundation.topten.client.resources.noteTemplates.NoteTemplates;
import net.rugby.foundation.topten.client.ui.HeaderView;
import net.rugby.foundation.topten.client.ui.HeaderViewImpl;
import net.rugby.foundation.topten.client.ui.RatingPopupViewImpl;
import net.rugby.foundation.topten.client.ui.RecentFeaturesViewImpl;
import net.rugby.foundation.topten.client.ui.SidebarViewImpl;
import net.rugby.foundation.topten.client.ui.content.ContentView;
import net.rugby.foundation.topten.client.ui.content.EditContent;
import net.rugby.foundation.topten.client.ui.notes.NoteView;
import net.rugby.foundation.topten.client.ui.notes.NoteViewColumnDefinitions;
import net.rugby.foundation.topten.client.ui.notes.NoteViewImpl;
import net.rugby.foundation.topten.client.ui.toptenlistview.CompactTopTenListViewImpl;
import net.rugby.foundation.topten.client.ui.toptenlistview.DesktopSeriesListView;
import net.rugby.foundation.topten.client.ui.toptenlistview.EditTTLInfo;
import net.rugby.foundation.topten.client.ui.toptenlistview.FeatureListView;
import net.rugby.foundation.topten.client.ui.toptenlistview.FeatureListViewImpl;
import net.rugby.foundation.topten.client.ui.toptenlistview.SeriesListView;
import net.rugby.foundation.topten.client.ui.toptenlistview.SeriesListViewImpl;
import net.rugby.foundation.topten.client.ui.toptenlistview.TopTenListView;
import net.rugby.foundation.topten.model.shared.Feature;
import net.rugby.foundation.topten.model.shared.INote;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.Note;
import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.core.client.Identity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Sample implementation of {@link ClientFactory}.
 */
public class ClientFactoryImpl implements ClientFactory, Presenter, CompChangeListener, RoundChangeListener, GuidChangeListener {

	private static final EventBus eventBus = new SimpleEventBus();

	private static PlaceController placeController = null;
	//private static  TopTenListView<ITopTenItem> listView = null;
	private static  TopTenListView<ITopTenItem> simpleView = null;
	private static  NoteView<INote> noteView = null;
	private NoteViewColumnDefinitions<INote> noteViewColumnDefinitions;

	private RecentFeaturesViewImpl recentFeaturesView;
	private static  SidebarViewImpl sidebarView = null;
	private static ContentView contentView = null;
	private static SeriesListView<IRatingSeries> seriesView = null;
	private static FeatureListView<ITopTenList> featureView = null;
	private static EditContent editContent = null;
	private static TopTenListServiceAsync rpcService = null;
	//	private static EditTTIText editTTIText = null;
	private static HeaderView headerView = null;
	private static EditTTLInfo editTTLInfo = null;
	private static Presenter identityPresenter = null;
	private static LoginInfo loginInfo = null;
	private static ICoreConfiguration coreConfig = null;
	private static List<IContent> contentList = null;
	private static RatingPopupViewImpl<IPlayerRating> ratingPopup = null;

	// Here are our note rendering caches
	private static Map<Long, String> playerNames = new HashMap<Long, String>();
	private static Map<Long, String> ttlNames = new HashMap<Long, String>();
	private static Map<Long, String> ttlContexts = new HashMap<Long, String>();
	private static Map<String, String> templateMap = new HashMap<String, String>();



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

	//	@Override
	//	public TopTenListView<ITopTenItem> getListView() {
	//		if (listView == null) {
	//			listView = new TopTenListViewImpl();
	//		}
	//		return listView;
	//	}

	@Override
	public TopTenListServiceAsync getRpcService() {
		if (rpcService == null) {
			rpcService = GWT.create(TopTenListService.class);
		}
		return rpcService;
	}

	//	@Override
	//	public EditTTIText getEditTTITextDialog() {
	//		if (editTTIText == null) {
	//			editTTIText = new EditTTIText();
	//		}
	//		return editTTIText;
	//	}

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
	public HeaderView getHeaderView() {
		if (headerView == null) {
			headerView = new HeaderViewImpl();
			RootPanel.get("header").add((HeaderViewImpl)headerView);
			headerView.setClientFactory(this);
		}
		return headerView; 
	}
	@Override
	public SidebarViewImpl getSidebarView() {
		if (sidebarView == null) {
			sidebarView = new SidebarViewImpl();
			sidebarView.setClientFactory(this);
		}
		return sidebarView; 
	}
	@Override
	public EditTTLInfo getEditTTLInfo() {
		if (editTTLInfo == null) {
			editTTLInfo = new EditTTLInfo();
			loadWYSIWYGEditor("edit..");
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


				// @REX how is this different from the Callback passed in to doSetup below?
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
		final CompChangeListener _compListener = this;
		final RoundChangeListener _roundListener = this;
		final GuidChangeListener _guidListener = this;

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
						// are we in a mobile screen?
						if (Window.getClientWidth() < 768) {
							// for now, no login from mobile devices
							//i.setParentWidget(getSidebarView().getSidebarProfile());
						} else {
							i.setParent(getHeaderView().getLoginPanel());
						}
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
							// set up content 
							getRpcService().getContentItems( new AsyncCallback<List<IContent>>() {



								@Override
								public void onFailure(Throwable caught) {
									cb.onFailure(caught);
								}

								@Override
								public void onSuccess(List<IContent> contentList) {
									ClientFactoryImpl.contentList = contentList;

									getHeaderView().setContent(contentList, loginInfo.isTopTenContentEditor());	

									getSidebarView().setup(coreConfig);

									getRpcService().getLatestFeatures(new AsyncCallback<List<Feature>>() {

										@Override
										public void onFailure(Throwable caught) {
											// TODO Auto-generated method stub

										}

										@Override
										public void onSuccess(List<Feature> result) {
											if (result != null) {
												getLatestFeatureView().setRecentFeatures(result);
											}

											Core.getCore().registerCompChangeListener(_compListener);
											Core.getCore().registerRoundChangeListener(_roundListener);
											Core.getCore().registerGuidChangeListener(_guidListener);

											//Core.getCore().setCurrentCompId(coreConfig.getDefaultCompId());
											cb.onSuccess(coreConfig);	
										}



									});
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

	private RecentFeaturesViewImpl getLatestFeatureView() {
		if (recentFeaturesView == null) {
			recentFeaturesView = new RecentFeaturesViewImpl();
			recentFeaturesView.setClientFactory(this);
			RootPanel div = RootPanel.get("features");
			if (div != null) {
				div.add(recentFeaturesView);
			} else {
				// bummer
			}
		}

		return recentFeaturesView;

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
	@Override
	public RatingPopupViewImpl<IPlayerRating> getRatingPopup() {
		if (ratingPopup == null) {
			ratingPopup = new RatingPopupViewImpl<IPlayerRating>();

			Iterator<IContent> it = getContentList().iterator();
			IContent content = null;
			boolean found = false;
			while (it.hasNext()) {
				content = it.next();
				if (content.getTitle().equals("rating popup details")) {
					found = true;
					break;
				}
			}

			if (found) {
				ratingPopup.setContent(content.getBody());
			}
		}
		return ratingPopup;
	}
	@Override
	public SeriesListView<IRatingSeries> getSeriesView() {
		if (seriesView == null) {
			if (Window.getClientWidth() > 992) {
				// col-lg, col-md
				seriesView = new DesktopSeriesListView();
			} else {
				// col-xs, col-sm
				seriesView = new SeriesListViewImpl();
			}
			seriesView.setClientFactory(this);
		}
		return seriesView; 
	}

	@Override
	public FeatureListView<ITopTenList> getFeatureListView() {
		if (featureView == null) {
			featureView = new FeatureListViewImpl();
			featureView.setClientFactory(this);
		}
		return featureView; 
	}


	@Override
	public NoteView<INote> getNoteView() {
		if (noteView == null) {
			noteView = new NoteViewImpl<INote>();
			noteView.setClientFactory(this);
			RootPanel.get("notes").add(noteView);

			if (noteViewColumnDefinitions == null) {
				noteViewColumnDefinitions = new NoteViewColumnDefinitions<INote>();
			}

			noteView.setColumnDefinitions(noteViewColumnDefinitions);

		}
		return noteView; 
	}

	@Override
	public String getPlaceFromURL() {
		if (Location.getPath().contains("/s/")) {
			// parse out the guid
			String chunks[] = Location.getPath().split("/");
			String guid = "";
			if (chunks.length > 2) {
				guid = chunks[2];
			}
			return guid;
		} else {
			return null;
		}
	}

	//	@Override
	//	public void setPlaceInUrl(SeriesPlace place) throws Exception {
	////		UrlBuilder builder = Location.createUrlBuilder().setPath("/s/" + place.getToken()).removeParameter("listId").removeParameter("compId").removeParameter("playerId");
	////		Window.Location.replace(builder.buildString());
	//		throw new Exception("don't use");
	//	}


	@Override
	public void renderNotes(final List<INote> notes, final ITopTenList ttl, final AsyncCallback<List<INote>> cb) {
		// what player names do we need?
		List<Long> needPlayerNames = determineNamesToFetch(notes);
		final List<Long> needTTLNames = determineListNamesToFetch(notes);		

		rpcService.getPlayerNames(needPlayerNames, new AsyncCallback<Map<Long, String>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(Map<Long, String> result) {

				// cache them
				for (Long id : result.keySet()) {
					playerNames.put(id, result.get(id));
				}


				rpcService.getTTLNames(needTTLNames, new AsyncCallback<Map<Long, String>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(Map<Long, String> result) {
						// cache them
						for (Long id : result.keySet()) {
							ttlNames.put(id, result.get(id));
						}

						rpcService.getTTLContexts(needTTLNames, new AsyncCallback<Map<Long, String>>() {
							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onSuccess(Map<Long, String> result) {
								// cache them
								for (Long id : result.keySet()) {
									ttlContexts.put(id, result.get(id));

								}									
								// we are ready for the NoteView to call render now...
								cb.onSuccess(notes);

							}
						});
					}

				});
			}

		});
	}

	@Override
	public Widget render(final INote note, ITopTenList context, boolean includeDetails) {
		String template = getTemplate(note.getTemplateSelector());
		StringBuilder builder = new StringBuilder(template);


		if (note.getPlayer1Id() != null) {
			swap(builder, Note.PLAYER1, playerNames.get(note.getPlayer1Id()));
		}

		if (note.getPlayer2Id() != null) {
			swap(builder, Note.PLAYER2, playerNames.get(note.getPlayer2Id()));
		}

		if (note.getPlayer3Id() != null) {
			swap(builder, Note.PLAYER3, playerNames.get(note.getPlayer3Id()));
		}

		if (includeDetails && note.getDetails() != null) {
			swap(builder, Note.DETAILS, note.getDetails());			
		} else {
			swap(builder, Note.DETAILS, "");			
		}

		Anchor a = null;

		if (context != null && context.getId().equals(note.getContextListId())) {
			// just blank out the context and link (since we are going to show this with the TTL, which is the context)
			swap(builder, Note.CONTEXT, "");
			swap(builder, Note.LIST, "");
			swap(builder, Note.LINK, "");
		} else {
			//			String ttlTitle = ttlNames.get(note.getContextListId());
			//			swap(builder, Note.CONTEXT, " on " +  ttlTitle);
			//			swap(builder, Note.LINK, "http://rugby.net/" + note.getLink());
			//}
			String linkText = "";
			if (template.contains(Note.CONTEXT)) {
				linkText = ttlContexts.get(note.getContextListId());
			} else {
				linkText = ttlNames.get(note.getContextListId());
			}

			swap(builder, Note.LIST, "");
			swap(builder, Note.CONTEXT, "in&nbsp;");
			swap(builder, Note.LINK, "");

			a = new Anchor(linkText);
			a.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					Core.getCore().changeGuid(note.getLink());

					// what we want to track about the note-sourced click stream:
					//		1) the template that caught the user's eye
					//		2) the list type the user left
					//		3) the list type the user went to
					//		4) the player name being followed
					recordAnalyticsEvent("note", "template", note.getTemplateSelector(), 1);
					recordAnalyticsEvent("note", "link", note.getLink(), 1);
					recordAnalyticsEvent("note", "type", note.getType().toString(), 1);
					recordAnalyticsEvent("note", "player", getPlayerName(note.getPlayer1Id()), 1);
				}

			});


		}

		Span w = new Span();
		w.setHTML(builder.toString());
		//w.add(text);
		if (a != null) {
			w.add(a);
		}
		return w.asWidget();
	}

	private String getTemplate(String templateSelector) {
		if (templateMap.isEmpty()) {
			// read into dictionary .INSTANCE.css().ensureInjected();
			String reader[] = NoteTemplates.INSTANCE.noteTemplates().getText().split("\n");
			String line = "";
			int i = 0;
			while (i < reader.length && (line = reader[i]) != null) {
				if (line.contains("=")) {
					// TT = [P1] was on [CONTEXT] [LINK]
					String key = line.split("=")[0].trim();
					String val = line.split("=")[1].trim();
					templateMap.put(key, val);
				}
				++i;
			}
		}
		return templateMap.get(templateSelector);
	}
	private boolean swap(StringBuilder builder, String from, String to) {
		int index = builder.indexOf(from);
		while (index != -1)
		{
			builder.replace(index, index + from.length(), to);
			index += to.length(); // Move to the end of the replacement
			index = builder.indexOf(from, index);
		}

		return true;
	}
	private List<Long> determineListNamesToFetch(List<INote> notes) {
		List<Long> retval = new ArrayList<Long>();
		for (INote n : notes) {
			if (n.getContextListId() != null && !ttlNames.containsKey(n.getContextListId())) {
				retval.add(n.getContextListId());
			}
		}
		return retval;
	}

	private List<Long> determineNamesToFetch(List<INote> notes) {
		List<Long> retval = new ArrayList<Long>();
		for (INote n : notes) {
			if (n.getPlayer1Id() != null && !playerNames.containsKey(n.getPlayer1Id())) {
				retval.add(n.getPlayer1Id());
			}
			if (n.getPlayer2Id() != null && !playerNames.containsKey(n.getPlayer2Id())) {
				retval.add(n.getPlayer2Id());
			}	
			if (n.getPlayer3Id() != null && !playerNames.containsKey(n.getPlayer3Id())) {
				retval.add(n.getPlayer3Id());
			}
		}

		return retval;
	}

	@Override
	public String getPlayerName(long playerId) {
		if (playerNames.containsKey(playerId)) {
			return playerNames.get(playerId);
		} else {
			return "n/a";
		}
	}

	@Override
	public void compChanged(final Long compId) {

		Core.getCore().getComp(compId, new AsyncCallback<ICompetition>() {


			@Override
			public void onFailure(Throwable caught) {

			}

			@Override
			public void onSuccess(final ICompetition comp) {
				sidebarView.setComp(comp);	
			}

		});



	}

	@Override
	public void roundChanged(int ordinal) {


	}

	@Override
	public void guidChanged(String guid) {
		rpcService.getPlace(guid, new AsyncCallback<IServerPlace>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(IServerPlace place) {
				getSeriesView().reset();
				if (place != null) {
					SeriesPlace p = new SeriesPlace();
					p.setCompId(place.getCompId());
					p.setGroupId(place.getGroupId());
					p.setMatrixId(place.getMatrixId());
					p.setItemId(place.getItemId());
					p.setQueryId(place.getQueryId());
					p.setSeriesId(place.getSeriesId());



					getPlaceController().goTo(p);
				}
			}

		});

	}

	@Override
	public void setTTLName(final Long nextId, final org.gwtbootstrap3.client.ui.Anchor anchor) {
		if (nextId != null) {
			if (ttlNames.containsKey(nextId)) {
				anchor.setText(ttlNames.get(nextId));
			} else {
				// fetch and cache
				List<Long> needTTLNames = new ArrayList<Long>();
				needTTLNames.add(nextId);
				rpcService.getTTLNames(needTTLNames, new AsyncCallback<Map<Long, String>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(Map<Long, String> result) {
						// cache them
						for (Long id : result.keySet()) {
							ttlNames.put(id, result.get(id));
						}

						anchor.setText(ttlNames.get(nextId));
					}
				});
			}
		}

	}
	
	private Element fbDiv = null;
	@Override
	public void showFacebookComments(String url) {
		//<div class="fb-comments" data-href="**FACEBOOKDATAREF**" data-width="500" data-numposts="5" data-colorscheme="light"></div>

		if (fbDiv == null) {
			fbDiv = DOM.getElementById("fbComments");
			initFacebook();
		}
		
		if (fbDiv != null) {
			//data-href="**FACEBOOKDATAREF**"
			fbDiv.setAttribute("data-href", url);
			// trigger fb parse
			parseFacebook();
		}
	}
	
	public static native void initFacebook() /*-{
		$wnd.FB.init()
	}-*/;
	
	public static native void parseFacebook() /*-{
		$wnd.FB.XFBML.parse();
	}-*/;

	public static native void recordAnalyticsEvent(String cat, String action, String label, int val) /*-{

		$wnd.ganew('send', 'event', cat, action, label, val);
	}-*/;

	public static native void loadWYSIWYGEditor(String text) /*-{

		$wnd.LazyLoad.js(["/theme/js/editor/wysihtml5-0.3.0.js","/theme/js/editor/bootstrap3-wysihtml5.js"], function() {

			$wnd.LazyLoad.css(["/theme/css/elements/form.css", "/theme/css/elements/bootstrap-wysihtml5.css"], function() { 

			//js all loaded
				$wnd.$('.textarea').wysihtml5({
			        "font-styles": true, //Font styling, e.g. h1, h2, etc. Default true
			        "emphasis": true, //Italics, bold, etc. Default true
			        "lists": true, //(Un)ordered lists, e.g. Bullets, Numbers. Default true
			        "html": true, //Button which allows you to edit the generated HTML. Default false
			        "link": true, //Button to insert a link. Default true
			        "image": true, //Button to insert an image. Default true,
			        "color": false, //Button to change color of font
			        "size": 'sm' //Button size like sm, xs etc.
			    });

			    $wnd.$('#modal').on('hidden', function(){
        			$wnd.$('.wysihtml5-sandbox, .wysihtml5-toolbar').remove();
        			$wnd.$('#textarea').show();
    			});

			});
		});

	}-*/;


}
