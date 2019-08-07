package com.akrivonos.app_standart_java;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akrivonos.app_standart_java.listeners.LoaderListener;
import com.akrivonos.app_standart_java.models.Photo;
import com.akrivonos.app_standart_java.services.PicturesDownloadService;

import java.util.ArrayList;

import static com.akrivonos.app_standart_java.AuthActivity.USER_NAME;

public class MainActivity extends AppCompatActivity implements LoaderListener {

    protected static final String SPAN_URL = "span_url";
    private static final String SEARCH_FIELD_TEXT = "search_field_text";
    protected static final String SEARCH_TEXT = "search_text";
    private TextView searchResultTextView;
    private EditText searchRequestEditText;
    private Button searchButton;
    private ProgressBar progressBar;
    public static String currentUser;
    private Toolbar toolbar;
    private String searchText;

    private PicturesDownloadService downloadPicturesManage;

    private View.OnClickListener startSearch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            searchText = searchRequestEditText.getText().toString().toLowerCase();
            if (!TextUtils.isEmpty(searchText)) {
                downloadPicturesManage.startLoadPictures(searchText);
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        searchRequestEditText = findViewById(R.id.search_request);
        searchButton = findViewById(R.id.search_button);
        searchResultTextView = findViewById(R.id.search_result);
        searchButton.setOnClickListener(startSearch);
        toolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);


        getUserName();
        restoreSearchField();
        searchResultTextView.setMovementMethod(LinkMovementMethod.getInstance());

        downloadPicturesManage = new PicturesDownloadService(this);
    }

    @Override
    protected void onDestroy() {
        saveSearchField();
        super.onDestroy();
    }

    private void setSpanTextInView(ArrayList<Photo> photos) { //добавление активной ссылки для каждой фото
        for (Photo photo : photos) {
            final String photoUrl = getPhotoUrl(photo);

            final SpannableString string = new SpannableString(photoUrl);
            string.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    startActivity(new Intent(MainActivity.this, LinkContentActivity.class)
                            .putExtra(SPAN_URL, photoUrl)
                            .putExtra(SEARCH_TEXT, searchText)
                            .putExtra(USER_NAME, currentUser));
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

    void saveSearchField() { //сохранение состояния поля для ввода
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String searchFieldText = searchRequestEditText.getText().toString();
        if (!TextUtils.isEmpty(searchFieldText)) {
            sharedPreferences.edit().putString(SEARCH_FIELD_TEXT, searchFieldText).apply();
        } else {
            sharedPreferences.edit().putString(SEARCH_FIELD_TEXT, null).apply();
        }
    }

    private void restoreSearchField() { //востановление состояния поля для ввода
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.contains(SEARCH_FIELD_TEXT)) {
            String searchFieldText = sharedPreferences.getString(SEARCH_FIELD_TEXT, "");
            searchRequestEditText.setText(searchFieldText);
        }
    }

    @Override
    public void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
        searchResultTextView.setVisibility(View.GONE);
        searchButton.setClickable(false);
    }

    @Override
    public void finishLoading(ArrayList<Photo> photos) {
        progressBar.setVisibility(View.GONE);
        searchResultTextView.setVisibility(View.VISIBLE);
        searchResultTextView.setText("");
        setSpanTextInView(photos);
        searchButton.setClickable(true);
    }

    private void getUserName() {
        Intent intent = getIntent();
        if (intent != null) {
            currentUser = intent.getStringExtra(USER_NAME);
            toolbar.setTitle(currentUser);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_info_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(1).setVisible(true).setIcon(R.drawable.ic_turned_in_black);
        menu.getItem(0).setVisible(true);
        getUserName();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorire_pick:
                startActivity(new Intent(MainActivity.this, FavoritesUserList.class).putExtra(USER_NAME, currentUser));
                return true;
            case R.id.history:
                startActivity(new Intent(MainActivity.this, ConventionHistoryActivity.class).putExtra(USER_NAME, currentUser));
                return true;
        }
        return false;
    }
}
