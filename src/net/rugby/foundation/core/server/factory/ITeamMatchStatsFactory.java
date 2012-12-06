package net.rugby.foundation.core.server.factory;

import net.rugby.foundation.model.shared.ITeamMatchStats;

public interface ITeamMatchStatsFactory {

	public abstract ITeamMatchStats getById(Long id);

	public abstract ITeamMatchStats put(ITeamMatchStats val);

	public abstract Boolean delete(ITeamMatchStats val);

}