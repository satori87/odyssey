/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.bg.ody.client.core;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.bg.ody.shared.MapData;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

/**
 * {@link AssetLoader} for {@link Pixmap} instances. The Pixmap is loaded
 * asynchronously.
 * 
 * @author mzechner
 */
public class MapLoader extends AsynchronousAssetLoader<MapData, MapLoader.MapParameter> {

	public MapLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	MapData map;

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, MapParameter parameter) {
		map = null;
		// if (file.exists()) {
		InputStream inputStream;
		try {
			inputStream = new InflaterInputStream(new FileInputStream(file.file()));
			Input input = new Input(inputStream);
			Kryo kryo = new Kryo();
			map = kryo.readObject(input, MapData.class);
		} catch (Exception e) {
			// e.printStackTrace();
			map = new MapData();
		}
	}

	@Override
	public MapData loadSync(AssetManager manager, String fileName, FileHandle file, MapParameter parameter) {
		MapData map = this.map;
		this.map = null;
		return map;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, MapParameter parameter) {
		return null;
	}

	static public class MapParameter extends AssetLoaderParameters<MapData> {
	}
}
