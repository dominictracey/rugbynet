package net.rugby.foundation.admin.server.factory;

import java.util.List;

import net.rugby.foundation.model.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema;

public interface IMatchRatingEngineSchemaFactory {
	IRatingEngineSchema getById(Long id);
	IRatingEngineSchema getDefault();
	IRatingEngineSchema put (IRatingEngineSchema schema);
	Boolean delete(IRatingEngineSchema schema);
	IRatingEngineSchema setAsDefault(IRatingEngineSchema schema);
	
	// this is awkward downcast
	List<ScrumMatchRatingEngineSchema> getScrumList();	
}
