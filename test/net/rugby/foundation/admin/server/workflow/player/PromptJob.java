package net.rugby.foundation.admin.server.workflow.player;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.PromisedValue;
import com.google.appengine.tools.pipeline.Value;

public class PromptJob extends Job2<Integer, Integer, String> {
	  /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	@Override
	  public Value<Integer> run(Integer intermediate, String userEmail) {
	    String prompt = "The intermediate result is " + intermediate + "." + " Please give one more int";
	    PromisedValue<Integer> oneMoreInt = newPromise(Integer.class);
	    ExternalAgentJob.getIntFromUser(prompt, userEmail, oneMoreInt.getHandle());
	    return oneMoreInt;
	  }
	}