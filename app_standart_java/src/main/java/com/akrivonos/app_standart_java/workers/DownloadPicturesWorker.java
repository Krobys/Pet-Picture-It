package com.akrivonos.app_standart_java.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.akrivonos.app_standart_java.models.PhotoInfo;
import com.akrivonos.app_standart_java.retrofit.RetrofitSearchDownload;
import com.akrivonos.app_standart_java.room.RoomAppDatabase;
import com.akrivonos.app_standart_java.room.ScheduledPictures;
import com.akrivonos.app_standart_java.utils.PreferenceUtils;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;

import static com.akrivonos.app_standart_java.constants.Values.DATABASE_NAME;
import static com.akrivonos.app_standart_java.constants.Values.REQUEST_TEXT_SCHEDULED;
import static com.akrivonos.app_standart_java.models.PostDownloadPicturePack.TYPE_DOWNLOAD_CHEDULE;

public class DownloadPicturesWorker extends Worker {

    private RoomAppDatabase roomAppDatabase;
    private Disposable disposableShedule;

    private io.reactivex.Observer<ArrayList<PhotoInfo>> photosSheduledObserver = new io.reactivex.Observer<ArrayList<PhotoInfo>>() {
        @Override
        public void onSubscribe(Disposable d) {
            disposableShedule = d;
        }

        @Override
        public void onNext(ArrayList<PhotoInfo> photoInfos) {
            if (photoInfos != null) {
                roomAppDatabase.scheduledPicturesDao().clearTable();
                Log.d("test", "Next schedule work");
                for (PhotoInfo photoInfo : photoInfos) {
                    roomAppDatabase.scheduledPicturesDao().addToSheduledTable(new ScheduledPictures(photoInfo));
                }
            }
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {

        }
    };

    public DownloadPicturesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Log.d("test", "DownloadPicturesWorker: initiated");
        roomAppDatabase = Room.databaseBuilder(context, RoomAppDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        RetrofitSearchDownload.getInstance().setObserverSheduled(photosSheduledObserver);
    }

    @NonNull
    @Override
    public Result doWork() {
        String requestText = getInputData().getString(REQUEST_TEXT_SCHEDULED);
        String userName = PreferenceUtils.getCurrentUserName(getApplicationContext());
        RetrofitSearchDownload.getInstance().startDownloadPictures(requestText, userName, 1, TYPE_DOWNLOAD_CHEDULE);
        return null;
    }

    @Override
    public void onStopped() {
        disposableShedule.dispose();
        super.onStopped();
    }
}
