package net.rugby.foundation.admin.server.factory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.IRawScoreFactory;
import net.rugby.foundation.model.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema20130713;

public abstract class BaseMatchRatingEngineSchemaFactory extends BaseCachingFactory<IRatingEngineSchema> implements IMatchRatingEngineSchemaFactory {

	private String memcacheDefaultKey = "MRES-Default";
	private IRawScoreFactory rsf;
	
	protected BaseMatchRatingEngineSchemaFactory(IRawScoreFactory rsf) {
		this.rsf = rsf;
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

	protected abstract IRatingEngineSchema getDefaultFromDB();

	@Override
	public IRatingEngineSchema put(IRatingEngineSchema schema) {
		try {

			super.put(schema);
			
			//also check if it has it's default flag set
			if (schema.getIsDefault()) {
				IRatingEngineSchema currentDefault = getDefault();
				if (currentDefault != null && !currentDefault.getId().equals(schema.getId())) {
					// clear default flag in current one
					currentDefault.setIsDefault(false);
					
					put(currentDefault); // weird re-entrant code...
					
					// update memCache's default schema
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutput out = new ObjectOutputStream(bos);   
					out.writeObject(schema);
					byte[] yourBytes = bos.toByteArray(); 

					out.close();
					bos.close();
					MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
					syncCache.put(memcacheDefaultKey, yourBytes);
					if (currentDefault != null && currentDefault.getId() != null && syncCache.contains(currentDefault.getId())) {
						syncCache.delete(currentDefault.getId()); // make them refetch it without the flag..
					}

				}
			}


		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "put" + ex.getMessage(), ex);
			return null;
		}
		return schema;

	}
	
	@Override
	public boolean delete(IRatingEngineSchema schema) {
		try {


			
			//also check if it has it's default flag set
			if (schema.getIsDefault()) {
				// can't delete default
				throw new Exception("Can't delete default schema");
			} else {
				super.delete(schema);
				
				// delete any raw scores created with this schema
				rsf.delete(schema);
			}


		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "put" + ex.getMessage(), ex);
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
					put(current);
				}
				schema.setIsDefault(true);
				put(schema);
				
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
	public IRatingEngineSchema create() {
		IRatingEngineSchema schema = new ScrumMatchRatingEngineSchema20130713();
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
		
		return schema;
	}
}
