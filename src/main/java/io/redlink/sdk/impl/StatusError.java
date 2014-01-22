package io.redlink.sdk.impl;

/**
 * RedLink's user Application Status Error data
 * 
 * @author rafa.haro@redlink.co
 *
 */
public class StatusError {
	
	private boolean accessible;
	private String reason;
	private String message;
	private int error;
	
	public StatusError(){
		
	}

	/**
     * Returns true if the Application is accessible
     * 
     * @return  
     */
	public boolean isAccessible() {
		return accessible;
	}

	/**
	 * Returns status error reason
	 * 
	 * @return
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * Returns status error message
	 * 
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns status error code
	 * 
	 * @return
	 */
	public int getError() {
		return error;
	}
}
