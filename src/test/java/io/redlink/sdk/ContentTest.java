package io.redlink.sdk;

import io.redlink.sdk.impl.CustomCredentials;
import io.redlink.sdk.impl.content.model.ContentItem;

import java.util.Random;
import java.util.UUID;

import net._01001111.text.LoremIpsum;

import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

public class ContentTest {
	
	private static RedLink.Content redlink;
	
	private static LoremIpsum loremipsum;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Credentials credentials = new CustomCredentials();
		Assume.assumeTrue(credentials.verify());
		redlink = RedLinkFactory.createContentClient(credentials);
		loremipsum = new LoremIpsum();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		redlink = null;
		loremipsum = null;
	}
	
	private String generateRandomContent() {
		Random random = new Random();
//		int words = random.nextInt(30) + 20;
//		StringBuilder sb = new StringBuilder();
//		for (int i=0; i < words; i++) {
//			sb.append(RandomStringUtils.randomAlphabetic(random.nextInt(12) + 2));
//			sb.append(" ");
//		}
//		return sb.toString();
		return loremipsum.paragraphs(random.nextInt(9) + 1, false);
	}
	
	@Test
	public void fullContentCycleTests() {
		ContentItem content = redlink.createContent(generateRandomContent());
		Assert.assertNotNull(content);
		Assert.assertNotNull(content.getId());
		String id = content.getId();
		Assert.assertNotNull(content.getUri());
		Assert.assertTrue(StringUtils.isNotBlank(redlink.getContentStatus(id)));
		Assert.assertTrue(redlink.deleteContent(id));
		Assert.assertFalse(redlink.deleteContent(id));
	}
	
	@Test
	public void createContent() {
		ContentItem content = redlink.createContent(generateRandomContent());
		Assert.assertNotNull(content);
		Assert.assertNotNull(content.getId());
		Assert.assertNotNull(content.getUri());
	}
	
	@Test
	public void createContentWithId() {
		String id = UUID.randomUUID().toString();
		ContentItem content = redlink.createContent(id, generateRandomContent());
		Assert.assertNotNull(content);
		Assert.assertNotNull(content.getId());
		Assert.assertEquals(id, content.getId());
		Assert.assertNotNull(content.getUri());
	}
	
	@Test
	public void createContentWithDuplicatedId() {
		String id = UUID.randomUUID().toString();
		ContentItem content = redlink.createContent(id, generateRandomContent());
		Assert.assertNotNull(content);
		Assert.assertNotNull(content.getId());
		Assert.assertNotNull(content.getUri());
		
		//ContentItem content2 = redlink.createContent(id, generateRandomContent());
	}
	
}
