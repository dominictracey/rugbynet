package net.rugby.foundation.game1.server.factory.ofy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.game1.server.factory.IMatchEntryFactory;
import net.rugby.foundation.game1.server.factory.IRoundEntryFactory;
import net.rugby.foundation.game1.shared.IMatchEntry;
import net.rugby.foundation.game1.shared.IRoundEntry;
import net.rugby.foundation.game1.shared.RoundEntry;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IMatchGroup;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;


public class OfyRoundEntryFactory implements IRoundEntryFactory {
	private Long id;
	private final Objectify ofy;
	private IMatchEntryFactory imf;
	private IMatchGroupFactory mgf;

	@Inject
	OfyRoundEntryFactory(IMatchEntryFactory imf, IMatchGroupFactory mgf) {
		this.ofy = DataStoreFactory.getOfy();
		this.imf = imf;
		this.mgf = mgf;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IRoundEntryFactory#getRoundEntry()
	 */
	@Override
	public IRoundEntry getRoundEntry() {

		IRoundEntry re = ofy.get(new Key<RoundEntry>(RoundEntry.class,id));

		if (re != null) {
			for (Long mid : re.getMatchPickIdList()) {
				if (mid != null) {
					imf.setId(mid);
					IMatchEntry ime = imf.getMatchEntry();
					if (ime != null)
						re.getMatchPickMap().put(ime.getMatchId(),ime);
				}
			}
			if (re.getTieBreakerMatchId() != null) {
				mgf.setId(re.getTieBreakerMatchId());
				re.setTieBreakerMatch(mgf.getGame());
			}
		}

		return re;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IRoundEntryFactory#put(net.rugby.foundation.game1.shared.IRoundEntry)
	 */
	@Override
	public IRoundEntry put(IRoundEntry re) {
		if (re != null) {

			// only try to swap out picks for matches that haven't been locked yet.		
			// now create a new list and save
			List<Long> newList = new ArrayList<Long>();

			// list of MatchEntry ids that have been replaced and need deleshunz.		
			List<Long> killList = new ArrayList<Long>();

			for (IMatchEntry me : re.getMatchPickMap().values()) {
				mgf.setId(me.getMatchId());
				IMatchGroup match = mgf.getGame();
				if (match.getLocked()) {
					if (me.getId() != null)  //@REX means that they are submitting a new pick for a newly locked match?
						newList.add(me.getId());
				} else {
					me = imf.put(me);
					newList.add(me.getId());
					// is this the same one? If not mark the old version for deleshunz
					if (re.getMatchPickMap().get(match.getId()) != null) {
						if (!re.getMatchPickMap().get(match.getId()).equals(me) )
							killList.add(re.getMatchPickMap().get(match.getId()).getId());
					}
				}

			}

			// if we make it this far, swap in the new
			re.setMatchPickIdList(newList);
			ofy.put(re);

			for (Long meid : killList) {
				imf.setId(meid);
				imf.delete();
			}	
		}

		return re;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IRoundEntryFactory#delete()
	 */
	@Override
	public Boolean delete() {
		Key<RoundEntry> key = new Key<RoundEntry>(RoundEntry.class,id);

		IRoundEntry e = ofy.find(key);  // find doesn't throw NotFoundException

		if (e != null) {
			for (Long meid : e.getMatchPickIdList()) {
				imf.setId(meid);
				imf.delete();
			}

			ofy.delete(key);
			return true;
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IRoundEntryFactory#getNew()
	 */
	@Override
	public IRoundEntry getNew() {
		return new RoundEntry();
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IRoundEntryFactory#getAll()
	 */
	@Override
	public Set<IRoundEntry> getAll() {
		Set<IRoundEntry> set = new HashSet<IRoundEntry>();

		List<RoundEntry> list = ofy.query(RoundEntry.class).list();

		// seems so stupid...
		for (RoundEntry re: list) {
			setId(re.getId());
			set.add(getRoundEntry());
		}

		return set;
	}
}
