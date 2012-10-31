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

import net.rugby.foundation.model.shared.IMatchGroup;

import com.googlecode.objectify.annotation.Entity;

/**
 * @author home
 *
 */
@Entity
public class RoundEntry implements Serializable, IRoundEntry {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	
	private Long roundId;
	//private String roundAbbr;
	@Transient
	private Map<Long, IMatchEntry> pickMap = null; 
	private List<Long> pickIdList = null; 
	private Integer tieBreakerVisitScore;
	private Integer tieBreakerHomeScore;
	private Long tieBreakerMatchId;
	@Transient
	private IMatchGroup tieBreakerMatch;
	
	public RoundEntry(Long id, Long roundId, Map<Long, IMatchEntry> pickMap,
			Integer tieBreakerVisitScore, Integer tieBreakerHomeScore) {
		super();
		this.id = id;
		this.roundId = roundId;
		this.pickMap = pickMap;
		this.tieBreakerVisitScore = tieBreakerVisitScore;
		this.tieBreakerHomeScore = tieBreakerHomeScore;
	}

	/**
	 * 
	 */
	public RoundEntry() {
		this.pickMap = new HashMap<Long, IMatchEntry>();
		this.pickIdList = new ArrayList<Long>();
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IRoundEntry#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IRoundEntry#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IRoundEntry#getRoundId()
	 */
	@Override
	public Long getRoundId() {
		return roundId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IRoundEntry#setRoundId(java.lang.Long)
	 */
	@Override
	public void setRoundId(Long roundId) {
		this.roundId = roundId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IRoundEntry#getPickMap()
	 */
	@Override
	public Map<Long,IMatchEntry> getMatchPickMap() {
		return pickMap;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IRoundEntry#setPickMap(java.util.Map)
	 */
	@Override
	public void setMatchPickMap(Map<Long,IMatchEntry> pickMap) {
		this.pickMap = pickMap;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IRoundEntry#getTieBreakerWScore()
	 */
	@Override
	public Integer getTieBreakerHomeScore() {
		return tieBreakerHomeScore;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IRoundEntry#setTieBreakerWScore(java.lang.Integer)
	 */
	@Override
	public void setTieBreakerHomeScore(Integer tieBreakerHomeScore) {
		this.tieBreakerHomeScore = tieBreakerHomeScore;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IRoundEntry#getTieBreakerLScore()
	 */
	@Override
	public Integer getTieBreakerVisitScore() {
		return tieBreakerVisitScore;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IRoundEntry#setTieBreakerLScore(java.lang.Integer)
	 */
	@Override
	public void setTieBreakerVisitScore(Integer tieBreakerVisitScore) {
		this.tieBreakerVisitScore = tieBreakerVisitScore;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IRoundEntry#getMatchPickIdList()
	 */
	@Override
	public List<Long> getMatchPickIdList() {
		return pickIdList;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IRoundEntry#setMatchPickIdList(java.util.List)
	 */
	@Override
	public void setMatchPickIdList(List<Long> pickList) {
		this.pickIdList = pickList;
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IRoundEntry#getTieBreakerMatchId()
	 */
	@Override
	public Long getTieBreakerMatchId() {
		return tieBreakerMatchId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IRoundEntry#setTieBreakerMatchId()
	 */
	@Override
	public void setTieBreakerMatchId(Long tieBreakerMatchId) {
		this.tieBreakerMatchId = tieBreakerMatchId;
		
	}

	@Override
	public IMatchGroup getTieBreakerMatch() {
		return tieBreakerMatch;
	}

	@Override
	public void setTieBreakerMatch(IMatchGroup tieBreakerMatch) {
		this.tieBreakerMatch = tieBreakerMatch;
	}



}
