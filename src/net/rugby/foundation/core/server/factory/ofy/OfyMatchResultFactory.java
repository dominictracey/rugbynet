package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.IMatchResultFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.MatchResult;
import net.rugby.foundation.model.shared.SimpleScoreMatchResult;

public class OfyMatchResultFactory extends BaseCachingFactory<IMatchResult> implements IMatchResultFactory, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -63026632234487370L;
	private Long id;

	//@Inject
	OfyMatchResultFactory() {
	}

	@Override
	protected IMatchResult getFromPersistentDatastore(Long id) {
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
	protected IMatchResult putToPersistentDatastore(IMatchResult g) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();

			// TODO don't need instanceof here?
			ofy.put((MatchResult)g);

			return g;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}
	@Override
	public boolean deleteFromPersistentDatastore(IMatchResult r) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();

			ofy.delete(r);
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem in delete: " + ex.getLocalizedMessage());
			return false;
		}
		return true;
	}

	@Override
	public IMatchResult create() {
		return new SimpleScoreMatchResult();
	}


}
