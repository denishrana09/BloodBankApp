package com.example.denish.bloodbank;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class SearchActivity extends BaseActivity {

    private android.widget.SearchView mSearchView;
    private static final String TAG = "SearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        activeToolbar(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (android.widget.SearchView)menu.findItem(R.id.app_bar_search).getActionView();
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        mSearchView.setSearchableInfo(searchableInfo);

        mSearchView.setIconified(false);

        mSearchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.length() > 0 &&
                        query.equalsIgnoreCase("O+") || query.equalsIgnoreCase("O-")
                        || query.equalsIgnoreCase("A-") || query.equalsIgnoreCase("A+")
                        || query.equalsIgnoreCase("B+") || query.equalsIgnoreCase("B-")
                        || query.equalsIgnoreCase("AB-") || query.equalsIgnoreCase("AB+")){
                    Log.d(TAG, "onQueryTextSubmit: called");
                    query = query.toUpperCase();
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    sharedPreferences.edit().putString("query",query).apply();
                    mSearchView.clearFocus();
                    finish();
                    return true;
                }else {
                    Toast.makeText(SearchActivity.this, "Type Valid Blood Group. Ex. O+,B+", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mSearchView.setOnCloseListener(new android.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                finish();
                return false;
            }
        });

        return true;
    }
}
