/**
 * 
 */
package net.rugby.foundation.game1.server.factory;

import java.util.List;
import net.rugby.foundation.game1.shared.IMatchStats;

/**
 * @author home
 *
 */
public interface IMatchStatsFactory {
	//void setId(Long id);
	void setMatchId(Long id);
	
	IMatchStats getMatchStatsShard();
	IMatchStats put(IMatchStats s);
	Boolean delete();

	/**
	 * 
	 */
	List<IMatchStats> getAll();
}
