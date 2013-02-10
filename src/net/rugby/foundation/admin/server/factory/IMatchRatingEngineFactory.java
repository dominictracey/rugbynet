package net.rugby.foundation.admin.server.factory;

import net.rugby.foundation.admin.server.model.IMatchRatingEngine;
import net.rugby.foundation.admin.shared.IMatchRatingEngineSchema;

public interface IMatchRatingEngineFactory {
	IMatchRatingEngine get(IMatchRatingEngineSchema schema);
}
