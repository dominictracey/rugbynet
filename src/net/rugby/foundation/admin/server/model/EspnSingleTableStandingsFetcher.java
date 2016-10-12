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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IHasId;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.Standing;
import net.rugby.foundation.model.shared.TeamGroup;

public class EspnSingleTableStandingsFetcher extends JsonFetcher implements IStandingsFetcher  {

	private IConfigurationFactory ccf;
	private ITeamGroupFactory tf;

	@Inject
	public EspnSingleTableStandingsFetcher(IConfigurationFactory ccf, ITeamGroupFactory tf) {
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
	public IStanding getStandingForTeam(ITeamGroup t) {
		Map<Long, IStanding> map = getStandings(round, comp);
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
		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "This method doesn't do anything useful, the url of the REST service is set internally.");
	}
	
	/***
	 * 
	 * @param key
	 * @return A map with Key: teamId, Value: IStanding object
	 */
	protected Map<Long,IStanding> getStandings(IRound r, ICompetition c	) {
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			Map<Long,IStanding> mr = null;
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
					mr = (Map<Long,IStanding>)obj;
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

	private Map<Long, IStanding> fetchStandings(IRound r, ICompetition c) {
		try {
			url = new URL(ccf.get().getBaseNodeUrl() + "/v1/admin/scraper/league/" + c.getForeignID() + "/standings/roundId/" + r.getId());
			
			JSONArray json = get();			
			Map<Long, IStanding> retval = new HashMap<Long, IStanding>();
			
			if (errorCode == null || errorCode.isEmpty()) {
				ObjectMapper mapper = new ObjectMapper();
				for (int i=0; i<json.length(); ++i) {
					
					IStanding s = mapper.readValue(json.getJSONObject(i).toString(), Standing.class);
					ITeamGroup t = tf.getTeamByForeignId(s.getForeignId());
					s.setTeam(t);
					s.setTeamId(t.getId());
					s.setRound(r);
					s.setRoundId(r.getId());
					retval.put(t.getId(), s);
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
