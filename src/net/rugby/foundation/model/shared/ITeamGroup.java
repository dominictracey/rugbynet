/**
 * 
 */
package net.rugby.foundation.model.shared;

import java.util.List;

/**
 * @author home
 *
 */
public interface ITeamGroup {

	public abstract String getGroupInfo();

	public abstract String getPool();

	public abstract void setPool(String pool);

	public abstract String getAbbr();

	public abstract void setAbbr(String abbr);

	/**
	 * @return
	 */
	public abstract String getShortName();

	public abstract void setShortName(String shortName);

	public abstract Long getId();

	/**
	 * @return
	 */
	public abstract String getDisplayName();
	
	/**
	 * @return #rrggbb color for the team
	 */
	public String getColor();
	
	/**
	 * @parameter color in #RRGGBB html format
	 */
	public void setColor(String color);

	
	
}