/**
 * 
 */
package net.rugby.foundation.game1.server.factory;

import net.rugby.foundation.game1.shared.IConfiguration;

/**
 * @author home
 *
 */
public interface IConfigurationFactory {
	IConfiguration get();
	IConfiguration put(IConfiguration config);
}
