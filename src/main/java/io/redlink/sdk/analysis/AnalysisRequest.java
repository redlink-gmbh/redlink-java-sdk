package io.redlink.sdk.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;

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
	public static enum InputFormat{
		TEXT(MediaType.TEXT_PLAIN_TYPE),
		HTML(MediaType.TEXT_HTML_TYPE),
		PDF(new MediaType("application", "pdf")),
		OFFICE(new MediaType("application", "doc"));
		
		private final MediaType type;
		
		private InputFormat(MediaType type) {
			this.type = type;
		}
		
		public MediaType value(){
			return type;
		}
	}
	
	/**
	 * Accepted Output Formats
	 */
	public static enum OutputFormat{
		XML(MediaType.APPLICATION_ATOM_XML_TYPE),
		JSON(MediaType.APPLICATION_JSON_TYPE),
		JSONLD(new MediaType("application", "ld+json")),
		RDFXML(new MediaType("application", "rdf+xml")),
		RDFJSON(new MediaType("application", "rdf+json")),
		TURTLE(new MediaType("text", "turtle")),
		NT(new MediaType("text", "rdf+n3"));
		
		
		private final MediaType type;
		
		private OutputFormat(MediaType type) {
			this.type = type;
		}
		
		public MediaType value(){
			return type;
		}
		
		public static OutputFormat get(String type){
			for(OutputFormat of:OutputFormat.values())
				if(of.type.toString().equals(type))
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
	private OutputFormat outputFormat = OutputFormat.TURTLE;
	
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
	private boolean summary = true;
	
	/**
	 * Request Entities' thumnails/depiction
	 */
	private boolean thumbnail = true;
	
	/**
	 * true -> Content is a String
	 * false -> Content is a File
	 */
	private boolean isContentString = true;

	public String getInputFormat(){
		return inputFormat.name();
	}
	
	public MediaType getInputMediaType(){
		return inputFormat.type;
	}
	
	public String getOutputFormat(){
		return outputFormat.name();
	}
	
	public MediaType getOutputMediaType(){
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
	
	public boolean getThumbnail(){
		return thumbnail;
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
		
		public AnalysisRequestBuilder setSummaries(boolean summary){
			this.request.summary = summary;
			return this;
		}
		
		public AnalysisRequestBuilder setThumbnails(boolean thumbnail){
			this.request.thumbnail = thumbnail;
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
