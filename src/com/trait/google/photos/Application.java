package com.trait.google.photos;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.gdata.data.*;
import com.google.gdata.data.media.MediaFileSource;
import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.util.ContentType;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.common.xml.XmlWriter;
import com.trait.google.auth.GoogleAuthorization;
import com.trait.google.util.PictureFileFilter;
import com.trait.google.util.VideoFileFilter;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yli on 2/16/2016.
 */
public class Application {
    private static final String DOMAIN = "127.0.0.1";
    private static final int PORT = 8080;

    private static final String API_KEY = "939654241792-m124icou3hu0tevgn8bdb7e2u62kmbkb.apps.googleusercontent.com";
    private static final String API_SECRET = "nzqJbBYlmC6kFiSrLKjpbXZe";
    private static final String SCOPE = "email profile https://picasaweb.google.com/data/";
    private static final File DATA_STORE_DIR = new File(System.getProperty("user.home"), ".store/traitphotos");


    private Client client;
    private Map<String, Credential> credentials;
    private FileDataStoreFactory dataStoreFactory;

    public Application() throws IOException {
        client = new Client("TraitPhotos");
        credentials = new HashMap<String, Credential>();
        dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
    }

    public void authorize(String userName) throws IOException {
        Credential credential = credentials.get(userName);

        if (credential == null) {
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setHost(DOMAIN).setPort(PORT).build();
            GoogleAuthorization googleAuthorization = new GoogleAuthorization(API_KEY, API_SECRET, SCOPE, receiver, dataStoreFactory);
            credential = googleAuthorization.authorize(userName);
            credentials.put(userName, credential);
        }

        Long expiresIn = credential.getExpiresInSeconds();
        if(credential.getAccessToken() == null || expiresIn != null && expiresIn.longValue() <= 60L) {
            credential.refreshToken();
        }
        client.setOAuth2Credential(credential);
    }

    public List<AlbumEntry> getAlbums() throws IOException, ServiceException {
        return client.getAlbums();
    }

    public AlbumEntry addEmptyAlbum(String albumName) throws IOException, ServiceException {
        AlbumEntry album = new AlbumEntry();
        album.setTitle(new PlainTextConstruct(albumName));
        album.setAccess("private");
        album.setDate(new Date());
        album = client.insertAlbum(album);
        System.out.println("Album " + albumName + " is created.");
        return album;
    }

    public AlbumEntry addAlbum(File albumDirectory) throws IOException, ServiceException {
        AlbumEntry album = addEmptyAlbum(albumDirectory.getName());

        uploadPicture(albumDirectory, album);
        //uploadVideo(albumDirectory, album);
        showAlbum(album);
        return album;
    }

    private void uploadVideo(File albumDirectory, AlbumEntry album) {
        File[] files = albumDirectory.listFiles(new VideoFileFilter());
        for (File file : files) {
            try {
                PhotoEntry photo = new PhotoEntry();
                photo.setTitle(new PlainTextConstruct(file.getName()));
                photo.setTimestamp(new Date(file.lastModified()));

                //support video mime type : https://developers.google.com/picasa-web/docs/2.0/developers_guide_protocol#PostVideo
                MediaContent content = new MediaContent();
                content.setMediaSource(new MediaFileSource(file, "video/avi"));
                photo.setContent(content);

                photo = client.insert(album, photo);
                System.out.println(photo.getTitle().getPlainText() + " upload success.");
            } catch (Exception ex) {
                System.err.println("Failed to upload file " + file.getAbsolutePath());
                ex.printStackTrace();
            }
        }
    }

    private void uploadPicture(File albumDirectory, AlbumEntry album) {
        File[] files = albumDirectory.listFiles(new PictureFileFilter());
        for (File file : files) {
            try {
                PhotoEntry photo = new PhotoEntry();
                photo.setTitle(new PlainTextConstruct(file.getName()));
                photo.setTimestamp(new Date(file.lastModified()));

                OtherContent content = new OtherContent();
                byte[] bytes = Files.readAllBytes(file.toPath());
                content.setBytes(bytes);
                content.setMimeType(new ContentType("image/jpeg"));
                photo.setContent(content);

                System.out.println("Uploading file " + file.getAbsolutePath() + " to " + album.getTitle().getPlainText());
                photo = client.insert(album, photo);
                System.out.println(photo.getTitle().getPlainText() + " upload success.");
            } catch (Exception ex) {
                System.err.println("Failed to upload file " + file.getAbsolutePath());
                ex.printStackTrace();
            }
        }
    }

    public void showAlbum(AlbumEntry album) throws IOException, ServiceException {
        System.out.println();
        System.out.println("Album name : " + album.getTitle().getPlainText());
        List<PhotoEntry> photos = client.getPhotos(album);
        for (PhotoEntry photo : photos) {
            showPhoto(photo);
        }
    }

    public void showPhoto(PhotoEntry photo) throws ServiceException, IOException {
        StringWriter stringWriter = new StringWriter();
        photo.getExifTags().generate(new XmlWriter(stringWriter),new ExtensionProfile());

        System.out.println("\tPhoto name : " + photo.getTitle().getPlainText() +
                " Timestamp : " + photo.getTimestamp() +
                //" TimestampExt : " + photo.getTimestampExt() +
                " Exif info : " + stringWriter.toString());
    }
}
