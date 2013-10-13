/**
 * 
 */
package net.rugby.foundation.game1.server.factory.ofy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.game1.server.factory.IMatchEntryFactory;
import net.rugby.foundation.game1.server.factory.IMatchStatsFactory;
import net.rugby.foundation.game1.shared.IMatchEntry;
import net.rugby.foundation.game1.shared.IMatchStats;
import net.rugby.foundation.game1.shared.MatchEntry;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IMatchGroup;

/**
 * @author home
 *
 */
public class OfyMatchEntryFactory implements IMatchEntryFactory {
	private Long id;
	private Objectify ofy;
	private final ITeamGroupFactory tf;
	private IMatchGroupFactory mf;
	private IMatchStatsFactory msf;
	
	@Inject
	OfyMatchEntryFactory(ITeamGroupFactory tf, IMatchGroupFactory mf, IMatchStatsFactory msf) {
		this.ofy = DataStoreFactory.getOfy();
		this.tf = tf;
		this.mf = mf;
		this.msf = msf;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IMatchEntryFactory#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IMatchEntryFactory#getMatchEntry()
	 */
	@Override
	public IMatchEntry getMatchEntry() {
		try {
			IMatchEntry me = ofy.find(new Key<MatchEntry>(MatchEntry.class,id));
			if (me == null) {
				return null;
			}
			Long tid = me.getTeamPickedId();
			me.setTeamPicked(tf.get(tid));
			return me;
		} catch (Throwable e) {
			Logger.getLogger("OfyMatchEntryFactory").log(Level.SEVERE, "getMatchEntry: " + e.getMessage(), e);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IMatchEntryFactory#put(net.rugby.foundation.game1.shared.IMatchEntry)
	 */
	@Override
	public IMatchEntry put(IMatchEntry e) {
		// don't put it if the match is locked
		IMatchGroup m = mf.get(e.getMatchId());
		if (m != null) {
			if (!m.getLocked()) {
				ofy.put(e);

				// update the MatchStats
				msf.setMatchId(m.getId());
				IMatchStats ms = msf.getMatchStatsShard();
				ms.setNumPicks(ms.getNumPicks()+1L);
				if (e.getTeamPickedId().equals(m.getHomeTeamId())) {
					ms.setNumHomePicks(ms.getNumHomePicks()+1L);
				}
				msf.put(ms);
				
				return e;			
			}
		}
		
		return null;

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IMatchEntryFactory#delete()
	 */
	@Override
	public Boolean delete() {
		Key<MatchEntry> key = new Key<MatchEntry>(MatchEntry.class,id);

		IMatchEntry e = ofy.find(key);  // find doesn't throw NotFoundException
		
		if (e != null) {
			// update the MatchStats
			IMatchGroup m = mf.get(e.getMatchId());
			if (m != null) {
				msf.setMatchId(e.getMatchId());
				IMatchStats ms = msf.getMatchStatsShard();
				ms.setNumPicks(ms.getNumPicks()-1L);
				if (e.getTeamPickedId().equals(m.getHomeTeamId())) {
					ms.setNumHomePicks(ms.getNumHomePicks()-1L);
				}
				msf.put(ms);
			}
			
			ofy.delete(key);
			return true;
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IMatchEntryFactory#getAll()
	 */
	@Override
	public Set<IMatchEntry> getAll() {
		Set<IMatchEntry> set = new HashSet<IMatchEntry>();
		List<MatchEntry> list = ofy.query(MatchEntry.class).list();
		for (MatchEntry me : list) {
			set.add(me);
		}

		return set;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IMatchEntryFactory#getForMatch(java.lang.Long)
	 */
	@Override
	public Set<IMatchEntry> getForMatch(Long matchId) {
		Set<IMatchEntry> mes = new HashSet<IMatchEntry>();
		List<MatchEntry> list = ofy.query(MatchEntry.class).filter("matchId =", matchId).list();
		for (MatchEntry me : list) {
			mes.add(me);
		}		
		return mes;
	}

//	/**
//	 * @param ofyMEtxn
//	 */
//	public void startTxn(Objectify ofyMEtxn) {
//		this.ofy = ofyMEtxn;
//		ofy.getTxn().commit();
//
//	}
//
//	public void endTxn(Objectify ofy) {
//		this.ofy = ofy;
//
//	}
}
