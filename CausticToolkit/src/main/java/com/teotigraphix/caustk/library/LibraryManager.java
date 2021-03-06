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
import java.util.List;
import java.util.UUID;

import org.androidtransfuse.event.EventObserver;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import com.teotigraphix.caustic.core.CausticException;
import com.teotigraphix.caustic.core.IMemento;
import com.teotigraphix.caustic.core.XMLMemento;
import com.teotigraphix.caustic.desktop.RuntimeUtils;
import com.teotigraphix.caustic.internal.rack.Rack;
import com.teotigraphix.caustic.internal.utils.PatternUtils;
import com.teotigraphix.caustic.machine.IMachine;
import com.teotigraphix.caustic.machine.MachineType;
import com.teotigraphix.caustic.osc.OutputPanelMessage;
import com.teotigraphix.caustic.osc.PatternSequencerMessage;
import com.teotigraphix.caustic.osc.RackMessage;
import com.teotigraphix.caustic.sequencer.IStepPhrase.Resolution;
import com.teotigraphix.caustk.controller.ICaustkController;
import com.teotigraphix.caustk.library.vo.EffectRackInfo;
import com.teotigraphix.caustk.library.vo.MetadataInfo;
import com.teotigraphix.caustk.library.vo.MixerPanelInfo;
import com.teotigraphix.caustk.library.vo.RackInfo;
import com.teotigraphix.caustk.project.IProjectManager;
import com.teotigraphix.caustk.project.IProjectManager.OnProjectManagerChange;
import com.teotigraphix.caustk.project.IProjectManager.ProjectManagerChangeKind;
import com.teotigraphix.caustk.tone.Tone;

public class LibraryManager implements ILibraryManager {

    //--------------------------------------------------------------------------
    // Variables
    //--------------------------------------------------------------------------

    private LibraryRegistry registry;

    private ICaustkController controller;

    private File librariesDirectory;

    private Library selectedLibrary;

    //--------------------------------------------------------------------------
    // API
    //--------------------------------------------------------------------------

    //----------------------------------
    // selectedLibrary
    //----------------------------------

    @Override
    public Library getSelectedLibrary() {
        return selectedLibrary;
    }

    @Override
    public void setSelectedLibrary(Library value) {
        if (value == selectedLibrary)
            return;

        selectedLibrary = value;
        controller.getDispatcher().trigger(
                new OnLibraryManagerSelectedLibraryChange(selectedLibrary));
    }

    //----------------------------------
    // librariesDirectory
    //----------------------------------

    @Override
    public File getLibrariesDirectory() {
        return librariesDirectory;
    }

    //----------------------------------
    // libraries
    //----------------------------------

    @Override
    public int getLibraryCount() {
        return registry.getLibraries().size();
    }

    @Override
    public Collection<Library> getLibraries() {
        return registry.getLibraries();
    }

    public LibraryManager(ICaustkController controller) {
        this.controller = controller;

        File root = controller.getConfiguration().getApplicationRoot();
        if (!root.exists())
            throw new RuntimeException("Application root not specified");

        librariesDirectory = new File(root, "libraries");
        if (!librariesDirectory.exists())
            librariesDirectory.mkdirs();

        registry = new LibraryRegistry(librariesDirectory);

        controller.getDispatcher().register(OnProjectManagerChange.class,
                new EventObserver<OnProjectManagerChange>() {
                    @Override
                    public void trigger(OnProjectManagerChange object) {
                        if (object.getKind() == ProjectManagerChangeKind.CREATE) {
                            //onProjectManagerCreateHandler();
                        } else if (object.getKind() == ProjectManagerChangeKind.LOAD) {
                            onProjectManagerLoadHandler();
                        } else if (object.getKind() == ProjectManagerChangeKind.SAVE) {
                            onProjectManagerSaveHandler();
                        } else if (object.getKind() == ProjectManagerChangeKind.EXIT) {
                            onProjectManagerExitHandler();
                        }
                    }
                });
    }

    protected void onProjectManagerSaveHandler() {
        saveSessionProperties();
    }

    protected void onProjectManagerLoadHandler() {
        load();
        String id = controller.getProjectManager().getSessionPreferences()
                .getString("selectedLibrary");
        if (id != null) {
            selectedLibrary = registry.getLibrary(id);
        }
    }

