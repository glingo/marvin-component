/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.marvin.component.templating.loader;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This loader will delegate control to a list of children loaders. This is the
 * default implementation used by Pebble; it delegates to a classpath loader and
 * a file loader to increase the chances of finding templates with varying
 * setups.
 *
 * @author mbosecke
 *
 */
public class DelegatingLoader implements Loader<DelegatingLoaderCacheKey> {

    private String prefix;

    private String suffix;

    private String charset = "UTF-8";

    /**
     * Children loaders to delegate to. The loaders are used in order and as
     * soon as one of them finds a template, the others will not be given a
     * chance to do so.
     */
    private final List<Loader<?>> loaders;

    /**
     * Constructor provided with a list of children loaders.
     *
     * @param loaders
     *            A list of loaders to delegate to
     */
    public DelegatingLoader(List<Loader<?>> loaders) {
        this.loaders = Collections.unmodifiableList(new ArrayList<Loader<?>>(loaders));
    }


    @Override
    public Reader getReader(DelegatingLoaderCacheKey cacheKey) throws Exception {

        Reader reader = null;

        final int size = this.loaders.size();
        for (int i = 0; i < size; i++) {
            Loader<?> loader = this.loaders.get(i);
            Object delegatingKey = cacheKey.getDelegatingCacheKeys().get(i);
            try {
                reader = this.getReaderInner(loader, delegatingKey);
                if (reader != null) {
                    break;
                }
            } catch (Exception e) {
                // do nothing
            }
        }
        if (reader == null) {
            String msg = String.format("Could not find template \"%s\"", cacheKey.getTemplateName());
            throw new Exception(msg);
        }

        return reader;
    }

    private <T> Reader getReaderInner(Loader<T> delegatingLoader, Object cacheKey) throws Exception {

        // This unchecked cast is ok, because we ensure that the type of the
        // cache key corresponds to the loader when we create the key.
        @SuppressWarnings("unchecked")
        T castedKey = (T) cacheKey;

        return delegatingLoader.getReader(castedKey);
    }

    public String getSuffix() {
        return suffix;
    }

    @Override
    public void setSuffix(String suffix) {
        this.suffix = suffix;
        loaders.stream().forEach((loader) -> {
            loader.setSuffix(suffix);
        });
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
        for (Loader<?> loader : loaders) {
            loader.setPrefix(prefix);
        }
    }

    public String getCharset() {
        return charset;
    }

    @Override
    public void setCharset(String charset) {
        this.charset = charset;
        loaders.stream().forEach((loader) -> {
            loader.setCharset(charset);
        });
    }

    @Override
    public String resolveRelativePath(String relativePath, String anchorPath) {
        if (relativePath == null) {
            return relativePath;
        }
        for (Loader<?> loader : this.loaders) {
            String path = loader.resolveRelativePath(relativePath, anchorPath);
            if (path != null) {
                return path;
            }
        }
        return null;
    }

    @Override
    public DelegatingLoaderCacheKey createCacheKey(String templateName) {

        List<Object> keys = new ArrayList<>();
        this.loaders.stream().forEach((loader) -> {
            keys.add(loader.createCacheKey(templateName));
        });

        return new DelegatingLoaderCacheKey(keys, templateName);
    }
}