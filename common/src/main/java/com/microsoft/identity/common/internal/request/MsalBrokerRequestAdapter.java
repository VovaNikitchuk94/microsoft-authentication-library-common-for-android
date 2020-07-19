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
package com.microsoft.identity.common.internal.request;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.identity.common.adal.internal.AuthenticationConstants;
import com.microsoft.identity.common.exception.ClientException;
import com.microsoft.identity.common.internal.authorities.Authority;
import com.microsoft.identity.common.internal.authorities.AzureActiveDirectoryAuthority;
import com.microsoft.identity.common.internal.authorities.Environment;
import com.microsoft.identity.common.internal.authscheme.AbstractAuthenticationScheme;
import com.microsoft.identity.common.internal.authscheme.BearerAuthenticationSchemeInternal;
import com.microsoft.identity.common.internal.authscheme.PopAuthenticationSchemeInternal;
import com.microsoft.identity.common.internal.broker.BrokerRequest;
import com.microsoft.identity.common.internal.broker.BrokerValidator;
import com.microsoft.identity.common.internal.commands.parameters.BrokerInteractiveTokenCommandParameters;
import com.microsoft.identity.common.internal.commands.parameters.BrokerSilentTokenCommandParameters;
import com.microsoft.identity.common.internal.commands.parameters.CommandParameters;
import com.microsoft.identity.common.internal.commands.parameters.InteractiveTokenCommandParameters;
import com.microsoft.identity.common.internal.commands.parameters.RemoveAccountCommandParameters;
import com.microsoft.identity.common.internal.commands.parameters.SilentTokenCommandParameters;
import com.microsoft.identity.common.internal.commands.parameters.TokenCommandParameters;
import com.microsoft.identity.common.internal.logging.DiagnosticContext;
import com.microsoft.identity.common.internal.logging.Logger;
import com.microsoft.identity.common.internal.providers.microsoft.azureactivedirectory.AzureActiveDirectory;
import com.microsoft.identity.common.internal.providers.oauth2.OpenIdConnectPromptParameter;
import com.microsoft.identity.common.internal.ui.AuthorizationAgent;
import com.microsoft.identity.common.internal.ui.browser.Browser;
import com.microsoft.identity.common.internal.ui.browser.BrowserDescriptor;
import com.microsoft.identity.common.internal.ui.browser.BrowserSelector;
import com.microsoft.identity.common.internal.util.BrokerProtocolVersionUtil;
import com.microsoft.identity.common.internal.util.ClockSkewManager;
import com.microsoft.identity.common.internal.util.IClockSkewManager;
import com.microsoft.identity.common.internal.util.QueryParamsAdapter;
import com.microsoft.identity.common.internal.util.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.microsoft.identity.common.adal.internal.AuthenticationConstants.Broker.ACCOUNT_CLIENTID_KEY;
import static com.microsoft.identity.common.adal.internal.AuthenticationConstants.Broker.ACCOUNT_HOME_ACCOUNT_ID;
import static com.microsoft.identity.common.adal.internal.AuthenticationConstants.Broker.ACCOUNT_REDIRECT;
import static com.microsoft.identity.common.adal.internal.AuthenticationConstants.Broker.BROKER_ACTIVITY_NAME;
import static com.microsoft.identity.common.adal.internal.AuthenticationConstants.Broker.BROKER_PACKAGE_NAME;
import static com.microsoft.identity.common.adal.internal.AuthenticationConstants.Broker.DEFAULT_BROWSER_PACKAGE_NAME;
import static com.microsoft.identity.common.adal.internal.AuthenticationConstants.Broker.ENVIRONMENT;
import static com.microsoft.identity.common.adal.internal.AuthenticationConstants.Broker.NEGOTIATED_BP_VERSION_KEY;
import static com.microsoft.identity.common.internal.util.GzipUtil.compressString;
import static com.microsoft.identity.common.internal.util.GzipUtil.decompressBytesToString;

public class MsalBrokerRequestAdapter implements IBrokerRequestAdapter {

