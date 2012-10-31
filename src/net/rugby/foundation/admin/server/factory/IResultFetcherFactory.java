package net.rugby.foundation.admin.server.factory;

import net.rugby.foundation.admin.server.model.IResultFetcher;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.IRound;

public interface IResultFetcherFactory {
	public IResultFetcher getResultFetcher(Long sourceCompID, IRound round, IMatchResult.ResultType resultType);
}
