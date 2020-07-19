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

package com.microsoft.identity.common;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.microsoft.identity.common.internal.net.UrlConnectionHttpClient;
import com.microsoft.identity.common.internal.net.HttpRequest;
import com.microsoft.identity.common.internal.net.HttpResponse;
import com.microsoft.identity.common.internal.net.HttpUrlConnectionFactory;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownServiceException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static com.microsoft.identity.common.MockUtil.getMockedConnectionWithFailureResponse;
import static com.microsoft.identity.common.internal.net.HttpUrlConnectionFactory.addMockedConnection;
import static com.microsoft.identity.common.internal.net.HttpUrlConnectionFactory.clearMockedConnectionQueue;
import static com.microsoft.identity.common.internal.net.HttpUrlConnectionFactory.getMockedConnectionCountInQueue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for {@link HttpRequest}.
 */
@RunWith(AndroidJUnit4.class)
public final class HttpRequestTest {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    @After
    public void tearDown() {
        HttpUrlConnectionFactory.clearMockedConnectionQueue();
    }

    /**
     * Verify the expected exception is thrown when sending get request with null url.
     */
    @Test(expected = NullPointerException.class)
    public void testNullRequestUrl() throws IOException {
        HttpRequest.sendGet(null, Collections.<String, String>emptyMap());
    }

