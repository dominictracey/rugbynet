package net.rugby.foundation.model.shared;

import java.util.Date;

public interface IPlayer extends IHasId {

	public abstract Long getScrumId();
	public abstract void setScrumId(Long scrumId);
	
	public abstract String getDisplayName();
	public abstract void setDisplayName(String displayName);
	
	public abstract Date getBirthDate();
	public abstract void setBirthDate(Date birthDay);
	
	/**
	 * 
	 * @return height in cm
	 */
	public abstract Float getHeight();
	
	/**
	 * 
	 * @param height in cm
	 */
	public abstract void setHeight(Float height);
	
	/**
	 * 
	 * @return weight in kg
	 */
	public abstract Float getWeight();
	
	/**
	 * 
	 * @param weight in kg
	 */
	public abstract void setWeight(Float weight);
	
	public abstract String getImageUri();
	public abstract void setImageUri(String imageUri);
	
	public abstract Long getCountryId();
	public abstract void setCountryId(Long id);
	public abstract ICountry getCountry();
	public abstract void setCountry(ICountry country);
	
	public abstract Position.position getPosition();
	public abstract void setPosition(Position.position position);
	
	public abstract Integer getNumCaps();
	public abstract void setNumCaps(Integer numCaps);
	public abstract void setSurName(String string);
	public abstract String getSurName();
	
	public abstract void setGivenName(String givenName);
	public abstract String getGivenName();
	/**
	 * 
	 * @return name as it appears on stat sheets
	 */
	public abstract String getShortName();
	/**
	 * 
	 * @param name as it appears on stat sheets
	 */
	public abstract void setShortName(String shortName);
	public abstract String getForeignUrl();
	public abstract String getTwitterHandle();
	public abstract void setTwitterHandle(String twitterHandle);
	
	
}
