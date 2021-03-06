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

package com.teotigraphix.caustk.project;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.androidtransfuse.event.EventObserver;
import org.apache.commons.io.FileUtils;

import com.teotigraphix.caustk.controller.ICaustkController;
import com.teotigraphix.caustk.controller.ICaustkController.OnControllerSave;

/**
 * The project manager manages the single project loaded for an application.
 * <p>
 * The manager will have a root directory passed to it when it is created. All
 * project related files are stored within this directory.
 */
public class ProjectManager implements IProjectManager {

    private ICaustkController controller;

    private File projectDirectory;

    private File sessionPreferencesFile;

    //----------------------------------
    // sessionPreferences
    //----------------------------------

    private SessionPreferences sessionPreferences;

    @Override
    public SessionPreferences getSessionPreferences() {
        return sessionPreferences;
    }

    //----------------------------------
    // applicationRoot
    //----------------------------------

    /**
     * The root application directory, all {@link Project}s are stored in the
     * <code>applicationRoot/projects</code> directory.
     */
    //private File applicationRoot;

    @Override
    public File getApplicationRoot() {
        return controller.getConfiguration().getApplicationRoot();
    }

    @Override
    public File getDirectory(String path) {
        File directory = new File(getApplicationRoot(), path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    //----------------------------------
    // project
    //----------------------------------

    private Project project;

    @Override
    public Project getProject() {
        return project;
    }

    //--------------------------------------------------------------------------
    // Constructor
    //--------------------------------------------------------------------------

    public ProjectManager(ICaustkController controller, File applicationRoot) {
        this.controller = controller;

        initialize(controller.getConfiguration().getApplicationRoot());

        controller.getDispatcher().register(OnControllerSave.class,
                new EventObserver<OnControllerSave>() {
                    @Override
                    public void trigger(OnControllerSave object) {
                        try {
                            save();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        controller.getDispatcher().register(OnProjectManagerChange.class,
                new EventObserver<OnProjectManagerChange>() {

                    @Override
                    public void trigger(OnProjectManagerChange object) {
                        if (object.getKind() == ProjectManagerChangeKind.SAVE_COMPLETE) {
                            try {
                                flushProjectFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    //-------------------------------------------------------------------------
    // IProjectManager API
    //--------------------------------------------------------------------------

    @Override
    public void initialize(File applicationRoot) {
        projectDirectory = new File(applicationRoot, "projects");
        sessionPreferencesFile = new File(applicationRoot, ".settings");

        if (!sessionPreferencesFile.exists()) {
            try {
                sessionPreferencesFile.createNewFile();
                sessionPreferences = new SessionPreferences();
                saveProjectPreferences();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (sessionPreferencesFile.exists()) {
                sessionPreferences = controller.getSerializeService().fromFile(
                        sessionPreferencesFile, SessionPreferences.class);
            }
        }
    }

    @Override
    public boolean isProject(File file) {
        if (file.isAbsolute())
            return file.exists();
        return toProjectFile(file).exists();
    }

    @Override
    public void exit() throws IOException {
        save();
        Project oldProject = project;
        project.close();
        project = null;
        controller.getDispatcher().trigger(
                new OnProjectManagerChange(oldProject, ProjectManagerChangeKind.EXIT));
    }

    @Override
    public void save() throws IOException {

        sessionPreferences.put("lastProject", project.getFile().getPath());
        // set modified
        project.getInfo().setModified(new Date());

        controller.getDispatcher().trigger(
                new OnProjectManagerChange(project, ProjectManagerChangeKind.SAVE));

        // all finalize actions like saving the full data to disk happen in a separate sequence
        // NO clients should be changing the Project state in this event
        controller.getDispatcher().trigger(
                new OnProjectManagerChange(project, ProjectManagerChangeKind.SAVE_COMPLETE));
    }

    protected void flushProjectFile() throws IOException {
        String data = controller.getSerializeService().toString(project);
        FileUtils.writeStringToFile(project.getFile(), data);

        saveProjectPreferences();
    }

    private void saveProjectPreferences() throws IOException {
        String data = controller.getSerializeService().toString(sessionPreferences);
        FileUtils.writeStringToFile(sessionPreferencesFile, data);
    }

    @Override
    public Project load(File file) throws IOException {
        file = toProjectFile(file);
        if (!file.exists())
            throw new IOException("Project file does not exist");

        project = controller.getSerializeService().fromFile(file, Project.class);
        project.open();
        controller.getDispatcher().trigger(
                new OnProjectManagerChange(project, ProjectManagerChangeKind.LOAD));
        return project;
    }

    @Override
    public Project create(File projectFile) throws IOException {
        project = new Project();
        project.setFile(new File(projectDirectory, projectFile.getPath()));
        project.setInfo(createInfo());
        project.open();
        controller.getDispatcher().trigger(
                new OnProjectManagerChange(project, ProjectManagerChangeKind.CREATE));
        return project;
    }

    //--------------------------------------------------------------------------
    // 
    //--------------------------------------------------------------------------

    private ProjectInfo createInfo() {
        ProjectInfo info = new ProjectInfo();
        info.setName("Untitled Project");
        info.setAuthor("Untitled Author");
        info.setCreated(new Date());
        info.setModified(new Date());
        info.setDescription("A new project");
        return info;
    }

    private File toProjectFile(File file) {
        if (file.isAbsolute())
            return file;
        return new File(projectDirectory, file.getPath());
    }

}
