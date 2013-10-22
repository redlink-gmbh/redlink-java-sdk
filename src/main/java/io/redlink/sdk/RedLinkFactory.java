package io.redlink.sdk;

import io.redlink.sdk.impl.DefaultCredentials;
import io.redlink.sdk.impl.analysis.RedLinkAnalysisImpl;
import io.redlink.sdk.impl.content.RedLinkContentImpl;
import io.redlink.sdk.impl.search.RedLinkSearchImpl;
import io.redlink.sdk.impl.vocabulary.RedLinkVocabularyImpl;

/**
 * RedLink SDK Factory
 * 
 * @author sergio.fernandez@redlink.co
 *
 */
public class RedLinkFactory {
	
	public static RedLink.Content createContentClient(String apiKey) {
		return createContentClient(new DefaultCredentials(apiKey));
	}
	
	public static RedLink.Content createContentClient(Credentials credentials) {
		return new RedLinkContentImpl(credentials);
	}
	
	public static RedLink.Search createSearchClient(String apiKey) {
		return createSearchClient(new DefaultCredentials(apiKey));
	}
	
	public static RedLink.Search createSearchClient(Credentials credentials) {
		return new RedLinkSearchImpl(credentials);
	}
	
	public static RedLink.Analysis createEnhanceClient(String apiKey) {
		return createAnalysisClient(new DefaultCredentials(apiKey));
	}
	
	public static RedLink.Analysis createAnalysisClient(Credentials credentials) {
		return new RedLinkAnalysisImpl(credentials);
	}
	
	public static RedLink.Analysis createAnalysisClient(String apiKey) {
		return new RedLinkAnalysisImpl(new DefaultCredentials(apiKey));
	}
	
	public static RedLink.Vocabulary createVocabularyClient(String apiKey) {
		return createVocabularyClient(new DefaultCredentials(apiKey));
	}
	
	public static RedLink.Vocabulary createVocabularyClient(Credentials credentials) {
		return new RedLinkVocabularyImpl(credentials);
	}

}
