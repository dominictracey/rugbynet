package net.rugby.foundation.model.shared;

import java.io.Serializable;

import javax.persistence.Id;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class Content extends HasInfo implements IHasId, IContent, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7643896023680307842L;
	@Id
	Long id;
	String body;
	String title;
	boolean active;
	Integer menuOrder;
	String menuName;
	String div;
	boolean showInMenu;
	boolean showInFooter;
	
	public Content() {}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IContent#getBody()
	 */
	@Override
	public String getBody() {
		return body;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IContent#setBody(java.lang.String)
	 */
	@Override
	public void setBody(String body) {
		this.body = body;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IContent#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	public Content(Long id, String text) {
		super();
		this.id = id;
		this.body = text;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
		
	}
	@Override
	public String getTitle() {
		return title;
	}
	@Override
	public void setTitle(String title) {
		this.title = title;
	}
	@Override
	public boolean isActive() {
		return active;
	}
	@Override
	public void setActive(boolean active) {
		this.active = active;
	}
	@Override
	public Integer getMenuOrder() {
		return menuOrder;
	}
	@Override
	public void setMenuOrder(Integer menuOrder) {
		this.menuOrder = menuOrder;
	}
	@Override
	public String getMenuName() {
		return menuName;
	}
	@Override
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	@Override
	public String getDiv() {
		return div;
	}
	@Override
	public void setDiv(String div) {
		this.div = div;
	}
	@Override
	public boolean isShowInMenu() {
		return showInMenu;
	}
	@Override
	public void setShowInMenu(boolean showInMenu) {
		this.showInMenu = showInMenu;
	}
	@Override
	public boolean isShowInFooter() {
		return showInFooter;
	}
	@Override
	public void setShowInFooter(boolean showInFooter) {
		this.showInFooter = showInFooter;
	}
	
}
