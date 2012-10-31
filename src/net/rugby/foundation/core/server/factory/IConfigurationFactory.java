/**
 * 
 */
package net.rugby.foundation.core.server.factory;

import net.rugby.foundation.model.shared.ICoreConfiguration;

/**
 * @author home
 *
 */
public interface IConfigurationFactory {
	public abstract ICoreConfiguration get();
	public abstract ICoreConfiguration put(ICoreConfiguration conf);
}
