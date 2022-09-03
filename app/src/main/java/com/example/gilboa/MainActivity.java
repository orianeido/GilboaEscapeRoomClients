package com.example.gilboa;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText fullName;
    EditText phoneNumber;
    Button register;
    RadioGroup radioAreaGroup;
    RadioButton radioAreaAshkelon, radioAreaSderot, radioAreaAshdod, radioAreaRest;

    private String place = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        fullName = findViewById(R.id.fullName);
        phoneNumber = findViewById(R.id.phoneNumber);
        radioAreaGroup = findViewById(R.id.radioAreaGroup);
        radioAreaAshkelon = findViewById(R.id.radioAreaAshkelon);
        radioAreaSderot = findViewById(R.id.radioAreaSderot);
        radioAreaAshdod = findViewById(R.id.radioAreaAshdod);
        radioAreaRest = findViewById(R.id.radioAreaRest);
        register = findViewById(R.id.registerButton);

        radioAreaGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioAreaAshkelon) {
                    place = "אשקלון";
                } else if(checkedId == R.id.radioAreaSderot) {
                    place = "שדרות ונתיבות";
                } else if(checkedId == R.id.radioAreaAshdod) {
                    place = "אשדוד";
                } else if(checkedId == R.id.radioAreaRest) {
                    place = "שאר הארץ";
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkDataEntered()) {
                    addUserToSheet(view);
                }
            }
        });
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    boolean checkDataEntered() {
        if (isEmpty(fullName)) {
            fullName.setError("הכנס שם תקין");
            return false;
        }
        if (isEmpty(phoneNumber)) {
            phoneNumber.setError("הכנס מספר טלפון תקין");
            return false;
        }
        if (place == null) {
            Toast.makeText(getApplicationContext(), "בחר אזור בארץ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void addUserToSheet(View view) {

        final String name = fullName.getText().toString().trim();
        final String phone = phoneNumber.getText().toString().trim();
        final String area = place;

        StringRequest stringRequest = new StringRequest(Request.Method.POST,ScriptURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //loading.dismiss();
                        //Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();
                        Intent intent;
                        if (response.equals("Success")){
                            intent = new Intent(view.getContext(), CorrectActivity.class);
                        }else{
                            intent = new Intent(view.getContext(), FailedActivity.class);
                        }
                        startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Intent intent = new Intent(view.getContext(), FailedActivity.class);
                        startActivity(intent);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();
                //here we pass params
                parmas.put("action","addUser");
                parmas.put("name", name);
                parmas.put("phone", phone);
                parmas.put("area", area);

                return parmas;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
}