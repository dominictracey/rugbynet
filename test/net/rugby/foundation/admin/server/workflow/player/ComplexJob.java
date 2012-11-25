package net.rugby.foundation.admin.server.workflow.player;


import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.Value;

public class ComplexJob extends Job3<Integer, Integer, Integer, Integer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3178247534671557413L;

	public class DiffJob extends Job2<Integer, Integer, Integer> {
		@Override
		public Value<Integer> run(Integer a, Integer b) {
			return immediate(a - b);
		}
	}


	
	@Override
	public Value<Integer> run(Integer x, Integer y, Integer z) {
		DiffJob diffJob = new DiffJob();
		MultJob multJob = new MultJob();
		FutureValue<Integer> r = futureCall(diffJob, immediate(x), immediate(y));
		FutureValue<Integer> s = futureCall(diffJob, immediate(x), immediate(z));
		FutureValue<Integer> t = futureCall(multJob, r, s);
		FutureValue<Integer> u = futureCall(diffJob, t, immediate(2));
		return u;
	}
}
