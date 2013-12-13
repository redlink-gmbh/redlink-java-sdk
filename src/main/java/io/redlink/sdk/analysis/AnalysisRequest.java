package io.redlink.sdk.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.openrdf.rio.RDFFormat;

import com.google.common.base.Optional;

/**
 * 
 * @author rafa.haro@redlink.co
 *
 */
public class AnalysisRequest{
	
	/**
	 * Accepted Input Formats
	 */
	public enum InputFormat{
		TEXT(MediaType.TEXT_PLAIN),
		HTML(MediaType.TEXT_HTML),
		PDF(new MediaType("application", "pdf").toString()),
		OFFICE(new MediaType("application", "doc").toString());
		
		private final String type;
		
		private InputFormat(String type) {
			this.type = type;
		}
		
		public String value(){
			return type;
		}
	}
	
	/**
	 * Accepted Output Formats
	 */
	public enum OutputFormat{
		XML(MediaType.APPLICATION_ATOM_XML),
		JSON(MediaType.APPLICATION_JSON),
		JSONLD(RDFFormat.JSONLD.getDefaultMIMEType()),
		RDFXML(RDFFormat.RDFXML.getDefaultMIMEType()),
		RDFJSON(RDFFormat.RDFJSON.getDefaultMIMEType()),
		TURTLE(RDFFormat.TURTLE.getDefaultMIMEType()),
		NT(RDFFormat.N3.getDefaultMIMEType());
		
		
		private final String type;
		
		private OutputFormat(String type) {
			this.type = type;
		}
		
		public String value(){
			return type;
		}
		
		public static OutputFormat get(String type){
			for(OutputFormat of:OutputFormat.values())
				if(of.type.equals(type))
					return of;
			return null;
		}
	}
	
	/**
	 * Default Input Format
	 */
	private InputFormat inputFormat = InputFormat.TEXT;
	
	/**
	 * Default Output Format
	 */
	private OutputFormat outputFormat = OutputFormat.RDFXML;
	
	/**
	 * Stream to Analyze
	 */
	private Optional<InputStream> content = Optional.absent();
	
	/**
	 * Analysis Service name
	 */
	private Optional<String> analysis = Optional.absent();
	
	/**
	 * Request Content Summary
	 */
	private boolean summary = false;
	
	/**
	 * true -> Content is a String
	 * false -> Content is a File
	 */
	private boolean isContentString = true;

	public String getInputFormat(){
		return inputFormat.value();
	}
	
	public String getOutputFormat(){
		return outputFormat.type;
	}
	
	public InputStream getContent(){
		return content.get();
	}
	
	public String getAnalysis(){
		return analysis.get();
	}
	
	public boolean getSummary(){
		return summary;
	}
	
	public boolean isContentString(){
		return isContentString;
	}
	/**
	 * Request Factory 
	 */
	public static class AnalysisRequestBuilder{

		private final AnalysisRequest request = new AnalysisRequest();
		
		public AnalysisRequestBuilder setInputFormat(InputFormat inputFormat){
			this.request.inputFormat = inputFormat;
			return this;
		}
		
		public AnalysisRequestBuilder setOutputFormat(OutputFormat outputFormat){
			this.request.outputFormat = outputFormat;
			return this;
		}
		
		public AnalysisRequestBuilder setAnalysis(String analysis){
			this.request.analysis = Optional.of(analysis);
			return this;
		}
		
		public AnalysisRequestBuilder setSummary(boolean summary){
			this.request.summary = summary;
			return this;
		}
		
		public AnalysisRequestBuilder setContent(String content){
			// Assuming UTF-8
			this.request.content = Optional.of(IOUtils.toInputStream(content));
			this.request.isContentString = true;
			return this;
		}
		
		public AnalysisRequestBuilder setContent(String content, String encoding) throws IOException{
			this.request.content = Optional.of(IOUtils.toInputStream(content,encoding));
			this.request.isContentString = true;
			return this;
		}

		public AnalysisRequestBuilder setContent(File file) throws FileNotFoundException{
			this.request.content = Optional.of((InputStream) new FileInputStream(file));
			this.request.isContentString = false;
			return this;
		}
		
		public AnalysisRequest build(){
			return request;
		}
	}
	
	public static AnalysisRequestBuilder builder(){
		return new AnalysisRequestBuilder();
	}
}
