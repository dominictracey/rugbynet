package net.rugby.foundation.core.server.factory.test;

import java.util.List;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.IContentFactory;
import net.rugby.foundation.model.shared.IContent;

public class TestContentFactory extends BaseCachingFactory<IContent>  implements IContentFactory {

	@Override
	public IContent create() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IContent getFromPersistentDatastore(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IContent putToPersistentDatastore(IContent t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean deleteFromPersistentDatastore(IContent t) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<IContent> getAll(Boolean onlyActive) {
		// TODO Auto-generated method stub
		return null;
	}


}
