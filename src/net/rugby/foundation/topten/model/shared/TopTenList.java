package net.rugby.foundation.topten.model.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;

@Entity
public class TopTenList  implements Serializable, ITopTenList {

	

	public TopTenList() {
		
		
	}
	public TopTenList(Long id, List<ITopTenItem> list, List<Long> itemIds,
			Date created, Date published, Date expiration, int roundOrdinal,
			Boolean live, Boolean series, String summary, Long editorId,
			String pipeLineId, String title, String context, String content,
			Long nextId, Long nextPublishedId, Long prevId,
			Long prevPublishedId, Long compId, Long queryId,
			String twitterDescription, String guid, String featureGuid,
			Long sponsorId) {
		super();
		this.id = id;
		this.list = list;
		this.itemIds = itemIds;
		this.created = created;
		this.published = published;
		this.expiration = expiration;
		this.roundOrdinal = roundOrdinal;
		this.live = live;
		this.series = series;
		this.summary = summary;
		this.editorId = editorId;
		this.pipeLineId = pipeLineId;
		this.title = title;
		this.context = context;
		this.content = content;
		this.nextId = nextId;
		this.nextPublishedId = nextPublishedId;
		this.prevId = prevId;
		this.prevPublishedId = prevPublishedId;
		this.compId = compId;
		this.queryId = queryId;
		this.twitterDescription = twitterDescription;
		this.guid = guid;
		this.featureGuid = featureGuid;
		this.sponsorId = sponsorId;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -2463963348532297138L;

	protected @Id Long id;
	@Transient protected List<ITopTenItem> list;
	@Unindexed
	protected List<Long> itemIds;
	protected Date created;
	protected Date published;
	protected Date expiration;
	protected int roundOrdinal;
	protected Boolean live;
	protected Boolean series;
	@Unindexed
	protected String summary;
	protected Long editorId;
	protected String pipeLineId;
	@Unindexed
	protected String title;
	@Unindexed
	protected String context;
	
	@Unindexed
	protected String content;
	protected Long nextId;
	protected Long nextPublishedId;
	protected Long prevId;
	protected Long prevPublishedId;
	protected Long compId;
	protected Long queryId;
	@Unindexed
	protected String twitterDescription;
	protected String guid;
	protected String featureGuid;
	
	protected Long sponsorId;
	
	// because these can be greater than 500 chars we keep them in a separate table
//	protected Long notesId;
//	@Transient
//	protected String notes;
	
	public class TopTenListSummary implements ITopTenListSummary {
		protected Long id;
		protected String title;

		
		protected TopTenListSummary(Long id, String title) {
			this.id = id;
			this.title = title;
		}
		
		@Override
		public Long getId() {
			return id;
		}
		@Override
		public void setId(Long id) {
			this.id = id;			
		}
		@Override
		public String getTitle() {
			return title;
		}
		@Override
		public void setTitle(String title) {
			this.title = title;
		}
		
	}


	@Override
	public Long getCompId() {
		return compId;
	}
	@Override
	public void setCompId(Long compId) {
		this.compId = compId;
	}

	@Override
	public List<ITopTenItem> getList() {
		return list;
	}

	@Override
	public void setList(List<ITopTenItem> list) {
		this.list = list;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Date getCreated() {
		return created;
	}

	@Override
	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public Date getPublished() {
		return published;
	}

	@Override
	public void setPublished(Date published) {
		this.published = published;
	}

	@Override
	public Date getExpiration() {
		return expiration;
	}

	@Override
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
	@Override
	public int getRoundOrdinal() {
		return roundOrdinal;
	}
	@Override
	public void setRoundOrdinal(int roundOrdinal) {
		this.roundOrdinal = roundOrdinal;
	}
	@Override
	public Boolean getLive() {
		return live;
	}

	@Override
	public void setLive(Boolean live) {
		this.live = live;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public Long getEditorId() {
		return editorId;
	}

	@Override
	public void setEditorId(Long editorId) {
		this.editorId = editorId;
	}

	@Override
	public String getPipeLineId() {
		return pipeLineId;
	}

	@Override
	public void setPipeLineId(String pipeLineId) {
		this.pipeLineId = pipeLineId;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Override
	public String getContext() {
		return context;
	}
	@Override
	public void setContext(String context) {
		this.context = context;
	}
	
	@Override
	public ITopTenListSummary getSummary() {
		return new TopTenListSummary(id,title);
	}
	@Override
	public Long getNextId() {
		return nextId;
	}
	@Override
	public void setNextId(Long nextId) {
		this.nextId = nextId;
	}
	@Override
	public Long getNextPublishedId() {
		return nextPublishedId;
	}
	@Override
	public void setNextPublishedId(Long nextPublishedId) {
		this.nextPublishedId = nextPublishedId;
	}
	@Override
	public Long getPrevId() {
		return prevId;
	}
	@Override
	public void setPrevId(Long prevId) {
		this.prevId = prevId;
	}
	@Override
	public Long getPrevPublishedId() {
		return prevPublishedId;
	}
	@Override
	public void setPrevPublishedId(Long prevPublishedId) {
		this.prevPublishedId = prevPublishedId;
	}
	@Override
	public void setSummary(String summary) {
		this.summary = summary;
	}
	@Override
	public List<Long> getItemIds() {
		return itemIds;
	}
	@Override
	public void setItemIds(List<Long> itemIds) {
		this.itemIds = itemIds;
	}
	@Override
	public Long getQueryId() {
		return queryId;
	}
	@Override
	public void setQueryId(Long id) {
		this.queryId = id;
	}
	@Override
	public Boolean getSeries() {
		return series;
	}
	@Override
	public void setSeries(Boolean series) {
		this.series = series;
	}
	@Override
	public String getTwitterDescription() {
		return twitterDescription;
	}
	@Override
	public void setTwitterDescription(String twitterDescription) {
		this.twitterDescription = twitterDescription;
	}
	@Override
	public String getGuid() {
		return guid;
	}
	@Override
	public void setGuid(String guid) {
		this.guid = guid;
	}
	@Override
	public String getFeatureGuid() {
		return featureGuid;
	}
	@Override
	public void setFeatureGuid(String featureGuid) {
		this.featureGuid = featureGuid;
	}
	@Override
	public Long getSponsorId() {
		return sponsorId;
	}
	@Override
	public void setSponsorId(Long sponsorId) {
		this.sponsorId = sponsorId;
	}

}
