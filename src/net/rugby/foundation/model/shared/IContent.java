package net.rugby.foundation.model.shared;

public interface IContent extends IHasId {

	public abstract String getBody();

	public abstract void setBody(String body);

	void setDiv(String div);

	String getDiv();

	void setMenuName(String menuName);

	String getMenuName();

	void setMenuOrder(Integer menuOrder);

	Integer getMenuOrder();

	void setActive(boolean active);

	boolean isActive();

	void setTitle(String title);

	String getTitle();

	void setShowInFooter(boolean showInFooter);

	boolean isShowInFooter();

	void setShowInMenu(boolean showInMenu);

	boolean isShowInMenu();


}