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
package io.redlink.sdk.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Some test to for the version helper
 *
 * @author sergio.fernandez@redlink.co
 */
public class VersionHelperTest {

    @Test
    public void testGetVersion() {
        System.out.println(new File("pom.xml").getAbsolutePath());
        final String version = VersionHelper.getVersion();
        Assert.assertNotNull(version);
        Assert.assertEquals("1.0.4-SNAPSHOT", version);
    }

    @Test
    public void testGetApiVersion() {
        final String apiVersion = VersionHelper.getApiVersion();
        Assert.assertNotNull(apiVersion);
        Assert.assertEquals("1.0", apiVersion);
    }

    @Test
    public void testformatApiVersion() {
        Assert.assertEquals("1.0", VersionHelper.formatApiVersion(null));
        Assert.assertEquals("1.0", VersionHelper.formatApiVersion(""));
        Assert.assertEquals("1.0", VersionHelper.formatApiVersion("foo"));
        Assert.assertEquals("1.0", VersionHelper.formatApiVersion("1"));
        Assert.assertEquals("1.0", VersionHelper.formatApiVersion("1.0"));
        Assert.assertEquals("1.0", VersionHelper.formatApiVersion("1.0-SNAPSHOT"));
        Assert.assertEquals("1.0", VersionHelper.formatApiVersion("1.0.0"));
        Assert.assertEquals("1.0", VersionHelper.formatApiVersion("1.0.3"));
        Assert.assertEquals("1.0", VersionHelper.formatApiVersion("1.0.3-SNAPSHOT"));
        Assert.assertEquals("1.0", VersionHelper.formatApiVersion("1.0.3-foo"));
    }

}
