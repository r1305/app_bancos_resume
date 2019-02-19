package com.solution.tecno.androidanimations;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.solution.tecno.androidanimations.Firebase.Constants;
import com.solution.tecno.androidanimations.Firebase.MyFirebaseInstanceIdService;
import com.squareup.picasso.Picasso;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import de.hdodenhof.circleimageview.CircleImageView;

public class FirstActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    Context ctx;
    Toolbar toolbar;
    DrawerLayout drawer;
    String user_id,full_name,user_name,user_photo;
    String base_url= Constants.BASE_URL;
    Credentials cred;
    TextView header_name,header_username;
    CircleImageView header_photo;

    AwesomeProgressDialog apd;
    AwesomeSuccessDialog asd;
    AwesomeErrorDialog aed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        ctx=FirstActivity.this;
        cred = new Credentials(ctx);
        MyFirebaseInstanceIdService serv=new MyFirebaseInstanceIdService();
        serv.onTokenRefresh2(ctx);
        user_id=cred.getUserId();
        full_name=cred.getFullName();
        user_name=cred.getUserName();
        user_photo=cred.getUserPhoto();
        System.out.println("***"+user_photo);

        //create progress dialog
        apd=new AwesomeProgressDialog(ctx)
                .setTitle(R.string.app_name)
                .setMessage("Cargando")
                .setColoredCircle(R.color.dialogInfoBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
                .setCancelable(false);

        //create success dialog
        asd=new AwesomeSuccessDialog(ctx)
                .setTitle(R.string.app_name)
                .setMessage("Listo!")
                .setColoredCircle(R.color.dialogSuccessBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
                .setCancelable(false);

        //create error dialog
        aed=new AwesomeErrorDialog(ctx)
                .setTitle(R.string.app_name)
                .setMessage("Ocurrió un error")
                .setColoredCircle(R.color.dialogErrorBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_error,R.color.white)
                .setCancelable(false);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                toolbar.setNavigationIcon(R.drawable.ic_close_drawer);
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
                toolbar.setNavigationIcon(R.drawable.ic_menu_white);
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawer.isDrawerOpen(Gravity.START)){
                    drawer.closeDrawer(Gravity.START);
                    toolbar.setNavigationIcon(R.drawable.ic_menu_white);
                }else{
                    drawer.openDrawer(Gravity.START);
                    toolbar.setNavigationIcon(R.drawable.ic_close_drawer);
                }
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        header_name = headerView.findViewById(R.id.nav_header_name);
        header_username = headerView.findViewById(R.id.nav_header_username);
        header_photo = headerView.findViewById(R.id.nav_header_photo);
        header_name.setText(full_name);
        header_username.setText(user_name);
        Picasso.get()
                .load(user_photo)
                .resize(200, 200)
                .centerCrop()
                .into(header_photo);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        Fragment fr=AccountsFragment.newInstance();
        fragmentTransaction.replace(R.id.container,fr);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_first, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifpest.xml.
        int id = item.getItemId();

        if(id == R.id.action_log_out){
            logout(user_id,"0");
        }

        return super.onOptionsItemSelected(item);
    }

    public void startNewActivity(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        Fragment fr;

        switch (id){
            case R.id.menu_accounts:
                fr=AccountsFragment.newInstance();
                fragmentTransaction.replace(R.id.container,fr);
            break;
            case R.id.menu_contacts:
                fr=ContactFragment.newInstance();
                fragmentTransaction.replace(R.id.container,fr);
            break;
            case R.id.menu_profile:
                fr=ProfileFragment.newInstance();
                fragmentTransaction.replace(R.id.container,fr);
            break;
            default:
                fr=AccountsFragment.newInstance();
                fragmentTransaction.replace(R.id.container,fr);
            break;
        }
        drawer.closeDrawer(Gravity.START);
        fragmentTransaction.commit();
        return true;
    }

    public void updateHeader(String name,String username,String photo) {
        header_name.setText(name); //str OR whatvever you need to set.
        header_username.setText(username); //str OR whatvever you need to set.
        Picasso.get()
                .load(photo)
                .resize(200, 200)
                .centerCrop()
                .into(header_photo);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    public void logout(final String user_id, String status) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?user_id="+user_id+"&status="+status;
        String url = base_url+"login.php"+params;
        System.out.println("***"+url);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONParser jp = new JSONParser();
                        try {
                            JSONArray ja=(JSONArray)jp.parse(response);
                            JSONObject item=(JSONObject)ja.get(0);
                            String id=item.get("id").toString();
                            String full_name=item.get("full_name").toString();
                            String user_name=item.get("username").toString();
                            String phone_number=item.get("phone_number").toString();
                            String email=item.get("email").toString();
                            String user_photo=item.get("profile_photo").toString();
                            String login_status=item.get("login_status").toString();

                            cred.save_credentials(id,full_name,user_name,phone_number,email,user_photo,login_status);
                            apd.hide();
                            asd.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    asd.hide();
                                    cred.logout();
                                }
                            }, 1500);


                        } catch (Exception e) {
                            apd.hide();
                            aed.setMessage(e.getMessage());
                            aed.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    aed.hide();
                                }
                            }, 2000);
                            System.out.println(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        apd.hide();
                        aed.setMessage(error.toString());
                        aed.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                aed.hide();
                            }
                        }, 2000);
                        // error
                        Log.d("Error.Response", error.toString());
                        apd.hide();
                        aed.setMessage(error.toString());
                        aed.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                aed.hide();
                            }
                        }, 2000);
                    }
                }
        );
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

}
