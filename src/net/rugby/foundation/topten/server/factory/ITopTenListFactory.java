package net.rugby.foundation.topten.server.factory;

import java.util.List;

import net.rugby.foundation.admin.shared.TopTenSeedData;
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
	/**
	 * 
	 * @param compId
	 * @return The last TopTenList published for this comp
	 */
	public ITopTenList getLatestForComp(Long compId);
	/**
	 * 
	 * @param compId
	 * @return The last TopTenList created for this comp (published or not)
	 */
	public ITopTenList getLastCreatedForComp(Long compId);
	public ITopTenList create(TopTenSeedData tti);
	
	void setLastCreatedForComp(ITopTenList ttl, Long compId);
	void setLatestPublishedForComp(ITopTenList ttl, Long compId);
	
	/**
	 * @param compId of set of lists to check over
	 * @return returns true if no errors are logged. Check log if false returned.
	 */
	public boolean scan(Long compId);
	ITopTenList create(TopTenSeedData tti, ITopTenList lastTTL);
}
