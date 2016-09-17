package net.rugby.foundation.admin.server.factory.espnscrum;

import net.rugby.foundation.admin.server.model.ITeamMatchStatsFetcher;
import net.rugby.foundation.model.shared.ICompetition.CompetitionType;

public interface ITeamMatchStatsFetcherFactory {

	ITeamMatchStatsFetcher get(CompetitionType fetcherType);

}