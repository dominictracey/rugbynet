package net.rugby.foundation.topten.client.ui.toptenlistview;

import java.util.List;

import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.ITopTenItem;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.ImageCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class CompactTopTenListViewImpl extends Composite implements TopTenListView<ITopTenItem>
{
	private static TopTenListViewImplUiBinder uiBinder = GWT.create(TopTenListViewImplUiBinder.class);


	@UiField HTMLPanel topTenPanel;


	@UiField CellTable<ITopTenItem> items;
	@UiField Button prevButton;
	@UiField Button nextButton;

	List<TopTenItemView> itemList;
	private ITopTenList list;
	private int itemCount;




	private TopTenListView.TopTenListViewPresenter presenter;


	private ClientFactory clientFactory;


	@UiTemplate("CompactTopTenListViewImpl.ui.xml")

	interface TopTenListViewImplUiBinder extends UiBinder<Widget, CompactTopTenListViewImpl>
	{
	}


	public CompactTopTenListViewImpl()
	{
		// add the login bar to the top
		//		navbar = new NavBarViewImpl();
		//		RootPanel.get("navbar").add(navbar);

		initWidget(uiBinder.createAndBindUi(this));
		prevButton.setIconSize(IconSize.LARGE);
		prevButton.setIcon(IconType.BACKWARD);
		nextButton.setIconSize(IconSize.LARGE);
		nextButton.setIcon(IconType.FORWARD);


		items.addColumn(new Column<ITopTenItem,String>(new TextCell()){
			@Override
			public String getValue(ITopTenItem s)
			{
				return s.getOrdinal() < 0 ? "" : Integer.toString(s.getOrdinal());
			}
			@Override
			public String getCellStyleNames(Context context, ITopTenItem value) {
				return "bubble";

			}
		});

		items.addColumn(new Column<ITopTenItem,String>(new TextCell()){
			@Override
			public String getValue(ITopTenItem s)
			{
				return s.getPlayer() == null ? "" : s.getPlayer().getDisplayName();
			}
			@Override
			public String getCellStyleNames(Context context, ITopTenItem value) {
				return "lead text-center";

			}
		});
		
		items.addColumn(new Column<ITopTenItem,String>(new TextCell()){
			@Override
			public String getValue(ITopTenItem s)
			{
				return s.getPosition() == null ? "" : "(" + s.getPosition().getName() + ")";
			}
			@Override
			public String getCellStyleNames(Context context, ITopTenItem value) {
				return "lead text-center";

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

		items.addColumn(new Column<ITopTenItem,String>(imageCell){
			@Override
			public String getValue(ITopTenItem s)
			{ //
				return "";
			}
			@Override
			public String getCellStyleNames(Context context, ITopTenItem value) {
				return "pull-right";
			}
		});

		//		items.addColumn(new Column<ITopTenItem,String>(new TextCell()){
		//			@Override
		//            public String getValue(ITopTenItem s)
		//            {
		//                return s.getPlayer().getDisplayName() == null ? "" : s.getPlayer().getDisplayName();
		//            }
		//			@Override
		//			public String getCellStyleNames(Context context, ITopTenItem value) {
		//				return "lead";
		//				
		//			}
		//		});

		//		items.addColumn(new Column<ITopTenItem,String>(new TextCell()){
		//			@Override
		//            public String getValue(ITopTenItem s)
		//            {
		//                return s.getPosition() == null ? "" : "(" + s.getPosition().getName() + ")";
		//            }
		//			@Override
		//			public String getCellStyleNames(Context context, ITopTenItem value) {
		//				return "lead";
		//				
		//			}
		//		});

	}


	@Override
	public void setList(final ITopTenList result, final String baseUrl) {
		list = result;
		//setVisible(false);
		if (result != null) {
			clientFactory.getNavBarView().setHeroListInfo(result.getTitle(),result.getContent());
			clientFactory.getNavBarView().setDetails("");
			clientFactory.getNavBarView().setHeroTextBig(false);
			
			if (list.getPrevPublishedId() != null) {
				prevButton.setEnabled(true);
			} else {
				prevButton.setEnabled(false);
			}

			if (list.getNextPublishedId() != null) {
				nextButton.setEnabled(true);
			} else {
				nextButton.setEnabled(false);
			}
			items.setRowData(result.getList());
			//			Iterator<ITopTenItem> it = result.getList().iterator();
			//			int count = 0;
			//			if (it != null) {
			//				itemList = new ArrayList<TopTenItemView>();
			//
			//				while (it.hasNext()) {
			//					final ITopTenItem item = it.next();
			//
			//					final int fCount = count++;
			//					Scheduler.get().scheduleDeferred(new ScheduledCommand() {    
			//						@Override
			//						public void execute() {
			//
			//							TopTenItemView itemView = new TopTenItemView(item, fCount, result.getId(), item.getPlayerId(), baseUrl);
			//							itemList.add(itemView);
			//							items.add(itemView);
			//							presenter.setTTIButtons(itemView);
			//						}
			//					});
			//				}
			//			}

			Element loadPanel = DOM.getElementById("loadPanel");
			if (loadPanel != null && loadPanel.hasParentElement()) {
				loadPanel.removeFromParent();
			}
			//setVisible(true);
		} else {
			items.setVisible(false);
			clientFactory.getNavBarView().setHeroListInfo("Top Rugby Performances","Choose from the Competition menu above to view the latest picks for Top Ten Performances");
			//clientFactory.getNavBarView().setDetails("Check back every Monday for top ten performances from competitions.");
			prevButton.setEnabled(false);
			nextButton.setEnabled(false);
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

	@UiHandler("prevButton")
	void onPrevButtonClicked(ClickEvent event) {	
		presenter.showPrev();
	}

	@UiHandler("nextButton")
	void onNextButtonClicked(ClickEvent event) {	
		presenter.showNext();
	}


	@Override
	public void setPresenter(TopTenListViewPresenter presenter) {
		this.presenter = presenter;
	}


	@Override
	public void hasNext(boolean has) {
		nextButton.setEnabled(has);

	}


	@Override
	public void hasPrev(boolean has) {
		prevButton.setEnabled(has);	
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




}