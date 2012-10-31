package net.rugby.foundation.core.server.factory.ofy;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import net.rugby.foundation.core.server.factory.IMatchResultFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.MatchResult;
import net.rugby.foundation.model.shared.SimpleScoreMatchResult;

public class OfyMatchResultFactory implements IMatchResultFactory {
	private Long id;
	private final Objectify ofy;
	
	@Inject
	OfyMatchResultFactory() {
		this.ofy = DataStoreFactory.getOfy();
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
		if (id != null)
			return ofy.get(new Key<SimpleScoreMatchResult>(SimpleScoreMatchResult.class,id));
		else
			return null;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IMatchResultFactory#put(net.rugby.foundation.model.shared.IMatchResult)
	 */
	@Override
	public IMatchResult put(IMatchResult g) {

		// don't need instanceof here?
		ofy.put((MatchResult)g);
		
		return g;
	}

}
