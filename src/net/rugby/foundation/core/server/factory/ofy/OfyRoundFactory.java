package net.rugby.foundation.core.server.factory.ofy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.Round;

public class OfyRoundFactory implements IRoundFactory, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2311118171036828094L;
	private Long id;
	private IMatchGroupFactory gf;
	private ICompetitionFactory cf;
	
	@Inject
	OfyRoundFactory(ICompetitionFactory cf, IMatchGroupFactory gf) {
		this.gf = gf;
		this.cf = cf;
	}
	
//	public void setFactories(ICompetitionFactory cf, IMatchGroupFactory gf) {
//		this.gf = gf;
//		this.cf = cf;
//	}
	
	@Override
	public void setId(Long id) {
		this.id = id;

	}

	
	@Override
	public IRound getRound() {
		try {
			
			if (id == null) {
				return new Round(); // put(null) actually saves it, which we don't want really?
			}
			
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			IRound r = null;
	
			value = (byte[])syncCache.get(id);
			if (value == null) {
				setId(id);
				r = getFromDB();

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos);   
				out.writeObject(r);
				byte[] yourBytes = bos.toByteArray(); 
	
				out.close();
				bos.close();
	
				syncCache.put(id, yourBytes);
			} else {
	
				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				r = (IRound)in.readObject();
	
				bis.close();
				in.close();
	
			}
			return r;
	
		} catch (Throwable ex) {
			Logger.getLogger("Core Service OfyRoundFactory.getRound").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}
	
	protected IRound getFromDB() {
		if (id == null) {
			return new Round();
		}
		Objectify ofy = DataStoreFactory.getOfy();

		Round r = ofy.get(new Key<Round>(Round.class,id));
		
		if (r != null) {
			r.setMatches(new ArrayList<IMatchGroup>());
			for (Long gid : r.getMatchIDs()) {
				gf.setId(gid);
				IMatchGroup g = gf.getGame();
				r.getMatches().add(g);
			}
		} else {
			r = new Round();
		}
		return r;
	}

	@Override
	public IRound put(IRound r) {

		try {
			Objectify ofy = DataStoreFactory.getOfy();

			if (r != null) {			
				((Round)r).setMatchIDs(new ArrayList<Long>());
				if (r.getId() == null)
					ofy.put(r); // get an id to pass down to the matches
				
				if (r.getMatches() != null) {
					for (IMatchGroup g : r.getMatches()) {
						g.setRoundId(r.getId());
						g = gf.put(g);
						((Round)r).getMatchIDs().add(g.getId());
					}
				} else {
					r.setMatches(new ArrayList<IMatchGroup>());
					((Round)r).setMatchIDs(new ArrayList<Long>());
				}
			} else {
				r = new Round();
				r.setName("--");
			}		
			
			ofy.put(r);
			
			// now update the memcache version
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			syncCache.delete(r.getId());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);   
			out.writeObject(r);
			byte[] yourBytes = bos.toByteArray(); 
	
			out.close();
			bos.close();
	
			syncCache.put(id, yourBytes);
	
			// force top-level reload
			cf.build(r.getCompId());
			
			return r;
		} catch (Throwable ex) {
			Logger.getLogger("Core Service OfyRoundFactory.put").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IRoundFactory#find(net.rugby.foundation.model.shared.IRound)
	 */
	@Override
	public IRound find(IRound round) {
		
		Objectify ofy = DataStoreFactory.getOfy();

		// Find the rounds with the same name
		Query<Round> qr = ofy.query(Round.class).filter("name", round.getName());
		
		// now see if any of these have the same matches
		for (Round r : qr) {			
			if (r.equals(round)) {
				setId(r.getId());
				return getRound();  // r has id set, parameter round may not
			}
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IRoundFactory#build(java.lang.Long)
	 */
	@Override
	public void build(Long roundId) {
		try {
			// now update the memcache version
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
				
			setId(roundId);
			IRound r = getFromDB();

			syncCache.delete(r.getId());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);   
			out.writeObject(r);
			byte[] yourBytes = bos.toByteArray(); 
	
			out.close();
			bos.close();
	
			syncCache.put(id, yourBytes);
			
			// and cascade up to comp
			cf.build(r.getCompId());
			
		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
		}	}

}
