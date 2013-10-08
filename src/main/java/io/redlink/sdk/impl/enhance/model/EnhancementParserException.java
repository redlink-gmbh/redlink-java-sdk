package io.redlink.sdk.impl.enhance.model;

/**
 * 
 * @author rafa.haro@redlink.co
 *
 */
public class EnhancementParserException extends Exception {

	private static final long serialVersionUID = 5685855665223872003L;
	
	public EnhancementParserException(String message){
		super(message);
	}
	
	public EnhancementParserException(String message, Throwable throwable){
		super(message, throwable);
	}
	
}
