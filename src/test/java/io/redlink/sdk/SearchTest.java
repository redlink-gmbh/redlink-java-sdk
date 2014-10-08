/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.redlink.sdk;

import io.redlink.sdk.impl.CustomCredentials;
import io.redlink.sdk.impl.search.model.SearchResults;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;

public class SearchTest {

    private static RedLink.Search redlink;

    private static final String TEST_CORE = "test";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Credentials credentials = new CustomCredentials();
        Assume.assumeTrue(credentials.verify());
        redlink = RedLinkFactory.createSearchClient(credentials);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        redlink = null;
    }

    //@Test
    public void search() {
        SearchResults results = redlink.search("example", TEST_CORE);
        Assert.assertNotNull(results);
        //Assert.assertTrue(results.hasNext());
        int n = 0;
        while (results.hasNext()) {
            results.remove();
            n++;
        }
        Assert.assertEquals(results.getTotalResults(), n);
        Assert.assertTrue(results.getTotalResults() <= results.getItemsPerPage());
    }

}
