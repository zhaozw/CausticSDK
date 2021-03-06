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
import java.util.Collection;
import java.util.UUID;

import com.teotigraphix.caustic.core.CausticException;
import com.teotigraphix.caustk.controller.ICaustkController;
import com.teotigraphix.caustk.library.vo.MetadataInfo;

public interface ILibraryManager {

    /**
     * Returns the appRoot/libraries directory.
     * <p>
     * If this directory does not exist, the directory will be created when the
     * {@link LibraryManager} has been first instantiated.
     */
    File getLibrariesDirectory();

    Library getSelectedLibrary();

    /**
     * @param value
     * @see OnLibraryManagerSelectedLibraryChange
     */
    void setSelectedLibrary(Library value);

    int getLibraryCount();

    Collection<Library> getLibraries();

    /**
     * Creates an initial {@link Library} from a <code>.caustic</code> file.
     * 
     * @param name The library name but will also be the directory that is
     *            created in the <code>libraries</code> directory.
     * @throws CausticException
     * @throws IOException
     */
    Library createLibrary(String name) throws IOException;

    LibraryScene createLibraryScene(MetadataInfo info);

    Library getLibraryByName(String name);

    Library getLibraryById(UUID id);

    /**
     * Imports a <code>.caustic</code> song file into the {@link Library}.
     * 
     * @param library The {@link Library} to import the caustic song data.
     * @param causticFile The <code>.caustic</code> file to initially load this
     *            libraries first data.
     * @throws IOException
     */
    void importSong(Library library, File causticFile) throws IOException;

    /**
     * Creates and loads a {@link Library} from the <code>libraries</code>
     * directory.
     * 
     * @param name The library name to load, this will be a directory name.
     * @throws IOException
     */
    Library loadLibrary(String name) throws IOException;

    /**
     * Saves the {@link Library} to disk using the <code>ctk</code> library
     * format.
     * 
     * @param library The {@link Library} to save.
     * @throws IOException
     */
    void saveLibrary(Library library) throws IOException;

    /**
     * Loads a {@link Library}s found in the <code>libraries</code> directory.
     * 
     * @throws IOException
     */
    void load() throws IOException;

    /**
     * Saves a {@link Library}s found in the {@link LibraryRegistry}.
     * 
     * @throws IOException
     */
    void save() throws IOException;

    /**
     * Clears the {@link LibraryRegistry}.
     */
    void clear();

    /**
     * Mainly for testing, deletes all library directories.
     * 
     * @throws IOException
     */
    void delete() throws IOException;

    /**
     * @see ICaustkController#getDispatcher()
     */
    public static class LibraryEvent {

        private Library library;

        public Library getLibrary() {
            return library;
        }

        public LibraryEvent(Library library) {
            this.library = library;
        }
    }

    /**
     * Dispatched when an individual {@link Library} has been loaded into the
     * manager.
     * 
     * @see ICaustkController#getDispatcher()
     */
    public static class OnLibraryManagerLoadComplete extends LibraryEvent {
        public OnLibraryManagerLoadComplete(Library library) {
            super(library);
        }
    }

    /**
     * @see ILibraryManager#setSelectedLibrary(Library)
     * @see ILibraryManager#getSelectedLibrary()
     */
    public static class OnLibraryManagerSelectedLibraryChange extends LibraryEvent {
        public OnLibraryManagerSelectedLibraryChange(Library library) {
            super(library);
        }
    }

}
