package com.akrivonos.app_standart_java;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.akrivonos.app_standart_java.adapters.PictureAdapter;
import com.akrivonos.app_standart_java.database.DatabaseControl;
import com.akrivonos.app_standart_java.database.DatabaseControlListener;
import com.akrivonos.app_standart_java.listeners.StartActivityControlListener;
import com.akrivonos.app_standart_java.models.PhotoInfo;

import java.util.ArrayList;

import static com.akrivonos.app_standart_java.constants.Values.BUNDLE_PHOTO_INFO;
import static com.akrivonos.app_standart_java.constants.Values.CURRENT_USER_NAME;
import static com.akrivonos.app_standart_java.constants.Values.VIEW_TYPE_PICTURE_CARD;

public class FavoritesUserList extends AppCompatActivity implements StartActivityControlListener {

    private DatabaseControlListener databaseControlListener;
    private String userName;
    private PictureAdapter favoritesPictureAdapter;
    private final ItemTouchHelper.Callback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == VIEW_TYPE_PICTURE_CARD) {
                databaseControlListener.setPhotoNotFavorite(favoritesPictureAdapter
                        .getData()
                        .get(viewHolder.getAdapterPosition()));
                favoritesPictureAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }

    };

    private void getUserName() {
        Intent intent = getIntent();
        if (intent.hasExtra(CURRENT_USER_NAME)) {
            userName = intent.getStringExtra(CURRENT_USER_NAME);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_user_list);
        getUserName();

        databaseControlListener = new DatabaseControl(getApplicationContext());

        StartActivityControlListener startActivityControlListener = FavoritesUserList.this;
        Context appContext = getApplicationContext();
        favoritesPictureAdapter = new PictureAdapter(startActivityControlListener, appContext);
        favoritesPictureAdapter.setVisibilityDeleteButton(true);

        RecyclerView favoritesRecyclerView = findViewById(R.id.favoriter_recycler_view);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoritesRecyclerView.setAdapter(favoritesPictureAdapter);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(favoritesRecyclerView);

    }

    private void updateRecView() {
        ArrayList<PhotoInfo> favoritePhotos = databaseControlListener.getAllFavoritesForUser(userName);
        favoritesPictureAdapter.throwOffData();
        favoritesPictureAdapter.setData(favoritePhotos);
        favoritesPictureAdapter.notifyDataSetChanged();
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
