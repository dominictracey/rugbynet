package net.rugby.foundation.topten.client;

import java.util.List;

import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.ITopTenList.ITopTenListSummary;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
@RemoteServiceRelativePath("TopTenService")
public interface TopTenListService extends RemoteService {
	public ITopTenList getTopTenList(Long id);
	public List<ITopTenListSummary> getTopTenListSummariesForComp(Long compId);
	public ITopTenItem saveTopTenItem(ITopTenItem item);
	public ITopTenList deleteTopTenList(ITopTenList list);
	public ITopTenList publishTopTenList(ITopTenList list);
	public ITopTenItem submitTopTenItem(ITopTenItem item);
	public ITopTenList getLatestForComp(Long compId);
	public Long getLatestListIdForComp(Long compId);
	public List<IContent> getContentItems();
	public ITopTenList saveTopTenList(ITopTenList list);
}
