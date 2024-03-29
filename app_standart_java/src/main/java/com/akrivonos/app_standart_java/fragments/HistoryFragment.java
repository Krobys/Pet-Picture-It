package com.akrivonos.app_standart_java.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akrivonos.app_standart_java.MainActivity;
import com.akrivonos.app_standart_java.R;
import com.akrivonos.app_standart_java.adapters.PictureAdapter;
import com.akrivonos.app_standart_java.listeners.OpenListItemLinkListener;
import com.akrivonos.app_standart_java.models.PhotoInfo;
import com.akrivonos.app_standart_java.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.Collections;


public class HistoryFragment extends Fragment {
    public static final String HISTORY_FRAGMENT = "history_fragment";
    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        setHasOptionsMenu(true);
        OpenListItemLinkListener startActivityControlListener = (OpenListItemLinkListener) getActivity();
        PictureAdapter historyPictureAdapter = new PictureAdapter(startActivityControlListener, getContext());

        RecyclerView recyclerView = view.findViewById(R.id.history_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(historyPictureAdapter);

        MainActivity mainActivity = (MainActivity) getActivity();
        if(mainActivity != null){
            ArrayList<PhotoInfo> historyPhotos = new ArrayList<>(mainActivity.getDatabase()
                    .historyPhotoDao()
                    .getHistoryConvention(PreferenceUtils.getCurrentUserName(getContext())));
            Collections.reverse(historyPhotos);
            historyPictureAdapter.setData(historyPhotos);
            historyPictureAdapter.notifyDataSetChanged();
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Activity activity = getActivity();
        if(activity != null)
        activity.setTitle("History");
        super.onCreateOptionsMenu(menu, inflater);
    }
}
