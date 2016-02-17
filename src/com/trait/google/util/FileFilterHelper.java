package com.trait.google.util;

import java.io.File;

/**
 * Created by yli on 2/16/2016.
 */
public class FileFilterHelper {

    private static long PICTURE_SIZE_LIMITATION = 75 * 1024 * 1024;
    private static long VIDEO_SIZE_LIMITATION = 512 * 1024 * 1024;

    private static final String[] SUPPORTED_PICTURE_EXTS = {".jpg", ".webp"};
    private static final String[] SUPPORTED_VIDEO_EXTS = {".mpg", ".mod", ".mmv", ".tod", ".wmv", ".asf", ".avi", ".divx", ".mov", ".m4v", ".3gp", ".3g2", ".mp4", ".m2t", ".m2ts", ".mts", ".mkv"};

    public static boolean isSupportedPicture(File file) {
        String fileName = file.getName().toLowerCase();
        for (String ext : SUPPORTED_PICTURE_EXTS) {
            if (fileName.endsWith(ext) && file.length() < PICTURE_SIZE_LIMITATION) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSupportedVideo(File file) {
        String fileName = file.getName().toLowerCase();
        for (String ext : SUPPORTED_VIDEO_EXTS) {
            if (fileName.endsWith(ext) && file.length() < VIDEO_SIZE_LIMITATION) {
                return true;
            }
        }
        return false;
    }

}
