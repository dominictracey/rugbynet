/**
 * 
 */
package net.rugby.foundation.game1.server.factory.ofy;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.game1.server.factory.IEntryFactory;
import net.rugby.foundation.game1.server.factory.ILeagueFactory;
import net.rugby.foundation.game1.shared.ClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.game1.shared.ILeague;
import net.rugby.foundation.game1.shared.League;
import net.rugby.foundation.model.shared.DataStoreFactory;

/**
 * @author home
 *
 */
public class OfyLeagueFactory implements ILeagueFactory {

	private Long id;
	private final Objectify ofy;
	private IEntryFactory ef;

	@Inject
	public OfyLeagueFactory(IEntryFactory ef) {
		this.ofy = DataStoreFactory.getOfy();
		this.ef = ef;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.ILeagueFactory#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.ILeagueFactory#get()
	 */
	@Override
	public ILeague get() {
		if (id == null || id == 0L) {
			return new League();
		}
		
		ILeague l = ofy.find(new Key<League>(League.class,id));
		
		//populate transient map
		for (Long eid : l.getEntryIds()) {
			//TODO can batch this.
			ef.setId(eid);
			IEntry entry = ef.getEntry();
			l.getEntryMap().put(eid, entry);
		}
		
		return l;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.ILeagueFactory#put(net.rugby.foundation.game1.shared.ILeague)
	 */
	@Override
	public ILeague put(ILeague l) {
		ofy.put(l);
		return l;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.ILeagueFactory#getAll()
	 */
	@Override
	public Set<ILeague> getAll() {
		Set<ILeague> set = new HashSet<ILeague>();
		
		Query<League> lq = ofy.query(League.class);
		
		for (ILeague l : lq) {
			setId(l.getId());  // populate the transients
			set.add(get());
		}
		
		return set;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.ILeagueFactory#delete()
	 */
	@Override
	public boolean delete() {
		try {
			ofy.delete(League.class, id);
		} catch (Throwable e) {
			Logger.getLogger(OfyLeagueFactory.class.getName()).log(Level.SEVERE,"Problems deleting league " + id.toString(),e);
			return false;
		}
		return true;
	}

}
