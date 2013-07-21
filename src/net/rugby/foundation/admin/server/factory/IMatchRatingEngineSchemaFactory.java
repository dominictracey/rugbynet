package net.rugby.foundation.admin.server.factory;

import java.util.List;

import net.rugby.foundation.admin.shared.IMatchRatingEngineSchema;
import net.rugby.foundation.admin.shared.ScrumMatchRatingEngineSchema;

public interface IMatchRatingEngineSchemaFactory {
	IMatchRatingEngineSchema getById(Long id);
	IMatchRatingEngineSchema getDefault();
	IMatchRatingEngineSchema put (IMatchRatingEngineSchema schema);
	Boolean delete(IMatchRatingEngineSchema schema);
	IMatchRatingEngineSchema setAsDefault(IMatchRatingEngineSchema schema);
	
	// this is awkward downcast
	List<ScrumMatchRatingEngineSchema> getScrumList();	
}
