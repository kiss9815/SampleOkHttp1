package com.example.tacademy.sampleokhttp1;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import okhttp3.Request;

public class MainActivity extends AppCompatActivity {

    EditText keywordView;
    ListView listView;
    MovieAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        keywordView = (EditText)findViewById(R.id.edit_keyword);
        listView = (ListView)findViewById(R.id.listView);
        mAdapter = new MovieAdapter();
        listView.setAdapter(mAdapter);

        Button btn = (Button)findViewById(R.id.btn_search);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = keywordView.getText().toString();
                if (!TextUtils.isEmpty(keyword)) {
                    try {
                        NetworkManager.getInstance().getNaverMovie(MainActivity.this,
                                keyword, 1, 50, new NetworkManager.OnResultListener<NaverMovies>() {
                                    @Override
                                    public void onSuccess(Request request, NaverMovies result) {
                                        mAdapter.addAll(result.items);
                                    }

                                    @Override
                                    public void onFailure(Request request, int code, Throwable cause) {
                                        Toast.makeText(MainActivity.this, "fail", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
