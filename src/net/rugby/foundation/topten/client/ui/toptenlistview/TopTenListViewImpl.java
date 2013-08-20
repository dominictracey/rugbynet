package net.rugby.foundation.topten.client.ui.toptenlistview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.place.TopTenListPlace;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.ITopTenItem;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavPills;
import com.github.gwtbootstrap.client.ui.NavWidget;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;


public class TopTenListViewImpl extends Composite implements TopTenListView<ITopTenItem>
{
	private static TopTenListViewImplUiBinder uiBinder = GWT.create(TopTenListViewImplUiBinder.class);


	@UiField NavPills loginPanel;
	@UiField HTMLPanel topTenPanel;
	@UiField Heading title;
	@UiField Paragraph details1;
	@UiField Paragraph details2;
	@UiField Dropdown compDropdown;
	@UiField NavWidget buttonBar;
	@UiField HTMLPanel items;
	@UiField Button prevButton;
	@UiField Button nextButton;
	
	List<TopTenItemView> itemList;
	private ITopTenList list;


	private net.rugby.foundation.topten.client.ui.toptenlistview.TopTenListView.TopTenListViewPresenter presenter;


	private ClientFactory clientFactory;


	@UiTemplate("TopTenListViewImpl.ui.xml")

	interface TopTenListViewImplUiBinder extends UiBinder<Widget, TopTenListViewImpl>
	{
	}


	public TopTenListViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
		prevButton.setIconSize(IconSize.LARGE);
		prevButton.setIcon(IconType.BACKWARD);
		nextButton.setIconSize(IconSize.LARGE);
		nextButton.setIcon(IconType.FORWARD);
	}


	@Override
	public void setList(ITopTenList result, String baseUrl) {
		list = result;
		if (result != null) {
			title.setText(result.getTitle());
			details1.setText(result.getContent());
			details2.setText("Check back every Monday for top ten performances from top competitions.");
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
			items.clear();
			Iterator<ITopTenItem> it = result.getList().iterator();
			int count = 0;
			if (it != null) {
				itemList = new ArrayList<TopTenItemView>();

				while (it.hasNext()) {
					ITopTenItem item = it.next();

					TopTenItemView itemView = new TopTenItemView(item, count++, result.getId(), item.getPlayerId(), baseUrl);
					itemList.add(itemView);

					items.add(itemView);
				}
			}
			
			// set the fbLike div property
			// data-href="http://dev.rugby.net/topten.html#List:listId=159002" 
			Element e = Document.get().getElementById("fbLike");
			if (e != null) {
				e.setPropertyString("data-href", baseUrl +"#List:listId=" + list.getId());
			}

		} else {
			items.clear();
			title.setText("Top Rugby Performances");
			details1.setText("Choose from the Competition menu above to view the latest picks for Top Ten Performances");
			details2.setText("Check back every Monday for top ten performances from competitions around the world.");
			prevButton.setEnabled(false);
			nextButton.setEnabled(false);
		}


	}

	@Override
	public ITopTenList getList() {
		return list;
	}


	@Override
	public void setComps(Map<Long, String> competitionMap, List<Long> compsUnderway) {
		Iterator<Long> it = compsUnderway.iterator();
		if (it != null) {
			compDropdown.clear();
			while (it.hasNext()) {
				final Long compId = it.next();
				NavLink nl = new NavLink(competitionMap.get(compId));
				nl.addClickHandler( new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						TopTenListPlace newPlace = new TopTenListPlace();
						newPlace.setCompId(compId);
						clientFactory.getPlaceController().goTo(newPlace);
					}
				});
				compDropdown.add(nl);
			}
		}

	}


	@Override
	public void addLoginPanel(HorizontalPanel acct) {
		loginPanel.add(acct);

	}


	@Override
	public NavPills getLoginPanel() {
		return loginPanel;
	}


	@Override
	public List<TopTenItemView> getItemViews() {
		return itemList;
	}


	@Override
	public NavWidget getButtonBar() {
		return buttonBar;
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
	
}