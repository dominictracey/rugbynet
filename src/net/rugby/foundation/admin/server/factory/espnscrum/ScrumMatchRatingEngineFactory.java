package net.rugby.foundation.admin.server.factory.espnscrum;

import java.io.Serializable;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.IMatchRatingEngineFactory;
import net.rugby.foundation.admin.server.model.IMatchRatingEngine;
//import net.rugby.foundation.admin.server.model.ScrumMatchRatingEngineV001;
import net.rugby.foundation.admin.server.model.ScrumMatchRatingEngineV100;
import net.rugby.foundation.admin.shared.IMatchRatingEngineSchema;
import net.rugby.foundation.admin.shared.ScrumMatchRatingEngineSchema20130121;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchRatingFactory;

public class ScrumMatchRatingEngineFactory
		implements IMatchRatingEngineFactory, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5286254257580581818L;
	private final IPlayerFactory pf;
	private final IPlayerMatchRatingFactory pmrf;

	@Inject
	public ScrumMatchRatingEngineFactory(IPlayerFactory pf, IPlayerMatchRatingFactory pmrf) {
		this.pf = pf;
		this.pmrf = pmrf;
	}
	
	@Override
	public IMatchRatingEngine get(IMatchRatingEngineSchema schema) {
		if (schema instanceof ScrumMatchRatingEngineSchema20130121) {
			return new ScrumMatchRatingEngineV100(pf, pmrf);
		}
		
		return null;
	}

}
