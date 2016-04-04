package net.rugby.foundation.admin.server.workflow;

public class RetryRequestException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6999694787940569477L;

	public RetryRequestException(String message) {
		super(message);
	}
}
