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

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvItem;
    private FloatingActionButton fabAdd;

    ArrayList<Item> items = new ArrayList<Item>();



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
                Intent intent = new Intent(
                        MainActivity.this,
                        AddAndUpdateActivity.class);

                startActivity(intent);

            }
        });
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            Toast.makeText(MainActivity.this,
                    "connect", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this,
                    "disconnect", Toast.LENGTH_LONG).show();
        }
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
        Log.d("Percobaan","loadAll : ");

        APIService apiService = retrofit.create(APIService.class);

        final Call<Result> result = apiService.getAll(Constants.TOKEN);



        result.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                progressDialog.dismiss();
                Log.d("Percobaan body : ", response.toString());

                Result jsonResult = response.body();

                items = jsonResult.getItems();

                ItemAdapter itemAdapter = new ItemAdapter(MainActivity.this);

                rvItem.setAdapter(itemAdapter);

                if (items != null) {
                    itemAdapter.setListItems(items);
                }


            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("Percobaan","loadAll onFailure: "+t.getMessage());

            }
        });


    }
}