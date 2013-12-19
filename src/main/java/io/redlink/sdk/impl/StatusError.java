package io.redlink.sdk.impl;

/**
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

	public boolean isAccessible() {
		return accessible;
	}

	public void setAccessible(boolean accessible) {
		this.accessible = accessible;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}
}
