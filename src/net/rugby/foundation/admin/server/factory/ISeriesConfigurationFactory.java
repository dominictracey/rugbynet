package net.rugby.foundation.admin.server.factory;

import java.util.List;

import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.core.server.factory.ICachingFactory;

public interface ISeriesConfigurationFactory extends ICachingFactory<ISeriesConfiguration> {
	List<ISeriesConfiguration> getAllActive();
}
