//package net.rugby.foundation.topten.server.rest;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.persistence.Id;
//
//import net.rugby.foundation.model.shared.IHasId;
//
//import com.googlecode.objectify.annotation.Entity;
//import com.googlecode.objectify.annotation.Indexed;
//import com.googlecode.objectify.annotation.Unindexed;
//
//@Entity
//public class PlayerMatch implements Serializable, IHasId {
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = -3455969458893817777L;
//	
//	public PlayerMatch() {
//		
//	}
//	
//	@Id
//	protected Long id;
//	@Indexed
//	public Long playerId;
//	
//	@Unindexed
//	public Integer rating;
//	@Unindexed
//	public Integer ranking;
//	@Unindexed
//	public String label;
//	@Unindexed
//	public String homeTeamAbbr;
//	@Unindexed
//	public String visitingTeamAbbr;
//	@Unindexed
//	public String homeTeamScore;
//	@Unindexed
//	public String visitingTeamScore;
//	@Unindexed
//	public String matchDate;
//	@Unindexed
//	public String position;
//	@Unindexed
//	public String matchRating;
//	@Unindexed
//	public String minutesPlayed;
//	
////	public Map<String, String> notes = new HashMap<String, String>();
//	@Unindexed
//	public List<String> notes = new ArrayList<String>();
//	@Unindexed
//	public String teamAbbr;
//
//	@Override
//	public void setId(Long id) {
//		this.id = id;
//		
//	}
//	@Override
//	public Long getId() {
//		return id;
//	}
//
//	
//}
