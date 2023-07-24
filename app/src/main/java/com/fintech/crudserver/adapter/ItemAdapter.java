package com.fintech.crudserver.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.fintech.crudserver.AddAndUpdateActivity;
import com.fintech.crudserver.R;
import com.fintech.crudserver.model.Item;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private ArrayList<Item> listItems = new ArrayList<>();
    private Context context;
    private OnItemDeleteListener deleteListener;

    public ItemAdapter(Context context, OnItemDeleteListener deleteListener) {
        this.context = context;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemAdapter.ItemViewHolder holder, int position) {
        final Item currentItem = listItems.get(position);

        holder.tvName.setText(currentItem.getName());
        holder.tvBrand.setText(currentItem.getBrand());
        holder.tvPrice.setText(String.valueOf(currentItem.getPrice()));

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemPosition = holder.getAdapterPosition();
                if (itemPosition != RecyclerView.NO_POSITION) {
                    Item item = listItems.get(itemPosition);
                    Intent intent = new Intent(context, AddAndUpdateActivity.class);
                    intent.putExtra("position", itemPosition);
                    intent.putExtra("item", item);
                    context.startActivity(intent);
                }
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemPosition = holder.getAdapterPosition();
                if (itemPosition != RecyclerView.NO_POSITION) {
                    Item item = listItems.get(itemPosition);
                    if (deleteListener != null) {
                        deleteListener.onItemDelete(item.getId());
                    }
                }
            }
        });

    }

    public void setListItems(ArrayList<Item> items) {
        this.listItems.clear();
        this.listItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvBrand, tvPrice;
        ImageButton editBtn, deleteBtn;
        CardView cView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvBrand = itemView.findViewById(R.id.tv_brand);
            tvPrice = itemView.findViewById(R.id.tv_price);
            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            cView = itemView.findViewById(R.id.cv_item);
        }
    }

    public interface OnItemDeleteListener {
        void onItemDelete(int itemId);
    }
}
