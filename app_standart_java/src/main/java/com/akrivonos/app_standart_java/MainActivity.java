package com.akrivonos.app_standart_java;


import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.room.Room;

import com.akrivonos.app_standart_java.fragments.FavoritesFragment;
import com.akrivonos.app_standart_java.fragments.GalleryFragment;
import com.akrivonos.app_standart_java.fragments.HistoryFragment;
import com.akrivonos.app_standart_java.fragments.LinkContentFragment;
import com.akrivonos.app_standart_java.fragments.MapSearch;
import com.akrivonos.app_standart_java.fragments.ScheduledPictureFragment;
import com.akrivonos.app_standart_java.fragments.SearchPictureFragment;
import com.akrivonos.app_standart_java.fragments.SettingsFragment;
import com.akrivonos.app_standart_java.listeners.MapCoordinatesPhotoListener;
import com.akrivonos.app_standart_java.listeners.OpenListItemLinkListener;
import com.akrivonos.app_standart_java.models.PhotoInfo;
import com.akrivonos.app_standart_java.receivers.BatteryChangeReceiver;
import com.akrivonos.app_standart_java.room.RoomAppDatabase;
import com.akrivonos.app_standart_java.utils.PreferenceUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import static com.akrivonos.app_standart_java.constants.Values.ARGUMENT_EXPANABLE_FRAG;
import static com.akrivonos.app_standart_java.constants.Values.ARGUMENT_SINGLE_FRAG;
import static com.akrivonos.app_standart_java.constants.Values.BUNDLE_PHOTO_INFO;
import static com.akrivonos.app_standart_java.constants.Values.DATABASE_NAME;
import static com.akrivonos.app_standart_java.constants.Values.EXPANDABLE_VALUE;
import static com.akrivonos.app_standart_java.constants.Values.LATTITUDE_LONGITUDE;
import static com.akrivonos.app_standart_java.constants.Values.MY_MAP_PERMISSION_CODE;
import static com.akrivonos.app_standart_java.constants.Values.TAG_FRAGMENT_ACTIVITY_START_MODE;
import static com.akrivonos.app_standart_java.constants.Values.TYPE_FRAG;
import static com.akrivonos.app_standart_java.fragments.FavoritesFragment.FAVORITES_FRAGMENT;
import static com.akrivonos.app_standart_java.fragments.GalleryFragment.GALLERY_FRAGMENT;
import static com.akrivonos.app_standart_java.fragments.HistoryFragment.HISTORY_FRAGMENT;
import static com.akrivonos.app_standart_java.fragments.MapSearch.MAP_SEARCH_FRAGMENT;
import static com.akrivonos.app_standart_java.fragments.ScheduledPictureFragment.SCHEDULE_FRAGMENT;
import static com.akrivonos.app_standart_java.fragments.SearchPictureFragment.SEARCH_PICTURE_FRAGMENT;
import static com.akrivonos.app_standart_java.fragments.SettingsFragment.SETTINGS_FRAGMENT;

