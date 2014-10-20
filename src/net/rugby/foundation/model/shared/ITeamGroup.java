/**
 * 
 */
package net.rugby.foundation.model.shared;

/**
 * @author home
 *
 */
public interface ITeamGroup extends IGroup {

	public abstract String getGroupInfo();

	public abstract String getPool();

	public abstract void setPool(String pool);

	public abstract String getAbbr();

	public abstract void setAbbr(String abbr);

	public abstract void setTwitter(String twitter);
	public abstract String getTwitter();
	
	/**
	 * @return
	 */
	public abstract String getShortName();

	public abstract void setShortName(String shortName);

//	public abstract Long getId();

//	/**
//	 * @return
//	 */
//	public abstract String getDisplayName();
//	
//	public abstract void setDisplayName(String displayName);

	
	/**
	 * @return #rrggbb color for the team
	 */
	public String getColor();
	
	/**
	 * @parameter color in #RRGGBB html format
	 */
	public void setColor(String color);

	
	
}