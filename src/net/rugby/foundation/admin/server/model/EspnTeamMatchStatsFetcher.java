package net.rugby.foundation.admin.server.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.json.JSONArray;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.ILineupSlotFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ILineupSlot;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.LineupSlot;
import net.rugby.foundation.model.shared.ScrumTeamMatchStats;

public class EspnTeamMatchStatsFetcher extends JsonFetcher implements ITeamMatchStatsFetcher {
	private IConfigurationFactory ccf;
	protected String errorMessage;
	private ITeamGroupFactory tf;
	private ITeamMatchStatsFactory tmsf;
	private IMatchGroupFactory mf;
		
	private ITeamMatchStats tms = null;
	
	public final static String TEAM_STATS_FETCHER_NO_JSON = "12000";
	public final static String TEAM_STATS_FETCHER_INVALID_JSON = "12001";
	public final static String TEAM_STATS_FETCHER_EXCEPTION_RAISED = "12002";
	
	public EspnTeamMatchStatsFetcher(ITeamGroupFactory tf, ITeamMatchStatsFactory tmsf, IMatchGroupFactory mf, IConfigurationFactory ccf) {
		this.tf = tf;
		this.ccf = ccf;
		this.tmsf = tmsf;
		this.mf = mf;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.ILineupFetcher#get(boolean)
	 */
	@Override
	public boolean process(IMatchGroup match, ICompetition comp, Boolean home) {
		try {
			String sHome = home ? "/teamStats/home" : "/teamStats/visitor";
			
			if (match.getForeignLeagueId() == null) {
				url = new URL(ccf.get().getBaseNodeUrl() + "v1/admin/scraper/league/" + comp.getForeignID() + "/match/" + match.getForeignId() + sHome);
			} else {
				url = new URL(ccf.get().getBaseNodeUrl() + "v1/admin/scraper/league/" + match.getForeignLeagueId() + "/match/" + match.getForeignId() + sHome);
			}
			JSONArray json = get();			

			if (json != null) {
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				tms = mapper.readValue(json.getJSONObject(0).toString(), ScrumTeamMatchStats.class);
				
				ITeamGroup t = home ? match.getHomeTeam() : match.getVisitingTeam();
				if (t != null) {
					 tms.setTeamId(t.getId());  // we'll need to get this sorted out.
					 tms.setCreated(DateTime.now().toDate());
					 tms.setIsHome(home);
					 tms.setMatchId(match.getId());
					 tms.setTeamAbbr(t.getAbbr());
					 tmsf.put(tms);
				} 
			} else {
				tms = emptyTMS(match, comp, home);
			}

			
			return errorCode == null;
			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			errorCode = "TSMF.process";
			errorMessage = ex.getLocalizedMessage();
			tms = emptyTMS(null, null, false);
			return false;
		}
		
	}

	@Override
	public ITeamMatchStats getStats() {
		return tms;
	}
	
	private ITeamMatchStats emptyTMS(IMatchGroup m, ICompetition c, Boolean home) {
		ITeamMatchStats blank = tmsf.create();
		blank.setIsHome(home);
		if (m != null) {
			blank.setMatchId(m.getId());
		
			if (home) {
				if (m.getHomeTeam() != null) {
					blank.setTeamAbbr(m.getHomeTeam().getAbbr());
				}
				blank.setTeamId(m.getHomeTeamId());
			} else {
				if (m.getVisitingTeam() != null) {
					blank.setTeamAbbr(m.getVisitingTeam().getAbbr());
				}
				blank.setTeamId(m.getVisitingTeamId());
			}
		}
		
		return blank;
	}
	
}
