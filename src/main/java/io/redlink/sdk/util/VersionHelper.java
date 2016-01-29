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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Helper for retrieving the version
 *
 * @author sergio.fernandez@redlink.co
 */
public class VersionHelper {

    public static final Pattern VERSION_PATTERN = Pattern.compile("(\\d)+\\.(\\d)+(\\.\\d+)?(\\-SNAPSHOT)?");
    public static final String VERSION = "1.0"; //fallback

    /**
     * Get artifact current version
     *
     * @return version
     */
    public static String getVersion() {
        String version = null;

        // try to load from maven properties first
        try {
            Properties p = new Properties();
            InputStream is = VersionHelper.class.getResourceAsStream("/META-INF/maven/io.redlink/redlink-sdk-java/pom.properties");
            if (is != null) {
                p.load(is);
                version = p.getProperty("version", "");
            }
        } catch (Exception e) {
            // ignore
        }

        // fallback to using Java API
        if (version == null) {
            Package aPackage = VersionHelper.class.getPackage();
            if (aPackage != null) {
                version = aPackage.getImplementationVersion();
                if (version == null) {
                    version = aPackage.getSpecificationVersion();
                }
            }
        }

        // fallback to read pom.xml on testing
        if (version == null) {
            final MavenXpp3Reader reader = new MavenXpp3Reader();
            try {
                Model model = reader.read(new FileReader(new File("pom.xml")));
                version = model.getVersion();
            } catch (IOException | XmlPullParserException e) {
                // ignore
            }
        }

        return version;
    }

    /**
     * Build a proper api version from the artifact version
     *
     * @return api version
     * @see <a href="http://dev.redlink.io/sdk#introduction">api/sdk versioning</a>
     */
    public static String getApiVersion() {
        final String version = getVersion();
        return formatApiVersion(version);
    }

    /**
     * Build a proper api version
     *
     * @param version raw version
     * @return api version
     * @see <a href="http://dev.redlink.io/sdk#introduction">api/sdk versioning</a>
     */
    protected static String formatApiVersion(String version) {
        if (StringUtils.isBlank(version)) {
            return VERSION;
        } else {
            final Matcher matcher = VERSION_PATTERN.matcher(version);
            if (matcher.matches()) {
                return String.format("%s.%s", matcher.group(1), matcher.group(2));
            } else {
                return VERSION;
            }
        }
    }

}
