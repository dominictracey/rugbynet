package net.rugby.foundation.admin.server.factory.test;

import java.util.List;

import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.shared.IMatchRatingEngineSchema;
import net.rugby.foundation.admin.shared.ScrumMatchRatingEngineSchema;
import net.rugby.foundation.admin.shared.ScrumMatchRatingEngineSchema20130713;

public class TestMatchRatingEngineSchemaFactory implements
		IMatchRatingEngineSchemaFactory {

	@Override
	public IMatchRatingEngineSchema getById(Long id) {
		if (id == 1000L) {
			return new ScrumMatchRatingEngineSchema20130713();
		}
		return null;
	}

	@Override
	public IMatchRatingEngineSchema getDefault() {
		return new ScrumMatchRatingEngineSchema20130713();
	}

	@Override
	public IMatchRatingEngineSchema put(IMatchRatingEngineSchema schema) {
		return schema;
	}

	@Override
	public Boolean delete(IMatchRatingEngineSchema schema) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMatchRatingEngineSchema setAsDefault(IMatchRatingEngineSchema schema) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ScrumMatchRatingEngineSchema> getScrumList() {
		// TODO Auto-generated method stub
		return null;
	}

}
