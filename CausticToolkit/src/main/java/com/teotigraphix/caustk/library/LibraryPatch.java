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

package com.teotigraphix.caustk.library;

import java.io.File;
import java.io.StringReader;

import com.teotigraphix.caustic.core.IMemento;
import com.teotigraphix.caustic.core.XMLMemento;
import com.teotigraphix.caustic.machine.MachineType;

/**
 * presetFile -
 * <code>/root/libraries/myLibrary/patches/d34sd9..sd23.bassline</code>
 */
public class LibraryPatch extends LibraryItem {

    //----------------------------------
    // machineType
    //----------------------------------

    private MachineType machineType;

    public MachineType getMachineType() {
        return machineType;
    }

    public void setMachineType(MachineType machineType) {
        this.machineType = machineType;
    }

    //----------------------------------
    // data
    //----------------------------------

    private String data;

    /**
     * Returns the String {@link IMemento} data that was saved from the current
     * machine.
     */
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    //----------------------------------
    // presetFile
    //----------------------------------

    /**
     * Returns the file, not the path of the preset {@link File}.
     * <p>
     * The file extension is correctly created using the
     * {@link #getMachineType()}.
     */
    public File getPresetFile() {
        return new File(getId().toString() + "." + machineType.getValue());
    }

    //----------------------------------
    // memento
    //----------------------------------

    /**
     * Returns the {@link IMemento} using the {@link #getData()} settings.
     */
    public IMemento getMemento() {
        return XMLMemento.createReadRoot(new StringReader(getData()));
    }

    public LibraryPatch() {
    }

}
