/**
 * 
 */
package net.rugby.foundation.engine.server;

import net.rugby.foundation.admin.server.AdminMainModule;
import net.rugby.foundation.admin.server.AdminTestModule;
import net.rugby.foundation.core.server.CoreMainModule;
import net.rugby.foundation.game1.server.Game1MainModule;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.topten.server.TopTenMainModule;
import net.rugby.foundation.topten.server.TopTenTestModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * @author home
 *
 */
public class BPMServletContextListener extends GuiceServletContextListener {
	
	public static final Injector getInjectorForNonServlets() {

		return Guice.createInjector(new CoreMainModule(), new Game1MainModule(), new AdminMainModule(), new TopTenMainModule());
//	return Guice.createInjector(new CoreTestModule(), new Game1TestModule(), new AdminTestModule(), new TopTenTestModule());		
	}
	
	/* (non-Javadoc)
	 * @see com.google.inject.servlet.GuiceServletContextListener#getInjector()
	 */
	@Override
	public Injector getInjector() {
		return Guice.createInjector(new BPMServletModule(), new CoreMainModule(), new Game1MainModule(), new AdminMainModule(), new TopTenMainModule());
//		return Guice.createInjector(new BPMServletModule(), new CoreTestModule(), new Game1TestModule(), new AdminTestModule(), new TopTenTestModule());

	}

}
