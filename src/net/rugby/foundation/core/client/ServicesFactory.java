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
		// The core service should be in servername/Core/
		// 
		String serverPart = GWT.getHostPageBaseURL().substring(0, GWT.getHostPageBaseURL().indexOf("/", 9));
		((ServiceDefTarget) rpcService).setServiceEntryPoint(serverPart + "/" + CoreServiceAsync.END_POINT);
	}


}
