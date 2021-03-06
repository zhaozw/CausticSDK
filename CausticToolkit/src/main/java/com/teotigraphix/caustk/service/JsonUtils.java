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

package com.teotigraphix.caustk.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.teotigraphix.caustk.controller.ISerialize;

public class JsonUtils {

    static String toGson(Object serialized, boolean prettyPrint) {
        GsonBuilder builder = new GsonBuilder();
        if (prettyPrint) {
            builder.setPrettyPrinting();
        }
        Gson gson = builder.create();
        if (serialized instanceof ISerialize)
            ((ISerialize)serialized).sleep();
        return gson.toJson(serialized);
    }

    static <T> T fromGson(File file, Class<T> classOfT) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        T result = null;
        try {
            result = gson.fromJson(new FileReader(file), classOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (result instanceof ISerialize)
            ((ISerialize)result).wakeup();
        return result;
    }

    static <T> T fromGson(String data, Class<T> classOfT) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        T result = null;
        try {
            result = gson.fromJson(new StringReader(data), classOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
        if (result instanceof ISerialize)
            ((ISerialize)result).wakeup();
        return result;
    }

}