    private void saveSessionProperties() {
        // if the project has selected a library, save it
        if (selectedLibrary != null) {
            controller.getProjectManager().getSessionPreferences()
                    .put("selectedLibrary", selectedLibrary.getId());
        }
    }

    protected void onProjectManagerExitHandler() {
        registry = new LibraryRegistry(librariesDirectory);
        selectedLibrary = null;
    }

    @Override
    public Library getLibraryById(UUID id) {
        return registry.getLibrary(id);
    }

    @Override
    public Library getLibraryByName(String name) {
        for (Library library : registry.getLibraries()) {
            if (library.getName().equals(name))
                return library;
        }
        return null;
    }

    public List<LibraryPatch> findPatchesByTag(String tag) {
        List<LibraryPatch> result = new ArrayList<LibraryPatch>();
        for (Library library : registry.getLibraries()) {
            for (LibraryPatch item : library.getPatches()) {
                if (item.hasTag(tag)) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    public List<LibraryPhrase> findPhrasesByTag(String tag) {
        List<LibraryPhrase> result = new ArrayList<LibraryPhrase>();
        for (Library library : registry.getLibraries()) {
            for (LibraryPhrase item : library.getPhrases()) {
                if (item.hasTag(tag)) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    public List<LibraryScene> findScenesByTag(String tag) {
        List<LibraryScene> result = new ArrayList<LibraryScene>();
        for (Library library : registry.getLibraries()) {
            for (LibraryScene item : library.getScenes()) {
                if (item.hasTag(tag)) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    /**
     * Saves the {@link Library}s to disk using the libraries directory
     * location.
     * <p>
     * Note: This DOES NOT get called from the {@link IProjectManager#save()}
     * method. The client needs to save a library by calling {@link #save()}.
     * 
     * @throws IOException
     */
    @Override
    public void save() throws IOException {
        for (Library library : registry.getLibraries()) {
            saveLibrary(library);
        }
    }

    /**
     * Loads the entire <code>libraries</code> directory into the manager.
     * <p>
     * Each sub directory located within the <code>libraries</code> directory
     * will be created as a {@link Library} instance.
     */
    @Override
    public void load() {
        Collection<File> dirs = FileUtils.listFilesAndDirs(librariesDirectory, new IOFileFilter() {
            @Override
            public boolean accept(File arg0, String arg1) {
                return false;
            }

            @Override
            public boolean accept(File arg0) {
                return false;
            }
        }, new IOFileFilter() {
            @Override
            public boolean accept(File file, String name) {
                if (file.getParentFile().getName().equals("libraries"))
                    return true;
                return false;
            }

            @Override
            public boolean accept(File file) {
                if (file.getParentFile().getName().equals("libraries"))
                    return true;
                return false;
            }
        });

        for (File directory : dirs) {
            if (directory.equals(librariesDirectory))
                continue;

            loadLibrary(directory.getName());
        }
    }

    @Override
    public void importSong(Library library, File causticFile) throws IOException {
        //----------------------------------------------------------------------
        // clear the core rack
        RackMessage.BLANKRACK.send(controller);

        Rack rack = new Rack(controller.getFactory(), true);
        rack.setEngine(controller);

        // load the file into the rack.
        try {
            rack.loadSong(causticFile.getAbsolutePath());
        } catch (CausticException e) {
            e.printStackTrace();
            throw new IOException(e);
        }

        // restore the rack
        //rack.restore();
        rack.getOutputPanel().restore();
        rack.getMixerPanel().restore();
        rack.getEffectsRack().restore();

        //loadLibraryPatches(library, rack);
        loadLibraryScene(library, causticFile, rack);
        loadLibraryPhrases(library, rack);

        // clear the core rack
        RackMessage.BLANKRACK.send(controller);
    }

    private LibraryScene createDefaultScene() throws CausticException {
        RackMessage.BLANKRACK.send(controller);

        Rack rack = new Rack(controller.getFactory(), true);
        rack.setEngine(controller);

        // create the default setup
        rack.addMachine("SubSynth", MachineType.SUBSYNTH);
        rack.addMachine("PCMSynth1", MachineType.PCMSYNTH);
        rack.addMachine("PCMSynth2", MachineType.PCMSYNTH);
        rack.addMachine("Bassline1", MachineType.BASSLINE);
        rack.addMachine("Bassline2", MachineType.BASSLINE);
        rack.addMachine("Beatbox", MachineType.BEATBOX);

        //rack.populateMachines();
        rack.getOutputPanel().restore();
        rack.getMixerPanel().restore();
        rack.getEffectsRack().restore();

        LibraryScene libraryScene = new LibraryScene();
        libraryScene.setId(UUID.randomUUID());
        MetadataInfo metadataInfo = new MetadataInfo();
        metadataInfo.addTag("DefaultScene");
        libraryScene.setMetadataInfo(metadataInfo);
        libraryScene.setRackInfo(LibrarySerializerUtils.createRackInfo(rack));
        libraryScene.setMixerInfo(LibrarySerializerUtils.createMixerPanelInfo(controller
                .getSoundMixer().getMixerPanel()));
        libraryScene.setEffectRackInfo(LibrarySerializerUtils.createEffectRackInfo(controller
                .getSoundMixer().getEffectsRack()));

        RackMessage.BLANKRACK.send(controller);
        return libraryScene;
    }

    @Override
    public Library createLibrary(String name) throws IOException {
        File newDirectory = new File(librariesDirectory, name);
        //if (newDirectory.exists())
        //    throw new CausticException("Library already exists " + newDirectory.getAbsolutePath());
        newDirectory.mkdir();

        // create a default scene for every new Library
        LibraryScene defaultScene = null;
        try {
            defaultScene = createDefaultScene();
        } catch (CausticException e) {
            e.printStackTrace();
        }

        Library library = new Library();
        library.setId(UUID.randomUUID());
        library.setDirectory(newDirectory);
        library.mkdirs();

        registry.addLibrary(library);
        library.addScene(defaultScene);

        return library;
    }

    @Override
    public LibraryScene createLibraryScene(MetadataInfo info) {
        LibraryScene scene = new LibraryScene();
        scene.setMetadataInfo(info);
        scene.setId(null);

        //--------------------------------------
        RackInfo rackInfo = new RackInfo();
        XMLMemento memento = XMLMemento.createWriteRoot("rack");
        for (int i = 0; i < 6; i++) {
            Tone tone = controller.getSoundSource().getTone(i);
            IMachine machine = tone.getMachine();
            if (machine != null) {
                IMemento child = memento.createChild("machine");
                child.putInteger("index", i);
                child.putInteger("active", machine != null ? 1 : 0);
                child.putString("id", machine.getId());
                child.putString("type", machine.getType().getValue());
            }
        }

        rackInfo.setData(memento.toString());

        scene.setRackInfo(rackInfo);

        MixerPanelInfo mixerPanelInfo = LibrarySerializerUtils.createMixerPanelInfo(controller
                .getSoundMixer().getMixerPanel());
        scene.setMixerInfo(mixerPanelInfo);

        EffectRackInfo effectRackInfo = LibrarySerializerUtils.createEffectRackInfo(controller
                .getSoundMixer().getEffectsRack());
        scene.setEffectRackInfo(effectRackInfo);

        //TagUtils.addDefaultTags(name, rack, scene);

        return scene;
    }

    private void loadLibraryScene(Library library, File causticFile, Rack rack) throws IOException {
        String name = causticFile.getName().replace(".caustic", "");
        LibraryScene scene = new LibraryScene();
        scene.setMetadataInfo(new MetadataInfo());

        scene.setId(UUID.randomUUID());
        library.addScene(scene);

        //--------------------------------------
        RackInfo rackInfo = new RackInfo();
        XMLMemento memento = XMLMemento.createWriteRoot("rack");
        for (int i = 0; i < 6; i++) {
            IMachine machine = rack.getMachine(i);
            LibraryPatch patch = null;

            if (machine != null) {
                patch = new LibraryPatch();
                patch.setMachineType(machine.getType());
                patch.setMetadataInfo(new MetadataInfo());
                patch.setId(UUID.randomUUID());
                TagUtils.addDefaultTags(machine, patch);
                relocatePresetFile(machine, library, patch);
                library.addPatch(patch);

                IMemento child = memento.createChild("machine");
                child.putInteger("index", i);
                child.putInteger("active", machine != null ? 1 : 0);

                if (patch != null)
                    child.putString("patchId", patch.getId().toString());

                child.putString("id", machine.getId());
                child.putString("type", machine.getType().getValue());
            }
        }

        rackInfo.setData(memento.toString());

        scene.setRackInfo(rackInfo);

        MixerPanelInfo mixerPanelInfo = LibrarySerializerUtils.createMixerPanelInfo(rack);
        scene.setMixerInfo(mixerPanelInfo);

        EffectRackInfo effectRackInfo = LibrarySerializerUtils.createEffectRackInfo(rack);
        scene.setEffectRackInfo(effectRackInfo);

        TagUtils.addDefaultTags(name, rack, scene);
    }

    private void loadLibraryPhrases(Library library, Rack rack) {
        for (int i = 0; i < 6; i++) {
            IMachine machine = rack.getMachine(i);
            if (machine != null) {

                String result = PatternSequencerMessage.QUERY_PATTERNS_WITH_DATA.queryString(
                        controller, i);

                if (result == null)
                    continue;

                for (String patternName : result.split(" ")) {
                    int bankIndex = PatternUtils.toBank(patternName);
                    int patternIndex = PatternUtils.toPattern(patternName);

                    // set the current bank and pattern of the machine to query
                    // the string pattern data
                    PatternSequencerMessage.BANK.send(controller, i, bankIndex);
                    PatternSequencerMessage.PATTERN.send(controller, i, patternIndex);

                    //----------------------------------------------------------------
                    // Load Pattern
                    //----------------------------------------------------------------

                    // load one phrase per pattern; load ALL patterns
                    // as caustic machine patterns
                    int length = (int)PatternSequencerMessage.NUM_MEASURES.query(controller, i);
                    float tempo = OutputPanelMessage.BPM.query(controller);
                    String noteData = PatternSequencerMessage.QUERY_NOTE_DATA.queryString(
                            controller, i);

                    LibraryPhrase phrase = new LibraryPhrase();
                    phrase.setMetadataInfo(new MetadataInfo());
                    phrase.setId(UUID.randomUUID());
                    phrase.setLength(length);
                    phrase.setTempo(tempo);
                    phrase.setMachineType(machine.getType());
                    phrase.setNoteData(noteData);
                    phrase.setResolution(calculateResolution(noteData));
                    TagUtils.addDefaultTags(phrase);
                    library.addPhrase(phrase);
                }
            }
        }
    }

    private Resolution calculateResolution(String data) {
        // TODO This is totally inefficient, needs to be lazy loaded
        // push the notes into the machines sequencer
        float smallestGate = 1f;
        String[] notes = data.split("\\|");
        for (String noteData : notes) {
            String[] split = noteData.split(" ");

            float start = Float.parseFloat(split[0]);
            float end = Float.parseFloat(split[3]);
            float gate = end - start;
            smallestGate = Math.min(smallestGate, gate);
        }

        Resolution result = Resolution.SIXTEENTH;
        if (smallestGate <= Resolution.SIXTYFOURTH.getValue() * 4)
            result = Resolution.SIXTYFOURTH;
        else if (smallestGate <= Resolution.THIRTYSECOND.getValue() * 4)
            result = Resolution.THIRTYSECOND;
        else if (smallestGate <= Resolution.SIXTEENTH.getValue() * 4)
            result = Resolution.SIXTEENTH;

        return result;
    }

    protected void relocatePresetFile(IMachine machine, Library library, LibraryPatch patch)
            throws IOException {
        String id = patch.getId().toString();
        machine.savePreset(id);
        File presetFile = RuntimeUtils.getCausticPresetsFile(machine.getType().toString(), id);
        if (!presetFile.exists())
            throw new IOException("Preset file does not exist");

        File presetsDirectory = library.getPresetsDirectory();
        File destFile = new File(presetsDirectory, presetFile.getName());
        FileUtils.copyFile(presetFile, destFile);
        FileUtils.deleteQuietly(presetFile);
    }

    @Override
    public void saveLibrary(Library library) throws IOException {
        String data = controller.getSerializeService().toString(library);
        File file = new File(library.getDirectory(), "library.ctk");
        FileUtils.writeStringToFile(file, data);
    }

    @Override
    public Library loadLibrary(String name) {
        File directory = new File(librariesDirectory, name);
        File file = new File(directory, "library.ctk");

        Library library = controller.getSerializeService().fromFile(file, Library.class);
        registry.addLibrary(library);

        controller.getDispatcher().trigger(new OnLibraryManagerLoadComplete(library));

        return library;
    }

    @Override
    public void clear() {
        registry = new LibraryRegistry(librariesDirectory);
    }

    @Override
    public void delete() throws IOException {
        for (Library library : registry.getLibraries()) {
            library.delete();
        }
        clear();
    }

}

/*


<?xml version="1.0" encoding="UTF-8"?>
<mixer>
    <master eq_bass="0.19565237" eq_high="0.100000024" eq_mid="0.0" id="-1" index="-1" volume="1.1478264"/>
    <channels>
        <channel delay_send="0.504348" eq_bass="-0.008696437" eq_high="0.06956482" eq_mid="1.1920929E-7" id="0" index="0" mute="0" pan="0.0" reverb_send="0.05869567" solo="0" stereo_width="0.99609375" volume="1.2782611"/>
        <channel delay_send="0.5000001" eq_bass="-1.0" eq_high="0.02666676" eq_mid="0.034783125" id="1" index="1" mute="0" pan="0.0" reverb_send="0.0" solo="0" stereo_width="0.0" volume="1.3652176"/>
        <channel delay_send="0.0" eq_bass="1.0" eq_high="-0.008695722" eq_mid="0.0" id="2" index="2" mute="0" pan="0.0" reverb_send="0.0" solo="0" stereo_width="0.0" volume="1.2782608"/>
        <channel delay_send="0.52173936" eq_bass="-1.0" eq_high="-2.9802322E-7" eq_mid="-0.026086628" id="3" index="3" mute="0" pan="0.18260896" reverb_send="0.07173912" solo="0" stereo_width="0.0" volume="1.4383385"/>
        <channel delay_send="0.0" eq_bass="-0.017391145" eq_high="0.38260782" eq_mid="0.0" id="4" index="4" mute="0" pan="0.0" reverb_send="0.07391316" solo="0" stereo_width="0.53269273" volume="1.3913045"/>
        <channel delay_send="0.0" eq_bass="1.0" eq_high="5.9604645E-7" eq_mid="-0.008694708" id="5" index="5" mute="0" pan="0.0" reverb_send="0.0" solo="0" stereo_width="0.99609375" volume="0.99999934"/>
    </channels>
    <delay feedback="0.7565215" stereo="1" time="5"/>
    <reverb damping="0.0" room="0.9" stereo="1"/>
</mixer>


<?xml version="1.0" encoding="UTF-8"?>
<rack>
    <machine active="1" id="LOWLEAD" index="0" type="subsynth"/>
    <machine active="1" id="HIGHLEAD" index="1" type="subsynth"/>
    <machine active="1" id="BASS" index="2" type="subsynth"/>
    <machine active="1" id="MELODY" index="3" type="bassline"/>
    <machine active="1" id="STRINGS" index="4" type="pcmsynth"/>
    <machine active="1" id="DRUMSIES" index="5" type="beatbox"/>
</rack>


<?xml version="1.0" encoding="UTF-8"?>
<effects>
    <channel id="LOWLEAD" index="0">
        <effect channel="0" depth="0.95" feedback="0.25" rate="0.43333432" type="5" wet="1.0"/>
    </channel>
    <channel id="HIGHLEAD" index="1"/>
    <channel id="BASS" index="2">
        <effect attack="0.011313666" channel="2" ratio="1.0" release="0.098769695" sidechain="5" threshold="0.19565232" type="3"/>
    </channel>
    <channel id="MELODY" index="3">
        <effect channel="3" depth="1" jitter="1.0" rate="0.31891286" type="4" wet="1.0"/>
        <effect attack="0.005227146" channel="3" ratio="0.7956521" release="0.105373904" sidechain="5" threshold="0.58260775" type="3"/>
    </channel>
    <channel id="STRINGS" index="4">
        <effect channel="4" depth="0.95" feedback="0.25" rate="0.05869563" type="5" wet="1.0"/>
        <effect attack="0.066963136" channel="4" ratio="1.0" release="0.03214792" sidechain="5" threshold="0.0" type="3"/>
    </channel>
    <channel id="DRUMSIES" index="5"/>
</effects>


*/

