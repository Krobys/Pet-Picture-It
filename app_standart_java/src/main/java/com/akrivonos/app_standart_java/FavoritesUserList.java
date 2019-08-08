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
import com.akrivonos.app_standart_java.models.PhotoMap;

import static com.akrivonos.app_standart_java.AuthActivity.USER_NAME;
import static com.akrivonos.app_standart_java.MainActivity.SEARCH_TEXT;
import static com.akrivonos.app_standart_java.MainActivity.SPAN_URL;
import static com.akrivonos.app_standart_java.MainActivity.currentUser;

public class FavoritesUserList extends AppCompatActivity {

    private DatabaseControlListener databaseControlListener;
    private PhotoMap favoritePhotos = null;
    private String userName;
    private TextView textFavoritesResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_user_list);

        textFavoritesResult = findViewById(R.id.textFavoritesResult);
        textFavoritesResult.setMovementMethod(LinkMovementMethod.getInstance());
        databaseControlListener = new DatabaseControl(getApplicationContext());
        
    }

    private void getListUserFavorites() {
        getUserName();
        favoritePhotos = databaseControlListener.getAllFavoritesForUser(userName);
    }

    private void getUserName() {
        Intent intent = getIntent();
        if (intent.hasExtra(USER_NAME)) {
            userName = intent.getStringExtra(USER_NAME);
        }
    }

    private void fillHistoryToTextView() {
        if (favoritePhotos.size() != 0) {
            textFavoritesResult.setText("");
            while (favoritePhotos.nextSection()) {
                String sectionName = favoritePhotos.getCurrentSectionName();
                textFavoritesResult.append(sectionName + "\n");
                for (String photo : favoritePhotos.getValuesInSection()) {
                    setSpanTextInView(photo, sectionName);
                }
            }
        } else {
            textFavoritesResult.setText(getString(R.string.no_info));
        }
    }

    private void setSpanTextInView(final String url, final String request) { //добавление активной ссылки для каждой фото
        final SpannableString string = new SpannableString(url);
        string.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(FavoritesUserList.this, LinkContentActivity.class)
                        .putExtra(SPAN_URL, url)
                        .putExtra(SEARCH_TEXT, request)
                        .putExtra(USER_NAME, currentUser));
            }
        }, 0, url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textFavoritesResult.append(string);
        textFavoritesResult.append("\n");
    }

    @Override
    protected void onResume() {
        getListUserFavorites();
        fillHistoryToTextView();
        super.onResume();
    }

}