    private static final String TAG = MsalBrokerRequestAdapter.class.getName();

    public static Gson sRequestAdapterGsonInstance;

    static {
        sRequestAdapterGsonInstance = new GsonBuilder()
                .registerTypeAdapter(
                        AbstractAuthenticationScheme.class,
                        new AuthenticationSchemeTypeAdapter()
                ).create();
    }

    @Override
    public BrokerRequest brokerRequestFromAcquireTokenParameters(@NonNull final InteractiveTokenCommandParameters parameters) {
        Logger.info(TAG, "Constructing result bundle from AcquireTokenOperationParameters.");

        final BrokerRequest brokerRequest = new BrokerRequest.Builder()
                .authority(parameters.getAuthority().getAuthorityURL().toString())
                .scope(TextUtils.join(" ", parameters.getScopes()))
                .redirect(getRedirectUri(parameters))
                .clientId(parameters.getClientId())
                .username(parameters.getLoginHint())
                .extraQueryStringParameter(
                        parameters.getExtraQueryStringParameters() != null ?
                                QueryParamsAdapter._toJson(parameters.getExtraQueryStringParameters())
                                : null
                ).prompt(parameters.getPrompt().name())
                .claims(parameters.getClaimsRequestJson())
                .forceRefresh(parameters.isForceRefresh())
                .correlationId(DiagnosticContext.getRequestContext().get(DiagnosticContext.CORRELATION_ID))
                .applicationName(parameters.getApplicationName())
                .applicationVersion(parameters.getApplicationVersion())
                .msalVersion(parameters.getSdkVersion())
                .environment(AzureActiveDirectory.getEnvironment().name())
                .multipleCloudsSupported(getMultipleCloudsSupported(parameters))
                .authorizationAgent(
                        parameters.isBrokerBrowserSupportEnabled() ?
                                AuthorizationAgent.BROWSER.name() :
                                AuthorizationAgent.WEBVIEW.name()
                ).authenticationScheme(parameters.getAuthenticationScheme())
                .powerOptCheckEnabled(parameters.isPowerOptCheckEnabled())
                .build();

        return brokerRequest;
    }

    @Override
    public BrokerRequest brokerRequestFromSilentOperationParameters(@NonNull final SilentTokenCommandParameters parameters) {

        Logger.info(TAG, "Constructing result bundle from AcquireTokenSilentOperationParameters.");

        final BrokerRequest brokerRequest = new BrokerRequest.Builder()
                .authority(parameters.getAuthority().getAuthorityURL().toString())
                .scope(TextUtils.join(" ", parameters.getScopes()))
                .redirect(getRedirectUri(parameters))
                .clientId(parameters.getClientId())
                .homeAccountId(parameters.getAccount().getHomeAccountId())
                .localAccountId(parameters.getAccount().getLocalAccountId())
                .username(parameters.getAccount().getUsername())
                .claims(parameters.getClaimsRequestJson())
                .forceRefresh(parameters.isForceRefresh())
                .correlationId(DiagnosticContext.getRequestContext().get(DiagnosticContext.CORRELATION_ID))
                .applicationName(parameters.getApplicationName())
                .applicationVersion(parameters.getApplicationVersion())
                .msalVersion(parameters.getSdkVersion())
                .environment(AzureActiveDirectory.getEnvironment().name())
                .multipleCloudsSupported(getMultipleCloudsSupported(parameters))
                .authenticationScheme(parameters.getAuthenticationScheme())
                .powerOptCheckEnabled(parameters.isPowerOptCheckEnabled())
                .build();

        return brokerRequest;
    }

    @NonNull
    private static AbstractAuthenticationScheme getAuthenticationScheme(
            @NonNull final Context context,
            @NonNull final BrokerRequest request) {
        final AbstractAuthenticationScheme requestScheme = request.getAuthenticationScheme();

        if (null == requestScheme) {
            // Default assumes the scheme is Bearer
            return new BearerAuthenticationSchemeInternal();
        } else {
            if (requestScheme instanceof PopAuthenticationSchemeInternal) {
                final IClockSkewManager clockSkewManager = new ClockSkewManager(context);
                ((PopAuthenticationSchemeInternal) requestScheme).setClockSkewManager(clockSkewManager);
            }

            return requestScheme;
        }
    }

