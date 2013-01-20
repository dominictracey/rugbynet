package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.Group;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.TeamGroup;

public class OfyTeamFactory implements ITeamGroupFactory, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7135535109216170518L;
	private Long id;
	//private final Objectify ofy;
	
//	@Inject
//	OfyTeamFactory() {
//		this.ofy = DataStoreFactory.getOfy();
//	}
		@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public ITeamGroup getTeam() {
		if (id == null) {
			return new TeamGroup();
		}
		
		Objectify ofy = DataStoreFactory.getOfy();
		ITeamGroup t = (ITeamGroup)ofy.get(new Key<Group>(Group.class,id));
		return t;
	}
	@Override
	public ITeamGroup put(ITeamGroup team) {
		if (team == null) {
			team = new TeamGroup();
			team.setShortName("TDB");
		}
		Objectify ofy = DataStoreFactory.getOfy();
		ofy.put(team);
		
		return team;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ITeamGroupFactory#getTeamByName(java.lang.String)
	 */
	@Override
	public ITeamGroup getTeamByName(String name) {
		Objectify ofy = DataStoreFactory.getOfy();
		Query<Group> team = ofy.query(Group.class).filter("displayName", name);
				
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
