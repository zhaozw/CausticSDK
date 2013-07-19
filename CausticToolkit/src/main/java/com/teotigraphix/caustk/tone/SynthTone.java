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

package com.teotigraphix.caustk.tone;

import com.teotigraphix.caustk.controller.ICaustkController;
import com.teotigraphix.caustk.core.components.PatternSequencerComponent;
import com.teotigraphix.caustk.core.components.PatternSequencerComponent.Resolution;
import com.teotigraphix.caustk.core.components.SynthComponent;
import com.teotigraphix.caustk.core.components.VolumeComponent;

public class SynthTone extends Tone {

    public VolumeComponent getVolume() {
        return getComponent(VolumeComponent.class);
    }

    public SynthComponent getSynth() {
        return getComponent(SynthComponent.class);
    }

    public PatternSequencerComponent getPatternSequencer() {
        return getComponent(PatternSequencerComponent.class);
    }

    //----------------------------------
    // enabled
    //----------------------------------

    private boolean enabled = false;

    public final boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean value) {
        if (value == enabled)
            return;
        enabled = value;
        // firePropertyChange(TonePropertyKind.ENABLED, mEnabled);
    }

    //----------------------------------
    // muted
    //----------------------------------

    private boolean muted = false;

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean value) {
        if (value == muted)
            return;
        muted = value;
        // firePropertyChange(TonePropertyKind.MUTE, mMuted);
    }

    //----------------------------------
    // selected
    //----------------------------------

    private boolean selected = false;

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean value) {
        if (value == selected)
            return;
        selected = value;
        // firePropertyChange(TonePropertyKind.SELECTED, mSelected);
    }

    //----------------------------------
    // presetBank
    //----------------------------------

    private String presetBank;

    public final String getPresetBank() {
        return presetBank;
    }

    public final void setPresetBank(String value) {
        if (value == presetBank)
            return;
        presetBank = value;
        // firePropertyChange(TonePropertyKind.PRESET_BANK, mPresetBank);
    }

    public SynthTone(ICaustkController controller) {
        super(controller);
    }

    public void _setLength(int value) {
        //StepSequencer s = (StepSequencer) machine.getSequencer();
        //s.getTriggerMap().setLength(value);
    }

    public int _getLength() {
        //StepSequencer s = (StepSequencer) machine.getSequencer();
        //return s.getTriggerMap().getLength();
        return -1;
    }

    /**
     * Called from the {@link SystemSequencer} in the triggerOn observer.
     * 
     * @param step
     * @param pitch
     * @param gate
     * @param velocity
     * @param flags
     */
    public void _triggerOn(int step, int pitch, float gate, float velocity, int flags) {
        getComponent(PatternSequencerComponent.class).triggerOn(Resolution.SIXTEENTH, step, pitch,
                gate, velocity, flags);
    }

    /**
     * Called from the {@link SystemSequencer} in the triggerOff observer.
     * 
     * @param step
     * @param pitch
     */
    public void _triggerOff(int step, int pitch) {
        getComponent(PatternSequencerComponent.class).triggerOff(Resolution.SIXTEENTH, step, pitch);
    }

}
