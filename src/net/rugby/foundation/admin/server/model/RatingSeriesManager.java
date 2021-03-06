package net.rugby.foundation.admin.server.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IRatingGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingMatrixFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingQuery.Status;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.Position;
import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.model.shared.UniversalRound;

import org.joda.time.DateTime;

import com.google.inject.Inject;

public class RatingSeriesManager implements IRatingSeriesManager {

	private IRatingSeriesFactory rsf;
	private IRatingMatrixFactory rmf;
	private IRatingQueryFactory rqf;
	private IRatingGroupFactory rgf;


	//protected IRatingSeries rs;
	protected ISeriesConfiguration sc;
	private IUniversalRoundFactory urf;
	private IRoundFactory rf;
	private ISeriesConfigurationFactory scf;
	private ICompetitionFactory cf;

	@Inject
	public RatingSeriesManager(ISeriesConfigurationFactory scf, IRatingSeriesFactory rsf, IRatingGroupFactory rgf, IRatingMatrixFactory rmf, IRatingQueryFactory rqf, 
			IUniversalRoundFactory urf, IRoundFactory rf, ICompetitionFactory cf) {
		this.scf = scf;
		this.rsf = rsf;
		this.rgf = rgf;
		this.rmf = rmf;
		this.rqf = rqf;
		this.urf = urf;
		this.rf = rf;
		this.cf = cf;
	}


	// This determines whether we are ready to create a new RatingGroup
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IRatingSeriesManager#initialize(net.rugby.foundation.model.shared.IRatingSeries)
	 */
	@Override
	public IRatingSeries initialize(ISeriesConfiguration sc) {

		assert sc.getPipelineId() != null;
		this.sc = sc;

		if (sc == null)
			return null;

		if (sc.getSeriesId() == null) {
			IRatingSeries series = rsf.create();
			series.getActiveCriteria().addAll(sc.getActiveCriteria());
			series.getCompIds().addAll(sc.getCompIds());
			series.getCountryIds().addAll(sc.getCountryIds());
			series.setMode(sc.getMode());
			series.setCreated(DateTime.now().toDate());
			series.setLive(true);
			series.setStart(DateTime.now().toDate());
			series.setHostCompId(sc.getHostCompId());
			rsf.build(series);
			rsf.put(series);
//			sc.setSeries(series);
			sc.setSeriesId(series.getId());
			if (sc.getTargetRound() == null) {
				UniversalRound now = urf.getCurrent();
				sc.setTargetRound(now);
				sc.setTargetRoundOrdinal(now.ordinal);
			}
			scf.put(sc);

			return series;
		} else {
			return rsf.get(sc.getSeriesId());
		}

	}

//	@Override
//	public Boolean readyForGroupUpdateOrCreation(ISeriesConfiguration config) {	
//
//		this.sc = config;
//
//		// return true if
//		//	1) Series has been created
//		//	2) Series has a target UniversalRound specified
//		//	3) target UR is not in future
//		//	4) The comps configured for this series have all of the stats all fetched for the target UR
//		//  5) The group exists for the target round, but the matrices aren't right
//		assert config.getPipelineId() != null;
//		IRatingSeries series = config.getSeries();
//		if (series == null) {
//			series = initialize(config);
//		}
//
//		int targetURordinal = config.getTargetRound().ordinal;
//
//		IRatingGroup g = rgf.getForUR(series.getId(), targetURordinal);
//		
//		// does the group already exist?
//		if (g != null) {
//			// make sure we aren't adding a new matrix to the target group
//			if (g.getRatingMatrixIds().size() != series.getActiveCriteria().size())
//				return true;
//			else {
//				
//				// need to create any matrices?
//				List<Criteria> missing = new ArrayList<Criteria>();
//				missing.addAll(series.getActiveCriteria());
//				for (IRatingMatrix rm : g.getRatingMatrices()) {
//					missing.remove(rm.getCriteria());
//				}
//				if (missing.size() > 0)
//					return true;
//				
//				// need to remove any matrices?
//				List<Criteria> extra = new ArrayList<Criteria>();
//				for (IRatingMatrix rm : g.getRatingMatrices()) {
//					extra.add(rm.getCriteria());
//				}
//				
//				extra.removeAll(series.getActiveCriteria());
//				if (extra.size() > 0)
//					return true;
//
//				
//			}
//		} else {
//			
//		}
//		
//
//		int currentURordinal = urf.getCurrent().ordinal;
//
//		// can't do a future round
//		if (targetURordinal > currentURordinal) {
//			return false;
//		}
//
//		// can't do it if the necessary rounds are not in the FETCHED state
//		// it is ok if the comp doesn't have a round for the target UR.
//		// note this isn't true for when we are doing match review
//		// THIS IS DEAD CODE AS OF 3/24/2016, ISN'T IT?
//		if (!config.getMode().equals(RatingMode.BY_MATCH) && !config.getMode().equals(RatingMode.BY_POSITION) && !config.getMode().equals(RatingMode.BY_COMP) && !config.getMode().equals(RatingMode.BY_TEAM)) {
//			for (Long cid : config.getCompIds()) {
//				ICompetition c = cf.get(cid);
//				// get the round for the target UniversalRound
//				for (IRound r : c.getRounds()) {
//					if (urf.get(r).ordinal == targetURordinal) {
//						if (r.getWorkflowStatus() == null) {
//							throw new RuntimeException(r.getName() + " in comp " + c.getShortName() + " does not have a valid workflow status.");
//						}
//						if (!r.getWorkflowStatus().equals(IRound.WorkflowStatus.FETCHED)) {
//							return false;
//						}
//					}
//				}
//			}
//		}
//		
//
//
//		return true;
//	}


