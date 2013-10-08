package io.redlink.sdk.impl.analysis.model;


/**
 * Text annotation, selects portions parsed textual content by using the following properties
 * 
 * @author sergio.fernandez@redlink.co
 * @see https://stanbol.apache.org/docs/trunk/components/enhancer/enhancementstructure#fisetextannotation
 *
 */
public final class TextAnnotation extends Enhancement {
	
	// properties
	private String type = null; // http://purl.org/dc/terms/type
	private int starts = 0; // http://fise.iks-project.eu/ontology/start
	private int ends = 0; // http://fise.iks-project.eu/ontology/end
	private String selectedText = null; // http://fise.iks-project.eu/ontology/selected-text
	private String selectionContext = null; // http://fise.iks-project.eu/ontology/selection-context
	
	public TextAnnotation(String uri){
		super(uri);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getStarts() {
		return starts;
	}

	public void setStarts(int starts) {
		this.starts = starts;
	}

	public int getEnds() {
		return ends;
	}

	public void setEnds(int ends) {
		this.ends = ends;
	}

	public String getSelectedText() {
		return selectedText;
	}

	public void setSelectedText(String selectedText) {
		this.selectedText = selectedText;
	}

	public String getSelectionContext() {
		return selectionContext;
	}

	public void setSelectionContext(String selectionContext) {
		this.selectionContext = selectionContext;
	}

}
