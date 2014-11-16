package net.rugby.foundation.topten.client.ui.toptenlistview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.ITopTenItem;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;


public class TopTenListViewImpl extends Composite implements TopTenListView<ITopTenItem>
{
	private static TopTenListViewImplUiBinder uiBinder = GWT.create(TopTenListViewImplUiBinder.class);


	@UiField HTMLPanel topTenPanel;


	@UiField HTMLPanel items;
	@UiField Button prevButton;
	@UiField Button nextButton;

	//NavBarViewImpl navbar;

	List<TopTenItemView> itemList;
	private ITopTenList list;
	private int itemCount;




	private net.rugby.foundation.topten.client.ui.toptenlistview.TopTenListView.TopTenListViewPresenter presenter;


	private ClientFactory clientFactory;


	@UiTemplate("TopTenListViewImpl.ui.xml")

	interface TopTenListViewImplUiBinder extends UiBinder<Widget, TopTenListViewImpl>
	{
	}


	public TopTenListViewImpl()
	{
		// add the login bar to the top
//		navbar = new NavBarViewImpl();
//		RootPanel.get("navbar").add(navbar);

		initWidget(uiBinder.createAndBindUi(this));
		prevButton.setIconSize(IconSize.LARGE);
		prevButton.setIcon(IconType.BACKWARD);
		nextButton.setIconSize(IconSize.LARGE);
		nextButton.setIcon(IconType.FORWARD);
		nextButton.setVisible(false);
		prevButton.setVisible(false);
	}


	@Override
	public void setList(final ITopTenList result, final String baseUrl) {
		list = result;
		//setVisible(false);
		if (result != null) {
			//clientFactory.getHeaderView().setHeroListInfo(result.getTitle(),result.getContent() + "<div id=\"fbListLike\"/>");

			if (list.getPrevPublishedId() != null) {
				prevButton.setVisible(true);
			} else {
				prevButton.setVisible(false);
			}

			if (list.getNextPublishedId() != null) {
				nextButton.setVisible(true);
			} else {
				nextButton.setVisible(false);
			}
			items.clear();
			Iterator<ITopTenItem> it = result.getList().iterator();
			int count = 0;
			if (it != null) {
				itemList = new ArrayList<TopTenItemView>();

				while (it.hasNext()) {
					final ITopTenItem item = it.next();

					final int fCount = count++;
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {    
						@Override
						public void execute() {

							TopTenItemView itemView = new TopTenItemView(item, fCount, result.getId(), item.getPlayerId(), baseUrl);
							itemList.add(itemView);
							items.add(itemView);
							presenter.setTTIButtons(itemView);
						}
					});
				}
			}


			//setVisible(true);
		} else {
			items.clear();
//			clientFactory.getHeaderView().setHeroListInfo("Top Rugby Performances","Choose from the Competition menu above to view the latest picks for Top Ten Performances");
			//clientFactory.getNavBarView().setDetails("Check back every Monday for top ten performances from competitions.");
			prevButton.setVisible(false);
			nextButton.setVisible(false);
		}


	}

	@Override
	public ITopTenList getList() {
		return list;
	}


	@Override
	public List<TopTenItemView> getItemViews() {
		return itemList;
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
		nextButton.setVisible(has);

	}


	@Override
	public void hasPrev(boolean has) {
		prevButton.setVisible(has);	
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


	@Override
	public void showContent(boolean show) {
		// can ignore this here I think.	
	}




}