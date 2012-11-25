package net.rugby.foundation.model.shared;

import java.net.URI;
import java.util.Date;

public interface IPlayer {

	public abstract Long getId();
	public abstract void setId(Long id);

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
	
	public abstract String getCountry();
	public abstract void setCountry(String country);
	
	public abstract Position.position getPosition();
	public abstract void setPosition(Position.position position);
	
	public abstract Integer getNumCaps();
	public abstract void setNumCaps(Integer numCaps);
	
}
