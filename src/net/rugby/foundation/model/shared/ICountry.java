package net.rugby.foundation.model.shared;

public interface ICountry {

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getIrb();

	public abstract void setIrb(String irb);

	public abstract String getAbbr();

	public abstract void setAbbr(String abbr);

	public abstract String getNickName();

	public abstract void setNickName(String nickName);

	public abstract Long getId();

	public abstract void setId(Long id);

}