package net.rugby.foundation.admin.server.factory.espnscrum;

import java.io.Serializable;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.IMatchRatingEngineFactory;
import net.rugby.foundation.admin.server.model.IMatchRatingEngine;
//import net.rugby.foundation.admin.server.model.ScrumMatchRatingEngineV001;
import net.rugby.foundation.admin.server.model.ScrumMatchRatingEngineV100;
import net.rugby.foundation.admin.shared.IRatingEngineSchema;
import net.rugby.foundation.admin.shared.IV1EngineWeightValues;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchRatingFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;

public class ScrumMatchRatingEngineFactory
		implements IMatchRatingEngineFactory, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5286254257580581818L;
	private final IPlayerFactory pf;
	private final IPlayerMatchRatingFactory pmrf;
	private ITeamMatchStatsFactory tmsf;

	@Inject
	public ScrumMatchRatingEngineFactory(IPlayerFactory pf, IPlayerMatchRatingFactory pmrf, ITeamMatchStatsFactory tmsf) {
		this.pf = pf;
		this.pmrf = pmrf;
		this.tmsf = tmsf;
	}
	
	@Override
	public IMatchRatingEngine get(IRatingEngineSchema schema) {
		if (schema instanceof IV1EngineWeightValues) {
			return new ScrumMatchRatingEngineV100(pf, pmrf, tmsf);
		} 
		
		return null;
	}

}
