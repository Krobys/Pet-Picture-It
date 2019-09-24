package com.akrivonos.app_standart_java;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.jakewharton.rxbinding3.view.RxView;

import io.reactivex.disposables.Disposable;

import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_NO;
import static com.akrivonos.app_standart_java.constants.Values.CURRENT_USER_NAME;
import static com.akrivonos.app_standart_java.constants.Values.DEFAULT_MODE_NIGHT;

public class AuthActivity extends AppCompatActivity {

    private EditText userNameField;
    private Disposable loginButDis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        restoreDefaultNightMode();
        userNameField = findViewById(R.id.nameOfUserField);
        Button logInButton = findViewById(R.id.logInButton);
        loginButDis = RxView.clicks(logInButton)
                .map(unit -> userNameField.getText().toString().toLowerCase())
                .filter(userName -> !TextUtils.isEmpty(userName))
                .subscribe(userName -> {
                    saveCurrentUser(userName);
                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
                    finish();
                });
    }

    private void saveCurrentUser(String currentUserName) { //сохранение состояния поля для ввода
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.edit().putString(CURRENT_USER_NAME, currentUserName).apply();
    }

    private void restoreDefaultNightMode() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int nightMode = sharedPreferences.getInt(DEFAULT_MODE_NIGHT, MODE_NIGHT_NO);
        int currentNightMode = AppCompatDelegate.getDefaultNightMode();
        if (currentNightMode != nightMode) {
            AppCompatDelegate.setDefaultNightMode(nightMode);
            recreate();
        }
    }

    @Override
    protected void onDestroy() {
        loginButDis.dispose();
        super.onDestroy();
    }
}
