package net.rugby.foundation.admin.server.workflow.player;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;

public class MultJob extends Job2<Integer, Integer, Integer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8841028546076917649L;

	@Override
	public Value<Integer> run(Integer a, Integer b) {
		return immediate(a*b);
	}
}
