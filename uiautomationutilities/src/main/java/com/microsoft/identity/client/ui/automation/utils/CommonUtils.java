//  Copyright (c) Microsoft Corporation.
//  All rights reserved.
//
//  This code is licensed under the MIT License.
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files(the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions :
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
package com.microsoft.identity.client.ui.automation.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;

import java.util.concurrent.TimeUnit;

public class CommonUtils {

    public final static long FIND_UI_ELEMENT_TIMEOUT = TimeUnit.SECONDS.toMillis(30);

    /**
     * Launch (open) the supplied package on the device
     *
     * @param packageName the package name to launch
     */
    public static void launchApp(@NonNull final String packageName) {
        final Context context = ApplicationProvider.getApplicationContext();
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);  //sets the intent to start your app
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);  //clear out any previous task, i.e., make sure it starts on the initial screen
        context.startActivity(intent);
    }

    /**
     * Grant (allow) the requested permission for the current package i.e the app that is currently
     * open on the device and requesting a permission.
     * <p>
     * When any app requests a permission on Android, it shows an Android system dialog on the UI,
     * this dialog looks the same regardless of the app or the permission being requested. This API
     * just responds to that by accepting that permission.
     */
    public static void grantPackagePermission() {
        UiAutomatorUtils.handleButtonClick("com.android.packageinstaller:id/permission_allow_button");
    }

    /**
     * Check if the supplied permission has already been granted for given package.
     *
     * @param packageName the package for which to check if permission was granted
     * @param permission  the permission which to check for
     * @return a boolean indicating whether permission was already granted or not
     */
    public static boolean hasPermission(@NonNull final String packageName, @NonNull final String permission) {
        final Context context = ApplicationProvider.getApplicationContext();
        final PackageManager packageManager = context.getPackageManager();
        return PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(
                permission,
                packageName
        );
    }

    /**
     * Get the complete resource id by combining the package name and the actual resource id
     *
     * @param appPackageName     the package name for the app
     * @param internalResourceId the resource id for the element
     * @return
     */
    public static String getResourceId(@NonNull final String appPackageName, @NonNull final String internalResourceId) {
        return appPackageName + ":id/" + internalResourceId;
    }

    /**
     * Checks if the supplied String could be a valid Android package name
     *
     * @param hint the String for which to check if it is a package name
     * @return a boolean indicating whether the supplied String is a valid Android package name
     */
    public static boolean isStringPackageName(@NonNull final String hint) {
        return hint.contains("."); // best guess
    }
}
