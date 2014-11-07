package net.rugby.foundation.topten.client;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.IServerPlace;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.topten.model.shared.INote;
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
	public void getPlayerRating(Long playerRatingId,
			AsyncCallback<IPlayerRating> asyncCallback);
	public void getRatingSeries(Long seriesId,
			AsyncCallback<IRatingSeries> asyncCallback);
	public void getRatingSeries(Long compId, RatingMode mode,
			AsyncCallback<IRatingSeries> asyncCallback);
	public void getTopTenListForRatingQuery(Long id,
			AsyncCallback<ITopTenList> asyncCallback);
	public void  getRatingGroup(Long ratingGroupId, AsyncCallback<IRatingGroup> asyncCallback);
	public void  getRatingMatrix(Long ratingMatrixId, AsyncCallback<IRatingMatrix> asyncCallback);
	public void getRatingQueriesForMatrix(Long id,
			AsyncCallback<List<IRatingQuery>> asyncCallback);
	public void getAvailableSeries(Long compId, AsyncCallback<Map<RatingMode, Long>> asyncCallback);
	public void getPlace(String guid, AsyncCallback<IServerPlace> asyncCallback);
	public void getDefaultRatingSeries(Long compId,
			AsyncCallback<IRatingSeries> asyncCallback);
	public void getNotesForList(Long id,
			AsyncCallback<List<INote>> asyncCallback);
	public void getPlayerNames(List<Long> needPlayerNames,
			AsyncCallback<Map<Long, String>> asyncCallback);
	public void getTTLNames(List<Long> needTTLNames,
			AsyncCallback<Map<Long, String>> asyncCallback);
}
