package net.rugby.foundation.core.server.factory;

import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.IServerPlace;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;

public interface IPlaceFactory extends ICachingFactory<IServerPlace>{
	public abstract IServerPlace getForGuid(String guid);
	public abstract IServerPlace getForName(String name);
	public abstract void buildItem(IServerPlace p, ITopTenItem item);
	public abstract void buildList(IServerPlace p, ITopTenList ttl);
	public abstract void buildQuery(IServerPlace p, IRatingQuery rq);
	public abstract void buildMatrix(IServerPlace p, IRatingMatrix rm);
	public abstract void buildGroup(IServerPlace p, IRatingGroup rg);
	public abstract void buildSeries(IServerPlace p, IRatingSeries rs);
}
