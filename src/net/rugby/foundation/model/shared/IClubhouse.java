/**
 * 
 */
package net.rugby.foundation.model.shared;

/**
 * @author home
 *
 */
public interface IClubhouse {

	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getDescription();

	public abstract void setDescription(String description);

	public abstract Long getLastWinnerID();

	public abstract void setLastWinnerID(Long lastWinnerID);

	public abstract Long getHomeID();

	public abstract void setHomeID(Long homeID);

	public abstract Long getOwnerID();

	public abstract void setOwnerID(Long ownerID);

	public abstract String getJoinLink();

	public abstract void setJoinLink(String joinLink);
	
	public abstract Boolean getActive();

	public abstract void setActive(Boolean active);

	/**
	 * @param publicClubhouse - Sets whether or not the clubhouse is visible and open for joining for anyone
	 */
	public abstract void setPublicClubhouse(Boolean publicClubhouse);
	

	/**
	 * @returns publicClubhouse - Gets whether or not the clubhouse is visible and open for joining for anyone
	 */
	public abstract Boolean setPublicClubhouse();
	
	
}