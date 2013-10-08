package io.redlink.sdk.util;

import org.openrdf.rio.RDFFormat;

public class FormatHelper {
	
	public static String getLabel(RDFFormat format) { 
		if (RDFFormat.TURTLE.equals(format)) {
			return "turtle";
		} else if (RDFFormat.RDFXML.equals(format)) {
			return "xml";
		} else if (RDFFormat.JSONLD.equals(format)) {
			return "json";
		} else if (RDFFormat.RDFJSON.equals(format)) {
			return "json";
		} else if (RDFFormat.N3.equals(format)) {
			return "n3";
		} else {
			return format.getName();
		}
	}

}
