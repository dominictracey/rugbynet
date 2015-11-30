package net.rugby.foundation.core.server.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.IServerPlace;
import net.rugby.foundation.model.shared.IServerPlace.PlaceType;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.model.shared.ServerPlace;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;
import net.rugby.foundation.admin.server.util.Hashids;


public abstract class BasePlaceFactory extends BaseCachingFactory<IServerPlace> implements IPlaceFactory {

	private ITopTenListFactory ttlf;
	private IRatingQueryFactory rqf;
	private IConfigurationFactory ccf;
	private IRatingSeriesFactory rsf;
	private IRatingMatrixFactory rmf;
	private IRatingGroupFactory rgf;

	public BasePlaceFactory(ITopTenListFactory ttlf, IRatingQueryFactory rqf, IConfigurationFactory ccf, IRatingSeriesFactory rsf, IRatingMatrixFactory rmf, IRatingGroupFactory rgf) {
		this.ttlf = ttlf;
		this.rqf = rqf;
		this.ccf = ccf;
		this.rsf = rsf;
		this.rmf = rmf;
		this.rgf = rgf;
	}
	
	private Hashids hashids = null;
	private final String SALT = "The Rugby Net Championship";
	private final String DEFAULT_S_GUID = "DEFAULT_S_GUID";
	@Override
	public IServerPlace getForGuid(String guid) {
		try {
			IServerPlace place = null;
			
			if (guid == null || guid.isEmpty()) {
				guid = DEFAULT_S_GUID;
			}
	
			place = getItem(getGuidCacheId(guid));		
			
			if (place != null) {
				return place;
			}

			if (place == null && guid.equals(DEFAULT_S_GUID)) {
				place = buildDefaultSPlace();
			} else if (place == null) {

				if (hashids == null) {
					hashids = new Hashids(SALT);
				}
				long[] ids = hashids.decode(guid);
				if (ids.length > 0) {
					place = get(ids[0]);
				}
			}
			if (place != null) {
				putItem(getGuidCacheId(guid), place);
			} else {
				return null;
			} 
			
			// repair - type should never be null
			if (place.getType() == null) {
				place.setType(PlaceType.SERIES);
			}
			
			return place;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getForGuid" + ex.getMessage(), ex);
			return null;
		}
	}
	
	private IServerPlace buildDefaultSPlace() {
		IServerPlace p = new ServerPlace();
		p.setCompId(ccf.get().getDefaultCompId());
		ITopTenList feature = ttlf.getLatestForComp(p.getCompId());
		if (feature == null) {
			p.setSeriesId(rsf.getDefaultSeriesId(p.getCompId()));
			p.setType(PlaceType.SERIES);
		} else {
			p.setListId(feature.getId());
			p.setType(PlaceType.FEATURE);
		}
		return p;
	}

	private String getGuidCacheId(String guid) {
		return guidPrefix + guid;
	}

	private final String guidPrefix = "spfGUID-";
	
	@Override
	public IServerPlace getForName(String name) {
		try {
			IServerPlace place = null;
	
			place = getItem(getNameCacheId(name));			

			if (place == null) {
				place = getForNameFromPersistentDatastore(name);

				if (place != null) {
					putItem(getNameCacheId(name), place);
				}	else {
					return null;
				}
			} 
			
			// repair - type should never be null
			if (place.getType() == null) {
				place.setType(PlaceType.SERIES);
			}
			
			return place;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getForName" + ex.getMessage(), ex);
			return null;
		}
	}
	
	private final String namePrefix = "spfNAME-";

	private String getNameCacheId(String name) {
		return namePrefix + name;
	}

	protected abstract IServerPlace getForNameFromPersistentDatastore(String name);

	protected String generate(Long id) {
		try {
			
			if (hashids == null) {
				hashids = new Hashids(SALT);
			}
			String hash = hashids.encode(id);
			return hash;
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "generate" + e.getMessage(), e);
			return null;
		}	
	}
	
	@Override
	public void buildItem(IServerPlace p, ITopTenItem item) {
		p.setItemId(item.getId());
		p.setType(PlaceType.SERIES);
		buildList(p, ttlf.get(item.getParentId()));
	}

	@Override
	public void buildList(IServerPlace p, ITopTenList ttl) {
		p.setListId(ttl.getId());
		p.setType(PlaceType.SERIES);
		buildQuery(p, rqf.get(ttl.getQueryId()));
	}
	@Override
	public void buildQuery(IServerPlace p, IRatingQuery rq) {
		p.setQueryId(rq.getId());
		p.setType(PlaceType.SERIES);
		buildMatrix(p, rmf.get(rq.getRatingMatrixId()));
	}
	@Override
	public void buildMatrix(IServerPlace p, IRatingMatrix rm) {
		p.setMatrixId(rm.getId());
		p.setType(PlaceType.SERIES);
		buildGroup(p, rgf.get(rm.getRatingGroupId()));
	}
	@Override
	public void buildGroup(IServerPlace p, IRatingGroup rg) {
		p.setGroupId(rg.getId());
		p.setType(PlaceType.SERIES);
		buildSeries(p,rsf.get(rg.getRatingSeriesId()));
	}
	@Override
	public void buildSeries(IServerPlace p, IRatingSeries rs) {
		p.setSeriesId(rs.getId());
		p.setType(PlaceType.SERIES);
		if (rs.getCompIds().size() > 1) {
			buildComp(p,ccf.get().getDefaultCompId());
		} else {
			buildComp(p,rs.getCompIds().get(0));
			
		}
	}
	@Override
	public void buildComp(IServerPlace p, Long compId) {
		p.setCompId(compId);
		p.setType(PlaceType.SERIES);
		put(p);
	}
	


}
