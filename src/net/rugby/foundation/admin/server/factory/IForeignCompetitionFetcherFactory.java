package net.rugby.foundation.admin.server.factory;

import net.rugby.foundation.admin.server.model.IForeignCompetitionFetcher;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;

public interface IForeignCompetitionFetcherFactory {
	enum CompetitionFetcherType {ESPNSCRUM_BASIC, ESPNSCRUM_EXTENDED, ESPNSCRUM_INTERNATIONALS}
	public IForeignCompetitionFetcher getForeignCompetitionFetcher(String url, CompetitionFetcherType fetcherType);
	/**
	 * @param rf
	 * @param mf
	 */
	//void setFactories(IRoundFactory rf, IMatchGroupFactory mf);
}
