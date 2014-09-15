package net.rugby.foundation.admin.server.factory.test;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.admin.shared.ISeriesConfiguration.ConfigurationType;
import net.rugby.foundation.admin.shared.seriesconfig.BaseSeriesConfiguration;
import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;

public class TestSeriesConfigurationFactory extends BaseCachingFactory<ISeriesConfiguration> implements ISeriesConfigurationFactory {

	private ICompetitionFactory cf;

	@Inject 
	public TestSeriesConfigurationFactory(ICompetitionFactory cf) {
		this.cf = cf;
	}
	
	@Override
	public ISeriesConfiguration create() {
		return new BaseSeriesConfiguration();
	}

	@Override
	public List<ISeriesConfiguration> getAllActive() {
		List<ISeriesConfiguration> list = new ArrayList<ISeriesConfiguration>();
		//if (compId == 1) {
			list.add(get(4750000L));
		//}
		return list;
	}

	@Override
	protected ISeriesConfiguration getFromPersistentDatastore(Long id) {
		// numbering scheme is SeriesID (75000L and up) plus 4000000L
		if (id == null) throw new RuntimeException("Don't pass null");
		
		if (id.equals(4750000L)) {
			ISeriesConfiguration sc = new BaseSeriesConfiguration();
			sc.setId(id);
			sc.setCompId(1L);
			sc.setCompName(cf.get(1L).getShortName());
			sc.setStatus("Pending");
			sc.setLastRoundId(null);
			sc.setLastRound(null);
			sc.setType(ConfigurationType.BY_POSITION);
			return sc;
		}
		
		
		return null;
	}

	@Override
	protected ISeriesConfiguration putToPersistentDatastore(
			ISeriesConfiguration t) {
		return t;
	}

	@Override
	protected boolean deleteFromPersistentDatastore(ISeriesConfiguration t) {
		return true;
	}

}
