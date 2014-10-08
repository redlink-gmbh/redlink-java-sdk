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
package io.redlink.sdk.impl.analysis.model;

/**
 * Enhancement Structure parsing error exception
 *
 * @author rafa.haro@redlink.co
 */
public class EnhancementParserException extends Exception {

    private static final long serialVersionUID = 5685855665223872003L;

    public EnhancementParserException(String message) {
        super(message);
    }

    public EnhancementParserException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
