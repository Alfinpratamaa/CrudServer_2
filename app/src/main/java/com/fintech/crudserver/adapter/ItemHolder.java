package com.fintech.crudserver.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.fintech.crudserver.R;

public class ItemHolder extends RecyclerView.ViewHolder {
    TextView name, brand, price;
    CardView cView;

    public ItemHolder(@NonNull View itemView) {
        super(itemView);
        name =itemView.findViewById(R.id.tv_name);
        brand =itemView.findViewById(R.id.tv_brand);

        price =itemView.findViewById(R.id.tv_price);
        cView =itemView.findViewById(R.id.cv_item);
    }
}
