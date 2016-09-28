package com.mrkevinthomas.kcards;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.FrameLayout;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String ARG_DECK = "deck";
    public static final String ARG_POSITION = "position";
    public static final String ARG_READ_ONLY = "read_only";

    protected Toolbar toolbar;
    protected FloatingActionButton fab;
    protected DrawerLayout drawer;
    protected ActionBarDrawerToggle toggle;
    protected NavigationView navigationView;

    protected boolean shouldShowUpButton() {
        return false;
    }

    protected int getViewId() {
        return R.layout.recycler_view;
    }

    protected int getNavItemId() {
        return R.id.nav_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);

        // set the main content view in a frame layout
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        LayoutInflater.from(this).inflate(getViewId(), frameLayout, true);

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Analytics.logOptionsItemSelectedEvent(item);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // ignore clicks on the current nav item
        if (id != getNavItemId()) {
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, DeckManagementActivity.class));
            } else if (id == R.id.nav_practice) {
                // TODO
            } else if (id == R.id.nav_trending) {
                startActivity(new Intent(this, DeckViewActivity.class));
            } else if (id == R.id.nav_capture) {
                // TODO
            }
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
