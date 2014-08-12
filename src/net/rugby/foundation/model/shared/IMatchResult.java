/**
 * 
 */
package net.rugby.foundation.model.shared;

import java.util.Date;

import net.rugby.foundation.model.shared.IMatchGroup.Status;

/**
 * @author home
 *
 */
public interface IMatchResult extends IHasId {

	enum ResultType {SIMPLE_SCORE, MATCHES}

	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract Long getMatchID();

	public abstract void setMatchID(Long matchID);

	public abstract Date getRecordedDate();

	public abstract void setRecordedDate(Date recordedDate);

	public abstract String getSource();

	public abstract void setSource(String source);

	public abstract IMatchResult.ResultType getType();

	public abstract void setType(IMatchResult.ResultType type);

	public abstract Status getStatus();

	public abstract void setStatus(Status status);

	void setMatch(IMatchGroup match);

	IMatchGroup getMatch();

}