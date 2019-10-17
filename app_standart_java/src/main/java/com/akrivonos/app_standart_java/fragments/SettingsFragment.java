package com.akrivonos.app_standart_java.fragments;


import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.akrivonos.app_standart_java.MainActivity;
import com.akrivonos.app_standart_java.R;
import com.akrivonos.app_standart_java.utils.PreferenceUtils;
import com.akrivonos.app_standart_java.workers.DownloadPicturesWorker;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.ghyeok.stickyswitch.widget.StickySwitch;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static androidx.appcompat.app.AppCompatDelegate.getDefaultNightMode;
import static com.akrivonos.app_standart_java.constants.Values.DEFAULT_MODE_NIGHT;
import static com.akrivonos.app_standart_java.constants.Values.REQUEST_TEXT_SCHEDULED;
import static com.akrivonos.app_standart_java.constants.Values.TAG_SCHEDULED_WORK;

public class SettingsFragment extends Fragment {
    public static final String SETTINGS_FRAGMENT = "settings_fragment";
    public static final String TEXT_REQUEST_SCHEDULED_SETTINGS = "text_request_scheduled";
    public static final String ID_RADIOBUTTON_SELECTED_SCHEDULED_SETTINGS = "id_radiobutton_scheduled";
    private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (buttonView, isChecked) -> changeAppThemeStyle(isChecked);
    private Switch aSwitch;
    private CheckBox checkBoxBackgroundTask;

    private final CompoundButton.OnCheckedChangeListener checkedUpdateOnBackground = (buttonView, isChecked) -> {
        if (isChecked) {
            startPopUpSettingsBackgroundTask(buttonView, true);
        } else {
            Context context = getContext();
            if (context != null)
                WorkManager.getInstance(getContext()).cancelAllWorkByTag(TAG_SCHEDULED_WORK);
        }
    };

    private final View.OnClickListener updateBackgroundServiceSettings = buttonView -> {
        if (checkBoxBackgroundTask.isChecked()) {
            startPopUpSettingsBackgroundTask(checkBoxBackgroundTask, false);
        }
    };

    public SettingsFragment() {
        // Required empty public constructor
    }

    private void startPopUpSettingsBackgroundTask(View view, boolean isFirstSetUp) {
        if (30 >= android.os.Build.VERSION_CODES.O) {
            Activity activity = getActivity();
            if (activity == null) return;
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_window_background_download_settings, null);
            PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            popupWindow.setAnimationStyle(R.style.AnimationPopUpCustom);
            popupWindow.setOutsideTouchable(false);
            popupWindow.setFocusable(true);
            popupWindow.setContentView(popupView);
            popupWindow.setOnDismissListener((isFirstSetUp) ? () -> checkBoxBackgroundTask.setChecked(false) : null);
            popupWindow.showAsDropDown(view, 15, 15, Gravity.CENTER_HORIZONTAL);
            Button applySettings = popupView.findViewById(R.id.button_apply_background_task);
            Button cancelButton = popupView.findViewById(R.id.cancel_schedule_button);
            EditText editTextScheduled = popupView.findViewById(R.id.editTextScheduledRequest);
            RadioGroup radioGroupBackgroundVariants = popupView.findViewById(R.id.radioGroupBackgroundUpdate);

            if (!isFirstSetUp) {
                Context context = getContext();
                if (context != null) {
                    String textRequestWorkerSchedule = PreferenceUtils.getScheduleTaskRequestText(context);
                    int checkedVariantRadioId = PreferenceUtils.getScheduleTaskIdRadiobuttonSelected(context);

                    editTextScheduled.setText(textRequestWorkerSchedule);
                    radioGroupBackgroundVariants.check(checkedVariantRadioId);
                }
            }

            final int[] requestFrequency = new int[1];
            radioGroupBackgroundVariants.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId) {
                    case R.id.radioButton1:
                        requestFrequency[0] = 15;
                        break;
                    case R.id.radioButton2:
                        requestFrequency[0] = 30;
                        break;
                    case R.id.radioButton3:
                        requestFrequency[0] = 60;
                        break;
                    case R.id.radioButton4:
                        requestFrequency[0] = 360;
                        break;
                    case R.id.radioButton5:
                        requestFrequency[0] = 720;
                        break;
                    case R.id.radioButton6:
                        requestFrequency[0] = 1440;
                        break;
                }
            });

            applySettings.setOnClickListener(v -> {
                String scheduledRequestText = editTextScheduled.getText().toString();
                if (!TextUtils.isEmpty(scheduledRequestText)) {
                    Constraints constraints = new Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .setRequiresStorageNotLow(true)
                            .setRequiresDeviceIdle(false)
                            .build();
                    Data data = new Data.Builder()
                            .putString(REQUEST_TEXT_SCHEDULED, scheduledRequestText)
                            .build();

                    PeriodicWorkRequest periodicWorkRequestDownload = new PeriodicWorkRequest.Builder(DownloadPicturesWorker.class, requestFrequency[0], TimeUnit.MINUTES)
                            .setConstraints(constraints)
                            .addTag(TAG_SCHEDULED_WORK)
                            .setInputData(data)
                            .build();

                    Context context = getContext();
                    if (context != null) {
                        if (!isFirstSetUp) {
                            WorkManager.getInstance(context).cancelAllWorkByTag(TAG_SCHEDULED_WORK);
                        }
                        WorkManager.getInstance(context).enqueue(periodicWorkRequestDownload);
                        PreferenceUtils.setScheduleTaskSettings(context, scheduledRequestText, radioGroupBackgroundVariants.getCheckedRadioButtonId());
                    }
                    popupWindow.setOnDismissListener(null);
                    popupWindow.dismiss();
                }
            });

            cancelButton.setOnClickListener(view1 -> {
                popupWindow.dismiss();
            });
        } else {
            Toast.makeText(getContext(), "android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O", Toast.LENGTH_SHORT).show();
            Log.d("test", "android.os.Build.VERSION.SDK_INT: " + android.os.Build.VERSION.SDK_INT + " android.os.Build.VERSION_CODES.O: " + android.os.Build.VERSION_CODES.O);
            checkBoxBackgroundTask.setChecked(false);
        }
    }

    private boolean isScheduleWorkerActive() {
        Context context = getContext();
        if (context != null)
            try {
                String idWorker = PreferenceUtils.getScheduledTaskId(getContext());
                if (!idWorker.equals("0")) {
                    WorkInfo workInfo = WorkManager.getInstance(context).getWorkInfoById(UUID.fromString(idWorker)).get();
                    if(workInfo != null){
                        return workInfo.getState() == WorkInfo.State.ENQUEUED;
                    }
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        return false;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        setHasOptionsMenu(true);
        aSwitch = view.findViewById(R.id.switch1);
        setSwitchDependsStyle();
        aSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBoxBackgroundTask = view.findViewById(R.id.check_box_allow_backgrounds_updates);
        checkBoxBackgroundTask.setChecked(isScheduleWorkerActive());
        checkBoxBackgroundTask.setOnCheckedChangeListener(checkedUpdateOnBackground);
        ImageButton backgroundTaskReconfigureButton = view.findViewById(R.id.edit_background_updates_button);
        backgroundTaskReconfigureButton.setOnClickListener(updateBackgroundServiceSettings);
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
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) activity.isRecreated = true;
        AppCompatDelegate.setDefaultNightMode(style_mode);
        saveDefaultNightMode(style_mode);
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
}