public class MainActivity extends AppCompatActivity implements OpenListItemLinkListener,
        MapCoordinatesPhotoListener{

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private boolean isExpandable;
    private RoomAppDatabase roomAppDatabase;
    private BatteryChangeReceiver batteryChangeReceiver;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public boolean isRecreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nvView);
        setupDrawerContent(navigationView);
        View header = navigationView.getHeaderView(0);
        setUpToggleDrawer();
        ((TextView)header.findViewById(R.id.name_user)).setText(PreferenceUtils.getCurrentUserName(this));
        if (savedInstanceState != null)
            isRecreated = savedInstanceState.getBoolean("test", false);
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryChangeReceiver = new BatteryChangeReceiver();
        registerReceiver(batteryChangeReceiver, batteryLevelFilter);
    }

    @Override
    protected void onResume() {
        isViewExpandable();
        setUpDefaultPage();
        super.onResume();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });
    }

    private void setUpToggleDrawer(){
        toggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }
    private void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = new SearchPictureFragment();
        String TAG_FRAGMENT = SEARCH_PICTURE_FRAGMENT;
        switch (menuItem.getItemId()) {
            case R.id.search_pictures:
                fragment = new SearchPictureFragment();
                TAG_FRAGMENT = SEARCH_PICTURE_FRAGMENT;
                break;
            case R.id.history:
                fragment = new HistoryFragment();
                TAG_FRAGMENT = HISTORY_FRAGMENT;
                break;
            case R.id.favorire_pick:
                fragment = new FavoritesFragment();
                TAG_FRAGMENT = FAVORITES_FRAGMENT;
                break;
            case R.id.find_on_map:
                if(checkPermissionsMap()){
                   startMapFragment();
                }
                break;
            case R.id.gallery:
                fragment = new GalleryFragment();
                TAG_FRAGMENT = GALLERY_FRAGMENT;
                break;
            case R.id.settings:
                fragment = new SettingsFragment();
                TAG_FRAGMENT = SETTINGS_FRAGMENT;
                break;
            case R.id.scheduled_pictures:
                fragment = new ScheduledPictureFragment();
                TAG_FRAGMENT = SCHEDULE_FRAGMENT;
                break;
        }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment, TAG_FRAGMENT).addToBackStack(null).commit();
            menuItem.setChecked(true);

        drawer.closeDrawers();
    }

    private void startMapFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        MapSearch mapFragment = new MapSearch();
        mapFragment.setMapListener(this);
        fragmentManager.beginTransaction().replace(R.id.flContent, mapFragment, MAP_SEARCH_FRAGMENT).addToBackStack(null).commit();
    }

    private void setUpDefaultPage() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Intent intent = getIntent();
        if (intent.hasExtra(TAG_FRAGMENT_ACTIVITY_START_MODE)) {
            if (intent.getStringExtra(TAG_FRAGMENT_ACTIVITY_START_MODE).equals(SCHEDULE_FRAGMENT)) {
                fragmentManager.beginTransaction().replace(R.id.flContent, new ScheduledPictureFragment(), SETTINGS_FRAGMENT).commit();
            }
        }else{
            if (isRecreated) {
                fragmentManager.beginTransaction().replace(R.id.flContent, new SettingsFragment(), SETTINGS_FRAGMENT).commit();
                isRecreated = false;
            } else {
                fragmentManager.beginTransaction().replace(R.id.flContent, new SearchPictureFragment(), SEARCH_PICTURE_FRAGMENT).commit();
            }
        }
    }

    private void isViewExpandable(){
        Intent intent = getIntent();
        boolean exp = intent.getBooleanExtra(EXPANDABLE_VALUE, true);
        if (intent.hasExtra(EXPANDABLE_VALUE)) {
            isExpandable = intent.getBooleanExtra(EXPANDABLE_VALUE, false);
            intent.removeExtra(EXPANDABLE_VALUE);
        } else {
            Fragment fragment = getLastFragment();
            if (fragment != null) {
                View isExpandableView = getLastFragment().getView();
                if (isExpandableView != null) {
                    View containerContentMore = isExpandableView.findViewById(R.id.details_picture_container);
                    isExpandable = (containerContentMore != null);
                }
            }
        }
    }

    private Fragment getLastFragment(){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.size() > 0)
        return fragments.get(fragments.size() - 1);
        else
            return null;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawer.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void openLinkItem(PhotoInfo photoInfo) {
        Fragment fragLinkContent = new LinkContentFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_PHOTO_INFO, photoInfo);
            if (isExpandable) {
                bundle.putInt(TYPE_FRAG, ARGUMENT_EXPANABLE_FRAG);
                            fragLinkContent.setArguments(bundle);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.details_picture_container, fragLinkContent).commit();
            } else {
                bundle.putInt(TYPE_FRAG, ARGUMENT_SINGLE_FRAG);
                fragLinkContent.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragLinkContent).addToBackStack(null).commit();
            }
    }

    private boolean checkPermissionsMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, MY_MAP_PERMISSION_CODE);
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void setResultCoordinatesPic(LatLng latLng) {
        SearchPictureFragment searchPictureFragment;
        clearStackFragments();
        FragmentManager fragmentManager = getSupportFragmentManager();
        try {
            searchPictureFragment = SearchPictureFragment.class.newInstance();
            Bundle bundle = new Bundle();
            bundle.putParcelable(LATTITUDE_LONGITUDE, latLng);
            searchPictureFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.flContent, searchPictureFragment, SEARCH_PICTURE_FRAGMENT).commit();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_MAP_PERMISSION_CODE) {
            for (int perm : grantResults) {
                if (perm != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            startMapFragment();
        }
    }

    private void clearStackFragments(){
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    public RoomAppDatabase getDatabase(){
       if(roomAppDatabase == null){
           roomAppDatabase = Room.databaseBuilder(this, RoomAppDatabase.class, DATABASE_NAME)
                   .allowMainThreadQueries()
                   .fallbackToDestructiveMigration()
                   .build();
       }
       return roomAppDatabase;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(batteryChangeReceiver);
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("recreate", isRecreated);
        outState.putBoolean("expandableV", isExpandable);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        isRecreated = savedInstanceState.getBoolean("recreate", false);
        isExpandable = savedInstanceState.getBoolean("expandableV", false);
        super.onRestoreInstanceState(savedInstanceState);
    }
}
