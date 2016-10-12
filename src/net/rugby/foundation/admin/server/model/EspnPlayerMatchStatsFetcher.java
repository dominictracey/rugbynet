package net.rugby.foundation.admin.server.model;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.rugby.foundation.admin.server.workflow.fetchstats.FetchMatchStats.Home_or_Visitor;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ScrumPlayerMatchStats;

public class EspnPlayerMatchStatsFetcher extends JsonFetcher implements IPlayerMatchStatsFetcher {

	private IPlayerMatchStats stats;
	private String XXXurl;
	private Integer slot;
	private Home_or_Visitor hov;
	private IMatchGroup match;
	private IPlayer player;
	private IConfigurationFactory ccf;
	private IRoundFactory rf;
	private ICompetitionFactory cf;

	public EspnPlayerMatchStatsFetcher(IConfigurationFactory ccf, IRoundFactory rf, ICompetitionFactory cf) {
		this.ccf = ccf;
		this.rf = rf;
		this.cf = cf;
		
		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.WARNING);
	}
	
	@Override
	public boolean process() {
		try {
			ICompetition c = cf.get(rf.get(match.getRoundId()).getCompId());
			if (match.getForeignLeagueId() == null) {
				url = new URL(ccf.get().getBaseNodeUrl() + "v1/admin/scraper/league/" + c.getForeignID() + "/match/" + match.getForeignId() + "/player/" + player.getScrumId() + "/playerMatchStats");
			} else {
				url = new URL(ccf.get().getBaseNodeUrl() + "v1/admin/scraper/league/" + match.getForeignLeagueId() + "/match/" + match.getForeignId() + "/player/" + player.getScrumId() + "/playerMatchStats");
				
			}
			
			JSONArray json = get();			
			boolean retval = true;
			
			if (errorMessage != null && !errorMessage.isEmpty()) {
				retval = false;
			}
			
			ObjectMapper mapper = new ObjectMapper();		
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			if (json != null && json.getJSONObject(0) != null)  {
				stats = mapper.readValue(json.getJSONObject(0).toString(), ScrumPlayerMatchStats.class);
			
				// populate the non-ESPN fields
				stats.setMatchId(match.getId());
				stats.setPlayerId(player.getId());
				stats.setCountryId(player.getCountryId());
				
				if (hov == Home_or_Visitor.HOME) {
					stats.setTeamAbbr(match.getHomeTeam().getAbbr());
					stats.setTeamId(match.getHomeTeam().getId());
				} else {
					stats.setTeamAbbr(match.getVisitingTeam().getAbbr());
					stats.setTeamId(match.getVisitingTeam().getId());
				}
				
				stats.setSlot(slot);
							
				return retval;
			} else {
				// problem with json 
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "No JSON response from scraper");
				return false;
			}
			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			errorMessage = ex.getLocalizedMessage();
			errorCode = "ESPN_PLAYER_MATCH_STATS_FETCHER_EXCEPTION"; //ex.getCause().toString();
			return false;
		}

	}

	@Override
	public IPlayer getPlayer() {
		return player;
	}
	@Override
	public void setPlayer(IPlayer player) {
		this.player = player;
	}
	@Override
	public IMatchGroup getMatch() {
		return match;
	}
	@Override
	public void setMatch(IMatchGroup match) {
		this.match = match;
	}
	@Override
	public Home_or_Visitor getHov() {
		return hov;
	}
	@Override
	public void setHov(Home_or_Visitor hov) {
		this.hov = hov;
	}
	@Override
	public Integer getSlot() {
		return slot;
	}
	@Override
	public void setSlot(Integer slot) {
		this.slot = slot;
	}

	@Override
	public void set(IPlayerMatchStats stats) {
		this.stats = stats;
	}
	@Override
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	@Override
	public IPlayerMatchStats getStats() {
		return stats;
	}

	@Override
	public Boolean hasFlopped() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUrl() {
		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE, "This method doesn't return anything useful, the url of the REST service is set internally.");
		return XXXurl;
	}
	
	@Override
	public void setUrl(String url) {
		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE, "This method doesn't do anything, the url of the REST service is set internally.");
		this.XXXurl = url;
	}

	@Override
	public void setUrl(String url, Boolean flushFromCache) {
		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE, "This method doesn't do anything, the url of the REST service is set internally.");
		this.XXXurl = url;
	}

}
