package net.rugby.foundation.topten.server.rest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PlayerMatch implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3455969458893817777L;
	
	public PlayerMatch() {
		
	}
	public Integer rating;
	public Integer ranking;
	
	public String label;
	public String homeTeamAbbr;
	public String visitingTeamAbbr;
	public String homeTeamScore;
	public String visitingTeamScore;
	public String matchDate;
	public String position;
	public String matchRating;
	public String minutesPlayed;
	
	public Map<String, String> notes = new HashMap<String, String>();
	public String teamAbbr;

	
}
