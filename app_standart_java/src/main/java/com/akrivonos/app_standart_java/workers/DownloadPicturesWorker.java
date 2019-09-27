package com.akrivonos.app_standart_java.workers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.room.Room;
import androidx.work.WorkerParameters;

import com.akrivonos.app_standart_java.MainActivity;
import com.akrivonos.app_standart_java.R;
import com.akrivonos.app_standart_java.models.PhotoInfo;
import com.akrivonos.app_standart_java.retrofit.RetrofitSearchDownload;
import com.akrivonos.app_standart_java.room.RoomAppDatabase;
import com.akrivonos.app_standart_java.room.ScheduledPictures;
import com.akrivonos.app_standart_java.utils.PreferenceUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.akrivonos.app_standart_java.constants.Values.DATABASE_NAME;
import static com.akrivonos.app_standart_java.constants.Values.REQUEST_TEXT_SCHEDULED;
import static com.akrivonos.app_standart_java.constants.Values.TAG_FRAGMENT_ACTIVITY_START_MODE;
import static com.akrivonos.app_standart_java.fragments.ScheduledPictureFragment.SCHEDULE_FRAGMENT;
import static com.akrivonos.app_standart_java.models.PostDownloadPicturePack.TYPE_DOWNLOAD_CHEDULE;

public class DownloadPicturesWorker extends androidx.work.Worker {

    private final RoomAppDatabase roomAppDatabase;
    private Disposable disposableShedule;
    private final WeakReference<Context> contextWeakReference;
    private String requestText;
    private String userName;

    public DownloadPicturesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        contextWeakReference = new WeakReference<>(context);
        roomAppDatabase = Room.databaseBuilder(context, RoomAppDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        Observer<ArrayList<PhotoInfo>> photosScheduledObserver = new Observer<ArrayList<PhotoInfo>>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposableShedule = d;
            }

            @Override
            public void onNext(ArrayList<PhotoInfo> photoInfos) {
                if (photoInfos != null) {
                    roomAppDatabase.scheduledPicturesDao().clearTable();
                    for (PhotoInfo photoInfo : photoInfos) {
                        roomAppDatabase.scheduledPicturesDao().addToSheduledTable(new ScheduledPictures(photoInfo));
                    }

                    Context context = contextWeakReference.get();
                    if (context != null) {
                        Bitmap bitmap = null;
                        try {
                            bitmap = BitmapFactory.decodeStream(new URL(photoInfos.get(0)
                                    .getUrlText())
                                    .openConnection()
                                    .getInputStream());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            Intent intent = new Intent(context, MainActivity.class).putExtra(TAG_FRAGMENT_ACTIVITY_START_MODE, SCHEDULE_FRAGMENT);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel29")
                                    .setSmallIcon(R.drawable.ic_turned_in_black)
                                    .setLargeIcon(bitmap)
                                    .setContentTitle("Downloading Finished")
                                    .setContentText("Downloaded " + photoInfos.size() + " photos by schedule task")
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true);
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                            notificationManager.notify(1, builder.build());
                        }
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

        RetrofitSearchDownload.getInstance().setObserverScheduled(photosScheduledObserver);
        PreferenceUtils.setScheduledTaskId(context, getId().toString());
    }

    @NonNull
    @Override
    public Result doWork() {
        requestText = getInputData().getString(REQUEST_TEXT_SCHEDULED);
        userName = PreferenceUtils.getCurrentUserName(getApplicationContext());
        RetrofitSearchDownload.getInstance().startDownloadPictures(requestText, userName, 1, TYPE_DOWNLOAD_CHEDULE);
        return null;
    }

    @Override
    public void onStopped() {
        disposableShedule.dispose();
        super.onStopped();
    }
}
