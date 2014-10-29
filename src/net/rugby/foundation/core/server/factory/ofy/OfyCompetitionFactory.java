package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.IClubhouseFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.Competition;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICompetition.CompetitionType;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamGroup;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;





@Singleton
public class OfyCompetitionFactory extends BaseCachingFactory<ICompetition> implements ICompetitionFactory, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -645530830404364424L;
	private final IRoundFactory rf;
	private ITeamGroupFactory tf;
	private IClubhouseFactory chf;
	private IConfigurationFactory ccf;
	//	private boolean saving = false;
	private ICompetition globalComp;


	@Inject
	OfyCompetitionFactory(IRoundFactory rf, ITeamGroupFactory tf, IClubhouseFactory chf, IConfigurationFactory ccf) {
		this.rf = rf;
		this.tf = tf;
		this.chf = chf;
		this.ccf = ccf;
	}
	//
	//	@Override
	//	public ICompetition getCompetition() {
	//		try {
	//			byte[] value = null;
	//			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	//			ICompetition c = null;
	//
	//			value = (byte[])syncCache.get(id);
	//			if (value == null) {
	//				setId(id);
	//				c = getFromDB();
	//				if (c.getLastSaved() == null) {
	//					c.setLastSaved(new Date());
	//				}
	//				ByteArrayOutputStream bos = new ByteArrayOutputStream();
	//				ObjectOutput out = new ObjectOutputStream(bos);   
	//				out.writeObject(c);
	//				byte[] yourBytes = bos.toByteArray(); 
	//
	//				out.close();
	//				bos.close();
	//
	//				syncCache.put(id, yourBytes);
	//			} else {
	//
	//				// send back the cached version
	//				ByteArrayInputStream bis = new ByteArrayInputStream(value);
	//				ObjectInput in = new ObjectInputStream(bis);
	//				c = (ICompetition)in.readObject();
	//
	//				bis.close();
	//				in.close();
	//
	//			}
	//			return c;
	//
	//		} catch (Throwable ex) {
	//			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
	//			return null;
	//		}
	//
	//	}

	@Override
	public void invalidate(Long compId) {
		try {
			ICompetition c = get(compId);
			if (c != null) {
				deleteFromMemcache(c);
			}
			//			if (!saving) {
			//				// now update the memcache version
			//				MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			//
			//				setId(compId);
			//				ICompetition comp = getFromDB();
			//
			//				syncCache.delete(comp.getId());
			//				ByteArrayOutputStream bos = new ByteArrayOutputStream();
			//				ObjectOutput out = new ObjectOutputStream(bos);   
			//				out.writeObject(comp);
			//				byte[] yourBytes = bos.toByteArray(); 
			//
			//				out.close();
			//				bos.close();
			//
			//				syncCache.put(id, yourBytes);
			//			}

		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	@Override
	protected ICompetition getFromPersistentDatastore(Long id) {

		Objectify ofy = DataStoreFactory.getOfy();
		Competition c = ofy.get(new Key<Competition>(Competition.class,id));

		if (c != null) {
			c.setRounds(new ArrayList<IRound>());
			for (Long rid : c.getRoundIds()) {
				IRound r = rf.get(rid);
				c.getRounds().add(r);
			}

			c.setTeams(new ArrayList<ITeamGroup>());
			for (Long tid : c.getTeamIds()) {
				ITeamGroup t = tf.get(tid);
				c.getTeams().add(t);
			}
		} else {
			c = new Competition();
		}

		return c;
	}

	@Override
	public ICompetition putToPersistentDatastore(ICompetition c) {

		try {
			Objectify ofy = DataStoreFactory.getOfy();
			if (c != null) {

				c.setLastSaved(new Date());	
				if (c.getId() == null) {
					ofy.put(c); // get an id to pass down to the rounds
				}

				c.setRoundIds(new ArrayList<Long>());
				if (c.getRounds() != null) {
					for (IRound r : c.getRounds()) {
						//saving = true;
						r.setCompId(c.getId());
						r = rf.put(r);
						//saving = false;
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

			//			// now update the memcache version
			//			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			//			syncCache.delete(c.getId());
			//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			//			ObjectOutput out = new ObjectOutputStream(bos);   
			//			out.writeObject(c);
			//			byte[] yourBytes = bos.toByteArray(); 
			//
			//			out.close();
			//			bos.close();
			//
			//			syncCache.put(id, yourBytes);

			return c;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ICompetitionFactory#getUnderwayComps()
	 */
	@Override
	public List<ICompetition> getUnderwayComps() {
		List<ICompetition> list = new ArrayList<ICompetition>();
		Objectify ofy = DataStoreFactory.getOfy();
		Query<Competition> cq = ofy.query(Competition.class).filter("underway", true);
		for (Competition c : cq) {

			// never let a competition out the door that you get back from Objectify. Always call get() or 
			// Bad Things (tm) will happen.			
			list.add(get(c.getId()));
		}

		return list;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ICompetitionFactory#getUnderwayComps()
	 */
	@Override
	public List<ICompetition> getAllComps() {
		try {
			List<ICompetition> list = new ArrayList<ICompetition>();
			Objectify ofy = DataStoreFactory.getOfy();
			Query<Competition> cq = ofy.query(Competition.class);
			for (Competition c : cq) {

				// never let a competition out the door that you get back from Objectify. Always call get() or 
				// Bad Things (tm) will happen.
				list.add(get(c.getId()));
			}

			return list;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public ICompetition repair(ICompetition comp) {
		try {
			// make sure teams are populated
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Requested repair comp " + comp.getLongName());
			//if (comp.getTeamIds() == null || comp.getTeamIds().isEmpty()) {
			comp.setTeamIds(new ArrayList<Long>());
			for (IRound r: comp.getRounds()) {
				for (IMatchGroup m : r.getMatches()) {
					if (!comp.getTeamIds().contains(m.getHomeTeamId())) {
						comp.getTeamIds().add(m.getHomeTeamId());
					}
					if (!comp.getTeamIds().contains(m.getVisitingTeamId())) {
						comp.getTeamIds().add(m.getVisitingTeamId());
					}
				}
			}
			put(comp);
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Repaired comp by adding teamIds for comp " + comp.getLongName());

			//}

			return comp;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public boolean deleteFromPersistentDatastore(ICompetition c) {
		Objectify ofy = DataStoreFactory.startTransaction();

		try {
			boolean ok = true;

			if (c != null) {
				// save compClubhouse 
				// can't have more than 5 entity types in the transaction so we have to break it up.
				Long ccid = c.getCompClubhouseId();

				Iterator<IRound> it = c.getRounds().iterator();

				while (it.hasNext() && ok) {
					ok = rf.delete(it.next());
				}

				// the competition clubhouse
				if (ok) {
					ok = chf.delete(ccid);
				}

				// the core configuration entry
				if (ok) {
					ICoreConfiguration cc = ccf.get();
					ok = cc.deleteComp(c.getId());
					if (ok) {
						ccf.put(cc);
					}
				}

				//@REX workflow configuration

				//@REX game1 configuration

				if (ok) {
					ofy.delete(c);
				}

				if (ofy.getTxn().isActive()) {
					if (ok) {
						ofy.getTxn().commit();
						return true;
					} else {
						ofy.getTxn().rollback();
					}
				}
			}

			return false;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem in delete: " + ex.getLocalizedMessage());
			if (ofy.getTxn().isActive())
				ofy.getTxn().rollback();
			return false;
		}
	}

	@Override
	public ICompetition getGlobalComp() {
		try {
			if (globalComp == null) {
				Objectify ofy = DataStoreFactory.getOfy();
				Query<Competition> cq = ofy.query(Competition.class).filter("CompetitionType", CompetitionType.GLOBAL);
				if (cq.count() > 0) {
					globalComp = cq.get();
				} else {
					globalComp = new Competition();
					globalComp.setLongName("All Global Competitions");
					globalComp.setCompType(CompetitionType.GLOBAL);
					globalComp.setAbbr("Global");
					globalComp.setUnderway(true);
					ofy.put(globalComp);
				}
			}
			return globalComp;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public ICompetition create() {
		try {
			return new Competition();
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}

	}


}
