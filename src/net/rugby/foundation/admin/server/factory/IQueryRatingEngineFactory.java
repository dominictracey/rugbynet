package net.rugby.foundation.admin.server.factory;

import net.rugby.foundation.admin.server.model.IQueryRatingEngine;
import net.rugby.foundation.admin.shared.IRatingEngineSchema;

public interface IQueryRatingEngineFactory {
	 IQueryRatingEngine get(IRatingEngineSchema schema);
}
