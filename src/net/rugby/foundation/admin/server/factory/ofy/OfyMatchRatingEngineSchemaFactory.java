package net.rugby.foundation.admin.server.factory.ofy;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.admin.server.factory.BaseMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.core.server.factory.IRawScoreFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema;

public class OfyMatchRatingEngineSchemaFactory extends BaseMatchRatingEngineSchemaFactory implements IMatchRatingEngineSchemaFactory {

	private Objectify ofy;

	@Inject
	public OfyMatchRatingEngineSchemaFactory(IRawScoreFactory rsf)  {
		super(rsf);
		this.ofy = DataStoreFactory.getOfy();
	}

	protected IRatingEngineSchema getFromPersistentDatastore(Long id) {
		return ofy.get(new Key<ScrumMatchRatingEngineSchema>(ScrumMatchRatingEngineSchema.class, id));
	}

	protected IRatingEngineSchema getDefaultFromDB() {
		Query<ScrumMatchRatingEngineSchema> qs = ofy.query(ScrumMatchRatingEngineSchema.class).filter("isDefault",true);
		assert qs.count() < 2;
		return qs.get();
	}

	@Override
	public IRatingEngineSchema putToPersistentDatastore(IRatingEngineSchema schema) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();

			if (schema != null) {
				schema.setCreated(new Date());
				ofy.put(schema);
			}

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "put" + ex.getMessage(), ex);
			return null;
		}
		return schema;

	}

	@Override
	public boolean deleteFromPersistentDatastore(IRatingEngineSchema schema) {
		try {
			ofy.delete(schema);
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,ex.getLocalizedMessage());
			return false;
		}

		return true;
	}

	@Override
	public List<ScrumMatchRatingEngineSchema> getScrumList() {
		Query<ScrumMatchRatingEngineSchema> qs = ofy.query(ScrumMatchRatingEngineSchema.class);
		return qs.list();
	}

}
