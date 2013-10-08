package net.rugby.foundation.core.server.factory;

import java.util.List;

import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStanding;

public interface IStandingFactory extends ICachingFactory<IStanding> {

	List<IStanding> getForRound(IRound r);


}
