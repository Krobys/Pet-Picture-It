package com.akrivonos.app_standart_java.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.akrivonos.app_standart_java.MainActivity;
import com.akrivonos.app_standart_java.models.Photo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import static com.akrivonos.app_standart_java.MainActivity.RESULT_TEXT;
import static com.akrivonos.app_standart_java.MainActivity.SEARCH_TEXT;
import static com.akrivonos.app_standart_java.MainActivity.STATUS_START;
import static com.akrivonos.app_standart_java.MainActivity.STATUS_STOP;

public class PicturesDownloadService extends Service {
    public static final String STATUS = "STATUS";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String urlDownload = buildUrlForSearchWithSearchText(intent.getStringExtra(SEARCH_TEXT));
        new RunLoadingPictures().execute(urlDownload);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    String buildUrlForSearchWithSearchText(String searchText) { // Генерация адреса для поиска
        String API_KEY = "c67772a7cb8e4c8be058a309f88f62cf";
        return "https://www.flickr.com/services/rest/?method=flickr.photos.search&api_key=" + API_KEY + "&text=" + searchText;
    }

    @SuppressLint("StaticFieldLeak")
    private class RunLoadingPictures extends AsyncTask<String, Void, ArrayList<Photo>> {

        @Override
        protected void onPreExecute() {
            sendBroadcast(new Intent(MainActivity.BROADCAST_ACTION)
                    .putExtra(STATUS, STATUS_START));
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Photo> doInBackground(String... strings) {
            return loadInformation(strings[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Photo> photos) {
            sendBroadcast(new Intent(MainActivity.BROADCAST_ACTION)
                    .putExtra(STATUS, STATUS_STOP)
                    .putExtra(RESULT_TEXT, photos));
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

        private ArrayList<Photo> loadInformation(String urlDownload) { // Загрузка xml в String
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
            return photos;
        }
    }
}