    @Override
    public BrokerInteractiveTokenCommandParameters brokerInteractiveParametersFromActivity(
            @NonNull final Activity callingActivity) {

        Logger.info(TAG, "Constructing BrokerAcquireTokenOperationParameters from calling activity");

        final Intent intent = callingActivity.getIntent();

        final BrokerRequest brokerRequest = brokerRequestFromBundle(intent.getExtras());

        if (brokerRequest == null) {
            Logger.error(TAG, "Broker Result is null, returning empty parameters, " +
                            "validation is expected to fail", null
            );
            return BrokerInteractiveTokenCommandParameters.builder().build();
        }

        int callingAppUid = intent.getIntExtra(
                AuthenticationConstants.Broker.CALLER_INFO_UID, 0
        );

        List<Pair<String, String>> extraQP = new ArrayList<>();

        if (!TextUtils.isEmpty(brokerRequest.getExtraQueryStringParameter())) {
            extraQP = QueryParamsAdapter._fromJson(brokerRequest.getExtraQueryStringParameter());
        }

        final AzureActiveDirectoryAuthority authority = AdalBrokerRequestAdapter.getRequestAuthorityWithExtraQP(
                brokerRequest.getAuthority(),
                extraQP
        );

        if (authority != null) {
            authority.setMultipleCloudsSupported(brokerRequest.getMultipleCloudsSupported());
        }

        String correlationIdString = brokerRequest.getCorrelationId();

        if (TextUtils.isEmpty(correlationIdString)) {
            UUID correlationId = UUID.randomUUID();
            correlationIdString = correlationId.toString();
        }
        final String negotiatedBrokerProtocolVersion = intent.getStringExtra(NEGOTIATED_BP_VERSION_KEY);

        Logger.info(TAG, "Authorization agent passed in by MSAL: " + brokerRequest.getAuthorizationAgent());

        final BrokerInteractiveTokenCommandParameters.BrokerInteractiveTokenCommandParametersBuilder
                commandParametersBuilder = BrokerInteractiveTokenCommandParameters.builder()
                .authenticationScheme(getAuthenticationScheme(callingActivity, brokerRequest))
                .activity(callingActivity)
                .androidApplicationContext(callingActivity.getApplicationContext())
                .sdkType(SdkType.MSAL)
                .callerUid(callingAppUid)
                .callerPackageName(brokerRequest.getApplicationName())
                .callerAppVersion(brokerRequest.getApplicationVersion())
                .extraQueryStringParameters(extraQP)
                .authority(authority)
                .scopes(getScopesAsSet(brokerRequest.getScope()))
                .clientId(brokerRequest.getClientId())
                .redirectUri(brokerRequest.getRedirect())
                .loginHint(brokerRequest.getUserName())
                .correlationId(correlationIdString)
                .claimsRequestJson(brokerRequest.getClaims())
                .prompt(brokerRequest.getPrompt() != null ?
                        OpenIdConnectPromptParameter.valueOf(brokerRequest.getPrompt()) :
                        OpenIdConnectPromptParameter.NONE)
                .negotiatedBrokerProtocolVersion(negotiatedBrokerProtocolVersion)
                .powerOptCheckEnabled(brokerRequest.isPowerOptCheckEnabled());

        if (brokerRequest.getAuthorizationAgent() != null
                && brokerRequest.getAuthorizationAgent().equalsIgnoreCase(AuthorizationAgent.BROWSER.name())
                && isCallingPackageIntune(brokerRequest.getApplicationName())) { // TODO : Remove this whenever we enable System Browser support in Broker for apps.
            Logger.info(TAG, "Setting Authorization Agent to Browser for Intune app");
            commandParametersBuilder
                    .authorizationAgent(AuthorizationAgent.BROWSER)
                    .brokerBrowserSupportEnabled(true)
                    .browserSafeList(getBrowserSafeListForBroker());
        } else {
            commandParametersBuilder.authorizationAgent(AuthorizationAgent.WEBVIEW);
        }

        // Set Global environment variable for instance discovery if present
        if (!TextUtils.isEmpty(brokerRequest.getEnvironment())) {
            AzureActiveDirectory.setEnvironment(
                    Environment.valueOf(brokerRequest.getEnvironment())
            );
        }

        return commandParametersBuilder.build();
    }

