package net.rugby.foundation.topten.server;

import net.rugby.foundation.core.server.factory.IPlaceFactory;
import net.rugby.foundation.core.server.factory.test.TestPlaceFactory;
import net.rugby.foundation.core.server.promote.IPromoter;
import net.rugby.foundation.core.server.promote.TwitterPromoter;
import net.rugby.foundation.topten.server.factory.INoteFactory;
import net.rugby.foundation.topten.server.factory.INoteRefFactory;
import net.rugby.foundation.topten.server.factory.IRoundNodeFactory;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;
import net.rugby.foundation.topten.server.factory.test.TestNoteFactory;
import net.rugby.foundation.topten.server.factory.test.TestNoteRefFactory;
import net.rugby.foundation.topten.server.factory.test.TestRoundNodeFactory;
import net.rugby.foundation.topten.server.factory.test.TestTopTenListFactory;
import net.rugby.foundation.topten.server.utilities.INotesCreator;
import net.rugby.foundation.topten.server.utilities.ISocialMediaDirector;
import net.rugby.foundation.topten.server.utilities.MatchNotesCreator;
import net.rugby.foundation.topten.server.utilities.SocialMediaDirector;
import net.rugby.foundation.topten.server.utilities.notes.TacklesNotesCreator;
import net.rugby.foundation.topten.server.utilities.notes.TopTenNotesCreator;
import net.rugby.foundation.topten.server.utilities.notes.TopTenNotesRenderer;
import net.rugby.foundation.topten.server.utilities.notes.TriesScoredNotesCreator;
import net.rugby.foundation.topten.server.utilities.notes.TwitterNotesRenderer;

import com.google.inject.AbstractModule;

public class TopTenTestModule extends AbstractModule {
	@Override
	 protected void configure() {
		bind(ITopTenListFactory.class).to(TestTopTenListFactory.class);
		bind(IPlaceFactory.class).to(TestPlaceFactory.class);
		bind(ISocialMediaDirector.class).to(SocialMediaDirector.class);
		bind(INoteFactory.class).to(TestNoteFactory.class);
		bind(INoteRefFactory.class).to(TestNoteRefFactory.class);
		bind(INotesCreator.class).to(MatchNotesCreator.class);
		bind(IPromoter.class).to(TwitterPromoter.class);
		bind(TopTenNotesRenderer.class);
		bind(TwitterNotesRenderer.class);
		bind(TopTenNotesCreator.class);
		bind(TriesScoredNotesCreator.class);
		bind(TacklesNotesCreator.class);
		bind(IRoundNodeFactory.class).to(TestRoundNodeFactory.class);
//		bind(IPlayerMatchFactory.class).to(TestPlayerMatchFactory.class);
	}
	
}

