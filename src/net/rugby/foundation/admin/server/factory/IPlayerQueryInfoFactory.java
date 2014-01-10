package net.rugby.foundation.admin.server.factory;

import java.util.List;

import net.rugby.foundation.model.shared.IPlayerQueryInfo;
import net.rugby.foundation.model.shared.IRatingQuery;

public interface IPlayerQueryInfoFactory {
	public abstract IPlayerQueryInfo get(Long id);
	public abstract List<IPlayerQueryInfo> query(IRatingQuery query, Long schemaId);
}
