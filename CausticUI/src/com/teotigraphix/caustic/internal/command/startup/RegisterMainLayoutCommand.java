////////////////////////////////////////////////////////////////////////////////
// Copyright 2013 Michael Schmalle - Teoti Graphix, LLC
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
// http://www.apache.org/licenses/LICENSE-2.0 
// 
// Unless required by applicable law or agreed to in writing, software 
// distributed under the License is distributed on an "AS IS" BASIS, 
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and 
// limitations under the License
// 
// Author: Michael Schmalle, Principal Architect
// mschmalle at teotigraphix dot com
////////////////////////////////////////////////////////////////////////////////

package com.teotigraphix.caustic.internal.command.startup;

import roboguice.inject.ContextSingleton;
import android.app.Activity;

import com.google.inject.Inject;
import com.teotigraphix.android.components.support.MainLayout;
import com.teotigraphix.android.service.ITouchService;
import com.teotigraphix.caustic.controller.OSCMessage;
import com.teotigraphix.caustic.internal.command.OSCCommandBase;

/**
 * Registers the {@link ITouchService} with the {@link MainLayout}.
 * <ul>
 * <li>param[0] - Interger; the Resource id of the {@link MainLayout}.</li>
 * </ul>
 */
@ContextSingleton
public class RegisterMainLayoutCommand extends OSCCommandBase {

    @Inject
    Activity activity;

    @Inject
    ITouchService touchService;

    @Override
    public void execute(OSCMessage message) {
        // need the Activity and the R.id.main_layout passed
        int resourceId = Integer.parseInt(message.getParameters().get(0));
        MainLayout layout = (MainLayout)activity.findViewById(resourceId);
        layout.setTouchService(touchService);
    }

}
