// Copyright (c) Microsoft Corporation.
// All rights reserved.
//
// This code is licensed under the MIT License.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files(the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions :
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
package com.microsoft.identity.common.internal.net;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Internal class to wrap the raw server response, headers and status code.
 */
public final class HttpResponse {

    private final int mStatusCode;
    private final String mResponseBody;
    private final Map<String, List<String>> mResponseHeaders;
    private Date mDate;

    /**
     * Constructor for {@link HttpResponse}.
     *
     * @param statusCode      The status code from the server response.
     * @param responseBody    Raw response body.
     * @param responseHeaders Response headers from the connection sent to the server.
     */
    public HttpResponse(final int statusCode, final String responseBody,
                        final Map<String, List<String>> responseHeaders) {
        mStatusCode = statusCode;
        mResponseBody = responseBody;
        mResponseHeaders = responseHeaders;
    }

    public HttpResponse(@Nullable final Date date,
                        final int statusCode,
                        @NonNull final String responseBody,
                        @NonNull final Map<String, List<String>> headerFields) {
        mDate = date;
        mStatusCode = statusCode;
        mResponseBody = responseBody;
        mResponseHeaders = headerFields;
    }

    public Date getDate() {
        return mDate;
    }

    /**
     * @return The status code.
     */
    public int getStatusCode() {
        return mStatusCode;
    }

    /**
     * @return The raw server response.
     */
    public String getBody() {
        return mResponseBody;
    }

    /**
     * @return The unmodified Map of response headers.
     * Response headers is set by {@link java.net.HttpURLConnection#getHeaderFields()} which is an unmodified Map.
     */
    public Map<String, List<String>> getHeaders() {
        return mResponseHeaders;
    }

    //CHECKSTYLE:OFF
    // This method is generated. Checkstyle and/or PMD has been disabled.
    // This method *must* be regenerated if the class' structural definition changes through the
    // addition/subtraction of fields.
    @Override
    public String toString() {
        return "HttpResponse{" +
                "mStatusCode=" + mStatusCode +
                ", mResponseBody='" + mResponseBody + '\'' +
                ", mResponseHeaders=" + mResponseHeaders +
                '}';
    }
    //CHECKSTYLE:ON
}