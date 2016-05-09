//package net.rugby.foundation.topten.server.factory.test;
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
//public class TestPlayerMatchFactory extends BaseCachingFactory<PlayerMatch>
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
//			return ofy.get(PlayerMatch.class, id);
//		} catch (Exception ex) {
//			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE," create ", ex);
//			return null;
//		}
//	}
//
//	@Override
//	protected PlayerMatch putToPersistentDatastore(PlayerMatch rn) {
//		try {
//			Objectify ofy = DataStoreFactory.getOfy();
//			ofy.put(rn);
//			return rn;
//		} catch (Exception ex) {
//			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE," create ", ex);
//			return null;
//		}
//	}
//
//	@Override
//	protected boolean deleteFromPersistentDatastore(PlayerMatch rn) {
//		try {
//			Objectify ofy = DataStoreFactory.getOfy();
//			ofy.delete(rn);
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
