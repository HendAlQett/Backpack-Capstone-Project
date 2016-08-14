package com.hend.backpack.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.hend.backpack.R;
import com.hend.backpack.adapters.LandmarkRecyclerViewAdapter;
import com.hend.backpack.apis.RestClient;
import com.hend.backpack.models.Landmark;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = MainActivity.class.getSimpleName();
    private GridLayoutManager gridLayout;
    LandmarkRecyclerViewAdapter adapter;
    List<Landmark> landmarksList;
    RecyclerView landmarkRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridLayout = new GridLayoutManager(MainActivity.this, 2);
        landmarkRecycler = (RecyclerView) findViewById(R.id.recyclerView);
        landmarkRecycler.setHasFixedSize(true);
        landmarkRecycler.setLayoutManager(gridLayout);
        landmarksList = new ArrayList<>();
//        adapter = new LandmarkRecyclerViewAdapter(MainActivity.this, landmarksList);
//        landmarkRecycler.setAdapter(adapter);
        RestClient.get().requestLandmarks(new Callback<List<Landmark>>() {
            @Override
            public void success(List<Landmark> landmarks, Response response) {
                landmarksList.clear();
                landmarksList=landmarks;
                adapter = new LandmarkRecyclerViewAdapter(MainActivity.this, landmarksList);
                landmarkRecycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                for (Landmark landmark : landmarks) {
                    Log.d(LOG_TAG, landmark.getName_en());
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
