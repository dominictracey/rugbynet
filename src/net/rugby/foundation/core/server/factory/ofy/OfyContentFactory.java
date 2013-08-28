package net.rugby.foundation.core.server.factory.ofy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.IContentFactory;
import net.rugby.foundation.model.shared.Content;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IContent;

public class OfyContentFactory extends BaseCachingFactory<IContent> implements IContentFactory {

	private final String key = this.getClass().getCanonicalName() + "All-top-ten";
	@Override
	public IContent getFromPersistentDatastore(Long id) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			return ofy.get(new Key<Content>(Content.class,id));
		} catch (NotFoundException ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,ex.getLocalizedMessage(),ex);
			return null;
		}
	}

	@Override
	public IContent putToPersistentDatastore(IContent t) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			ofy.put((Content)t);
			return t;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,ex.getLocalizedMessage(),ex);
			return null;
		}
	}

	@Override
	public boolean deleteFromPersistentDatastore(IContent t) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			ofy.delete((Content)t);
			return true;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,ex.getLocalizedMessage(),ex);
			return false;
		}
	}

	@Override
	public IContent create() {
		try {
			Content t = new Content();
			Objectify ofy = DataStoreFactory.getOfy();
			ofy.put(t);
			return t;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,ex.getLocalizedMessage(),ex);
			return null;
		}
	}

	@Override
	public List<IContent> getAll(Boolean onlyActive) {
		List<IContent> list = super.getList(key);
		
		if (list == null) {
			Objectify ofy = DataStoreFactory.getOfy();
			Query<Content> qc = null;
			if (onlyActive) {
				qc = ofy.query(Content.class).filter("active", true);
			} else {
				qc = ofy.query(Content.class);
			}
			
			//@REX hate this
			list = new ArrayList<IContent>();
			Iterator<Content> it = qc.iterator();
			while (it.hasNext()) {
				list.add(it.next());
			}
			
			super.putList(key, list);
		}
		
		return list;
	}

}
