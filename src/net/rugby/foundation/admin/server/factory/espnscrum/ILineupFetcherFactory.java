package net.rugby.foundation.admin.server.factory.espnscrum;

import net.rugby.foundation.admin.server.model.ILineupFetcher;
import net.rugby.foundation.model.shared.ICompetition.CompetitionType;

public interface ILineupFetcherFactory {

	ILineupFetcher getLineupFetcher(CompetitionType fetcherType);

}
