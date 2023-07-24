package com.fintech.crudserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fintech.crudserver.adapter.ItemAdapter;
import com.fintech.crudserver.model.Item;
import com.fintech.crudserver.model.Result;
import com.fintech.crudserver.service.APIService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements ItemAdapter.OnItemDeleteListener {

    private RecyclerView rvItem;
    private FloatingActionButton fabAdd;

    private ArrayList<Item> items = new ArrayList<>();

    private ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvItem = findViewById(R.id.rv_item);
        rvItem.setLayoutManager(new LinearLayoutManager(this));
        rvItem.setHasFixedSize(true);

        fabAdd = findViewById(R.id.fab_add);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddAndUpdateActivity.class);
                startActivity(intent);
            }
        });

        itemAdapter = new ItemAdapter(this, this);
        rvItem.setAdapter(itemAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllData();
    }

    public void loadAllData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait ...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService apiService = retrofit.create(APIService.class);
        final Call<Result> result = apiService.getAll(Constants.TOKEN);

        result.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                progressDialog.dismiss();
                Result jsonResult = response.body();

                items = jsonResult.getItems();

                if (items != null) {
                    itemAdapter.setListItems(items);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("Percobaan", "loadAll onFailure: " + t.getMessage());
            }
        });
    }

    public void deleteItem(int id) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait ...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService apiService = retrofit.create(APIService.class);

        Call<Result> result = apiService.delete(Constants.TOKEN, id);

        result.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                progressDialog.dismiss();
                Result jsonResult = response.body();

                Toast.makeText(MainActivity.this, jsonResult.getMessage(), Toast.LENGTH_LONG).show();

                loadAllData(); // Refresh the list after deleting an item
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Failed to delete item!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onItemDelete(int itemId) {
        deleteItem(itemId);
    }
}
