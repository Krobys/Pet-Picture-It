package com.akrivonos.app_standart_java;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.akrivonos.app_standart_java.database.DatabaseControl;
import com.akrivonos.app_standart_java.database.DatabaseControlListener;

import java.util.ArrayList;
import java.util.Map;

import static com.akrivonos.app_standart_java.AuthActivity.CURRENT_USER_NAME;
import static com.akrivonos.app_standart_java.MainActivity.SEARCH_TEXT;
import static com.akrivonos.app_standart_java.MainActivity.SPAN_URL;

public class ConventionHistoryActivity extends AppCompatActivity {

    private DatabaseControlListener databaseControlListener;
    private Map<String, ArrayList<String>> historyPhotos = null;
    private String userName;
    private TextView textHistoriesResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convention_history);
        textHistoriesResult = findViewById(R.id.textHistoryResult);
        textHistoriesResult.setMovementMethod(LinkMovementMethod.getInstance());

        databaseControlListener = new DatabaseControl(getApplicationContext());
    }

    private void getListUserHistory() {
        getUserName();
        historyPhotos = databaseControlListener.getHistoryConvention(userName);
    }

    private void getUserName() {
        Intent intent = getIntent();
        if (intent.hasExtra(CURRENT_USER_NAME)) {
            userName = intent.getStringExtra(CURRENT_USER_NAME);
        }
    }

    //TODO Сделать историю не сортированной по рзделам и имени, а просто подряд
    private void fillHistoryToTextView() {
        if (historyPhotos != null)
            if (historyPhotos.size() != 0) {
                textHistoriesResult.setText("");
                for (String key : historyPhotos.keySet()) {
                    textHistoriesResult.append(key + ":\n");
                    for (String url : historyPhotos.get(key)) {
                        setSpanTextInView(url, key);
                    }
                }
            } else {
                textHistoriesResult.setText(getString(R.string.no_info));
            }
    }

    private void setSpanTextInView(final String url, final String request) { //добавление активной ссылки для каждой фото
        final SpannableString string = new SpannableString(url);
        string.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(ConventionHistoryActivity.this, LinkContentActivity.class)
                        .putExtra(SPAN_URL, url)
                        .putExtra(SEARCH_TEXT, request)
                        .putExtra(CURRENT_USER_NAME, userName));
            }
        }, 0, url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textHistoriesResult.append(string);
        textHistoriesResult.append("\n");
    }

    @Override
    protected void onResume() {
        getListUserHistory();
        fillHistoryToTextView();
        super.onResume();
    }
}
