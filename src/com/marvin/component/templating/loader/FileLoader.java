package com.marvin.component.templating.loader;

import com.marvin.component.util.PathUtils;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This loader searches for a file located anywhere on the filesystem. It uses
 * java.io.File to perform the lookup.
 *
 * @author mbosecke
 *
 */
public class FileLoader implements LoaderInterface<String> {

    private static final Logger logger = Logger.getLogger(FileLoader.class.getName());

    private String prefix;

    private String suffix;

    private String charset = "UTF-8";

    @Override
    public Reader getReader(String templateName) throws Exception {

        Reader reader = null;
        InputStream is = null;
        InputStreamReader isr;

        // add the prefix and ensure the prefix ends with a separator character
        StringBuilder path = new StringBuilder("");
        if (getPrefix() != null) {

            path.append(getPrefix());

            if (!getPrefix().endsWith(String.valueOf(File.separatorChar))) {
                path.append(File.separatorChar);
            }
        }

        templateName = templateName + (getSuffix() == null ? "" : getSuffix());

        logger.log(Level.FINE, "Looking for template in {}{}.", new Object[]{path.toString(), templateName});

        /*
         * if template name contains path segments, move those segments into the
         * path variable. The below technique needs to know the difference
         * between the path and file name.
         */
        String[] pathSegments = templateName.split("\\\\|/");

        if (pathSegments.length > 1) {
            // file name is the last segment
            templateName = pathSegments[pathSegments.length - 1];
        }
        for (int i = 0; i < (pathSegments.length - 1); i++) {
            path.append(pathSegments[i]).append(File.separatorChar);
        }

        // try to load File
        if (is == null) {
            File file = new File(path.toString(), templateName);
            if (file.exists() && file.isFile()) {
                try {
                    is = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                }
            }
        }

        if (is == null) {
            String msg = String.format("Could not find template \"%s%s\"", path, templateName);
            throw new Exception(msg);
        }

        try {
            isr = new InputStreamReader(is, charset);
            reader = new BufferedReader(isr);
        } catch (UnsupportedEncodingException e) {
        }

        return reader;
    }

    public String getSuffix() {
        return suffix;
    }

    @Override
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getCharset() {
        return charset;
    }

    @Override
    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override
    public String resolveRelativePath(String relativePath, String anchorPath) {
        return PathUtils.resolveRelativePath(relativePath, anchorPath, File.separatorChar);
    }

    @Override
    public String createCacheKey(String templateName) {
       return templateName;
    }
}
