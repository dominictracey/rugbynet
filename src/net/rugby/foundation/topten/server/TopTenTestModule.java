package net.rugby.foundation.topten.server;

import net.rugby.foundation.core.server.factory.IPlaceFactory;
import net.rugby.foundation.core.server.factory.test.TestPlaceFactory;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;
import net.rugby.foundation.topten.server.factory.test.TestTopTenListFactory;

import com.google.inject.AbstractModule;

public class TopTenTestModule extends AbstractModule {
	@Override
	 protected void configure() {
		bind(ITopTenListFactory.class).to(TestTopTenListFactory.class);
		bind(IPlaceFactory.class).to(TestPlaceFactory.class);
	}
	
}

