package com.akrivonos.app_standart_java.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "rsp", strict = false)
public class Rsp {
    @Element(name = "photos")
    private Photos photos;

    public Photos getPhotos() {
        return photos;
    }

    @Root(name = "photos", strict = false)
    public static class Photos {
        @Attribute(name = "page")
        private int page;
        @Attribute(name = "pages")
        private int pages;
        @ElementList(entry = "photo", inline = true)
        private List<Photo> photo;

        public int getPage() {
            return page;
        }

        public int getPages() {
            return pages;
        }

        public List<Photo> getPhoto() {
            return photo;
        }
    }

    @Root(name = "photo", strict = false)
    public static class Photo {
        @Attribute(name = "id")
        private String id;
        @Attribute(name = "secret")
        private String secret;
        @Attribute(name = "server")
        private String server;
        @Attribute(name = "farm")
        private String farm;

        public String getId() {
            return id;
        }

        public String getSecret() {
            return secret;
        }

        public String getServer() {
            return server;
        }

        public String getFarm() {
            return farm;
        }
    }
}

