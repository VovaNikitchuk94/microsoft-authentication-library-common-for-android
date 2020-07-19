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
package com.microsoft.identity.common.internal.commands.parameters;

import android.text.TextUtils;

import com.microsoft.identity.common.exception.ArgumentException;
import com.microsoft.identity.common.internal.broker.BrokerValidator;
import com.microsoft.identity.common.internal.cache.BrokerOAuth2TokenCache;
import com.microsoft.identity.common.internal.request.BrokerRequestType;
import com.microsoft.identity.common.internal.request.SdkType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class BrokerInteractiveTokenCommandParameters extends InteractiveTokenCommandParameters {

    private String callerPackageName;
    private int callerUid;
    private String callerAppVersion;
    private String brokerVersion;

    private boolean shouldResolveInterrupt;
    private BrokerRequestType requestType;
    private String negotiatedBrokerProtocolVersion;

    private String enrollmentId;

    /**
     * Helper method to identify if the request originated from Broker itself or from client libraries.
     *
     * @return : true if request is the request is originated from Broker, false otherwise
     */
    public boolean isRequestFromBroker() {
        return requestType == BrokerRequestType.BROKER_RT_REQUEST ||
                requestType == BrokerRequestType.RESOLVE_INTERRUPT;
    }

    @Override
    public void validate() throws ArgumentException {
        super.validate();
        if (getAuthority() == null) {
            throw new ArgumentException(
                    ArgumentException.ACQUIRE_TOKEN_OPERATION_NAME,
                    "mAuthority", "Authority Url is not set"
            );
        }
        if (getScopes() == null || getScopes().isEmpty()) {
            throw new ArgumentException(
                    ArgumentException.ACQUIRE_TOKEN_OPERATION_NAME,
                    "mScopes", "Scope or resource is not set"
            );
        }
        if (TextUtils.isEmpty(getClientId())) {
            throw new ArgumentException(
                    ArgumentException.ACQUIRE_TOKEN_OPERATION_NAME,
                    "mClientId", "Client Id is not set"
            );
        }

        // If the request type is BROKER_RT_REQUEST, it means the caller here would be broker itself so
        // calling package name and calling uid will be null, otherwise we need to validate that these are
        // not null for successfully storing tokens in cache.
        if (!isRequestFromBroker()) {
            if (callerUid == 0) {
                throw new ArgumentException(
                        ArgumentException.ACQUIRE_TOKEN_OPERATION_NAME,
                        "mCallerUId", "Caller Uid is not set"
                );
            }
            if (TextUtils.isEmpty(callerPackageName)) {
                throw new ArgumentException(
                        ArgumentException.ACQUIRE_TOKEN_OPERATION_NAME,
                        "mCallerPackageName", "Caller package name is not set"
                );
            }
            if (!(getOAuth2TokenCache() instanceof BrokerOAuth2TokenCache)) {
                throw new ArgumentException(
                        ArgumentException.ACQUIRE_TOKEN_OPERATION_NAME,
                        "AcquireTokenSilentOperationParameters",
                        "OAuth2Cache not an instance of BrokerOAuth2TokenCache"
                );
            }
            if (SdkType.MSAL == getSdkType() &&
                    !BrokerValidator.isValidBrokerRedirect(getRedirectUri(), getAndroidApplicationContext(), getCallerPackageName())) {
                throw new ArgumentException(
                        ArgumentException.ACQUIRE_TOKEN_OPERATION_NAME,
                        "mRedirectUri", "The redirect URI doesn't match the uri" +
                        " generated with caller package name and signature"
                );
            }
        }
    }
}
