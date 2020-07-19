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
package com.microsoft.identity.client.ui.automation.interaction;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.microsoft.identity.client.ui.automation.utils.UiAutomatorUtils;

import org.junit.Assert;

import static com.microsoft.identity.client.ui.automation.utils.CommonUtils.FIND_UI_ELEMENT_TIMEOUT;
import static org.junit.Assert.fail;

public class AadLoginComponentHandler implements ILoginComponentHandler {

    @Override
    public void handleEmailField(@NonNull final String username) {
        UiAutomatorUtils.handleInput("i0116", username);
        handleNextButton();
    }

    @Override
    public void handlePasswordField(@NonNull final String password) {
        UiAutomatorUtils.handleInput("i0118", password);
        handleNextButton();
    }

    @Override
    public void handleBackButton() {
        UiAutomatorUtils.handleButtonClick("idBtn_Back");
    }

    @Override
    public void handleNextButton() {
        UiAutomatorUtils.handleButtonClick("idSIButton9");
    }

    @Override
    public void handleAccountPicker(@NonNull final String username) {
        final UiDevice uiDevice =
                UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Confirm On Account Picker
        final UiObject accountPicker = UiAutomatorUtils.obtainUiObjectWithResourceId("tilesHolder");

        if (!accountPicker.waitForExists(FIND_UI_ELEMENT_TIMEOUT)) {
            fail("Account picker screen did not show up");
        }

        final UiObject account = uiDevice.findObject(new UiSelector()
                .text("Sign in with " + username + " work or school account.")
        );

        account.waitForExists(FIND_UI_ELEMENT_TIMEOUT);

        try {
            account.click();
        } catch (UiObjectNotFoundException e) {
            fail(e.getMessage());
        }
    }

    private UiObject getConsentScreen() {
        return UiAutomatorUtils.obtainUiObjectWithResourceId("consentHeader");
    }

    @Override
    public void confirmConsentPageReceived() {
        final UiObject consentScreen = getConsentScreen();
        Assert.assertTrue(consentScreen.waitForExists(FIND_UI_ELEMENT_TIMEOUT));
    }

    @Override
    public void acceptConsent() {
        confirmConsentPageReceived();
        handleNextButton();
    }

    public void declineConsent() {
        confirmConsentPageReceived();
        handleBackButton();
    }

    @Override
    public void handleSpeedBump() {
        // Confirm On Speed Bump Screen
        final UiObject speedBump = UiAutomatorUtils.obtainUiObjectWithResourceId("appConfirmTitle");

        if (!speedBump.waitForExists(FIND_UI_ELEMENT_TIMEOUT)) {
            fail("Speed Bump screen did not show up");
        }

        handleNextButton();
    }
}
