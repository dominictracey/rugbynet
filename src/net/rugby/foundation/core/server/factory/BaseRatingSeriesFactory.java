package net.rugby.foundation.core.server.factory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.RatingMode;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Inject;

public abstract class BaseRatingSeriesFactory extends BaseCachingFactory<IRatingSeries> implements IRatingSeriesFactory {
	
	protected ICompetitionFactory cf;
	protected ICountryFactory ctf;
	protected IRatingGroupFactory rgf;
	protected ISeriesConfigurationFactory scf;

	@Inject
	public void setFactories(ICompetitionFactory cf, ICountryFactory ctf, IRatingGroupFactory rgf, ISeriesConfigurationFactory scf) {
		this.cf = cf;
		this.ctf = ctf;
		this.rgf = rgf;
		this.scf = scf;
	}
	
	@Override
	public IRatingSeries build(IRatingSeries rs)
	{
		try {
//			if (rs.getCompIds() != null && !rs.getCompIds().isEmpty()) {
//				assert(rs.getComps() != null);
//				if (!rs.getComps().isEmpty()) {
//					rs.getComps().clear();
//				}
//				for (Long cid : rs.getCompIds()) {
//					rs.getComps().add(cf.get(cid));
//				}
//			}
			
			if (rs.getCountryIds() != null && !rs.getCountryIds().isEmpty()) {
				assert (rs.getCountries() != null);
				if (!rs.getCountries().isEmpty()) {
					rs.getCountries().clear();
				}
				for (Long cid : rs.getCountryIds()) {
					rs.getCountries().add(ctf.getById(cid));
				}
			}
			
			List<Long> badRatingGroupIds = new ArrayList<Long>();
			if (rs.getRatingGroupIds() != null && !rs.getRatingGroupIds().isEmpty()) {
				assert (rs.getRatingGroups() != null);
				if (!rs.getRatingGroups().isEmpty()) {
					rs.getRatingGroups().clear();
				}
				for (Long rgid : rs.getRatingGroupIds()){
					// check for bad groups
					IRatingGroup rg = rgf.get(rgid);
					if (rg == null) {
						badRatingGroupIds.add(rgid);
					} else {
						// this will populate down to the RatingQuery
						rs.getRatingGroups().add(rg);
					}
				}
				
				if (badRatingGroupIds.size() > 0) {
					// now just drop any groups that are dangling IDs
					for (Long badId : badRatingGroupIds) {
						rs.getRatingGroupIds().remove(badId);
					}
					
					putToPersistentDatastore(rs);
				}
			}
			
			// now do a traversal of the tree to do the up links
			for (IRatingGroup rg : rs.getRatingGroups()) {
				rg.setRatingSeries(rs);
				for (IRatingMatrix rm : rg.getRatingMatrices()) {
					rm.setRatingGroup(rg);
					for (IRatingQuery rq : rm.getRatingQueries()) {
						rq.setRatingMatrix(rm);
					}
				}
			}
			
			// tell the configuration object to repull from cache
			if (rs.getId() != null) {
				scf.dropFromCache(scf.getForSeriesId(rs.getId()).getId());
			}
			
			return rs;
			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "build" + ex.getMessage(), ex);
			return null;
		}

	}
	
	private final String prefix = "RS-mode";
	
	@Override
	public HashMap<RatingMode, Long> getModesForComp(Long compId)
	{
		try {
			HashMap<RatingMode, Long> list = null;
	
			String key = prefix+compId.toString();
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();

			value = (byte[])syncCache.get(key);
			if (value == null) {
				list = getModesForCompFromPersistentDatastore(compId);
				Serializable sList = (Serializable) list;
				syncCache.delete(key);
				if (sList != null) {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutput out = new ObjectOutputStream(bos);   
					out.writeObject(sList);
					byte[] yourBytes = bos.toByteArray(); 

					out.close();
					bos.close();

					syncCache.put(key, yourBytes);
				}
				return list;
			} else {

				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				Object obj = in.readObject();

				//				if (typeLiteral.equals(obj.getClass())) {  // can't do 'obj instanceof List<T>' *sadfase*
				list = (HashMap<RatingMode, Long>)obj;

				bis.close();
				in.close();

			}
			return list;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getModesForComp" + ex.getMessage(), ex);
			return null;
		}

	}
	
	@Override
	public Long getDefaultSeriesId(Long compId) {
		Map<RatingMode, Long> modes = getModesForCompFromPersistentDatastore(compId);
		if (modes.keySet().contains(RatingMode.BY_COMP)) {
			return get(compId, RatingMode.BY_COMP).getId();
		} else if (modes.keySet().contains(RatingMode.BY_MATCH)) {
			return get(compId, RatingMode.BY_MATCH).getId();
		} else if (modes.keySet().contains(RatingMode.BY_POSITION)) {
			return get(compId, RatingMode.BY_POSITION).getId();
		} else if (modes.keySet().contains(RatingMode.BY_TEAM)) {
			return get(compId, RatingMode.BY_TEAM).getId();
		}  else if (modes.keySet().contains(RatingMode.BY_COUNTRY)) {
			return get(compId, RatingMode.BY_COUNTRY).getId();
		} else {
			throw new RuntimeException("No default mode found for compId " + compId);
		}
		
	}
	
	public abstract HashMap<RatingMode, Long> getModesForCompFromPersistentDatastore(Long compId);
	
//	@Override
//	public IRatingSeries put(IRatingSeries t) {
//		super.put(t);
//		
//		// force upstream memcache update
//		rsf.put(t.getRatingSeries());
//		
//		return t;
//	}

}
