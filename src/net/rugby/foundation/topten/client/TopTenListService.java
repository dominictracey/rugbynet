package net.rugby.foundation.topten.client;

import java.util.List;
import java.util.Map;

import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.IServerPlace;
import net.rugby.foundation.model.shared.RatingMode;
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
	public IPlayerRating getPlayerRating(Long playerRatingId);
	public IRatingSeries getRatingSeries(Long seriesId);
	public IRatingSeries getRatingSeries(Long compId, RatingMode mode);
	public ITopTenList getTopTenListForRatingQuery(Long id);
	public IRatingGroup getRatingGroup(Long ratingGroupId);
	public IRatingMatrix getRatingMatrix(Long ratingMatrixId);
	public List<IRatingQuery> getRatingQueriesForMatrix(Long id);
	public List<RatingMode> getAvailableSeries(Long compId);
	public IServerPlace getPlace(String guid);
}
