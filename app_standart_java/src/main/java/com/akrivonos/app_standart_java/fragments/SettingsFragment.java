package com.akrivonos.app_standart_java.fragments;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.akrivonos.app_standart_java.MainActivity;
import com.akrivonos.app_standart_java.R;

import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_NO;
import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_YES;
import static android.support.v7.app.AppCompatDelegate.getDefaultNightMode;
import static com.akrivonos.app_standart_java.constants.TagsFragments.SETTINGS_FRAGMENT;
import static com.akrivonos.app_standart_java.constants.Values.DEFAULT_MODE_NIGHT;
import static com.akrivonos.app_standart_java.constants.Values.EXPANDABLE_VALUE;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            changeAppThemeStyle(isChecked);
        }
    };
    private Switch aSwitch;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        setHasOptionsMenu(true);
        aSwitch = view.findViewById(R.id.switch1);
        setSwitchDependsStyle();
        aSwitch.setOnCheckedChangeListener(onCheckedChangeListener);

        return view;
    }

    private void saveDefaultNightMode(int defaultMode) { //сохранить тему приложения (восстанавливается в AuthActivity)
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.edit().putInt(DEFAULT_MODE_NIGHT, defaultMode).apply();
    }

    private void changeAppThemeStyle(boolean isChecked) { //изменить тему
        int style_mode = (isChecked)
                ? MODE_NIGHT_YES
                : MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(style_mode);
        saveDefaultNightMode(style_mode);
        recreateActivity();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Activity activity = getActivity();
        if(activity != null)
        activity.setTitle("Settings");
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setSwitchDependsStyle() {
        int styleMode = getDefaultNightMode();
        aSwitch.setChecked((styleMode == MODE_NIGHT_YES));
    }

    private void recreateActivity(){
        new Handler().post(new Runnable() {

            @Override
            public void run()
            {
                Intent intent = getActivity().getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                getActivity().overridePendingTransition(0, 0);
                getActivity().finish();

                getActivity().overridePendingTransition(0, 0);
                intent.putExtra(SETTINGS_FRAGMENT, "settings" );
                intent.putExtra(EXPANDABLE_VALUE, ((MainActivity)getActivity()).getExpandable());
                startActivity(intent);
            }
        });
    }
}
