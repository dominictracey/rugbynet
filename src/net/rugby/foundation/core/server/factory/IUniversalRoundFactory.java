package net.rugby.foundation.core.server.factory;

import java.util.List;

import org.joda.time.DateTime;

import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.UniversalRound;

public interface IUniversalRoundFactory {


	List<UniversalRound> lastTwentyUniversalRounds();

	List<UniversalRound> lastYearUniversalRounds();

	UniversalRound get(DateTime rTime);

	UniversalRound get(IRound r);

	UniversalRound get(int ordinal);

	UniversalRound getCurrent();

}
