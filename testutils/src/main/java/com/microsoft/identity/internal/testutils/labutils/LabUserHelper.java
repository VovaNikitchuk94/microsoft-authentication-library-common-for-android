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
package com.microsoft.identity.internal.testutils.labutils;

import com.microsoft.identity.internal.test.labapi.api.ConfigApi;
import com.microsoft.identity.internal.test.labapi.api.UserApi;
import com.microsoft.identity.internal.test.labapi.model.ConfigInfo;
import com.microsoft.identity.internal.test.labapi.model.UserInfo;

import java.util.List;

public class LabUserHelper {
    public static ConfigInfo getConfigInfo(LabUserQuery query) {
        LabAuthenticationHelper.setupApiClientWithAccessToken();
        ConfigApi api = new ConfigApi();
        List<ConfigInfo> configInfos;

        try {
            configInfos = api.getConfig(query.userType, query.mfa, query.protectionPolicy, query.homeDomain, query.homeUpn, query.b2cProvider, query.federationProvider, query.azureEnvironment, query.signInAudience);
        } catch (com.microsoft.identity.internal.test.labapi.ApiException ex) {
            throw new RuntimeException("Error retrieving lab user", ex);
        }

        final ConfigInfo pickedConfig = configInfos.get(0);
        CurrentLabConfig.configInfo = pickedConfig;

        return pickedConfig;

    }

    public static ConfigInfo getConfigInfoFromUpn(final String upn) {
        LabAuthenticationHelper.setupApiClientWithAccessToken();
        ConfigApi api = new ConfigApi();
        List<ConfigInfo> configInfos;

        try {
            configInfos = api.getConfigByUPN(upn);
        } catch (com.microsoft.identity.internal.test.labapi.ApiException ex) {
            throw new RuntimeException("Error retrieving lab user", ex);
        }

        final ConfigInfo pickedConfig = configInfos.get(0);
        CurrentLabConfig.configInfo = pickedConfig;

        return pickedConfig;
    }

    public static String getUpnForTest(LabUserQuery query) {
        final ConfigInfo configInfo = getConfigInfo(query);
        return configInfo.getUserInfo().getUpn();
    }

    public static String getPasswordForUser(final String username) {
        final ConfigInfo configInfo = getConfigInfoFromUpn(username);
        return LabSecretHelper.getPasswordForLab(configInfo.getUserInfo().getLabName());
    }

    public static String getPasswordForUser(final UserInfo userInfo) {
        return LabSecretHelper.getPasswordForLab(userInfo.getLabName());
    }

    public static Credential getCredentials(LabUserQuery query) {
        ConfigInfo configInfo;
        Credential credential = new Credential();

        configInfo = getConfigInfo(query);
        credential.userName = configInfo.getUserInfo().getUpn();
        credential.password = getPasswordForUser(configInfo.getUserInfo());

        return credential;
    }

}
