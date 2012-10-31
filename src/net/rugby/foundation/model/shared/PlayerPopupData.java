package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class  PlayerPopupData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private Long id;
	private boolean thumbnail;  //show thumbnail or flag
	private String teamName;
	private PlayerRowData rowData;
	
	ArrayList<MatchPopupData> matchData = new ArrayList<MatchPopupData>();
	
	public PlayerPopupData() {
		//new PlayerPopupData(0L,false,"",new ArrayList<MatchPopupData>(), null);
	}



	public PlayerPopupData(Long id, boolean thumbnail, String teamName,
			ArrayList<MatchPopupData> matchData, PlayerRowData rowData) {
		super();
		this.id = id;
		this.thumbnail = thumbnail;
		this.teamName = teamName;
		this.matchData = matchData;
		this.rowData = rowData;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(boolean thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public ArrayList<MatchPopupData> getMatchData() {
		return matchData;
	}

	public void setMatchData(ArrayList<MatchPopupData> matchData) {
		this.matchData = matchData;
	}



	public PlayerRowData getRowData() {
		return rowData;
	}



	public void setRowData(PlayerRowData rowData) {
		this.rowData = rowData;
	}
	
//	
//	@SuppressWarnings("serial")
//	@Entity
//	public class PlayerRowData implements Serializable {
//	  @Id
//	  private Long id;
//	  private String displayName;
//	  private String teamAbbr;
//	  private Key<Group> team;
//	  private String pool;
//	  private Long origRating;
//	  private Long overallRating;
//	  private Long positionRating;
//	  private Long classRating;
//	  private Long lastRating;
//	  private Player.movement movement;



	public void addMatchPopupData(String displayName, String url,
			Long overallRating, long posRating, long classRating) {
		if (matchData == null) {
			matchData = new ArrayList<MatchPopupData>();
		}
		matchData.add(new MatchPopupData(displayName, url, overallRating, posRating, classRating));
		
	}

	
}
