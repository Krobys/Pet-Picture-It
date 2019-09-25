package com.akrivonos.app_standart_java.fragments;


import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import com.akrivonos.app_standart_java.MainActivity;
import com.akrivonos.app_standart_java.R;
import com.akrivonos.app_standart_java.utils.PreferenceUtils;

import org.jetbrains.annotations.NotNull;

import io.ghyeok.stickyswitch.widget.StickySwitch;

import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_NO;
import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_YES;
import static android.support.v7.app.AppCompatDelegate.getDefaultNightMode;
import static com.akrivonos.app_standart_java.constants.Values.DEFAULT_MODE_NIGHT;
import static com.akrivonos.app_standart_java.constants.Values.EXPANDABLE_VALUE;

public class SettingsFragment extends Fragment {
    public static final String SETTINGS_FRAGMENT = "settings_fragment";
    private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (buttonView, isChecked) -> changeAppThemeStyle(isChecked);
    private Switch aSwitch;

    private final View.OnClickListener popupStartClickListener = v -> {
        Activity activity = getActivity();
        if (activity == null) return;
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);
        PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnimationStyle(R.style.AnimationPopUpCustom);
        popupWindow.setContentView(popupView);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAsDropDown(v, 0, 0);

        Button acceptButton = popupView.findViewById(R.id.button_popup_accept);
        StickySwitch switchStateMeet = popupView.findViewById(R.id.switch_popup_custom);

        TextView titleCloseMeet = popupView.findViewById(R.id.textViewTitleClose);
        TextView descCloseMeet = popupView.findViewById(R.id.textViewDescriptionClose);
        TextView titleFarMeet = popupView.findViewById(R.id.textViewTitleFar);
        TextView descFarMeet = popupView.findViewById(R.id.textViewDescriptionFar);

        int colorActive = ResourcesCompat.getColor(getResources(), R.color.colorActiveTextPopUp, null);
        int colorNotActive = ResourcesCompat.getColor(getResources(), R.color.colorNotActiveTextPopUp, null);

        ObjectAnimator valueAnimatorActivateTitleClose = ObjectAnimator.ofObject(titleCloseMeet, "textColor", new ArgbEvaluator(), colorActive, colorNotActive);
        ObjectAnimator valueAnimatorActivateDescClose = ObjectAnimator.ofObject(descCloseMeet, "textColor", new ArgbEvaluator(), colorActive, colorNotActive);
        ObjectAnimator valueAnimatorActivateTitleFar = ObjectAnimator.ofObject(titleFarMeet, "textColor", new ArgbEvaluator(), colorActive, colorNotActive);
        ObjectAnimator valueAnimatorActivateDescFar = ObjectAnimator.ofObject(descFarMeet, "textColor", new ArgbEvaluator(), colorActive, colorNotActive);

        boolean switchChecked = PreferenceUtils.getStateMeetRequierments(getContext());
        switchStateMeet.setDirection((switchChecked) ? StickySwitch.Direction.RIGHT : StickySwitch.Direction.LEFT);
        if (switchChecked) {
            titleCloseMeet.setTextColor(colorNotActive);
            descCloseMeet.setTextColor(colorNotActive);
        } else {
            titleFarMeet.setTextColor(colorNotActive);
            descFarMeet.setTextColor(colorNotActive);
        }

        acceptButton.setOnClickListener(v1 -> {
            boolean state;
            state = switchStateMeet.getDirection() != StickySwitch.Direction.LEFT;
            PreferenceUtils.saveStateMeetRequierments(getContext(), state);
            popupWindow.dismiss();
        });

        switchStateMeet.setOnSelectedChangeListener(new StickySwitch.OnSelectedChangeListener() {
            @Override
            public void onSelectedChange(@NotNull StickySwitch.Direction direction, @NotNull String s) {
                if (direction == StickySwitch.Direction.RIGHT) {
                    valueAnimatorActivateTitleClose.setDuration(300).start();
                    valueAnimatorActivateDescClose.setDuration(300).start();
                    valueAnimatorActivateTitleFar.setDuration(300).reverse();
                    valueAnimatorActivateDescFar.setDuration(300).reverse();
                } else {
                    valueAnimatorActivateTitleClose.setDuration(300).reverse();
                    valueAnimatorActivateDescClose.setDuration(300).reverse();
                    valueAnimatorActivateTitleFar.setDuration(300).start();
                    valueAnimatorActivateDescFar.setDuration(300).start();
                }
            }
        });
    };

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        setHasOptionsMenu(true);
        aSwitch = view.findViewById(R.id.switch1);
        setSwitchDependsStyle();
        aSwitch.setOnCheckedChangeListener(onCheckedChangeListener);

        Button popupStartButton = view.findViewById(R.id.show_popup_button);
        popupStartButton.setOnClickListener(popupStartClickListener);
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
        new Handler().post(() -> {
            Activity activity = getActivity();
            if (activity == null) return;
            Intent intent = activity.getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            getActivity().overridePendingTransition(0, 0);
            getActivity().finish();

            getActivity().overridePendingTransition(0, 0);
            intent.putExtra(SETTINGS_FRAGMENT, "settings");
            intent.putExtra(EXPANDABLE_VALUE, ((MainActivity) activity).getExpandable());
            startActivity(intent);
        });
    }
}
