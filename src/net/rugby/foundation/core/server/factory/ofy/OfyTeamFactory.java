package net.rugby.foundation.core.server.factory.ofy;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.Group;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.TeamGroup;

public class OfyTeamFactory implements ITeamGroupFactory {
	private Long id;
	private final Objectify ofy;
	
	@Inject
	OfyTeamFactory() {
		this.ofy = DataStoreFactory.getOfy();
	}
		@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public ITeamGroup getTeam() {
		ITeamGroup t = (ITeamGroup)ofy.get(new Key<Group>(Group.class,id));
		return t;
	}
	@Override
	public ITeamGroup put(ITeamGroup team) {
		if (team == null) {
			team = new TeamGroup();
			team.setShortName("TDB");
		}
		
		ofy.put(team);
		
		return team;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ITeamGroupFactory#getTeamByName(java.lang.String)
	 */
	@Override
	public ITeamGroup getTeamByName(String name) {
		Query<Group> team = ofy.query(Group.class).filter("displayName", name);
		
		assert(team.count() == 1);
		
		if (team.count() == 1) {
			return (ITeamGroup)team.get();
		}
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ITeamGroupFactory#find(net.rugby.foundation.model.shared.ITeamGroup)
	 */
	@Override
	public ITeamGroup find(ITeamGroup team) {
		ITeamGroup g = getTeamByName(team.getDisplayName());
		
		if (team.equals(g)) {
			return g; // has Id set (parameter may not)
		}
		return null;
	}

}
