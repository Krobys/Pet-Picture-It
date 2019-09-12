package com.akrivonos.app_standart_java;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.akrivonos.app_standart_java.fragments.FavoritesFragment;
import com.akrivonos.app_standart_java.fragments.GalleryFragment;
import com.akrivonos.app_standart_java.fragments.HistoryFragment;
import com.akrivonos.app_standart_java.fragments.LinkContentFragment;
import com.akrivonos.app_standart_java.fragments.MapSearch;
import com.akrivonos.app_standart_java.fragments.SearchPictureFragment;
import com.akrivonos.app_standart_java.fragments.SettingsFragment;
import com.akrivonos.app_standart_java.listeners.MapCoordinatesPhotoListener;
import com.akrivonos.app_standart_java.listeners.OnResultCoordinatesPictureListener;
import com.akrivonos.app_standart_java.listeners.OpenListItemLinkListener;
import com.akrivonos.app_standart_java.models.PhotoInfo;
import com.akrivonos.app_standart_java.utils.PreferenceUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import static com.akrivonos.app_standart_java.constants.TagsFragments.FAVORITES_FRAGMENT;
import static com.akrivonos.app_standart_java.constants.TagsFragments.GALLERY_FRAGMENT;
import static com.akrivonos.app_standart_java.constants.TagsFragments.HISTORY_FRAGMENT;
import static com.akrivonos.app_standart_java.constants.TagsFragments.MAP_SEARCH_FRAGMENT;
import static com.akrivonos.app_standart_java.constants.TagsFragments.SEARCH_PICTURE_FRAGMENT;
import static com.akrivonos.app_standart_java.constants.TagsFragments.SETTINGS_FRAGMENT;
import static com.akrivonos.app_standart_java.constants.Values.BUNDLE_PHOTO_INFO;
import static com.akrivonos.app_standart_java.constants.Values.MY_MAP_PERMISSION_CODE;

public class MainActivity extends AppCompatActivity implements OpenListItemLinkListener,
        MapCoordinatesPhotoListener{

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

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

        ((TextView)header.findViewById(R.id.name_user)).setText(PreferenceUtils.getCurrentUserName(this));
        setUpDefaultPage();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private void setUpToggleDrawer(){
        toggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }
    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        String TAG_FRAGMENT = SEARCH_PICTURE_FRAGMENT;
        switch (menuItem.getItemId()) {
            case R.id.search_pictures:
                setUpToggleDrawer();
                fragment = new SearchPictureFragment();
                //fragmentClass = SearchPictureFragment.class;
                TAG_FRAGMENT = SEARCH_PICTURE_FRAGMENT;
                break;
            case R.id.history:
                fragment = new HistoryFragment();
               // fragmentClass = HistoryFragment.class;
                TAG_FRAGMENT = HISTORY_FRAGMENT;
                break;
            case R.id.favorire_pick:
                fragment = new FavoritesFragment();
                //fragmentClass = FavoritesFragment.class;
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
                //fragmentClass = SettingsFragment.class;
                TAG_FRAGMENT = SETTINGS_FRAGMENT;
                break;
            default:
                fragment = new SearchPictureFragment();
                //fragmentClass = SearchPictureFragment.class;
        }
        if(fragment != null){

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment, TAG_FRAGMENT).addToBackStack(null).commit();

            menuItem.setChecked(true);

            setTitle(menuItem.getTitle());
        }
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
        if(getIntent().hasExtra(SETTINGS_FRAGMENT)){
            fragmentManager.beginTransaction().add(R.id.flContent, new SettingsFragment(), SETTINGS_FRAGMENT).commit();
            getIntent().removeExtra(SETTINGS_FRAGMENT);
        }else{
            Log.d("test", "not has extra");
            fragmentManager.beginTransaction().add(R.id.flContent, new SearchPictureFragment(), SEARCH_PICTURE_FRAGMENT).commit();
        }

    }

    private Fragment getLastFragment(){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        return fragments.get(fragments.size() - 1);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        fragLinkContent.setArguments(bundle);
        View containerContentMore = getLastFragment().getView().findViewById(R.id.details_picture_container);
        boolean mDualPane = (containerContentMore != null) && (containerContentMore.getVisibility() == View.VISIBLE);
        if (mDualPane) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.details_picture_container, fragLinkContent).commit();
        } else {

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
        searchPictureFragment = (SearchPictureFragment) fragmentManager.findFragmentByTag(SEARCH_PICTURE_FRAGMENT);
        if(searchPictureFragment != null){
            fragmentManager.beginTransaction().replace(R.id.flContent, searchPictureFragment, SEARCH_PICTURE_FRAGMENT).commit();
            ((OnResultCoordinatesPictureListener) searchPictureFragment).startCoordinatesSearch(latLng);
        }else {
            searchPictureFragment = new SearchPictureFragment();
            fragmentManager.beginTransaction().replace(R.id.flContent, searchPictureFragment, SEARCH_PICTURE_FRAGMENT).commit();
            ((OnResultCoordinatesPictureListener) searchPictureFragment).startCoordinatesSearch(latLng);
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
}
