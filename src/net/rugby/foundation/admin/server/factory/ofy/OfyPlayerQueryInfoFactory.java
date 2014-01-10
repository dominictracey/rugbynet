package net.rugby.foundation.admin.server.factory.ofy;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.IPlayerQueryInfoFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.model.shared.IPlayerQueryInfo;
import net.rugby.foundation.model.shared.IRatingQuery;

public class OfyPlayerQueryInfoFactory implements IPlayerQueryInfoFactory {

	@Inject
	public OfyPlayerQueryInfoFactory(IPlayerRatingFactory prf) {
		
	}
	
	@Override
	public IPlayerQueryInfo get(Long id) {
		// currently not stored in database
		return null;
	}

	@Override
	public List<IPlayerQueryInfo> query(IRatingQuery query, Long schemaId) {
		// create list from ratings that match query
		List<IPlayerQueryInfo> list = new ArrayList<IPlayerQueryInfo>();
		
		
		return null;
	}

}
