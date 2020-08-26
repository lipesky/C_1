package com.example.c_1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FruitDetails extends AppCompatActivity {

    private final String fruits_url = "http://api.tropicalfruitandveg.com/tfvjsonapi.php?tfvitem=";
    private final int request_max_retries = 5;
    private final ExecutorService singleExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("tfvname"));
        setSupportActionBar(toolbar);
        setTextsVisibility(View.GONE);
        load();
    }

    protected void loadImage(final String strUrl, final Callback<Void, Void> afterLoad){
        singleExecutor.submit(new Callable<Void>() {
            @Override
            public Void call(){
                byte[] img = null;
                int currentTry = 1;
                while(img == null && currentTry <= request_max_retries){
                    try{
                        img = null;
                        URL url = new URL(strUrl);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        if(con.getResponseCode() == 200){
                            System.out.println("content-length ----> " +con.getContentLength());
                            img = new byte[con.getContentLength()];
                            InputStream stream =  con.getInputStream();
                            int readedLen = 0;
                            int currentIndex = 0;
                            do{
                                currentIndex += readedLen;
                                readedLen = stream.read(img, currentIndex, img.length - currentIndex);
                            }while(readedLen > 0);
                        }else{
                            currentTry++;
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        currentTry++;
                    }
                }
                final Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView image = findViewById(R.id.image);
                        image.setImageBitmap(bitmap);

                        if(afterLoad != null){
                            afterLoad.call(null);
                        }
                    }
                });
                return null;
            }
        });

    }

    protected void load(){
        loadData(
                // Before Load
                new Callback<Void, Void>() {
                    @Override
                    public Void call(Void args) {
                        setTextsVisibility(View.GONE);
                        return null;
                    }
                },
                // After Load
                new Callback<Void, Fruit>() {
                    @Override
                    public Void call(final Fruit fruit) {
                        fruit.getImageurl();
                        System.out.println("fruit loaded");
                        loadImage(fruit.getImageurl(), new Callback<Void, Void>() {
                            @Override
                            public Void call(Void args) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        TextView textView;
                                        textView = findViewById(R.id.botname);
                                        textView.setText(fruit.getBotname());
                                        textView = findViewById(R.id.climate);
                                        textView.setText(fruit.getClimate());
                                        textView = findViewById(R.id.description);
                                        textView.setText(fruit.getDescription());
                                        textView = findViewById(R.id.health);
                                        textView.setText(fruit.getHealth());
                                        textView = findViewById(R.id.othname);
                                        textView.setText(fruit.getAllOthname());
                                        textView = findViewById(R.id.propagation);
                                        textView.setText(fruit.getPropagation());
                                        textView = findViewById(R.id.soil);
                                        textView.setText(fruit.getSoil());
                                        /*textView = findViewById(R.id.tfvname);
                                        textView.setText(args.getTfvname());*/
                                        textView = findViewById(R.id.uses);
                                        textView.setText(fruit.getUses());

                                        setTextsVisibility(View.VISIBLE);
                                    }
                                });
                                return null;
                            }
                        });

                        return null;
                    }
                }
        );
    }

    protected void setTextsVisibility(int visibility){
        ConstraintLayout texts = findViewById(R.id.texts);
        int childsLen = texts.getChildCount();
        for(int i = 0; i < childsLen - 1; i++){
            texts.getChildAt(i).setVisibility(visibility);
        }
        texts.getChildAt(childsLen - 1).setVisibility(visibility == View.GONE ? View.VISIBLE : View.GONE );
    }

    protected void loadData(Callback<Void, Void> beforeLoad, final Callback<Void, Fruit> afterLoad){
        if(beforeLoad != null){
            beforeLoad.call(null);
        }
        singleExecutor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Fruit fruit = null;
                try{
                    int current_try = 0;
                    JSONObject json = null;
                    URL url = new URL(fruits_url+getIntent().getStringExtra("tfvname"));
                    while(current_try < request_max_retries && json == null) {
                        current_try++;
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        if (connection.getResponseCode() == 200) {
                            int offset = 0;
                            int contentLength = connection.getContentLength();
                            Log.i("ContentLength", ""+contentLength);
                            InputStream inputStream = connection.getInputStream();
                            InputStreamReader streamReader = new InputStreamReader(inputStream);
                            BufferedReader reader = new BufferedReader(streamReader);

                            StringBuilder resposta = new StringBuilder();
                            String str = reader.readLine();
                            while (str != null){
                                resposta.append(str);
                                str = reader.readLine();
                            }

                            json = new JSONObject(resposta.toString());
                        }
                        connection.disconnect();
                    }
                    json = json.getJSONArray("results").getJSONObject(0);
                    fruit = new Fruit(
                        json.getString("botname"),
                        json.getString("climate"),
                        json.getString("description"),
                        json.getString("health"),
                        json.getString("imageurl"),
                        json.getString("othname").split(","),
                        json.getString("propagation"),
                        json.getString("soil"),
                        json.getString("tfvname"),
                        json.getString("uses")
                    );


                }catch(Exception e){
                    Log.e("getAllFruits Exception", e.getMessage() + e.getStackTrace().toString());
                    e.printStackTrace();
                }finally {
                    if(afterLoad != null){
                        afterLoad.call(fruit);
                    }
                }


                return null;
            }
        });
    }

}
