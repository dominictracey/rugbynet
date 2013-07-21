package net.rugby.foundation.core.server.factory.ofy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import net.rugby.foundation.core.server.factory.IMatchResultFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.MatchGroup;
import net.rugby.foundation.model.shared.MatchResult;
import net.rugby.foundation.model.shared.SimpleScoreMatchResult;

public class OfyMatchResultFactory implements IMatchResultFactory, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -63026632234487370L;
	private Long id;

	//@Inject
	OfyMatchResultFactory() {
	}
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IMatchResultFactory#get()
	 */
	@Override
	public IMatchResult get() {
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			IMatchResult mr = null;

			if (id == null) {
				return (SimpleScoreMatchResult) put(null);
			}

			value = (byte[])syncCache.get(id);
			if (value == null) {
				setId(id);
				mr = getFromDB();

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos);   
				out.writeObject(mr);
				byte[] yourBytes = bos.toByteArray(); 

				out.close();
				bos.close();

				syncCache.put(id, yourBytes);
			} else {

				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				mr = (IMatchResult)in.readObject();

				bis.close();
				in.close();

			}
			return mr;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}	


	private IMatchResult getFromDB() {
		if (id != null) {
			Objectify ofy = DataStoreFactory.getOfy();
			return ofy.get(new Key<SimpleScoreMatchResult>(SimpleScoreMatchResult.class,id));
		} else
			return null;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IMatchResultFactory#put(net.rugby.foundation.model.shared.IMatchResult)
	 */
	@Override
	public IMatchResult put(IMatchResult g) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();

			// TODO don't need instanceof here?
			ofy.put((MatchResult)g);

			// now update the memcache version
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			syncCache.delete(g.getId());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);   
			out.writeObject(g);
			byte[] yourBytes = bos.toByteArray(); 

			out.close();
			bos.close();

			syncCache.put(id, yourBytes);
			return g;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

}
