package net.rugby.foundation.admin.server.factory.espnscrum;

import java.io.Serializable;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.IQueryRatingEngineFactory;
import net.rugby.foundation.admin.server.model.IQueryRatingEngine;
import net.rugby.foundation.admin.server.model.ScrumQueryRatingEngineV100;
import net.rugby.foundation.admin.server.model.ScrumTimeSeriesRatingEngineV100;
import net.rugby.foundation.admin.shared.IRatingEngineSchema;
import net.rugby.foundation.admin.shared.IV1EngineWeightValues;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchRatingFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.model.shared.IRatingQuery;

public class ScrumQueryRatingEngineFactory
		implements IQueryRatingEngineFactory, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5286254257580581818L;
	private final IPlayerFactory pf;
	private final IPlayerMatchRatingFactory pmrf;
	private ITeamMatchStatsFactory tmsf;
	private IMatchGroupFactory mgf;
	private IRoundFactory rf;
	private IStandingFactory sf;
	private IPlayerMatchStatsFactory pmsf;
	private IRatingQueryFactory rqf;
	private ICompetitionFactory cf;
	private IPlayerRatingFactory prf;

	@Inject
	public ScrumQueryRatingEngineFactory(IPlayerFactory pf, IMatchGroupFactory mgf, IPlayerMatchRatingFactory pmrf, ITeamMatchStatsFactory tmsf, 
			IRoundFactory rf, IStandingFactory sf, IPlayerMatchStatsFactory pmsf, IRatingQueryFactory rqf, ICompetitionFactory cf, IPlayerRatingFactory prf) {
		this.pf = pf;
		this.pmrf = pmrf;
		this.tmsf = tmsf;
		this.mgf = mgf;
		this.rf = rf;
		this.sf = sf;
		this.pmsf = pmsf;
		this.rqf = rqf;
		this.cf = cf;
		this.prf = prf;
	}
	
	@Override
	public IQueryRatingEngine get(IRatingEngineSchema schema, IRatingQuery rq) {
		if (schema instanceof IV1EngineWeightValues) {
			if (rq.getRoundIds().size() == 1) {
				return new ScrumQueryRatingEngineV100(pf, mgf, pmrf, rf, sf, pmsf, tmsf, rqf, cf);
			} else {
				return new ScrumTimeSeriesRatingEngineV100(pf, mgf, pmrf, rf, sf, pmsf, tmsf, rqf, cf, prf);
			}
		} 
		
		return null;
	}

	@Override
	public IQueryRatingEngine get(IRatingEngineSchema schema) {
		if (schema instanceof IV1EngineWeightValues) {
			return new ScrumQueryRatingEngineV100(pf, mgf, pmrf, rf, sf, pmsf, tmsf, rqf, cf);
		} 
		
		return null;
	}

}
