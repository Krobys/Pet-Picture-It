package com.akrivonos.app_standart_java.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class ScheduledPictureFragment extends Fragment {
    public final static String SCHEDULE_FRAGMENT = "schedule_fragment";

    public ScheduledPictureFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scheduled_picture, container, false);

        setHasOptionsMenu(true);
        OpenListItemLinkListener startActivityControlListener = (OpenListItemLinkListener) getActivity();
        PictureAdapter schedulePictureAdapter = new PictureAdapter(startActivityControlListener, getContext());

        RecyclerView recyclerView = view.findViewById(R.id.schedule_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(schedulePictureAdapter);

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            ArrayList<PhotoInfo> schedulePhotos = new ArrayList<>(mainActivity.getDatabase()
                    .scheduledPicturesDao()
                    .getSchedulePictures(PreferenceUtils.getCurrentUserName(getContext())));
            schedulePictureAdapter.setData(schedulePhotos);
            schedulePictureAdapter.notifyDataSetChanged();
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Activity activity = getActivity();
        if (activity != null)
            activity.setTitle("Scheduled Pictures");
        super.onCreateOptionsMenu(menu, inflater);
    }

}
