package com.solution.tecno.androidanimations.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.solution.tecno.androidanimations.R;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Utils {

    private static final String[] ALPHA_NUMERIC_STRING = {"a","A","b","B","c","C","d","D","e","E","f","F","g","G","h","H","i","I","j","J","k","K","l","L","m","M","n","N","o","O","p","P","q","Q","r","R","s","S","t","T","u","U","v","V","w","W","x","X","y","Y","z","Z","0","1","2","3","4","5","6","7","8","9"};
    //Spanish, PeruLocale locale = new Locale("es", "pe"); //Spanish, Peru
    Locale locale = new Locale("es", "pe");
    private RequestQueue queue;
    private String url ="";
    private Context ctx;
    private Credentials cred;

    public Utils(Context ctx) {
        this.ctx = ctx;
        cred = new Credentials(ctx);
    }

    public Utils() {
    }

    public String generateToken(int size) {
        String token = "";
        for(int i=0;i<size;i++){
            Random rand = new Random();
            int character = rand.nextInt(ALPHA_NUMERIC_STRING.length);
            token+=(ALPHA_NUMERIC_STRING[character]);
        }
        return token;
    }

    public void createAlert(Context ctx, String error,int type){
        AlertDialog.Builder builder =  new AlertDialog.Builder(ctx);
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_alert, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        TextView et_title = dialogView.findViewById(R.id.et_title);
        String title = "";
        if(type == 1)
            title = "¡Error!";
        else if(type == 2)
            title = "¡Éxito!";
        else if(type == 3)
            title = "¡Alerta!";

        TextView et_message = dialogView.findViewById(R.id.et_message);

        et_title.setText(title);
        et_message.setText(error);
        alertDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
            }
        }, 1500);
    }

    public String toEngDate(String esp_date)
    {
        String eng_date = "";
        String[] ar_fec_nac = esp_date.split("/");
        eng_date = ar_fec_nac[2]+"-"+ar_fec_nac[1]+"-"+ar_fec_nac[0];
        return eng_date;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void updateToken(final String token)
    {
        cred = new Credentials(ctx);
        try{
            String url=ctx.getApplicationContext().getString(R.string.base_url)+"updateFCM.php";
            Log.i("updateToken_url",url);
            queue = Volley.newRequestQueue(ctx);

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("updateToken_response: " + response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("updateToken_error: " + error.getMessage());
                }
            }){
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String> params = new HashMap<>();
                    params.put("id",cred.getUserId());
                    params.put("fcm",token);
                    return params;
                }
            };
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }catch(Exception e){
            Log.e("updateToken_error", Objects.requireNonNull(e.getMessage()));
        }


    }
}