    /**
     * Verify that HTTP GET succeed and no retry happens.
     */
    @Test
    public void testHttpGetSucceed() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.GET, false, false);
    }

    /**
     * Verify that HTTP GET succeed and no retry happens.
     */
    @Test
    public void testHttpGetSucceedSpecific() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.GET, true, false);
    }

    /**
     * Verify that HTTP GET succeed and no retry happens.
     */
    @Test
    public void testHttpGetSucceedNoRetry() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.GET, false, true);
    }

    /**
     * Verify that HTTP GET succeed and no retry happens.
     */
    @Test
    public void testHttpGetSucceedSpecificNoRetry() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.GET, true, true);
    }

    /**
     * Verify that HTTP POST succeeds and no retry happens.
     */
    @Test
    public void testHttpPostSucceed() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.HEAD, false, false);
    }

    /**
     * Verify that HTTP POST succeeds and no retry happens.
     */
    @Test
    public void testHttpPostSucceedSpecific() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.HEAD, true, false);
    }

    /**
     * Verify that HTTP POST succeeds and no retry happens.
     */
    @Test
    public void testHttpPostSucceedNoRetry() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.HEAD, false, true);
    }

    /**
     * Verify that HTTP POST succeeds and no retry happens.
     */
    @Test
    public void testHttpPostSucceedSpecifcNoRetry() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.HEAD, true, true);
    }

    /**
     * Verify that HTTP HEAD succeeds and no retry happens.
     */
    @Test
    public void testHttpHeadSucceed() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.HEAD, false, false);
    }

    /**
     * Verify that HTTP HEAD succeeds and no retry happens.
     */
    @Test
    public void testHttpHeadSucceedSpecific() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.HEAD, true, false);
    }

    /**
     * Verify that HTTP HEAD succeeds and no retry happens.
     */
    @Test
    public void testHttpHeadSucceedNoRetry() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.HEAD, false, true);
    }

    /**
     * Verify that HTTP HEAD succeeds and no retry happens.
     */
    @Test
    public void testHttpHeadSucceedSpecificNoRetry() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.HEAD, true, true);
    }

    /**
     * Verify that HTTP PUT succeeds and no retry happens.
     */
    @Test
    public void testHttpPutSucceed() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.PUT, false, false);
    }

    /**
     * Verify that HTTP PUT succeeds and no retry happens.
     */
    @Test
    public void testHttpPutSucceedSpecific() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.PUT, true, false);
    }

    /**
     * Verify that HTTP PUT succeeds and no retry happens.
     */
    @Test
    public void testHttpPutSucceedNoRetry() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.PUT, false, true);
    }

    /**
     * Verify that HTTP PUT succeeds and no retry happens.
     */
    @Test
    public void testHttpPutSucceedSpecificNoRetry() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.PUT, true, true);
    }

    /**
     * Verify that HTTP DELETE succeeds and no retry happens.
     */
    @Test
    public void testHttpDeleteSucceed() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.DELETE, false, false);
    }

    /**
     * Verify that HTTP DELETE succeeds and no retry happens.
     */
    @Test
    public void testHttpDeleteSucceedSpecific() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.DELETE, true, false);
    }

    /**
     * Verify that HTTP DELETE succeeds and no retry happens.
     */
    @Test
    public void testHttpDeleteSucceedNoRetry() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.DELETE, false, true);
    }

    /**
     * Verify that HTTP DELETE succeeds and no retry happens.
     */
    @Test
    public void testHttpDeleteSucceedSpecificNoRetry() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.DELETE, true, true);
    }

    /**
     * Verify that HTTP TRACE succeeds and no retry happens.
     */
    @Test
    public void testHttpTraceSucceed() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.TRACE, false, false);
    }

    /**
     * Verify that HTTP TRACE succeeds and no retry happens.
     */
    @Test
    public void testHttpTraceSucceedSpecific() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.TRACE, true, false);
    }

    /**
     * Verify that HTTP TRACE succeeds and no retry happens.
     */
    @Test
    public void testHttpTraceSucceedNoRetry() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.TRACE, false, true);
    }

    /**
     * Verify that HTTP TRACE succeeds and no retry happens.
     */
    @Test
    public void testHttpTraceSucceedSpeicficNoRetry() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.TRACE, true, true);
    }

    /**
     * Verify that HTTP OPTIONS succeeds and no retry happens.
     */
    @Test
    public void testHttpOptionsSucceed() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.OPTIONS, false, false);
    }

    /**
     * Verify that HTTP OPTIONS succeeds and no retry happens.
     */
    @Test
    public void testHttpOptionsSucceedSpecific() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.OPTIONS, true, false);
    }

    /**
     * Verify that HTTP OPTIONS succeeds and no retry happens.
     */
    @Test
    public void testHttpOptionsSucceedNoRetry() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.OPTIONS, false, true);
    }

    /**
     * Verify that HTTP OPTIONS succeeds and no retry happens.
     */
    @Test
    public void testHttpOptionsSucceedSpecificNoRetry() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.OPTIONS, true, true);
    }

    /**
     * Verify that HTTP PATCH succeeds and no retry happens.
     */
    @Test
    public void testHttpPatchSucceed() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.PATCH, false, false);
    }

    /**
     * Verify that HTTP PATCH succeeds and no retry happens.
     */
    @Test
    public void testHttpPatchSucceedNoRetries() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.PATCH, false, true);
    }

    /**
     * Verify that HTTP PATCH succeeds and no retry happens.
     */
    @Test
    public void testHttpPatchSucceedSpecific() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.PATCH, true, false);
    }

    /**
     * Verify that HTTP PATCH succeeds and no retry happens.
     */
    @Test
    public void testHttpPatchSucceedSpecificNoRetries() throws Exception {
        testHttpMethodSucceed(HttpTestMethod.PATCH, true, true);
    }

    /**
     * Verify that when an HTTP method succeeds, no retry happens.
     */
    private void testHttpMethodSucceed(HttpTestMethod method, boolean specific, boolean retries) throws Exception {
        // prepare the connection, only one connection will be made.
        final HttpURLConnection mockedSuccessConnection =
                MockUtil.getMockedConnectionWithSuccessResponse(
                        getSuccessResponse()
                );
        mockRequestBody(mockedSuccessConnection);
        addMockedConnection(mockedSuccessConnection);

        try {
            assertEquals(1, getMockedConnectionCountInQueue());
            final HttpResponse response = sendMethod(method, specific, retries);
            verifySuccessHttpResponse(response);
        } catch (final IOException e) {
            fail();
        }

        assertEquals(0, getMockedConnectionCountInQueue());
        final InOrder inOrder = Mockito.inOrder(mockedSuccessConnection);
        // default times for verify is 1.
        inOrder.verify(mockedSuccessConnection).getInputStream();
        inOrder.verify(mockedSuccessConnection, Mockito.never()).getErrorStream();
        inOrder.verify(mockedSuccessConnection).getResponseCode();
        inOrder.verify(mockedSuccessConnection).getHeaderFields();
        inOrder.verifyNoMoreInteractions();
    }

    private HttpResponse sendMethod(HttpTestMethod method, boolean specific, boolean retries) throws Exception {
        final HttpResponse response;
        switch ((specific ? 2 : 0) | (retries ? 0 : 1)) {
            case 0:
            response = sendWithMethod(method);
            break;
            case 1:
                response = sendWithMethodNoRetry(method);
                break;
            case 2:
                response = sendSpecific(method);
                break;
            case 3:
                response = sendSpecificWithoutRetry(method);
                break;
            default:
                throw new IllegalStateException("Unknown request pattern requested");
        }
        return response;
    }

    /**
     * Verify the correct response is returned if first network call fails with internal error,
     * and retry succeeds.
     */
    private void testHttpMethodFailedWithStatusCodeRetrySucceed(HttpTestMethod method, int code) throws Exception {
        // Set up two connections, the first is failed with 500, the second one succeeds.
        final HttpURLConnection firstConnection =
                getMockedConnectionWithFailureResponse(
                        code,
                        getErrorResponse()
                );
        mockRequestBody(firstConnection);

        final HttpURLConnection secondConnection =
                MockUtil.getMockedConnectionWithSuccessResponse(getSuccessResponse());
        mockRequestBody(secondConnection);

        addMockedConnection(firstConnection);
        addMockedConnection(secondConnection);

        try {
            assertEquals(2, getMockedConnectionCountInQueue());
            final HttpResponse response = sendWithMethod(method);
            verifySuccessHttpResponse(response);
        } catch (final IOException e) {
            fail();
        }

        assertEquals(0, getMockedConnectionCountInQueue());

        final InOrder inOrder = Mockito.inOrder(firstConnection, secondConnection);
        inOrder.verify(firstConnection).getInputStream();
        inOrder.verify(firstConnection).getErrorStream();
        inOrder.verify(firstConnection).getResponseCode();
        // no HttpResponse is created, no need to verify getHeaderFields.

        inOrder.verify(secondConnection).getInputStream();
        inOrder.verify(secondConnection, Mockito.never()).getErrorStream();
        inOrder.verify(secondConnection).getResponseCode();
        inOrder.verify(secondConnection).getHeaderFields();

        inOrder.verifyNoMoreInteractions();
    }

    @Test(expected = UnknownServiceException.class)
    public void testPostFailsWithTwoErrors500() throws Exception {
        testHttpMethodFailedWithStatusCodeRetryFails(HttpTestMethod.POST, 500);
    }

    /**
     * Verify the correct response is returned if first network call fails with internal error,
     * and retry succeeds.
     */
    private void testHttpMethodFailedWithStatusCodeRetryFails(HttpTestMethod method, int code) throws Exception {
        // Set up three connections, all with the specified code. all failures.
            final HttpURLConnection firstConnection =
                    getMockedConnectionWithFailureResponse(
                            code,
                            getErrorResponse()
                    );
            mockRequestBody(firstConnection);

            final HttpURLConnection secondConnection =
                    getMockedConnectionWithFailureResponse(
                            code,
                            getErrorResponse()
                    );
            mockRequestBody(secondConnection);

            final HttpURLConnection thirdConnection =
                    getMockedConnectionWithFailureResponse(
                            code,
                            getErrorResponse()
                    );
            mockRequestBody(thirdConnection);
            addMockedConnection(firstConnection);
            addMockedConnection(secondConnection);
            addMockedConnection(thirdConnection);

            assertEquals(3, getMockedConnectionCountInQueue());
        try {
            final HttpResponse response = sendWithMethod(method);
            fail();
        } finally {

            assertEquals(1, getMockedConnectionCountInQueue());

            final InOrder inOrder = Mockito.inOrder(firstConnection, secondConnection);
            inOrder.verify(firstConnection).getInputStream();
            inOrder.verify(firstConnection).getErrorStream();
            inOrder.verify(firstConnection).getResponseCode();
            inOrder.verify(firstConnection).getDate();
            inOrder.verify(firstConnection).getHeaderFields();
            inOrder.verify(secondConnection).getInputStream();
            inOrder.verify(secondConnection).getErrorStream();
            inOrder.verify(secondConnection).getResponseCode();
            inOrder.verify(secondConnection).getDate();
            inOrder.verify(secondConnection).getHeaderFields();


            inOrder.verifyNoMoreInteractions();
        }
    }

    /**
     * Verify that if the client is configured without retry, no retry happens.
     */
    @Test
    public void testNoRetryDoesNotRetry() throws Exception {
        for (int code: Arrays.asList(500, 503, 504, 404, 499)) {
            for (HttpTestMethod method : HttpTestMethod.values()) {
                testHttpMethodFailedWithStatusCodeWithoutRetryNoRetryHappens(method, code);
            }
        }
    }

    private void testHttpMethodFailedWithStatusCodeWithoutRetryNoRetryHappens(HttpTestMethod method, int code) throws Exception {
        // Set up two connections, the first is failed with 500, the second one does not occur.
        final HttpURLConnection firstConnection =
                getMockedConnectionWithFailureResponse(
                        code,
                        getErrorResponse()
                );
        mockRequestBody(firstConnection);

        final HttpURLConnection secondConnection =
                MockUtil.getMockedConnectionWithSuccessResponse(getSuccessResponse());
        mockRequestBody(secondConnection);

        addMockedConnection(firstConnection);
        addMockedConnection(secondConnection);

        try {
            assertEquals(2, getMockedConnectionCountInQueue());
            final HttpResponse response = sendMethod(method, false, false);
            assertEquals(code, response.getStatusCode());
        } catch (final IOException e) {
            fail();
        }

        assertEquals(1, getMockedConnectionCountInQueue());

        final InOrder inOrder = Mockito.inOrder(firstConnection, secondConnection);
        inOrder.verify(firstConnection).getInputStream();
        inOrder.verify(firstConnection).getErrorStream();
        inOrder.verify(firstConnection).getResponseCode();
        inOrder.verify(firstConnection).getDate();
        inOrder.verify(firstConnection).getHeaderFields();
        inOrder.verifyNoMoreInteractions();

        clearMockedConnectionQueue();
    }
    /**
     * Verify the correct response is returned if first network call fails with internal error,
     * and retry succeeds.
     */
    @Test
    public void testHttpPostFailedWith500RetrySucceed() throws Exception {
        testHttpMethodFailedWithStatusCodeRetrySucceed(HttpTestMethod.POST, 500);
    }

    /**
     * Verify the correct response is returned if first network call fails with internal error,
     * and retry succeeds.
     */
    @Test
    public void testHttpGetFailedWith500RetrySucceed() throws Exception {
        testHttpMethodFailedWithStatusCodeRetrySucceed(HttpTestMethod.GET, 500);
    }
    /**
     * Verify the correct response is returned if first network call fails with internal error,
     * and retry succeeds.
     */
    @Test
    public void testHttpPutFailedWith500RetrySucceed() throws Exception {
        testHttpMethodFailedWithStatusCodeRetrySucceed(HttpTestMethod.PUT, 500);
    }
    /**
     * Verify the correct response is returned if first network call fails with internal error,
     * and retry succeeds.
     */
    @Test
    public void testHttpOptionsFailedWith500RetrySucceed() throws Exception {
        testHttpMethodFailedWithStatusCodeRetrySucceed(HttpTestMethod.OPTIONS, 500);
    }

    /**
     * Verify that the initial post request failed with
     * {@link HttpURLConnection#HTTP_UNAVAILABLE} and retry succeeds.
     */
    @Test
    public void testHttpPostFailedWith503RetrySucceed() throws Exception {
        testHttpMethodFailedWithStatusCodeRetrySucceed(HttpTestMethod.POST, 503);
    }

    /**
     * Verify that the initial get request failed with {@link HttpURLConnection#HTTP_GATEWAY_TIMEOUT} and retry
     * succeeds.
     */
    @Test
    public void testHttpGetFailedWith504RetrySucceed() throws Exception {
        testHttpMethodFailedWithStatusCodeRetrySucceed(HttpTestMethod.GET, 504);
    }

    /**
     * Verify that the initial post request failed with {@link SocketTimeoutException} and retry
     * succeeds.
     */
    private void testHttpMethodFailedWithSocketTimeoutRetrySucceed(HttpTestMethod method) throws Exception {
        // Set up two connections, the first is failed with SocketTimeout, the second one succeeds.
        final HttpURLConnection firstConnection = MockUtil.getMockedConnectionWithSocketTimeout();
        mockRequestBody(firstConnection);

        final HttpURLConnection secondConnection = MockUtil.getMockedConnectionWithSuccessResponse(
                getSuccessResponse());
        mockRequestBody(secondConnection);

        addMockedConnection(firstConnection);
        addMockedConnection(secondConnection);

        try {
            assertEquals(2, getMockedConnectionCountInQueue());
            final HttpResponse response = sendWithMethod(method);
            verifySuccessHttpResponse(response);
        } catch (final IOException e) {
            fail();
        }

        assertEquals(0, getMockedConnectionCountInQueue());
        final InOrder inOrder = Mockito.inOrder(firstConnection, secondConnection);
        inOrder.verify(firstConnection).getInputStream();
        inOrder.verify(firstConnection, Mockito.never()).getErrorStream();
        inOrder.verify(firstConnection, Mockito.never()).getResponseCode();

        inOrder.verify(secondConnection).getInputStream();
        inOrder.verify(secondConnection, Mockito.never()).getErrorStream();
        inOrder.verify(secondConnection).getResponseCode();
        inOrder.verify(secondConnection).getHeaderFields();

        inOrder.verifyNoMoreInteractions();
    }

    /**
     * Verify that the initial post request failed with {@link SocketTimeoutException} and retry
     * succeeds.
     */
    @Test
    public void testHttpMethodsFailWithSocketTimeoutRetrySucceed() throws Exception {
        for (HttpTestMethod method: HttpTestMethod.values()) {
            testHttpMethodFailedWithSocketTimeoutRetrySucceed(method);
        }
    }

    /**
     * Verify that http get request fails with {@link HttpURLConnection#HTTP_BAD_REQUEST},
     * no retry happens.
     */
    @Test
    public void testHttpGetFailedNoRetry() throws Exception {
        testHttpMethodFailedNoRetry(HttpTestMethod.GET, HttpURLConnection.HTTP_BAD_REQUEST);
    }


    /**
     * Verify that http POST request fails with {@link HttpURLConnection#HTTP_UNAUTHORIZED},
     * no retry happens.
     */
    @Test
    public void testHttpPostFailedNoRetry() throws Exception {
        testHttpMethodFailedNoRetry(HttpTestMethod.POST, HttpURLConnection.HTTP_UNAUTHORIZED);
    }
    @Test
    public void testHttpPutFailedNoRetry() throws Exception {
        testHttpMethodFailedNoRetry(HttpTestMethod.PUT, HttpURLConnection.HTTP_UNAUTHORIZED);
    }
    @Test
    public void testHttpOptionsFailedNoRetry() throws Exception {
        testHttpMethodFailedNoRetry(HttpTestMethod.OPTIONS, HttpURLConnection.HTTP_UNAUTHORIZED);
    }
    @Test
    public void testHttpTraceFailedNoRetry() throws Exception {
        testHttpMethodFailedNoRetry(HttpTestMethod.TRACE, HttpURLConnection.HTTP_UNAUTHORIZED);
    }
    @Test
    public void testHttpPatchtFailedNoRetry() throws Exception {
        testHttpMethodFailedNoRetry(HttpTestMethod.PATCH, HttpURLConnection.HTTP_UNAUTHORIZED);
    }
    @Test
    public void testHttpDeleteFailedNoRetry() throws Exception {
        testHttpMethodFailedNoRetry(HttpTestMethod.DELETE, HttpURLConnection.HTTP_UNAUTHORIZED);
    }
    @Test
    public void testHttpHeadFailedNoRetry() throws Exception {
        testHttpMethodFailedNoRetry(HttpTestMethod.HEAD, HttpURLConnection.HTTP_UNAUTHORIZED);
    }

    /**
     * Verify that http request fails with {@link HttpURLConnection#HTTP_UNAUTHORIZED},
     * no retry happens.
     */
    private void testHttpMethodFailedNoRetry(HttpTestMethod method, int code) throws Exception {
        final HttpURLConnection mockedFailureConnection =
                getMockedConnectionWithFailureResponse(
                        code,
                        getErrorResponse()
                );
        mockRequestBody(mockedFailureConnection);
        addMockedConnection(mockedFailureConnection);

        // send a post request
        try {
            assertEquals(1, getMockedConnectionCountInQueue());

            final HttpResponse response = sendWithMethod(method);
            assertNotNull(response);
            assertEquals(response.getStatusCode(), code);
            assertEquals(response.getBody(), getErrorResponse());
        } catch (final IOException e) {
            fail();
        }

        assertEquals(0, getMockedConnectionCountInQueue());

        final InOrder inOrder = Mockito.inOrder(mockedFailureConnection);
        inOrder.verify(mockedFailureConnection).getInputStream();
        inOrder.verify(mockedFailureConnection).getErrorStream();
        inOrder.verify(mockedFailureConnection).getResponseCode();
        inOrder.verify(mockedFailureConnection).getDate();
        inOrder.verify(mockedFailureConnection).getHeaderFields();
        inOrder.verifyNoMoreInteractions();
    }

    /**
     * Verify that http get request fails with {@link HttpURLConnection#HTTP_BAD_METHOD}
     * and no response body, no retry happens.
     */
    public void testHttpMethodFailedNoRetryNoResponseBody(HttpTestMethod method) throws Exception {
        final HttpURLConnection mockedFailureConnection =
                getMockedConnectionWithFailureResponse(
                        HttpURLConnection.HTTP_BAD_METHOD,
                        null
                );
        addMockedConnection(mockedFailureConnection);

        try {
            assertEquals(1, getMockedConnectionCountInQueue());
            final HttpResponse response = sendWithMethod(method);
            assertNotNull(response);
            assertEquals(response.getStatusCode(), HttpURLConnection.HTTP_BAD_METHOD);
            assertTrue(response.getBody().isEmpty());
        } catch (final IOException e) {
            fail();
        }

        assertEquals(0, getMockedConnectionCountInQueue());

        final InOrder inOrder = Mockito.inOrder(mockedFailureConnection);
        inOrder.verify(mockedFailureConnection).getInputStream();
        inOrder.verify(mockedFailureConnection).getErrorStream();
        inOrder.verify(mockedFailureConnection).getResponseCode();
        inOrder.verify(mockedFailureConnection).getHeaderFields();
        inOrder.verifyNoMoreInteractions();
    }

    /**
     * Verify that http get request fails with {@link HttpURLConnection#HTTP_BAD_METHOD}
     * and no response body, no retry happens.
     */
    @Test
    public void testHttpGetFailedNoRetryNoResponseBody() throws Exception {
        testHttpMethodFailedNoRetryNoResponseBody(HttpTestMethod.GET);
    }

    /**
     * Verify that initial http post fails with {@link HttpURLConnection#HTTP_INTERNAL_ERROR}(retryable status code
     * 500/503/504), retry fails with {@link HttpURLConnection#HTTP_UNAUTHORIZED}(non retryable status code).
     */
    @Test
    public void testHttpPostFailedWithRetryableStatusCodeRetryFailsWithNonRetryableCode() throws Exception {
        sendMethodWithRetryableStatusCodeRetryFailsWithNonRetryableCode(HttpTestMethod.POST);

    }
    @Test
    public void testHttpPutFailedWithRetryableStatusCodeRetryFailsWithNonRetryableCode() throws Exception {
        sendMethodWithRetryableStatusCodeRetryFailsWithNonRetryableCode(HttpTestMethod.PUT);

    }
    @Test
    public void testHttpPatchFailedWithRetryableStatusCodeRetryFailsWithNonRetryableCode() throws Exception {
        sendMethodWithRetryableStatusCodeRetryFailsWithNonRetryableCode(HttpTestMethod.PATCH);

    }
    @Test
    public void testHttpOptionsFailedWithRetryableStatusCodeRetryFailsWithNonRetryableCode() throws Exception {
        sendMethodWithRetryableStatusCodeRetryFailsWithNonRetryableCode(HttpTestMethod.OPTIONS);

    }
    @Test
    public void testHttpTraceFailedWithRetryableStatusCodeRetryFailsWithNonRetryableCode() throws Exception {
        sendMethodWithRetryableStatusCodeRetryFailsWithNonRetryableCode(HttpTestMethod.TRACE);

    }
    @Test
    public void testHttpGetFailedWithRetryableStatusCodeRetryFailsWithNonRetryableCode() throws Exception {
        sendMethodWithRetryableStatusCodeRetryFailsWithNonRetryableCode(HttpTestMethod.GET);

    }
    @Test
    public void testHttpHeadFailedWithRetryableStatusCodeRetryFailsWithNonRetryableCode() throws Exception {
        sendMethodWithRetryableStatusCodeRetryFailsWithNonRetryableCode(HttpTestMethod.HEAD);

    }
    @Test
    public void testHttpDeleteFailedWithRetryableStatusCodeRetryFailsWithNonRetryableCode() throws Exception {
        sendMethodWithRetryableStatusCodeRetryFailsWithNonRetryableCode(HttpTestMethod.DELETE);

    }

    private void sendMethodWithRetryableStatusCodeRetryFailsWithNonRetryableCode(HttpTestMethod method) throws Exception {
        // The first connection fails with retryable status code 500, the retry connection fails with 401.
        final HttpURLConnection firstConnection =
                getMockedConnectionWithFailureResponse(
                        HttpURLConnection.HTTP_INTERNAL_ERROR,
                        getErrorResponse()
                );
        mockRequestBody(firstConnection);

        final HttpURLConnection secondConnection =
                getMockedConnectionWithFailureResponse(
                        HttpURLConnection.HTTP_UNAUTHORIZED,
                        getErrorResponse()
                );
        mockRequestBody(secondConnection);

        addMockedConnection(firstConnection);
        addMockedConnection(secondConnection);

        try {
            assertEquals(2, getMockedConnectionCountInQueue());
            final HttpResponse response = sendWithMethod(method);
            assertNotNull(response);
            assertEquals(response.getStatusCode(), HttpURLConnection.HTTP_UNAUTHORIZED);
            assertEquals(response.getBody(), getErrorResponse());
        } catch (final IOException e) {
            fail();
        }

        assertEquals(0, getMockedConnectionCountInQueue());

        final InOrder inOrder = Mockito.inOrder(firstConnection, secondConnection);
        inOrder.verify(firstConnection).getInputStream();
        inOrder.verify(firstConnection).getErrorStream();
        inOrder.verify(firstConnection).getResponseCode();

        inOrder.verify(secondConnection).getInputStream();
        inOrder.verify(secondConnection).getErrorStream();
        inOrder.verify(secondConnection).getResponseCode();
        inOrder.verify(secondConnection).getHeaderFields();

        inOrder.verifyNoMoreInteractions();
    }

    /**
     * Verify that initial http post fails with {@link SocketTimeoutException}, retry fails with
     * {@link HttpURLConnection#HTTP_BAD_REQUEST}(non retryable status code).
     */
    @Test
    public void testHttpPostFailedWithSocketTimeoutRetryFailedWithNonRetryableCode() throws Exception {
        // The first connection fails with retryable SocketTimeout, the retry connection fails with 400.
        final HttpURLConnection firstConnection = MockUtil.getMockedConnectionWithSocketTimeout();
        mockRequestBody(firstConnection);

        final HttpURLConnection secondConnection = getMockedConnectionWithFailureResponse(
                HttpURLConnection.HTTP_BAD_REQUEST, getErrorResponse());
        mockRequestBody(secondConnection);

        addMockedConnection(firstConnection);
        addMockedConnection(secondConnection);

        try {
            assertEquals(2, getMockedConnectionCountInQueue());
            final HttpResponse response = sendWithMethod(HttpTestMethod.POST);
            assertNotNull(response);
            assertEquals(response.getStatusCode(), HttpURLConnection.HTTP_BAD_REQUEST);
            assertEquals(response.getBody(), getErrorResponse());
        } catch (final IOException e) {
            fail();
        }

        assertEquals(0, getMockedConnectionCountInQueue());
        final InOrder inOrder = Mockito.inOrder(firstConnection, secondConnection);
        inOrder.verify(firstConnection).getInputStream();
        inOrder.verify(firstConnection, Mockito.never()).getErrorStream();
        inOrder.verify(firstConnection, Mockito.never()).getResponseCode();

        inOrder.verify(secondConnection).getInputStream();
        inOrder.verify(secondConnection).getErrorStream();
        inOrder.verify(secondConnection).getResponseCode();
        inOrder.verify(secondConnection).getHeaderFields();

        inOrder.verifyNoMoreInteractions();
    }

    /**
     * Verify that initial http post fails with {@link SocketTimeoutException}, when we're not
     * running in legacy retry mode the socket timeout exception propagates to the caller.
     */
    @Test(expected = SocketTimeoutException.class)
    public void testHttpPostFailedWithSocketTimeoutNoRetriesDoesNotRetry() throws Exception {
        // The first connection fails with retryable SocketTimeout, the retry connection fails with 400.
        final HttpURLConnection firstConnection = MockUtil.getMockedConnectionWithSocketTimeout();
        mockRequestBody(firstConnection);

        final HttpURLConnection secondConnection = MockUtil.getMockedConnectionWithSocketTimeout();
        mockRequestBody(secondConnection);

        addMockedConnection(firstConnection);
        addMockedConnection(secondConnection);

        try {
            assertEquals(2, getMockedConnectionCountInQueue());
            final HttpResponse response = sendWithMethodNoRetry(HttpTestMethod.POST);
        } catch (final IOException e) {
            if (! (e instanceof SocketTimeoutException)) {
                fail();
            }
            throw e;
        } finally {
            assertEquals(1, getMockedConnectionCountInQueue());
            final InOrder inOrder = Mockito.inOrder(firstConnection, secondConnection);
            inOrder.verify(firstConnection).getInputStream();
            inOrder.verify(firstConnection, Mockito.never()).getErrorStream();
            inOrder.verify(firstConnection, Mockito.never()).getResponseCode();
            inOrder.verifyNoMoreInteractions();
        }
    }

    private void verifySuccessHttpResponse(final HttpResponse httpResponse) {
        assertNotNull(httpResponse);
        assertEquals(httpResponse.getStatusCode(), HttpURLConnection.HTTP_OK);
        assertEquals(httpResponse.getBody(), getSuccessResponse());
    }

    private enum HttpTestMethod {
        GET { boolean canHaveBody() { return false; }
            HttpResponse specific(URL url, Map<String, String> headers, byte[] body, String contentType) throws Exception {
                return HttpRequest.sendGet(url, headers);
            }
            HttpResponse specificNoRetry(URL url, Map<String, String> headers, byte[] body, String contentType) throws Exception {
                return UrlConnectionHttpClient.getDefaultInstance().get(url, headers);
            }},
        HEAD { boolean canHaveNoBody() { return true; } boolean canHaveBody() { return false; }
            HttpResponse specific(URL url, Map<String, String> headers, byte[] body, String contentType) throws Exception {
                return HttpRequest.sendHead(url, headers);
            }
            HttpResponse specificNoRetry(URL url, Map<String, String> headers, byte[] body, String contentType) throws Exception {
                return UrlConnectionHttpClient.getDefaultInstance().head(url, headers);
            }},
        PUT { boolean canHaveNoBody() { return false; }
            HttpResponse specific(URL url, Map<String, String> headers, byte[] body, String contentType) throws Exception {
                return HttpRequest.sendPut(url, headers, body, contentType);
            }
            HttpResponse specificNoRetry(URL url, Map<String, String> headers, byte[] body, String contentType) throws Exception {
                return UrlConnectionHttpClient.getDefaultInstance().put(url, headers, body);
            }},
        POST { HttpResponse specific(URL url, Map<String, String> headers, byte[] body, String contentType) throws Exception {
            return HttpRequest.sendPost(url, headers, body, contentType);
        }
            HttpResponse specificNoRetry(URL url, Map<String, String> headers, byte[] body, String contentType) throws Exception {
                return UrlConnectionHttpClient.getDefaultInstance().post(url, headers, body);
            }},
        OPTIONS { HttpResponse specific(URL url, Map<String, String> headers, byte[] body, String contentType) throws Exception {
            return HttpRequest.sendOptions(url, headers);
        }
            HttpResponse specificNoRetry(URL url, Map<String, String> headers, byte[] body, String contentType) throws Exception {
                return UrlConnectionHttpClient.getDefaultInstance().options(url, headers);
            }},
        TRACE { boolean canHaveBody() { return false; }
            boolean canHaveNoBody() { return true; }
            HttpResponse specific(URL url, Map<String, String> headers, byte[] body, String contentType) throws Exception {
                return HttpRequest.sendTrace(url, headers);
            }
            HttpResponse specificNoRetry(URL url, Map<String, String> headers, byte[] body, String contentType) throws Exception {
                return UrlConnectionHttpClient.getDefaultInstance().trace(url, headers);
            }},
        PATCH{ HttpResponse specific(URL url, Map<String, String> headers, byte[] body, String contentType) throws Exception {
                return HttpRequest.sendPatch(url, headers, body, contentType);
            }
            HttpResponse specificNoRetry(URL url, Map<String, String> headers, byte[] body, String contentType) throws Exception {
                return UrlConnectionHttpClient.getDefaultInstance().patch(url, headers, body);
            }},
        DELETE{ HttpResponse specific(URL url, Map<String, String> headers, byte[] body, String contentType) throws Exception {
                return HttpRequest.sendDelete(url, headers, body, contentType);
            }
            HttpResponse specificNoRetry(URL url, Map<String, String> headers, byte[] body, String contentType) throws Exception {
                return UrlConnectionHttpClient.getDefaultInstance().delete(url, headers, body);
            }};

        abstract HttpResponse specific(URL url, Map<String, String> headers, byte[] body, String contentType) throws Exception;

        abstract HttpResponse  specificNoRetry(URL url, Map<String, String> headers, byte[] body, String contentType) throws Exception;

        boolean canHaveBody() {
            return true;
        }
        boolean canHaveNoBody() {
            return true;
        }
    }

    private static HttpResponse sendSpecific(HttpTestMethod method) throws Exception {
        URL validRequestUrl = Util.getValidRequestUrl();
        return method.specific(validRequestUrl,
                method.canHaveBody() ? Collections.singletonMap("Content-Type", "application/x-www-form-urlencoded") : Collections.<String, String>emptyMap(),
                method.canHaveBody() ? UUID.nameUUIDFromBytes((validRequestUrl.toString() + method).getBytes(UTF8)).toString().getBytes() : null,
                null);
    }

    private static HttpResponse sendSpecificWithoutRetry(HttpTestMethod method) throws Exception {
        URL validRequestUrl = Util.getValidRequestUrl();
        return method.specificNoRetry(validRequestUrl, Collections.<String, String>emptyMap(),
                method.canHaveBody() ? UUID.nameUUIDFromBytes((validRequestUrl.toString() + method).getBytes(UTF8)).toString().getBytes() : null ,
                method.canHaveBody() ? "application/x-www-form-urlencoded" : null);
    }

    private static HttpResponse sendWithMethod(HttpTestMethod method) throws Exception {
        URL validRequestUrl = Util.getValidRequestUrl();
        return HttpRequest.sendWithMethod(
                method.name(),
                validRequestUrl,
                Collections.<String, String>emptyMap(),
                method.canHaveBody() ? UUID.nameUUIDFromBytes((validRequestUrl.toString() + method).getBytes(UTF8)).toString().getBytes() : null ,
                method.canHaveBody() ? "application/x-www-form-urlencoded" : null
        );
    }

    private static HttpResponse sendWithMethodNoRetry(HttpTestMethod method)  throws Exception {
        URL validRequestUrl = Util.getValidRequestUrl();
        return UrlConnectionHttpClient.getDefaultInstance().method(
                method.name(),
                validRequestUrl,
                method.canHaveBody() ? Collections.singletonMap("Content-Type", "application/x-www-form-urlencoded") : Collections.<String, String>emptyMap(),
                method.canHaveBody()? UUID.nameUUIDFromBytes((validRequestUrl.toString() + method).getBytes(UTF8)).toString().getBytes(UTF8) : null
        );

    }

    /**
     * @return Successful response from server.
     */
    private String getSuccessResponse() {
        return "{\"response\":\"success response\"}";
    }

    /**
     * @return Error response from server.
     */
    private String getErrorResponse() {
        return "{\"response\":\"error response\"}";
    }

    private void mockRequestBody(final HttpURLConnection mockedConnection) throws IOException {
        Mockito.when(mockedConnection.getOutputStream())
                .thenReturn(Mockito.mock(OutputStream.class));
    }
}
