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

import com.akrivonos.app_standart_java.R;
import com.akrivonos.app_standart_java.adapters.PictureAdapter;
import com.akrivonos.app_standart_java.database.DatabaseControl;
import com.akrivonos.app_standart_java.database.DatabaseControlListener;
import com.akrivonos.app_standart_java.listeners.OpenListItemLinkListener;
import com.akrivonos.app_standart_java.models.PhotoInfo;
import com.akrivonos.app_standart_java.utils.PreferenceUtils;

import java.util.ArrayList;

import static com.akrivonos.app_standart_java.constants.Values.VIEW_TYPE_PICTURE_CARD;

public class FavoritesFragment extends Fragment {
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

    public FavoritesFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        setHasOptionsMenu(true);
        databaseControlListener = new DatabaseControl(getContext());
        userName = PreferenceUtils.getCurrentUserName(getContext());
        OpenListItemLinkListener startActivityControlListener = (OpenListItemLinkListener) getActivity();

        favoritesPictureAdapter = new PictureAdapter(startActivityControlListener, getContext());
        favoritesPictureAdapter.setVisibilityDeleteButton(true);

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
        ArrayList<PhotoInfo> favoritePhotos = databaseControlListener.getAllFavoritesForUser(userName);
        favoritesPictureAdapter.throwOffData();
        favoritesPictureAdapter.setData(favoritePhotos);
        favoritesPictureAdapter.notifyDataSetChanged();
    }
}
