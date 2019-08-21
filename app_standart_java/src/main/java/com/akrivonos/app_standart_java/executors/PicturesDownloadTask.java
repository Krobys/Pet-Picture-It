package com.akrivonos.app_standart_java.executors;

import android.os.AsyncTask;
import android.util.Log;

import com.akrivonos.app_standart_java.listeners.LoaderListener;
import com.akrivonos.app_standart_java.models.Photo;
import com.akrivonos.app_standart_java.models.PhotoInfo;
import com.google.android.gms.maps.model.LatLng;

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

import static com.akrivonos.app_standart_java.constants.Values.API_KEY_FLICKR;
import static com.akrivonos.app_standart_java.constants.Values.PAGE_DEF_PIC;
import static com.akrivonos.app_standart_java.constants.Values.PAGE_MAP_PIC;

public class PicturesDownloadTask extends AsyncTask<String, Void, ArrayList<PhotoInfo>>{
    private final WeakReference<LoaderListener> loaderListenerWeakReference;
    private String searchText;
    private String userName;
    private int currentPage, pagesAmount;
    private int typeLoadPageTask;

    public PicturesDownloadTask(LoaderListener loaderListener) {
        loaderListenerWeakReference = new WeakReference<>(loaderListener);
    }

    public void startLoadPictures(String searchText, String userName, int pageToLoad) {
        this.searchText = searchText;
        this.userName = userName;
        String urlDownload = buildUrlForSearch(searchText, pageToLoad);
        execute(urlDownload);
        typeLoadPageTask = PAGE_DEF_PIC;
    }

    public void startLoadPictures(LatLng coordinatesToSearch, String userName, int pageToLoad) {
        this.userName = userName;
        String urlDownload = buildUrlForSearch(coordinatesToSearch, pageToLoad);
        execute(urlDownload);
        typeLoadPageTask = PAGE_MAP_PIC;
    }

    private String buildUrlForSearch(String searchText, int pageToLoad) { // Генерация адреса для поиска с указанием страницы
        return "https://www.flickr.com/services/rest/?method=flickr.photos.search&api_key=" + API_KEY_FLICKR + "&text=" + searchText + "&page=" + pageToLoad;
    }

    private String buildUrlForSearch(LatLng latLng, int pageToLoad) {
        String urlRequest = "https://www.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=" + API_KEY_FLICKR + "&page=" + pageToLoad + "&lat=" + latLng.latitude + "&lng=" + latLng.longitude;
        return urlRequest;
    }

    private String getPhotoUrl(Photo photo) { // генерация адреса для каждой фото
        String farm = photo.getFarm();
        String server = photo.getServer();
        String id = photo.getId();
        String secret = photo.getSecret();
        return "https://farm" + farm + ".staticflickr.com/" + server + "/" + id + "_" + secret + ".jpg";
    }

    @Override
    protected void onPreExecute() {
        LoaderListener loaderListener = loaderListenerWeakReference.get();
        if (loaderListener != null)
            loaderListener.startLoading();
        else
            Log.d("WeakReferenceError", "loaderListenerWeakReference has been cleaned");
        super.onPreExecute();
    }

    @Override
    protected ArrayList<PhotoInfo> doInBackground(String... strings) {
        return loadInformation(strings[0]);
    }

    @Override
    protected void onPostExecute(ArrayList<PhotoInfo> photos) {
        LoaderListener loaderListener = loaderListenerWeakReference.get();
        if (loaderListener != null) {
            loaderListener.finishLoading(photos, new Integer[]{currentPage, pagesAmount, typeLoadPageTask});
            loaderListenerWeakReference.clear();
        } else
            Log.d("WeakReferenceError", "loaderListenerWeakReference has been cleaned");
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
                                break;
                            case "pages":
                                pagesAmount = Integer.valueOf(xpp.getAttributeValue(i));
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
            photoInfo.setRequestText((typeLoadPageTask == PAGE_DEF_PIC) ? searchText : "geo");
            photoInfo.setUserName(userName);
            photosFinal.add(photoInfo);
        }
        return photosFinal;
    }
}
