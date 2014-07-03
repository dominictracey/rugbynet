/**
 * 
 */
package net.rugby.foundation.core.server.factory.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;


import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.Standing;

/**
 * @author home
 *
 */
public class TestStandingFactory extends BaseCachingFactory<IStanding> implements IStandingFactory, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5267158546061782777L;
	private IRoundFactory rf;
	private ITeamGroupFactory tf;

	@Inject
	public TestStandingFactory(IRoundFactory rf, ITeamGroupFactory tf) {
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

			return standing;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	// this only works for the test data in roundIds 2-5.
	protected IStanding getFromPersistentDatastore(Long id) {
		try {

			if (id != null) {
				IStanding t = new Standing();
				t.setId(id);
				t.setRoundId(id/10000L);
				t.setTeamId(id-t.getRoundId()*10000L);
				t.setRound(rf.get(t.getRoundId()));
				t.setTeam(tf.get(t.getTeamId()));
				long l = t.getTeamId();
				int i = (int) l-9000;
				t.setStanding(i);
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
			return true;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return false;
		}
	}

	@Override
	public List<IStanding> getForRound(IRound r) {
		List<IStanding> list = new ArrayList<IStanding>();
		for (IMatchGroup m : r.getMatches()) {
			Long sid = r.getId() * 10000L;
			sid += m.getHomeTeamId();
			list.add(get(sid));
			sid = r.getId() * 10000L + m.getVisitingTeamId();
			list.add(get(sid));
		}
		return list;
	}
}
