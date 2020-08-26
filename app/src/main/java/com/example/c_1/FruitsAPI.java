package com.example.c_1;

import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FruitsAPI {
    private final String all_fruits_url = "http://tropicalfruitandveg.com/api/tfvjsonapi.php?search=all";
    private final int request_max_retries = 5;
    private final int read_max_buffer_size = 1024; // 1KB
    private final int wait_time = 200;

    // Decided to use at maximum of 4 threads per core for granting good performance
    private final ExecutorService multiExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4);
    private final ExecutorService singleExecutor = Executors.newSingleThreadExecutor();

    protected void getAllFruits(Callback<Void, FruitPreview[]> callback){
        singleExecutor.submit(new _GetAllFruits(callback));
    }

    protected void getFruitThumb(String strUrl, Callback<Void, byte[]> callback){
        multiExecutor.submit(new _GetFruitThumb(strUrl, callback));
    }


    // Callables
    private class _GetFruitThumb implements Callable<byte[]>{

        private String strUrl;
        private Callback<Void, byte[]> callback;

        _GetFruitThumb(String strUrl, Callback<Void, byte[]> callback){
            this.strUrl = strUrl;
            this.callback = callback;
        }

        @Override
        public byte[] call()  {
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
            if(callback != null){
                callback.call(img);
            }
            return img;
        }
    }

    private class _GetAllFruits implements Callable<Void>{

        private Callback<Void, FruitPreview[]> callback;

        public _GetAllFruits(Callback c){
            this.callback = c;
        }

        @Override
        public Void call(){
            FruitPreview[] fruitlist = null;
            try{
                int current_try = 0;
                JSONObject json = null;
                URL url = new URL(all_fruits_url);
                while(current_try < request_max_retries && json == null) {
                    current_try++;
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    if (connection.getResponseCode() == 200) {
                        int offset = 0;
                        int contentLength = connection.getContentLength();
                        Log.i("ContentLength", ""+contentLength);
                        // byte[] buffer = new byte[contentLength];
                        // String responseMessage =connection.getInputStream().;
                        // Log.i("received response", responseMessage);
                        // json = new JSONObject(responseMessage);
                        // byte[] chunk_buffer = new byte[read_max_buffer_size];
                        InputStream inputStream = connection.getInputStream();
                        InputStreamReader streamReader = new InputStreamReader(inputStream);
                        BufferedReader reader = new BufferedReader(streamReader);

                        StringBuilder resposta = new StringBuilder();
                        /*inputStream.read(buffer);*/
                        String str = reader.readLine();
                        while (str != null){
                            resposta.append(str);
                            str = reader.readLine();
                        }

                        //Log.i("resposta -->", resposta.toString());
                        json = new JSONObject(resposta.toString());
                    }
                    connection.disconnect();
                }

                int resultLength = json.getInt("tfvcount");
                fruitlist = new FruitPreview[resultLength];
                for(int i = 0; i < resultLength; i++){
                    JSONObject fruit =  json.getJSONArray("results").getJSONObject(i);
                    fruitlist[i] = new FruitPreview(fruit.getString("botname"),
                            fruit.getString("imageurl"),
                            fruit.getString("othname").split(","),
                            fruit.getString("tfvname"));
                }
                //return fruitlist;

            }catch(Exception e){
                Log.e("getAllFruits Exception", e.getMessage() + e.getStackTrace().toString());
                e.printStackTrace();
            }finally {
                if(this.callback != null){
                    try {
                        this.callback.call(fruitlist);
                    }catch (Exception e){
                        Log.e("callback error", e.getMessage());
                    }
                }
            }
            return null;
        }
    }

}

