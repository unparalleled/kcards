package com.mrkevinthomas.kcards;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.mrkevinthomas.kcards.deck_list.DeckListActivity;
import com.mrkevinthomas.kcards.deck_list.FollowedDeckListActivity;
import com.mrkevinthomas.kcards.deck_list.SharedDeckListActivity;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String ARG_DECK = "arg_deck";
    public static final String ARG_POSITION = "arg_position";
    public static final String ARG_READ_ONLY = "arg_read_only";

    public static final String KEY_IS_SWAPPED = "key_is_swapped";
    public static final String KEY_IS_HIDDEN = "key_is_hidden";

    public static final int REQUEST_DECK = 0;

    protected Toolbar toolbar;
    protected FloatingActionButton fab;
    protected DrawerLayout drawer;
    protected ActionBarDrawerToggle toggle;
    protected NavigationView navigationView;

    protected ViewGroup mainContentHolder;
    protected ProgressBar progressBar;

    /**
     * @return the resource id of the main layout to be inflated and attached to the main content holder
     */
    @LayoutRes
    protected int getViewId() {
        return R.layout.recycler_view;
    }

    /**
     * Used for checking the current nav item and ignoring clicks on it.
     * This can be ignored if {@link #shouldShowUpButton()} returns true as there is no nav drawer.
     *
     * @return id of the navigation item which corresponds to the current activity
     */
    protected int getNavItemId() {
        return R.id.nav_home;
    }

    /**
     * Determines whether or not to show the up button or hamburger menu.
     * If this returns false, then you MUST return a nav item id from {@link #getNavItemId()}.
     *
     * @return whether or not to show the up button instead of the hamburger menu
     */
    protected boolean shouldShowUpButton() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);

        // set the main content view in the holder layout
        mainContentHolder = (ViewGroup) findViewById(R.id.main_content_holder);
        LayoutInflater.from(this).inflate(getViewId(), mainContentHolder, true);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (shouldShowUpButton()) {
            toggle.setDrawerIndicatorEnabled(false);
            toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            // must be set after configuring the toggle
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // disable nav drawer too
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        navigationView.setCheckedItem(getNavItemId());
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Analytics.logOptionsItemSelectedEvent(item);

        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // ignore clicks on the current nav item
        if (id != getNavItemId()) {
            if (id == R.id.nav_home) {
                Intent intent = new Intent(this, DeckListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // start fresh from home
                startActivity(intent);
            } else if (id == R.id.nav_shared) {
                startActivity(new Intent(this, SharedDeckListActivity.class));
            } else if (id == R.id.nav_followed) {
                startActivity(new Intent(this, FollowedDeckListActivity.class));
            } else if (id == R.id.nav_dictionary) {
                // TODO
            }
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
