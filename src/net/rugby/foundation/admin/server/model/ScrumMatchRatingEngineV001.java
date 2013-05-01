package net.rugby.foundation.admin.server.model;

import java.util.ArrayList;
import java.util.List;

import net.rugby.foundation.admin.shared.IMatchRatingEngineSchema;
import net.rugby.foundation.admin.shared.ScrumMatchRatingEngineSchema20130121;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchRatingFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerMatchRating;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ITeamMatchStats;

public class ScrumMatchRatingEngineV001 implements
		IMatchRatingEngine  {

	ITeamMatchStats homeTeamStats;
	ITeamMatchStats visitTeamStats;
	List<IPlayerMatchStats> homePlayerStats;
	List<IPlayerMatchStats> visitPlayerStats;
	private List<IMatchRatingEngineSchema> supportedSchemas;
	private final IPlayerMatchRatingFactory pmrf;
	private final IPlayerFactory pf;
	
//	public void SetFactories(IPlayerMatchRatingFactory pmrf, IPlayerFactory pf) {
//		this.pmrf = pmrf;
//		this.pf = pf;
//	}
	
//	public ScrumMatchRatingEngineV001() {
//		supportedSchemas = new ArrayList<IMatchRatingEngineSchema>();
//		supportedSchemas.add(new ScrumMatchRatingEngineSchema20130121());
//	}
	
	public ScrumMatchRatingEngineV001(IPlayerFactory pf, IPlayerMatchRatingFactory pmrf) {
		supportedSchemas = new ArrayList<IMatchRatingEngineSchema>();
		supportedSchemas.add(new ScrumMatchRatingEngineSchema20130121());
		this.pmrf = pmrf;
		this.pf = pf;
	}

	@Override
	public void setTeamStats(ITeamMatchStats homeStats,
			ITeamMatchStats visitStats) {
		this.homeTeamStats = homeStats;
		this.visitTeamStats = visitStats;
	}

	@Override
	public void setPlayerStats(List<IPlayerMatchStats> homeStats,
			List<IPlayerMatchStats> visitStats) {
		this.homePlayerStats = homeStats;
		this.visitPlayerStats = visitStats;

	}

	@Override
	public List<IPlayerMatchRating> generate(IMatchRatingEngineSchema schema, IMatchGroup match) {
		List<IPlayerMatchRating> mrl = new ArrayList<IPlayerMatchRating>();
		for (IPlayerMatchStats pms : homePlayerStats) {
			if (pms != null) {
				IPlayerMatchRating pmr = pmrf.getNew(pf.getById(pms.getPlayerId()), match, 500, schema, pms);
				pmrf.put(pmr);
				mrl.add(pmr);
			}
		}
		
		for (IPlayerMatchStats pms : visitPlayerStats) {
			if (pms != null) {
				IPlayerMatchRating pmr = pmrf.getNew(pf.getById(pms.getPlayerId()), match, 500, schema, pms);
				pmrf.put(pmr);
				mrl.add(pmr);
			}
		}
		
		return null;
	}

	@Override
	public List<IMatchRatingEngineSchema> getSupportedSchemas() {

		return supportedSchemas;
	}

}
