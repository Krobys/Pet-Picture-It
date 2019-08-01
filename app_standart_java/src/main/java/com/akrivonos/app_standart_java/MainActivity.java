package com.akrivonos.app_standart_java;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akrivonos.app_standart_java.models.Photo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String SEARCH_TEXT = "search_text";
    public static final String RESULT_TEXT = "result_text";
    public static final String BROADCAST_ACTION = "com.akrivonos.broadcastrec.Broadcast";
    public static final int STATUS_START = 1;
    public static final int STATUS_STOP = 0;
    protected static final String SPAN_URL = "span_url";
    protected static final String STATUS = "STATUS";
    protected static final String SERVICE_FILTER = "com.akrivonos.app_standart_java.SERVICE";
    private int status;
    private TextView searchResultTextView;
    private EditText searchRequestEditText;
    private Button searchButton;
    private ProgressBar progressBar;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            status = intent.getIntExtra(STATUS, 0);
            switch (status) {
                case STATUS_START:
                    progressBar.setVisibility(View.VISIBLE);
                    searchResultTextView.setVisibility(View.GONE);
                    searchButton.setClickable(false);
                    break;
                case STATUS_STOP:
                    progressBar.setVisibility(View.GONE);
                    searchResultTextView.setVisibility(View.VISIBLE);
                    searchResultTextView.setText("");
                    startSettingLoadedInfo(intent.getStringExtra(RESULT_TEXT));
                    searchButton.setClickable(true);
                    stopService(new Intent(SERVICE_FILTER).setPackage(getPackageName()));
                    break;
            }
        }
    };

    private View.OnClickListener startSearch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String searchText = searchRequestEditText.getText().toString();
            if (!TextUtils.isEmpty(searchText)) {
                startService(new Intent(SERVICE_FILTER).setPackage(getPackageName()).putExtra(SEARCH_TEXT, searchText));
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        searchRequestEditText = findViewById(R.id.search_request);
        searchButton = findViewById(R.id.search_button);
        searchResultTextView = findViewById(R.id.search_result);
        searchButton.setOnClickListener(startSearch);

        searchResultTextView.setMovementMethod(LinkMovementMethod.getInstance());

        registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_ACTION));
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(SERVICE_FILTER).setPackage(getPackageName()));
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private void startSettingLoadedInfo(String info) {
        List<Photo> photos;
        try {
            photos = parseXml(info);
            setSpanTextInView(photos);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Photo> parseXml(String xml) throws XmlPullParserException, IOException { // Парсинг фотографий в список
        List<Photo> photos = new ArrayList<>();
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

    private void setSpanTextInView(List<Photo> photos) { //добавление активной ссылки для каждой фото
        for (Photo photo : photos) {
            final String photoUrl = getPhotoUrl(photo);

            final SpannableString string = new SpannableString(photoUrl);
            string.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    startActivity(new Intent(MainActivity.this, LinkContentActivity.class).putExtra(SPAN_URL, photoUrl));
                }
            }, 0, photoUrl.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            searchResultTextView.append(string);
            searchResultTextView.append("\n");
        }
    }

    private String getPhotoUrl(Photo photo) { // генерация адреса для каждой фото
        String farm = photo.getFarm();
        String server = photo.getServer();
        String id = photo.getId();
        String secret = photo.getSecret();
        return "https://farm" + farm + ".staticflickr.com/" + server + "/" + id + "_" + secret + ".jpg";
    }
}
