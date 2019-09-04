package com.akrivonos.app_standart_java;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_NO;
import static com.akrivonos.app_standart_java.constants.Values.CURRENT_USER_NAME;
import static com.akrivonos.app_standart_java.constants.Values.DEFAULT_MODE_NIGHT;

public class AuthActivity extends AppCompatActivity {

    private EditText userNameField;
    private final View.OnClickListener checkUser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String userName = userNameField.getText().toString().toLowerCase();
            if (!TextUtils.isEmpty(userName)) {
                saveCurrentUser(userName);
                startActivity(new Intent(AuthActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(AuthActivity.this, getString(R.string.auth_field_empty_error), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        restoreDefaultNightMode();
        userNameField = findViewById(R.id.nameOfUserField);
        Button logInButton = findViewById(R.id.logInButton);
        logInButton.setOnClickListener(checkUser);
    }

    private void saveCurrentUser(String currentUserName) { //сохранение состояния поля для ввода
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.edit().putString(CURRENT_USER_NAME, currentUserName).apply();
    }

    private void restoreDefaultNightMode(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int nightMode = sharedPreferences.getInt(DEFAULT_MODE_NIGHT, MODE_NIGHT_NO);
        int currentNightMode = AppCompatDelegate.getDefaultNightMode();
        if(currentNightMode != nightMode){
            AppCompatDelegate.setDefaultNightMode(nightMode);
            recreate();
        }
    }
}
