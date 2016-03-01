package net.rugby.foundation.admin.server.factory.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.IBlurbFactory;
import net.rugby.foundation.admin.shared.Blurb;
import net.rugby.foundation.admin.shared.IBlurb;
import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.core.server.factory.IPlaceFactory;

public class TestBlurbFactory extends BaseCachingFactory<IBlurb> implements
		IBlurbFactory {

	protected List<IBlurb> all = new ArrayList<IBlurb>();
	protected List<IBlurb> active = new ArrayList<IBlurb>();
	protected Map<Long, IBlurb> idMap = new HashMap<Long, IBlurb>();
	private IPlaceFactory pf;
	private IAppUserFactory auf;
	private boolean initialized = false;
	private Long nextId = 90000100L;
	
	@Inject
	public TestBlurbFactory(IPlaceFactory pf, IAppUserFactory auf) {
		this.pf = pf;
		this.auf = auf;
		initialize();
	}
	
	private void initialize() {
		if (!initialized) {
			auf.setId(8000L);
			IBlurb b1 = new Blurb(9000001L, new Date(), 33001L, pf.get(33001L), "I had many problems of this same type, and also with some extreme flickering, but I was able to make this work perfectly by ditching the col-md-* elements and going for a list instead, as the jQuery UI sortable example is on their site. http://jqueryui.com/sortable/#display-grid", "Top Ten Flankers of the Aviva Premiership This Week", 8000L, true);
			all.add(b1);
			active.add(b1);
			idMap.put(9000001L, b1);
			
			IBlurb b2 = new Blurb(9000002L, new Date(), 33001L, pf.get(33001L), "The answer provided by @AlexStack (to add a transparent border-top to each <div>) is almost there, but doesn't work in current FireFox (36.0.1 at time of testing/writing)... the same issue occurs.  \n\n" +
														"Does anybody know a solution that covers all major browsers?", "Top Ten Flankers of the PRO12 This Week", 8000L, true);
			all.add(b2);
			active.add(b2);
			idMap.put(9000002L, b2);
			
			IBlurb b3 = new Blurb(9000003L, new Date(), 33001L, pf.get(33001L), "Dragging a panel that is not in the right-most column moves the panel with the mouse, and the placeholder moves under the mouse unless you move it over the right-most column.", "Billy Bobby is moving up in the world", 8000L, true);
			all.add(b3);
			active.add(b3);
			idMap.put(9000003L, b3);
			
			IBlurb b4 = new Blurb(9000004L, new Date(), 33001L, pf.get(33001L), "This one isn't active", "It happened a while back and is inactive now", 8000L, false);
			all.add(b4);
			idMap.put(9000004L, b4);
			
			initialized = true;
		}
	}

	@Override
	public List<IBlurb> getActive() {
		return active;
	}

	@Override
	public IBlurb create() {
		try {
			IBlurb blurb = new Blurb();
			blurb.setCreated(new Date());
			blurb.setId(nextId);
			nextId++;
			return blurb;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "create" + ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected IBlurb getFromPersistentDatastore(Long id) {		
		return idMap.get(id);
	}

	@Override
	protected IBlurb putToPersistentDatastore(IBlurb blurb) {
		idMap.put(blurb.getId(), blurb);

		// @TODO do we need to update active and all?
		
		return blurb;
	}

	@Override
	protected boolean deleteFromPersistentDatastore(IBlurb blurb) {
		idMap.remove(blurb.getId());
		return false;
	}

}
