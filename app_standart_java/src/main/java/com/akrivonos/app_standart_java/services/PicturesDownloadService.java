package com.akrivonos.app_standart_java.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.akrivonos.app_standart_java.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

import static com.akrivonos.app_standart_java.MainActivity.RESULT_TEXT;
import static com.akrivonos.app_standart_java.MainActivity.SEARCH_TEXT;
import static com.akrivonos.app_standart_java.MainActivity.STATUS_START;
import static com.akrivonos.app_standart_java.MainActivity.STATUS_STOP;

public class PicturesDownloadService extends Service {
    public static final String STATUS = "STATUS";
    private ExecutorService executorService;
    private String urlDownload;

    public PicturesDownloadService() {
        executorService = Executors.newFixedThreadPool(1);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        urlDownload = buildUrlForSearchWithSearchText(intent.getStringExtra(SEARCH_TEXT));
        executorService.execute(new RunTask(startId));
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

    class RunTask implements Runnable {
        int startId;

        RunTask(int startId) {
            this.startId = startId;
        }

        @Override
        public void run() {
            sendBroadcast(new Intent(MainActivity.BROADCAST_ACTION)
                    .putExtra(STATUS, STATUS_START));

            String resultInformation = loadInformation();
            sendBroadcast(new Intent(MainActivity.BROADCAST_ACTION)
                    .putExtra(STATUS, STATUS_STOP)
                    .putExtra(RESULT_TEXT, resultInformation));

            stopSelf(startId);
        }

        private String loadInformation() { // Загрузка xml в String
            BufferedReader reader = null;
            StringBuilder buf = new StringBuilder();
            URL url;
            try {
                url = new URL(urlDownload);
                Log.d("test", "loadInformation: " + url.toString());
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
            return new String(buf);
        }
    }
}
