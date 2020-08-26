package com.example.c_1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FruitsAdapter extends RecyclerView.Adapter<FruitsAdapter.FruitsListViewViewHolder>{


    public static class FruitsListViewViewHolder extends RecyclerView.ViewHolder{
        public ConstraintLayout item;
        public FruitsListViewViewHolder(ConstraintLayout view) {
            super(view);
            this.item = view;
        }
    }

    private FruitPreview[] items;
    private final FruitsAPI fruitsAPI;
    private final Context context;

    public FruitsAdapter(FruitPreview[] items, FruitsAPI api, Context context){
        this.items = items;
        this.fruitsAPI = api;
        this.context = context;
    };

    public FruitsListViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        ConstraintLayout item = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.fruit_list_item, parent, false);
        FruitsListViewViewHolder viewHolder = new FruitsListViewViewHolder(item);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FruitsListViewViewHolder viewHolder, int position){
        final FruitPreview fruitPreview = this.items[position];
        ConstraintLayout item = viewHolder.item;
        TextView tfvnome = item.findViewById(R.id.tfvnome);
        TextView botname = item.findViewById(R.id.botname);
        TextView othname = item.findViewById(R.id.othname);
        final ImageView image = item.findViewById(R.id.image);

        tfvnome.setText(fruitPreview.getTfvname());
        String strBotname = fruitPreview.getBotname();

        if(fruitPreview.getImageBitmap() == null) {
            this.fruitsAPI.getFruitThumb(fruitPreview.getImageurl(), new Callback<Void, byte[]>() {
                @Override
                public Void call(byte[] args) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(args, 0, args.length);
                    fruitPreview.setImageBitmap(bitmap);
                    image.setImageBitmap(bitmap);
                    return null;
                }
            });
            fruitPreview.setImageBitmap(null);
            image.setImageBitmap(null);
            image.setImageDrawable(null);
        }else{
            image.setImageBitmap(fruitPreview.getImageBitmap());
        }

        if(strBotname.length() > 27){
            botname.setText(strBotname.substring(0, 26) + "...");
        }else{
            botname.setText(strBotname);
        }
        othname.setText(this.items[position].getAllOthname());
        item.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FruitDetails.class);
                intent.putExtra("tfvname", fruitPreview.getTfvname());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount(){
        return this.items.length;
    }
}