    @Override
    public BrokerSilentTokenCommandParameters brokerSilentParametersFromBundle(
            @NonNull final Bundle bundle,
            @NonNull final Context context,
            @NonNull final Account account) {

        Logger.info(TAG, "Constructing BrokerAcquireTokenSilentOperationParameters from result bundle");

        final BrokerRequest brokerRequest = brokerRequestFromBundle(bundle);

        if (brokerRequest == null) {
            Logger.error(TAG, "Broker Result is null, returning empty parameters, " +
                            "validation is expected to fail", null
            );
            return BrokerSilentTokenCommandParameters
                    .builder().build();
        }

        int callingAppUid = bundle.getInt(
                AuthenticationConstants.Broker.CALLER_INFO_UID
        );

        final Authority authority = Authority.getAuthorityFromAuthorityUrl(
                brokerRequest.getAuthority()
        );

        if (authority instanceof AzureActiveDirectoryAuthority) {
            ((AzureActiveDirectoryAuthority) authority).setMultipleCloudsSupported(
                    brokerRequest.getMultipleCloudsSupported()
            );
        }

        String correlationIdString = bundle.getString(
                brokerRequest.getCorrelationId()
        );
        if (TextUtils.isEmpty(correlationIdString)) {
            UUID correlationId = UUID.randomUUID();
            correlationIdString = correlationId.toString();
        }

        final String negotiatedBrokerProtocolVersion = bundle.getString(NEGOTIATED_BP_VERSION_KEY);

        final BrokerSilentTokenCommandParameters commandParameters = BrokerSilentTokenCommandParameters
                .builder()
                .authenticationScheme(getAuthenticationScheme(context, brokerRequest))
                .androidApplicationContext(context)
                .accountManagerAccount(account)
                .sdkType(SdkType.MSAL)
                .callerUid(callingAppUid)
                .callerPackageName(brokerRequest.getApplicationName())
                .callerAppVersion(brokerRequest.getApplicationVersion())
                .authority(authority)
                .correlationId(correlationIdString)
                .scopes(getScopesAsSet(brokerRequest.getScope()))
                .redirectUri(brokerRequest.getRedirect())
                .clientId(brokerRequest.getClientId())
                .forceRefresh(brokerRequest.getForceRefresh())
                .claimsRequestJson(brokerRequest.getClaims())
                .loginHint(brokerRequest.getUserName())
                .homeAccountId(brokerRequest.getHomeAccountId())
                .localAccountId(brokerRequest.getLocalAccountId())
                .negotiatedBrokerProtocolVersion(negotiatedBrokerProtocolVersion)
                .powerOptCheckEnabled(brokerRequest.isPowerOptCheckEnabled())
                .build();


        // Set Global environment variable for instance discovery if present
        if (!TextUtils.isEmpty(brokerRequest.getEnvironment())) {
            AzureActiveDirectory.setEnvironment(
                    Environment.valueOf(brokerRequest.getEnvironment())
            );
        }

        return commandParameters;
    }

