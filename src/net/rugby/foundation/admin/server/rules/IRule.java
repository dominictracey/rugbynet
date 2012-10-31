/**
 * 
 */
package net.rugby.foundation.admin.server.rules;

/**
 * @author Dominic Tracey
 * 
 *
 */
public interface IRule<T> {
	void setTarget(T t);
	Boolean test();
}
