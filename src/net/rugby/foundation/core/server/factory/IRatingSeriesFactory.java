package net.rugby.foundation.core.server.factory;

import java.util.HashMap;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.RatingMode;

public interface IRatingSeriesFactory extends ICachingFactory<IRatingSeries> {

	IRatingSeries get(Long compId, RatingMode mode);

	IRatingSeries build(IRatingSeries series);

	HashMap<RatingMode, Long> getModesForComp(Long compId);

	Long getDefaultSeriesId(Long compId);

}
