package net.rugby.foundation.core.server.factory.test;

import java.util.ArrayList;
import java.util.List;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.IContentFactory;
import net.rugby.foundation.model.shared.Content;
import net.rugby.foundation.model.shared.IContent;

public class TestContentFactory extends BaseCachingFactory<IContent>  implements IContentFactory {

	@Override
	public IContent create() {
		IContent c = new Content();
		return c;
	}

	@Override
	protected IContent getFromPersistentDatastore(Long id) {
		if (id == 14000L) {
			IContent c = create();
			c.setActive(true);
			c.setTitle("Who");
			c.setBody("we are awesome");
			c.setMenuName("Who are you guys");
			c.setShowInMenu(true);
			return c;
		}
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
		List<IContent> cs = new ArrayList<IContent>();
		cs.add(get(14000L));
		return cs;
	}

	@Override
	public IContent getForDiv(String string) {
		// TODO Auto-generated method stub
		return null;
	}


}
