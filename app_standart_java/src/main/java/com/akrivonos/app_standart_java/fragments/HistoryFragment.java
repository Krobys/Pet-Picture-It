package com.akrivonos.app_standart_java.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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


public class HistoryFragment extends Fragment {

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

        DatabaseControlListener databaseControlListener = new DatabaseControl(getContext());
        ArrayList<PhotoInfo> historyPhotos = databaseControlListener.getHistoryConvention(PreferenceUtils.getCurrentUserName(getContext()));
        historyPictureAdapter.setData(historyPhotos);
        historyPictureAdapter.notifyDataSetChanged();
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
