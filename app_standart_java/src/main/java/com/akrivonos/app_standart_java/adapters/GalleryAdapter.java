package com.akrivonos.app_standart_java.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akrivonos.app_standart_java.R;
import com.akrivonos.app_standart_java.models.PhotoGallery;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.PhotoViewHolder> {

    private ArrayList<PhotoGallery> photos = new ArrayList<>();
    private final LayoutInflater layoutInflater;

    public GalleryAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    public void setDataGallery(List<PhotoGallery> photos) {
        if (photos == null) return;
        this.photos = (ArrayList<PhotoGallery>) photos;
        notifyDataSetChanged();
    }

    public void addItemPhoto(PhotoGallery photoGallery) {
        photos.add(0, photoGallery);
        notifyItemInserted(0);
    }

    public ArrayList<PhotoGallery> getData() {
        return photos;
    }

    public void deleteFromGallery(int positionToRemove) {
        photos.remove(positionToRemove);
        notifyItemRemoved(positionToRemove);
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PhotoViewHolder(layoutInflater.inflate(R.layout.item_gallery, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder photoViewHolder, int position) {
        Uri uriPhoto = photos.get(position).getUriPhotoU();
        photoViewHolder.photoImageView.setImageURI(uriPhoto);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {

        final ImageView photoImageView;

        PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photo_from_camera);
        }
    }
}
