/**
 * 
 */
package net.rugby.foundation.core.client;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * @author home
 *
 */
public class ServicesFactory {

	public static final CoreServiceAsync rpcService = GWT.create(CoreService.class);

	static
	{
	((ServiceDefTarget) rpcService).setServiceEntryPoint(GWT.getHostPageBaseURL() + CoreServiceAsync.END_POINT);
	}


}
