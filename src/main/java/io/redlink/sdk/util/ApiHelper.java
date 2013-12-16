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

    public static final Pattern VERSION_PATTERN= Pattern.compile("(\\d)+\\.(\\d)+(\\.\\d+)?\\-([A-Z]+)(\\-SNAPSHOT)?");

    /**
     * Build a proper api version from the artifact version
     *
     * @return api version
     * @see <a href="http://dev.redlink.io/sdk#introduction">api/sdk versioning</a>
     */
    public static String getApiVersion() {
        return getApiVersion(ApiHelper.class.getPackage().getImplementationVersion());
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
            }  else {
                return null;
            }
        }
    }

}
