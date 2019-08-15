package com.akrivonos.app_standart_java.executors;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.akrivonos.app_standart_java.listeners.LoaderListener;
import com.akrivonos.app_standart_java.models.Photo;
import com.akrivonos.app_standart_java.models.PhotoInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class PicturesDownloadTask {
    private final WeakReference<LoaderListener> loaderListenerWeakReference;
    private String searchText;
    private String userName;
    private int currentPage, pagesAmount;
    public PicturesDownloadTask(LoaderListener loaderListener) {
        loaderListenerWeakReference = new WeakReference<>(loaderListener);
    }

    public void startLoadPictures(String searchText, String userName, int pageToLoad) {
        this.searchText = searchText;
        this.userName = userName;
        String urlDownload = buildUrlForSearchWithSearchText(searchText, pageToLoad);
        new RunLoadingPictures().execute(urlDownload);
    }

    private String buildUrlForSearchWithSearchText(String searchText, int pageToLoad) { // Генерация адреса для поиска
        String API_KEY = "c67772a7cb8e4c8be058a309f88f62cf";
        String urlToLoad = "https://www.flickr.com/services/rest/?method=flickr.photos.search&api_key=" + API_KEY + "&text=" + searchText + "&page=" + pageToLoad;
        Log.d("test", urlToLoad);
        return urlToLoad;

    }

    private String getPhotoUrl(Photo photo) { // генерация адреса для каждой фото
        String farm = photo.getFarm();
        String server = photo.getServer();
        String id = photo.getId();
        String secret = photo.getSecret();
        return "https://farm" + farm + ".staticflickr.com/" + server + "/" + id + "_" + secret + ".jpg";
    }

    @SuppressLint("StaticFieldLeak")
    private class RunLoadingPictures extends AsyncTask<String, Void, ArrayList<PhotoInfo>> {

        @Override
        protected void onPreExecute() {
            loaderListenerWeakReference.get().startLoading();
            super.onPreExecute();
        }

        @Override
        protected ArrayList<PhotoInfo> doInBackground(String... strings) {
            return loadInformation(strings[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<PhotoInfo> photos) {
            loaderListenerWeakReference.get().finishLoading(photos, new Integer[]{currentPage, pagesAmount});
            super.onPostExecute(photos);
        }

        private ArrayList<Photo> parseXml(String xml) throws XmlPullParserException, IOException { // Парсинг фотографий в список
            ArrayList<Photo> photos = new ArrayList<>();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xml));

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("photos")) {
                        for (int i = 0; i < xpp.getAttributeCount(); i++) {
                            switch (xpp.getAttributeName(i)) {
                                case "page":
                                    currentPage = Integer.valueOf(xpp.getAttributeValue(i));
                                    Log.d("test", "currentpage: " + currentPage);
                                    break;
                                case "pages":
                                    pagesAmount = Integer.valueOf(xpp.getAttributeValue(i));
                                    Log.d("test", "amountPages " + pagesAmount);
                                    break;
                            }
                        }
                    }

                    if (xpp.getName().equals("photo")) {
                        Photo photo = new Photo();
                        for (int i = 0; i < xpp.getAttributeCount(); i++) {
                            switch (xpp.getAttributeName(i)) {
                                case "id":
                                    photo.setId(xpp.getAttributeValue(i));
                                    break;
                                case "secret":
                                    photo.setSecret(xpp.getAttributeValue(i));
                                    break;
                                case "server":
                                    photo.setServer(xpp.getAttributeValue(i));
                                    break;
                                case "farm":
                                    photo.setFarm(xpp.getAttributeValue(i));
                                    break;
                            }
                        }
                        photos.add(photo);
                    }
                }
                xpp.next();
            }
            return photos;
        }

        private ArrayList<PhotoInfo> loadInformation(String urlDownload) { // Загрузка xml в список
            Log.d("test", "loadInformation: url: " + urlDownload);
            BufferedReader reader = null;
            StringBuilder buf = new StringBuilder();
            URL url;
            try {
                url = new URL(urlDownload);
                HttpsURLConnection c = (HttpsURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setReadTimeout(10000);
                c.connect();
                reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    buf.append(line).append("\n");
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            ArrayList<Photo> photos = new ArrayList<>();
            try {
                photos = parseXml(new String(buf));
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ArrayList<PhotoInfo> photosFinal = new ArrayList<>();
            PhotoInfo photoInfo;
            for (Photo photo : photos) {
                photoInfo = new PhotoInfo();
                photoInfo.setUrlText(getPhotoUrl(photo));
                photoInfo.setRequestText(searchText);
                photoInfo.setUserName(userName);
                photosFinal.add(photoInfo);
            }
            return photosFinal;
        }
    }
}
