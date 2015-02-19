package net.rugby.foundation.admin.server.factory.test;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.admin.shared.ISeriesConfiguration.Status;
import net.rugby.foundation.admin.shared.seriesconfig.BaseSeriesConfiguration;
import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.RatingMode;

public class TestSeriesConfigurationFactory extends BaseCachingFactory<ISeriesConfiguration> implements ISeriesConfigurationFactory {

	private ICompetitionFactory cf;
	private IUniversalRoundFactory urf;
	private Long count = 47500100L;

	@Inject 
	public TestSeriesConfigurationFactory(ICompetitionFactory cf, IUniversalRoundFactory urf) {
		this.cf = cf;
		this.urf = urf;
	}
	
	@Override
	public ISeriesConfiguration create() {
		return new BaseSeriesConfiguration();
	}

	@Override
	public List<ISeriesConfiguration> getAll(Boolean active) {
		List<ISeriesConfiguration> list = new ArrayList<ISeriesConfiguration>();
		//if (compId == 1) {
			list.add(get(4750000L));
			list.add(get(4750001L));
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
			sc.getCompIds().add(1L);
//			sc.getComps().add(cf.get(1L));
			//sc.getCompNames().add(cf.get(1L).getShortName());
			sc.setStatus(Status.PENDING);
			sc.setLastRound(urf.get(cf.get(1L).getRounds().get(0)));
			sc.setLastRoundOrdinal(sc.getLastRound().ordinal);
			sc.setTargetRoundOrdinal(sc.getLastRoundOrdinal() + 1);
			sc.setTargetRound(urf.get(sc.getTargetRoundOrdinal()));
			sc.setMode(RatingMode.BY_POSITION);
			sc.getActiveCriteria().add(Criteria.IN_FORM);
			sc.setLive(true);
			return sc;
		} else if (id.equals(4750001L)) {
			ISeriesConfiguration sc = new BaseSeriesConfiguration();
			sc.setId(id);
			sc.getCompIds().add(1L);
//			sc.getComps().add(cf.get(1L));
			//sc.getCompNames().add(cf.get(1L).getShortName());
			sc.setStatus(Status.PENDING);
			sc.setLastRound(urf.get(cf.get(1L).getRounds().get(0)));
			sc.setLastRoundOrdinal(sc.getLastRound().ordinal);
			sc.setTargetRoundOrdinal(sc.getLastRoundOrdinal() + 1);
			sc.setTargetRound(urf.get(sc.getTargetRoundOrdinal()));
			sc.setMode(RatingMode.BY_MATCH);
			sc.getActiveCriteria().add(Criteria.ROUND);
			sc.setLive(true);
			return sc;
		} else if (id.equals(4750002L)) {
			ISeriesConfiguration sc = new BaseSeriesConfiguration();
			sc.setId(id);
			sc.getCompIds().add(1L);
//			sc.getComps().add(cf.get(1L));
			//sc.getCompNames().add(cf.get(1L).getShortName());
			sc.setStatus(Status.PENDING);
			sc.setLastRound(urf.get(cf.get(1L).getRounds().get(0)));
			sc.setLastRoundOrdinal(sc.getLastRound().ordinal);
			sc.setTargetRoundOrdinal(sc.getLastRoundOrdinal() + 1);
			sc.setTargetRound(urf.get(sc.getTargetRoundOrdinal()));
			sc.setMode(RatingMode.BY_COMP);
			sc.getActiveCriteria().add(Criteria.ROUND);
			sc.setLive(true);
			return sc;
		}
		
		
		
		return null;
	}

	@Override
	protected ISeriesConfiguration putToPersistentDatastore(
			ISeriesConfiguration sc) {
		sc.setId(count ++);
		// repopulate the comps list
//		sc.setComps(new ArrayList<ICompetition>());
//		for (Long compId : sc.getCompIds()) {
//			sc.getComps().add(cf.get(compId));
//		}
		return sc;
	}

	@Override
	protected boolean deleteFromPersistentDatastore(ISeriesConfiguration t) {
		return true;
	}

	@Override
	public ISeriesConfiguration getForSeriesId(Long id) {
		if (id.equals(75000L)) {
			// this is wrong
			return get(4750001L); 
		}
		return null;
	}

}
