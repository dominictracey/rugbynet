/**
 * 
 */
package net.rugby.foundation.game1.shared;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

/**
 * @author home
 *
 */
@Entity
public class ClubhouseLeagueMap implements IClubhouseLeagueMap, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	
	private Long leagueId;
	private Long compId;
	private Long clubhouseId;
	
	public ClubhouseLeagueMap()
	{
		this.compId = null;
		this.clubhouseId = null;
		this.leagueId = null;

	}
	
	public ClubhouseLeagueMap(Long compId, Long clubhouseId, Long leagueId) {
		this.compId = compId;
		this.clubhouseId = clubhouseId;
		this.leagueId = leagueId;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IDefaultLeague#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IDefaultLeague#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IDefaultLeague#getLeagueId()
	 */
	@Override
	public Long getLeagueId() {
		return leagueId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IDefaultLeague#setLeagueId(java.lang.Long)
	 */
	@Override
	public void setLeagueId(Long leagueId) {
		this.leagueId = leagueId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IDefaultLeague#getCompId()
	 */
	@Override
	public Long getCompId() {
		return compId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IDefaultLeague#setCompId(java.lang.Long)
	 */
	@Override
	public void setCompId(Long compId) {
		this.compId = compId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IClubhouseLeagueMap#getClubhouseId()
	 */
	@Override
	public Long getClubhouseId() {
		return clubhouseId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IClubhouseLeagueMap#setClubhouseId(java.lang.Long)
	 */
	@Override
	public void setClubhouseId(Long clubhouseId) {
		this.clubhouseId = clubhouseId;
		
	}
}
