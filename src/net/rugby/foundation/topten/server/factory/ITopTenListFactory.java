package net.rugby.foundation.topten.server.factory;

import java.util.List;

import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.ITopTenList.ITopTenListSummary;

public interface ITopTenListFactory {
	public ITopTenList get(Long id);
	public List<ITopTenListSummary> getSummariesForComp(Long compId);
	public ITopTenItem put(ITopTenItem item);
	public ITopTenList put(ITopTenList list);
	public ITopTenList delete(ITopTenList list);
	public ITopTenList publish(ITopTenList list);
	public ITopTenItem submit(ITopTenItem item);
	public ITopTenList getLatestForComp(Long compId);
}
