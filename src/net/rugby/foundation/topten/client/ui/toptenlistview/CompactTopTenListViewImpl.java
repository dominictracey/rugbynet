package net.rugby.foundation.topten.client.ui.toptenlistview;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IRatingQuery.MinMinutes;
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
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
	@UiField HTML algorithm;
	
	private ITopTenList list;
	private int itemCount;





	private TopTenListViewPresenter presenter;
	private ClientFactory clientFactory;


	private boolean showContent;


	@UiTemplate("CompactTopTenListViewImpl.ui.xml")

	interface CompactTopTenListViewImplUiBinder extends UiBinder<Widget, CompactTopTenListViewImpl>
	{
	}


	public CompactTopTenListViewImpl()
	{

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
				return clientFactory.getTeamLogoStyle(value.getTeamId()) + " teamlogo-small";
			}
		});

		// movement icon
		items.addColumn(new TextColumn<ITopTenItem>(){
			@Override
			public String getValue(ITopTenItem s)
			{
				return " ";
			}


			@Override
			public String getCellStyleNames(Context context, ITopTenItem value) {	
				String style = "movement ";
				if (value.getLastOrdinal() == 0) {
					style += "movement-none";
				} else {
					if (value.getLastOrdinal() == value.getOrdinal()) {
						style += "movement-unchanged";
					} else if (value.getLastOrdinal() > value.getOrdinal()) {
						style += "movement-up";
					} else {
						style += "movement-down";
					}
				}
				return style;
			}
		});
		
		items.addColumn(new TextColumn<ITopTenItem>(){
			@Override
			public String getValue(ITopTenItem s)
			{
				if (s.getLastOrdinal() == 0) {
					return "";
				}
				
				if (s.getLastOrdinal() > s.getOrdinal()) {
					return "(" + s.getLastOrdinal() + ")";
				} else if (s.getLastOrdinal() < s.getOrdinal()) {
					return "(" + s.getLastOrdinal() + ")";
				} else {
					return "(" + s.getLastOrdinal() + ")";
				}
			}
			@Override
			public String getCellStyleNames(Context context, ITopTenItem value) {
				return "text-right compactTTL position";

			}
		});
		
		items.addColumn(new Column<ITopTenItem, SafeHtml>(new SafeHtmlCell()){
			@Override
			public SafeHtml getValue(ITopTenItem s)
			{
				SafeHtmlBuilder sb = new SafeHtmlBuilder();
				String guid = "";
				
				// this will always drive users to the feature, even if they share the series link (a good thing?)
				if (s.getFeatureGuid() == null) {
					guid = s.getPlaceGuid();
				} else {
					guid = s.getFeatureGuid();
				}
				
				String listDesc = list.getTwitterDescription();
				if (listDesc == null || listDesc.isEmpty()) {
					listDesc = list.getTitle();
				}

				String tweet = s.getTweet();  // @player of @team
				if (clientFactory.getLoginInfo() != null && (clientFactory.getLoginInfo().isTopTenContentContributor() || clientFactory.getLoginInfo().isTopTenContentEditor())) {
					if (tweet != null && !tweet.isEmpty()) {						
						//assert(tweet.charAt(0) == '@');	
						tweet += " is #" + s.getOrdinal() + " on @TheRugbyNet " + listDesc;
						if (s.getTwitterChannel() != null && !s.getTwitterChannel().isEmpty() && tweet.length() < 115 - s.getTwitterChannel().length()) {
							 tweet += " " + s.getTwitterChannel();
						}
					}
				} else {
					// just a regular user					
					if (tweet != null && !tweet.isEmpty()) {

						tweet = "Congrats to " + s.getTweet();

						tweet += " for being #" + s.getOrdinal() + " on @TheRugbyNet " + listDesc;
						
						if (s.getTwitterChannel() != null && !s.getTwitterChannel().isEmpty() && tweet.length() < 115 - s.getTwitterChannel().length()) {
							 tweet += " " + s.getTwitterChannel();
						}

					}
				}

				sb.appendHtmlConstant("<div class=\"addthis_toolbox addthis_default_style addthis_32x32_style rugbyNetAddThis\" addthis:url=\"" + clientFactory.getCoreConfig().getBaseToptenUrl() + "s/" + guid + "\" addthis:title=\"" + tweet + "\">"
						//						+ "<a class=\"addthis_button_email\"></a>"
						+ "<a class=\"addthis_button_facebook\"></a>"
						+ "<a class=\"addthis_button_twitter\"></a>"
						//						+ "<a class=\"addthis_button_reddit\"></a>"
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
				if (event.getColumn() != 7) {
					boolean isClick = "click".equals(event.getNativeEvent().getType());
					if (isClick) {
						if (presenter != null && event != null) {
							presenter.showRatingDetails(event.getValue());
						}
					}
				}
			}
		});

		items.getElement().getStyle().setCursor(Cursor.POINTER); 

		topTenPanel.addStyleName("compactTopTenPanel");
	}


	@Override
	public void setList(final ITopTenList result) {
		list = result;
		
		if (result != null) {

			recordAnalyticsHit(Window.Location.getPath(), result.getTitle());

			items.setRowData(result.getList());
			generated.setHTML("<i>generated: " + result.getCreated().toGMTString() + "</i>");
			String algo = "";
			if (result.getSeries() != null && result.getSeries() == true) {
				algo = "This list was created using the <u>" + clientFactory.getSeriesView().getMatrix().getCriteria().getMenuName() + "</u> algorithm.<br/>";
				if (clientFactory.getSeriesView().getMatrix().getCriteria() == Criteria.AVERAGE_IMPACT) {
					
					if (clientFactory.getSeriesView().getQuery().getMinMinutesType() == MinMinutes.ROUND) {
						algo += "* Includes only players who have averaged at least " + clientFactory.getSeriesView().getQuery().getMinMinutes() + " minutes per round.<br/>";
					} else if (clientFactory.getSeriesView().getQuery().getMinMinutesType() == MinMinutes.TOTAL) {
						algo += "* Includes only players with at least " + clientFactory.getSeriesView().getQuery().getMinMinutes() + " minutes played in the last twelve months.<br/>";
					} 
						
				}
			} 
			algo += " To learn about how these lists are crafted, click <a href=\"#Content:contentId=5171798064758784\">here.";
			algorithm.setHTML(algo);
			
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