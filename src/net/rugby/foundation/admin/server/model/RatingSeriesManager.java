package net.rugby.foundation.admin.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.server.factory.IQueryRatingEngineFactory;
import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IRatingGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingMatrixFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingQuery.Status;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.Position;
import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.model.shared.UniversalRound;

import com.google.inject.Inject;

public class RatingSeriesManager implements IRatingSeriesManager {

	private IRatingSeriesFactory rsf;
	private IRatingMatrixFactory rmf;
	private IRatingQueryFactory rqf;
	private IRatingGroupFactory rgf;
	private IQueryRatingEngineFactory qref;
	private IMatchRatingEngineSchemaFactory mresf;

	protected IRatingSeries rs;
	protected ISeriesConfiguration sc;
	private IUniversalRoundFactory urf;
	private IRoundFactory rf;
	private ISeriesConfigurationFactory scf;
	private ICompetitionFactory cf;

	@Inject
	public RatingSeriesManager(ISeriesConfigurationFactory scf, IRatingSeriesFactory rsf, IRatingGroupFactory rgf, IRatingMatrixFactory rmf, IRatingQueryFactory rqf, 
			IQueryRatingEngineFactory qref, IMatchRatingEngineSchemaFactory rsef, IUniversalRoundFactory urf, IRoundFactory rf, ICompetitionFactory cf) {
		this.scf = scf;
		this.rsf = rsf;
		this.rgf = rgf;
		this.rmf = rmf;
		this.rqf = rqf;
		this.qref = qref;
		this.mresf = rsef;
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
			sc.setSeries(series);
			sc.setSeriesId(series.getId());
			scf.put(sc);

			return series;
		} else {
			return sc.getSeries();
		}

	}

	@Override
	public Boolean readyForNewGroup(ISeriesConfiguration config) {	

		this.sc = config;

		// return true if
		//	1) Series has been created
		//	2) Series has a target UniversalRound specified
		//	3) target UR is not in future
		//	4) The comps configured for this series have all of the stats all fetched for the target UR
		assert config.getPipelineId() != null;
		IRatingSeries series = config.getSeries();
		if (series == null) {
			return false;
		}

		int targetURordinal = config.getTargetRound().ordinal;

		// does the group already exist?
		for (IRatingGroup g : series.getRatingGroups()) {
			if (g.getUniversalRoundOrdinal() == targetURordinal) {
				return false;
			}
		}

		int currentURordinal = urf.getCurrent().ordinal;

		// can't do a future round
		if (targetURordinal > currentURordinal) {
			return false;
		}

		// can't do it if the necessary rounds are not in the FETCHED state
		// it is ok if the comp doesn't have a round for the target UR.
		// note this isn't true for when we are doing match review
		if (!config.getMode().equals(RatingMode.BY_MATCH) && !config.getMode().equals(RatingMode.BY_POSITION) && !config.getMode().equals(RatingMode.BY_COMP)) {
			for (ICompetition c : config.getComps()) {
				// get the round for the target UniversalRound
				for (IRound r : c.getRounds()) {
					if (urf.get(r).ordinal == targetURordinal) {
						if (r.getWorkflowStatus() == null) {
							throw new RuntimeException(r.getName() + " in comp " + c.getShortName() + " does not have a valid workflow status.");
						}
						if (!r.getWorkflowStatus().equals(IRound.WorkflowStatus.FETCHED)) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}


//
//	@Override
//	public String process(IRatingSeries rs) {
//		//	
//		//		// start a workflow
//		//		IRatingGroup rg = addRatingGroup(rs, DateTime.now());		
//		//		rs.getRatingGroups().add(rg);
//		//		rs.getRatingGroupIds().add(rg.getId());
//		//
//		//		rsf.put(rs);
//
//		generateRatings(rs);
//
//		return null;
//	}



	/*
	 * Important note: when the RatingSeries graph is changed, we should drop the entire graph from memcache to keep it synched with what we are saving to the persistent datastore.
	 * Unfortunately this might hose up the Test versions of the factories?
	 * (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IRatingSeriesManager#addRatingGroup(net.rugby.foundation.model.shared.IRatingSeries, net.rugby.foundation.model.shared.UniversalRound)
	 */
	public IRatingGroup addRatingGroup(IRatingSeries rs, UniversalRound time ) {
		IRatingGroup rg = rgf.create();
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
		rs.getRatingGroupIds().add(rg.getId());
		rs.getRatingGroups().add(rg);
		rsf.put(rs);

		return rg;
	}

	private List<Long> findRoundsForRatingMatrices(IRatingMatrix rm) {
		List<Long> rids = new ArrayList<Long>();
		assert(rm != null);
		try {
			IRatingSeries rs = rm.getRatingGroup().getRatingSeries();

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
				} else if (rm.getCriteria() == Criteria.BEST_YEAR || rm.getCriteria() == Criteria.IN_FORM) {
					// get the rounds for the comps that happened in the last year.
					List<IRound> rounds = getRoundsFromLastYear(comp);
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

	private List<IRound> getRoundsFromLastYear(ICompetition comp) {


		DateTime yearAgo = DateTime.now().minusYears(1);
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
		boolean scaleTime = rm.getCriteria() == Criteria.IN_FORM;
		List<Long> rids = findRoundsForRatingMatrices(rm);
		if (rm.getRatingGroup().getRatingSeries().getMode() == RatingMode.BY_POSITION) {

			for (position pos : Position.position.values()) {
				if (pos != position.RESERVE && pos != position.NONE) {
					IRatingQuery rq = rqf.create();
					rq.setRatingMatrix(rm);
					rq.getPositions().add(pos);
					rq.setLabel(pos.getName());
					rq.setCompIds(rm.getRatingGroup().getRatingSeries().getCompIds());
					rq.setScaleStanding(true);
					rq.setScaleComp(false);
					rq.setScaleTime(scaleTime);
					rq.setStatus(Status.NEW);
					rq.setRoundIds(rids);
					rq.setRatingMatrixId(rm.getId());
					rqf.put(rq);
					rm.getRatingQueries().add(rq);
					rm.getRatingQueryIds().add(rq.getId());
					rmf.put(rm);
				}
			}
		} else if (rm.getRatingGroup().getRatingSeries().getMode() == RatingMode.BY_MATCH) {

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
		}  else if (rm.getRatingGroup().getRatingSeries().getMode() == RatingMode.BY_COMP) {

			//for (Long rid : rids) {
				//IRound targetRound = rf.get(rid);
				IRatingQuery rq = createRatingQuery(rm);

				rq.setScaleStanding(true);
				rq.setScaleComp(true);
				rq.setScaleTime(scaleTime);

				rq.setCompIds(rm.getRatingGroup().getRatingSeries().getCompIds());

				rq.setRoundIds(rids);
				rq.setRatingMatrixId(rm.getId());
				rq.setLabel(rm.getRatingGroup().getUniversalRound().longDesc);
				rqf.put(rq);
				rm.getRatingQueries().add(rq);
				rm.getRatingQueryIds().add(rq.getId());
				rmf.put(rm);
			//}
		}

	}




	private IRatingQuery createRatingQuery(IRatingMatrix rm) {
		IRatingQuery rq = rqf.create();
		rq.setStatus(Status.NEW);
		rq.setRatingMatrix(rm);
		rq.setCompIds(rm.getRatingGroup().getRatingSeries().getCompIds());
		return rq;
	}


//	public void generateRatings(IRatingSeries rs)
//	{
//		for (IRatingGroup rg : rs.getRatingGroups())
//		{
//			for (IRatingMatrix rm : rg.getRatingMatrices()) {
//				for (IRatingQuery rq : rm.getRatingQueries()) {
//					IQueryRatingEngine qre = qref.get(mresf.getDefault());
//					qre.setQuery(rq);
//					qre.generate(mresf.getDefault(), true, true, true, false);
//					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,qre.getMetrics());
//				}
//			}
//		}
//	}




}
