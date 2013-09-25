package net.rugby.foundation.admin.server.factory;

import net.rugby.foundation.admin.server.model.IMatchRatingEngine;
import net.rugby.foundation.admin.shared.IRatingEngineSchema;

public interface IMatchRatingEngineFactory {
	IMatchRatingEngine get(IRatingEngineSchema schema);
}
