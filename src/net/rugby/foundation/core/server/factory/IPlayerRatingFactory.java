/**
 * 
 */
package net.rugby.foundation.core.server.factory;

import java.util.List;

import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IPlayerRating;


/**
 * @author home
 *
 */
public interface IPlayerRatingFactory extends ICachingFactory<IPlayerRating> {
	List<IPlayerRating> query(IRatingQuery query);
	boolean deleteForQuery(IRatingQuery query);
	boolean deleteForMatch(IMatchGroup m);
	/***
	 * Creates "wrapper" IPlayerRating objects for the IPlayerMatchStats for use in the PlayerListView
	 * @param matchId - match to find stats for
	 * @param schema - ignored
	 * @return list (one per player) of IPlayerRating where the rating and query are null
	 */
	List<IPlayerRating> getForMatch(Long matchId, IRatingEngineSchema schema);
	Boolean deleteForSchema(IRatingEngineSchema schema);
}
