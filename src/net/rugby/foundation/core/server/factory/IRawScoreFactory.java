package net.rugby.foundation.core.server.factory;

import net.rugby.foundation.model.shared.IRawScore;

public interface IRawScoreFactory extends ICachingFactory<IRawScore> {
	IRawScore getForPMSid(Long pmsId, Long schemaId);
	boolean deleteForPMSid(Long pmsId);
	//List<IRawScore> getForMatchId(Long matchId, Long schemaId);
}
