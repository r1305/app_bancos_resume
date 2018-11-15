package com.solution.tecno.androidanimations;

import android.content.Context;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import in.shadowfax.proswipebutton.ProSwipeButton;


public class ProfileFragment extends Fragment {
    ProSwipeButton proSwipeBtn;
    Context ctx;
    EditText prof_name,prof_user_name,prof_phone,prof_psw,prof_email;
    Credentials cred;
    String base_url="https://www.jadconsultores.com.pe/php_connection/app/bancos_resumen/";
    String user_id;
    AwesomeProgressDialog apd;
    AwesomeSuccessDialog asd;
    AwesomeErrorDialog aed;
    public ProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx=this.getContext();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);;
        cred = new Credentials(ctx);
        user_id = cred.getUserId();
        proSwipeBtn = v.findViewById(R.id.profile_btn_save);
        prof_name = v.findViewById(R.id.profile_et_name);
        prof_user_name = v.findViewById(R.id.profile_et_user);
        prof_phone = v.findViewById(R.id.profile_et_phone);
        prof_psw = v.findViewById(R.id.profile_et_password);
        prof_email = v.findViewById(R.id.profile_et_email);

        getUserProfile(user_id);

        proSwipeBtn.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                apd.show();
                // user has swiped the btn. Perform your async operation now
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // task success! show TICK icon in ProSwipeButton
                        String name = prof_name.getText().toString();
                        String username = prof_user_name.getText().toString();
                        String phone = prof_phone.getText().toString();
                        String psw = prof_psw.getText().toString();
                        String email = prof_email.getText().toString();
                        if(name.isEmpty() && username.isEmpty() && psw.isEmpty() && phone.isEmpty() && email.isEmpty()){
                            prof_name.setError("Complete el nombre");
                            prof_name.requestFocus();
                            prof_user_name.setError("Complete el usuario");
                            prof_user_name.requestFocus();
                            prof_psw.setError("Ingrese contraseña");
                            prof_psw.requestFocus();
                            prof_phone.setError("Ingrese un número celular");
                            prof_phone.requestFocus();
                            prof_email.setError("Ingrese un correo");
                            prof_email.requestFocus();
                            apd.hide();
                            proSwipeBtn.showResultIcon(false);
                            return;
                        }
                        if(name.isEmpty()){
                            prof_name.setError("Complete el nombre");
                            prof_name.requestFocus();
                            apd.hide();
                            proSwipeBtn.showResultIcon(false);
                            return;
                        }
                        if(username.isEmpty()){
                            prof_user_name.setError("Complete el usuario");
                            prof_user_name.requestFocus();
                            apd.hide();
                            proSwipeBtn.showResultIcon(false);
                            return;
                        }
                        if(email.isEmpty()){
                            prof_email.setError("Complete el correo");
                            prof_email.requestFocus();
                            apd.hide();
                            proSwipeBtn.showResultIcon(false);
                            return;
                        }else if(!isValidEmail(email)){
                            prof_email.setError("Ingrese un correo válido");
                            prof_email.requestFocus();
                            apd.hide();
                            proSwipeBtn.showResultIcon(false);
                            return;
                        }
                        if(phone.isEmpty()){
                            prof_phone.setError("Complete su celular");
                            prof_phone.requestFocus();
                            apd.hide();
                            proSwipeBtn.showResultIcon(false);
                            return;
                        }else if(phone.length()<9){
                            prof_phone.setError("Ingrese un número de celular válido");
                            prof_phone.requestFocus();
                            apd.hide();
                            proSwipeBtn.showResultIcon(false);
                            return;
                        }
                        if(psw.isEmpty()){
                            prof_psw.setError("Ingrese contraseña");
                            prof_psw.requestFocus();
                            apd.hide();
                            proSwipeBtn.showResultIcon(false);
                            return;
                        }

                        if(name!="" && username!="" &&
                                phone!="" && psw!="" &&
                                email!="" && isValidEmail(email)){
                            updateUser(user_id,username,phone,name,psw,email);
                        }else{
                            apd.hide();
                            proSwipeBtn.showResultIcon(false);
                            return;
                        }
                    }
                }, 1500);
            }
        });
        return v;
    }

    public void updateUser(final String id, final String username,
                           final String phone_number,final String full_name,
                           final String psw,final String email) {

        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?id="+id+
                "&username="+Uri.encode(username)+
                "&phone="+Uri.encode(phone_number)+
                "&name="+Uri.encode(full_name)+
                "&psw="+Uri.encode(psw)+
                "&email="+email
                ;

        String url = base_url+"updateUser.php"+params;
        System.out.println(url);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if(response.equals("true")){
                                apd.hide();
                                asd.show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        asd.hide();
                                    }
                                }, 1500);
                                proSwipeBtn.showResultIcon(true);
                                cred.save_credentials(id,full_name,username,phone_number);
                                ((FirstActivity)getContext()).updateHeader(full_name,username);
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                                Fragment fr=AccountsFragment.newInstance();
                                fragmentTransaction.replace(R.id.container,fr);
                                fragmentTransaction.commit();
                            }else{
                                apd.hide();
                                aed.setMessage("Ocurrió un error");
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        aed.hide();
                                    }
                                }, 3000);
                                proSwipeBtn.showResultIcon(false); // false if task failed
                            }
                        } catch (Exception e) {
                            apd.hide();
                            aed.setMessage(e.getMessage());
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    aed.hide();
                                }
                            }, 3000);
                            proSwipeBtn.showResultIcon(false); // false if task failed
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        apd.hide();
                        aed.setMessage(error.toString());
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                aed.hide();
                            }
                        }, 3000);
                        proSwipeBtn.showResultIcon(false); // false if task failed
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        queue.add(postRequest);
    }

    public void getUserProfile(String user_id) {
        apd.setMessage("Cargando...");
        apd.show();

        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?id="+Integer.parseInt(user_id);
        String url = base_url+"getUserProfile.php"+params;
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONParser jp = new JSONParser();
                        try {
                            JSONArray ja=(JSONArray)jp.parse(response);
                            for(int i=0;i<ja.size();i++){
                                JSONObject item=(JSONObject)ja.get(i);
                                String name=item.get("full_name").toString();
                                String user=item.get("username").toString();
                                String psw=item.get("clave").toString();
                                String phone=item.get("phone_number").toString();
                                String email=item.get("email").toString();

                                prof_name.setText(name);
                                prof_user_name.setText(user);
                                prof_psw.setText(psw);
                                prof_phone.setText(phone);
                                prof_email.setText(email);
                            }
                            apd.hide();
                            asd.show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    asd.hide();
                                }
                            }, 1500);
                        } catch (Exception e) {
                            apd.hide();
                            aed.setMessage(e.getMessage());
                            aed.show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    aed.hide();
                                }
                            }, 3000);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        apd.hide();
                        aed.setMessage(error.toString());
                        aed.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                aed.hide();
                            }
                        },3000);
                    }
                }
        );
        queue.add(postRequest);
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
