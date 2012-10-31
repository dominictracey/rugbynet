/**
 * 
 */
package net.rugby.foundation.game1.server.factory;

import java.util.Set;

import net.rugby.foundation.game1.shared.IRoundEntry;

/**
 * @author home
 *
 */
public interface IRoundEntryFactory {
	void setId(Long id);
	
	IRoundEntry getRoundEntry();
	IRoundEntry put(IRoundEntry e);
	Boolean delete();

	/**
	 * @return
	 */
	IRoundEntry getNew();

	/**
	 * @return
	 */
	Set<IRoundEntry> getAll();
}
