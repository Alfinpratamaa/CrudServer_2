package com.fintech.crudserver;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fintech.crudserver.model.Item;
import com.fintech.crudserver.model.Result;
import com.fintech.crudserver.service.APIService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddAndUpdateActivity extends AppCompatActivity {

    EditText edName, edBrand, edPrice;
    Button btnSubmit;


    boolean isEdit = false;

    Item item;
    int position;

    private final int ALERT_DIALOG_CLOSE = 20;
    private final int ALERT_DIALOG_DELETE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_and_update);

        edName = findViewById(R.id.ed_name);
        edBrand = findViewById(R.id.ed_brand);
        edPrice = findViewById(R.id.ed_price);


        btnSubmit = findViewById(R.id.btn_submit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEdit) {
                    editData();
                } else {
                    addNewData();
                }
            }
        });

        item = getIntent().getParcelableExtra("item");

        if (item != null) {
            position = getIntent().getIntExtra("position", 0);
            isEdit = true;
        }

        String actionBarTitle;
        String btnTitle;

        if (isEdit) {
            actionBarTitle = "Ubah";
            btnTitle = "Update";

            if (item != null) {
                edName.setText(item.getName());
                edBrand.setText(item.getBrand());
                edPrice.setText("" + item.getPrice());
            }
        } else {
            actionBarTitle = "Tambah";
            btnTitle = "Simpan";
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(actionBarTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        checkConnection();
        btnSubmit.setText(btnTitle);


    }

    private void addNewData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("New data");
        progressDialog.setMessage("Please wait ...");
        progressDialog.show();

        String name = edName.getText().toString().trim();
        String brand = edBrand.getText().toString().trim();
        Integer price = Integer.parseInt(edPrice.getText().toString().trim());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService apiService = retrofit.create(APIService.class);

        final Call<Result> result = apiService.create(
                Constants.TOKEN, name,
                brand, price);
        Log.d("Percobaan","link : "+retrofit.baseUrl());

        result.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                progressDialog.dismiss();

                Result jsonResult = response.body();

                Toast.makeText(AddAndUpdateActivity.this,
                        jsonResult.getMessage(), Toast.LENGTH_LONG).show();

                finish();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
                if (t instanceof IOException) {
                    Toast.makeText(AddAndUpdateActivity.this, "This is an actual network failure :( Inform the user and possibly retry", Toast.LENGTH_SHORT).show();
                    Log.e("Percobaan", "IOException: " + t.getMessage());
                    t.printStackTrace();
                } else {
                    Toast.makeText(AddAndUpdateActivity.this, "Conversion issue! Big problems :(", Toast.LENGTH_SHORT).show();
                    Log.e("Percobaan", "Error: " + t.getMessage());
                    t.printStackTrace();
                }
            }

        });


    }

    private void editData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait ...");
        progressDialog.show();

        String name = edName.getText().toString().trim();
        String brand = edBrand.getText().toString().trim();
        Integer price = Integer.parseInt(edPrice.getText().toString().trim());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService apiService = retrofit.create(APIService.class);

        final Call<Result> result = apiService.update(Constants.TOKEN,
                item.getId(), name, brand, price);

        result.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    Result jsonResult = response.body();
                    Toast.makeText(AddAndUpdateActivity.this,
                            jsonResult.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    // Log the non-successful response
                    Log.d("Percobaan", "Response code: " + response.code());
                    Log.d("Percobaan", "Error body: " + response.errorBody().toString());
                    Toast.makeText(AddAndUpdateActivity.this,
                            "Server returned an error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("Percobaan", "onFailure: " + t.getMessage());

                if (t instanceof IOException) {
                    Toast.makeText(AddAndUpdateActivity.this,
                            "This is an actual network failure :( inform the user and possibly retry",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddAndUpdateActivity.this,
                            "Conversion issue! Big problems :(", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isEdit) {
            getMenuInflater().inflate(R.menu.menu_form, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            showAlertDialog(ALERT_DIALOG_DELETE);
        } else if (item.getItemId() == android.R.id.home) {
            showAlertDialog(ALERT_DIALOG_CLOSE);
        }
        return super.onOptionsItemSelected(item);
    }


    private void showAlertDialog(int type) {
        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;

        String dialogTitle, dialogMessage;

        if (isDialogClose) {
            dialogTitle = "Cancel";
            dialogMessage = "Do you want to cancel ?";
        } else {
            dialogTitle = "Delete item";
            dialogMessage = "Are you sure to delete this item ?";
        }

        AlertDialog.Builder alertDialogBuilder =
                new AlertDialog.Builder(this);


        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder.setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isDialogClose) {
                            finish();
                        } else {
                            deleteItem(item.getId());
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }

    @Override
    public void onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE);
    }

    private void deleteItem(int id) {
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

                Toast.makeText(AddAndUpdateActivity.this,
                        jsonResult.getMessage(), Toast.LENGTH_LONG).show();

                finish();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();

            }
        });

    }

    public void checkConnection(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            Toast.makeText(AddAndUpdateActivity.this,
                    "connect", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(AddAndUpdateActivity.this,
                    "disconnect", Toast.LENGTH_LONG).show();
        }
    }
}