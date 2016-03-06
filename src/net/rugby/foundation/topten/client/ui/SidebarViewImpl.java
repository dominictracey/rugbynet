package net.rugby.foundation.topten.client.ui;

import java.util.HashMap;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.AnchorButton;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.ListGroupItem;
import org.gwtbootstrap3.client.ui.constants.IconPosition;
import org.gwtbootstrap3.client.ui.html.Span;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.place.FeatureListPlace;
import net.rugby.foundation.topten.client.place.SeriesPlace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;


public class SidebarViewImpl extends Composite
{
	private static SidebarViewImplUiBinder uiBinder = GWT.create(SidebarViewImplUiBinder.class);

	@UiField
	protected ListGroup dashboardMenu;

	boolean ignore = false;
	@UiField 
	protected DropDownMenu profileMenu;

	@UiField 
	protected ListGroupItem sidebarProfile;

	@UiField AnchorButton profileButton;
	
	ListGroupItem caratParent = null;


	final HTML carat = new HTML("<div class=\"arrow\"></div><div class=\"arrow_border\"></div>");

	HashMap<Long,Anchor> anchorMap = new HashMap<Long,Anchor>();
	HashMap<Long,ListGroupItem> liMap = new HashMap<Long,ListGroupItem>();
	HashMap<Long,ListGroup> submenuMap = new HashMap<Long,ListGroup>();

	interface SidebarViewImplUiBinder extends UiBinder<Widget, SidebarViewImpl>
	{
	}

	private ClientFactory clientFactory;


	public SidebarViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
		if (RootPanel.get("sidebar-nav") != null) {
			RootPanel.get("sidebar-nav").add(this);
			dashboardMenu.removeStyleName("list-group");
			dashboardMenu.setId("dashboard-menu");
			sidebarProfile.removeStyleName("list-group-item");
			sidebarProfile.setStyleName("show-xs");  
			sidebarProfile.addStyleName("hidden-md");
			sidebarProfile.addStyleName("hidden-lg");
			sidebarProfile.addStyleName("hidden-sm");
			profileButton.removeStyleName("btn");
			profileButton.removeStyleName("btn-default");
			profileButton.setIconPosition(IconPosition.LEFT);
			Span span = new Span("Account");
			profileButton.add(span);
			profileMenu.removeStyleName("dropdown-menu");
			profileMenu.addStyleName("submenu");

			carat.setStyleName("pointer");
		}
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
		
		for (Long compId : coreConfig.getCompsForClient()) {
			addCompMenu(compId, coreConfig.getCompetitionMap().get(compId), coreConfig.getSeriesMap().get(compId));
		}


		// this calls a function in /theme/theme.js
		hookupTheme();

	}

	protected void addCompMenu(final Long compId, String compName, final HashMap<RatingMode, Long>  modeMap) {

		final ListGroupItem li = new ListGroupItem();
		li.remove(0);
		Anchor a = new Anchor();
		a.setHTML("<i class=\"fa fa-globe\"></i><span>" + compName + "</span><b class=\"caret\"></b>");
		anchorMap.put(compId, a);
		liMap.put(compId, li);
		li.add(a);
		dashboardMenu.add(li);
		li.removeStyleName("list-group-item");

		//		if (modeMap.isEmpty()) {
		//			a.addClickHandler(new ClickHandler() {
		//
		//				@Override
		//				public void onClick(ClickEvent event) {
		//					// TODO Auto-generated method stub
		//
		//				}
		//
		//			});
		//		} else {
		// first create submenu
		ListGroup submenu = new ListGroup();
		submenuMap.put(compId, submenu);
		submenu.setStyleName("submenu");

		// add home link
		ListGroupItem lgi = new ListGroupItem();
		lgi.removeStyleName("list-group-item");
		//lgi.setStyleName("null");
		Anchor homeLink = new Anchor();
		homeLink.setText("Home");

		homeLink.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				// allow other elements to display this comp
				if (Core.getCore().getCurrentCompId() != compId) {
					ignore = true;
					Core.getCore().setCurrentCompId(compId);
				}

				FeatureListPlace place = new FeatureListPlace();
				place.setCompId(compId);

				// remove the carat if it is somewhere else
				if (caratParent != null && carat != null && !caratParent.equals(li)) {
					caratParent.remove(carat);
					caratParent.removeStyleName("active");
					caratParent.getElement().getElementsByTagName("ul").getItem(0).getStyle().setProperty("display", "none");
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
			//lgi.setStyleName("null");
			Anchor modeLink = new Anchor();
			modeLink.setText(mode.getMenuName());
			final RatingMode _mode = mode;
			modeLink.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {

					// allow other elements to display this comp
					if (!Core.getCore().getCurrentCompId().equals(compId)) {
						ignore = true;
						Core.getCore().setCurrentCompId(compId);
					}

					SeriesPlace place = new SeriesPlace();
					place.setCompId(compId);
					place.setSeriesId(modeMap.get(_mode));

					// remove the carat if it is somewhere else
					if (caratParent != null && carat != null && !caratParent.equals(li)) {
						caratParent.remove(carat);
						caratParent.removeStyleName("active");
						caratParent.getElement().getElementsByTagName("ul").getItem(0).getStyle().setProperty("display", "none");
						//							li.setStyleName("active");
						//							li.getElement().getElementsByTagName("ul").getItem(0).getStyle().setProperty("display", "block");
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

		//		}

	}

	public static native void hookupTheme() /*-{
		$wnd.hookupTheme();
	}-*/;

	public void setComp(ICompetition comp) {

		if (!ignore) {
			ListGroupItem li = liMap.get(comp.getId());

			//if (li != null) {
				// apply active class to li
				li.setStyleName("active");

				ListGroup subMenu = submenuMap.get(comp.getId());
				// open the submenu
				subMenu.getElement().getStyle().setProperty("display", "block");


				// remove the carat if it is somewhere else
				if (caratParent != null && carat != null) {
					caratParent.remove(carat);
					caratParent.removeStyleName("active");
					caratParent.getElement().getElementsByTagName("ul").getItem(0).getStyle().setProperty("display", "none");
				}

				// add the carat
				li.add(carat);
				caratParent = li;
			} else {
				ignore = false;
			}
		//}

	}

	public DropDownMenu getSidebarProfile() {
		return profileMenu;
	}

}