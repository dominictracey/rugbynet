package net.rugby.foundation.core.server.factory;

import java.util.HashMap;

import net.rugby.foundation.model.shared.ITeamGroup;

public interface ITeamGroupFactory extends ICachingFactory<ITeamGroup> {
	
	ITeamGroup getTeamByName(String name);
	
	/**
	 * @return an existing ITeamGroup that matches the attributes of the passed in ITeamGroup (except id). Saves us from creating duplicates on accident.
	 */
	ITeamGroup find(ITeamGroup team);

	HashMap<Long, String> getTeamLogoStyleMap();

	ITeamGroup getTeamBySnakeCaseDisplayName(String snakeCaseName);

	/**
	 * Sometimes scrum.com has weird names: there is a character encoding problem with Stade Francais, for instance. 
	 * Also the new Super Rugby teams are Argentina Super Rugby Team, etc.
	 * 
	 * @param teamName as it appears on espn. 
	 * @return our version of the IMatchGroup
	 */
	ITeamGroup getTeamByScrumName(String teamName);

}
