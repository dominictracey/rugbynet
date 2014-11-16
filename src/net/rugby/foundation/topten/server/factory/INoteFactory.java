package net.rugby.foundation.topten.server.factory;

import java.util.List;

import net.rugby.foundation.core.server.factory.ICachingFactory;
import net.rugby.foundation.topten.model.shared.INote;
import net.rugby.foundation.topten.model.shared.ITopTenList;

public interface INoteFactory extends ICachingFactory<INote> {

	List<INote> getByUROrdinal(int urOrd);
	List<INote> getForList(ITopTenList list);
	boolean deleteForList(ITopTenList list);
	void dropMemcacheForUniversalRound(int uro);
	void dropMemcacheForList(Long listId);
}
