package net.rugby.foundation.topten.server.factory;

import java.util.List;

import net.rugby.foundation.core.server.factory.ICachingFactory;
import net.rugby.foundation.topten.model.shared.INote;
import net.rugby.foundation.topten.model.shared.ITopTenList;

public interface INoteFactory extends ICachingFactory<INote> {

	List<INote> getByUROrdinal(int urOrd);
	/**
	 * 
	 * @param list
	 * @param includeAll - do you want the self-referential links as well (useful for bulk delete/update, not suitable for client display)
	 * @return
	 */
	List<INote> getForList(ITopTenList list, boolean includeAll);
	boolean deleteForList(ITopTenList list);
	void dropMemcacheForUniversalRound(int uro);
	void dropMemcacheForList(Long listId);
}
