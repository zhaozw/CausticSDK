
package com.teotigraphix.caustk.core.components.bassline;

import com.teotigraphix.caustic.effect.IBasslineDistortionUnit.Program;
import com.teotigraphix.caustic.osc.BasslineDistortionMessage;
import com.teotigraphix.caustk.core.components.ToneComponent;

public class BasslineDistortion extends ToneComponent {
    
    //--------------------------------------------------------------------------
    // API :: Properties
    //--------------------------------------------------------------------------

    //----------------------------------
    // amount
    //----------------------------------

    private float amount = 15f;

    public float getAmount() {
        return amount;
    }

    public float getAmount(boolean restore) {
        return BasslineDistortionMessage.DISTORTION_AMOUNT.query(getEngine(), getToneIndex());
    }

    public void setAmount(float value) {
        if (value == amount)
            return;
        if (value < 0f || value > 20f)
            throw newRangeException(BasslineDistortionMessage.DISTORTION_AMOUNT.toString(),
                    "0..20", value);
        amount = value;
        BasslineDistortionMessage.DISTORTION_AMOUNT.send(getEngine(), getToneIndex(), amount);
    }

    //----------------------------------
    // postGain
    //----------------------------------

    private float postGain = 0.2f;

    public float getPostGain() {
        return postGain;
    }

    public float getPostGain(boolean restore) {
        return BasslineDistortionMessage.DISTORTION_POSTGAIN.query(getEngine(), getToneIndex());
    }

    public void setPostGain(float value) {
        if (value == postGain)
            return;
        if (value < 0f || value > 1f)
            throw newRangeException(BasslineDistortionMessage.DISTORTION_POSTGAIN.toString(),
                    "0..1", value);
        postGain = value;
        BasslineDistortionMessage.DISTORTION_POSTGAIN.send(getEngine(), getToneIndex(), postGain);
    }

    //----------------------------------
    // preGain
    //----------------------------------

    private float preGain = 4.05f;

    public float getPreGain() {
        return preGain;
    }

    public float getPreGain(boolean restore) {
        return BasslineDistortionMessage.DISTORTION_PREGAIN.query(getEngine(), getToneIndex());
    }

    public void setPreGain(float value) {
        if (value == preGain)
            return;
        if (value < 0f || value > 5f)
            throw newRangeException(BasslineDistortionMessage.DISTORTION_PREGAIN.toString(),
                    "0..5", value);
        preGain = value;
        BasslineDistortionMessage.DISTORTION_PREGAIN.send(getEngine(), getToneIndex(), preGain);
    }

    //----------------------------------
    // program
    //----------------------------------

    private Program program = Program.OFF;

    public Program getProgram() {
        return program;
    }

    public Program getProgram(boolean restore) {
        return Program.toType(BasslineDistortionMessage.DISTORTION_PROGRAM.query(getEngine(),
                getToneIndex()));
    }

    public void setProgram(Program value) {
        if (value == program)
            return;
        program = value;
        BasslineDistortionMessage.DISTORTION_PROGRAM.send(getEngine(), getToneIndex(),
                program.getValue());
    }

    public BasslineDistortion() {
    }

    @Override
    public void restore() {
        setAmount(getAmount(true));
        setPostGain(getPostGain(true));
        setPreGain(getPreGain(true));
        setProgram(getProgram(true));
    }

}