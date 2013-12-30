package net.rugby.foundation.model.shared;

import java.util.List;

import net.rugby.foundation.model.shared.IRatingQuery.Status;
import net.rugby.foundation.model.shared.Position.position;

public interface IRatingQuery extends IHasId {

	public abstract List<Long> getCompIds();

	public abstract List<Long> getRoundIds();

	public abstract List<Long> getTeamIds();

	public abstract List<Long> getCountryIds();

	public abstract List<position> getPositions();

	void setCompIds(List<Long> compIds);

	void setRoundIds(List<Long> roundIds);

	void setTeamIds(List<Long> teamIds);

	void setCountryIds(List<Long> countryIds);

	void setPositions(List<position> positions);

	public enum Status { NEW, RUNNING, COMPLETE, ERROR }

	Status getStatus();

	void setStatus(Status status);;
}