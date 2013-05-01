package net.rugby.foundation.admin.server.factory;

import net.rugby.foundation.admin.server.model.IPlayerMatchStatsFetcher;
import net.rugby.foundation.admin.server.workflow.matchrating.GenerateMatchRatings.Home_or_Visitor;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;

public interface IPlayerMatchStatsFetcherFactory {
	public IPlayerMatchStatsFetcher getResultFetcher(IPlayer player, IMatchGroup match, Home_or_Visitor side, Integer slot, String name);
}
