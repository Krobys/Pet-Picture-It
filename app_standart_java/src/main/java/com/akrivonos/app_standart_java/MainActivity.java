package com.akrivonos.app_standart_java;



import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.akrivonos.app_standart_java.fragments.FavoritesFragment;
import com.akrivonos.app_standart_java.fragments.HistoryFragment;
import com.akrivonos.app_standart_java.fragments.LinkContentFragment;
import com.akrivonos.app_standart_java.fragments.SearchPictureFragment;
import com.akrivonos.app_standart_java.fragments.SettingsFragment;
import com.akrivonos.app_standart_java.listeners.OpenListItemLinkListener;
import com.akrivonos.app_standart_java.models.PhotoInfo;

import static com.akrivonos.app_standart_java.constants.Values.BUNDLE_PHOTO_INFO;
import static com.akrivonos.app_standart_java.constants.Values.CURRENT_FRAGMENT;

public class MainActivity extends AppCompatActivity implements OpenListItemLinkListener {
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private boolean mDualPane;

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

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;

        switch (menuItem.getItemId()) {
            case R.id.search_pictures:
                fragmentClass = SearchPictureFragment.class;
                break;
            case R.id.history:
                fragmentClass = HistoryFragment.class;
                break;
            case R.id.favorire_pick:
                fragmentClass = FavoritesFragment.class;
                break;
//            case R.id.find_on_map:
//                fragmentClass = ThirdFragment.class;
//                break;
//            case R.id.gallery:
//                fragmentClass = ThirdFragment.class;
//                break;
            case R.id.settings:
                fragmentClass = SettingsFragment.class;
                break;
            default:
                fragmentClass = SearchPictureFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment, CURRENT_FRAGMENT).commit();

        menuItem.setChecked(true);

        setTitle(menuItem.getTitle());

        drawer.closeDrawers();
    }

    private void setUpDefaultPage() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new SearchPictureFragment(), CURRENT_FRAGMENT).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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
        if (mDualPane) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(CURRENT_FRAGMENT);
            FrameLayout container;
            if (fragment != null) {
                View view = fragment.getView();
                if (view != null) {
                    container = view.findViewById(R.id.details_picture_container);
                    if (container != null) {

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.details_picture_container, fragLinkContent).commit();
                    }
                }
            }
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragLinkContent).commit();
        }
    }
}
