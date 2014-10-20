package net.rugby.foundation.core.server.factory;

import java.util.List;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.RatingMode;

public interface IRatingSeriesFactory extends ICachingFactory<IRatingSeries> {

	IRatingSeries get(Long compId, RatingMode mode);

	IRatingSeries build(IRatingSeries series);

	List<RatingMode> getModesForComp(Long compId);

	Long getDefaultSeriesId(Long compId);

}
