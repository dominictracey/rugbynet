package net.rugby.foundation.topten.model.shared;

import java.util.Date;
import java.util.List;

public interface ITopTenList {


	
	public interface ITopTenListSummary {
		public abstract Long getId();
		public abstract void setId(Long id);
		public abstract String getTitle();
		public abstract void setTitle(String title);
	}

	public abstract List<ITopTenItem> getList();
	public abstract void setList(List<ITopTenItem> list);
	Long getId();
	void setId(Long id);
	Long getQueryId();
	void setQueryId(Long id);
	String getTitle();
	void setTitle(String id);
	Date getCreated();
	void setCreated(Date created);
	Date getPublished();
	void setPublished(Date published);
	Date getExpiration();
	void setExpiration(Date expiration);
	Boolean getLive();
	void setLive(Boolean live);
	Long getEditorId();
	void setEditorId(Long editorId);
	String getPipeLineId();
	void setPipeLineId(String pipeLineId);
	String getContent();
	void setContent(String content);
	ITopTenListSummary getSummary();
	void setSummary(String summary);
	void setPrevPublishedId(Long prevPublishedId);
	Long getPrevPublishedId();
	void setPrevId(Long prevId);
	Long getPrevId();
	void setNextPublishedId(Long nextPublishedId);
	Long getNextPublishedId();
	void setNextId(Long nextId);
	Long getNextId();
	Long getCompId();
	void setCompId(Long compId);
	void setItemIds(List<Long> itemIds);
	List<Long> getItemIds();
	void setSeries(Boolean series);
	Boolean getSeries();
	void setTwitterDescription(String twitterDescription);
	String getTwitterDescription();
	public abstract void setGuid(String guid);
	String getGuid();
//	void setNotes(String notes);
//	String getNotes();
//	void setNotesId(Long notesId);
//	Long getNotesId();
	/**
	 * 
	 * @return ordinal of Universal Round the list was created for
	 */
	int getRoundOrdinal();
	/**
	 * 
	 * @param roundOrdinal - ordinal of Universal Round the list was created for
	 */
	void setRoundOrdinal(int roundOrdinal);
	/**
	 * 
	 * @return for a match, will be "Gloucester vs. Newcastle"; for a round will be "Round 2"
	 */
	String getContext();
	/**
	 * 
	 * @param context - for a match, should be "Gloucester vs. Newcastle"; for a round should be "Round 2"
	 */
	void setContext(String context);
	String getFeatureGuid();
	void setFeatureGuid(String featureGuid);
	public abstract Long getSponsorId();
	public abstract void setSponsorId(Long id);

}
