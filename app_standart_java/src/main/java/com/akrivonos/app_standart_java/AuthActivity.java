package com.akrivonos.app_standart_java;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AuthActivity extends AppCompatActivity {

    protected static final String USER_NAME = "user_name";
    EditText userNameField;
    Button logInButton;
    View.OnClickListener checkUser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String userName = userNameField.getText().toString().toLowerCase();
            if (!TextUtils.isEmpty(userName)) {
                startActivity(new Intent(AuthActivity.this, MainActivity.class).putExtra(USER_NAME, userName));
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

        userNameField = findViewById(R.id.nameOfUserField);
        logInButton = findViewById(R.id.logInButton);
        logInButton.setOnClickListener(checkUser);
    }
}
