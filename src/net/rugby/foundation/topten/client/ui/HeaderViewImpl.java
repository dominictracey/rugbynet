package net.rugby.foundation.topten.client.ui;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.place.ContentPlace;
import net.rugby.foundation.topten.client.ui.content.EditContent.EditContentPresenter;

import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.ListGroupItem;
import org.gwtbootstrap3.client.ui.Nav;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;


public class HeaderViewImpl extends Composite implements HeaderView, EditContentPresenter 
{
	private static NavBarViewImplUiBinder uiBinder = GWT.create(NavBarViewImplUiBinder.class);

	@UiField Nav nav;
	@UiField ListGroupItem loginDropdown;
	@UiField ListGroupItem contentDropdown;
	

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
		
		nav.addStyleName("navbar-nav");
		nav.addStyleName("pull-right");
		nav.addStyleName("hidden-xs");


	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.client.ui.NavBarView#setComps(java.util.Map, java.util.List)
	 */
	@Override
	public void setComps(Map<Long, String> competitionMap, List<Long> compsUnderway) {
//		ListIterator<Long> it = compsUnderway.listIterator(compsUnderway.size());
//		if (it != null) {
//			compDropdown.clear();
//			while (it.hasPrevious()) {
//				final Long compId = it.previous();
//				NavLink nl = new NavLink(competitionMap.get(compId));
//				nl.addClickHandler( new ClickHandler() {
//
//					@Override
//					public void onClick(ClickEvent event) {
//						TopTenListPlace newPlace = new TopTenListPlace();
//						newPlace.setCompId(compId);
//						assert (clientFactory != null);
//						clientFactory.getPlaceController().goTo(newPlace);
//					}
//				});
//				compDropdown.add(nl);
//
//			}
//			compDropdown.addStyleName("transparentMenuBG");
//		}

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
				contentDropdown.clear();
				contentDropdown.addStyleName("transparentMenuBG");
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

						contentDropdown.add(nl);
						//					if (content.isShowInFooter()) {
						//						NavLink a = new NavLink(content.getTitle());
						//						a.addClickHandler( new ClickHandler() {
						//
						//							@Override
						//							public void onClick(ClickEvent event) {
						//								ContentPlace newPlace = new ContentPlace(content.getId());
						//								assert (clientFactory != null);
						//								clientFactory.getPlaceController().goTo(newPlace);
						//							}
						//						});
						//						footerLinks.appendChild(a.getElement());
						//					}
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


	@Override
	public void saveContent(IContent content) {
		Core.getCore().saveContent(content, new AsyncCallback<IContent>() {
			@Override
			public void onFailure(Throwable caught) {
				// fail silently
			}

			@Override
			public void onSuccess(final IContent result) {
				setDivContent(result);
				clientFactory.getEditContentDialog().hide();
			}
		});

	}
//
//	private Map<String, Button> getButtonMap() {
//		if (buttonMap == null) {
//			buttonMap = new HashMap<String,Button>();
//		}
//		return buttonMap;
//	}

	@Override
	public void cancelEditContent() {
		clientFactory.getEditContentDialog().hide();

	}

}