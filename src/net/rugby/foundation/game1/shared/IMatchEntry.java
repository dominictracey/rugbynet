/**
 * 
 */
package net.rugby.foundation.game1.shared;

import net.rugby.foundation.model.shared.ITeamGroup;

/**
 * @author home
 *
 */
public interface IMatchEntry {

	public abstract Long getId();
	
	public abstract void setId(Long id);

	public abstract Long getMatchId();

	public abstract void setMatchId(Long matchId);

	public abstract Long getTeamPickedId();

	public abstract void setTeamPickedId(Long teamId);

	public abstract ITeamGroup getTeamPicked();

	public abstract void setTeamPicked(ITeamGroup team);

	

}