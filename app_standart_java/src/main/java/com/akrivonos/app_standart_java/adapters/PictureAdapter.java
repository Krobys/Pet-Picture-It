package com.akrivonos.app_standart_java.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.akrivonos.app_standart_java.R;
import com.akrivonos.app_standart_java.database.DatabaseControl;
import com.akrivonos.app_standart_java.database.DatabaseControlListener;
import com.akrivonos.app_standart_java.listeners.ControlBorderDownloaderListener;
import com.akrivonos.app_standart_java.listeners.StartActivityControlListener;
import com.akrivonos.app_standart_java.models.PhotoInfo;
import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.akrivonos.app_standart_java.utils.InternetUtils.isInternetConnectionEnable;

public class PictureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final WeakReference<Context> contextWeakReference;
    public static final int VIEW_TYPE_PICTURE_CARD = 2;
    private final StartActivityControlListener activityControl;
    private final DatabaseControlListener databaseControlListener;
    private ControlBorderDownloaderListener borderDownloader;
    private boolean visibilityDeleteButton = false;
    private static final int VIEW_TYPE_TITLE = 1;
    private ArrayList<PhotoInfo> photosPicture = new ArrayList<>();

    private int currentPage;
    private int pagesAmount;

    public PictureAdapter(StartActivityControlListener startActivityControlListener, Context appContext) {
        contextWeakReference = new WeakReference<>(appContext);
        this.databaseControlListener = new DatabaseControl(contextWeakReference.get());
        activityControl = startActivityControlListener;
    }

    public PictureAdapter(StartActivityControlListener startActivityControlListener, ControlBorderDownloaderListener controlBorderDownloaderListener, Context appContext) {
        contextWeakReference = new WeakReference<>(appContext);
        this.databaseControlListener = new DatabaseControl(contextWeakReference.get());
        activityControl = startActivityControlListener;
        borderDownloader = controlBorderDownloaderListener;
    }

    public ArrayList<PhotoInfo> getData() {
        return photosPicture;
    }

    public void setData(ArrayList<PhotoInfo> photosPicture) { // добавляем информацию в адаптер для отображения
        int oldSize = this.photosPicture.size();
        this.photosPicture.addAll(photosPicture);
        int newSize = this.photosPicture.size();
        notifyItemRangeChanged(oldSize, newSize);
    }

    public void setVisibilityDeleteButton(boolean visibility) { // показывать кнопку удаления
        visibilityDeleteButton = visibility;
    }

    public void deleteItem(int position) { //далить элемент из адаптера
        photosPicture.remove(position);
        notifyItemRemoved(position);
    }

    public void setPageSettings(int currentPage, int pagesAmount) { //установить настройи загрузки(для бесконечной загрузки)
        this.currentPage = currentPage;
        this.pagesAmount = pagesAmount;
    }

    public void throwOffData() {
        photosPicture = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) { // разделяем элементы на два типа(карточка и заглавный секции)
        if (photosPicture.get(position).getUrlText() == null) {
            return VIEW_TYPE_TITLE;
        } else {
            return VIEW_TYPE_PICTURE_CARD;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_TITLE:
                view = LayoutInflater.from(contextWeakReference.get()).inflate(R.layout.item_title_picture, viewGroup, false);
                return new TitleViewHolder(view);
            case VIEW_TYPE_PICTURE_CARD:
                view = LayoutInflater.from(contextWeakReference.get()).inflate(R.layout.item_picture, viewGroup, false);
                return new PictureViewHolder(view);
            default:
                view = LayoutInflater.from(contextWeakReference.get()).inflate(R.layout.item_picture, viewGroup, false);
        }
        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case VIEW_TYPE_TITLE:
                TitleViewHolder titleViewHolder = (TitleViewHolder) viewHolder;
                titleViewHolder.titlePictureSection.setText(photosPicture.get(position).getRequestText().toUpperCase());
                break;
            case VIEW_TYPE_PICTURE_CARD:
                PictureViewHolder pictureViewHolder = (PictureViewHolder) viewHolder;
                Glide.with(contextWeakReference.get())
                        .load(photosPicture.get(position).getUrlText())
                        .into(pictureViewHolder.picture);
                pictureViewHolder.requestText.setText(photosPicture.get(position).getRequestText());
                pictureViewHolder.photoInfo = photosPicture.get(position);
                break;
        }

        if (position == (photosPicture.size() - 3)) { //Скачивание следующей страницы данных
            if ((currentPage < pagesAmount) && isInternetConnectionEnable(contextWeakReference.get())) {
                borderDownloader.loadNextPage(currentPage + 1);
            }
        }
    }

    @Override
    public int getItemCount() {
        return photosPicture.size();
    }

    class PictureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener { // Холдер для карточки с картинкой

        private final ImageView picture;
        private final TextView requestText;
        private final ImageButton deleteButton;
        private PhotoInfo photoInfo;

        PictureViewHolder(@NonNull View itemView) {
            super(itemView);
            deleteButton = itemView.findViewById(R.id.delete_button);
            deleteButton.setOnClickListener(this);
            deleteButton.setVisibility((visibilityDeleteButton)
                    ? View.VISIBLE
                    : View.GONE);
            picture = itemView.findViewById(R.id.picture_downloaded);
            requestText = itemView.findViewById(R.id.request_text);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            if (v.getId() == R.id.delete_button) {
                databaseControlListener.setPhotoNotFavorite(photosPicture.get(adapterPosition));
                deleteItem(adapterPosition);
            } else {
                activityControl.startActivity(photoInfo);
            }
        }

    }

    class TitleViewHolder extends RecyclerView.ViewHolder { // Холдер для заглавия

        final TextView titlePictureSection;

        TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            titlePictureSection = itemView.findViewById(R.id.title_picture_topic);
        }
    }

}
