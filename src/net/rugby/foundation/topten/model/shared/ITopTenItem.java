package net.rugby.foundation.topten.model.shared;

import net.rugby.foundation.model.shared.IPlayer;

import com.google.gwt.http.client.URL;

public interface ITopTenItem {

	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract Long getPlayerId();

	public abstract void setPlayerId(Long playerId);

	public abstract IPlayer getPlayer();

	public abstract void setPlayer(IPlayer player);

	public abstract String getText();

	public abstract void setText(String text);

	public abstract String getImage();

	public abstract void setImage(String image);

	public abstract Long getContributorId();

	public abstract void setContributorId(Long contributorId);

	public abstract Long getEditorId();

	public abstract void setEditorId(Long editorId);

	public abstract boolean isSubmitted();
	
	public abstract void setSubmitted(boolean set);

	public abstract String getImageUrl();

	public abstract void setImageUrl(String imageUrl);

	public abstract String getMatchReportLink();

	public abstract void setMatchReportLink(String matchReportLink);

	String getTeamName();

	void setTeamName(String teamName);

}
