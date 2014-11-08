package net.rugby.foundation.topten.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.place.ContentPlace;
import net.rugby.foundation.topten.client.place.TopTenListPlace;
import net.rugby.foundation.topten.client.ui.content.EditContent;
import net.rugby.foundation.topten.client.ui.content.EditContent.EditContentPresenter;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavPills;
import com.github.gwtbootstrap.client.ui.NavWidget;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;


public class HeaderViewImpl extends Composite implements HeaderView, EditContentPresenter 
{
	private static NavBarViewImplUiBinder uiBinder = GWT.create(NavBarViewImplUiBinder.class);


	@UiField NavPills loginPanel;
	@UiField Dropdown compDropdown;
	@UiField Dropdown contentDropdown;
	Element title;
	Element details1;
	Element details2;
	Element fbLike;
	Element footerLinks;
	Element hero;
	NavWidget buttonBar;

	private ClientFactory clientFactory;


	private boolean isEditor;

	private Map<String,Button> buttonMap = null;

	@UiTemplate("HeaderViewImpl.ui.xml")

	interface NavBarViewImplUiBinder extends UiBinder<Widget, HeaderViewImpl>
	{
	}


	public HeaderViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
		title = DOM.getElementById("heading");
		details1 = DOM.getElementById("details1");
		details2 = DOM.getElementById("details2");
		fbLike = DOM.getElementById("fbLike");
		footerLinks = DOM.getElementById("footerLinks");
		hero = DOM.getElementById("hero");


	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.client.ui.NavBarView#setComps(java.util.Map, java.util.List)
	 */
	@Override
	public void setComps(Map<Long, String> competitionMap, List<Long> compsUnderway) {
		ListIterator<Long> it = compsUnderway.listIterator(compsUnderway.size());
		if (it != null) {
			compDropdown.clear();
			while (it.hasPrevious()) {
				final Long compId = it.previous();
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
			compDropdown.addStyleName("transparentMenuBG");
		}

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.client.ui.NavBarView#addLoginPanel(com.google.gwt.user.client.ui.HorizontalPanel)
	 */
	@Override
	public void addLoginPanel(HorizontalPanel acct) {
		loginPanel.add(acct);

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.client.ui.NavBarView#getLoginPanel()
	 */
	@Override
	public NavPills getLoginPanel() {
		return loginPanel;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.client.ui.NavBarView#setClientFactory(net.rugby.foundation.topten.client.ClientFactory)
	 */
	@Override
	public void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.client.ui.NavBarView#setHeroListInfo(java.lang.String, java.lang.String)
	 */
	@Override
	public void setHeroListInfo(String title1, String details11) {
//		title.setInnerHTML(title1);
//		details1.setInnerHTML(details11);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.client.ui.NavBarView#setDetails(java.lang.String)
	 */
	@Override
	public void setDetails(String details11) {
		//details2.setInnerHTML(details11);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.client.ui.NavBarView#setFBLikeAttribute(java.lang.String, java.lang.String)
	 */
	@Override
	public void setFBLikeAttribute(String name, String value) {
		fbLike.setAttribute(name, value);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.client.ui.NavBarView#getButtonBar()
	 */
	@Override
	public NavWidget getButtonBar() {
		if (buttonBar == null) {
			buttonBar = new NavWidget();
			//buttonBar.addStyleName("btn-group");
			RootPanel.get("buttons").add(buttonBar);
		}
		return buttonBar;
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
					if (content.isShowInMenu()) {
						NavLink nl = new NavLink(content.getTitle());
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
					} else if (!content.getDiv().isEmpty()) {
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
		String c = content.getBody().replace("<% players %>", "");
		final EditContentPresenter contentPresenter = this;
		if (isEditor) {
			if (!getButtonMap().containsKey(content.getDiv())) {

				Button edit = new Button("Edit " + content.getDiv());
				edit.setType(ButtonType.DANGER);
				edit.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						EditContent ec = clientFactory.getEditContentDialog();
						ec.setContent(content, contentPresenter);
						ec.center();
					}

				});
				getButtonBar().add(edit);
				getButtonMap().put(content.getDiv(), edit);

			} else {
				// when refreshButtons is called in the TTLActivity it drops out button.
				if (getButtonBar().getWidgetIndex(getButtonMap().get(content.getDiv())) == -1) {
					getButtonBar().add(getButtonMap().get(content.getDiv()));
				}
			}
		}
		DOM.getElementById(content.getDiv()).setInnerHTML(c);
	}

	@Override
	public void collapseHero(boolean collapse) {
//		if (collapse) {
//			hero.removeClassName("hero-unit");
//			hero.addClassName("collapse");
//		} else {
//			hero.removeClassName("collapse");
//			hero.addClassName("hero-unit");
//		}

	}


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

	private Map<String, Button> getButtonMap() {
		if (buttonMap == null) {
			buttonMap = new HashMap<String,Button>();
		}
		return buttonMap;
	}

	@Override
	public void cancelEditContent() {
		clientFactory.getEditContentDialog().hide();

	}


	@Override
	public void setHeroTextBig(Boolean big) {
		if (!big) {
			details1.removeClassName("lead");
			details2.removeClassName("lead");
			details1.addClassName("compactContent");
			details2.addClassName("compactContent");
		} else {
			details1.addClassName("lead");
			details2.addClassName("lead");
			details1.removeClassName("compactContent");
			details2.removeClassName("compactContent");
		}

	}
}