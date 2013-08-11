package net.rugby.foundation.topten.server;

import net.rugby.foundation.topten.server.factory.ITopTenListFactory;
import net.rugby.foundation.topten.server.factory.test.TestTopTenListFactory;

import com.google.inject.AbstractModule;

public class TopTenTestModule extends AbstractModule {
	@Override
	 protected void configure() {
		bind(ITopTenListFactory.class).to(TestTopTenListFactory.class);
	}
	
}
