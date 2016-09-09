package com.mrkevinthomas.kcards;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.mrkevinthomas.kcards.models.Deck;
import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.List;

public class DeckCreationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private DeckListAdapter deckListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeckDialog();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        deckListAdapter = new DeckListAdapter();
        recyclerView.setAdapter(deckListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadDecks();
    }

    private void loadDecks() {
        SQLite.select()
                .from(Deck.class)
                .async()
                .queryResultCallback(new QueryTransaction.QueryResultCallback<Deck>() {
                    @Override
                    public void onQueryResult(QueryTransaction transaction, @NonNull CursorResult<Deck> tResult) {
                        // called when query returns on UI thread
                        List<Deck> decks = tResult.toListClose();
                        deckListAdapter.setDeckList(decks);
                    }
                }).execute();
    }

    private void showDeckDialog() {
        View diaogView = getLayoutInflater().inflate(R.layout.deck_edit_dialog, null);
        final EditText nameInput = (EditText) diaogView.findViewById(R.id.deck_name_input);
        final EditText descriptionInput = (EditText) diaogView.findViewById(R.id.deck_description_input);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Deck of Cards");
        builder.setView(diaogView);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = nameInput.getText().toString();
                String description = descriptionInput.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    Deck deck = new Deck(name, description);
                    deckListAdapter.addDeck(deck);
                    deck.save();

                    Bundle bundle = new Bundle();
                    bundle.putString("word", name);
                    KcardsApp.logAnalyticsEvent("add_word", bundle);
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.setCancelable(true);
        builder.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.card_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