	/*
	 * Important note: when the RatingSeries graph is changed, we should drop the entire graph from memcache to keep it synched with what we are saving to the persistent datastore.
	 * Unfortunately this might hose up the Test versions of the factories?
	 * *
	 * * NB: Sometimes we already have the group and just need to graft a new RatingMatrix into it
	 * (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IRatingSeriesManager#doRatingGroup(net.rugby.foundation.model.shared.IRatingSeries, net.rugby.foundation.model.shared.UniversalRound)
	 */
	@Override
	public IRatingGroup getRatingGroup(ISeriesConfiguration sc, UniversalRound time ) {

		this.sc = sc;
		
		if (sc.getSeriesId() == null) {
			IRatingSeries rs = initialize(sc);
			sc.setSeriesId(rs.getId());
			scf.put(sc);
		}
		
		IRatingSeries rs = rsf.get(sc.getSeriesId());
		
		// graft new Matrix or create new Group?
		IRatingGroup rg = rgf.getForUR(rs.getId(), time.ordinal);
		
		if (rg == null) {
			
			// first, cull out older groups so we don't go past the memcache limit of 1Mb
			//cullOldRatingGroups(rs);
			
			rg = rgf.create();
			rg.setRatingSeries(rs);
			assert(rs.getId() != null);
			rg.setRatingSeriesId(rs.getId());
			rg.setUniversalRound(time);
			rg.setUniversalRoundOrdinal(time.ordinal);
			rg.setRatingSeries(rs);
			rgf.put(rg); // get id for ratingGroup

			// now create the Rating Matrices for the group
			// the criteria are the drop down box ("in form", "best over last year", "average impact", "best all time")
			for (Criteria criteria : rs.getActiveCriteria()) {
				IRatingMatrix rm = rmf.create();
				rm.setCriteria(criteria);
				rm.setRatingGroup(rg);
				rm.setRatingGroupId(rg.getId());
				rm.setGenerated(DateTime.now().toDate());
				rmf.put(rm);
				rg.getRatingMatrices().add(rm);
				rg.getRatingMatrixIds().add(rm.getId());

				generateRatingQueries(rm);
			}


			rgf.put(rg);
			// put it at the right place (sort by UR ordinal)
			int index = 0;
			for (Long cursorId : rs.getRatingGroupIds()) {
				IRatingGroup cursor = rgf.get(cursorId);
				if (cursor.getUniversalRoundOrdinal() < rg.getUniversalRoundOrdinal()) {
					break;
				}
				index++;
			}

			rs.getRatingGroupIds().add(index, rg.getId());
			//rs.getRatingGroups().add(index, rg);
			rsf.put(rs);

		} else { // graft new matrix(ices) and prune dead ones
			
			List<Criteria> missing = new ArrayList<Criteria>();
			missing.addAll(rs.getActiveCriteria());
			for (IRatingMatrix rm : rg.getRatingMatrices()) {
				missing.remove(rm.getCriteria());
			}
			
			for (Criteria criteria : missing) {
				IRatingMatrix rm = rmf.create();
				rm.setCriteria(criteria);
				rm.setRatingGroup(rg);
				rm.setRatingGroupId(rg.getId());
				rm.setGenerated(DateTime.now().toDate());
				rmf.put(rm);
				rg.getRatingMatrices().add(rm);
				rg.getRatingMatrixIds().add(rm.getId());
				rgf.put(rg);
				generateRatingQueries(rm);
			}
			
			List<Criteria> extra = new ArrayList<Criteria>();
			for (IRatingMatrix rm : rg.getRatingMatrices()) {
				extra.add(rm.getCriteria());
			}
			
			extra.removeAll(rs.getActiveCriteria());
			
			// can't do concurrent mods in for loop
			List<IRatingMatrix> shadow = new ArrayList<IRatingMatrix>();
			shadow.addAll(rg.getRatingMatrices());
			
			for (IRatingMatrix rm : shadow) {
				if (extra.contains(rm.getCriteria())) {
					rg.getRatingMatrices().remove(rm);
					rg.getRatingMatrixIds().remove(rm.getId());
					rgf.put(rg);
					rmf.delete(rm);
				}
			}
		}
		return rg;
	}

