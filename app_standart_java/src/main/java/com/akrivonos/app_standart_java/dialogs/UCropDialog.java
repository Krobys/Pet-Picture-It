package com.akrivonos.app_standart_java.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.akrivonos.app_standart_java.R;
import com.akrivonos.app_standart_java.listeners.NotifyGalleryAdapterListener;
import com.akrivonos.app_standart_java.listeners.StartUCropListener;
import com.akrivonos.app_standart_java.models.PhotoGallery;
import com.akrivonos.app_standart_java.room.GalleryPhoto;
import com.akrivonos.app_standart_java.room.RoomAppDatabase;

public class UCropDialog extends Dialog implements View.OnClickListener {
    private final PhotoGallery photoGallery;
    private final StartUCropListener startUCropListener;
    private final NotifyGalleryAdapterListener notifyGalleryAdapterListener;
    private final RoomAppDatabase appDatabase;
    public UCropDialog(Context context,
                       StartUCropListener startUCropListener,
                       NotifyGalleryAdapterListener notifyGalleryAdapterListener,
                       PhotoGallery photoGallery,
                       RoomAppDatabase roomAppDatabase) {
        super(context);
        this.photoGallery = photoGallery;
        this.startUCropListener = startUCropListener;
        this.notifyGalleryAdapterListener = notifyGalleryAdapterListener;
        appDatabase = roomAppDatabase;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_is_ucrop);

        TextView delete = findViewById(R.id.btn_change);
        delete.setOnClickListener(this);
        TextView cancel = findViewById(R.id.btn_save);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                GalleryPhoto galleryPhoto = new GalleryPhoto(photoGallery);
                appDatabase.galleryPhotoDao().addToGallery(galleryPhoto);
                notifyGalleryAdapterListener.addToAdapter(photoGallery);
                dismiss();
                break;
            case R.id.btn_change:
                dismiss();
                startUCropListener.startUCrop();
                break;
        }
    }
}