    @Nullable
    public BrokerRequest brokerRequestFromBundle(@NonNull final Bundle requestBundle) {
        BrokerRequest brokerRequest = null;

        if (requestBundle.containsKey(AuthenticationConstants.Broker.BROKER_REQUEST_V2_COMPRESSED)) {
            try {
                final String deCompressedString = decompressBytesToString(
                        requestBundle.getByteArray(AuthenticationConstants.Broker.BROKER_REQUEST_V2_COMPRESSED)
                );
                brokerRequest = sRequestAdapterGsonInstance.fromJson(
                        deCompressedString, BrokerRequest.class
                );
            } catch (final IOException e) {
                // We would ideally never run into this case as compression would always work as expected.
                // The caller should handle the null value of broker request.
                Logger.error(TAG, "Decompression of Broker Request failed," +
                        " unable to continue with Broker Request", e
                );
            }

        } else {
            brokerRequest = sRequestAdapterGsonInstance.fromJson(
                    requestBundle.getString(AuthenticationConstants.Broker.BROKER_REQUEST_V2),
                    BrokerRequest.class
            );
        }
        return brokerRequest;
    }

    /**
     * Helper method to transforn scopes string to Set
     */
    private Set<String> getScopesAsSet(@Nullable final String scopeString) {
        if (TextUtils.isEmpty(scopeString)) {
            return new HashSet<>();
        }
        final String[] scopes = scopeString.split(" ");
        return new HashSet<>(Arrays.asList(scopes));
    }

    /**
     * Helper method to get redirect uri from parameters, calculates from package signature if not available.
     */
    private String getRedirectUri(@NonNull TokenCommandParameters parameters) {
        if (TextUtils.isEmpty(parameters.getRedirectUri())) {
            return BrokerValidator.getBrokerRedirectUri(
                    parameters.getAndroidApplicationContext(),
                    parameters.getApplicationName()
            );
        }
        return parameters.getRedirectUri();
    }

    /**
     * Method to construct a request bundle for broker hello request.
     *
     * @param parameters input parameters.
     * @return request bundle to perform hello.
     */
    public Bundle getRequestBundleForHello(@NonNull final CommandParameters parameters) {
        final Bundle requestBundle = new Bundle();
        requestBundle.putString(
                AuthenticationConstants.Broker.CLIENT_ADVERTISED_MAXIMUM_BP_VERSION_KEY,
                AuthenticationConstants.Broker.BROKER_PROTOCOL_VERSION_CODE
        );

        if (!StringUtil.isEmpty(parameters.getRequiredBrokerProtocolVersion())) {
            requestBundle.putString(
                    AuthenticationConstants.Broker.CLIENT_CONFIGURED_MINIMUM_BP_VERSION_KEY,
                    parameters.getRequiredBrokerProtocolVersion()
            );
        }

        return requestBundle;
    }

    /**
     * Method to construct a request intent for broker acquireTokenInteractive request.
     * Only used in case of BrokerContentProvider
     *
     * @param resultBundle result Bundle returned by broker.
     * @param parameters input parameters
     * @param negotiatedBrokerProtocolVersion protocol version returned by broker hello.
     * @return request Intent
     */
    public Intent getRequestIntentForAcquireTokenInteractive(@NonNull final Bundle resultBundle,
                                                             @NonNull final InteractiveTokenCommandParameters parameters,
                                                             @Nullable final String negotiatedBrokerProtocolVersion) {
        final Bundle requestBundle = getRequestBundleForAcquireTokenInteractive(
                parameters,
                negotiatedBrokerProtocolVersion
        );
        Intent interactiveRequestIntent = new Intent();
        interactiveRequestIntent.putExtras(requestBundle);
        interactiveRequestIntent.putExtras(resultBundle);
        interactiveRequestIntent.setPackage(resultBundle.getString(BROKER_PACKAGE_NAME));
        interactiveRequestIntent.setClassName(
                resultBundle.getString(BROKER_PACKAGE_NAME, ""),
                resultBundle.getString(BROKER_ACTIVITY_NAME, "")
        );
        interactiveRequestIntent.putExtra(
                AuthenticationConstants.Broker.NEGOTIATED_BP_VERSION_KEY,
                negotiatedBrokerProtocolVersion
        );
        return interactiveRequestIntent;
    }

