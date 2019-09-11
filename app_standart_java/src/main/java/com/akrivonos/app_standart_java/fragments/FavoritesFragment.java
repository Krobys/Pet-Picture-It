package com.akrivonos.app_standart_java.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akrivonos.app_standart_java.R;
import com.akrivonos.app_standart_java.adapters.PictureAdapter;
import com.akrivonos.app_standart_java.database.DatabaseControl;
import com.akrivonos.app_standart_java.database.DatabaseControlListener;
import com.akrivonos.app_standart_java.listeners.OpenListItemLinkListener;

import static com.akrivonos.app_standart_java.constants.Values.VIEW_TYPE_PICTURE_CARD;

/**
 * A simple {@link Fragment} subclass.
 */
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
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        databaseControlListener = new DatabaseControl(getContext());

        OpenListItemLinkListener startActivityControlListener = (OpenListItemLinkListener) getActivity();

        favoritesPictureAdapter = new PictureAdapter(startActivityControlListener, getContext());
        favoritesPictureAdapter.setVisibilityDeleteButton(true);

        RecyclerView favoritesRecyclerView = view.findViewById(R.id.favoriter_recycler_view);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        favoritesRecyclerView.setAdapter(favoritesPictureAdapter);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(favoritesRecyclerView);

        return view;
    }

}
