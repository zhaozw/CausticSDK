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

package com.teotigraphix.caustk.controller.command;

import com.teotigraphix.caustic.core.IDispatcher;
import com.teotigraphix.caustk.controller.ICaustkController;
import com.teotigraphix.caustk.controller.IControllerAPI;

public class CommandContext {

    private final ICaustkController controller;

    public <T extends IControllerAPI> T api(Class<T> clazz) {
        return controller.api(clazz);
    }

    private final OSCMessage message;

    public OSCMessage getMessage() {
        return message;
    }

    protected IDispatcher getDispatcher() {
        return controller.getDispatcher();
    }

    public CommandContext(ICaustkController controller, OSCMessage message) {
        this.controller = controller;
        this.message = message;
    }

}
