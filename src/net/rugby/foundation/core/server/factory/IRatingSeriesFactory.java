package net.rugby.foundation.core.server.factory;

import java.util.Map;

import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.RatingMode;

public interface IRatingSeriesFactory extends ICachingFactory<IRatingSeries> {

	IRatingSeries get(Long compId, RatingMode mode);

	IRatingSeries build(IRatingSeries series);

	Map<RatingMode, Long> getModesForComp(Long compId);

	Long getDefaultSeriesId(Long compId);

}
