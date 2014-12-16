package net.rugby.foundation.topten.client.ui;

import java.util.Iterator;
import java.util.List;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.place.ContentPlace;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.ListGroupItem;
import org.gwtbootstrap3.client.ui.Nav;
import org.gwtbootstrap3.client.ui.constants.IconType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;


public class HeaderViewImpl extends Composite implements HeaderView 
{
	private static NavBarViewImplUiBinder uiBinder = GWT.create(NavBarViewImplUiBinder.class);

	@UiField Nav nav;
	@UiField ListGroupItem loginDropdown;
	@UiField ListGroupItem contentDropdown;
	
	@UiField DropDownMenu contentDropdownMenu;
	

	private ClientFactory clientFactory;


	private boolean isEditor;
//
//	private Map<String,Button> buttonMap = null;

	@UiTemplate("HeaderViewImpl.ui.xml")

	interface NavBarViewImplUiBinder extends UiBinder<Widget, HeaderViewImpl>
	{
	}


	public HeaderViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		loginDropdown.setStyleName("dropdown");
		contentDropdown.setStyleName("dropdown");
		contentDropdownMenu.addStyleName("dropdown-menu-right");
		
		nav.addStyleName("navbar-nav");
		nav.addStyleName("pull-right");
		nav.addStyleName("hidden-xs");


	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.client.ui.NavBarView#getLoginPanel()
	 */
	@Override
	public ListGroupItem getLoginPanel() {
		return loginDropdown;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.client.ui.NavBarView#setClientFactory(net.rugby.foundation.topten.client.ClientFactory)
	 */
	@Override
	public void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;

	}



	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.client.ui.NavBarView#setContent(java.util.List)
	 */
	@Override
	public void setContent(List<IContent> list, boolean isEditor) {
		this.isEditor = isEditor;
		if (list != null) {
			Iterator<IContent> it = list.iterator();
			if (it != null) {
				contentDropdownMenu.clear();

				while (it.hasNext()) {
					final IContent content = it.next();
					if (content != null && content.isShowInMenu()) {
						AnchorListItem nl = new AnchorListItem(content.getTitle());
						nl.addClickHandler( new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								ContentPlace newPlace = new ContentPlace(content.getId());
								assert (clientFactory != null);
								clientFactory.getPlaceController().goTo(newPlace);
							}
						});

						contentDropdownMenu.add(nl);

					} else if (content.getDiv() != null && !content.getDiv().isEmpty()) {
						setDivContent(content);
					}
					//					if (isEditor) {
					//						Element div = DOM.getElementById(content.getDiv());
					//						String id = DOM.createUniqueId();
					//						HTMLPanel panel = new HTMLPanel("<div id=\"" + id + "><span>" + c + "</span></div>");
					////						if (panel..hasChildNodes()) {
					////							panel.removeChild(div.getFirstChild());
					////						}
					//						
					//						panel.add(new HTML(c));
					//						Button edit = new Button("Edit");
					//						panel.add(edit);
					//						div.
					//						edit.addClickHandler(new ClickHandler() {
					//
					//							@Override
					//							public void onClick(ClickEvent event) {
					//								EditContent ec = clientFactory.getEditContentDialog();
					//								ec.setContent(content, contentPresenter);
					//								ec.center();
					//							}
					//							
					//						});
					//					} else {

					//}
				}
			}	
		}
	}

	private void setDivContent(final IContent content) {
//		String c = content.getBody().replace("<% players %>", "");
//		final EditContentPresenter contentPresenter = this;
//		if (isEditor) {
//			if (!getButtonMap().containsKey(content.getDiv())) {
//
//				Button edit = new Button("Edit " + content.getDiv());
//				edit.setType(ButtonType.DANGER);
//				edit.addClickHandler(new ClickHandler() {
//
//					@Override
//					public void onClick(ClickEvent event) {
//						EditContent ec = clientFactory.getEditContentDialog();
//						ec.setContent(content, contentPresenter);
//						ec.center();
//					}
//
//				});
//				getButtonBar().add(edit);
//				getButtonMap().put(content.getDiv(), edit);
//
//			} else {
//				// when refreshButtons is called in the TTLActivity it drops out button.
//				if (getButtonBar().getWidgetIndex(getButtonMap().get(content.getDiv())) == -1) {
//					getButtonBar().add(getButtonMap().get(content.getDiv()));
//				}
//			}
//		}
//		DOM.getElementById(content.getDiv()).setInnerHTML(c);
	}

//	@Override
//	public void collapseHero(boolean collapse) {
////		if (collapse) {
////			hero.removeClassName("hero-unit");
////			hero.addClassName("collapse");
////		} else {
////			hero.removeClassName("collapse");
////			hero.addClassName("hero-unit");
////		}
//
//	}

	
	protected void AddContentMenuItem(final IContent c, ListGroupItem p) {
//		tog.setHTML(clientFactory.getLoginInfo().getNickname() + "<b class=\"caret\"></b>");
		AnchorListItem linky = new AnchorListItem(c.getTitle());
		linky.setIcon(IconType.UNLOCK);
		linky.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ContentPlace place =  new ContentPlace(c.getId());
				clientFactory.getPlaceController().goTo(place);
			}
			
		});
		
		linky.addStyleDependentName("IdentityButton");
		
		linky.setIcon(IconType.COG);
		  		
		p.add(linky);
		linky.setVisible(true);

	}

}