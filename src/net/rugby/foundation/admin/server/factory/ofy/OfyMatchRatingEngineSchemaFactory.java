package net.rugby.foundation.admin.server.factory.ofy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.shared.IRatingEngineSchema;
import net.rugby.foundation.admin.shared.ScrumMatchRatingEngineSchema;
import net.rugby.foundation.admin.shared.ScrumMatchRatingEngineSchema20130713;
import net.rugby.foundation.model.shared.DataStoreFactory;

public class OfyMatchRatingEngineSchemaFactory implements
IMatchRatingEngineSchemaFactory {

	private Objectify ofy;
	private String memcacheDefaultKey = "MRES-Default";

	@Inject
	public OfyMatchRatingEngineSchemaFactory() {
		this.ofy = DataStoreFactory.getOfy();
	}

	@Override
	public IRatingEngineSchema getById(Long id) {
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			IRatingEngineSchema m = null;

			if (id == null) {
				return (IRatingEngineSchema) put(null);
			}

			value = (byte[])syncCache.get(id);
			if (value == null) {

				m = getByIdFromDB(id);

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos);   
				out.writeObject(m);
				byte[] yourBytes = bos.toByteArray(); 

				out.close();
				bos.close();

				syncCache.put(id, yourBytes);
			} else {

				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				m = (IRatingEngineSchema)in.readObject();

				bis.close();
				in.close();

			}
			return m;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}

	}

	private IRatingEngineSchema getByIdFromDB(Long id) {
		return ofy.get(new Key<ScrumMatchRatingEngineSchema>(ScrumMatchRatingEngineSchema.class, id));
	}

	@Override
	public IRatingEngineSchema getDefault() {
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			IRatingEngineSchema m = null;

			value = (byte[])syncCache.get(memcacheDefaultKey);
			if (value == null) {

				m = getDefaultFromDB();

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos);   
				out.writeObject(m);
				byte[] yourBytes = bos.toByteArray(); 

				out.close();
				bos.close();

				syncCache.put(memcacheDefaultKey, yourBytes);
			} else {

				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				m = (IRatingEngineSchema)in.readObject();

				bis.close();
				in.close();

			}
			return m;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getDefault" + ex.getMessage(), ex);
			return null;
		}
	}

	private IRatingEngineSchema getDefaultFromDB() {
		Query<ScrumMatchRatingEngineSchema> qs = ofy.query(ScrumMatchRatingEngineSchema.class).filter("isDefault",true);
		assert qs.count() < 2;
		return qs.get();
	}

	@Override
	public IRatingEngineSchema put(IRatingEngineSchema schema) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			// only one per name
			//			if (schema != null && schema.getPlayerMatchStats() != null) {
			//				IMatchRatingEngineSchema existing = get(schema.getPlayerMatchStats(), schema.getSchema());
			//				if (existing != null) {
			//					ofy.delete(existing);
			//				}
			//			}

			if (schema != null) {
				schema.setCreated(new Date());
				ofy.put(schema);

				// put it in memcache - note we don't have to remove any existing because SetPolicy.SET_ALWAYS will replace them.
				MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos);   
				out.writeObject(schema);
				byte[] yourBytes = bos.toByteArray(); 

				out.close();
				bos.close();

				syncCache.put(((IRatingEngineSchema)schema).getId(), yourBytes);

				//also check if it has it's default flag set
				if (schema.getIsDefault()) {
					IRatingEngineSchema currentDefault = getDefault();
					if (currentDefault != null && !currentDefault.getId().equals(schema.getId())) {
						// clear default flag in current one
						currentDefault.setIsDefault(false);
						put(currentDefault); // weird re-entrant code...
					}

					syncCache.put(memcacheDefaultKey, yourBytes);
				}
			} else {
				schema = new ScrumMatchRatingEngineSchema20130713();
				schema.setIsDefault(false);
				((ScrumMatchRatingEngineSchema20130713)schema).setCleanBreaksWeight(.2F);
				((ScrumMatchRatingEngineSchema20130713)schema).setDefendersBeatenWeight(.2F);
				((ScrumMatchRatingEngineSchema20130713)schema).setKicksWeight(.2F);
				((ScrumMatchRatingEngineSchema20130713)schema).setLineoutShareWeight(.2F);
				((ScrumMatchRatingEngineSchema20130713)schema).setLineoutsStolenOnOppThrowWeight(.2F);
				((ScrumMatchRatingEngineSchema20130713)schema).setLineoutsWonOnThrowWeight(.2F);
				((ScrumMatchRatingEngineSchema20130713)schema).setMaulShareWeight(.2F);
				((ScrumMatchRatingEngineSchema20130713)schema).setMetersRunWeight(.2F);
				((ScrumMatchRatingEngineSchema20130713)schema).setMinutesShareWeight(.2F);
				((ScrumMatchRatingEngineSchema20130713)schema).setOffloadsWeight(.2F);
				((ScrumMatchRatingEngineSchema20130713)schema).setPassesWeight(.2F);
				((ScrumMatchRatingEngineSchema20130713)schema).setPenaltiesConcededWeight(-.2F);
				((ScrumMatchRatingEngineSchema20130713)schema).setPointsDifferentialWeight(.2F);
				((ScrumMatchRatingEngineSchema20130713)schema).setPointsWeight(.2F);
				((ScrumMatchRatingEngineSchema20130713)schema).setRedCardsWeight(-.2F);
				((ScrumMatchRatingEngineSchema20130713)schema).setRuckShareWeight(.2F);
				((ScrumMatchRatingEngineSchema20130713)schema).setRunsWeight(.2F);
				((ScrumMatchRatingEngineSchema20130713)schema).setScrumShareWeight(.2F);
				((ScrumMatchRatingEngineSchema20130713)schema).setTacklesMadeWeight(.2F);
				((ScrumMatchRatingEngineSchema20130713)schema).setTacklesMissedWeight(-.2F);
				((ScrumMatchRatingEngineSchema20130713)schema).setTriesWeight(.2F);
				((ScrumMatchRatingEngineSchema20130713)schema).setTryAssistsWeight(.2F);
				((ScrumMatchRatingEngineSchema20130713)schema).setTurnoversWeight(-.2F);
				((ScrumMatchRatingEngineSchema20130713)schema).setYellowCardsWeight(-.2F);


			}

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "put" + ex.getMessage(), ex);
			return null;
		}
		return schema;

	}

	@Override
	public Boolean delete(IRatingEngineSchema schema) {
		try {
			boolean isDefault = schema.getIsDefault();
			ofy.delete(schema);
			//also delete from memcache
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			syncCache.delete(schema.getId());
			if (isDefault) {
				syncCache.delete(memcacheDefaultKey);
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,ex.getLocalizedMessage());
			return false;
		}

		return true;
	}

	@Override
	public IRatingEngineSchema setAsDefault(IRatingEngineSchema schema) {

		IRatingEngineSchema current = getDefault();

		if (current != null && current.equals(schema)) {
			return schema;
		} else {
			try {
				if (current != null) {
					current.setIsDefault(false);
					ofy.put(current);
				}
				schema.setIsDefault(true);
				ofy.put(schema);
				
				// also memcache
				MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos);   
				out.writeObject(schema);
				byte[] yourBytes = bos.toByteArray(); 

				out.close();
				bos.close();

				syncCache.put(memcacheDefaultKey, yourBytes);
				syncCache.put(schema.getId(), yourBytes);
				if (current != null && current.getId() != null && syncCache.contains(current.getId())) {
					syncCache.delete(current.getId()); // make them refetch it without the flag..
				}
				
				return schema;
			} catch (Exception ex) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,ex.getLocalizedMessage());
				return null;	
			}
		}
	}

	@Override
	public List<ScrumMatchRatingEngineSchema> getScrumList() {
		Query<ScrumMatchRatingEngineSchema> qs = ofy.query(ScrumMatchRatingEngineSchema.class);
		return qs.list();
	}

}
