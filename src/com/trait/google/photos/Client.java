package com.trait.google.photos;

import com.google.api.client.auth.oauth2.Credential;
import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.Link;
import com.google.gdata.data.photos.*;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yli on 2/15/2016.
 */
public class Client {
    private static final String API_PREFIX = "https://picasaweb.google.com/data/feed/api/user/";

    private PicasawebService service;


    public Client(String appName) {
        service = new PicasawebService(appName);
    }

    public void setOAuth2Credential(Credential credential) {
        service.setOAuth2Credentials(credential);
    }


    /**
     * Retrieves the albums for the given user.
     */
    public List<AlbumEntry> getAlbums(String username) throws IOException,
            ServiceException {

        String albumUrl = API_PREFIX + username;
        UserFeed userFeed = getFeed(albumUrl, UserFeed.class);

        List<GphotoEntry> entries = userFeed.getEntries();
        List<AlbumEntry> albums = new ArrayList<AlbumEntry>();
        for (GphotoEntry entry : entries) {
            GphotoEntry adapted = entry.getAdaptedEntry();
            if (adapted instanceof AlbumEntry) {
                albums.add((AlbumEntry) adapted);
            }
        }
        return albums;
    }

    /**
     * Retrieves the albums for the currently logged-in user.  This is equivalent
     * to calling {@link #getAlbums(String)} with "default" as the username.
     */
    public List<AlbumEntry> getAlbums() throws IOException, ServiceException {
        return getAlbums("default");
    }

    /**
     * Album-specific insert method to insert into the gallery of the current
     * user, this bypasses the need to have a top-level entry object for parent.
     */
    public AlbumEntry insertAlbum(AlbumEntry album) throws IOException, ServiceException {
        String feedUrl = API_PREFIX + "default";
        return service.insert(new URL(feedUrl), album);
    }

    /**
     * Retrieves the photos for the given album.
     */
    public List<PhotoEntry> getPhotos(AlbumEntry album) throws IOException,
            ServiceException {

        String feedHref = getLinkByRel(album.getLinks(), Link.Rel.FEED);
        AlbumFeed albumFeed = getFeed(feedHref, AlbumFeed.class);

        List<GphotoEntry> entries = albumFeed.getEntries();
        List<PhotoEntry> photos = new ArrayList<PhotoEntry>();
        for (GphotoEntry entry : entries) {
            GphotoEntry adapted = entry.getAdaptedEntry();
            if (adapted instanceof PhotoEntry) {
                photos.add((PhotoEntry) adapted);
            }
        }
        return photos;
    }


    /**
     * Insert an entry into another entry.  Because our entries are a hierarchy,
     * this lets you insert a photo into an album even if you only have the
     * album entry and not the album feed, making it quicker to traverse the
     * hierarchy.
     */
    public <T extends GphotoEntry> T insert(GphotoEntry<?> parent, T entry)
            throws IOException, ServiceException {

        String feedUrl = getLinkByRel(parent.getLinks(), Link.Rel.FEED);
        return service.insert(new URL(feedUrl), entry);
    }

    /**
     * Helper function to allow retrieval of a feed by string url, which will
     * create the URL object for you.  Most of the Link objects have a string
     * href which must be converted into a URL by hand, this does the conversion.
     */
    public <T extends GphotoFeed> T getFeed(String feedHref,
                                            Class<T> feedClass) throws IOException, ServiceException {
        System.out.println("Get Feed URL: " + feedHref);
        return service.getFeed(new URL(feedHref), feedClass);
    }

    /**
     * Helper function to get a link by a rel value.
     */
    public String getLinkByRel(List<Link> links, String relValue) {
        for (Link link : links) {
            if (relValue.equals(link.getRel())) {
                return link.getHref();
            }
        }
        throw new IllegalArgumentException("Missing " + relValue + " link.");
    }
}
