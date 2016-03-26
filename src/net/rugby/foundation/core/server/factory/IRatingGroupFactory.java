package net.rugby.foundation.core.server.factory;

import net.rugby.foundation.model.shared.IRatingGroup;

public interface IRatingGroupFactory extends ICachingFactory<IRatingGroup> {

	void deleteTTLs(IRatingGroup rg);

	IRatingGroup getForUR(Long ratingSeriesId, int universalRoundOrdinal);

}
