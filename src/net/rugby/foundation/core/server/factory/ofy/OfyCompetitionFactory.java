package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.IClubhouseFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.Competition;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICompetition.CompetitionType;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IRound.WorkflowStatus;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.Round;
import net.rugby.foundation.model.shared.UniversalRound;

import org.joda.time.DateTime;

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
	private IRatingSeriesFactory rsf;
	private IUniversalRoundFactory urf;


	@Inject
	OfyCompetitionFactory(IRoundFactory rf, ITeamGroupFactory tf, IClubhouseFactory chf, IConfigurationFactory ccf, IRatingSeriesFactory rsf,
			IUniversalRoundFactory urf) {
		this.rf = rf;
		this.tf = tf;
		this.chf = chf;
		this.ccf = ccf;
		this.rsf = rsf;
		this.urf = urf;
	}

	@Override
	public void invalidate(Long compId) {
		try {
			ICompetition c = get(compId);
			if (c != null) {
				deleteFromMemcache(c);
			}

		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	@Override
	protected ICompetition getFromPersistentDatastore(Long id) {


		try {
			Objectify ofy = DataStoreFactory.getOfy();
			Competition c = ofy.get(new Key<Competition>(Competition.class,id));
			UniversalRound now = urf.get(new DateTime());

			if (c != null) {
				c.setRounds(new ArrayList<IRound>());
				int count = 0;
				c.setPrevRoundIndex(-1);
				c.setNextRoundIndex(-1);
				for (Long rid : c.getRoundIds()) {
					IRound r = rf.get(rid);
					if (r != null) {
						c.getRounds().add(r);
						if (r.getUrOrdinal() > now.ordinal && c.getPrevRoundIndex() == -1) {
							c.setPrevRoundIndex(count-1);
						} else if (r.getUrOrdinal() > now.ordinal + 1 && c.getNextRoundIndex() == -1) {
							c.setNextRoundIndex(count);
						}			
						count++;
					}
				}


				c.setTeams(new ArrayList<ITeamGroup>());
				for (Long tid : c.getTeamIds()) {
					ITeamGroup t = tf.get(tid);
					c.getTeams().add(t);
				}
			} else {
				c = new Competition();
			}

			// populate the seriesMap
			c.setSeriesMap(rsf.getModesForComp(id));

			// and the component competitions for virtual comps
			for (Long sid : c.getSeriesMap().values()) {
				IRatingSeries rs = rsf.get(sid);
				for (Long cid : rs.getCompIds())	
					if (!c.getComponentCompIds().contains(cid)) {
						c.getComponentCompIds().add(cid);
					}
			}
			
			// must have a weighting value
			if (c.getWeightingFactor() == null) {
				c.setWeightingFactor(1.0F);
			}

			return c;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
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

			//update core configuration
//			ICoreConfiguration conf = ccf.get();
//			conf.deleteComp(c.getId());
//			if (c.getUnderway()) {
//				conf.addCompUnderway(c.getId());
//			} else if (c.getShowToClient()) {
//				conf.addCompForClient(c.getId());
//			} else {
//				conf.getAllComps().add(c.getId());
//			}
//			ccf.put(conf);
			

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
		Query<Competition> cq = ofy.query(Competition.class).filter("underway", true).order("-weightingFactor");
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
			Query<Competition> cq = ofy.query(Competition.class).order("-weightingFactor");
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
			
			// the top 14 had some ghost rounds created (rounds in the DB with compId set, but they weren't in the comp.getRounds())
			// the matchIds in these rounds seemed to not exist so just delete them.
			Objectify ofy = DataStoreFactory.getOfy();
			Query<Round> rq = ofy.query(Round.class).filter("compId", comp.getId());
			for (Round r: rq.list()) {
				if (!comp.getRoundIds().contains(r.getId())) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Deleting bad round " + r.getName() + " with UROrd " + r.getUrOrdinal());
					rf.delete(r);
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
				if (ok && ccid != null) {
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
					globalComp.setWeightingFactor(4F);
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

	@Override
	public List<Long> getClientComps() {
		try {
			List<Long> list = new ArrayList<Long>();
			Objectify ofy = DataStoreFactory.getOfy();
			Query<Competition> cq = ofy.query(Competition.class).filter("underway", true).filter("showToClient", true).order("-weightingFactor");
			for (Competition c : cq) {

				// never let a competition out the door that you get back from Objectify. Always call get() or 
				// Bad Things (tm) will happen.
				list.add(c.getId());
			}

			return list;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public Boolean addRound(Long compId, int uri, String name) {
		try {
			ICompetition c = get(compId);
			UniversalRound ur = urf.get(uri);

			if (c != null) {
				// do we already have this round? if not, find the insertion point
				int insert = 0;
				for (IRound r : c.getRounds()) {
					if (r.getUrOrdinal() == uri) {
						return false;
					} else if (r.getUrOrdinal() > uri) {
						break;
					} else {
						insert++;
					}
				}

				IRound r = rf.create();
				r.setAbbr(ur.shortDesc);
				if (!name.isEmpty()) {
					r.setName(name);
				} else {
					r.setName(ur.longDesc);
				}
				r.setBegin(ur.start);
				DateTime end = new DateTime(ur.start);
				end.plusDays(2);
				end.plusHours(23);
				end.plusMinutes(59);
				end.plusSeconds(59);
				r.setEnd(end.toDate());
				r.setCompId(compId);
				r.setOrdinal(insert);
				r.setUrOrdinal(uri);
				r.setWorkflowStatus(WorkflowStatus.PENDING);
				rf.put(r);

				c.getRounds().add(insert, r);
				c.getRoundIds().add(insert,r.getId());

				put(c);

				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Adding round " + name + " to comp " + c.getLongName());

				return true;
			} 

			return false;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return false;
		}
	}

	@Override
	public List<ICompetition> getVirtualComps() {

		List<ICompetition> list = new ArrayList<ICompetition>();
		Objectify ofy = DataStoreFactory.getOfy();
		Query<Competition> cq = ofy.query(Competition.class).filter("compType", "GLOBAL").filter("foreignID", null);
		for (Competition c : cq) {	
			list.add(get(c.getId()));
		}

		cq = ofy.query(Competition.class).filter("compType", "EUROPE").filter("foreignID", null);
		for (Competition c : cq) {	
			list.add(get(c.getId()));
		}
		
		cq = ofy.query(Competition.class).filter("compType", "SOUTHERN_HEMISPHERE").filter("foreignID", null);
		for (Competition c : cq) {	
			list.add(get(c.getId()));
		}
		
		return list;
	}

	@Override
	public Map<Long, String> getAllCompIds() {
		try {
			Map<Long, String> map = new HashMap<Long, String>();
			Objectify ofy = DataStoreFactory.getOfy();
			Query<Competition> cq = ofy.query(Competition.class).order("-weightingFactor");
			for (Competition c : cq) {
				map.put(c.getId(), c.getLongName());
			}
			return map;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public List<Long> getUnderwayCompIds() {
		try {
			List<Long> list = new ArrayList<Long>();
			Objectify ofy = DataStoreFactory.getOfy();
			Query<Competition> cq = ofy.query(Competition.class).filter("underway",true).order("-weightingFactor");
			for (Competition c : cq) {
				list.add(0,c.getId());
			}
			return list;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public Map<Long, Float> getAllCompWeights() {
		try {
			Map<Long, Float> map = new HashMap<Long, Float>();
			Objectify ofy = DataStoreFactory.getOfy();
			Query<Competition> cq = ofy.query(Competition.class).order("-weightingFactor");
			for (Competition c : cq) {
				map.put(c.getId(), c.getWeightingFactor());
			}
			return map;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}



}
