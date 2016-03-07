package net.rugby.foundation.admin.server.factory.ofy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.admin.server.factory.IBlurbFactory;
import net.rugby.foundation.admin.shared.Blurb;
import net.rugby.foundation.admin.shared.IBlurb;
import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.IPlaceFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IServerPlace;
import net.rugby.foundation.model.shared.ServerPlace;

public class OfyBlurbFactory extends BaseCachingFactory<IBlurb> implements IBlurbFactory {

	private Objectify ofy;
	private IPlaceFactory pf;
	
	@Inject
	public OfyBlurbFactory(IPlaceFactory pf) {
		this.ofy = DataStoreFactory.getOfy();
		this.pf = pf;
	}

	@Override
	public IBlurb create() {
		try {
			IBlurb blurb = new Blurb();
			blurb.setCreated(new Date());
			blurb.setActive(true);
			return blurb;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "create" + ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected IBlurb getFromPersistentDatastore(Long id) {
		try {
			Blurb blurb = ofy.get(new Key<Blurb>(Blurb.class, id));
			if (blurb != null) {
				populateBlurb(blurb);
			}
			return blurb;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "get" + ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected IBlurb putToPersistentDatastore(IBlurb blurb) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();

			if (blurb != null) {
				blurb.setCreated(new Date());
				ofy.put(blurb);
			}
			return blurb;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "put" + ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected boolean deleteFromPersistentDatastore(IBlurb blurb) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();

			if (blurb != null) {
				ofy.delete(blurb);
				return true;
			} else {
				return false;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "delete" + ex.getMessage(), ex);
			return false;
		}
	}

	@Override
	public List<IBlurb> getActive() {
		try {
			List<IBlurb> list = new ArrayList<IBlurb>();
			Objectify ofy = DataStoreFactory.getOfy();
			
			Query<Blurb> qsp = ofy.query(Blurb.class).filter("active",true);
			
			for (Blurb b : qsp.list()) {
				populateBlurb(b);
				list.add(b);
			}
			
			return list;
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getActive" + e.getMessage(), e);
			return null;
		}
	}

	private void populateBlurb(Blurb b) {
		b.setServerPlace(pf.get(b.getServerPlaceId()));
	}


}
