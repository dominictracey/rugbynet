package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.BaseMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.Group;
import net.rugby.foundation.model.shared.ISimpleScoreMatchResult;
import net.rugby.foundation.model.shared.MatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup;

public class OfyMatchGroupFactory extends BaseMatchGroupFactory implements Serializable,IMatchGroupFactory {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 5536925770981961238L;


	@Override
	protected IMatchGroup getFromPersistentDatastore(Long id) {
		MatchGroup g = null;
		if (id != null) {
			Objectify ofy = DataStoreFactory.getOfy();
			g = ofy.get(new Key<MatchGroup>(MatchGroup.class,id));
			tf.setId(g.getHomeTeamId());
			g.setHomeTeam(tf.getTeam());
			tf.setId(g.getVisitingTeamId());
			g.setVisitingTeam(tf.getTeam());

			if (g.getSimpleScoreMatchResultId() != null) {
				mrf.setId(g.getSimpleScoreMatchResultId());
				g.setSimpleScoreMatchResult((ISimpleScoreMatchResult) mrf.get());  // @REX need to sort this out before other types of results are added
			}
		} else { // create new
			// @REX cast
			g = (MatchGroup) put(null);
		}
		return g;
	}



	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IMatchGroupFactory#find(net.rugby.foundation.model.shared.IMatchGroup)
	 * 
	 * It's ok not to cache this as it is just used (as of 7/13/13) in the competition import/set-up
	 */
	@Override
	public IMatchGroup find(IMatchGroup match) {
		Objectify ofy = DataStoreFactory.getOfy();
		// @REX should we just check the match - don't we need to also cross-check against comp as well?
		Query<Group> qg = ofy.query(Group.class).filter("displayName",match.getDisplayName()).filter("date", match.getDate());

		if (qg.count() != 1) {
			return null;
		}

		if (qg.get() instanceof IMatchGroup) {
			IMatchGroup g = (IMatchGroup)qg.get();
			tf.setId(g.getHomeTeamId());
			g.setHomeTeam(tf.getTeam());
			tf.setId(g.getVisitingTeamId());
			g.setVisitingTeam(tf.getTeam());
			if (g.equals(match)) {
				return g; // will have an id since it came out of the db
			}
		}
		return null;
	}



	@Override
	public List<? extends IMatchGroup> getMatchesWithPipelines() {
		Objectify ofy = DataStoreFactory.getOfy();
		// @REX should we just check the match - don't we need to also cross-check against comp as well?
		Query<MatchGroup> qg = ofy.query(MatchGroup.class).filter("fetchMatchStatsPipelineId !=", null);
		List<IMatchGroup> list = new ArrayList<IMatchGroup>();
		Iterator<MatchGroup> it = qg.list().iterator();
		while (it.hasNext()) {
			IMatchGroup g = (IMatchGroup)it.next();
			tf.setId(g.getHomeTeamId());
			g.setHomeTeam(tf.getTeam());
			tf.setId(g.getVisitingTeamId());
			g.setVisitingTeam(tf.getTeam());
			list.add(g);
		}
		return list;
	}

	@Override
	public boolean deleteFromPersistentDatastore(IMatchGroup m) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			ofy.delete(m);
			return true;

		} catch (Throwable ex) {
			String id = "null";
			if (m != null) {
				id = m.getId().toString();
			}
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Error deleting match " + id + ex.getLocalizedMessage());
			return false;
		}

	}



	@Override
	public IMatchGroup create() {
		try {
		IMatchGroup m =  new MatchGroup();
		//m.setCreatedDate(new Date());
		return m;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Error creating match " + ex.getLocalizedMessage(), ex);
			return null;
		}
	}



	@Override
	protected IMatchGroup putToPersistentDatastore(IMatchGroup m) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			ofy.put(m);
			return m;

		} catch (Throwable ex) {
			String id = "null";
			if (m != null) {
				id = m.getId().toString();
			}
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Error deleting match " + id + ex.getLocalizedMessage(), ex);
			return null;
		}
	}



}
