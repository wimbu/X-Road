/**
 * The MIT License
 * Copyright (c) 2018 Estonian Information System Authority (RIA),
 * Nordic Institute for Interoperability Solutions (NIIS), Population Register Centre (VRK)
 * Copyright (c) 2015-2017 Estonian Information System Authority (RIA), Population Register Centre (VRK)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ee.ria.xroad.common.conf.serverconf.model;

import ee.ria.xroad.common.conf.serverconf.PathGlob;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Endpoint
 */
@Getter
@Setter
public class EndpointType {
    public static final String ANY_METHOD = "*";
    public static final String ANY_PATH = "**";

    @Setter(AccessLevel.NONE)
    private Long id;
    private String serviceCode;
    private String method;
    private String path;
    private boolean generated;

    protected EndpointType() {
        //JPA
    }

    /**
     * Create an endpoint
     * @param serviceCode
     * @param method
     * @param path
     */
    public EndpointType(String serviceCode, String method, String path, boolean generated) {
        if (serviceCode == null || method == null || path == null) {
            throw new IllegalArgumentException("Endpoint parts can not be null");
        }
        this.serviceCode = serviceCode;
        this.method = method;
        this.path = path;
        this.generated = generated;
    }

    public final boolean matches(String anotherMethod, String anotherPath) {
        return (ANY_METHOD.equals(method) || method.equalsIgnoreCase(anotherMethod))
                && (ANY_PATH.equals(path) || PathGlob.matches(path, anotherPath));
    }

    public final boolean isEquivalent(EndpointType other) {
        return other.getServiceCode().equals(serviceCode)
                && other.getMethod().equals(method)
                && other.getPath().equals(path);
    }

    public final boolean isBaseEndpoint() {
        return this.method.equals(ANY_METHOD) && this.path.equals(ANY_PATH);
    }
}
