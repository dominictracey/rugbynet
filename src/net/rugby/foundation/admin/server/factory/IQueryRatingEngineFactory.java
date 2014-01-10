package net.rugby.foundation.admin.server.factory;

import net.rugby.foundation.admin.server.model.IQueryRatingEngine;
import net.rugby.foundation.admin.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.IRatingQuery;

public interface IQueryRatingEngineFactory {
	 IQueryRatingEngine get(IRatingEngineSchema schema);

	IQueryRatingEngine get(IRatingEngineSchema schema, IRatingQuery rq);
}
