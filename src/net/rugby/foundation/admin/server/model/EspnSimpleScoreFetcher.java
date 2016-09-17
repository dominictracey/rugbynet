package net.rugby.foundation.admin.server.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.ILineupSlotFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IMatchResultFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ILineupSlot;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.WorkflowStatus;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.IMatchResult.ResultType;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ISimpleScoreMatchResult;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.LineupSlot;
import net.rugby.foundation.model.shared.SimpleScoreMatchResult;

public class EspnSimpleScoreFetcher extends JsonFetcher implements IResultFetcher {
	protected IMatchGroup match;
	protected ICompetition comp;
	private IConfigurationFactory ccf;
	private IMatchGroupFactory mf;

	protected String errorMessage;
	private IMatchResultFactory mrf;

		
	public EspnSimpleScoreFetcher(IConfigurationFactory ccf, IMatchGroupFactory mf, IMatchResultFactory mrf) {

		this.ccf = ccf;
		this.mf = mf;
		this.mrf = mrf;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.ILineupFetcher#get(boolean)
	 */
	@Override
	public IMatchResult getResult(IMatchGroup match)  {
		return _getResult(match,true);		
	}
	
	protected IMatchResult _getResult(IMatchGroup match, boolean save) {
		try {
			IMatchResult result = null;
			
			url = new URL(ccf.get().getBaseNodeUrl() + "v1/admin/scraper/league/" + comp.getForeignID() + "/match/" + match.getForeignId() + "/result");
			
			JSONArray json = get();			
			
			if (json != null) {
				if (errorMessage != "null") {
					ObjectMapper mapper = new ObjectMapper();
					for (int i=0; i<json.length(); ++i) {
						
						result = mapper.readValue(json.getJSONObject(i).toString(), SimpleScoreMatchResult.class);
		
						if (result != null) {
							result.setMatchID(match.getId());
							result.setRecordedDate(new Date());
							result.setSource(url.toString());
							result.setType(ResultType.SIMPLE_SCORE);
							
							if (save) {
								mrf.put(result);
								match.setSimpleScoreMatchResultId(result.getId());
								match.setSimpleScoreMatchResult((ISimpleScoreMatchResult)result);
								match.setStatus(result.getStatus());
								match.setWorkflowStatus(WorkflowStatus.FINAL);
								match.setLocked(true);
								mf.put(match);				
							}
							
						} else {
							Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Problem parsing MatchResult from JSON response to " + url.toString());
						}
					}
				} else {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Error returned from " + url.toString() + " " + errorCode + " : " + errorMessage);
				}
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "The node.js server is probably down trying to access " + url.toString());
			}

 			return result;
			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			errorMessage = ex.getLocalizedMessage();
			return null;
		}
	}
	

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.ILineupFetcher#setComp(net.rugby.foundation.model.shared.ICompetition)
	 */
	@Override
	public void setComp(ICompetition comp) {
		this.comp = comp;
	}

	@Override
	public void setRound(IRound round) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, IMatchGroup> getMatches(String url, Map<String, ITeamGroup> teams) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isAvailable(IMatchGroup match) {
		try {
			IMatchResult r = _getResult(match, false);
			if (r != null) {
				if (r.getStatus().toString().contains("FINAL") ) {
					return true;
				}
			}
			return false;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			errorMessage = ex.getLocalizedMessage();
			return false;
		}
	}
	
}
