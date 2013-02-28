////////////////////////////////////////////////////////////////////////////////
// Copyright 2012 Michael Schmalle - Teoti Graphix, LLC
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

package com.teotigraphix.caustic.internal.song;

import com.teotigraphix.caustic.song.IPattern;

/**
 * @author Michael Schmalle
 * @copyright Teoti Graphix, LLC
 * @since 1.0
 */
public class SongPatternInfo {

    IPattern mPattern;

    public final IPattern getPattern() {
        return mPattern;
    }

    int mStartBar;

    public final int getStartBar() {
        return mStartBar;
    }

    int mEndBar;

    public final int getEndBar() {
        return mEndBar;
    }

    public SongPatternInfo(IPattern pattern, int startBar, int endBar) {
        mPattern = pattern;
        mStartBar = startBar;
        mEndBar = endBar;
    }

}
