/**
 * 
 */
package net.rugby.foundation.admin.server.rules;

/**
 * @author home
 *
 */
public class CoreRule<T> implements IRule<T> {

	protected T target;
	protected String log;
	
	public CoreRule(T t)
	{
		this.target = t;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.IRule#setTarget(java.lang.Object)
	 */
	@Override
	public void setTarget(T t) {
		this.target = t;

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.IRule#test()
	 */
	@Override
	public Boolean test() {
		// default is null
		return null;
	}
	@Override
	public String getLog() {
		return log;
	}

}
