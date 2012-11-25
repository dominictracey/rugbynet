package net.rugby.foundation.admin.server.workflow.player;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.PromisedValue;
import com.google.appengine.tools.pipeline.Value;

public class ExternalAgentJob extends Job1<Integer, String> {
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	  public Value<Integer> run(String userEmail) {
	    // Invoke ComplexJob on three promised values
	    PromisedValue<Integer> x = newPromise(Integer.class);
	    PromisedValue<Integer> y = newPromise(Integer.class);
	    PromisedValue<Integer> z = newPromise(Integer.class);
	    ComplexJob cj = new ComplexJob();
	    FutureValue<Integer> intermediate = futureCall(cj, x, y, z);
	      
	    // Kick off the process of retrieving the data from the external agent
	    getIntFromUser("Please give 1st int", userEmail, x.getHandle());
	    getIntFromUser("Please give 2nd int", userEmail, y.getHandle());
	    getIntFromUser("Please give 3rd int", userEmail, z.getHandle());

	    // Send the user the intermediate result and ask for one more integer
	    FutureValue<Integer> oneMoreInt = futureCall(new PromptJob(), intermediate, immediate(userEmail));

	    // Invoke MultJob on intermediate and oneMoreInt
	    return futureCall(new MultJob(), intermediate, oneMoreInt);
	  }

	  public static void getIntFromUser(String prompt, String userEmail, String promiseHandle) {
		  Logger.getLogger("EAJ").log(Level.INFO, "http://localhost:8888/test.html?pv=" + promiseHandle);
	    // 1. Send the user an e-mail containing the prompt.
	    // 2. Ask user to submit one more integer on some web page.
	    // 3. promiseHandle is a query string argument
	    // 4. Handler for submit invokes submitPromisedValue(promiseHandle, value)
	  }
	}
