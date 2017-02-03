package net.rugby.foundation.admin.server.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.IStandingFull;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.Standing;
import net.rugby.foundation.model.shared.StandingFull;

public class EspnMultiTableStandingsFetcher extends JsonFetcher implements IStandingsFetcher  {

	private IConfigurationFactory ccf;
	private ITeamGroupFactory tf;

	@Inject
	public EspnMultiTableStandingsFetcher(IConfigurationFactory ccf, ITeamGroupFactory tf) {
		this.ccf = ccf;
		this.tf = tf;
	}
	
	private IRound round;
	private ICompetition comp;

	@Override
	public void setRound(IRound r) {
		this.round = r;
	}

	@Override
	public void setComp(ICompetition c) {
		this.comp = c;

	}

	@Override
	public IStandingFull getStandingForTeam(ITeamGroup t) {
		Map<Long, IStandingFull> map = getStandings(round, comp);
		if (map != null) {
			if (map.containsKey(t.getId())) {
				return map.get(t.getId());
			}
		}
			
		return null;

	}

	@Override
	public void setUc(IUrlCacher uc)  {
		
	}

	@Override
	public void setUrl(String url) {
		//Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "This method doesn't do anything useful, the url of the REST service is set internally.");
	}
	
	/***
	 * 
	 * @param key
	 * @return A map with Key: teamId, Value: IStanding object
	 */
	protected Map<Long,IStandingFull> getStandings(IRound r, ICompetition c	) {
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			Map<Long,IStandingFull> mr = null;
			String id = c.getId().toString() + "-" + r.getId().toString();
			value = (byte[])syncCache.get(id);
			if (value == null) {
				mr = fetchStandings(r,c);

				if (mr != null) {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutput out = new ObjectOutputStream(bos);   
					out.writeObject(mr);
					byte[] yourBytes = bos.toByteArray(); 

					out.close();
					bos.close();

					if (yourBytes.length < 1048000) {
						syncCache.put(id, yourBytes);
					} else {
						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"** attempt to store oversize standing map\n" + syncCache.getStatistics());
					}
				}
			} else {

				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				Object obj = in.readObject();
				if (obj instanceof Map<?,?>) {  // can't do 'obj instanceof T' *sadfase*
					mr = (Map<Long,IStandingFull>)obj;
				}

				bis.close();
				in.close();

			}
			return mr;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	private Map<Long, IStandingFull> fetchStandings(IRound r, ICompetition c) {
		try {
			url = new URL(ccf.get().getBaseNodeUrl() + "/v1/admin/scraper/league/" + c.getForeignID() + "/poolStandings");
			
			JSONArray json = get();			
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, json.toString());
			Map<Long, IStandingFull> retval = new HashMap<Long, IStandingFull>();
			
			if (errorCode == null || errorCode.isEmpty()) {
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				for (int i=0; i<json.length(); ++i) {
					JSONObject pool = json.getJSONObject(i);
					String poolName = pool.getString("pool");
					JSONArray poolArray = pool.getJSONArray("standings");
					for (int j=0; j<poolArray.length(); ++j) {
						IStandingFull s = mapper.readValue(poolArray.getJSONObject(j).toString(), StandingFull.class);
						ITeamGroup t = tf.getTeamByForeignId(s.getForeignId());
						if (t != null) {
							s.setTeam(t);
							s.setTeamId(t.getId());
							s.setRound(r);
							s.setRoundId(r.getId());
							s.setPool(poolName);
							retval.put(t.getId(), s);
						} else {
							Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Couldn't find a team in our database with foreignID " + s.getForeignId() + ". Probably fucking Bristol.");
						}
					}
				}
				return retval;
			} else {
				return null;
			}	
			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

}
