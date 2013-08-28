/**
 * 
 */
package net.rugby.foundation.core.server.factory;

import net.rugby.foundation.model.shared.HasId;

/**
 * @author home
 *
 */
public interface ICachingFactory<T extends HasId> {
	public abstract T get(Long id);
	public abstract T put(T t);
	public abstract boolean delete(T t);
	public abstract T create();
}
