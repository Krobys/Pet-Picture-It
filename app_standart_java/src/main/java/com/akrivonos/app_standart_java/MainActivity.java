package com.akrivonos.app_standart_java;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.akrivonos.app_standart_java.adapters.PictureAdapter;
import com.akrivonos.app_standart_java.executors.PicturesDownloadTask;
import com.akrivonos.app_standart_java.listeners.ControlBorderDownloaderListener;
import com.akrivonos.app_standart_java.listeners.LoaderListener;
import com.akrivonos.app_standart_java.listeners.StartActivityControlListener;
import com.akrivonos.app_standart_java.models.PhotoInfo;
import com.akrivonos.app_standart_java.utils.InternetUtils;

import java.util.ArrayList;

import static com.akrivonos.app_standart_java.AuthActivity.CURRENT_USER_NAME;

public class MainActivity extends AppCompatActivity implements LoaderListener,
        StartActivityControlListener,
        ControlBorderDownloaderListener {

    private static final String SEARCH_FIELD_TEXT = "search_field_text";
    static final String BUNDLE_PHOTO_INFO = "bundle_photo_info";
    private EditText searchRequestEditText;
    private Button searchButton;
    private String searchText;
    private ProgressBar progressBar;
    private String currentUser;
    private Toolbar toolbar;
    private PicturesDownloadTask downloadPicturesManage;
    private PictureAdapter pictureAdapter;

    private final View.OnClickListener startSearch = new View.OnClickListener() {
        @Override
        public void onClick(View v) { // Кнопка начала скачивания
            searchText = searchRequestEditText.getText().toString().toLowerCase();
            if (!TextUtils.isEmpty(searchText)) {
                if (InternetUtils.isInternetConnectionEnable(getApplicationContext())) {
                    pictureAdapter.throwOffData();
                    downloadPicturesManage.startLoadPictures(searchText, currentUser, 1);
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final ItemTouchHelper.Callback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) { // Свайп для recycleView
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            pictureAdapter.deleteItem(viewHolder.getAdapterPosition());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StartActivityControlListener startActivityControlListener = MainActivity.this;
        ControlBorderDownloaderListener controlBorderDownloaderListener = MainActivity.this;
        Context appContext = getApplicationContext();
        pictureAdapter = new PictureAdapter(startActivityControlListener, controlBorderDownloaderListener, appContext); //создаем адаптер

        RecyclerView recyclerViewPictures = findViewById(R.id.rec_view_picture);
        recyclerViewPictures.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPictures.setAdapter(pictureAdapter);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerViewPictures);

        progressBar = findViewById(R.id.progressBar);
        searchRequestEditText = findViewById(R.id.search_request);
        searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(startSearch);
        toolbar = findViewById(R.id.toolbar_actionbar);
        currentUser = getCurrentUserName();
        setSupportActionBar(toolbar);

        restoreSearchField();

        downloadPicturesManage = new PicturesDownloadTask(this);
    }

    private void saveSearchField() { //сохранение состояния поля для ввода
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String searchFieldText = searchRequestEditText.getText().toString();
        sharedPreferences.edit().putString(SEARCH_FIELD_TEXT, searchFieldText).apply();
    }

    private void restoreSearchField() { //востановление состояния поля для ввода
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.contains(SEARCH_FIELD_TEXT)) {
            String searchFieldText = sharedPreferences.getString(SEARCH_FIELD_TEXT, "");
            searchRequestEditText.setText(searchFieldText);
        }
    }

    private String getCurrentUserName() { //получение имени текущего пользователя
        String currentUserName;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        currentUserName = sharedPreferences.getString(CURRENT_USER_NAME, "");
        return currentUserName;
    }

    private void setUserNameTitle() { //устанавливаем имя пользователя в тулбар
        Intent intent = getIntent();
        if (intent != null) {
            toolbar.setTitle(currentUser);
        }
    }

    @Override
    public void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
        searchButton.setClickable(false);
    }

    @Override
    public void finishLoading(ArrayList<PhotoInfo> photos, Integer[] pageSettings) {
        progressBar.setVisibility(View.GONE);
        pictureAdapter.setData(photos);
        searchButton.setClickable(true);
        pictureAdapter.setPageSettings(pageSettings[0], pageSettings[1]);
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
        setUserNameTitle();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Class openClassActivity = null;
        switch (item.getItemId()) {
            case R.id.favorire_pick:
                openClassActivity = FavoritesUserList.class;
                break;
            case R.id.history:
                openClassActivity = ConventionHistoryActivity.class;
                break;
        }
        startActivity(new Intent(MainActivity.this, openClassActivity).putExtra(CURRENT_USER_NAME, currentUser));
        return true;
    }

    @Override
    public void startActivity(PhotoInfo photoInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_PHOTO_INFO, photoInfo);
        startActivity(new Intent(this, LinkContentActivity.class).putExtra(BUNDLE_PHOTO_INFO, bundle));
    }

    @Override
    public void loadNextPage(int pageToLoad) {
        downloadPicturesManage.startLoadPictures(searchText, currentUser, pageToLoad);
    }

    @Override
    protected void onDestroy() {
        saveSearchField();
        super.onDestroy();
    }
}
