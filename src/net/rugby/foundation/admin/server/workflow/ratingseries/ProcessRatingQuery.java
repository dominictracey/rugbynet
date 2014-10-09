package net.rugby.foundation.admin.server.workflow.ratingseries;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.server.factory.IQueryRatingEngineFactory;
import net.rugby.foundation.admin.server.model.IQueryRatingEngine;
import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.model.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingQuery.Status;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

import com.google.appengine.tools.pipeline.ImmediateValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

//@Singleton
public class ProcessRatingQuery extends Job1<Boolean, IRatingQuery> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	private IRatingQueryFactory rqf;

	private IMatchRatingEngineSchemaFactory mresf;

	private IQueryRatingEngineFactory qref;

	private IPlayerRatingFactory prf;

	private ITopTenListFactory ttlf;


	public ProcessRatingQuery() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}

	@Override
	public Value<Boolean> run(IRatingQuery rq) {

		if (injector == null) {
			injector = BPMServletContextListener.getInjectorForNonServlets();
		}

		this.rqf = injector.getInstance(IRatingQueryFactory.class);
		this.mresf = injector.getInstance(IMatchRatingEngineSchemaFactory.class);
		this.qref = injector.getInstance(IQueryRatingEngineFactory.class);
		this.prf = injector.getInstance(IPlayerRatingFactory.class);
		this.ttlf = injector.getInstance(ITopTenListFactory.class);

		// first see if we are re-running, in which case clear out any ratings already in place
		if (!rq.getStatus().equals(Status.NEW)) {
			prf.deleteForQuery(rq);
		}

		rq.setStatus(Status.RUNNING);
		rqf.put(rq);


		// get the engine
		IRatingEngineSchema mres = mresf.getDefault();
		assert (mres != null);
		IQueryRatingEngine mre = qref.get(mres, rq);
		assert (mre != null);

		boolean ok = false;
		
		try {
			ok = mre.setQuery(rq);
			if (ok) {
				mre.generate(mres,rq.getScaleStanding(),rq.getScaleComp(),rq.getScaleTime());
			} else {
				// stats aren't ready
				rq.setStatus(Status.NEW);
				rqf.put(rq);
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem generating ratings", ex);
			rq.setStatus(Status.ERROR);
			rqf.put(rq);
			ok = false;
		}
		
		if (ok) {
			// the engine will set the status to complete if successful
			IRatingQuery procked = rqf.get(rq.getId());
	
			// now create the TTL
			TopTenSeedData data = new TopTenSeedData(rq.getId(), "", "", null, rq.getRoundIds(), 10);
			ITopTenList ttl = ttlf.create(data);
			ttlf.put(ttl);
			
			return new ImmediateValue<Boolean>(procked.getStatus().equals(Status.COMPLETE));
		}
		
		return new ImmediateValue<Boolean>(false);

	}

}
