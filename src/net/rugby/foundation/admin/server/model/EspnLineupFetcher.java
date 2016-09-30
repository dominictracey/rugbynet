package net.rugby.foundation.admin.server.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.ILineupSlotFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ILineupSlot;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.LineupSlot;

public class EspnLineupFetcher extends JsonFetcher implements ILineupFetcher {
	protected IMatchGroup match;
	protected ICompetition comp;
	private IConfigurationFactory ccf;
	private IPlayerFactory pf;
	private IMatchGroupFactory mf;
	private ILineupSlotFactory lsf;
	
	protected String errorMessage;
		
	public final static String LINEUP_FETCHER_ERROR_CODE_NO_JSON_RETURNED = "14000";
	public final static String LINEUP_FETCHER_ERROR_CODE_EXCEPTION_THROWN = "14001";
	
	public EspnLineupFetcher(ILineupSlotFactory lsf, IConfigurationFactory ccf, IPlayerFactory pf, IMatchGroupFactory mf) {
		this.lsf = lsf;
		this.ccf = ccf;
		this.pf = pf;
		this.mf = mf;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.ILineupFetcher#get(boolean)
	 */
	@Override
	public List<ILineupSlot> get(boolean home) {
		try {
			String sHome = home ? "/lineUps/true" : "/lineUps/false";
			
			url = new URL(ccf.get().getBaseNodeUrl() + "v1/admin/scraper/league/" + comp.getForeignID() + "/match/" + match.getForeignId() + sHome);
			
			JSONArray json = get();			
			
			if (json != null) {
				List<ILineupSlot> retval = new ArrayList<ILineupSlot>();
				
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				for (int i=0; i<json.length(); ++i) {
					
					ILineupSlot l = mapper.readValue(json.getJSONObject(i).toString(), LineupSlot.class);
					
					IPlayer p = pf.getByScrumId(l.getForeignPlayerId());
					if (p != null) {
					 l.setPlayer(p);  // we'll need to get this sorted out.
					 l.setPlayerId(p.getId());
					}
					
					IMatchGroup m = mf.getMatchByEspnId(l.getForeignMatchId());
					if (m != null) {
						l.setMatch(m);
						l.setMatchId(m.getId());
					}
					
					l.setPos(lsf.getPosFromSlot(l.getSlot()));
					
					retval.add(l);
				}
				
				return retval;
			} else {
				errorCode = LINEUP_FETCHER_ERROR_CODE_NO_JSON_RETURNED;
				errorMessage = "No JSON in response to " + url.toString();
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "No JSON in response to " + url.toString());
				return null;
			}
			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			errorCode = LINEUP_FETCHER_ERROR_CODE_EXCEPTION_THROWN;
			errorMessage = ex.getLocalizedMessage();
			return null;
		}
		
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.ILineupFetcher#getMatch()
	 */
	@Override
	public IMatchGroup getMatch() {
		return match;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.ILineupFetcher#setMatch(net.rugby.foundation.model.shared.IMatchGroup)
	 */
	@Override
	public void setMatch(IMatchGroup match) {
		this.match = match;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.ILineupFetcher#getComp()
	 */
	@Override
	public ICompetition getComp() {
		return comp;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.ILineupFetcher#setComp(net.rugby.foundation.model.shared.ICompetition)
	 */
	@Override
	public void setComp(ICompetition comp) {
		this.comp = comp;
	}
	
}
