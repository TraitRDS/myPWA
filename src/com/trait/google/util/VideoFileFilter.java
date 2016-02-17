package com.trait.google.util;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by yli on 2/16/2016.
 */
public class VideoFileFilter implements FileFilter {
    @Override
    public boolean accept(File file) {
        if (FileFilterHelper.isSupportedVideo(file)) {
            return true;
        } else {
            if (!FileFilterHelper.isSupportedPicture(file)) {
                System.err.println("File " + file.getAbsolutePath() + " is filtered out.");
            }
            return false;
        }
    }
}
