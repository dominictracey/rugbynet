package net.rugby.foundation.topten.client.ui.toptenlistview;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.ITopTenItem;

import org.gwtbootstrap3.client.ui.Button;

import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.IconSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.CellTable;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.ImageCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class CompactTopTenListViewImpl extends Composite implements TopTenListView<ITopTenItem>
{
	private static CompactTopTenListViewImplUiBinder uiBinder = GWT.create(CompactTopTenListViewImplUiBinder.class);


	@UiField HTMLPanel topTenPanel;


	CellTable<ITopTenItem> items;
	@UiField Column tableCol;
//	@UiField Heading title;
//	@UiField HTML details1;
//	@UiField Row contentPanel;
//	@UiField org.gwtbootstrap3.client.ui.Column contentDiv;
//	@UiField VerticalPanel contentArea;
//	@UiField Button prevButton;
//	@UiField Button nextButton;

	List<TopTenItemView> itemList;
	private ITopTenList list;
	private int itemCount;




	private TopTenListView.TopTenListViewPresenter presenter;


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
//		prevButton.setIconSize(IconSize.LARGE);
//		prevButton.setIcon(IconType.CHEVRON_LEFT);
//		nextButton.setIconSize(IconSize.LARGE);
//		nextButton.setIcon(IconType.CHEVRON_RIGHT);
//		prevButton.setVisible(false);
//		nextButton.setVisible(false);
//		contentPanel.addStyleName("contentPanel");
//		contentArea.addStyleName("contentArea");
		items = new CellTable<ITopTenItem>();

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
//
//		items.addColumn(new TextColumn<ITopTenItem>(){
//			@Override
//			public String getValue(ITopTenItem s)
//			{
//				SafeHtmlBuilder sb = new SafeHtmlBuilder();
//				String name = s.getPlayer() == null ? "" : s.getPlayer().getDisplayName();
////				sb.appendHtmlConstant("<table width=\"100%\"><tr><td>" + name + "</td></tr><tr><td>"
////						+ "<div class=\"addthis_toolbox addthis_default_style addthis_32x32_style\" addthis:url=\"" + clientFactory.getCoreConfig().getBaseToptenUrl() + "/s/" + s.getPlaceGuid() + "\" addthis:title=\"" + s.getPlayer().getDisplayName() + "\">"
//////						+ "<a class=\"addthis_button_email\"></a>"
////						+ "<a class=\"addthis_button_facebook\"></a>"
////						+ "<a class=\"addthis_button_twitter\"></a>"
////						+ "<a class=\"addthis_button_compact\">Share</a>"
////						+ "</div></td></tr></table>");
//				sb.appendHtmlConstant(name);
//				return sb.toSafeHtml().toString();
//				//return s.getPlayer() == null ? "" : s.getPlayer().getDisplayName();
//				
//			}
//			@Override
//			public String getCellStyleNames(Context context, ITopTenItem value) {
//				return "lead text-center compactTTL";
//
//			}
//		});
//
//		items.addColumn(new TextColumn<ITopTenItem>(){
//			@Override
//			public String getValue(ITopTenItem s)
//			{
//
//				return s.getRating() == 0 ? "" : "(" + Integer.toString(s.getRating()) + ")";
//			}
//			@Override
//			public String getCellStyleNames(Context context, ITopTenItem value) {
//				return "text-right compactTTL position";
//
//			}
//		});
//
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
//		items.addColumn(new TextColumn<ITopTenItem>(){
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
//
//		items.addColumn(new TextColumn<ITopTenItem>(){
//			@Override
//			public String getValue(ITopTenItem s)
//			{
//				return s.getPosition() == null ? "" : s.getPosition().getName();
//			}
//			@Override
//			public String getCellStyleNames(Context context, ITopTenItem value) {
//				return "text-center compactTTL position";
//			}
//		});
//
//		ImageCell imageCell = new ImageCell() {
//			@Override
//			public void render(Context context, String value, SafeHtmlBuilder sb) {
//				if (((ITopTenItem)context.getKey()).getTeamId() != null) {
//					String imagePath = "/resources/" + ((ITopTenItem)context.getKey()).getTeamId() + "/200.png";
//					sb.appendHtmlConstant("<img src = '"+imagePath+"' height = '40px' width = '40px' title=\"" + ((ITopTenItem)context.getKey()).getTeamName()  + "\"/>");
//				}
//
//			}
//		};
//
//		items.addColumn(new TextColumn<ITopTenItem>(){
//			@Override
//			public String getValue(ITopTenItem s)
//			{ //
//				return "";
//			}
//			@Override
//			public String getCellStyleNames(Context context, ITopTenItem value) {
//				return "compactTTL";
//			}
//		});
//
//		items.addColumn(new TextColumn<ITopTenItem>(){
//			@Override
//			public String getValue(ITopTenItem s)
//			{
//				SafeHtmlBuilder sb = new SafeHtmlBuilder();
//				sb.appendHtmlConstant("<div class=\"addthis_toolbox addthis_default_style\" addthis:url=\"" + clientFactory.getCoreConfig().getBaseToptenUrl() + "/s/" + s.getPlaceGuid() + "\" addthis:title=\"" + s.getTweet() + "\">"
//						+ "<a class=\"addthis_button_email\"></a>"
//						+ "<a class=\"addthis_button_facebook\"></a>"
//						+ "<a class=\"addthis_button_twitter\"></a>"
//						+ "<a class=\"addthis_button_compact\">Share</a>"
//						+ "</div>");
//				return sb.toSafeHtml().toString();
//			}
//			@Override
//			public String getCellStyleNames(Context context, ITopTenItem value) {
//				return "text-right compactTTL position";
//
//			}
//		});
//
//		items.addCellPreviewHandler( new Handler<ITopTenItem>() {
//
//			@Override
//			public void onCellPreview(CellPreviewEvent<ITopTenItem> event) {
//				if (event.getColumn() == 3) {
//					boolean isClick = "click".equals(event.getNativeEvent().getType());
//					if (isClick) {
//						presenter.showRatingDetails(event.getValue());
//					}
//				}
//			}
//		});
//		
		tableCol.add(items);
	}


	@Override
	public void setList(final ITopTenList result, final String baseUrl) {
		list = result;
		//setVisible(false);
		if (result != null) {
			recordAnalyticsHit(baseUrl + "#listId=" + result.getId(), result.getTitle());

//			clientFactory.getHeaderView().setHeroListInfo(result.getTitle(),result.getContent());
//			clientFactory.getHeaderView().setDetails("");
//			clientFactory.getHeaderView().setHeroTextBig(false);
//
//			if (list.getPrevPublishedId() != null) {
//				prevButton.setVisible(true);
//			} else {
//				prevButton.setVisible(false);
//			}
//
//			if (list.getNextPublishedId() != null) {
//				nextButton.setVisible(true);
//			} else {
//				nextButton.setVisible(false);
//			}
//
//
//			contentDiv.setVisible(showContent);
//
//
//			title.setText(result.getTitle());
//			details1.setHTML(result.getContent() + "<div id=\"fbListLike\"/>");
			items.setRowData(result.getList());

			Scheduler.get().scheduleDeferred(new ScheduledCommand() {    
				@Override
				public void execute() {
					renderAddThis();
				}
			});


			//presenter.setFBListLike(result, baseUrl);
		} else {
			items.setVisible(false);
//			clientFactory.getHeaderView().setHeroListInfo("Top Rugby Performances","Choose from the Competition menu above to view the latest picks for Top Ten Performances");
//			prevButton.setVisible(false);
//			nextButton.setVisible(false);
		}

	}

	@Override
	public ITopTenList getList() {
		return list;
	}


	@Override
	public List<TopTenItemView> getItemViews() {
		return null;
	}
//
//	@UiHandler("prevButton")
//	void onPrevButtonClicked(ClickEvent event) {	
//		presenter.showPrev();
//	}
//
//	@UiHandler("nextButton")
//	void onNextButtonClicked(ClickEvent event) {	
//		presenter.showNext();
//	}


	@Override
	public void setPresenter(TopTenListViewPresenter presenter) {
		this.presenter = presenter;
	}

//
//	@Override
//	public void hasNext(boolean has) {
//		nextButton.setEnabled(has);
//		nextButton.setVisible(has);
//	}
//
//
//	@Override
//	public void hasPrev(boolean has) {
//		prevButton.setEnabled(has);	
//		prevButton.setVisible(has);
//
//	}


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