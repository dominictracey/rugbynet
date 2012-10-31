/**
 * 
 */
package net.rugby.foundation.game1.server.factory;

import net.rugby.foundation.game1.shared.ILeaderboardRow;

/**
 * @author home
 *
 */
public interface ILeaderboardRowFactory {
	void setId(Long id);
	ILeaderboardRow get();
	ILeaderboardRow put(ILeaderboardRow lbr);
	void delete();
	ILeaderboardRow cloneFrom();
	ILeaderboardRow getNew();

}
