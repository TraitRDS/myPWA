package com.ricky.traitphotos.app;

import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.util.ServiceException;
import com.ricky.google.photos.Application;
import com.ricky.traitphotos.util.SimpleCommandLineParser;

import java.io.File;
import java.io.IOException;
import java.io.SyncFailedException;
import java.util.List;

/**
 * Created by yli on 2/16/2016.
 */
public class CommandLineApp {

    public static void main(String[] args) {

        SimpleCommandLineParser parser = new SimpleCommandLineParser(args);

        String userName = parser.getValue("username", "u");
        String action = parser.getValue("action", "a");

        boolean help = parser.containsKey("help", "h");

        if (help || action == null) {
            usage();
            System.exit(1);
        }

        try {
            Application application = new Application();
            application.authorize(userName);
            if (action.toLowerCase().equals("upload")) {
                String albumPath = parser.getValue("directory", "d");
                if (albumPath == null) {
                    error("Didn't specify the album path.");
                    error();
                    usage();
                    System.exit(1);
                }
                File albumFolder = new File(albumPath);
                if (!albumFolder.isDirectory()) {
                    error("Album directory " + albumPath + " is not a directory.");
                    System.exit(1);
                }
                application.addAlbum(albumFolder);
            } else if (action.toLowerCase().equals("delete")) {
                String albumName = parser.getValue("album", "l");
                if (albumName == null) {
                    error("Did not specify the album name.");
                    error();
                    usage();
                    System.exit(1);
                }

                List<AlbumEntry> albums = application.getAlbums();
                for (AlbumEntry album : albums) {
                    if (album.getTitle().getPlainText().equals(albumName)) {
                        album.delete();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    private static void createAlbumFromLocalFolder(SimpleCommandLineParser parser) {

    }

    private static void usage() {
        error("Usage: TraitPhotos --username <username> --exec <command>");
        error();

        error("Manage the google photos.");
        error();

        error("User information:");
        error("\t-u, -username\t\t\tuser name");
        error();

        error("Commands:");
        error("\t-a, -action=ACTION\t\tACTION is 'upload'");
        error("\t-d, -directory=DIR\t\tDIR is full path of upload directory");
        error();

    }

    private static void error() {
        System.err.println();
    }

    private static void error(String message) {
        System.err.println(message);
    }
}
