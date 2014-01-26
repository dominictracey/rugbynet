/**
 * 
 */
package net.rugby.foundation.core.server;

import net.rugby.foundation.admin.server.AdminMainModule;
import net.rugby.foundation.game1.server.Game1MainModule;
import net.rugby.foundation.topten.server.TopTenMainModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * @author home
 *
 */
public class BPMServletContextListener extends GuiceServletContextListener {
	
	public static final Injector getInjectorForNonServlets() {

		return Guice.createInjector(new Game1MainModule(), new CoreMainModule(), new AdminMainModule(), new TopTenMainModule());
//	return Guice.createInjector( new Game1TestModule(), new CoreTestModule(), new AdminTestModule(), new TopTenTestModule());		
	}
	
	/* (non-Javadoc)
	 * @see com.google.inject.servlet.GuiceServletContextListener#getInjector()
	 */
	@Override
	public Injector getInjector() {
		return Guice.createInjector(new BPMServletModule(), new Game1MainModule(), new CoreMainModule(), new AdminMainModule(), new TopTenMainModule());
//		return Guice.createInjector(new BPMServletModule(), new Game1TestModule(), new CoreTestModule(), new AdminTestModule(), new TopTenTestModule());

	}

}
