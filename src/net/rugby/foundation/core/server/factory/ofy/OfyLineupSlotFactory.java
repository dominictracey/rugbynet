package net.rugby.foundation.core.server.factory.ofy;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.ILineupSlotFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.ILineupSlot;
import net.rugby.foundation.model.shared.LineupSlot;
import net.rugby.foundation.model.shared.Position;
import net.rugby.foundation.model.shared.Position.position;

public class OfyLineupSlotFactory extends BaseCachingFactory<ILineupSlot> implements ILineupSlotFactory {


	@Override
	public ILineupSlot create() {
		try {
			ILineupSlot s = new LineupSlot();
			return s;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected ILineupSlot getFromPersistentDatastore(Long id) {
		try {
			if (id != null) {
				Objectify ofy = DataStoreFactory.getOfy();
				ILineupSlot s = ofy.get(new Key<LineupSlot>(LineupSlot.class,id));
				return s;
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Don't try to get with null to get an empty object. Call create() instead!");
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected ILineupSlot putToPersistentDatastore(ILineupSlot t) {
		try {
			if (t != null) {
				Objectify ofy = DataStoreFactory.getOfy();
				ofy.put(t);
				return t;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected boolean deleteFromPersistentDatastore(ILineupSlot t) {
		try {
			if (t != null) {
				Objectify ofy = DataStoreFactory.getOfy();
				ofy.delete(t);
				return true;
			} else {
				return false;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return false;
		}
	}

	@Override
	public position getPosFromSlot(int slot) {
		if (slot == 0) {
			return Position.position.FULLBACK;
		} else if  (slot == 1) {
			return Position.position.WING;
		} else if  (slot == 2 || slot == 3) {
			return Position.position.CENTER;
		} else if  (slot == 4) {
			return Position.position.WING;
		} else if  (slot == 5) {
			return Position.position.FLYHALF;
		} else if  (slot == 6) {
			return Position.position.SCRUMHALF;
		} else if  (slot == 7 || slot == 9) {
			return Position.position.PROP;
		} else if  (slot == 8) {
			return Position.position.HOOKER;
		} else if  (slot == 10 || slot == 11) {
			return Position.position.LOCK;
		} else if  (slot == 12 || slot == 13) {
			return Position.position.FLANKER;
		} else if  (slot == 14) {
			return Position.position.NUMBER8;
		} else if  (slot < 24) {
			return Position.position.RESERVE;
		} else {
			return Position.position.NONE;
		}
	}

}
