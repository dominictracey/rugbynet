package net.rugby.foundation.game1.server.factory.ofy;

import java.util.ArrayList;
import java.util.HashMap;

import net.rugby.foundation.game1.server.factory.IEntryFactory;
import net.rugby.foundation.game1.server.factory.IRoundEntryFactory;
import net.rugby.foundation.game1.shared.Entry;
import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.game1.shared.IRoundEntry;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IRound;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;


public class OfyEntryFactory implements IEntryFactory {
	private Long id;
	private Long userId;
	private Long compId;
	private final Objectify ofy;
	private final IRoundEntryFactory ref;
	private IRound round;
	private ICompetition comp;
	private String name;
	@Inject
	OfyEntryFactory(IRoundEntryFactory ref) {
		this.ofy = DataStoreFactory.getOfy();

		this.ref = ref;
	}
	
	@Override
	public void setId(Long id) {
		this.id = id;
		this.name = null;
		this.comp = null;
		this.userId = null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IEntryFactory#getEntry()
	 */
	@Override
	public IEntry getEntry() {
		if (name != null && comp!= null && userId != null)
			return getEntryByName();

		Key<Entry> key = new Key<Entry>(Entry.class,id);

		IEntry e = ofy.find(key);  // find doesn't throw NotFoundException
		
		if (e != null) {
			buildRoundEntryList(e);
		} else {
			e = new Entry();
			e.setRoundEntries(new HashMap<Long, IRoundEntry>());
		}
		return e;
	}

	/**
	 * @return
	 */
	private IEntry getEntryByName() {
		Query<Entry> qe = ofy.query(Entry.class).filter("name", name).filter("compId", comp.getId());
		
		IEntry e = qe.get();
		
		if (e != null)
			buildRoundEntryList(e);
		
		return e;
	}

	/**
	 * @param e
	 */
	private void buildRoundEntryList(IEntry e) {
		e.setRoundEntries(new HashMap<Long, IRoundEntry>());
		for (Long reid : e.getRoundEntryIdList()) {
			ref.setId(reid);
			IRoundEntry g = ref.getRoundEntry();
			e.getRoundEntries().put(g.getRoundId(),g);
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IEntryFactory#put(net.rugby.foundation.game1.shared.IEntry)
	 */
	@Override
	public IEntry put(IEntry e) {
		if (e != null) {
			e.setRoundEntryIdList(new ArrayList<Long>());
			if (e.getRoundEntries() != null) {
				for (Long rid : e.getRoundEntries().keySet()) {
					IRoundEntry re = ref.put(e.getRoundEntries().get(rid));
					e.getRoundEntryIdList().add(re.getId());
				}
			}
		} else {
			e = new Entry();
			e.setName("--");
		}		
		
		ofy.put(e);
		
		return e;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IEntryFactory#setUserIdAndCompId(java.lang.Long, java.lang.Long)
	 */
	@Override
	public void setUserIdAndCompId(Long userId, Long compId) {
		this.userId = userId;
		this.compId = compId;
		this.name = null;
		this.id = null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IEntryFactory#getEntries()
	 */
	@Override
	public ArrayList<IEntry> getEntries() {
		
		ArrayList<IEntry> entries = new ArrayList<IEntry>();
		
		if (userId != null && compId != null) {
			Query<Entry> qe = ofy.query(Entry.class).filter("ownerId", userId).filter("compId", compId);
			
			for (Entry e : qe) {
				setId(e.getId());
				entries.add(getEntry());
			}
		} else if (round != null && comp != null) {
			Query<Entry> qe = ofy.query(Entry.class).filter("compId", comp.getId());
			
			for (Entry e : qe) {
				buildRoundEntryList(e);
				if (e.getRoundEntries().containsKey(round.getId()))
					entries.add(e);
			}			
		} else if (comp != null) {
			Query<Entry> qe = ofy.query(Entry.class).filter("compId", comp.getId());
			
			for (Entry e : qe) {
				buildRoundEntryList(e);
				entries.add(e);
			}
		}
		
		return entries;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.IEntryFactory#delete()
	 */
	@Override
	public Boolean delete() {
		Key<Entry> key = new Key<Entry>(Entry.class,id);

		IEntry e = ofy.find(key);  // find doesn't throw NotFoundException
		
		if (e != null) {
			for (Long reid : e.getRoundEntryIdList()) {
				ref.setId(reid);
				ref.delete();
			}
			
			ofy.delete(key);
			return true;
		}
		
		return false;
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IEntryFactory#setRoundAndComp(net.rugby.foundation.model.shared.IRound, net.rugby.foundation.model.shared.ICompetition)
	 */
	@Override
	public void setRoundAndComp(IRound round, ICompetition comp) {
		this.round = round;
		this.comp = comp;
		this.userId = null;
		this.name = null;

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IEntryFactory#setNameAndComp(java.lang.String, net.rugby.foundation.model.shared.ICompetition, java.lang.Long)
	 */
	@Override
	public void setNameAndComp(String name, ICompetition comp, Long userId) {
		this.name = name;
		this.comp = comp;
		this.userId = userId;
		this.round = null;
		this.id = null;
		
	}

}
