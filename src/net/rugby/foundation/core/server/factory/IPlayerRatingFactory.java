/**
 * 
 */
package net.rugby.foundation.core.server.factory;

import java.util.List;

import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IPlayerRating;


/**
 * @author home
 *
 */
public interface IPlayerRatingFactory extends ICachingFactory<IPlayerRating> {
	List<IPlayerRating> query(IRatingQuery query);
	boolean deleteForQuery(IRatingQuery query);
}
