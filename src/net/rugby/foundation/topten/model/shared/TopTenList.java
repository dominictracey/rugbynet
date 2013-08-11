package net.rugby.foundation.topten.model.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Transient;

import net.rugby.foundation.model.shared.IPlayer;

import com.google.gwt.http.client.URL;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class TopTenList  implements Serializable, ITopTenList {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -2463963348532297138L;

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
	protected @Id Long id;
	protected List<ITopTenItem> list;
	protected Date created;
	protected Date published;
	protected Date expiration;
	protected Boolean live;
	protected String summary;
	protected Long editorId;
	protected String pipeLineId;
	protected String title;
	protected String content;
	protected Long nextId;
	protected Long nextPublishedId;
	protected Long prevId;
	protected Long prevPublishedId;
	protected Long compId;

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

}
