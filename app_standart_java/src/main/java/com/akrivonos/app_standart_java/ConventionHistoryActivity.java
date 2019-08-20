package com.akrivonos.app_standart_java;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.akrivonos.app_standart_java.adapters.PictureAdapter;
import com.akrivonos.app_standart_java.database.DatabaseControl;
import com.akrivonos.app_standart_java.database.DatabaseControlListener;
import com.akrivonos.app_standart_java.listeners.StartActivityControlListener;
import com.akrivonos.app_standart_java.models.PhotoInfo;

import java.util.ArrayList;

import static com.akrivonos.app_standart_java.constants.Values.BUNDLE_PHOTO_INFO;
import static com.akrivonos.app_standart_java.constants.Values.CURRENT_USER_NAME;

public class ConventionHistoryActivity extends AppCompatActivity implements StartActivityControlListener {

    private DatabaseControlListener databaseControlListener;
    private PictureAdapter historyPictureAdapter;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convention_history);
        getUserName();
        databaseControlListener = new DatabaseControl(getApplicationContext());

        StartActivityControlListener startActivityControlListener = ConventionHistoryActivity.this;
        Context appContext = getApplicationContext();
        historyPictureAdapter = new PictureAdapter(startActivityControlListener, appContext);

        RecyclerView recyclerView = findViewById(R.id.history_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(historyPictureAdapter);

    }

    private void getUserName() {
        Intent intent = getIntent();
        if (intent.hasExtra(CURRENT_USER_NAME)) {
            userName = intent.getStringExtra(CURRENT_USER_NAME);
        }
    }

    private void updateRecView() {
        ArrayList<PhotoInfo> historyPhotos = databaseControlListener.getHistoryConvention(userName);
        historyPictureAdapter.setData(historyPhotos);
        historyPictureAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        updateRecView();
        super.onResume();
    }

    @Override
    public void startActivity(PhotoInfo photoInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_PHOTO_INFO, photoInfo);
        startActivity(new Intent(this, LinkContentActivity.class).putExtra(BUNDLE_PHOTO_INFO, bundle));
    }
}
