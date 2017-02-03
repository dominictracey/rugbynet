package net.rugby.foundation.core.server.factory;

import java.util.List;

import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStandingFull;

public interface IStandingFullFactory extends ICachingFactory<IStandingFull> {

	List<IStandingFull> getForRound(IRound r);

	List<IStandingFull> getLatestForComp(Long compId);



}