package net.rugby.foundation.game1.client.place;

import net.rugby.foundation.model.shared.LoginInfo;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/**
 * A place object representing a particular state of the UI. A Place can be converted to and from a
 * URL history token by defining a {@link PlaceTokenizer} for each {@link Place}, and the 
 * {@link PlaceHistoryHandler} automatically updates the browser URL corresponding to each 
 * {@link Place} in your app.
 */
public class Game1Place extends Place {
  
	private String seps = "[,;]";
	
	/**
	 * entryId is the game entry currently being worked with 
	 */
	private Long entryId = 0L;

	/**
	 * clubhouseId identifies current clubhouse
	 */
	private Long clubhouseId = 0L;
	
	/**
	 * Currently selected competition
	 */
	private Long competitionId = 0L;
	
	/**
	 * Currently selected tab
	 */
	private Long tab = 0L;
	
	/**
	 * 
	 * @param token
	 */
	public Game1Place(String token) {
		String[] tok = token.split(seps);
		
		if (tok.length == 4) {
			if (checkInput(tok[0]))
				this.setEntryId(new Long(tok[0]));
			else
				this.setEntryId(null);
			
			if (checkInput(tok[1]))
				this.setClubhouseId(new Long(tok[1]));
			else
				this.setClubhouseId(null);
			
			if (checkInput(tok[2]))
				this.setCompetitionId(new Long(tok[2]));
			else
				this.setCompetitionId(null);
			
			if (checkInput(tok[3]))
				this.setTab(new Long(tok[3]));
			else
				this.setTab(null);
		}
	}

	/**
	 * @param string
	 * @return
	 */
	private boolean checkInput(String string) {
		if (string.matches("^(0|[1-9][0-9]*)$"))
			return true;
		else
			return false;
	}

	public Game1Place(Long eid, Long lid, Long cid, Long tab) {
		this.setEntryId(eid);
		this.setClubhouseId(lid);
		this.setCompetitionId(cid);
		this.setTab(tab);
	}

	/**
	 * @param loginInfo
	 */
	public Game1Place(LoginInfo loginInfo) {
		this.setEntryId(loginInfo.getLastEntryId());
		this.setClubhouseId(loginInfo.getLastClubhouseId());
		this.setCompetitionId(loginInfo.getLastCompetitionId());	
		this.setTab(0L);
	}

	public Long getEntryId() {
		return entryId;
	}

	public void setEntryId(Long entryId) {
		this.entryId = entryId;
	}

	public Long getClubhouseId() {
		return clubhouseId;
	}

	public void setClubhouseId(Long clubhouseId) {
		this.clubhouseId = clubhouseId;
	}

	public Long getCompetitionId() {
		return competitionId;
	}

	public void setCompetitionId(Long competitionId) {
		this.competitionId = competitionId;
	}

	public Long getTab() {
		return tab;
	}

	public void setTab(Long tab) {
		this.tab = tab;
	}

	/**
	 * PlaceTokenizer knows how to serialize the Place's state to a URL token.
	 */
	public static class Tokenizer implements PlaceTokenizer<Game1Place> {

		@Override
		public String getToken(Game1Place place) {
			String retval = "";
			if (place.getEntryId() != null) 
				retval +=place.getEntryId().toString();
			retval += ",";
			if (place.getClubhouseId() != null)
				retval += place.getClubhouseId().toString();
			retval += ",";
			if (place.getCompetitionId() != null)
				retval += place.getCompetitionId().toString();
			
			retval += ";"; //semicolon for intra-activity browser history
			if (place.getTab() != null)
				retval += place.getTab().toString();
			
			return  retval;
		}

		@Override
		public Game1Place getPlace(String token) {
			return new Game1Place(token);
		}

	}
}
