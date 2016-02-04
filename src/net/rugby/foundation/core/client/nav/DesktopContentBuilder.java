package net.rugby.foundation.core.client.nav;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.rugby.foundation.core.client.CoreClientFactory;
import net.rugby.foundation.core.client.nav.INavManager.IContentPresenter;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.LoginInfo;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.ListGroupItem;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Toggle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/*
 * <!-- 				<b:ListGroupItem ui:field="contentDropdown"> -->
<!-- 					<b:Anchor dataToggle="DROPDOWN">WTF</b:Anchor> -->
<!-- 					<b:DropDownMenu ui:field="contentDropdownMenu"> -->
<!-- <!-- 						<b:AnchorListItem>Action 1</b:AnchorListItem> --> -->
<!-- <!--     					<b:AnchorListItem icon="CAMERA">Action 2</b:AnchorListItem> --> -->
<!-- 					</b:DropDownMenu> -->
<!-- 				</b:ListGroupItem> -->
<!-- 				<b:ListGroupItem ui:field="loginDropdown"> -->
<!-- 				</b:ListGroupItem> -->
 */

public class DesktopContentBuilder extends ContentBuilder {

	private ListGroupItem contentDropdown;
	private DropDownMenu contentDropdownMenu;
	private Anchor wtf;
	private IContentPresenter presenter;
	
	public DesktopContentBuilder(CoreClientFactory clientFactory, LoginInfo loginInfo) {
		this.clientFactory = clientFactory;
		this.loginInfo = loginInfo;
		
		contentDropdown = new ListGroupItem();
		contentDropdown.setStyleName("dropdown");
		
		wtf = new Anchor();
		wtf.setDataToggle(Toggle.DROPDOWN);
		wtf.setHTML("About");
		
		contentDropdownMenu = new DropDownMenu();
		contentDropdownMenu.addStyleName("dropdown-menu-right");
		
		contentDropdown.add(wtf);
		contentDropdown.add(contentDropdownMenu);
	}

	@Override
	public void build(final HashMap<String, Long> map) {
		try {
			this.map = map;
			
			if (parent.isAttached()) {
				parent.addStyleName("navbar-nav");
				parent.addStyleName("pull-right");
				parent.addStyleName("hidden-xs");
			} else {
				clientFactory.console("no parent div set for DesktopContentBuilder.build");
			}
			
			if (map != null) {
				List<String> list = new ArrayList<String>();
				list.addAll(map.keySet());
				Collections.sort(list);
				contentDropdownMenu.clear();
				for (String s: list) {
	
					final String _s = s;
					AnchorListItem nl = new AnchorListItem(s.substring(3));
					nl.addClickHandler( new ClickHandler() {
	
						@Override
						public void onClick(ClickEvent event) {
							if (presenter != null) {
								presenter.show(map.get(_s));
							}
						}
					});
	
					contentDropdownMenu.add(nl);
					
				}	
			}
			
			parent.add(contentDropdown);
			
		} catch (Exception e) {
			clientFactory.console(e.getLocalizedMessage());
		}
	}

	protected void AddContentMenuItem(final IContent c, ListGroupItem p) {
//		tog.setHTML(clientFactory.getLoginInfo().getNickname() + "<b class=\"caret\"></b>");
		AnchorListItem linky = new AnchorListItem(c.getTitle());
		linky.setIcon(IconType.UNLOCK);
		linky.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (presenter != null) {
					presenter.show(c.getId());
				}
			}
			
		});
		
		linky.addStyleDependentName("IdentityButton");
		
		linky.setIcon(IconType.COG);
		  		
		p.add(linky);
		linky.setVisible(true);

	}

	public void setContentPresenter(IContentPresenter contentPresenter) {
		this.presenter = contentPresenter;
		
	}
}
