package net.rugby.foundation.topten.client.ui.toptenlistview;

import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.ITopTenItem;

import org.gwtbootstrap3.client.ui.gwt.CellTable;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.ImageCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class CompactTopTenListViewImpl extends Composite implements TopTenListView<ITopTenItem>
{
	private static CompactTopTenListViewImplUiBinder uiBinder = GWT.create(CompactTopTenListViewImplUiBinder.class);


	@UiField HTMLPanel topTenPanel;

	@UiField 
	CellTable<ITopTenItem> items;
	
	@UiField HTML generated;

	private ITopTenList list;
	private int itemCount;

	



	private TopTenListViewPresenter presenter;
	//private TopTenListView.Presenter listViewPresenter;
	private ClientFactory clientFactory;


	private boolean showContent;


	@UiTemplate("CompactTopTenListViewImpl.ui.xml")

	interface CompactTopTenListViewImplUiBinder extends UiBinder<Widget, CompactTopTenListViewImpl>
	{
	}


	public CompactTopTenListViewImpl()
	{
		// add the login bar to the top
		//		navbar = new NavBarViewImpl();
		//		RootPanel.get("navbar").add(navbar);

		initWidget(uiBinder.createAndBindUi(this));

		items.addColumn(new TextColumn<ITopTenItem>(){
			@Override
			public String getValue(ITopTenItem s)
			{
				return s.getOrdinal() < 0 ? "" : Integer.toString(s.getOrdinal()) + ".";
			}
			@Override
			public String getCellStyleNames(Context context, ITopTenItem value) {
				return "compactTTL";

			}
		});

		items.addColumn(new TextColumn<ITopTenItem>(){
			@Override
			public String getValue(ITopTenItem s)
			{
				SafeHtmlBuilder sb = new SafeHtmlBuilder();
				String name = s.getPlayer() == null ? "" : s.getPlayer().getDisplayName();
				sb.appendHtmlConstant(name);
				return sb.toSafeHtml().asString();
			}
			
			@Override
			public String getCellStyleNames(Context context, ITopTenItem value) {
				return "lead text-center compactTTL";

			}
			
		});

		items.addColumn(new TextColumn<ITopTenItem>(){
			@Override
			public String getValue(ITopTenItem s)
			{
				if (Window.getClientWidth() > 479) {
					return s.getRating() == 0 ? "" : "(" + Integer.toString(s.getRating()) + ")";
				} else {
					return s.getPosition().getAbbr() + " (" + Integer.toString(s.getRating()) + ")";
				}
			}
			@Override
			public String getCellStyleNames(Context context, ITopTenItem value) {
				return "text-right compactTTL position";

			}
		});

//		ImageCell ratingDetailsCell = new ImageCell() {
//			@Override
//			public void render(Context context, String value, SafeHtmlBuilder sb) {
//				if (((ITopTenItem)context.getKey()).getRating() != 0) {
//					String imagePath = "/resources/info35.png";
//					sb.appendHtmlConstant("<img src = '"+imagePath+"' height = '30px' width = '30px' title=\"click for details\"/>");
//				}
//			}
//
//			public Set<String> getConsumedEvents() {
//				HashSet<String> events = new HashSet<String>();
//				events.add("click");
//				return events;
//			}
//		};
//
//		items.addColumn(new Column<ITopTenItem,String>(ratingDetailsCell) {
//			@Override
//			public String getValue(ITopTenItem s)
//			{ //
//				return "";
//			}
//			@Override
//			public String getCellStyleNames(Context context, ITopTenItem value) {
//				return "compactTTL";
//			}
//
//		});

		items.addColumn(new TextColumn<ITopTenItem>(){
			@Override
			public String getValue(ITopTenItem s)
			{
				if (Window.getClientWidth() > 991) {
					return s.getPosition() == null ? "" : s.getPosition().getName();
				} else if (Window.getClientWidth() > 479) {
					return s.getPosition() == null ? "" : s.getPosition().getAbbr();
				} else {
					return "";  // stack up with rating
				}
			}
			@Override
			public String getCellStyleNames(Context context, ITopTenItem value) {
				return "compactTTL"; //return "text-center compactTTL position";
			}
		});
		
		items.addColumn(new TextColumn<ITopTenItem>(){
			@Override
			public String getValue(ITopTenItem s)
			{
				return " ";
			}
			@Override
			public String getCellStyleNames(Context context, ITopTenItem value) {
				return "usa";
			}
		});

		ImageCell imageCell = new ImageCell() {
			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				if (((ITopTenItem)context.getKey()).getTeamId() != null) {
					
					String imagePath = "/resources/" + ((ITopTenItem)context.getKey()).getTeamId() + "/200.png";
					sb.appendHtmlConstant("<img src = '"+imagePath+"' height = '40px' width = '40px' title=\"" + ((ITopTenItem)context.getKey()).getTeamName()  + "\"/>");
				}

			}
		};

		items.addColumn(new TextColumn<ITopTenItem>(){
			@Override
			public String getValue(ITopTenItem s)
			{ //
				return "";
			}
			@Override
			public String getCellStyleNames(Context context, ITopTenItem value) {
				return ""; //return "compactTTL";
			}
		});

		items.addColumn(new Column<ITopTenItem, SafeHtml>(new SafeHtmlCell()){
			@Override
			public SafeHtml getValue(ITopTenItem s)
			{
				SafeHtmlBuilder sb = new SafeHtmlBuilder();
				sb.appendHtmlConstant("<div class=\"addthis_toolbox addthis_default_style\" addthis:url=\"" + clientFactory.getCoreConfig().getBaseToptenUrl() + "/s/" + s.getPlaceGuid() + "/\" addthis:title=\"" + s.getTweet() + "\">"
						+ "<a class=\"addthis_button_email\"></a>"
						+ "<a class=\"addthis_button_facebook\"></a>"
						+ "<a class=\"addthis_button_twitter\"></a>"
						+ "<a class=\"addthis_button_compact\">Share</a>"
						+ "</div>");
				return sb.toSafeHtml();
			}
			@Override
			public String getCellStyleNames(Context context, ITopTenItem value) {
				return ""; //return "text-right compactTTL position";

			}
		});

		items.addCellPreviewHandler( new Handler<ITopTenItem>() {

			@Override
			public void onCellPreview(CellPreviewEvent<ITopTenItem> event) {
				//if (event.getColumn() == 3) {
					boolean isClick = "click".equals(event.getNativeEvent().getType());
					if (isClick) {
						presenter.showRatingDetails(event.getValue());
					}
				//}
			}
		});
		
//		tableCol.add(items);
		
//		 // Add a selection model to handle user selection.
//	    final SingleSelectionModel<ITopTenItem> selectionModel = new SingleSelectionModel<ITopTenItem>();
//	    items.setSelectionModel(selectionModel);
//	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
//	      public void onSelectionChange(SelectionChangeEvent event) {
//	        ITopTenItem selected = selectionModel.getSelectedObject();
//	        if (selected != null) {
//	          presenter.showRatingDetails(selected);
//	        }
//	      }
//	    });
	    
	    topTenPanel.addStyleName("compactTopTenPanel");
	}


	@Override
	public void setList(final ITopTenList result, final String baseUrl) {
		list = result;
		//setVisible(false);
		if (result != null) {
			recordAnalyticsHit(baseUrl + "#listId=" + result.getId(), result.getTitle());

			items.setRowData(result.getList());
			generated.setHTML("<i>generated: " + result.getCreated().toGMTString() + "</i>");
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {    
				@Override
				public void execute() {
					renderAddThis();
				}
			});
		} else {
			items.setVisible(false);
		}

	}

	@Override
	public ITopTenList getList() {
		return list;
	}


	@Override
	public void setPresenter(TopTenListViewPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	@Override
	public int getItemCount() {
		return itemCount;
	}

	@Override
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}


	// per http://stackoverflow.com/questions/2457794/integrating-google-analytics-into-gwt-application
	// ??       $wnd.ganew.push(['_trackPageview(' + title + ')']);
	public static native void recordAnalyticsHit(String url, String title) /*-{
    	$wnd.ganew('send', 'pageview', {'page': url,'title': title});
	}-*/;

	public static native void renderAddThis() /*-{
		$wnd.addthis.update('config', 'pubid', 'ra-5425a2ac6bdc588a');
		$wnd.addthis.update('config', 'data_ga_property', 'UA-2626751-1');
		$wnd.addthis.update('config', 'data_ga_social', true);
		$wnd.addthis.toolbox(".addthis_toolbox");
	}-*/;

	@Override
	public void showContent(boolean show) {
		this.showContent = show;

	}


	@Override
	public void hasNext(boolean has) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void hasPrev(boolean has) {
		// TODO Auto-generated method stub
		
	}


}