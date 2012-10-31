package net.rugby.foundation.core.server.factory.ofy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.server.factory.IClubhouseFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.Competition;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamGroup;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

@Singleton
public class OfyCompetitionFactory implements ICompetitionFactory {
	private Long id;
	private final Objectify ofy;
	private final IRoundFactory rf;
	private ITeamGroupFactory tf;
	private IClubhouseFactory chf;
	private boolean saving = false;
	private IMatchGroupFactory mf;
	private boolean loading = false;
	
	@Inject
	OfyCompetitionFactory(IRoundFactory rf, ITeamGroupFactory tf, IClubhouseFactory chf, IMatchGroupFactory mf) {
		this.ofy = DataStoreFactory.getOfy();

		this.rf = rf;
		this.mf = mf;
		//rf.setFactories(this, mf);
		//mf.setFactories(rf, tf);
		this.tf = tf;
		this.chf = chf;
		
		//Logger.getLogger("OfyCompFactory.Constructor").log(Level.WARNING, "Const " + this.toString() +  " MF " + mf.toString() + " loading " + loading + " saving " + saving);
	}
	
	@Override
	public void setId(Long id) {
		this.id = id;

	}

	@Override
	public ICompetition getCompetition() {
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			ICompetition c = null;
			//Logger.getLogger("OfyCompFactory.getCompetition").log(Level.WARNING, "Const " + this.toString() +  " MF " + mf.toString() + " loading " + loading + " saving " + saving);

			value = (byte[])syncCache.get(id);
			if (value == null) {
				setId(id);
				c = getFromDB();
				if (c.getLastSaved() == null) {
					c.setLastSaved(new Date());
				}
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos);   
				out.writeObject(c);
				byte[] yourBytes = bos.toByteArray(); 
	
				out.close();
				bos.close();
	
				syncCache.put(id, yourBytes);
			} else {
	
				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				c = (ICompetition)in.readObject();
	
				bis.close();
				in.close();

			}
			return c;

		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
		
	}
	
	@Override
	public void build(Long compId) {
		try {
			if (!saving) {
				// now update the memcache version
				MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
					
				setId(compId);
				ICompetition comp = getFromDB();
	
				syncCache.delete(comp.getId());
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos);   
				out.writeObject(comp);
				byte[] yourBytes = bos.toByteArray(); 
		
				out.close();
				bos.close();
		
				syncCache.put(id, yourBytes);
			}
			
		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	
	protected ICompetition getFromDB() {
		loading = true;
		Competition c = ofy.get(new Key<Competition>(Competition.class,id));
		
		if (c != null) {
			c.setRounds(new ArrayList<IRound>());
			for (Long rid : c.getRoundIds()) {
				rf.setId(rid);
				IRound r = rf.getRound();
				c.getRounds().add(r);
			}
			
			c.setTeams(new ArrayList<ITeamGroup>());
			for (Long tid : c.getTeamIds()) {
				tf.setId(tid);
				ITeamGroup t = tf.getTeam();
				c.getTeams().add(t);
			}
		} else {
			c = new Competition();
		}
		loading = false;
		return c;
	}

	@Override
	public ICompetition put(ICompetition c) {

		try {
			if (c != null) {
				c.setLastSaved(new Date());	
				if (c.getId() == null) {
					ofy.put(c); // get an id to pass down to the rounds
				}
				
				c.setRoundIds(new ArrayList<Long>());
				if (c.getRounds() != null) {
					for (IRound r : c.getRounds()) {
						saving = true;
						r.setCompId(c.getId());
						r = rf.put(r);
						saving = false;
						c.getRoundIds().add(r.getId());  //@REX doubles up on re-save?
					}
				} else {
					c.setRounds(new ArrayList<IRound>());
					c.setRoundIds(new ArrayList<Long>());
				}
				
				// re-populate teams out of the matches
				c.setTeamIds(new ArrayList<Long>());
				c.setTeams(new ArrayList<ITeamGroup>());
				for (IRound r : c.getRounds()) {
					for (IMatchGroup m: r.getMatches()) {
						if (!c.getTeamIds().contains(m.getHomeTeamId())) {
							c.getTeamIds().add(m.getHomeTeamId()); 
							c.getTeams().add(m.getHomeTeam()); 
						}
						if (!c.getTeamIds().contains(m.getVisitingTeamId())) {
							c.getTeamIds().add(m.getVisitingTeamId()); 
							c.getTeams().add(m.getVisitingTeam()); 
						}
	
					}
				}
	
				
				if (c.getCompClubhouseId() == null) {
					chf.setId(null);
					IClubhouse clubhouse = null;
					clubhouse = chf.get();
					clubhouse.setActive(true);
					clubhouse.setDescription("CompetitionClubhouse for " + c.getLongName());
					clubhouse.setName("CC" + c.getLongName());
					clubhouse.setOwnerID(-99L);  //-99L is system owned
					clubhouse.setPublicClubhouse(false);
					// no join link
					
					clubhouse = chf.put(clubhouse);
	
					c.setCompClubhouseId(clubhouse.getId());
				}
				
			} else {
				c = new Competition();
				c.setShortName("--");
			}		
			
			ofy.put(c);
			
			// now update the memcache version
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			syncCache.delete(c.getId());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);   
			out.writeObject(c);
			byte[] yourBytes = bos.toByteArray(); 
	
			out.close();
			bos.close();
	
			syncCache.put(id, yourBytes);
			
			return c;
		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ICompetitionFactory#getUnderwayComps()
	 */
	@Override
	public List<ICompetition> getUnderwayComps() {
		List<ICompetition> list = new ArrayList<ICompetition>();
		
		Query<Competition> cq = ofy.query(Competition.class).filter("underway", true);
		for (Competition c : cq) {
			
			// never let a competition out the door that you get back from Objectify. Always call getCompetition or 
			// Bad Things (tm) will happen.			
			setId(c.getId());
			list.add(getCompetition());
		}
		
		return list;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ICompetitionFactory#getUnderwayComps()
	 */
	@Override
	public List<ICompetition> getAllComps() {
		List<ICompetition> list = new ArrayList<ICompetition>();
		
		Query<Competition> cq = ofy.query(Competition.class);
		for (Competition c : cq) {
			
			// never let a competition out the door that you get back from Objectify. Always call getCompetition or 
			// Bad Things (tm) will happen.
			setId(c.getId());
			list.add(getCompetition());
		}
		
		return list;
	}

//	/* (non-Javadoc)
//	 * @see net.rugby.foundation.core.server.factory.ICompetitionFactory#getLastUpdate(java.lang.Long)
//	 */
//	@Override
//	public Date getLastUpdate(Long compId) {
//		setId(compId);
//		ICompetition c = getCompetition();
//		if (c != null) {
//			if (c.getLastSaved() == null) {
//				c.setLastSaved(new Date());
//				put(c);
//			}
//			return c.getLastSaved();
//		}
//		return null;
//	}

}
