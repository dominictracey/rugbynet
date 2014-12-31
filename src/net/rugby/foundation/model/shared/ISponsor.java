package net.rugby.foundation.model.shared;

public interface ISponsor extends IHasId {

	public abstract String getTagline();

	public abstract void setTagline(String tagline);

	public abstract String getAbbr();

	public abstract void setAbbr(String abbr);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getEmail();

	public abstract void setEmail(String email);

	public abstract String getContactName();

	public abstract void setContactName(String contactName);

	boolean isActive();

	void setActive(boolean active);

}