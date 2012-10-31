/**
 * 
 */
package net.rugby.foundation.core.server.factory;

import net.rugby.foundation.model.shared.IMatchResult;

/**
 * @author home
 *
 */
public interface IMatchResultFactory {
	void setId(Long id);
	
	IMatchResult get();

	IMatchResult put(IMatchResult g);
}
