package net.rugby.foundation.admin.server.factory;

import net.rugby.foundation.admin.server.model.IForeignCompetitionFetcher;
import net.rugby.foundation.model.shared.ICompetition.CompetitionType;

public interface IForeignCompetitionFetcherFactory {
	public IForeignCompetitionFetcher getForeignCompetitionFetcher(String url, CompetitionType fetcherType);
}
