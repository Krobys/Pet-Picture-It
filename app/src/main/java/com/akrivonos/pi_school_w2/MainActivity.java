package com.akrivonos.pi_school_w2;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akrivonos.pi_school_w2.Api.RetrofitSearchDownload;
import com.akrivonos.pi_school_w2.Models.Rsp;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView searchResultTextView;
    private EditText searchRequestEditText;
    private Button searchButton;
    private ProgressBar progressBar;
    protected static final String SPAN_URL = "span_url";
    private View.OnClickListener startSearch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String searchText = searchRequestEditText.getText().toString();
            if (!TextUtils.isEmpty(searchText)) {
                progressBar.setVisibility(View.VISIBLE);
                searchResultTextView.setVisibility(View.GONE);
                searchButton.setClickable(false);
                RetrofitSearchDownload.getInstance().startDownloadPictures(searchText);
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
            }
        }
    };
    private Observer<Rsp> observerPhotosSearch = new Observer<Rsp>() {
        @Override
        public void onChanged(@Nullable Rsp photosSearchClass) {
            if (photosSearchClass != null) {
                progressBar.setVisibility(View.GONE);
                searchResultTextView.setVisibility(View.VISIBLE);
                searchButton.setClickable(true);
                searchResultTextView.setText("");
                List<Rsp.Photo> photos = photosSearchClass.getPhotos().getPhoto();
                for (Rsp.Photo photo : photos) {
                    final String photoUrl = getPhotoUrl(photo);

                    final SpannableString string = new SpannableString(photoUrl);
                    string.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            startActivity(new Intent(MainActivity.this, LinkContentActivity.class).putExtra(SPAN_URL, photoUrl));
                        }
                    }, 0, photoUrl.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    searchResultTextView.append(string);
                    searchResultTextView.append("\n");
                }
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.error_download), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        searchRequestEditText = findViewById(R.id.search_request);
        searchButton = findViewById(R.id.search_button);
        searchResultTextView = findViewById(R.id.search_result);
        searchButton.setOnClickListener(startSearch);

        searchResultTextView.setMovementMethod(LinkMovementMethod.getInstance());

        LiveData<Rsp> liveData = RetrofitSearchDownload.getInstance().getDataBinder();
        liveData.observe(this, observerPhotosSearch);
    }

    private String getPhotoUrl(Rsp.Photo photo) {
        String farm = photo.getFarm();
        String server = photo.getServer();
        String id = photo.getId();
        String secret = photo.getSecret();
        return "https://farm" + farm + ".staticflickr.com/" + server + "/" + id + "_" + secret + ".jpg";
    }
}
