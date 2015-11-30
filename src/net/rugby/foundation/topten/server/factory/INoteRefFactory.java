package net.rugby.foundation.topten.server.factory;

import java.util.List;

import net.rugby.foundation.core.server.factory.ICachingFactory;
import net.rugby.foundation.topten.model.shared.INote;
import net.rugby.foundation.topten.model.shared.INoteRef;
import net.rugby.foundation.topten.model.shared.ITopTenList;

public interface INoteRefFactory extends ICachingFactory<INoteRef> {

	List<INoteRef> getForNote(INote t);
	List<INoteRef> getForList(ITopTenList ttl);
	
}
