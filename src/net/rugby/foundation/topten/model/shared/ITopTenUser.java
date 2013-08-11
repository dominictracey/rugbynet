package net.rugby.foundation.topten.model.shared;

public interface ITopTenUser {
	public abstract boolean isTopTenContentContributor();
	public abstract void setTopTenContentContributor(boolean b);
	
	public abstract boolean isTopTenContentEditor();
	public abstract void setTopTenContentEditor(boolean b);
}
