package net.iturrioz.lolgoldefficiency;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.iturrioz.lolgoldefficiency.data.LolRequest;
import net.iturrioz.lolgoldefficiency.data.StatValue;
import net.iturrioz.lolgoldefficiency.data.domain.Rune;

import org.json.simple.JSONObject;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    final static String VERSION_KEY = "LOL_VERSION";
    final static String VERSION_LIST_KEY = "LOL_VERSION_LIST";
    final static Gson GSON = new Gson();

    private String pvpNetKey = getResources().getString(R.string.pvp_net_key);

    private SharedPreferences sharedPref;

    private StatValue stats;
    private Map<Rune.RuneType, List<Rune>> runeMap;
    private List<String> versions;
    private String version;

    FloatingActionButton fabDownload;
    NavigationView navigationView;

    private View progressBar;
    private TextView progressText;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
            Default code
         */
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabDownload = (FloatingActionButton) findViewById(R.id.fab_refresh);
        fabDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Refreshing version list", Snackbar.LENGTH_LONG).show();
                fabDownload.setVisibility(View.GONE);
                getListOfVersions();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (version != null && !version.isEmpty()) {
                    ((TextView) drawerView.findViewById(R.id.nav_header_version)).setText(version);
                }

            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*
            Custom code
         */

        progressBar = findViewById(R.id.progressBar);
        progressText = (TextView) findViewById(R.id.progressText);
        listView = (ListView) findViewById(R.id.listView);

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        version = sharedPref.getString(VERSION_KEY, "");
        versions = Arrays.asList(sharedPref.getString(VERSION_LIST_KEY, "").split(","));
        if (version.isEmpty()) {
            downloadDataTask().execute();
        } else {
            loadStoredData();
            updateListView();
        }
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.action_get_versions) {
//           // menu
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_stats) {
            updateStatsListView();
        } else if (id == R.id.nav_mark) {
            updateListView(Rune.RuneType.Mark, R.string.nav_marks, 0);
        } else if (id == R.id.nav_seal) {
            updateListView(Rune.RuneType.Seal, R.string.nav_seals, 1);
        } else if (id == R.id.nav_glyph) {
            updateListView(Rune.RuneType.Glyph, R.string.nav_glyphs, 2);
        } else if (id == R.id.nav_quintessence) {
            updateListView(Rune.RuneType.Quintessence, R.string.nav_quintessences, 3);
        } else if (id == R.id.nav_versions) {
            updateVersionsListView();
            fabDownload.setVisibility(View.VISIBLE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /*
        Custom code
     */
    AsyncTask<String, String, Void> downloadDataTask() {
        return new AsyncTask<String, String, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                listView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                progressText.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(String... params) {

                if (params.length == 0) {
                    publishProgress("Getting version");
                    versions = LolRequest.getVersionList(pvpNetKey);
                    persistVersionsList(versions);
                    version = versions.get(0);
                } else {
                    version = params[0];
                }
                SharedPreferences.Editor edit = sharedPref.edit();
                edit.putString(VERSION_KEY, version);

                loadStoredData();

                if (runeMap.isEmpty() || stats == null) {
                    publishProgress("Getting items");
                    JSONObject jsonObject = LolRequest.getItems(version);

                    publishProgress("Creating stats");
                    stats = StatValue.createStats((JSONObject) jsonObject.get("data"));
                    edit.putString(statsPreferencesKey(), GSON.toJson(stats));
                    for (final StatValue.StatType statType : StatValue.StatType.values()) {
                        edit.putInt(statType.name(), (int) stats.getFieldValue(statType));
                    }

                    publishProgress("Getting runes");
                    JSONObject runes = LolRequest.getRunes(version);

                    publishProgress("Reading runes");
                    runeMap = Rune.readRunes(runes, stats);

                    edit.putString(runesPreferencesKey(), GSON.toJson(runeMap));
                    edit.apply();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void runes) {
                super.onPostExecute(runes);

                progressBar.setVisibility(View.GONE);
                progressText.setVisibility(View.GONE);

                listView.setVisibility(View.VISIBLE);
                updateListView();
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                progressText.setVisibility(View.VISIBLE);
                progressText.setText(values[0]);
            }
        };
    }

    private String runesPreferencesKey() {
        return "Runes-" + version;
    }

    @NonNull
    private String statsPreferencesKey() {
        return "Stats-" + version;
    }

    private void getListOfVersions() {
        new AsyncTask<Void, List<String>, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... params) {
                return LolRequest.getVersionList(pvpNetKey);
            }

            @Override
            protected void onPostExecute(List<String> strings) {
                super.onPostExecute(strings);
                versions = strings;

                persistVersionsList(strings);

                View currentFocus = getCurrentFocus();
                if (currentFocus != null) {
                    Snackbar.make(currentFocus, "List of versions updated", Snackbar.LENGTH_SHORT).show();
                }

                updateVersionsListView();
            }
        }.execute();
    }

    private void persistVersionsList(List<String> strings) {
        SharedPreferences.Editor edit = sharedPref.edit();

        StringBuilder builder = new StringBuilder();
        for (String version : strings) {
            if (builder.length() > 0) {
                builder.append(",");
            }
            builder.append(version);
        }
        edit.putString(VERSION_LIST_KEY, builder.toString());
        edit.apply();
    }

    private void loadStoredData() {
        final String runesPrefString = sharedPref.getString(runesPreferencesKey(), "");
        final String statsPrefString = sharedPref.getString(statsPreferencesKey(), "");
        if (runesPrefString.isEmpty() || statsPrefString.isEmpty()) {
            runeMap = new HashMap<>();
        } else {
            Type listType = new TypeToken<Map<Rune.RuneType, List<Rune>>>() {}.getType();
            runeMap = GSON.fromJson(runesPrefString, listType);
            stats = GSON.fromJson(statsPrefString, StatValue.class);
        }
    }

    private void updateListView() {
        SubMenu subMenu = navigationView.getMenu().getItem(1).getSubMenu();
        if (subMenu.getItem(0).isChecked()) {
            updateListView(Rune.RuneType.Mark, R.string.nav_marks, 0);
        } else if (subMenu.getItem(1).isChecked()) {
            updateListView(Rune.RuneType.Seal, R.string.nav_seals, 1);
        } else if (subMenu.getItem(2).isChecked()) {
            updateListView(Rune.RuneType.Glyph, R.string.nav_glyphs, 2);
        } else if (subMenu.getItem(3).isChecked()) {
            updateListView(Rune.RuneType.Quintessence, R.string.nav_quintessences, 3);
        } else {
            updateListView(Rune.RuneType.Mark, R.string.nav_marks, 0);
        }
    }

    private void updateListView(final Rune.RuneType runeType, final int stringId, final int menuPosition) {
        if (runeMap == null || runeMap.size() == 0) {
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                Snackbar.make(currentFocus, R.string.error_no_runes, Snackbar.LENGTH_SHORT).show();
            }
        } else {
            final List<Rune> runes = runeMap.get(runeType);
            setTitle(stringId);
            SubMenu subMenu = navigationView.getMenu().getItem(1).getSubMenu();
            subMenu.getItem(0).setChecked(false);
            subMenu.getItem(1).setChecked(false);
            subMenu.getItem(2).setChecked(false);
            subMenu.getItem(3).setChecked(false);
            subMenu.getItem(menuPosition).setChecked(true);
            navigationView.getMenu().getItem(0).setChecked(false);

            final ArrayAdapter<Rune> adapter = new ArrayAdapter<Rune>(this, android.R.layout.simple_list_item_2, android.R.id.text1, runes) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);

                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                    Rune rune = runes.get(position);
                    text1.setText(rune.getName());
                    text2.setText(rune.getGold().toString());

                    return view;
                }
            };

            listView.setOnItemClickListener(null);
            listView.setAdapter(adapter);
            fabDownload.setVisibility(View.GONE);
        }
    }

    private void updateVersionsListView() {
        setTitle(R.string.nav_versions);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, versions) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView text1 = (TextView) view.findViewById(android.R.id.text1);

                text1.setText(versions.get(position));

                return view;
            }
        };

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                downloadDataTask().execute(versions.get(position));
            }
        });
    }

    private void updateStatsListView() {
        if (stats == null) {
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                Snackbar.make(currentFocus, R.string.error_no_stats, Snackbar.LENGTH_SHORT).show();
            }
        } else {
            setTitle(R.string.nav_stats);

            navigationView.getMenu().getItem(0).setChecked(true);
            SubMenu subMenu = navigationView.getMenu().getItem(1).getSubMenu();
            subMenu.getItem(0).setChecked(false);
            subMenu.getItem(1).setChecked(false);
            subMenu.getItem(2).setChecked(false);
            subMenu.getItem(3).setChecked(false);

            final ArrayAdapter<StatValue.StatType> adapter = new ArrayAdapter<StatValue.StatType>(this, android.R.layout.simple_list_item_2,
                    android.R.id.text1, StatValue.StatType.values()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);

                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                    StatValue.StatType statType = StatValue.StatType.values()[position];
                    double value = stats.getFieldValue(statType);
                    text1.setText(statType.name());
                    text2.setText(value > 0 ? String.format("%.1f", value) : "-");

                    return view;
                }
            };

            listView.setOnItemClickListener(null);
            listView.setAdapter(adapter);
            fabDownload.setVisibility(View.GONE);
        }
    }
}
