package net.rugby.foundation.admin.server.factory.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.admin.shared.ISeriesConfiguration.Status;
import net.rugby.foundation.admin.shared.seriesconfig.BaseSeriesConfiguration;
import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.RatingMode;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

public class OfySeriesConfigurationFactory extends BaseCachingFactory<ISeriesConfiguration> implements ISeriesConfigurationFactory {

	private Objectify ofy;
	private ICompetitionFactory cf;
	private IUniversalRoundFactory urf;
	private IRatingSeriesFactory sf;

	@Inject
	public OfySeriesConfigurationFactory(ICompetitionFactory cf, IUniversalRoundFactory urf, IRatingSeriesFactory sf) {
		this.ofy = DataStoreFactory.getOfy();
		this.cf = cf;
		this.urf = urf;
		this.sf = sf;
	}

	protected ISeriesConfiguration getFromPersistentDatastore(Long id) {
		ISeriesConfiguration sc = ofy.get(new Key<BaseSeriesConfiguration>(BaseSeriesConfiguration.class, id));
		sc.setLastRound(urf.get(sc.getLastRoundOrdinal()));
		sc.setTargetRound(urf.get(sc.getTargetRoundOrdinal()));
		sc.setSeries(sf.get(sc.getSeriesId()));
		//		for (Long compId : sc.getCompIds()) {
		//			sc.getComps().add(cf.get(compId));
		//		}
		if (sc.getHostCompId() != null) {
			sc.setHostComp(cf.get(sc.getHostCompId()));
		}

		return sc;
	}

	@Override
	public ISeriesConfiguration putToPersistentDatastore(ISeriesConfiguration sc) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();

			if (sc != null) {
				ofy.put(sc);
			}

			// repopulate the comps list
			//			sc.setComps(new ArrayList<ICompetition>());
			//			for (Long compId : sc.getCompIds()) {
			//				sc.getComps().add(cf.get(compId));
			//			}

			// if the series already exists, we may need to add or remove comps
			if (sc.getSeriesId() != null) {
				IRatingSeries rs = sf.get(sc.getSeriesId());
				if (rs.getCompIds() != null) {
					rs.getCompIds().clear();
					rs.getCompIds().addAll(sc.getCompIds());
				}
				if (rs.getCountryIds() != null) {
					rs.getCountryIds().clear();
					rs.getCountryIds().addAll(sc.getCountryIds());
				}
				if (rs.getActiveCriteria() != null) {
					rs.getActiveCriteria().clear();
					rs.getActiveCriteria().addAll(sc.getActiveCriteria());
				}
				ofy.put(rs);
				sc.setSeries(rs);
				sf.dropFromCache(rs.getId());
			}

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "put" + ex.getMessage(), ex);
			return null;
		}
		return sc;

	}

	@Override
	public boolean deleteFromPersistentDatastore(ISeriesConfiguration sc) {
		try {
			if (sc.getSeriesId() != null) {
				IRatingSeries rs = sf.get(sc.getSeriesId());
				sf.delete(rs);
			}
			ofy.delete(sc);
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,ex.getLocalizedMessage());
			return false;
		}

		return true;
	}

	@Override
	public List<ISeriesConfiguration> getAll(Boolean active) {
		try {
			Query<BaseSeriesConfiguration> qs = ofy.query(BaseSeriesConfiguration.class).order("displayName");
			if (active) {
				qs = qs.filter("live",active);
			}
			List<ISeriesConfiguration> retval = new ArrayList<ISeriesConfiguration>();
			retval.addAll(qs.list());
			for (ISeriesConfiguration sc : retval) {
				sc.setLastRound(urf.get(sc.getLastRoundOrdinal()));
				sc.setTargetRound(urf.get(sc.getTargetRoundOrdinal()));
//				if (sc.getSeriesId() != null)
//					sc.setSeries(sf.get(sc.getSeriesId()));
			}
			return retval;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getAll" + ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public ISeriesConfiguration create() {
		try {
			ISeriesConfiguration sc = new BaseSeriesConfiguration();
			sc.setStatus(Status.PENDING);
			sc.setLive(true);
			return sc;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "create" + ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public ISeriesConfiguration getForSeriesId(Long id) {
		try {
			Query<BaseSeriesConfiguration> qs = ofy.query(BaseSeriesConfiguration.class).filter("seriesId",id);
			assert (qs.list().size() == 1);
			return qs.list().get(0);
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getForSeriesId" + ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public ISeriesConfiguration getByCompAndMode(Long compId, RatingMode mode) {
		try {
			Query<BaseSeriesConfiguration> qs = ofy.query(BaseSeriesConfiguration.class).filter("hostCompId", compId).filter("mode", mode).filter("live", true);
			
			List<ISeriesConfiguration> retval = new ArrayList<ISeriesConfiguration>();
			retval.addAll(qs.list());
			for (ISeriesConfiguration sc : retval) {
				sc.setLastRound(urf.get(sc.getLastRoundOrdinal()));
				sc.setTargetRound(urf.get(sc.getTargetRoundOrdinal()));
				if (sc.getSeriesId() != null)
					sc.setSeries(sf.get(sc.getSeriesId()));
			}
			if (retval.size() == 1) {
				return retval.get(0);
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Series Configuration Factory call to getByCompAndMode with compId " + compId + " and mode " + mode.name() + " has more than one result. This is invalid so we are returning null.");
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getByCompAndMode" + ex.getMessage(), ex);
			return null;
		}
	}

}
