package net.rugby.foundation.core.server.factory;

import net.rugby.foundation.model.shared.ITeamGroup;

public interface ITeamGroupFactory {
	void setId(Long id);
	
	ITeamGroup getTeam();
	ITeamGroup getTeamByName(String name);
	
	/**
	 * @return an existing ITeamGroup that matches the attributes of the passed in ITeamGroup (except id). Saves us from creating duplicates on accident.
	 */
	ITeamGroup find(ITeamGroup team);
	ITeamGroup put(ITeamGroup team);
}
