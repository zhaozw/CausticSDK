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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.teotigraphix.caustic.machine.MachineType;
import com.teotigraphix.caustk.tone.Tone;

/**
 * The {@link Library} is the main serialized json file that gets saved along
 * with the caustic song file.
 * <p>
 * {@link Library} is a model with add/remove and access to its sub items.
 */
public class Library {

    protected File getPresetsDirectory() {
        return new File(directory, "presets");
    }

    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    //----------------------------------
    // patches
    //----------------------------------

    private Map<UUID, LibraryPatch> patches = new HashMap<UUID, LibraryPatch>();

    public Collection<LibraryPatch> getPatches() {
        return patches.values();
    }

    public void addPatch(LibraryPatch patch) {
        if (patches.containsKey(patch.getId()))
            return;
        patches.put(patch.getId(), patch);
    }

    public void removePatch(LibraryPatch patch) {
        if (!patches.containsKey(patch.getId()))
            return;
        patches.remove(patch.getId());
    }

    //----------------------------------
    // phrases
    //----------------------------------

    private Map<UUID, LibraryPhrase> phrases = new HashMap<UUID, LibraryPhrase>();

    public Collection<LibraryPhrase> getPhrases() {
        return phrases.values();
    }

    public void addPhrase(LibraryPhrase patch) {
        if (phrases.containsKey(patch.getId()))
            return;
        phrases.put(patch.getId(), patch);
    }

    public void removePhrase(LibraryPhrase patch) {
        if (!phrases.containsKey(patch.getId()))
            return;
        phrases.remove(patch.getId());
    }

    //----------------------------------
    // scenes
    //----------------------------------

    private Map<UUID, LibraryScene> scenes = new HashMap<UUID, LibraryScene>();

    public List<LibraryScene> getScenes() {
        return new ArrayList<LibraryScene>(scenes.values());
    }

    public void addScene(LibraryScene patch) {
        if (scenes.containsKey(patch.getId()))
            return;
        scenes.put(patch.getId(), patch);
    }

    public void removeScene(LibraryScene patch) {
        if (!scenes.containsKey(patch.getId()))
            return;
        scenes.remove(patch.getId());
    }

    //----------------------------------
    // automations
    //----------------------------------

    @SuppressWarnings("unused")
    private Map<UUID, LibraryAutomation> automations = new HashMap<UUID, LibraryAutomation>();

    private File directory;

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File value) {
        directory = value;
    }

    public Library() {
    }

    /**
     * Creates the sub directories of the library on creation.
     */
    public void mkdirs() throws IOException {
        getPresetsDirectory().mkdir();
    }

    public void delete() throws IOException {
        FileUtils.deleteDirectory(directory);
        if (directory.exists())
            throw new IOException("Library " + directory.getAbsolutePath() + " was not deleted.");
    }

    public LibraryScene findSceneById(UUID id) {
        for (LibraryScene item : scenes.values()) {
            if (item.getId().equals(id))
                return item;
        }
        return null;
    }

    public LibraryPatch findPatchById(UUID uuid) {
        for (LibraryPatch item : patches.values()) {
            if (item.getId().equals(uuid))
                return item;
        }
        return null;
    }

    public LibraryPhrase findPhraseById(UUID uuid) {
        for (LibraryPhrase item : phrases.values()) {
            if (item.getId().equals(uuid))
                return item;
        }
        return null;
    }

    public List<LibraryScene> findScenesByTag(String tag) {
        List<LibraryScene> result = new ArrayList<LibraryScene>();
        for (LibraryScene item : getScenes()) {
            if (item.hasTag(tag)) {
                result.add(item);
            }
        }
        return result;
    }

    public List<LibraryPatch> findPatchByTag(String tag) {
        List<LibraryPatch> result = new ArrayList<LibraryPatch>();
        for (LibraryPatch item : getPatches()) {
            if (item.hasTag(tag)) {
                result.add(item);
            }
        }
        return result;
    }

    public List<LibraryPhrase> findPhrasesByTag(String tag) {
        List<LibraryPhrase> result = new ArrayList<LibraryPhrase>();
        for (LibraryPhrase item : getPhrases()) {
            if (item.hasTag(tag)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Returns a {@link File} with the correct absolute path of the preset in
     * the <code>/MyLibrary/presets</code> directory.
     * 
     * @param preset The file name of the preset file.
     */
    public File getPresetFile(File preset) {
        return new File(getPresetsDirectory(), preset.getName());
    }

    /**
     * Returns all {@link LibraryPhrase}s in the {@link Library} for the tone's
     * {@link MachineType}.
     * 
     * @param tone The tone used to search.
     * @return A {@link List} of {@link LibraryPhrase}s that are of the same
     *         {@link MachineType} as the tone.
     */
    public List<LibraryPhrase> findPhrasesForTone(Tone tone) {
        String type = tone.getMachine().getType().getValue();
        return findPhrasesByTag(type);
    }

    public LibraryScene getDefaultScene() {
        LibraryScene libraryScene = findScenesByTag("DefaultScene").get(0);
        return libraryScene;
    }

    public String getName() {
        return getDirectory().getName();
    }

}
