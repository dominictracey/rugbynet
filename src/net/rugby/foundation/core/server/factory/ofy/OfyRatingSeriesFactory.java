package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.BaseRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.IRatingGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.model.shared.RatingSeries;

public class OfyRatingSeriesFactory extends BaseRatingSeriesFactory implements IRatingSeriesFactory, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -63026632234487370L;

	@Inject
	OfyRatingSeriesFactory(IRatingGroupFactory rgf) {
		this.rgf = rgf;
	}

	@Override
	protected IRatingSeries getFromPersistentDatastore(Long id) {
		if (id != null) {
			Objectify ofy = DataStoreFactory.getOfy();
			IRatingSeries retval = ofy.get(new Key<RatingSeries>(RatingSeries.class,id));
			build(retval);
			return retval;
		} else
			return null;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IRatingSeriesFactory#put(net.rugby.foundation.model.shared.IRatingSeries)
	 */
	@Override
	protected IRatingSeries putToPersistentDatastore(IRatingSeries g) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();

			ofy.put((RatingSeries)g);

			return g;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}
	@Override
	public boolean deleteFromPersistentDatastore(IRatingSeries r) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			// be more explicit about the deleting in case we get more granular in lazy loading
			for (Long gid : r.getRatingGroupIds()) {
				IRatingGroup g = rgf.get(gid);
				if (g != null) {
					rgf.delete(g);
				}
			}
			ofy.delete(r);
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem in delete: " + ex.getLocalizedMessage());
			return false;
		}
		return true;
	}

	@Override
	public IRatingSeries create() {
		return new RatingSeries();
	}

	@Override
	public IRatingSeries get(Long compId, RatingMode mode) {

		try {
			Objectify ofy = DataStoreFactory.getOfy();
			Query<RatingSeries> qrm = ofy.query(RatingSeries.class).filter("live", true).filter("mode", mode);
			if (qrm.count() == 0) {
				return null;
			}
			for (RatingSeries qrs : qrm.list()) {
				if (qrs.getCompIds().contains(compId)) 
					return build(qrs);
			}
			return null;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem in get(Long compId, RatingMode mode): " + ex.getLocalizedMessage());
			return null;
		}
	}

	@Override
	public Map<RatingMode,Long> getModesForCompFromPersistentDatastore(Long compId) {
		Map<RatingMode,Long> map = new HashMap<RatingMode,Long>();
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			Query<RatingSeries> qrm = ofy.query(RatingSeries.class);

			for (RatingSeries qrs : qrm.list()) {
				if (qrs.getCompIds().contains(compId)) 
					map.put(qrs.getMode(), qrs.getId());
			}
			return map;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem in getModesForCompFromPersistentDatastore: " + ex.getLocalizedMessage());
			return null;
		}
	}

}
