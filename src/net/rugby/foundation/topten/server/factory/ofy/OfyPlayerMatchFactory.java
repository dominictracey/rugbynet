//package net.rugby.foundation.topten.server.factory.ofy;
//
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import net.rugby.foundation.core.server.factory.BaseCachingFactory;
//import net.rugby.foundation.model.shared.DataStoreFactory;
//import net.rugby.foundation.topten.server.factory.IPlayerMatchFactory;
//import net.rugby.foundation.topten.server.rest.PlayerMatch;
//
//import com.googlecode.objectify.Objectify;
//
//
//public class OfyPlayerMatchFactory extends BaseCachingFactory<PlayerMatch>
//		implements IPlayerMatchFactory {
//
//	@Override
//	public PlayerMatch create() {
//		try {
//			return new PlayerMatch();
//		} catch (Exception ex) {
//			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE," create ", ex);
//			return null;
//		}
//	}
//
//	@Override
//	protected PlayerMatch getFromPersistentDatastore(Long id) {
//		try {
//			Objectify ofy = DataStoreFactory.getOfy();
//			
//			PlayerMatch pm = ofy.get(PlayerMatch.class, id);
//			return pm;
//		} catch (Exception ex) {
//			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE," get ", ex);
//			return null;
//		}
//	}
//
//	@Override
//	protected PlayerMatch putToPersistentDatastore(PlayerMatch pm) {
//		try {
//			Objectify ofy = DataStoreFactory.getOfy();
//			
//			ofy.put(pm);
//			return pm;
//		} catch (Exception ex) {
//			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE," create ", ex);
//			return null;
//		}
//	}
//
//	@Override
//	protected boolean deleteFromPersistentDatastore(PlayerMatch pm) {
//		try {
//			Objectify ofy = DataStoreFactory.getOfy();
//			ofy.delete(pm);
//			return true;
//		} catch (Exception ex) {
//			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE," create ", ex);
//			return false;
//		}
//	}
//
//
//
//}
