/**
 * 
 */
package net.rugby.foundation.model.shared;

import java.util.Date;

/**
 * A clubhouse is essentially a collection of IAppUsers, as opposed to a league which is a collection of IEntrys.
 * 
 * A clubhouse is generic (game-agnostic) while a league is game-specific and emphemeral.
 * 
 * @author DPT
 *
 */
public interface IClubhouseMembership {

	public abstract Long getAppUserID();

	public abstract void setAppUserID(Long appUserID);

	public abstract Long getClubhouseID();

	public abstract void setClubhouseID(Long clubhouseID);

	public abstract String getUserName();

	public abstract void setUserName(String userName);

	public abstract Date getJoined();

	public abstract void setJoined(Date joined);

	public abstract Long getId();

	public abstract void setId(Long id);

}