package net.rugby.foundation.topten.server;

import net.rugby.foundation.topten.server.factory.ITopTenListFactory;
import net.rugby.foundation.topten.server.factory.ofy.OfyTopTenListFactory;
import net.rugby.foundation.topten.server.factory.test.TestTopTenListFactory;

import com.google.inject.AbstractModule;

public class TopTenMainModule extends AbstractModule {
	@Override
	 protected void configure() {
		bind(ITopTenListFactory.class).to(OfyTopTenListFactory.class);
	}
	
}

