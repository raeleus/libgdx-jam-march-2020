/*
 * The MIT License
 *
 * Copyright 2018 Raymond Buckley.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ray3k.template;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonBinary;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.ray3k.template.AnimationStateDataLoader.AnimationStateDataParameter;

/**
 * {@link AssetLoader} for {@link SkeletonData} instances. Loads an exported
 * Spine's skeleton data. The atlas with the images will be loaded as a
 * dependency. This has to be declared as a {@link SkeletonDataLoaderParameter}
 * in the {@link AssetManager#load(String, Class, AssetLoaderParameters)} call.
 * Supports both binary and JSON skeleton format files. If the animation file
 * name has a 'skel' extension, it will be loaded as binary. Any other extension
 * will be assumed as JSON.
 *
 * Example: suppose you have 'data/spine/character.atlas',
 * 'data/spine/character.png' and 'data/spine/character.skel'. To load it with
 * an asset manager, just do the following:
 * * <pre>
 * {@code
 * assetManager.setLoader(SkeletonData.class, new SkeletonDataLoader(new InternalFileHandleResolver()));
 * AnimationStateDataParameter parameter = new AnimationStateDataParameter("data/spine/character.atlas");
 * assetManager.load("data/spine/character.skel", SkeletonData.class, parameter);
 * }
 * </pre>
 *
 * @author Alvaro Barbeira
 */
public class SkeletonDataLoader extends AsynchronousAssetLoader<SkeletonData, SkeletonDataLoader.SkeletonDataLoaderParameter> {

    SkeletonData skeletonData;

    public SkeletonDataLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, SkeletonDataLoaderParameter parameter) {
    
    }

    @Override
    public SkeletonData loadSync(AssetManager manager, String fileName, FileHandle file, SkeletonDataLoaderParameter parameter) {
        skeletonData = null;
        TextureAtlas atlas = manager.get(parameter.atlasName, TextureAtlas.class);
    
        String extension = file.extension();
        if (extension.toLowerCase().equals("skel")) {
            SkeletonBinary skeletonBinary = new SkeletonBinary(atlas);
            skeletonBinary.setScale(parameter.scale);
            skeletonData = skeletonBinary.readSkeletonData(file);
        } else {
            SkeletonJson skeletonJson = new SkeletonJson(atlas);
            skeletonJson.setScale(parameter.scale);
            skeletonData = skeletonJson.readSkeletonData(file);
        }
        return skeletonData;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, SkeletonDataLoaderParameter parameter) {
        Array<AssetDescriptor> deps = new Array<AssetDescriptor>();
        deps.add(new AssetDescriptor(parameter.atlasName, TextureAtlas.class));
        return deps;
    }

    static public class SkeletonDataLoaderParameter extends AssetLoaderParameters<SkeletonData> {
        public String atlasName;
        public float scale;

        public SkeletonDataLoaderParameter(String atlasName, float scale) {
            this.atlasName = atlasName;
            this.scale = scale;
        }

        public SkeletonDataLoaderParameter(String atlasName) {
            this(atlasName, 1);
        }
    }
}
