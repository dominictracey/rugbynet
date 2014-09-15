package net.rugby.foundation.admin.server.factory;

import java.util.List;

import net.rugby.foundation.core.server.factory.ICachingFactory;
import net.rugby.foundation.model.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema;

public interface IMatchRatingEngineSchemaFactory extends ICachingFactory<IRatingEngineSchema> {

	IRatingEngineSchema getDefault();
	IRatingEngineSchema setAsDefault(IRatingEngineSchema schema);
	
	// this is awkward downcast
	List<ScrumMatchRatingEngineSchema> getScrumList();	
}
