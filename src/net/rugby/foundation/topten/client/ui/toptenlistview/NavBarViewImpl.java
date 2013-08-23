package net.rugby.foundation.topten.client.ui.toptenlistview;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.place.TopTenListPlace;
import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavPills;
import com.github.gwtbootstrap.client.ui.NavWidget;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;


public class NavBarViewImpl extends Composite 
{
	private static NavBarViewImplUiBinder uiBinder = GWT.create(NavBarViewImplUiBinder.class);


	@UiField NavPills loginPanel;
	@UiField Dropdown compDropdown;
	Element title;
	Element details1;
	Element details2;
	Element fbLike;
	NavWidget buttonBar;
	
	private ClientFactory clientFactory;


	@UiTemplate("NavBarViewImpl.ui.xml")

	interface NavBarViewImplUiBinder extends UiBinder<Widget, NavBarViewImpl>
	{
	}


	public NavBarViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
		title = DOM.getElementById("heading");
		details1 = DOM.getElementById("details1");
		details2 = DOM.getElementById("details2");
		fbLike = DOM.getElementById("fbLike");
		buttonBar = new NavWidget();
		buttonBar.addStyleName("btn-group");
		RootPanel.get("hero").add(buttonBar);
	}


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
						assert (clientFactory != null);
						clientFactory.getPlaceController().goTo(newPlace);
					}
				});
				compDropdown.add(nl);
			}
		}

	}


	public void addLoginPanel(HorizontalPanel acct) {
		loginPanel.add(acct);

	}

	public NavPills getLoginPanel() {
		return loginPanel;
	}


	public void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		
	}
	
	public void setHeroListInfo(String title1, String details11) {
		title.setInnerHTML(title1);
		details1.setInnerHTML(details11);
	}
	
	public void setDetails(String details11) {
		details2.setInnerHTML(details11);
	}

	public void setFBLikeAttribute(String name, String value) {
		fbLike.setAttribute(name, value);
	}
	
	public NavWidget getButtonBar() {
		return buttonBar;
	}
}