
package com.teotigraphix.caustk.core.components;

import com.teotigraphix.caustic.core.ICausticEngine;
import com.teotigraphix.caustic.core.IRestore;
import com.teotigraphix.caustic.internal.utils.ExceptionUtils;
import com.teotigraphix.caustk.tone.CaustkTone;

public abstract class ToneComponent implements IRestore {

    //----------------------------------
    // tone
    //----------------------------------

    private transient CaustkTone tone;

    public CaustkTone getTone() {
        return tone;
    }

    public void setTone(CaustkTone value) {
        tone = value;
    }

    protected int getToneIndex() {
        return tone.getIndex();
    }

    protected ICausticEngine getEngine() {
        return tone.getEngine();
    }

    //--------------------------------------------------------------------------
    // Constructor
    //--------------------------------------------------------------------------

    public ToneComponent() {
    }

    //--------------------------------------------------------------------------
    // ISerialize API :: Methods
    //--------------------------------------------------------------------------

    public String serialize() {
        return tone.getController().getSerializeService().toUnString(this);
    }

    /**
     * Returns a new {@link IllegalArgumentException} for an error in OSC range.
     * 
     * @param control The OSC control involved.
     * @param range The accepted range.
     * @param value The value that is throwing the range exception.
     * @return A new {@link IllegalArgumentException}.
     */
    protected final RuntimeException newRangeException(String control, String range, Object value) {
        return ExceptionUtils.newRangeException(control, range, value);
    }
}