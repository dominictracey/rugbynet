package net.rugby.foundation.core.server.factory.ofy;

import com.google.inject.Inject;

import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IStandingFullFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.StandingFull;

public class OfyStandingFullFactory extends OfyStandingFactory implements IStandingFullFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3011818629485838725L;

	@Inject
	public OfyStandingFullFactory(IRoundFactory rf, ITeamGroupFactory tf, ICompetitionFactory cf) {
		super(rf, tf, cf);
	}
	
	@Override
	public IStanding create() {
		return new StandingFull();
	}

}
