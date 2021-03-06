package net.rugby.foundation.admin.server.factory;

import java.util.List;

import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.core.server.factory.ICachingFactory;
import net.rugby.foundation.model.shared.RatingMode;

public interface ISeriesConfigurationFactory extends ICachingFactory<ISeriesConfiguration> {
	List<ISeriesConfiguration> getAll(Boolean active);

	ISeriesConfiguration getForSeriesId(Long id);

	ISeriesConfiguration getByCompAndMode(Long compId, RatingMode byMatch);
}
