package com.akrivonos.pi_school_w2;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akrivonos.pi_school_w2.Api.RetrofitSearchDownload;
import com.akrivonos.pi_school_w2.Models.PhotosSearchClass;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView searchResultTextView;
    private EditText searchRequestEditText;
    private Button searchButton;
    private ProgressBar progressBar;

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

        LiveData<PhotosSearchClass> liveData = RetrofitSearchDownload.getInstance().getDataBinder();
        liveData.observe(this, observerPhotosSearch);
    }

    private View.OnClickListener startSearch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String searchText = searchRequestEditText.getText().toString();
            if (!TextUtils.isEmpty(searchText)) {
                progressBar.setVisibility(View.VISIBLE);
                searchResultTextView.setVisibility(View.INVISIBLE);
                RetrofitSearchDownload.getInstance().startDownloadPictures(searchText);
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private Observer<PhotosSearchClass> observerPhotosSearch = new Observer<PhotosSearchClass>() {
        @Override
        public void onChanged(@Nullable PhotosSearchClass photosSearchClass) {
            if (photosSearchClass != null) {
                progressBar.setVisibility(View.INVISIBLE);
                searchResultTextView.setVisibility(View.VISIBLE);
                searchResultTextView.setText("");
                List<PhotosSearchClass.Photo> photos = photosSearchClass.getRsp().getPhotos().getPhoto();
                for (PhotosSearchClass.Photo photo : photos) {
                    searchResultTextView.append(getPhotoUrl(photo));
                }
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.error_download), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    };

    private String getPhotoUrl(PhotosSearchClass.Photo photo) {
        return "https://farm" + photo.getFarm() + ".staticflickr.com/" + photo.getServer() + "/" + photo.getId() + "_" + photo.getSecret() + ".jpg";
    }
}
