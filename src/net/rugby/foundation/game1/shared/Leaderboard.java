/**
 * 
 */
package net.rugby.foundation.game1.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;

import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IRound;

/**
 * @author Dominic Tracey
 *
 */
@Entity
public class Leaderboard implements ILeaderboard, Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	private Long id;
	
	@Transient
	private ICompetition comp;
	private Long compId;
	
	@Transient
	private List<ILeaderboardRow> rows;
	private List<Long> rowIds;
	
	@Transient
	private IRound round;
	private Long roundId;
	private List<String> roundNames;
	
	@Transient
	private ILeague league;
	private Long leagueId;
	
	public Leaderboard() {
		rows = new ArrayList<ILeaderboardRow>();
		rowIds = new ArrayList<Long>();
		roundNames = new ArrayList<String>();
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboard#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboard#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboard#getCompId()
	 */
	@Override
	public Long getCompId() {
		return compId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboard#setCompId(java.lang.Long)
	 */
	@Override
	public void setCompId(Long compId) {
		this.compId = compId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboard#getComp()
	 */
	@Override
	public ICompetition getComp() {
		return comp;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboard#setComp(net.rugby.foundation.model.shared.ICompetition)
	 */
	@Override
	public void setComp(ICompetition comp) {
		this.comp = comp;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboard#getRows()
	 */
	@Override
	public List<ILeaderboardRow> getRows() {
		return rows;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboard#setRows(java.util.List)
	 */
	@Override
	public void setRows(List<ILeaderboardRow> rows) {
		this.rows = rows;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboard#getRowIds()
	 */
	@Override
	public List<Long> getRowIds() {
		return rowIds;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboard#setRowIds(java.util.List)
	 */
	@Override
	public void setRowIds(List<Long> rowIds) {
		this.rowIds = rowIds;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboard#getRoundId()
	 */
	@Override
	public Long getRoundId() {
		return roundId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboard#setRoundId(java.lang.Long)
	 */
	@Override
	public void setRoundId(Long roundId) {
		this.roundId = roundId;		
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboard#getRound()
	 */
	@Override
	public IRound getRound() {
		return round;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboard#setRound(net.rugby.foundation.model.shared.IRound)
	 */
	@Override
	public void setRound(IRound round) {
		this.round = round;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeaderboard#sanityCheck()
	 */
	@Override
	public boolean sanityCheck() {
		// have the rows been inserted in proper order? 
		// each row should have a score less than or equal to the one before it
		int lastScore= 1000000;
		for (ILeaderboardRow r : getRows()) {
			if (r.getTotal() > lastScore) 
				return false;
			else
				lastScore = r.getTotal();
		}
		
		return true;
	}

	@Override
	public List<String> getRoundNames() {
		return roundNames;
	}

	@Override
	public void setRoundNames(List<String> roundNames) {
		this.roundNames = roundNames;
	}

	@Override
	public ILeague getLeague() {
		return league;
	}

	@Override
	public void setLeague(ILeague league) {
		this.league = league;
	}

	@Override
	public Long getLeagueId() {
		return leagueId;
	}

	@Override
	public void setLeagueId(Long leagueId) {
		this.leagueId = leagueId;
	}

}
