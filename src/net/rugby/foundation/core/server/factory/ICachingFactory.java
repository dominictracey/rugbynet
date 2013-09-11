/**
 * 
 */
package net.rugby.foundation.core.server.factory;

import net.rugby.foundation.model.shared.IHasId;

/**
 * @author home
 *
 */
public interface ICachingFactory<T extends IHasId> {
	public abstract T get(Long id);
	public abstract T put(T t);
	public abstract boolean delete(T t);
	public abstract T create();
}