	/**
	 * So we have run into problems when we get up around 50 or so RatingGroups. Cull them so we have no more that 25 RGs:
	 * 		- Every week for the last 16 weeks
	 * 		- Every other week for the prior 12 weeks
	 * 		- Every fourth week for the prior 24 weeks
	 * 		- Remove everything older
	 * 
	 * Algorithm: =if($G$42-A56<$G$43,1,if($G$42-A56<$G$44,MOD(A56,2),if($G$42-A56<$G$45,MOD(A56,4),0)))
	 * 		https://docs.google.com/spreadsheets/d/1TgEUXWRLXHf225OexK4hMsauayCIasZ-l5jzZ1IiaSs/edit#gid=0
	 * 
	 * @param rs
	 */
//	private final int EVERY_WEEK_CUTOFF = 16;
//	private final int EVERY_OTHER_WEEK_CUTOFF = 12;
//	private final int EVERY_FOURTH_WEEK_CUTOFF = 24;
//	private void cullOldRatingGroups(IRatingSeries rs) {
//		
//		if (rs.getMode() == RatingMode.BY_MATCH) {
//			// don't flush the match series
//			return;
//		}
//		
//		if (rs.getRatingGroups().size() < 28) {
//			return;
//		}
//		
//		UniversalRound nowUR = urf.getCurrent();
//		
//		int everyWeekCutoff = nowUR.ordinal - EVERY_WEEK_CUTOFF;
//		int everyOtherWeekCutoff = nowUR.ordinal - EVERY_WEEK_CUTOFF - EVERY_OTHER_WEEK_CUTOFF;
//		int everyFourthWeekCutoff = nowUR.ordinal - EVERY_WEEK_CUTOFF - EVERY_OTHER_WEEK_CUTOFF - EVERY_FOURTH_WEEK_CUTOFF;
//		
//		List<IRatingGroup> toDelete = new ArrayList<IRatingGroup>();
//		for (IRatingGroup rg : rs.getRatingGroups()) {
//			if (rg.getUniversalRoundOrdinal() < everyFourthWeekCutoff) {
//				// delete very old
//				toDelete.add(rg);
//			} else if (rg.getUniversalRoundOrdinal() < everyOtherWeekCutoff) {
//				// delete every fourth one
//				if ((rg.getUniversalRoundOrdinal() % 4) != 1) {
//					toDelete.add(rg);
//				}
//			} else if (rg.getUniversalRoundOrdinal() < everyWeekCutoff) {
//				// delete every fourth one
//				if ((rg.getUniversalRoundOrdinal() % 2) != 1) {
//					toDelete.add(rg);
//				}
//			}
//		}
//		
//		for (IRatingGroup rg : toDelete) {
//			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Removing rating group from RatingSeries " + rs.getDisplayName() + " for " + rg.getUniversalRound().longDesc);
//			rs.getRatingGroups().remove(rg);
//			rs.getRatingGroupIds().remove(rg);
//		}
//		
//		assert(rs.getRatingGroupIds().size() == rs.getRatingGroups().size());
//		assert(rs.getRatingGroupIds().size() <= 28);
//		
//		
//		rsf.put(rs);
//	}


