package com.akrivonos.app_standart_java.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akrivonos.app_standart_java.MainActivity;
import com.akrivonos.app_standart_java.R;
import com.akrivonos.app_standart_java.adapters.PictureAdapter;
import com.akrivonos.app_standart_java.listeners.OpenListItemLinkListener;
import com.akrivonos.app_standart_java.models.PhotoInfo;
import com.akrivonos.app_standart_java.room.RoomAppDatabase;
import com.akrivonos.app_standart_java.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.akrivonos.app_standart_java.constants.Values.VIEW_TYPE_PICTURE_CARD;

public class FavoritesFragment extends Fragment {
    public static final String FAVORITES_FRAGMENT = "favorites_fragment";
    private String userName;
    private RoomAppDatabase appDatabase;
    private PictureAdapter favoritesPictureAdapter;
    private final ItemTouchHelper.Callback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == VIEW_TYPE_PICTURE_CARD) {
                PhotoInfo photoInfo = favoritesPictureAdapter
                        .getData()
                        .get(viewHolder.getAdapterPosition());
                appDatabase.favoritePhotoDao()
                        .setPhotoNotFavorite(photoInfo.getUrlText(), photoInfo.getUserName());
                favoritesPictureAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }

    };

    public FavoritesFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        setHasOptionsMenu(true);
        userName = PreferenceUtils.getCurrentUserName(getContext());
        OpenListItemLinkListener startActivityControlListener = (OpenListItemLinkListener) getActivity();

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null){
            appDatabase = mainActivity.getDatabase();
            favoritesPictureAdapter = new PictureAdapter(startActivityControlListener, getContext(), appDatabase);
            favoritesPictureAdapter.setVisibilityDeleteButton(true);
        }

        RecyclerView favoritesRecyclerView = view.findViewById(R.id.favoriter_recycler_view);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        favoritesRecyclerView.setAdapter(favoritesPictureAdapter);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(favoritesRecyclerView);

        updateRecView();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Activity activity = getActivity();
        if(activity != null)
        activity.setTitle("Favorite");
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateRecView() {
        ArrayList<PhotoInfo> favoritePhotos = new ArrayList<>(appDatabase.favoritePhotoDao().getFavoritesForUser(userName));
        favoritesPictureAdapter.throwOffData();
        favoritesPictureAdapter.setData(sortBySections(favoritePhotos));
        favoritesPictureAdapter.notifyDataSetChanged();
    }

    private ArrayList<PhotoInfo> sortBySections(ArrayList<PhotoInfo> photos) { // сортируем фотографии по секциям и добавляем элементы для заглавия
        Map<String, ArrayList<String>> photoMap = new HashMap<>();

        for (PhotoInfo photoInfo : photos) {
            String key = photoInfo.getRequestText();
            String value = photoInfo.getUrlText();

            ArrayList<String> section;
            if (photoMap.containsKey(key)) {
                section = photoMap.get(key);
                section.add(value);
                photoMap.put(key, section);
            } else {
                section = new ArrayList<>();
                section.add(value);
                photoMap.put(key, section);
            }
        }
        return addTitleItemToArray(photoMap);
    }

    private ArrayList<PhotoInfo> addTitleItemToArray(Map<String, ArrayList<String>> photoMap) {//добавление оглавляющего элемента для каждого раздела

        String userName = PreferenceUtils.getCurrentUserName(getContext());//проверка на null делается в методе
        ArrayList<PhotoInfo> photosWithTitle = new ArrayList<>();
        PhotoInfo photoInfo;

        for (String key : photoMap.keySet()) {
            photoInfo = new PhotoInfo();
            photoInfo.setRequestText(key);
            photosWithTitle.add(photoInfo);
            for (String url : photoMap.get(key)) {
                photoInfo = new PhotoInfo();
                photoInfo.setRequestText(key);
                photoInfo.setUrlText(url);
                photoInfo.setUserName(userName);
                photosWithTitle.add(photoInfo);
            }
        }
        return photosWithTitle;
    }
}
