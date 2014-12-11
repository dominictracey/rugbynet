package net.rugby.foundation.topten.client.ui;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.Collapse;
import org.gwtbootstrap3.client.ui.DropDown;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.ListGroupItem;
import org.gwtbootstrap3.client.ui.ListItem;
import org.gwtbootstrap3.client.ui.Navbar;
import org.gwtbootstrap3.client.ui.VerticalButtonGroup;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.gwtbootstrap3.client.ui.html.Span;
import org.gwtbootstrap3.client.ui.html.UnorderedList;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.place.FeatureListPlace;
import net.rugby.foundation.topten.client.place.SeriesPlace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;


public class SidebarViewImpl extends Composite
{
	private static NavBarViewImplUiBinder uiBinder = GWT.create(NavBarViewImplUiBinder.class);

	@UiField
	protected ListGroup dashboardMenu;
//	@UiField
//	protected ListGroup profileMenu;

//	@UiField
//	protected ListGroupItem sidebarProfile = null;
	
	ListGroupItem caratParent = null;

	
	final HTML carat = new HTML("<div class=\"arrow\"></div><div class=\"arrow_border\"></div>");

	HashMap<Long,Anchor> anchorMap = new HashMap<Long,Anchor>();
	HashMap<Long,ListGroupItem> liMap = new HashMap<Long,ListGroupItem>();
	HashMap<Long,ListGroup> submenuMap = new HashMap<Long,ListGroup>();
	
	interface NavBarViewImplUiBinder extends UiBinder<Widget, SidebarViewImpl>
	{
	}

	private ClientFactory clientFactory;


	public SidebarViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
		RootPanel.get("sidebar-nav").add(this);
		dashboardMenu.removeStyleName("list-group");
		dashboardMenu.setId("dashboard-menu");
//		profileMenu.removeStyleName("list-group");
//		profileMenu.setId("profile-menu");
//		profileMenu.setStyleName("show-xs");  
//		profileMenu.addStyleName("hidden-*");
		carat.setStyleName("pointer");
	}

//	public void setComps(Map<Long, String> competitionMap, List<Long> compsUnderway) {
//		ListIterator<Long> it = compsUnderway.listIterator(compsUnderway.size());
//		if (it != null) {
//
//		}
//
//	}

	public void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;

	}

	public void setup(ICoreConfiguration coreConfig) {
		for (Long compId : coreConfig.getCompetitionMap().keySet()) {
			addCompMenu(compId, coreConfig.getCompetitionMap().get(compId), coreConfig.getSeriesMap().get(compId));
		}


		// this calls a function in /theme/theme.js
		hookupTheme();
		
	}

	protected void addCompMenu(final Long compId, String compName, final HashMap<RatingMode, Long>  modeMap) {

		final ListGroupItem li = new ListGroupItem();
		Anchor a = new Anchor();
		a.setHTML("<i class=\"fa fa-laptop\"></i><span>" + compName + "</span><b class=\"caret\"></b>");
		anchorMap.put(compId, a);
		liMap.put(compId, li);
		li.add(a);
		dashboardMenu.add(li);
		li.removeStyleName("list-group-item");
		
		if (modeMap.isEmpty()) {
			a.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					// TODO Auto-generated method stub

				}

			});
		} else {
			// first create submenu
			ListGroup submenu = new ListGroup();
			submenuMap.put(compId, submenu);
			submenu.setStyleName("submenu");
			
			// add home link
			ListGroupItem lgi = new ListGroupItem();
			lgi.removeStyleName("list-group-item");
			Anchor homeLink = new Anchor();
			homeLink.setText("Home");

			homeLink.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
									
					// allow other elements to display this comp
					if (Core.getCore().getCurrentCompId() != compId) {
						Core.getCore().setCurrentCompId(compId);
					}
					
					FeatureListPlace place = new FeatureListPlace();
					place.setCompId(compId);
					
					// remove the carat if it is somewhere else
					if (caratParent != null && carat != null) {
						caratParent.remove(carat);
					}
					
					// add the carat
					li.add(carat);
					caratParent = li;
					
					clientFactory.getPlaceController().goTo(place);
				}
				
			});
			lgi.add(homeLink);
			submenu.add(lgi);
			
			for (RatingMode mode: modeMap.keySet()) {
				lgi = new ListGroupItem();
				lgi.removeStyleName("list-group-item");
				Anchor modeLink = new Anchor();
				modeLink.setText(mode.getMenuName());
				final RatingMode _mode = mode;
				modeLink.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						
						// allow other elements to display this comp
						if (Core.getCore().getCurrentCompId() != compId) {
							Core.getCore().setCurrentCompId(compId);
						}
						
						SeriesPlace place = new SeriesPlace();
						place.setCompId(compId);
						place.setSeriesId(modeMap.get(_mode));
						
						// remove the carat if it is somewhere else
						if (caratParent != null && carat != null) {
							caratParent.remove(carat);
						}
						
						// add the carat
						li.add(carat);
						caratParent = li;
						
						clientFactory.getPlaceController().goTo(place);
					}
					
				});
				lgi.add(modeLink);
				submenu.add(lgi);
			}
			li.add(submenu);
			a.setStyleName("dropdown-toggle");

		}

	}

	public static native void hookupTheme() /*-{
		$wnd.hookupTheme();
	}-*/;

	public void setComp(ICompetition comp) {
		ListGroupItem li = liMap.get(comp.getId());
		// apply active class to li
		li.setStyleName("active");
		
		// apply display: block to submenu
		submenuMap.get(comp.getId()).setStyleName("display: block;");
		
		// remove the carat if it is somewhere else
		if (caratParent != null && carat != null) {
			caratParent.remove(carat);
		}
		
		// add the carat
		li.add(carat);
		caratParent = li;
		
	}

	public ListGroupItem getSidebarProfile() {
		return null; //sidebarProfile;
	}

}