	private List<Long> findRoundsForRatingMatrices(IRatingMatrix rm) {
		List<Long> rids = new ArrayList<Long>();
		assert(rm != null && rm.getRatingGroupId() != null);
		try {
			IRatingGroup rg = rgf.get(rm.getRatingGroupId());
			assert rg != null && rg.getRatingSeriesId() != null;
			IRatingSeries rs = rsf.get(rg.getRatingSeriesId());

			for (Long compId : rs.getCompIds()) {
				ICompetition comp = cf.get(compId);
				UniversalRound ur = sc.getTargetRound();
				if (rm.getCriteria() == Criteria.ROUND) {
					// we only want the target round
					IRound targetRound = null;
					boolean found = false;
					for (IRound r : comp.getRounds()) {
						targetRound = r;
						if (urf.get(r).ordinal == ur.ordinal) {
							found = true;
							break;
						}
					}
					if (found) {
						rids.add(targetRound.getId());
					}
				} else if (rm.getCriteria() == Criteria.BEST_YEAR || rm.getCriteria() == Criteria.AVERAGE_IMPACT) {
					// get the rounds for the comps that happened in the last year.
					List<IRound> rounds = getRoundsFromLastXMonths(comp, 12);
					for (IRound r : rounds) {
						if (urf.get(r).ordinal <= ur.ordinal) {
							rids.add(r.getId());
						}
					}
				} else if (rm.getCriteria() == Criteria.IN_FORM) {
					// get the rounds for the comps that happened in the last twelve months if its a virtual comp
					List<IRound> rounds = null;
					if (rs.getCompIds().size() > 1) {
						rounds = getRoundsFromLastXMonths(comp, 12);  ///?
					} else {
						rounds = getRoundsFromLastXMonths(comp, 12); // for a single comp, just get all the data
					}

					for (IRound r : rounds) {
						if (urf.get(r).ordinal <= ur.ordinal) {
							rids.add(r.getId());
						}
					}
				}
			}
		}  catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getMessage(), e);
			return null;
		}

		return rids;
	}

	private List<IRound> getRoundsFromLastXMonths(ICompetition comp, int xMonths) {


		DateTime yearAgo = DateTime.now().minusMonths(xMonths);
		List<IRound> yearRounds = new ArrayList<IRound>();
		for (IRound rd: comp.getRounds()) {
			DateTime dtb = new DateTime(rd.getBegin());
			DateTime dte = new DateTime(rd.getEnd());
			if (dtb.isAfter(yearAgo) && dte.isBefore(DateTime.now())) {
				yearRounds.add(rd);
			}
		}

		return yearRounds;
	}



	private void generateRatingQueries(IRatingMatrix rm) {
		boolean scaleTime = rm.getCriteria() == Criteria.IN_FORM;  // can also be BEST_YEAR
		boolean scaleMinutesPlayed = rm.getCriteria() == Criteria.AVERAGE_IMPACT;
		List<Long> rids = findRoundsForRatingMatrices(rm);
		IRatingGroup rg = rgf.get(rm.getRatingGroupId());
		assert rg != null && rg.getRatingSeriesId() != null;
		IRatingSeries rs = rsf.get(rg.getRatingSeriesId());
		if (rs.getMode() == RatingMode.BY_POSITION) {

			for (position pos : Position.position.values()) {
				if (pos != position.RESERVE && pos != position.NONE) {
					IRatingQuery rq = rqf.create();
					rq.setRatingMatrix(rm);
					rq.getPositions().add(pos);
					rq.setLabel(pos.getName());
					rq.setCompIds(rs.getCompIds());
					rq.setCountryIds(rs.getCountryIds());
					rq.setScaleStanding(true);
					rq.setScaleComp(rs.getCompIds().size() > 1);
					rq.setScaleTime(scaleTime);
					rq.setScaleMinutesPlayed(scaleMinutesPlayed);
					if (scaleMinutesPlayed) {
						rq.setMinMinutes(sc.getMinMinutes());
						rq.setMinMinutesType(sc.getMinMinuteType());
					}
					rq.setStatus(Status.NEW);
					rq.setRoundIds(rids);
					rq.setRatingMatrixId(rm.getId());
					rqf.put(rq);
					rm.getRatingQueries().add(rq);
					rm.getRatingQueryIds().add(rq.getId());
					//rmf.put(rm);
				}
			}
			
			// have to save them again outside the above loop so they get memcached right :-/
			for (IRatingQuery q : rm.getRatingQueries()) {
				q.setRatingMatrix(rm);
				rqf.put(q);
			}
			
			// finally, save the matrix
			rmf.put(rm);
			
		} else if (rs.getMode() == RatingMode.BY_TEAM) {
			
			// first get a list of teams
			List<ITeamGroup> teams = new ArrayList<ITeamGroup>();
			
			ICompetition hostComp = rs.getHostComp();
			if (hostComp == null && rs.getHostCompId() != null) {
				hostComp = cf.get(rs.getHostCompId());
			}
			
			if (hostComp != null) {
				for (ITeamGroup team : hostComp.getTeams()) {
					// remove TBC and TBD
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Team series, adding team " + team.getDisplayName());
					if (!team.getDisplayName().contains("TBC") && !team.getDisplayName().contains("TBD")) {
						teams.add(team);
					}
				}
			}
			
			// sort alphabetically by displayName
			Collections.sort(teams, new Comparator<ITeamGroup>() {
				@Override
				public int compare(ITeamGroup o1, ITeamGroup o2) {
					return o1.getDisplayName().compareTo(o2.getDisplayName());
				}
			});
			
			for (ITeamGroup team : teams) {
			
				IRatingQuery rq = rqf.create();
				rq.setRatingMatrix(rm);
				rq.getTeamIds().add(team.getId());
				rq.setLabel(team.getDisplayName());
				rq.setCompIds(rs.getCompIds());
				rq.setCountryIds(rs.getCountryIds());
				rq.setScaleStanding(true);
				rq.setScaleComp(rs.getCompIds().size() > 1);
				rq.setScaleTime(scaleTime);
				rq.setScaleMinutesPlayed(scaleMinutesPlayed);
				if (scaleMinutesPlayed) {
					rq.setMinMinutes(sc.getMinMinutes());
					rq.setMinMinutesType(sc.getMinMinuteType());
				}
				rq.setStatus(Status.NEW);
				rq.setRoundIds(rids);
				rq.setRatingMatrixId(rm.getId());
				rqf.put(rq);
				rm.getRatingQueries().add(rq);
				rm.getRatingQueryIds().add(rq.getId());
				rmf.put(rm);
				
			}		
		} else if (rs.getMode() == RatingMode.BY_MATCH) {

			for (Long rid : rids) {
				IRound targetRound = rf.get(rid);
				for (IMatchGroup m: targetRound.getMatches()) {

					IRatingQuery rq = createRatingQuery(rm);

					rq.setScaleStanding(true);
					rq.setScaleComp(false);
					rq.setScaleTime(scaleTime);

					rq.setRoundIds(rids);
					rq.getTeamIds().add(m.getHomeTeamId());
					rq.getTeamIds().add(m.getVisitingTeamId());
					rq.setRatingMatrixId(rm.getId());
					rq.setLabel(m.getDisplayName());
					rqf.put(rq);
					rm.getRatingQueries().add(rq);
					rm.getRatingQueryIds().add(rq.getId());
					rmf.put(rm);
				}
			}
		}  else if (rs.getMode() == RatingMode.BY_COMP) {

			IRatingQuery rq = createRatingQuery(rm);

			rq.setScaleStanding(true);
			rq.setScaleComp(true);
			rq.setScaleTime(scaleTime);

			rq.setCompIds(rs.getCompIds());
			rq.setCountryIds(rs.getCountryIds());
			rq.setRoundIds(rids);
			rq.setRatingMatrixId(rm.getId());
			rq.setLabel(rg.getUniversalRound().longDesc);
			rqf.put(rq);
			rm.getRatingQueries().add(rq);
			rm.getRatingQueryIds().add(rq.getId());
			rmf.put(rm);
		}
	}

	private IRatingQuery createRatingQuery(IRatingMatrix rm) {
		IRatingQuery rq = rqf.create();
		rq.setStatus(Status.NEW);
		rq.setRatingMatrix(rm);
		rq.setRatingMatrixId(rm.getId());
		IRatingGroup rg = rgf.get(rm.getRatingGroupId());
		assert rg != null && rg.getRatingSeriesId() != null;
		IRatingSeries rs = rsf.get(rg.getRatingSeriesId());
		rq.setCompIds(rs.getCompIds());
		return rq;
	}
}
