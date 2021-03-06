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

package com.teotigraphix.caustk.sound;

import com.teotigraphix.caustic.core.IMemento;
import com.teotigraphix.caustic.effect.IEffectsRack;
import com.teotigraphix.caustic.machine.IMachine;
import com.teotigraphix.caustic.mixer.IMixerPanel;

public interface ICaustkSoundMixer {

    void pasteMasterChannel(IMemento memento);

    void pasteEffectChannel(IMachine machine, IMemento memento);

    void copyEffectChannel(IMachine machine, IMemento memento);

    void pasteMixerChannel(IMachine machine, IMemento memento);

    IMixerPanel getMixerPanel();

    IEffectsRack getEffectsRack();

}
