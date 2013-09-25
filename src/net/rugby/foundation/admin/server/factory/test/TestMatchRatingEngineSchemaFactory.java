package net.rugby.foundation.admin.server.factory.test;

import java.util.List;

import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.shared.IRatingEngineSchema;
import net.rugby.foundation.admin.shared.ScrumMatchRatingEngineSchema;
import net.rugby.foundation.admin.shared.ScrumMatchRatingEngineSchema20130713;

public class TestMatchRatingEngineSchemaFactory implements
		IMatchRatingEngineSchemaFactory {

	@Override
	public IRatingEngineSchema getById(Long id) {
		if (id == 1000L) {
			return new ScrumMatchRatingEngineSchema20130713();
		}
		return null;
	}

	@Override
	public IRatingEngineSchema getDefault() {
		IRatingEngineSchema s = new ScrumMatchRatingEngineSchema20130713();
		((ScrumMatchRatingEngineSchema20130713)s).setCleanBreaksWeight(.2F);
		((ScrumMatchRatingEngineSchema20130713)s).setDefendersBeatenWeight(.2F);
		((ScrumMatchRatingEngineSchema20130713)s).setIsDefault(true);
		((ScrumMatchRatingEngineSchema20130713)s).setKicksWeight(.04f);
		((ScrumMatchRatingEngineSchema20130713)s).setLineoutShareWeight(.2f);
		((ScrumMatchRatingEngineSchema20130713)s).setLineoutsStolenOnOppThrowWeight(.4f);
		((ScrumMatchRatingEngineSchema20130713)s).setLineoutsWonOnThrowWeight(.2f);
		((ScrumMatchRatingEngineSchema20130713)s).setMaulShareWeight(.3f);
		((ScrumMatchRatingEngineSchema20130713)s).setRuckShareWeight(.4f);
		((ScrumMatchRatingEngineSchema20130713)s).setMetersRunWeight(.2f);
		((ScrumMatchRatingEngineSchema20130713)s).setMinutesShareWeight(.1f);
		((ScrumMatchRatingEngineSchema20130713)s).setOffloadsWeight(.3f);
		((ScrumMatchRatingEngineSchema20130713)s).setPassesWeight(.05f);
		((ScrumMatchRatingEngineSchema20130713)s).setPenaltiesConcededWeight(-.3f);
		((ScrumMatchRatingEngineSchema20130713)s).setPointsDifferentialWeight(.2f);
		((ScrumMatchRatingEngineSchema20130713)s).setPointsWeight(.1f);
		((ScrumMatchRatingEngineSchema20130713)s).setRedCardsWeight(-.5f);
		((ScrumMatchRatingEngineSchema20130713)s).setRunsWeight(.1f);
		((ScrumMatchRatingEngineSchema20130713)s).setScrumShareWeight(.5f);
		((ScrumMatchRatingEngineSchema20130713)s).setTacklesMadeWeight(.3f);
		((ScrumMatchRatingEngineSchema20130713)s).setTacklesMissedWeight(-.2f);
		((ScrumMatchRatingEngineSchema20130713)s).setTriesWeight(.4f);
		((ScrumMatchRatingEngineSchema20130713)s).setTryAssistsWeight(.3f);
		((ScrumMatchRatingEngineSchema20130713)s).setTurnoversWeight(-.3f);
		((ScrumMatchRatingEngineSchema20130713)s).setYellowCardsWeight(-.3f);
		
		return s;
	}

	@Override
	public IRatingEngineSchema put(IRatingEngineSchema schema) {
		return schema;
	}

	@Override
	public Boolean delete(IRatingEngineSchema schema) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRatingEngineSchema setAsDefault(IRatingEngineSchema schema) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ScrumMatchRatingEngineSchema> getScrumList() {
		// TODO Auto-generated method stub
		return null;
	}

}
