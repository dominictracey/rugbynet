package net.rugby.foundation.admin.server.workflow.matchrating;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IPlayer;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Job5;
import com.google.appengine.tools.pipeline.Job6;
import com.google.appengine.tools.pipeline.NoSuchObjectException;
import com.google.appengine.tools.pipeline.OrphanedObjectException;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.google.appengine.tools.pipeline.Value;

public class FetchPlayerManually extends Job1<IPlayer, IPlayer> {

	private static final long serialVersionUID = 483113213168220162L;
	private IPlayerFactory pf;

	//@Inject
	public FetchPlayerManually(IPlayerFactory pf) {
		this.pf = pf;
	}
	
	
	/**
	 * return IPlayer reference
	 * params String compName
	 * 			Long scrumId
	 * 			Long adminID
	 */		
	@Override
	public Value<IPlayer> run(IPlayer player) {

		Logger.getLogger("FetchedPlayer").log(Level.INFO,"Found Player " + player.getDisplayName());
//		PipelineService service = PipelineServiceFactory.newPipelineService();
//		try {
//			//service.submitPromisedValue(promiseHandle, pf.getByScrumId(14505L));
//		} catch (NoSuchObjectException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return immediate(null);
//		} catch (OrphanedObjectException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return immediate(null);
//		}
		return immediate(player);

	}
	
}
