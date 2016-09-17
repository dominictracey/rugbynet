package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.server.factory.BaseTeamFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.Group;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.TeamGroup;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

public class OfyTeamFactory extends BaseTeamFactory implements ITeamGroupFactory, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7135535109216170518L;

	@Override
	public ITeamGroup getFromPersistentDatastore(Long id) {
		if (id == null) {
			throw new RuntimeException("Call create to get a new team.");
		}

		Objectify ofy = DataStoreFactory.getOfy();
		ITeamGroup t = (ITeamGroup)ofy.get(new Key<Group>(Group.class,id));
		if (t.getSnakeCaseDisplayName() == null) {
			ofy.put(t); //save newly updated snake case name to database
		}
		return t;
	}

	@Override
	public ITeamGroup putToPersistentDatastore(ITeamGroup team) {
		if (team == null) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Don't call TeamFactory.put with null parameter please, Use create()");
			return null;
//			team = new TeamGroup();
//			team.setShortName("TDB");
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

		if (team.count() > 0) {
			return (ITeamGroup)team.list().get(0);
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

	@Override
	public ITeamGroup create() {
		return new TeamGroup();
	}

	@Override
	protected boolean deleteFromPersistentDatastore(ITeamGroup t) {
		Objectify ofy = DataStoreFactory.getOfy();
		ofy.delete(t);
		return true;
	}

	@Override
	protected HashMap<Long, String> getTeamLogoStyleMapFromPersistentDatastore() {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			HashMap<Long, String> map = new HashMap<Long, String>();

			Query<Group> team = ofy.query(Group.class).filter("groupType", "TEAM");

			if (team.count() > 0) {
				for (Group t : team.list()) {
					if (!((ITeamGroup)t).getAbbr().isEmpty()) {
						map.put(t.getId(), ((ITeamGroup)t).getAbbr());
					}
				}
			}

			return map;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"getTeamLogoStyleMapFromPersistentDatastore", ex);
			return null;
		}
	}

	@Override
	public ITeamGroup getTeamBySnakeCaseDisplayName(String snakeCaseName) {
		Objectify ofy = DataStoreFactory.getOfy();
		Query<Group> team = ofy.query(Group.class).filter("snakeCaseDisplayName", snakeCaseName);

		if (team.count() > 0) {
			return (ITeamGroup)team.list().get(0);
		}

		return null;
	}

	@Override
	public ITeamGroup getTeamByScrumName(String teamName) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			Query<Group> team = ofy.query(Group.class).filter("scrumName", teamName);

			if (team.count() == 0) {
				team = ofy.query(Group.class).filter("displayName", teamName);
			}
			
			if (team.list().size() > 1) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"We have more than one team group in the dataBase that have either the same displayName or the same scrumName. This is most likely a Bad Thing(tm)!");
			}
			
			if (team.count() > 0) {
				return (ITeamGroup)team.list().get(0);
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"getTeamByScrumName", ex);
			return null;
		}
	}

	@Override
	public ITeamGroup getTeamByForeignId(Long foreignId) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			Query<Group> team = ofy.query(Group.class).filter("foreignId", foreignId);

			if (team.list().size() > 1) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"We have more than one team group in the dataBase that have either the same foreignId. This is most likely a Bad Thing(tm)!");
			}
			
			if (team.count() > 0) {
				return (ITeamGroup)team.list().get(0);
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"getTeamByForeignId", ex);
			return null;
		}
	}
}
