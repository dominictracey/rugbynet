package net.rugby.foundation.core.server.factory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Inject;

import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.RatingMode;

public abstract class BaseRatingSeriesFactory extends BaseCachingFactory<IRatingSeries> implements IRatingSeriesFactory {
	
	private ICompetitionFactory cf;
	private ICountryFactory ctf;
	protected IRatingGroupFactory rgf;

	@Inject
	public void setFactories(ICompetitionFactory cf, ICountryFactory ctf, IRatingGroupFactory rgf) {
		this.cf = cf;
		this.ctf = ctf;
		this.rgf = rgf;
	}
	
	@Override
	public IRatingSeries build(IRatingSeries rs)
	{
		try {
			if (rs.getCompIds() != null && !rs.getCompIds().isEmpty()) {
				assert(rs.getComps() != null);
				for (Long cid : rs.getCompIds()) {
					rs.getComps().add(cf.get(cid));
				}
			}
			
			if (rs.getCountryIds() != null && !rs.getCountryIds().isEmpty()) {
				assert (rs.getCountries() != null);
				for (Long cid : rs.getCountryIds()) {
					rs.getCountries().add(ctf.getById(cid));
				}
			}
			
			if (rs.getRatingGroupIds() != null && !rs.getRatingGroupIds().isEmpty()) {
				assert (rs.getRatingGroups() != null);
				for (Long rgid : rs.getRatingGroupIds()){
					rs.getRatingGroups().add(rgf.get(rgid));
				}
			}
			
			return rs;
			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "build" + ex.getMessage(), ex);
			return null;
		}

	}
	
	private final String prefix = "RS-mode";
	
	@Override
	public List<RatingMode> getModesForComp(Long compId)
	{
		try {
			List<RatingMode> list = null;
	
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
				list = (List<RatingMode>)obj;


				bis.close();
				in.close();

			}
			return list;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getModesForComp" + ex.getMessage(), ex);
			return null;
		}

	}
	
	public abstract List<RatingMode> getModesForCompFromPersistentDatastore(Long compId);
	

}
