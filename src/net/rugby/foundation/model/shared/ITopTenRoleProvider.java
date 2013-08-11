package net.rugby.foundation.model.shared;

public interface ITopTenRoleProvider {
	public abstract boolean isTopTenContentEditor();
	public abstract boolean isTopTenContentContributor();
	void setTopTenContentContributor(boolean set);
	void setTopTenContentEditor(boolean set);
}
