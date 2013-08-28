package net.rugby.foundation.topten.model.shared;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Transient;

import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.Position;

import com.google.gwt.http.client.URL;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class TopTenItem implements Serializable, ITopTenItem 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2772522698226722738L;

	@Id  protected Long id;
	protected Long playerId;
	@Transient protected IPlayer player;
	protected String text;
	protected String imageUrl;
	protected Long contributorId;
	protected Long editorId;
	protected boolean isSubmitted;
	protected String matchReportLink;
	protected String teamName;
	//@Transient protected ITopTenList parent;
	protected Long parentId;
	protected Long teamId;
	protected Position.position position;
	
	public TopTenItem() {
		
	}
	
	public TopTenItem(Long id, Long playerId, IPlayer player, String text,
			String image, Long contributorId, Long editorId, boolean isSubmitted, 
			String matchReportLink, String teamName, Long teamId, Position.position position, ITopTenList list) {
		super();
		this.id = id;
		this.playerId = playerId;
		this.player = player;
		this.text = text;
		this.imageUrl = image;
		this.contributorId = contributorId;
		this.editorId = editorId;
		this.isSubmitted = isSubmitted;
		this.matchReportLink = matchReportLink;
		this.teamName = teamName;
		this.teamId = teamId;
		this.parentId = list.getId();
		//this.parent = list;
		this.parentId = list.getId();
		this.position = position;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITopTenItem#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITopTenItem#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITopTenItem#getPlayerId()
	 */
	@Override
	public Long getPlayerId() {
		return playerId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITopTenItem#setPlayerId(java.lang.Long)
	 */
	@Override
	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITopTenItem#getPlayer()
	 */
	@Override
	public IPlayer getPlayer() {
		return player;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITopTenItem#setPlayer(net.rugby.foundation.model.shared.IPlayer)
	 */
	@Override
	public void setPlayer(IPlayer player) {
		this.player = player;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITopTenItem#getText()
	 */
	@Override
	public String getText() {
		return text;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITopTenItem#setText(java.lang.String)
	 */
	@Override
	public void setText(String text) {
		this.text = text;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITopTenItem#getImage()
	 */
	@Override
	public String getImage() {
		return imageUrl;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITopTenItem#setImage(com.google.gwt.http.client.URL)
	 */
	@Override
	public void setImage(String image) {
		this.imageUrl = image;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITopTenItem#getContributorId()
	 */
	@Override
	public Long getContributorId() {
		return contributorId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITopTenItem#setContributorId(java.lang.Long)
	 */
	@Override
	public void setContributorId(Long contributorId) {
		this.contributorId = contributorId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITopTenItem#getEditorId()
	 */
	@Override
	public Long getEditorId() {
		return editorId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITopTenItem#setEditorId(java.lang.Long)
	 */
	@Override
	public void setEditorId(Long editorId) {
		this.editorId = editorId;
	}

	@Override
	public boolean isSubmitted() {
		return isSubmitted;
	}

	@Override
	public void setSubmitted(boolean set) {
		this.isSubmitted = set;
	}

	@Override
	public String getImageUrl() {
		return imageUrl;
	}
	@Override
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	@Override
	public String getMatchReportLink() {
		return matchReportLink;
	}
	@Override
	public void setMatchReportLink(String matchReportLink) {
		this.matchReportLink = matchReportLink;
	}
	@Override
	public String getTeamName() {
		return teamName;
	}
	@Override
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
//	@Override
//	public ITopTenList getParent() {
//		return parent;
//	}
//	@Override
//	public void setParent(ITopTenList parent) {
//		this.parent = parent;
//	}
	@Override
	public Long getParentId() {
		return parentId;
	}
	@Override
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	@Override
	public Long getTeamId() {
		return teamId;
	}
	@Override
	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}
	@Override
	public Position.position getPosition() {
		return position;
	}
	@Override
	public void setPosition(Position.position position) {
		this.position = position;
	}

}

