package net.rugby.foundation.topten.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.ITopTenList.ITopTenListSummary;


public interface TopTenListServiceAsync {
	public void getTopTenList(Long id, AsyncCallback<ITopTenList> callback);
	public void getTopTenListSummariesForComp(Long compId, AsyncCallback<List<ITopTenListSummary>> callback);
	public void saveTopTenItem(ITopTenItem item,
			AsyncCallback<ITopTenItem> asyncCallback);
	public void deleteTopTenList(ITopTenList list,
			AsyncCallback<ITopTenList> asyncCallback);
	public void publishTopTenList(ITopTenList list,
			AsyncCallback<ITopTenList> asyncCallback);
	public void submitTopTenItem(ITopTenItem item,
			AsyncCallback<ITopTenItem> asyncCallback);
	public void getLatestForComp(Long compId, AsyncCallback<ITopTenList> asyncCallback);
	public void getLatestListIdForComp(Long compId,
			AsyncCallback<Long> asyncCallback);
	public void getContentItems(AsyncCallback<List<IContent>> asyncCallback);
	public void saveTopTenList(ITopTenList list,
			AsyncCallback<ITopTenList> asyncCallback);

}
