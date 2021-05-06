package com.example.bakingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.bakingapp.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ActivityMainBinding binding;
    private MainRecipeAdapter adapter;
    private boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mTwoPane = (findViewById(R.id.dummy) != null);


        if (mTwoPane){
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            binding.rvMaster.setLayoutManager(gridLayoutManager);
        }
        else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            binding.rvMaster.setLayoutManager(layoutManager);
        }

        binding.mainLayoutSwipe.setOnRefreshListener(this);

        loadJSON();
    }

    private void loadJSON() {

        binding.mainLayoutSwipe.setRefreshing(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppUtils.BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<List<Recipe>> call = request.getJSON();

        Log.d("JSON CALL", "RETROFIT CALL");
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {

                Log.i("JSON RESPONSE", "SUCCESS");
                List<Recipe> jsonResponse = response.body();

                adapter = new MainRecipeAdapter(jsonResponse, new MainRecipeAdapter.RecipeItemClickListener() {
                    @Override
                    public void onRecipeItemClick(int clickedItemIndex) {
                        Recipe recipe = adapter.getRecipeAtIndex(clickedItemIndex);
                        Intent intent = new Intent(MainActivity.this, RecipeInfoActivity.class);
                        intent.putExtra(AppUtils.EXTRAS_RECIPE, recipe);
                        startActivityForResult(intent, 1);
                    }
                });
                binding.rvMaster.setAdapter(adapter);
                binding.mainLayoutSwipe.setRefreshing(false);

                AppUtils.setIdleResourceTo(true);
            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                Log.e("JSON RESPONSE", "FAIL: " + t.getMessage());
                binding.mainLayoutSwipe.setRefreshing(false);

                // if we have a network error, prompt a dialog asking to retry or exit
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.no_network)
                        .setNegativeButton(R.string.no_network_try_again, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadJSON();
                            }
                        })
                        .setPositiveButton(R.string.no_network_close, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                builder.create().show();

                AppUtils.setIdleResourceTo(false);
            }
        });

    }

    @Override
    public void onRefresh() {
        loadJSON();
    }
}
