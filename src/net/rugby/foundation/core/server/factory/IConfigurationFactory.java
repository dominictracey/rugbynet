/**
 * 
 */
package net.rugby.foundation.core.server.factory;

import net.rugby.foundation.model.shared.ICoreConfiguration;

/**
 * @author home
 *
 */
public interface IConfigurationFactory extends ICachingFactory<ICoreConfiguration>{
	public abstract ICoreConfiguration get();
}
