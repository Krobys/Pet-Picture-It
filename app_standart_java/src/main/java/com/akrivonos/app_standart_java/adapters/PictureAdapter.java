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
import com.akrivonos.app_standart_java.listeners.ControlBorderDownloaderListener;
import com.akrivonos.app_standart_java.listeners.OpenListItemLinkListener;
import com.akrivonos.app_standart_java.models.PhotoInfo;
import com.akrivonos.app_standart_java.room.FavoritePhoto;
import com.akrivonos.app_standart_java.room.RoomAppDatabase;
import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding3.view.RxView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

import static com.akrivonos.app_standart_java.constants.Values.PAGE_MAP_PIC;
import static com.akrivonos.app_standart_java.constants.Values.VIEW_TYPE_PICTURE_CARD;
import static com.akrivonos.app_standart_java.constants.Values.VIEW_TYPE_TITLE;
import static com.akrivonos.app_standart_java.utils.InternetUtils.isInternetConnectionEnable;

public class PictureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final LayoutInflater layoutInflater;

    private final OpenListItemLinkListener activityControl;
    private ControlBorderDownloaderListener borderDownloader = null;
    private boolean visibilityDeleteButton = false;

    private int typeLoadPage;
    private final ArrayList<PhotoInfo> photosPicture = new ArrayList<>();
    private RoomAppDatabase appDatabase;
    private int currentPage;
    private int pagesAmount;

    public PictureAdapter(OpenListItemLinkListener startActivityControlListener, Context context) { // конструктор адаптера без бесконечной подгрузки
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        activityControl = startActivityControlListener;
    }

    public PictureAdapter(OpenListItemLinkListener startActivityControlListener, Context context, RoomAppDatabase appDatabase) { // конструктор адаптера без бесконечной подгрузки
        this.context = context;
        this.appDatabase = appDatabase;
        layoutInflater = LayoutInflater.from(context);
        activityControl = startActivityControlListener;
    }

    public PictureAdapter(OpenListItemLinkListener startActivityControlListener, ControlBorderDownloaderListener controlBorderDownloaderListener, Context context) { //с бесконечной подгрузкой
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        activityControl = startActivityControlListener;
        borderDownloader = controlBorderDownloaderListener;
    }

    public ArrayList<PhotoInfo> getData() {
        return photosPicture;
    }

    public void setData(List<PhotoInfo> photosPicture) {
        if (photosPicture == null) return;// добавляем информацию в адаптер для отображения
        int oldSize = this.photosPicture.size();
        int newSize = oldSize + photosPicture.size();

        this.photosPicture.addAll(photosPicture);
        notifyItemRangeChanged(oldSize, newSize);
    }

    public void setTypeLoadingPage(int typeLoadingPage) {
        typeLoadPage = typeLoadingPage;
    }

    public void setVisibilityDeleteButton(boolean visibility) { // показывать кнопку удаления
        visibilityDeleteButton = visibility;
    }

    public void deleteItem(int position) { //удалить элемент из адаптера
        photosPicture.remove(position);
        notifyItemRemoved(position);
    }

    public void setPageSettings(int currentPage, int pagesAmount) { //установить настройи загрузки(для бесконечной загрузки)
        this.currentPage = currentPage;
        this.pagesAmount = pagesAmount;
    }

    public void throwOffData() { //очищаем адаптер
        photosPicture.clear();
    }

    @Override
    public int getItemViewType(int position) { // разделяем элементы на два типа(карточка и заглавный секции)
        return (photosPicture.get(position).getUrlText() == null)
                ? VIEW_TYPE_TITLE
                : VIEW_TYPE_PICTURE_CARD;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return (viewType == VIEW_TYPE_TITLE)
                ? new TitleViewHolder(layoutInflater.inflate(R.layout.item_title_picture, viewGroup, false))
                : new PictureViewHolder(layoutInflater.inflate(R.layout.item_picture, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        PhotoInfo photoInfo = photosPicture.get(position);
        switch (viewHolder.getItemViewType()) {
            case VIEW_TYPE_TITLE:
                TitleViewHolder titleViewHolder = (TitleViewHolder) viewHolder;
                titleViewHolder.titlePictureSection.setText(photoInfo.getRequestText().toUpperCase());
                break;
            case VIEW_TYPE_PICTURE_CARD:
                PictureViewHolder pictureViewHolder = (PictureViewHolder) viewHolder;
                Glide.with(pictureViewHolder.picture)
                        .load(photoInfo.getUrlText())
                        .into(pictureViewHolder.picture);
                pictureViewHolder.requestText.setText((typeLoadPage != PAGE_MAP_PIC)
                        ? photoInfo.getRequestText()
                        : "");
                pictureViewHolder.photoInfo = photoInfo;
                break;
        }
        if (borderDownloader != null)
            if (position == (photosPicture.size() - 3))  //Скачивание следующей страницы данных при достижении 2 элемента в конце списка
                if ((currentPage < pagesAmount) && isInternetConnectionEnable(context)) {
                    borderDownloader.loadNextPage(currentPage + 1, typeLoadPage);
                }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        if (holder.getItemViewType() == VIEW_TYPE_PICTURE_CARD) {
            ((PictureViewHolder) holder).disposeViews();
        }
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return photosPicture.size();
    }

    class PictureViewHolder extends RecyclerView.ViewHolder { // Холдер для карточки с картинкой

        private final ImageView picture;
        private final TextView requestText;
        private final ImageButton deleteButton;
        private final Disposable cardViewDis;
        private final Disposable deleteButtonDis;
        private PhotoInfo photoInfo;

        PictureViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewDis = RxView.clicks(itemView)
                    .subscribe(unit -> activityControl.openLinkItem(photoInfo));
            deleteButton = itemView.findViewById(R.id.delete_button);
            deleteButton.setVisibility((visibilityDeleteButton)
                    ? View.VISIBLE
                    : View.GONE);
            deleteButtonDis = RxView.clicks(deleteButton)
                    .filter(unit -> deleteButton.getVisibility() == View.VISIBLE)
                    .map(unit -> getAdapterPosition())
                    .subscribe(position -> {
                        appDatabase.favoritePhotoDao().setPhotoNotFavorite(new FavoritePhoto(photosPicture.get(position)));
                        deleteItem(position);
                    });
            //deleteButton.setOnClickListener(this);

            picture = itemView.findViewById(R.id.photo_from_camera);
            requestText = itemView.findViewById(R.id.request_text);
            //itemView.setOnClickListener(this);
        }

        void disposeViews() {
            cardViewDis.dispose();
            deleteButtonDis.dispose();
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
