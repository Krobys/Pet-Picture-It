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
import com.akrivonos.app_standart_java.models.PhotoInfo;

import java.util.ArrayList;

import static com.akrivonos.app_standart_java.AuthActivity.USER_NAME;
import static com.akrivonos.app_standart_java.MainActivity.SEARCH_TEXT;
import static com.akrivonos.app_standart_java.MainActivity.SPAN_URL;
import static com.akrivonos.app_standart_java.MainActivity.currentUser;

public class ConventionHistoryActivity extends AppCompatActivity {

    DatabaseControlListener databaseControlListener;
    ArrayList<ArrayList<PhotoInfo>> historyPhotos = null;
    String userName;
    TextView textHistoriesResult;

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
        if (intent.hasExtra(USER_NAME)) {
            userName = intent.getStringExtra(USER_NAME);
        }
    }

    private void fillHistoryToTextView() {
        if (historyPhotos.size() != 0) {
            textHistoriesResult.setText("");
        } else {
            textHistoriesResult.setText(getString(R.string.no_info));
        }

        for (ArrayList<PhotoInfo> photoListByTitle : historyPhotos) {
            textHistoriesResult.append(photoListByTitle.get(0).getRequestText() + ":\n");
            for (PhotoInfo photo : photoListByTitle) {
                photo.showPhotoInfos();
                setSpanTextInView(photo);
            }
        }
    }

    private void setSpanTextInView(final PhotoInfo photo) { //добавление активной ссылки для каждой фото
        final SpannableString string = new SpannableString(photo.getUrlText());
        string.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(ConventionHistoryActivity.this, LinkContentActivity.class)
                        .putExtra(SPAN_URL, photo.getUrlText())
                        .putExtra(SEARCH_TEXT, photo.getRequestText())
                        .putExtra(USER_NAME, currentUser));
            }
        }, 0, photo.getUrlText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