    /**
     * Method to construct a request bundle for broker acquireTokenInteractive request.
     *
     * @param parameters input parameters
     * @param negotiatedBrokerProtocolVersion protocol version returned by broker hello.
     * @return request Bundle
     */
    public Bundle getRequestBundleForAcquireTokenInteractive(@NonNull final InteractiveTokenCommandParameters parameters,
                                                             @Nullable final String negotiatedBrokerProtocolVersion) {
        final BrokerRequest brokerRequest = brokerRequestFromAcquireTokenParameters(parameters);
        return getRequestBundleFromBrokerRequest(
                brokerRequest,
                negotiatedBrokerProtocolVersion
        );
    }

    /**
     * Method to construct a request bundle for broker acquireTokenSilent request.
     *
     * @param parameters input parameters
     * @param negotiatedBrokerProtocolVersion protocol version returned by broker hello.
     * @return request Bundle
     */
    public Bundle getRequestBundleForAcquireTokenSilent(@NonNull final SilentTokenCommandParameters parameters,
                                                        @Nullable final String negotiatedBrokerProtocolVersion) {
        final MsalBrokerRequestAdapter msalBrokerRequestAdapter = new MsalBrokerRequestAdapter();

        final BrokerRequest brokerRequest = msalBrokerRequestAdapter.
                brokerRequestFromSilentOperationParameters(parameters);

        final Bundle requestBundle = getRequestBundleFromBrokerRequest(
                brokerRequest,
                negotiatedBrokerProtocolVersion
        );

        requestBundle.putInt(
                AuthenticationConstants.Broker.CALLER_INFO_UID,
                parameters.getAndroidApplicationContext().getApplicationInfo().uid
        );

        return requestBundle;
    }

    private Bundle getRequestBundleFromBrokerRequest(@NonNull BrokerRequest brokerRequest,
                                                     @Nullable String negotiatedBrokerProtocolVersion) {
        final Bundle requestBundle = new Bundle();

        if (BrokerProtocolVersionUtil.canCompressBrokerPayloads(negotiatedBrokerProtocolVersion)) {
            try {
                final String jsonString = sRequestAdapterGsonInstance.toJson(brokerRequest, BrokerRequest.class);
                byte[] compressedBytes = compressString(jsonString);
                Logger.info(TAG, "Broker Result, raw payload size:"
                        + jsonString.getBytes().length + " ,compressed bytes size: " + compressedBytes.length
                );
                requestBundle.putByteArray(
                        AuthenticationConstants.Broker.BROKER_REQUEST_V2_COMPRESSED,
                        compressedBytes
                );
            } catch (IOException e) {
                Logger.error(TAG, "Compression to bytes failed, sending broker request as json String", e);
                requestBundle.putString(
                        AuthenticationConstants.Broker.BROKER_REQUEST_V2,
                        sRequestAdapterGsonInstance.toJson(brokerRequest, BrokerRequest.class)
                );
            }
        } else {
            Logger.info(TAG, "Broker protocol version: " + negotiatedBrokerProtocolVersion +
                    " lower than compression changes, sending as string"
            );
            requestBundle.putString(
                    AuthenticationConstants.Broker.BROKER_REQUEST_V2,
                    sRequestAdapterGsonInstance.toJson(brokerRequest, BrokerRequest.class)
            );
        }
        requestBundle.putString(
                AuthenticationConstants.Broker.NEGOTIATED_BP_VERSION_KEY,
                negotiatedBrokerProtocolVersion
        );
        return requestBundle;
    }

    /**
     * Method to construct a request bundle for broker getAccounts request.
     *
     * @param parameters input parameters
     * @param negotiatedBrokerProtocolVersion protocol version returned by broker hello.
     * @return request Bundle
     */
    public Bundle getRequestBundleForGetAccounts(@NonNull final CommandParameters parameters,
                                                 @Nullable final String negotiatedBrokerProtocolVersion) {
        final Bundle requestBundle = new Bundle();
        requestBundle.putString(ACCOUNT_CLIENTID_KEY, parameters.getClientId());
        requestBundle.putString(ACCOUNT_REDIRECT, parameters.getRedirectUri());
        requestBundle.putString(
                AuthenticationConstants.Broker.NEGOTIATED_BP_VERSION_KEY,
                negotiatedBrokerProtocolVersion
        );
        //Disable the environment and tenantID. Just return all accounts belong to this clientID.
        return requestBundle;
    }

