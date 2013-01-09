package net.rugby.foundation.game1.server;

import net.rugby.foundation.game1.server.BPM.CoreRuleFactory;
import net.rugby.foundation.game1.server.BPM.ICoreRuleFactory;
import net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory;
import net.rugby.foundation.game1.server.factory.IConfigurationFactory;
import net.rugby.foundation.game1.server.factory.IEntryFactory;
import net.rugby.foundation.game1.server.factory.ILeaderboardFactory;
import net.rugby.foundation.game1.server.factory.ILeaderboardRowFactory;
import net.rugby.foundation.game1.server.factory.ILeagueFactory;
import net.rugby.foundation.game1.server.factory.IMatchEntryFactory;
import net.rugby.foundation.game1.server.factory.IMatchStatsFactory;
import net.rugby.foundation.game1.server.factory.IRoundEntryFactory;
import net.rugby.foundation.game1.server.factory.ofy.OfyClubhouseLeagueMapFactory;
import net.rugby.foundation.game1.server.factory.ofy.OfyConfigurationFactory;
import net.rugby.foundation.game1.server.factory.ofy.OfyEntryFactory;
import net.rugby.foundation.game1.server.factory.ofy.OfyLeaderBoardRowFactory;
import net.rugby.foundation.game1.server.factory.ofy.OfyLeaderboardFactory;
import net.rugby.foundation.game1.server.factory.ofy.OfyLeagueFactory;
import net.rugby.foundation.game1.server.factory.ofy.OfyMatchEntryFactory;
import net.rugby.foundation.game1.server.factory.ofy.OfyMatchStatsFactory;
import net.rugby.foundation.game1.server.factory.ofy.OfyRoundEntryFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class Game1MainModule extends AbstractModule {
	@Override
	 protected void configure() {
		bind(Game1ServiceImpl.class);
		bind(IEntryFactory.class).to(OfyEntryFactory.class);
		bind(IRoundEntryFactory.class).to(OfyRoundEntryFactory.class).in(Singleton.class);
		bind(IMatchEntryFactory.class).to(OfyMatchEntryFactory.class).in(Singleton.class);
		bind(ILeaderboardFactory.class).to(OfyLeaderboardFactory.class);
		bind(ILeaderboardRowFactory.class).to(OfyLeaderBoardRowFactory.class);
		bind(ICoreRuleFactory.class).to(CoreRuleFactory.class);
		bind(IConfigurationFactory.class).to(OfyConfigurationFactory.class);
		bind(ILeagueFactory.class).to(OfyLeagueFactory.class);
		bind(IClubhouseLeagueMapFactory.class).to(OfyClubhouseLeagueMapFactory.class);
		bind(IMatchStatsFactory.class).to(OfyMatchStatsFactory.class);
	}
}

