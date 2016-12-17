package net.rugby.foundation.model.shared;

public interface IStanding extends IHasId {

	public abstract Long getRoundId();

	public abstract void setRoundId(Long roundId);

	public abstract Long getTeamId();

	public abstract void setTeamId(Long teamId);

	public abstract Integer getStanding();

	public abstract void setStanding(Integer standing);

	IRound getRound();

	void setRound(IRound round);

	ITeamGroup getTeam();

	void setTeam(ITeamGroup team);

	Long getForeignId();

	void setForeignId(Long foreignId);

}