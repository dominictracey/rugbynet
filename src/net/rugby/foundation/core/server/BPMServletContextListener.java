/**
 * 
 */
package net.rugby.foundation.core.server;

import net.rugby.foundation.admin.server.AdminMainModule;
import net.rugby.foundation.admin.server.AdminTestModule;
import net.rugby.foundation.game1.server.Game1MainModule;
import net.rugby.foundation.game1.server.Game1TestModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * @author home
 *
 */
public class BPMServletContextListener extends GuiceServletContextListener {

	private static Injector injector;
	private static boolean initialized = false;
	
	public static final Injector getInjectorForNonServlets() {
//		if (!initialized) {
//						injector = Guice.createInjector(new BPMServletModule(), new Game1MainModule(), new CoreMainModule(), new AdminMainModule());
//			//injector = Guice.createInjector(new BPMServletModule(), new Game1TestModule(), new CoreTestModule(), new AdminTestModule());
//			initialized = true;
//		}
//		
//		return injector;
		return Guice.createInjector(new Game1MainModule(), new CoreMainModule(), new AdminMainModule());
//	return Guice.createInjector( new Game1TestModule(), new CoreTestModule(), new AdminTestModule());		
	}
	
	/* (non-Javadoc)
	 * @see com.google.inject.servlet.GuiceServletContextListener#getInjector()
	 */
	@Override
	public Injector getInjector() {
		return Guice.createInjector(new BPMServletModule(), new Game1MainModule(), new CoreMainModule(), new AdminMainModule());
//		return Guice.createInjector(new BPMServletModule(), new Game1TestModule(), new CoreTestModule(), new AdminTestModule());

	}

}
