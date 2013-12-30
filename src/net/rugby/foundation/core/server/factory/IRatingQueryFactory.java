/**
 * 
 */
package net.rugby.foundation.core.server.factory;

import java.util.List;

import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.Position.position;


/**
 * @author home
 *
 */
public interface IRatingQueryFactory extends ICachingFactory<IRatingQuery> {
	IRatingQuery query(List<Long> compId, List<Long> roundId, List<position> posi, List<Long> countryId, List<Long> teamId);
}
