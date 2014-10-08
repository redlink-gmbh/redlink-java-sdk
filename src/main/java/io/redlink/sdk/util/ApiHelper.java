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

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper around Maven stuff
 *
 * @author sergio.fernandez@redlink.co
 */
public class ApiHelper {

    public static final Pattern VERSION_PATTERN = Pattern.compile("(\\d)+\\.(\\d)+(\\.\\d+)?\\-([A-Z]+)(\\-SNAPSHOT)?");

    /**
     * Build a proper api version from the artifact version
     *
     * @return api version
     * @see <a href="http://dev.redlink.io/sdk#introduction">api/sdk versioning</a>
     */
    public static String getApiVersion() {
        String version = getApiVersion(ApiHelper.class.getPackage().getImplementationVersion());
        return (StringUtils.isBlank(version) ? "1.0-BETA" : version); //FIXME
    }

    /**
     * Build a proper api version
     *
     * @param version raw version
     * @return api version
     * @see <a href="http://dev.redlink.io/sdk#introduction">api/sdk versioning</a>
     */
    public static String getApiVersion(String version) {
        if (StringUtils.isBlank(version)) {
            return null;
        } else {
            final Matcher matcher = VERSION_PATTERN.matcher(version);
            if (matcher.matches()) {
                if (StringUtils.isBlank(matcher.group(4))) {
                    return String.format("%s.%s", matcher.group(1), matcher.group(2));
                } else {
                    return String.format("%s.%s-%s", matcher.group(1), matcher.group(2), matcher.group(4));
                }
            } else {
                return null;
            }
        }
    }

}
