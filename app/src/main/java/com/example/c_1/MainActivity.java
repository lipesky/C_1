package com.example.c_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

    // The API object responsible for communication with http://tropicalfruitandveg.com/api/
    private final FruitsAPI fruitsAPI = new FruitsAPI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Intent intent = new Intent(getApplicationContext(), FruitDetails.class);
        intent.putExtra("tfvname", "banana");
        startActivity(intent);*/

        final RecyclerView list = findViewById(R.id.fruitsList);

        // Optimization for rendering
        list.setHasFixedSize(true);

        // Sets the swipe listener
        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.fruitListSwipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFruitList(
                        // Before Refresh
                        null,
                        // After refresh
                        new Callback<Void, Void>() {
                            @Override
                            public Void call(Void args) {
                                swipeRefreshLayout.setRefreshing(false);
                                return null;
                            }
                        });
            }
        });

        // Get Initial data
        final ConstraintLayout progress = findViewById(R.id.progress_group);
        refreshFruitList(
                // Before refresh
                new Callback<Void, Void>(){
                    @Override
                    public Void call(Void args) {
                        progress.setVisibility(View.VISIBLE);
                        return null;
                    }
                },
                // After refresh
                new Callback<Void, Void>(){
                    @Override
                    public Void call(Void args) {
                        progress.setVisibility(View.GONE);
                        list.setVisibility(View.VISIBLE);
                        return null;
                    }
                });
    }


    protected void refreshFruitList(final Callback<Void, Void> beforeRefresh, final Callback<Void, Void> afterRefresh) {
        // Before Refresh
        if(beforeRefresh != null){
            beforeRefresh.call(null);
        }

        // Get the fruit list
        fruitsAPI.getAllFruits(new Callback<Void, FruitPreview[]>(){
            @Override
            public Void call(final FruitPreview[] args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RecyclerView list = findViewById(R.id.fruitsList);
                        FruitsAdapter fruitsAdapter;
                        if(args != null){
                            fruitsAdapter = new FruitsAdapter(args, fruitsAPI, MainActivity.this);
                        }else{
                            fruitsAdapter = new FruitsAdapter(new FruitPreview[0], fruitsAPI, getApplicationContext());
                        }
                        list.setAdapter(fruitsAdapter);


                        // After refresh
                        if(afterRefresh != null){
                            afterRefresh.call(null);
                        }
                    }
                });
                return null;
            }
        });

    }
}
