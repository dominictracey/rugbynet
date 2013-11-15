package net.rugby.foundation.admin.server.factory;

import net.rugby.foundation.admin.server.model.IStandingsFetcher;
import net.rugby.foundation.model.shared.IRound;

public interface IStandingsFetcherFactory {
	IStandingsFetcher getFetcher(IRound r);
}
