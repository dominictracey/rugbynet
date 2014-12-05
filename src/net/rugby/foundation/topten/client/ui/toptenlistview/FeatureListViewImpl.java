package net.rugby.foundation.topten.client.ui.toptenlistview;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.constants.IconSize;
import org.gwtbootstrap3.client.ui.constants.IconType;

import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.model.shared.ITopTenList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;


public class FeatureListViewImpl extends Composite implements FeatureListView<ITopTenList> {

	private static TopTenListViewImplUiBinder uiBinder = GWT.create(TopTenListViewImplUiBinder.class);

	interface TopTenListViewImplUiBinder extends UiBinder<Widget, FeatureListViewImpl>
	{
	}
	
	@UiField Button prevButton;
	@UiField Button nextButton;
	@UiField Panel featureBody;
	@UiField PanelBody featuredTTL;
	
	// admin buttons
	@UiField Panel adminButtons;
	@UiField Button edit;
	@UiField Button publish;
	@UiField Button promote;
	
	ITopTenList list = null;
	
	public FeatureListViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
		prevButton.setIconSize(IconSize.LARGE);
		prevButton.setIcon(IconType.BACKWARD);
		nextButton.setIconSize(IconSize.LARGE);
		nextButton.setIcon(IconType.FORWARD);
		nextButton.setVisible(false);
		prevButton.setVisible(false);
	}
	

	private ClientFactory clientFactory;
	private FeatureListViewPresenter presenter;
	
	@Override
	public void setList(ITopTenList result, String baseUrl) {
		list = result;
		clientFactory.getSimpleView().setList(result, baseUrl);
		
		featuredTTL.add(clientFactory.getSimpleView());
		
		if (result != null) {
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
		}
	}

	@Override
	public void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	@Override
	public ITopTenList getList() {
		return list;
	}

	@Override
	public void showContent(boolean show) {
		// TODO Auto-generated method stub
		
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
	public void setPresenter(FeatureListViewPresenter presenter) {
		this.presenter = presenter;		
	}

	
	@UiHandler("prevButton")
	void onPrevButtonClicked(ClickEvent event) {	
		presenter.showPrev();
	}

	@UiHandler("nextButton")
	void onNextButtonClicked(ClickEvent event) {	
		presenter.showNext();
	}

	@UiHandler("edit")
	void onEditButtonClicked(ClickEvent event) {	
		presenter.edit(list);
	}

	@UiHandler("publish")
	void onPublishButtonClicked(ClickEvent event) {	
		presenter.publish(list);
	}
	
	@UiHandler("promote")
	void onPromoteButtonClicked(ClickEvent event) {	
		presenter.promote(list);
	}
//
//	@Override
//	public void setList(final ITopTenList result, final String baseUrl) {
//		list = result;
//		//setVisible(false);
//		if (result != null) {
//			//clientFactory.getHeaderView().setHeroListInfo(result.getTitle(),result.getContent() + "<div id=\"fbListLike\"/>");
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
//			items.clear();
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
//
//
//			//setVisible(true);
//		} else {
//			items.clear();
////			clientFactory.getHeaderView().setHeroListInfo("Top Rugby Performances","Choose from the Competition menu above to view the latest picks for Top Ten Performances");
//			//clientFactory.getNavBarView().setDetails("Check back every Monday for top ten performances from competitions.");
//			prevButton.setVisible(false);
//			nextButton.setVisible(false);
//		}
// }





}