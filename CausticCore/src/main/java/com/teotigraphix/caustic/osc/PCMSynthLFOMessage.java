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

package com.teotigraphix.caustic.osc;

/**
 * The {@link PCMSynthLFOMessage} holds all OSC messages associated with the
 * {@link IPCMSynthLFO1} API.
 * 
 * @author Michael Schmalle
 * @copyright Teoti Graphix, LLC
 * @since 1.0
 */
public class PCMSynthLFOMessage extends CausticMessage {

    /**
     * Message: <code>/caustic/[machine_index]/lfo_depth [value]</code>
     * <p>
     * <strong>Default</strong>: <code>0.0</code>
     * <p>
     * <strong>Parameters</strong>:
     * <ul>
     * <li><strong>machine_index</strong>: The machine index.</li>
     * <li><strong>value</strong>: (0.0..1.0)</li>
     * </ul>
     * <p>
     * <strong>Returns</strong>: <code>float</code>
     * 
     * @see ILFOComponent#getDepth()
     * @see ILFOComponent#setDepth(float)
     */
    public static final PCMSynthLFOMessage LFO_DEPTH = new PCMSynthLFOMessage(
            "/caustic/${0}/lfo_depth ${1}");

    /**
     * Message: <code>/caustic/[machine_index]/lfo_rate [value]</code>
     * <p>
     * <strong>Default</strong>: <code>1</code>
     * <p>
     * <strong>Parameters</strong>:
     * <ul>
     * <li><strong>machine_index</strong>: The machine index.</li>
     * <li><strong>value</strong>: (1..12)</li>
     * </ul>
     * <p>
     * <strong>Returns</strong>: <code>int</code>
     * 
     * @see ILFOComponent#getRate()
     * @see ILFOComponent#setRate(int)
     */
    public static final PCMSynthLFOMessage LFO_RATE = new PCMSynthLFOMessage(
            "/caustic/${0}/lfo_rate ${1}");

    /**
     * Message: <code>/caustic/[machine_index]/lfo_target [value]</code>
     * <p>
     * <strong>Default</strong>: <code>0</code>
     * <p>
     * <strong>Parameters</strong>:
     * <ul>
     * <li><strong>machine_index</strong>: The machine index.</li>
     * <li><strong>value</strong>: (0..3)</li>
     * </ul>
     * <p>
     * <strong>Returns</strong>: <code>int</code>
     * 
     * @see IPCMSynthLFO1#getTarget()
     * @see IPCMSynthLFO1#setTarget(LFOTarget)
     */
    public static final PCMSynthLFOMessage LFO_TARGET = new PCMSynthLFOMessage(
            "/caustic/${0}/lfo_target ${1}");

    /**
     * Message: <code>/caustic/[machine_index]/lfo_waveform [value]</code>
     * <p>
     * <strong>Default</strong>: <code>0</code>
     * <p>
     * <strong>Parameters</strong>:
     * <ul>
     * <li><strong>machine_index</strong>: The machine index.</li>
     * <li><strong>value</strong>: (0..3)</li>
     * </ul>
     * <p>
     * <strong>Returns</strong>: <code>int</code>
     * 
     * @see IPCMSynthLFO1#getWaveform()
     * @see IPCMSynthLFO1#setWaveForm(WaveForm)
     */
    public static final PCMSynthLFOMessage LFO_WAVEFORM = new PCMSynthLFOMessage(
            "/caustic/${0}/lfo_waveform ${1}");

    PCMSynthLFOMessage(String message) {
        super(message);
    }
}
