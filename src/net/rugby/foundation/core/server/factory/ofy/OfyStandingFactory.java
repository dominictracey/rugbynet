/**
 * 
 */
package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.Standing;

/**
 * @author home
 *
 */
public class OfyStandingFactory extends BaseCachingFactory<IStanding> implements IStandingFactory, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5267158546061782777L;
	private IRoundFactory rf;
	private ITeamGroupFactory tf;

	@Inject
	public OfyStandingFactory(IRoundFactory rf, ITeamGroupFactory tf) {
		this.rf = rf;
		this.tf = tf;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IStandingFactory#put(net.rugby.foundation.model.shared.IStanding)
	 */
	@Override
	public IStanding putToPersistentDatastore(IStanding standing) {
		try {
			if (standing == null) {
				return null;
			}
			Objectify ofy = DataStoreFactory.getOfy();
			ofy.put(standing);
			return standing;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	protected IStanding getFromPersistentDatastore(Long id) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			if (id != null) {
				IStanding t = ofy.get(new Key<Standing>(Standing.class,id));
				rf.setId(t.getRoundId());
				t.setRound(rf.getRound());
				t.setTeam(tf.get(t.getTeamId()));
				return t;
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Don't try to get with null. Call create() instead!");
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public IStanding create() {
		try {
			IStanding p = new Standing();
			return p;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}


	@Override
	protected boolean deleteFromPersistentDatastore(IStanding t) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			ofy.delete(t);
			return true;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return false;
		}
	}

	@Override
	public List<IStanding> getForRound(IRound r) {
		List<IStanding> list = new ArrayList<IStanding>();
		Objectify ofy = DataStoreFactory.getOfy();
		Query<Standing> qs = ofy.query(Standing.class).filter("roundId", r.getId()).order("standing");
		if (qs.count() != 0) {
			for (Standing s : qs) {
				rf.setId(s.getRoundId());
				s.setRound(rf.getRound());
				s.setTeam(tf.get(s.getTeamId()));
				list.add(s);
			}
		} else { // give default to everyone
			Map<Long,ITeamGroup> teamMap = new HashMap<Long, ITeamGroup>();
			for (IMatchGroup m: r.getMatches()) {
				if (!teamMap.containsKey(m.getHomeTeamId())) {
					teamMap.put(m.getHomeTeamId(), m.getHomeTeam());
					list.add(createDefault(r,m.getHomeTeam()));
				} 
				if (!teamMap.containsKey(m.getVisitingTeamId())) {
					teamMap.put(m.getVisitingTeamId(), m.getVisitingTeam());
					list.add(createDefault(r,m.getVisitingTeam()));
				}
			}
		}
		return list;
	}
	
	private IStanding createDefault(IRound r, ITeamGroup t) {
		IStanding s = create();
		s.setRound(r);
		s.setRoundId(r.getId());
		s.setTeam(t);
		s.setTeamId(t.getId());
		s.setStanding(1);
		return s;
	}
}
