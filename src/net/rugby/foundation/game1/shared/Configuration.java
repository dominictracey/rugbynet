/**
 * 
 */
package net.rugby.foundation.game1.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;

/**
 * @author home
 *
 */
@Entity
public class Configuration implements IConfiguration, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	
	// these are the records of the "official clubhouses" for the Competitions being run.
	// They are ids of 
	private List<Long> competitionClubhouseLeageMapIds;
	
	// Key: 	compId
	// Value:	leagueId
	@Transient
	private Map<Long,Long> leagueIdMap;
	
	@Transient
	private List<IClubhouseLeagueMap> competitionClubhouseLeageMapList;
	
	@Transient 
	private List<Long> clubhouseIds;
	
	public Configuration() {
		leagueIdMap = new HashMap<Long,Long>();
		competitionClubhouseLeageMapIds = new ArrayList<Long>();
		competitionClubhouseLeageMapList = new ArrayList<IClubhouseLeagueMap>();
		clubhouseIds = new ArrayList<Long>();
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IConfiguration#getLeagueIdMap()
	 */
	@Override
	public Map<Long,Long> getLeagueIdMap() {
		return leagueIdMap;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IConfiguration#setLeagueIdMap(java.util.Map)
	 */
	@Override
	public void setLeagueIdMap(Map<Long,Long> leagueIdMap) {
		this.leagueIdMap = leagueIdMap;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IConfiguration#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IConfiguration#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public List<Long> getCompetitionClubhouseLeageMapIds() {
		return competitionClubhouseLeageMapIds;
	}

	@Override
	public void setCompetitionClubhouseLeageMapIds(
			List<Long> competitionClubhouseLeageMapIds) {
		this.competitionClubhouseLeageMapIds = competitionClubhouseLeageMapIds;
	}

	@Override
	public List<IClubhouseLeagueMap> getCompetitionClubhouseLeageMapList() {
		return competitionClubhouseLeageMapList;
	}

	@Override
	public void setCompetitionClubhouseLeageMapList(
			List<IClubhouseLeagueMap> competitionClubhouseLeageMapList) {
		this.competitionClubhouseLeageMapList = competitionClubhouseLeageMapList;
	}

	@Override
	public List<Long> getClubhouseIds() {
		return clubhouseIds;
	}

	@Override
	public void setClubhouseIds(List<Long> clubhouseIds) {
		this.clubhouseIds = clubhouseIds;
	}
	
	
}
