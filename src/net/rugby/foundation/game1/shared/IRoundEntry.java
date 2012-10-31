/**
 * 
 */
package net.rugby.foundation.game1.shared;

import java.util.List;
import java.util.Map;

import net.rugby.foundation.model.shared.IMatchGroup;

/**
 * @author home
 *
 */
public interface IRoundEntry {

	public abstract Long getId();
	
	public abstract void setId(Long id);

	public abstract Long getRoundId();

	public abstract void setRoundId(Long roundId);

	/**
	 * 
	 * @return Key: matchId, Value: matchEntry (key is not matchEntry Id!)
	 */
	public abstract Map<Long, IMatchEntry> getMatchPickMap();

	public abstract void setMatchPickMap(Map<Long, IMatchEntry> pickList);

	public abstract List<Long> getMatchPickIdList();

	public abstract void setMatchPickIdList(List<Long> pickList);


	public abstract Long getTieBreakerMatchId();
	/**
	 * @param tieBreakerMatchId
	 * 
	 * I hope this can't get called on the client
	 */
	public abstract void setTieBreakerMatchId(Long tieBreakerMatchId);

	
	public abstract Integer getTieBreakerHomeScore();
	public abstract void setTieBreakerHomeScore(Integer tieBreakerHomeScore);

	public abstract Integer getTieBreakerVisitScore();
	public abstract void setTieBreakerVisitScore(Integer tieBreakerVisitScore);

	/**
	 * @return
	 */
	IMatchGroup getTieBreakerMatch();

	/**
	 * @param tieBreakerMatch
	 */
	void setTieBreakerMatch(IMatchGroup tieBreakerMatch);


	

}