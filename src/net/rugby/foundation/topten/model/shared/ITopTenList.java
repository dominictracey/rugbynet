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

}
