package io.redlink.sdk.impl.analysis.model;


/**
 * Text annotation, selects pieces of parsed textual content by using the following properties
 * 
 * @author sergio.fernandez@redlink.co
 * @author rafa.haro@redlink.co
 * @see https://stanbol.apache.org/docs/trunk/components/enhancer/enhancementstructure#fisetextannotation
 *
 */
public final class TextAnnotation extends Enhancement {
	
	// properties
	private int starts = 0; // http://fise.iks-project.eu/ontology/start
	private int ends = 0; // http://fise.iks-project.eu/ontology/end
	private String selectedText = null; // http://fise.iks-project.eu/ontology/selected-text
	private String selectionContext = null; // http://fise.iks-project.eu/ontology/selection-context
	
	public TextAnnotation(){
		super();
	}

	/**
	 * Returns the position of the text where the annotation starts
	 * 
	 * @return
	 */
	public int getStarts() {
		return starts;
	}

	void setStarts(int starts) {
		this.starts = starts;
	}

	/**
	 * Returns the position of the text where the annotation ends
	 * 
	 * @return
	 */
	public int getEnds() {
		return ends;
	}

	void setEnds(int ends) {
		this.ends = ends;
	}

	/**
	 * Returns the matched/extracted piece of text from the analyzed content
	 * 
	 * @return
	 */
	public String getSelectedText() {
		return selectedText;
	}

	void setSelectedText(String selectedText) {
		this.selectedText = selectedText;
	}

	/**
	 * Returns a surrounding context of the matched text
	 * 
	 * @return
	 */
	public String getSelectionContext() {
		return selectionContext;
	}

	void setSelectionContext(String selectionContext) {
		this.selectionContext = selectionContext;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ends;
		result = prime * result + starts;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TextAnnotation other = (TextAnnotation) obj;
		if (ends != other.ends)
			return false;
		if (starts != other.starts)
			return false;
		return true;
	}
	
	
}
