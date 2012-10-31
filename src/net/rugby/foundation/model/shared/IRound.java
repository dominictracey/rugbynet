/**
 * 
 */
package net.rugby.foundation.model.shared;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author home
 *
 */
public interface IRound {

	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract ArrayList<Long> getMatchIDs();
	public abstract void  setMatchIDs(ArrayList<Long> ids);
	public abstract ArrayList<IMatchGroup> getMatches();
	public abstract void  setMatches(ArrayList<IMatchGroup> ids);
	
	public abstract Date getBegin();

	public abstract void setBegin(Date begin);

	public abstract Date getEnd();

	public abstract void setEnd(Date end);

	public abstract Date getComputed();

	public abstract void setComputed(Date computed);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getAbbr();

	public abstract void setAbbr(String abbr);

	/**
	 * @param e
	 */
	public abstract void setOrdinal(Integer e);

	public abstract Integer getOrdinal();

	/**
	 * @param mg
	 */
	public abstract void addMatch(IMatchGroup mg);
	
	public abstract Long getCompId();
	public abstract void setCompId(Long compId);
	

}