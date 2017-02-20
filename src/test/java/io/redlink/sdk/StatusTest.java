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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.redlink.sdk.impl.Status;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Status unit tests
 *
 * @author sergio.fernandez@redlink.co
 *
 */
public class StatusTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testParse() throws IOException {
        final String json = "{\n" +
                "    \"accessible\": true,\n" +
                "    \"seconds\": 53,\n" +
                "    \"datasets\": [\n" +
                "        \"test\"\n" +
                "    ],\n" +
                "    \"limit\": \"unlimited\",\n" +
                "    \"owner\": \"0\",\n" +
                "    \"requests\": 9689,\n" +
                "    \"bytes\": 44051,\n" +
                "    \"analyses\": [\n" +
                "        \"test\"\n" +
                "    ]\n" +
                "}";
        final Status status = mapper.readValue(json, Status.class);
        Assert.assertNotNull(status);
        Assert.assertTrue(status.isAccessible());
        Assert.assertEquals(53, status.getSeconds());
        Assert.assertEquals(1, status.getDatasets().size());
        Assert.assertEquals(-1, status.getLimit());
        Assert.assertEquals(0, status.getOwner());
        Assert.assertEquals(9689, status.getRequests());
        Assert.assertEquals(44051, status.getBytes());
        Assert.assertEquals(1, status.getAnalyses().size());
    }

    @Test
    public void testParseRangeLimits() throws IOException {
        final String json = "{\n" +
                "    \"accessible\": true,\n" +
                "    \"seconds\": 1857853,\n" +
                "    \"datasets\": [\n" +
                "        \"test\"\n" +
                "    ],\n" +
                "    \"limit\": \"unlimited\",\n" +
                "    \"owner\": \"0\",\n" +
                "    \"requests\": 3092426,\n" +
                "    \"bytes\": 2971455451,\n" +
                "    \"analyses\": [\n" +
                "        \"test\"\n" +
                "    ]\n" +
                "}";
        final Status status = mapper.readValue(json, Status.class);
        Assert.assertNotNull(status);
        Assert.assertTrue(status.isAccessible());
        Assert.assertEquals(1857853, status.getSeconds());
        Assert.assertEquals(1, status.getDatasets().size());
        Assert.assertEquals(-1, status.getLimit());
        Assert.assertEquals(0, status.getOwner());
        Assert.assertEquals(3092426, status.getRequests());
        Assert.assertEquals(2971455451L, status.getBytes());
        Assert.assertEquals(1, status.getAnalyses().size());
    }


}
