////////////////////////////////////////////////////////////////////////////////
// Copyright 2011 Michael Schmalle - Teoti Graphix, LLC
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

package com.teotigraphix.caustic.internal.utils;

import com.teotigraphix.caustic.core.IMemento;
import com.teotigraphix.caustic.core.IPersist;
import com.teotigraphix.caustic.core.XMLMemento;

/**
 * @author Michael Schmalle
 * @copyright Teoti Graphix, LLC
 * @since 1.0
 */
public final class MementoUtil {
    public final static int booleanToInt(boolean value) {
        return !value ? 0 : 1;
    }

    public final static boolean intToBoolean(int value) {
        return value == 0 ? false : true;
    }

    public final static boolean floatToBoolean(float value) {
        return value == 0f ? false : true;
    }

    public final static void copy(IPersist from, IPersist to, String rootTag) {
        IMemento memento = XMLMemento.createWriteRoot(rootTag);
        from.copy(memento);
        to.paste(memento);
    }
}
