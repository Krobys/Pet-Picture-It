package com.akrivonos.app_standart_java;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.akrivonos.app_standart_java.adapters.GalleryAdapter;
import com.akrivonos.app_standart_java.database.DatabaseControl;
import com.akrivonos.app_standart_java.database.DatabaseControlListener;
import com.akrivonos.app_standart_java.dialogs.UCropDialog;
import com.akrivonos.app_standart_java.listeners.NotifyGalleryAdapterListener;
import com.akrivonos.app_standart_java.listeners.StartUCropListener;
import com.akrivonos.app_standart_java.models.PhotoGallery;
import com.akrivonos.app_standart_java.utils.PreferenceUtils;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.akrivonos.app_standart_java.constants.Values.MY_CAMERA_PERMISSION_CODE;
import static com.akrivonos.app_standart_java.constants.Values.REQUEST_IMAGE_CAPTURE;

public class GalleryActivity extends AppCompatActivity implements StartUCropListener, NotifyGalleryAdapterListener {
    private final String TAG = "test";
    private Toolbar toolbar;
    private File currentPhoto;
    private Uri photoUri;
    private GalleryAdapter galleryAdapter;
    private DatabaseControlListener databaseControlListener;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private final ItemTouchHelper.Callback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) { // Свайп для recycleView

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            int position = viewHolder.getAdapterPosition();
            Uri uriPhoto = galleryAdapter.getData().get(position).getUriPhoto();
            deleteFileFromDevice(new File(uriPhoto.toString()).getName());
            databaseControlListener.deleteFromGallery(uriPhoto);
            galleryAdapter.deleteFromGallery(position);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        toolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);

        databaseControlListener = new DatabaseControl(this);

        galleryAdapter = new GalleryAdapter(this);
        galleryAdapter.setDataGallery(databaseControlListener.getPhotosFromGallery(PreferenceUtils.getCurrentUserName(this)));

        RecyclerView recyclerViewGallery = findViewById(R.id.recycler_view_gallery);
        recyclerViewGallery.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewGallery.setAdapter(galleryAdapter);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerViewGallery);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.photo_camera){
            if(checkPermissionsCamera()){
                dispatchTakePictureIntent();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        toolbar.setTitle(getString(R.string.gallery));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_IMAGE_CAPTURE:
                if(resultCode == RESULT_OK)
                showUCropDialog();
                break;
            case UCrop.REQUEST_CROP:
                if(resultCode == RESULT_OK){
                    final Uri resultUri = UCrop.getOutput(data);
                    PhotoGallery photoGallery = new PhotoGallery();
                    photoGallery.setUriPhoto(resultUri);
                    photoGallery.setUserName(PreferenceUtils.getCurrentUserName(this));
                    photoGallery.setDateMillis(System.currentTimeMillis());
                    databaseControlListener.addToGallery(photoGallery);
                    galleryAdapter.addItemPhoto(photoGallery);
                }
        break;
        }
    }

    private void dispatchTakePictureIntent() {  // запуск камеры для снятия фотографии
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File imagefile;
            imagefile = createImageFile();
            if(imagefile != null){
                photoUri = FileProvider.getUriForFile(this, "com.akrivonos.app_standart_java.provider", imagefile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    private File createImageFile() {//создание нового файла для фотографии
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_.jpg";
        File storageDir = getFilesDir();
        File image = new File(storageDir, imageFileName);
        currentPhoto = image;
        return image;
    }

    private boolean checkPermissionsCamera() {//проверка разрешений
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, MY_CAMERA_PERMISSION_CODE);
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_CAMERA_PERMISSION_CODE){
            for (int perm : grantResults) {
                if (perm != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            dispatchTakePictureIntent();
        }
    }

    private void showUCropDialog(){//показать диалог выбора использования uCrop
        PhotoGallery photoGallery = new PhotoGallery();
        photoGallery.setUriPhoto(photoUri);
        photoGallery.setUserName(PreferenceUtils.getCurrentUserName(this));
        photoGallery.setDateMillis(System.currentTimeMillis());
        UCropDialog cdd = new UCropDialog(this,this,this, photoGallery);
        Objects.requireNonNull(cdd.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cdd.show();
    }

    @Override
    public void startUCrop() {//запустить обработку фотографии с помощью uCrop
        Uri photo = Uri.fromFile(currentPhoto);
        UCrop uCrop = UCrop.of(photo, photo);
        uCrop.withAspectRatio(3, 4);
        uCrop.withMaxResultSize(480, 720);
        uCrop.withOptions(new UCrop.Options());
        uCrop.start(GalleryActivity.this);
    }

    @Override
    public void addToAdapter(PhotoGallery photoGallery) {// отобразить изменения в списке
        galleryAdapter.addItemPhoto(photoGallery);
    }

    private void deleteFileFromDevice(String fileName){
        if(getApplicationContext().deleteFile(fileName)) {
            Toast.makeText(getApplicationContext(),"File Deleted",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(),"Not Deleted",Toast.LENGTH_SHORT).show();
        }
    }
}
