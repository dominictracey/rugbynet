package net.rugby.foundation.admin.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.server.factory.IQueryRatingEngineFactory;
import net.rugby.foundation.core.server.factory.IRatingGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingMatrixFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.model.shared.ICompetition;
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

import com.google.inject.Inject;

public class RatingSeriesManager implements IRatingSeriesManager {

	private IRatingSeriesFactory rsf;
	private IRatingMatrixFactory rmf;
	private IRatingQueryFactory rqf;
	private IRatingGroupFactory rgf;
	private IQueryRatingEngineFactory qref;
	private IMatchRatingEngineSchemaFactory mresf;

	@Inject
	public RatingSeriesManager(IRatingSeriesFactory rsf, IRatingGroupFactory rgf, IRatingMatrixFactory rmf, IRatingQueryFactory rqf, 
			IQueryRatingEngineFactory qref, IMatchRatingEngineSchemaFactory rsef) {
		this.rsf = rsf;
		this.rgf = rgf;
		this.rmf = rmf;
		this.rqf = rqf;
		this.qref = qref;
		this.mresf = rsef;
	}


	// This creates the first RatingGroups and RatingMatrices and populates them with RatingQueries
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IRatingSeriesManager#initialize(net.rugby.foundation.model.shared.IRatingSeries)
	 */
	@Override
	public List<IRatingGroup> initialize(IRatingSeries rs) {

		// create a RatingGroup for now
		IRatingGroup rg = addRatingGroup(rs, DateTime.now());		
		rs.getRatingGroups().add(rg);
		rs.getRatingGroupIds().add(rg.getId());

		rsf.put(rs);

		generateRatings(rs);

		return rs.getRatingGroups();
	}

	protected IRatingGroup addRatingGroup(IRatingSeries rs, DateTime time ) {
		IRatingGroup rg = rgf.create();
		rg.setRatingSeries(rs);
		assert(rs.getId() != null);
		rg.setRatingSeriesId(rs.getId());
		rgf.put(rg); // get id for ratingGroup

		// now create the Rating Matrices for the group
		// the criteria are the drop down box ("in form", "best over last year", "average impact", "best all time")
		for (Criteria criteria : rs.getActiveCriteria()) {
			IRatingMatrix rm = rmf.create();
			rm.setCriteria(criteria);
			rm.setRatingGroup(rg);
			rm.setRatingGroupId(rg.getId());
			rm.setGenerated(DateTime.now().toDate());
			rm.setRatingGroupId(rg.getId());
			rm.setRatingGroup(rg);
			rmf.put(rm);
			generateRatingQueries(rm);
			rg.getRatingMatrices().add(rm);
			rg.getRatingMatrixIds().add(rm.getId());

		}

		rgf.put(rg);

		return rg;
	}

	private List<Long> findRoundsForRatingMatrices(IRatingMatrix rm) {
		List<Long> rids = new ArrayList<Long>();
		assert(rm != null);
		try {
			IRatingSeries rs = rm.getRatingGroup().getRatingSeries();

			for (ICompetition comp : rs.getComps()) {
				IRound round = comp.getPrevRound();
				if (rs.getMode() == RatingMode.BY_LAST_MATCH) {
					rids.add(round.getId());
				} else if (rm.getCriteria() == Criteria.BEST_YEAR) {
					// get the rounds for the comps that happened in the last year.
					List<IRound> rounds = getRoundsFromLastYear(comp);
					for (IRound r : rounds) {
						rids.add(r.getId());
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
					rq.setCompIds(rm.getRatingGroup().getRatingSeries().getCompIds());
					rq.setScaleStanding(true);
					rq.setScaleComp(false);
					rq.setScaleTime(scaleTime);
					rq.setStatus(Status.NEW);
					rq.setRoundIds(rids);
					rqf.put(rq);
					rm.getRatingQueries().add(rq);
					rm.getRatingQueryIds().add(rq.getId());
					rmf.put(rm);
				}
			}
		}


	}

	public void generateRatings(IRatingSeries rs)
	{
		for (IRatingGroup rg : rs.getRatingGroups())
		{
			for (IRatingMatrix rm : rg.getRatingMatrices()) {
				for (IRatingQuery rq : rm.getRatingQueries()) {
					IQueryRatingEngine qre = qref.get(mresf.getDefault());
					qre.setQuery(rq);
					qre.generate(mresf.getDefault(), true, true, true);
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,qre.getMetrics());
				}
			}
		}
	}


}
