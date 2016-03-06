package net.rugby.foundation.admin.shared;

import java.util.Date;

import net.rugby.foundation.model.shared.IHasId;
import net.rugby.foundation.model.shared.IServerPlace;

import org.joda.time.DateTime;

public interface IBlurb extends IHasId{

	public abstract Date getCreated();

	public abstract void setCreated(Date date);

	public abstract Long getServerPlaceId();

	public abstract void setServerPlaceId(Long serverPlaceId);

	public abstract IServerPlace getServerPlace();

	public abstract void setServerPlace(IServerPlace serverPlace);

	public abstract String getBodyText();

	public abstract void setBodyText(String bodyText);

	public abstract String getLinkText();

	public abstract void setLinkText(String linkText);

	public abstract Long getAuthorId();

	public abstract void setAuthorId(Long authorId);

	Boolean getActive();

	void setActive(Boolean active);

}