    /**
     * Method to construct a request bundle for broker removeAccount request.
     *
     * @param parameters input parameters
     * @param negotiatedBrokerProtocolVersion protocol version returned by broker hello.
     * @return request Bundle
     */
    public Bundle getRequestBundleForRemoveAccount(@NonNull final RemoveAccountCommandParameters parameters,
                                                   @Nullable final String negotiatedBrokerProtocolVersion) {
        final Bundle requestBundle = new Bundle();
        if (null != parameters.getAccount()) {
            requestBundle.putString(ACCOUNT_CLIENTID_KEY, parameters.getClientId());
            requestBundle.putString(ENVIRONMENT, parameters.getAccount().getEnvironment());
            requestBundle.putString(ACCOUNT_HOME_ACCOUNT_ID, parameters.getAccount().getHomeAccountId());
        }
        requestBundle.putString(
                AuthenticationConstants.Broker.NEGOTIATED_BP_VERSION_KEY,
                negotiatedBrokerProtocolVersion
        );

        return requestBundle;
    }

    /**
     * Method to construct a request bundle for broker removeAccount request.
     *
     * @param parameters input parameters
     * @param negotiatedBrokerProtocolVersion protocol version returned by broker hello.
     * @return request Bundle
     */
    public Bundle getRequestBundleForRemoveAccountFromSharedDevice(@NonNull final RemoveAccountCommandParameters parameters,
                                                                   @Nullable final String negotiatedBrokerProtocolVersion) {
        final Bundle requestBundle = new Bundle();

        try {
            Browser browser = BrowserSelector.select(parameters.getAndroidApplicationContext(), parameters.getBrowserSafeList());
            requestBundle.putString(DEFAULT_BROWSER_PACKAGE_NAME, browser.getPackageName());
        } catch (ClientException e) {
            // Best effort. If none is passed to broker, then it will let the OS decide.
            Logger.error(TAG, e.getErrorCode(), e);
        }
        requestBundle.putString(
                AuthenticationConstants.Broker.NEGOTIATED_BP_VERSION_KEY,
                negotiatedBrokerProtocolVersion
        );

        return requestBundle;
    }

    private boolean getMultipleCloudsSupported(@NonNull final TokenCommandParameters parameters) {
        if (parameters.getAuthority() instanceof AzureActiveDirectoryAuthority) {
            final AzureActiveDirectoryAuthority authority = (AzureActiveDirectoryAuthority) parameters.getAuthority();
            return authority.getMultipleCloudsSupported();
        } else {
            return false;
        }
    }

    /**
     * List of System Browsers which can be used from broker, currently only Chrome is supported.
     * This information here is populated from the default browser safelist in MSAL.
     *
     * @return
     */
    public static List<BrowserDescriptor> getBrowserSafeListForBroker() {
        List<BrowserDescriptor> browserDescriptors = new ArrayList<>();
        final HashSet<String> signatureHashes = new HashSet();
        signatureHashes.add("7fmduHKTdHHrlMvldlEqAIlSfii1tl35bxj1OXN5Ve8c4lU6URVu4xtSHc3BVZxS6WWJnxMDhIfQN0N0K2NDJg==");
        final BrowserDescriptor chrome = new BrowserDescriptor(
                "com.android.chrome",
                signatureHashes,
                null,
                null
        );
        browserDescriptors.add(chrome);

        return browserDescriptors;
    }

    /**
     * Helper method to validate in Broker that the calling package in Microsoft Intune
     * to allow System Webview Support.
     */
    private boolean isCallingPackageIntune(@NonNull final String packageName) {
        final String methodName = ":isCallingPackageIntune";
        final String intunePackageName = "com.microsoft.intune";
        Logger.info(TAG + methodName, "Calling package name : " + packageName);
        return intunePackageName.equalsIgnoreCase(packageName);
    }
}
