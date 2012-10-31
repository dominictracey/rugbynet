/**
 * 
 */
package net.rugby.foundation.game1.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;

/**
 * @author home
 *
 */
@Entity
public class LeaderboardRow implements ILeaderboardRow, Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	private Long id;
	@Unindexed
	private Long appUserId;
	@Unindexed
	private List<Integer> scores;
	private Integer rank;
	private Integer total;
	@Unindexed
	private Integer tieBreakerFactor;

	@Unindexed
	private String entryName;

	private Long entryId;
	@Unindexed
	private String appUserName;

	
	public LeaderboardRow() {
		scores = new ArrayList<Integer>();
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboardRow#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboardRow#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboardRow#getAppUserId()
	 */
	@Override
	public Long getAppUserId() {
		return appUserId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboardRow#setAppUserId(java.lang.Long)
	 */
	@Override
	public void setAppUserId(Long appUserId) {
		this.appUserId = appUserId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboardRow#getScore()
	 */
	@Override
	public List<Integer> getScores() {
		return scores;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboardRow#setScore(int)
	 */
	@Override
	public void setScores(List<Integer> scores) {
		this.scores = scores;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboardRow#getRank()
	 */
	@Override
	public Integer getRank() {
		return rank;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboardRow#setRank(int)
	 */
	@Override
	public void setRank(Integer rank) {
		this.rank = rank;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboardRow#getTotal()
	 */
	@Override
	public Integer getTotal() {
		return total;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboardRow#setTotal(int)
	 */
	@Override
	public void setTotal(Integer total) {
		this.total = total;	
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ILeaderboardRow o) {
		int byScores = getTotal().compareTo(o.getTotal());
		return (byScores != 0 ? byScores :
			getTieBreakerFactor().compareTo(o.getTieBreakerFactor()));

	}

	public Integer getTieBreakerFactor() {
		return tieBreakerFactor;
	}

	public void setTieBreakerFactor(Integer tieBreakerFactor) {
		this.tieBreakerFactor = tieBreakerFactor;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboardRow#getAppUserName()
	 */
	@Override
	public String getAppUserName() {
		return appUserName;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboardRow#setAppUserName(java.lang.String)
	 */
	@Override
	public void setAppUserName(String appUserName) {
		this.appUserName = appUserName;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboardRow#getEntryId()
	 */
	@Override
	public Long getEntryId() {
		return entryId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboardRow#setEntryId(java.lang.Long)
	 */
	@Override
	public void setEntryId(Long entryId) {
		this.entryId = entryId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboardRow#getEntryName()
	 */
	@Override
	public String getEntryName() {
		return entryName;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboardRow#setEntryName(java.lang.String)
	 */
	@Override
	public void setEntryName(String entryName) {
		this.entryName = entryName;
	}

}
